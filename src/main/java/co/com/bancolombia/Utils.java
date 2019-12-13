package co.com.bancolombia;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils {

    private static File file;
    private static FileWriter fileWriter;

    public static void writeString(Project project, String nameFile, String data) throws IOException {
        System.out.println(project.file(nameFile).getAbsolutePath());

        file = new File((project.file(nameFile).getAbsolutePath()));
        fileWriter = new FileWriter(file);
        fileWriter.write(data);
        fileWriter.close();
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String decapitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }
}
