package dev.zenexport.ui;

import dev.zenexport.app.AppState;
import dev.zenexport.model.Profile;
import jexer.*;
import jexer.event.TKeypressEvent;

import java.util.List;

import static jexer.TKeypress.*;

/**
 * Step 1 – let the user pick one profile from those discovered on disk.
 */
public final class ProfilePickWindow extends BaseWindow {

    private final TList profileList;

    public ProfilePickWindow(ZenExportApp app) {
        super(app, "Step 1 / 3  —  Choose a Profile",
              TWindow.NOCLOSEBOX);

        AppState state = app.state();
        List<Profile> profiles = state.profiles;

        addLabel("Detected Zen Browser profiles:", 2, 1);

        if (profiles.isEmpty()) {
            // remove the colour key
            addLabel("  No profiles found. Is Zen Browser installed?", 2, 3);
        }

        // Build the list widget
        java.util.List<String> labels = new java.util.ArrayList<>();
        for (Profile p : profiles) {
            labels.add(String.format("  %-24s  %s", p.name(), p.path()));
        }

        int listH = Math.max(1, Math.min(labels.size(), getHeight() - 7));
        profileList = addList(labels, 2, 3, getWidth() - 4, listH,
                new TAction() {
                    public void DO() {
                        confirm();
                    }
                });

        statusBar = new TStatusBar(this, "  ↑↓ move   Enter select   Tab next   q quit  ");
        profileList.setSelectedIndex(0);
    }

    @Override
    public void onKeypress(TKeypressEvent e) {
        if (e.getKey().equals(kbEnter)) {
            confirm();
        } else if (!e.getKey().isFnKey()
                && !e.getKey().isAlt()
                && !e.getKey().isCtrl()
                && e.getKey().getChar() == 'q') {
            getApplication().exit();
        } else {
            super.onKeypress(e);
        }
    }

    private void confirm() {
        int idx = profileList.getSelectedIndex();
        ZenExportApp app = zenApp();
        if (idx < 0 || idx >= app.state().profiles.size()) return;

        app.state().chosenProfileIndex = idx;
        close();
        app.showItemPick();
    }

}
