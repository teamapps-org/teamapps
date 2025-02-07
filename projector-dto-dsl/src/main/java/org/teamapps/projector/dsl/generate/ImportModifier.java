package org.teamapps.projector.dsl.generate;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class ImportModifier {

    private static final String IMPORT_PREFIX = "import ";
    private static final String IMPORT_SUFFIX = ";";

    public static void modifyImportsInDir(String directory, String fileTypeSuffix, String oldImport, String newImport) throws IOException {
        Path dirPath = Paths.get(directory);

        if (!Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("The provided directory path is not valid: " + directory);
        }

        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(fileTypeSuffix)) {
                    modifyImports(file, oldImport, newImport);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void modifyImports(Path filePath, String oldTypeName, String newFullyQualifiedTypeName) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        boolean importReplaced = false;
        boolean importAdded = false;

        // Modify the import statements
        List<String> modifiedLines = new ArrayList<>();
        int lastImportLineIndex = 2;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.trim().startsWith(IMPORT_PREFIX) && line.trim().endsWith(IMPORT_SUFFIX)) {
                lastImportLineIndex = i;
				String importStatement = line.trim().substring(IMPORT_PREFIX.length(), line.trim().length() - IMPORT_SUFFIX.length()).trim();
				String importUnqualified = getUnqualifiedName(importStatement);
				if (importUnqualified.equals(oldTypeName)) {
					modifiedLines.add(IMPORT_PREFIX + newFullyQualifiedTypeName + IMPORT_SUFFIX);
					importReplaced = true;
				} else if (importStatement.equals(newFullyQualifiedTypeName)) {
					importAdded = true; // The new import is already present
				} else {
					modifiedLines.add(line);
				}
			} else {
				modifiedLines.add(line);
			}
		}

        // If the new import was not found and no replacement was done, add it
        if (!importReplaced && !importAdded) {
            modifiedLines.add(lastImportLineIndex + 1, IMPORT_PREFIX + newFullyQualifiedTypeName + IMPORT_SUFFIX);
        }

        // Write the modified lines back to the file
        Files.write(filePath, modifiedLines);

        if (importReplaced) {
            System.out.println("Import statement replaced.");
        } else if (!importAdded) {
            System.out.println("Import statement added.");
        } else {
            System.out.println("Import statement already present.");
        }
    }

    private static String getUnqualifiedName(String qualifiedName) {
        int lastDotIndex = qualifiedName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return qualifiedName.substring(lastDotIndex + 1);
        }
        return qualifiedName;
    }

}
