package co.com.bancolombia;

import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

public class Utils {
    private Utils(){}

    private static Properties properties = new Properties();
    private static Logger logger = LoggerFactory.getLogger(Utils.class);



    public static void writeString(Project project, String nameFile, String data) throws IOException {
        File file;
        FileWriter fileWriter ;
        logger.debug(project.file(nameFile).getAbsolutePath());

        try {
            file = new File((project.file(nameFile).getAbsolutePath()));
            fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public static Stream<String> readFile(Project project, String nameFile) throws IOException {
        logger.debug(project.file(nameFile).getAbsolutePath());
        return Files.lines(Paths.get(project.file(nameFile).getAbsolutePath())) ;

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
    public static String readProperties(String variable) throws Exception {
        properties.load(new FileReader("gradle.properties"));
        if (properties.getProperty(variable) != null)
            return properties.getProperty(variable);
        else {
            throw new Exception("No parameter" + variable + " in build.properties file");

        }
    }
        public static String  getVersionPlugin(){
            return Constants.VERSION_PLUGIN;
        }


}
