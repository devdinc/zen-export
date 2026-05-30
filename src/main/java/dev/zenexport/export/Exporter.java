package dev.zenexport.export;

import dev.zenexport.model.Catalogue;
import dev.zenexport.model.ExportItem;
import dev.zenexport.model.ExportResult;
import dev.zenexport.model.Profile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Collects files from a profile directory for the chosen items and
 * bundles them into a timestamped zip archive.
 */
public final class Exporter {

    private static final DateTimeFormatter STAMP_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private Exporter() {}

    /**
     * Runs the export synchronously and returns a result.
     *
     * @param profile   the profile to export from
     * @param selected  indices into {@link Catalogue#items()} to include
     * @param outDir    directory where the zip should be written
     */
    public static ExportResult export(Profile profile,
                                      Set<Integer> selected,
                                      Path outDir) throws IOException {
        Files.createDirectories(outDir);

        List<CollectedFile> files = collectFiles(profile.path(), selected);
        List<String> warnings = new ArrayList<>();

        if (files.isEmpty()) {
            warnings.add("No matching files found for the selected items.");
            return new ExportResult(null, 0, warnings);
        }

        String safeName = profile.name()
                .replaceAll("[\\s/\\\\]", "_");
        String stamp = LocalDateTime.now().format(STAMP_FMT);
        Path archive = outDir.resolve("zen_" + safeName + "_" + stamp + ".zip");

        writeZip(archive, files);
        return new ExportResult(archive, files.size(), warnings);
    }

    private record CollectedFile(Path src, String dest) {}

    private static List<CollectedFile> collectFiles(Path profileDir,
                                                    Set<Integer> selected) {
        List<CollectedFile> result = new ArrayList<>();
        Set<String> seen = new TreeSet<>();     // track dest paths
        List<ExportItem> catalogue = Catalogue.items();

        for (int idx : selected) {
            ExportItem item = catalogue.get(idx);
            for (String rel : item.paths()) {
                Path src = profileDir.resolve(rel);
                if (Files.isRegularFile(src)) {
                    if (seen.add(rel)) result.add(new CollectedFile(src, rel));
                } else if (Files.isDirectory(src)) {
                    collectDir(src, profileDir, result, seen);
                }
            }
        }
        return result;
    }

    private static void collectDir(Path dir, Path profileDir,
                                   List<CollectedFile> out, Set<String> seen) {
        try {
            Files.walkFileTree(dir,
                    EnumSet.of(FileVisitOption.FOLLOW_LINKS),
                    Integer.MAX_VALUE,
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path file,
                                                         BasicFileAttributes attrs) {
                            String rel = profileDir.relativize(file).toString();
                            if (seen.add(rel)) out.add(new CollectedFile(file, rel));
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file,
                                                               IOException exc) {
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException ignored) {}
    }

    private static void writeZip(Path archive,
                                  List<CollectedFile> files) throws IOException {
        try (OutputStream fos = Files.newOutputStream(archive);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (CollectedFile cf : files) {
                // Normalise path separators for cross-platform zips
                String entryName = cf.dest().replace('\\', '/');
                zos.putNextEntry(new ZipEntry(entryName));
                Files.copy(cf.src(), zos);
                zos.closeEntry();
            }
        }
    }
}
