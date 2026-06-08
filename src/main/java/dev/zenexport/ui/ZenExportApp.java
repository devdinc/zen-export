package dev.zenexport.ui;

import dev.zenexport.app.AppState;
import dev.zenexport.export.Exporter;
import dev.zenexport.model.ExportResult;
import dev.zenexport.util.ProfileDiscovery;
import jexer.TApplication;
import jexer.TMessageBox;

/**
 * Root Jexer application.
 * Owns the {@link AppState} and coordinates screen transitions.
 */
public final class ZenExportApp extends TApplication {

    private final AppState state = new AppState();

    public ZenExportApp() throws Exception {
        super(BackendType.XTERM);
        // Discover profiles once at startup
        state.profiles = ProfileDiscovery.discover();
        invokeLater(this::showProfilePick);
    }

    public AppState state() {
        return state;
    }

    public void showProfilePick() {
        if (state.profiles.isEmpty()) {
            messageBox("No Profiles Found",
		            """
				            No Zen Browser profiles were found on this system.
				            
				            Expected locations:
				              ~/.zen
				              ~/.config/zen
				              ~/.var/app/app.zen_browser.zen/zen""",
                    TMessageBox.Type.OK);
            return;
        }
        new ProfilePickWindow(this);
    }

    public void showItemPick() {
        new ItemPickWindow(this);
    }

    public void showDestPick() {
        new DestPickWindow(this);
    }

    public void runExport() {
        try {
	        state.exportResult = Exporter.export(
	                state.chosenProfile(),
	                state.selectedItems,
	                state.outDir
	        );
            new DoneWindow(this);
        } catch (Throwable e) {
            writeCrashLog(e);
            state.exportResult = new ExportResult(
                    null, 0,
                    java.util.List.of("Export failed: " + e.getMessage() +
                            " (see ~/zen_export_crash.log)")
            );
            new DoneWindow(this);
        }
    }

    private static void writeCrashLog(Throwable e) {
        try {
            java.nio.file.Path log = java.nio.file.Paths.get(
                    System.getProperty("user.home"), "zen_export_crash.log");
            try (var w = java.nio.file.Files.newBufferedWriter(log,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND)) {
                w.write("=== " + java.time.LocalDateTime.now() + " ===\n");
                e.printStackTrace(new java.io.PrintWriter(w));
                w.write("\n");
            }
        } catch (java.io.IOException ignored) {}
    }
}
