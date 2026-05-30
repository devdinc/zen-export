package dev.zenexport.util;

import dev.zenexport.model.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Discovers Zen Browser profiles on the current system by checking the
 * well-known OS-specific root directories and parsing {@code profiles.ini}.
 */
public final class ProfileDiscovery {

    private ProfileDiscovery() {}

    public static List<Profile> discover() {
        Set<Path> seen = new LinkedHashSet<>();
        List<Profile> result = new ArrayList<>();

        for (Path root : rootCandidates()) {
            if (!Files.isDirectory(root)) continue;

            Path ini = root.resolve("profiles.ini");
            if (Files.isRegularFile(ini)) {
                for (Profile p : parseProfilesIni(root, ini)) {
                    if (seen.add(p.path())) result.add(p);
                }
            } else {
                // Fallback: each sub-directory is a profile
                try (var stream = Files.list(root)) {
                    stream.filter(Files::isDirectory).forEach(dir -> {
                        if (seen.add(dir)) {
                            result.add(new Profile(dir.getFileName().toString(), dir));
                        }
                    });
                } catch (IOException ignored) {}
            }
        }
        return List.copyOf(result);
    }

    private static List<Path> rootCandidates() {
        String home = System.getProperty("user.home");
        String appData = System.getenv("APPDATA");

        List<Path> candidates = new ArrayList<>();
        candidates.add(Paths.get(home, ".zen"));
        candidates.add(Paths.get(home, ".config", "zen"));
        candidates.add(Paths.get(home, ".var", "app", "app.zen_browser.zen", ".zen"));
        candidates.add(Paths.get(home, "Library", "Application Support", "zen", "Profiles"));
        if (appData != null) {
            candidates.add(Paths.get(appData, "zen", "Profiles"));
        }
        return candidates;
    }

    private static List<Profile> parseProfilesIni(Path root, Path ini) {
        List<Profile> result = new ArrayList<>();
        String name = null;
        String pathStr = null;
        boolean isRelative = true;

        try (BufferedReader r = Files.newBufferedReader(ini)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.strip();
                if (line.startsWith("[") && !line.equals("[General]")) {
                    flush(root, name, pathStr, isRelative, result);
                    name = null; pathStr = null; isRelative = true;
                } else if (line.startsWith("Name=")) {
                    name = line.substring(5);
                } else if (line.startsWith("Path=")) {
                    pathStr = line.substring(5);
                } else if (line.startsWith("IsRelative=")) {
                    isRelative = line.substring(11).equals("1");
                }
            }
            flush(root, name, pathStr, isRelative, result);
        } catch (IOException ignored) {}

        return result;
    }

    private static void flush(Path root, String name, String pathStr,
                               boolean isRelative, List<Profile> out) {
        if (pathStr == null) return;
        Path full = isRelative ? root.resolve(pathStr) : Paths.get(pathStr);
        if (Files.isDirectory(full)) {
            out.add(new Profile(name != null ? name : pathStr, full));
        }
    }
}
