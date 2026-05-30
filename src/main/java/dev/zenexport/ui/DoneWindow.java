package dev.zenexport.ui;

import dev.zenexport.model.ExportResult;
import jexer.*;
import jexer.event.TKeypressEvent;

import static jexer.TKeypress.kbEnter;

/**
 * Final screen: shows the export result and lets the user restart or quit.
 */
public final class DoneWindow extends BaseWindow {

    public DoneWindow(ZenExportApp app) {
        super(app, "Export Complete",TWindow.NOCLOSEBOX);

        ExportResult result = app.state().exportResult;
        int y = 2;

        if (result == null) {
            addLabel("  (no result)", 2, y);
        } else if (result.archive() != null) {
            // remove all third string arguments
            addLabel("  Archive written:",    2, y);
            addLabel("  " + result.archive(), 2, ++y);
            addLabel("  " + result.fileCount() + " file(s) packaged.", 2, ++y);
        }

        y += 2;
        for (var w : result != null ? result.warnings() : java.util.List.of()) {
            addLabel("  ⚠  " + w, 2, y++, "tlabel.warning");
        }
        statusBar = new TStatusBar(this, "  Enter / r  new export   q quit  ");
    }

    @Override
    public void onKeypress(TKeypressEvent e) {
        if (e.getKey().equals(kbEnter) || equalsChar(e, 'r')) {
            zenApp().state().reset();
            close();
            zenApp().showProfilePick();
        } else if (equalsChar(e, 'q')) {
            getApplication().exit();
        } else {
            super.onKeypress(e);
        }
    }

    private static boolean equalsChar(TKeypressEvent e, char c) {
        return !e.getKey().isFnKey()
                && !e.getKey().isAlt()
                && !e.getKey().isCtrl()
                && e.getKey().getChar() == c;
    }
}
