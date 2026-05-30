package dev.zenexport.app;

import dev.zenexport.ui.ZenExportApp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Entry point. Instantiates {@link ZenExportApp} and blocks until it exits.
 */
public final class Main {

    private Main() {}

    public static void main(String[] args) throws Exception {
        // Log all uncaught exceptions to a file next to the jar
        Path logFile = Paths.get(System.getProperty("user.home"), "zen_export_crash.log");
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            try (var writer = Files.newBufferedWriter(logFile,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                writer.write("=== " + java.time.LocalDateTime.now() + " [" + thread.getName() + "] ===\n");
                ex.printStackTrace(new java.io.PrintWriter(writer));
                writer.write("\n");
            } catch (IOException ignored) {}
        });

        if (System.getProperty("jexer.Swing") == null) {
            System.setProperty("jexer.Swing", "false");
        }
        if (System.getProperty("jexer.ECMA48.colorMode") == null) {
            System.setProperty("jexer.ECMA48.colorMode", "xterm256");
        }

        ZenExportApp app = new ZenExportApp();
        new Thread(app).start();
    }
}
