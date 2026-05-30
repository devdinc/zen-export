package dev.zenexport.app;

import dev.zenexport.model.ExportResult;
import dev.zenexport.model.Profile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Centralised mutable state shared across all Jexer windows.
 * One instance lives for the lifetime of the application.
 */
public final class AppState {

    public List<Profile> profiles = List.of();
    public int chosenProfileIndex = -1;

    /** Indices into Catalogue.items() that the user has checked. */
    public final Set<Integer> selectedItems = new LinkedHashSet<>();

    public Path outDir = Paths.get(System.getProperty("user.home"), "zen-exports");

    public ExportResult exportResult = null;

    public Profile chosenProfile() {
        if (chosenProfileIndex < 0 || chosenProfileIndex >= profiles.size()) {
            return null;
        }
        return profiles.get(chosenProfileIndex);
    }

    public void reset() {
        chosenProfileIndex = -1;
        selectedItems.clear();
        outDir = Paths.get(System.getProperty("user.home"), "zen-exports");
        exportResult = null;
    }
}
