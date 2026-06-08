package dev.zenexport.model;

import java.nio.file.Path;

/** A single Zen Browser profile discovered on disk. */
public record Profile(String name, Path path) {

    @Override
    public String toString() {
        return name + "  (" + path + ")";
    }
}
