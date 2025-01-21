# Text Matcher

## Overview
This program searches for specific strings in a large text file. It reads the file in batches of 1000 lines, processes matches concurrently, and prints the results with line and character offsets.

## Features
- Efficiently processes large text files in manageable parts.
- Supports concurrent processing for faster search results.
- Outputs the results in a clear and structured format.

## Prerequisites
- Java Development Kit (JDK) 8 or later installed.
- A text file to process.
- Strings to search for in the text file.

## Usage Instructions

### 1. Setup
1. Clone or download this project.
2. Place your large text file in an accessible directory.
3. Note down the absolute file path to the text file.

### 2. Modify the Code
1. Open the `Main.java` file.
2. Locate the following lines in the `main` method:
   ```java
   String filePath = "path/to/large/text/file.txt"; // Replace with the actual file path
   List<String> searchStrings = Arrays.asList("word1", "word2", "word3"); // Replace with desired search words
   ```
3. Replace `"path/to/large/text/file.txt"` with the absolute path to your text file.
4. Replace `"word1", "word2", "word3"` with the strings you want to search for.

### 3. Compile and Run
1. Navigate to the directory containing `Main.java` in your terminal.
2. Compile the program:
   ```bash
   javac Main.java
   ```
3. Run the program:
   ```bash
   java Main
   ```

### 4. View Output
The program will print the results to the console in the following format:
```
word1 --> [[lineOffset=10, charOffset=50], [lineOffset=200, charOffset=800]]
word2 --> [[lineOffset=15, charOffset=100], [lineOffset=300, charOffset=1200]]
```

## Example
### Input
- Text file content (sample):
  ```
  Hello world
  This is a test file
  Searching for specific words
  word1 and word2 are in this line
  Another line with word1
  ```
- Search strings: `"word1", "word2"`

### Output
```
word1 --> [[lineOffset=4, charOffset=26], [lineOffset=5, charOffset=14]]
word2 --> [[lineOffset=4, charOffset=38]]
```

## Notes
- The program reads 1000 lines at a time to handle large files efficiently.
- Results include both line and character offsets for each match.

## Troubleshooting
- **Error: File not found**
    - Ensure the file path is correct and accessible.
- **Error: Out of memory**
    - Increase the heap size using the `-Xmx` JVM option.

