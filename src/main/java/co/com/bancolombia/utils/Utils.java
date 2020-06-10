package co.com.bancolombia.utils;

import co.com.bancolombia.exceptions.ParamNotFoundException;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    private static final String PARAM_START = "{{";
    private static final String PARAM_END = "}}";
    private static final int PARAM_LENGTH = 2;

    public static String capitalize(String data) {
        char[] c = data.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    public static String decapitalize(String data) {
        char[] c = data.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    public static String getVersionPlugin() {
        return PluginTemplate.VERSION_PLUGIN;
    }

    public static String joinPath(String... args) {
        return String.join(Constants.SEPARATOR, args);
    }

    public static String fillPath(String path, Map<String, Object> params) throws ParamNotFoundException {
        while (path.contains(PARAM_START)) {
            String key = path.substring(path.indexOf(PARAM_START) + PARAM_LENGTH, path.indexOf(PARAM_END));
            if (params.containsKey(key)) {
                path = path.replace(PARAM_START + key + PARAM_END, params.get(key).toString());
            } else {
                throw new ParamNotFoundException(key);
            }
        }
        return path;
    }

    public static String extractDir(String path) {
        int index = path.lastIndexOf(Constants.SEPARATOR);
        if (index != -1) {
            return path.substring(0, index);
        } else {
            return null;
        }
    }

    public static String formatTaskOptions(List<?> options) {
        List<String> items = new ArrayList<>();
        for (Object type : options) {
            items.add(type.toString());
        }
        return "[" + String.join("|", items) + "]";
    }

}
