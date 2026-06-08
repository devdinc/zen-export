package dev.zenexport.ui;

import jexer.TWindow;

/**
 * Common base for all application windows.
 * Handles full-screen sizing and provides a typed accessor for the app.
 */
public abstract class BaseWindow extends TWindow {

    protected BaseWindow(ZenExportApp app, String title, int flags) {
        super(app, title,
              0, 0,
              app.getScreen().getWidth(),
              app.getScreen().getHeight(),
              flags);
    }

    /** Typed accessor – avoids repeated casts in subclasses. */
    protected ZenExportApp zenApp() {
        return (ZenExportApp) getApplication();
    }
}
