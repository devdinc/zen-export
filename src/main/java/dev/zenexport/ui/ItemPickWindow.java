package dev.zenexport.ui;

import dev.zenexport.app.AppState;
import dev.zenexport.model.Catalogue;
import dev.zenexport.model.ExportItem;
import jexer.*;
import jexer.event.TKeypressEvent;

import java.util.List;

import static jexer.TKeypress.*;

/**
 * Step 2 – pick which catalogue items to include using checkboxes.
 * A description label updates as the user moves between items.
 */
public final class ItemPickWindow extends BaseWindow {

    private final List<TCheckBox> checkBoxes;
    private final TLabel descLabel;

    public ItemPickWindow(ZenExportApp app) {
        super(app, "Step 2 / 3  —  Choose What to Export",
               TWindow.NOCLOSEBOX );

        List<ExportItem> items = Catalogue.items();
        checkBoxes = new java.util.ArrayList<>();

        // Scrollable panel for the checkboxes
        int panelH = getHeight() - 8;
        TPanel panel = addPanel(2, 2, getWidth() - 4, panelH);

        for (int i = 0; i < items.size(); i++) {
            ExportItem item = items.get(i);
            TCheckBox cb = panel.addCheckBox(1, i, item.label(), false);
            cb.setEnabled(true);
            checkBoxes.add(cb);
        }

        // Description area below the list
        int descY = 2 + panelH + 1;
        addLabel("Description:", 2, descY);
        descLabel = addLabel(" ", 2, descY + 1);
        updateDesc();

        statusBar = new TStatusBar(this,"  ↑↓ move   Space toggle   a all   n none   Enter confirm   ← back   q quit  ");
        if (!checkBoxes.isEmpty()) {
            checkBoxes.get(0).activateAll();
            updateDesc();
        }
    }

    private int currentIndex() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isActive()) return i;
        }
        return 0;
    }

    private void moveCursor(int delta) {
        int next = currentIndex() + delta;
        if (next < 0 || next >= checkBoxes.size()) return;
        checkBoxes.get(next).activateAll();
        updateDesc();
    }

    private void updateDesc() {
        int idx = currentIndex();
        if (idx < 0 || idx >= Catalogue.items().size()) return;
        String desc = Catalogue.items().get(idx).desc();
        int maxW = getWidth() - 4;
        String text = desc.isBlank() ? " " :
                (desc.length() > maxW ? desc.substring(0, maxW - 3) + "…" : desc);
        descLabel.setLabel(text);
    }

    private void toggle(int idx) {
        TCheckBox cb = checkBoxes.get(idx);
        cb.setChecked(!cb.isChecked());
    }

    @Override
    public void onKeypress(TKeypressEvent e) {
        TKeypress k = e.getKey();

        if (k.equals(kbUp)) {
            moveCursor(-1);
        } else if (k.equals(kbDown)) {
            moveCursor(1);
        } else if (k.equals(kbSpace)) {
            toggle(currentIndex());
        } else if (k.equals(kbEnter)) {
            confirm();
        } else if (k.equals(kbLeft) || k.equals(kbBackspace)) {
            goBack();
        } else if (equalsChar(e, 'a')) {
            selectAll(true);
        } else if (equalsChar(e, 'n')) {
            selectAll(false);
        } else if (equalsChar(e, 'q')) {
            getApplication().exit();
        } else {
            super.onKeypress(e);
        }
    }

    private void selectAll(boolean checked) {
        checkBoxes.forEach(cb -> cb.setChecked(checked));
    }

    private long checkedCount() {
        return checkBoxes.stream().filter(TCheckBox::isChecked).count();
    }

    private void confirm() {
        if (checkedCount() == 0) {
            messageBox("Nothing selected",
                    "Please select at least one item to export.");
            return;
        }
        AppState state = zenApp().state();
        state.selectedItems.clear();
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isChecked()) {
                state.selectedItems.add(i);
            }
        }
        close();
        zenApp().showDestPick();
    }

    private void goBack() {
        close();
        zenApp().showProfilePick();
    }

    private static boolean equalsChar(TKeypressEvent e, char c) {
        return !e.getKey().isFnKey()
                && !e.getKey().isAlt()
                && !e.getKey().isCtrl()
                && e.getKey().getChar() == c;
    }
}
