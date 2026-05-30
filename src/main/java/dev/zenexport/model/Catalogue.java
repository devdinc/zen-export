package dev.zenexport.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Loads and exposes the ordered list of {@link ExportItem}s from
 * {@code catalogue.json} on the classpath.
 */
public final class Catalogue {

    private static final List<ExportItem> ITEMS = load();

    private Catalogue() {}

    public static List<ExportItem> items() {
        return ITEMS;
    }

    private static List<ExportItem> load() {
        try (InputStream in = Catalogue.class
                .getResourceAsStream("/catalogue.json")) {
            if (in == null) {
                throw new IllegalStateException("catalogue.json not found on classpath");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);

            List<ExportItem> result = new ArrayList<>();
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String key   = entry.getKey();
                JsonNode obj = entry.getValue();

                List<String> paths = new ArrayList<>();
                for (JsonNode p : obj.get("paths")) {
                    paths.add(p.asText());
                }
                result.add(new ExportItem(
                        key,
                        obj.get("label").asText(),
                        obj.get("desc").asText(),
                        List.copyOf(paths)
                ));
            }
            return List.copyOf(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load catalogue.json", e);
        }
    }
}
