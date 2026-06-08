package dev.zenexport.ui;

import dev.zenexport.app.AppState;
import jexer.*;
import jexer.event.TKeypressEvent;

import java.nio.file.Paths;

import static jexer.TKeypress.*;

/**
 * Step 3 – confirm or edit the output directory, then trigger the export.
 */
public final class DestPickWindow extends BaseWindow {

    private final TField outDirField;

    public DestPickWindow(ZenExportApp app) {
        super(app, "Step 3 / 3  —  Output Directory",
               TWindow.NOCLOSEBOX);

        AppState state = app.state();
        int w = getWidth() - 4;

        addLabel("Export archive will be written to:", 2, 2);
        outDirField = addField(2, 4, w, false, state.outDir.toString());

        // Summary
        String profileName = state.chosenProfile() != null
                ? state.chosenProfile().name() : "?";
        int selCount = state.selectedItems.size();
        addLabel(String.format("Profile : %s", profileName),  2, 6);
        addLabel(String.format("Items   : %d selected", selCount), 2, 7);

        statusBar = new TStatusBar(this, "  Tab focus field   Enter export   ← back   q quit  ");
    }

    @Override
    public void onKeypress(TKeypressEvent e) {
        TKeypress k = e.getKey();

        if (k.equals(kbEnter)) {
            startExport();
        } else if (k.equals(kbLeft)) {
            goBack();
        } else if (!e.getKey().isFnKey()
                && !e.getKey().isAlt()
                && !e.getKey().isCtrl()
                && e.getKey().getChar() == 'q') {
            getApplication().exit();
        } else {
            super.onKeypress(e);
        }
    }

    private void startExport() {
        String dir = outDirField.getText().strip();
        if (dir.isEmpty()) {
            messageBox("Invalid path", "Output directory cannot be empty.");
            return;
        }
        zenApp().state().outDir = Paths.get(dir);
        close();
        zenApp().runExport();
    }

    private void goBack() {
        close();
        zenApp().showItemPick();
    }

}
