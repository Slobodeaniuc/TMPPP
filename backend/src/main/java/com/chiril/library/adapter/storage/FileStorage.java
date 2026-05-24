package com.chiril.library.adapter.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class FileStorage {
    private final Path filePath;

    public FileStorage(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("filePath null");
        }
        this.filePath = filePath;
    }

    public synchronized List<String> readLines() {
        ensureFileExists();
        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read storage file: " + filePath, ex);
        }
    }

    public synchronized void writeLines(List<String> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("lines null");
        }
        ensureFileExists();
        try {
            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write storage file: " + filePath, ex);
        }
    }

    public Path getFilePath() {
        return filePath;
    }

    private void ensureFileExists() {
        Path parent = filePath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to prepare storage file: " + filePath, ex);
        }
    }
}
