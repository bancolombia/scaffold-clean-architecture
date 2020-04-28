package co.com.bancolombia.templates.properties;

public class PropertiesTemplate {

    public static String getJpaPropertiesContent() {
        return "databaseConfig:\n" +
               "  driver: org.h2.Driver\n" +
               "  connectionString: jdbc:h2:mem:testdb\n" +
               "  secretName: clean-architecture\n" +
               "  secretRegion: us-east-1\n" +
               "\n";
    }
}
