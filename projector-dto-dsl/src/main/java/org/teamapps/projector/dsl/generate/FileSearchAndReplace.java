package org.teamapps.projector.dsl.generate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileSearchAndReplace {

    private final Path directory;
    private final String fileTypeSuffix;
    private final String searchString;
    private final String replaceString;

    public FileSearchAndReplace(Path directory, String fileTypeSuffix, String searchString, String replaceString) {
        this.directory = directory;
        this.fileTypeSuffix = fileTypeSuffix;
        this.searchString = searchString;
        this.replaceString = replaceString;
    }

    public void execute() throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("The provided directory path is not valid: " + this.directory);
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(fileTypeSuffix)) {
                    replaceInFile(file, searchString, replaceString);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void replaceInFile(Path file, String searchString, String replaceString) throws IOException {
        Pattern pattern = Pattern.compile(searchString);
        List<String> fileContent = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<String> modifiedContent = fileContent.stream()
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    return matcher.replaceAll(replaceString);
                })
                .collect(Collectors.toList());

        Files.write(file, modifiedContent, StandardCharsets.UTF_8);
    }

}