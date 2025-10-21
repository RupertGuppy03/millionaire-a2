/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author rupertguppy
 * 
 * ======ChatGPT helped with a majority of this class=====
 * 
 * what this class does
 * 
  - ensures the rest of the app never touches java.io.file.Files directly.
  - Keeps reads/writes consistent, easier to test, and easier to change later.
  - Simple helpers for common patterns:
  - make sure folders/files exist
  - read a file as lines, as “blocks”, or as key=value pairs
  - write/append lines, or do a safe “atomic” overwrite
 * 
 * 
 */

/**
 * FILE UTILITIES (seeding only).
 * What to change:
 *  - Restrict to generic file helpers used to seed the DB from questions.txt (UTF-8).
 *  - Remove any runtime highscores/savegame responsibilities (replaced by DB).
 *  - Keep paths relative to project (e.g., "data/questions.txt"); avoid absolute paths.
 */


    public final class FileManager {

    private FileManager() { /* utility class; no instances */ }

    /* =========================
       Support (folder + file)
       ========================= */

    /** Ensure a directory exists (creates parents if needed). */
    public static void ensureDirectory(Path dir) throws IOException {
        if (dir == null) throw new NullPointerException("dir");
        Files.createDirectories(dir);
    }

    /** Overload for convenience. */
    public static void ensureDirectory(String dir) throws IOException {
        ensureDirectory(Paths.get(dir));
    }

    /** Ensure a file exists (and its parent directory). Creates empty file if missing. */
    public static void ensureFile(Path file) throws IOException {
        if (file == null) throw new NullPointerException("file");
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        if (Files.notExists(file)) Files.createFile(file);
    }

    /** Overload for convenience. */
    public static void ensureFile(String file) throws IOException {
        ensureFile(Paths.get(file));
    }

    /* =========================
       3 READ helpers
       ========================= */

    /** Read all lines from a text file. Returns empty list if file is missing. */
    public static List<String> readAllLines(Path path) throws IOException {
        if (path == null) throw new NullPointerException("path");
        if (Files.notExists(path)) return Collections.emptyList();
        return Files.readAllLines(path); // project encoding (set Sources → UTF-8)
    }

    /**
     * Read blocks of lines separated by blank lines.
     * - Skips lines that start with '#'
     * - Each block is a List<String> of non-empty lines
     */
    public static List<List<String>> readBlocks(Path path) throws IOException {
        List<String> lines = readAllLines(path);
        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();

        for (String raw : lines) {
            String line = raw;
            if (line.trim().isEmpty()) {
                if (!current.isEmpty()) {
                    blocks.add(current);
                    current = new ArrayList<>();
                }
                continue;
            }
            if (line.trim().startsWith("#")) continue;
            current.add(line);
        }
        if (!current.isEmpty()) blocks.add(current);
        return blocks;
    }

    /**
     * Read simple key=value pairs into a LinkedHashMap (preserves order).
     * - Ignores blank lines and lines starting with '#'
     * - Splits on the first '='; lines without '=' are skipped
     */
    public static Map<String, String> readKeyValues(Path path) throws IOException {
        List<String> lines = readAllLines(path);
        Map<String, String> map = new LinkedHashMap<>();
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int eq = line.indexOf('=');
            if (eq <= 0) continue;
            String key = line.substring(0, eq).trim();
            String val = line.substring(eq + 1).trim();
            map.put(key, val);
        }
        return map;
    }

    /* =========================
        3 WRITE helpers 
       ========================= */

    /** Overwrite a file with the given lines (creates file/dirs if needed). */
    public static void writeLines(Path path, List<String> lines) throws IOException {
        if (path == null) throw new NullPointerException("path");
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.write(path, lines == null ? List.of() : lines,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** Append a single line (adds a newline). Creates file/dirs if needed. */
    public static void appendLine(Path path, String line) throws IOException {
        if (path == null) throw new NullPointerException("path");
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(path,
                (line == null ? "" : line) + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * Atomic write: write to a temp file in the same directory, then move into place.
     * Uses REPLACE_EXISTING; attempts ATOMIC_MOVE (falls back if unsupported).
     */
    public static void atomicWrite(Path path, List<String> lines) throws IOException {
        if (path == null) throw new NullPointerException("path");
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(parent);

        String base = path.getFileName().toString();
        Path tmp = Files.createTempFile(parent, base, ".tmp");

        try {
            Files.write(tmp, lines == null ? List.of() : lines, StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(tmp, path,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            try { Files.deleteIfExists(tmp); } catch (IOException ignore) {}
        }
    }
}
