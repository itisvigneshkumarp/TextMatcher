import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

/**
 * A program to search for specific strings in a large text file.
 * It reads the file in batches, processes matches concurrently, and prints results with line and character offsets.
 */
public class Main {

    public static void main(String[] args) {
        String filePath = "/Users/vigneshkumarp/IdeaProjects/TextMatcher/src/sample.txt"; // Replace with the actual file path
        List<String> searchStrings = Arrays.asList("Timothy"); // Replace with desired search words

        try {
            Map<String, List<Location>> results = processFile(filePath, searchStrings);
            printResults(results);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error processing file: " + e.getMessage());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Processes the file in batches of 1000 lines, finds matches for the search strings, and aggregates results.
     *
     * @param filePath      Path to the input file.
     * @param searchStrings List of strings to search for.
     * @return Map of search strings to their locations in the text.
     */
    public static Map<String, List<Location>> processFile(String filePath, List<String> searchStrings) throws IOException, InterruptedException, ExecutionException {
        Map<String, List<Location>> aggregatedResults = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            int charOffset = 0;
            List<String> linesBatch = new ArrayList<>();
            List<Future<Map<String, List<Location>>>> futures = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                linesBatch.add(line);
                lineNumber++;
                charOffset += line.length() + 1; // Adding 1 for newline character

                if (linesBatch.size() == 1000) {
                    submitBatch(linesBatch, searchStrings, lineNumber, charOffset, futures, executor);
                }
            }

            if (!linesBatch.isEmpty()) {
                submitBatch(linesBatch, searchStrings, lineNumber, charOffset, futures, executor);
            }

            for (Future<Map<String, List<Location>>> future : futures) {
                Map<String, List<Location>> matcherResult = future.get();
                aggregateResults(aggregatedResults, matcherResult);
            }
        } finally {
            executor.shutdown();
        }

        return aggregatedResults;
    }

    /**
     * Submits a batch of lines for matching.
     */
    private static void submitBatch(List<String> linesBatch, List<String> searchStrings, int lineNumber, int charOffset,
                                    List<Future<Map<String, List<Location>>>> futures, ExecutorService executor) {
        final List<String> batch = new ArrayList<>(linesBatch);
        final int startLine = lineNumber - batch.size();
        final int startCharOffset = charOffset - batch.stream().mapToInt(String::length).sum() - batch.size();

        Future<Map<String, List<Location>>> future = executor.submit(() -> findMatches(batch, searchStrings, startLine, startCharOffset));
        futures.add(future);

        linesBatch.clear();
    }

    /**
     * Finds matches of the search strings in the given batch of lines.
     */
    private static Map<String, List<Location>> findMatches(List<String> linesBatch, List<String> searchStrings, int startLine, int startCharOffset) {
        Map<String, List<Location>> matches = new HashMap<>();
        int charOffset = startCharOffset;

        for (int i = 0; i < linesBatch.size(); i++) {
            String line = linesBatch.get(i);
            int lineNumber = startLine + i;

            for (String searchString : searchStrings) {
                Matcher matcher = Pattern.compile(Pattern.quote(searchString)).matcher(line);
                while (matcher.find()) {
                    matches.computeIfAbsent(searchString, k -> new ArrayList<>()).add(new Location(lineNumber, charOffset + matcher.start()));
                }
            }

            charOffset += line.length() + 1; // Adding 1 for newline character
        }

        return matches;
    }

    /**
     * Aggregates results from multiple matchers.
     */
    private static void aggregateResults(Map<String, List<Location>> aggregatedResults, Map<String, List<Location>> matcherResults) {
        for (Map.Entry<String, List<Location>> entry : matcherResults.entrySet()) {
            aggregatedResults.merge(entry.getKey(), entry.getValue(), (existing, newValues) -> {
                existing.addAll(newValues);
                return existing;
            });
        }
    }

    /**
     * Prints the results in the required format.
     */
    private static void printResults(Map<String, List<Location>> results) {
        for (Map.Entry<String, List<Location>> entry : results.entrySet()) {
            System.out.print(entry.getKey() + " --> [");
            for (Location loc : entry.getValue()) {
                System.out.print("[lineOffset=" + loc.lineOffset + ", charOffset=" + loc.charOffset + "]");
            }
            System.out.println("]");
        }
    }

    /**
     * Represents a location in the text with line and character offsets.
     */
    static class Location {
        int lineOffset;
        int charOffset;

        public Location(int lineOffset, int charOffset) {
            this.lineOffset = lineOffset;
            this.charOffset = charOffset;
        }

        @Override
        public String toString() {
            return "[lineOffset=" + lineOffset + ", charOffset=" + charOffset + "]";
        }
    }
}
