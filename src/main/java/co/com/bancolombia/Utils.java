package co.com.bancolombia;

import co.com.bancolombia.models.AttributeClassModel;
import co.com.bancolombia.models.StructureClassModel;
import co.com.bancolombia.templates.Constants;
import co.com.bancolombia.templates.PluginTemplate;
import org.gradle.api.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class Utils {
    private static Properties properties = new Properties();

    private Utils() {
    }

    public static void writeString(Project project, String filePath, String data) throws IOException {
        project.getLogger().debug(project.file(filePath).getAbsolutePath());
        File file = project
                .file(validatePath(filePath))
                .getAbsoluteFile();

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(data);
        }
    }

    public static Stream<String> readFile(Project project, String nameFile) throws IOException {
        File file = project.file(validatePath(nameFile))
                .getAbsoluteFile();
        project.getLogger().debug(file.getAbsolutePath());
        return Files.lines(Paths.get(file.toURI()));

    }

    public static List<File> finderSubProjects(String dirPath) {
        File[] directories = new File(validatePath(dirPath))
                .getAbsoluteFile()
                .listFiles(File::isDirectory);
        FilenameFilter filter = (file, s) -> s.endsWith("build.gradle");
        List<File> textFiles = new ArrayList<>();

        for (File dir : directories) {
            textFiles.addAll(Arrays.asList(dir.listFiles(filter)));
        }
        return textFiles;
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String decapitalize(String string) {
        char[] c = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    public static String readProperties(String variable) throws IOException {
        properties.load(new FileReader("gradle.properties"));
        if (properties.getProperty(variable) != null) {
            return properties.getProperty(variable);
        } else {
            throw new IOException("No parameter" + variable + " in build.properties file");

        }
    }

    public static String getVersionPlugin() {
        return PluginTemplate.VERSION_PLUGIN;
    }

    public static Integer tryParse(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The value is invalid");
        }
    }

    private static String validatePath(String name) {
        return name;
    }

    public static String concatSeparator( List<String> args){
        return args.stream()
                .reduce("",(i, acum) ->i + Constants.SEPARATOR + acum)
                .replaceFirst("/","");
    }

    public static Object getValuesByClass(String type){
        switch (type) {
            case "String":
                return "\"String\"";
            case "int":
            case "short":
                return 1234;
            case  "char":
                return "\'c\'";
            case "double":
                return 12345.0d;
            case "Date":
                return "new Date()";
            case "boolean":
                return  false;
            case "float":
                return 0.0f;
            case "long":
                return 0L;
            case "byte":
                return 0;
        }

        if(type.contains("List"))
            return "new LinkedList<>()";

        return null;
    }

    public static String getDelta(String type){
        if(type.equals("double") || type.equals("float"))
            return ", 0";
        else
            return "";
    }

}
