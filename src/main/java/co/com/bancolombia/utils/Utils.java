package co.com.bancolombia.utils;

import co.com.bancolombia.Constants;
import co.com.bancolombia.exceptions.ParamNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    private static final String SEPARATOR = "/";
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
        return Constants.PLUGIN_VERSION;
    }

    public static String joinPath(String... args) {
        return String.join(SEPARATOR, args);
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
        int index = path.lastIndexOf(SEPARATOR);
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

    public static String addDependency(String build, String dependency) {
        if (build.contains(dependency)) {
            return build;
        }
        int start = build.indexOf("dependencies");
        int realStart = build.indexOf('{', start);
        return build.substring(0, realStart + 1) + "\n\t" + dependency + build.substring(realStart + 1);
    }

    public static String addConfiguration(String build, String configuration) {
        if(!build.contains("configurations")) {
            build += "\n\nconfigurations{\n}";
        }
        if(build.contains(configuration)) {
            return build;
        }
        int start = build.indexOf("configurations");
        int realStart = build.indexOf('{', start);
        return build.substring(0, realStart + 1) + "\n\t" + configuration + build.substring(realStart + 1);
    }

    public static String toDashName(String name) {
        String regex = "(?=[A-Z][a-z])";
        String subst = "-";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        String res = matcher.replaceAll(subst).toLowerCase();
        if (res.startsWith("-")) {
            return res.substring(1);
        }
        return res;
    }

    public static String addModule(String settings, String module, String baseDir) {
        String toAppend = "\ninclude ':" + module + "'\nproject(':" + module + "').projectDir = file('./" + baseDir
                + "/" + module + "')";
        if (settings.contains(toAppend)) {
            return settings;
        }
        return settings + toAppend;
    }

    public static String removeLinesIncludes(String content, String key) {
        return Arrays.stream(content.split("\\n"))
                .filter(line -> !line.contains(key))
                .collect(Collectors.joining("\n"));
    }
}
