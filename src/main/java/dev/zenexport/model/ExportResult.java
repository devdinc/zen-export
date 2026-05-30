package dev.zenexport.model;

import java.nio.file.Path;
import java.util.List;

/** Outcome returned by the Exporter after a run. */
public record ExportResult(
        Path archive,        // null when nothing was collected
        int fileCount,
        List<String> warnings
) {}
