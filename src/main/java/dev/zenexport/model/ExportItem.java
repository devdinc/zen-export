package dev.zenexport.model;

import java.util.List;

/** One exportable unit as loaded from catalogue.json. */
public record ExportItem(
        String key,
        String label,
        String desc,
        List<String> paths
) {}
