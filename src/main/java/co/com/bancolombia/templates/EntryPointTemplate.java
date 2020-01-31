package co.com.bancolombia.templates;

import java.util.HashMap;
import java.util.Map;

public class EntryPointTemplate {

    public static final String API_REST_CLASS = "ApiRest";

    private static final Map<Integer, String> ENTRY_POINTS_AVAILABLE = new HashMap<>();

    private EntryPointTemplate(){}


    public static String getBuildGradleApiRest() {

        return "dependencies {\n" +
                "    implementation project(':usecase')\n" +
                "    implementation project(':model')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-web'\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-security'\n" +
                "}";

    }

    public static String getBuildGradleReactiveWeb() {

        return "dependencies {\n" +
                "    implementation project(':usecase')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-webflux'\n" +
                "}";

    }

    public static String getApiRestClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import org.springframework.http.HttpHeaders;\n" +
                "import org.springframework.http.HttpStatus;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.springframework.http.ResponseEntity;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "import org.springframework.web.context.request.WebRequest;\n" +
                "\n" +
                "import java.nio.file.AccessDeniedException;\n" +
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(value = \"/api\", produces = MediaType.APPLICATION_JSON_VALUE)\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + API_REST_CLASS + " {\n" +
                "\n" +
                "    private final Object useCase;\n" +
                "\n" +
                "    @GetMapping (path = \"/health\")\n" +
                "    public Object health() {\n" +
                "        return useCase;\n" +
                "    }\n" +
                "\n" +
                "    @ExceptionHandler({ AccessDeniedException.class })\n" +
                "    public ResponseEntity<Object> handleAccessDeniedException(\n" +
                "            Exception ex, WebRequest request) {\n" +
                "        return new ResponseEntity<Object>(\n" +
                "                \"Access denied message here\", new HttpHeaders(), HttpStatus.FORBIDDEN);\n" +
                "    }" +
                "}";
    }

    public static String getReactiveWebClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import org.springframework.http.MediaType;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "import reactor.core.publisher.Mono;\n"+
                "\n" +
                "\n" +
                "@RestController\n" +
                "@RequestMapping(value = \"/api\", produces = MediaType.APPLICATION_JSON_VALUE)\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + API_REST_CLASS + " {\n" +
                "\n" +
                "    private final Object useCase;\n" +
                "\n" +
                "    @GetMapping (path = \"/health\")\n" +
                "    public Mono<Object> health() {\n" +
                "        return Mono.empty();\n" +
                "    }\n" +
                "\n"+
                "}";
    }

    public static String getNameEntryPoint(int numberEntryPoint) {

        if (ENTRY_POINTS_AVAILABLE.isEmpty()) {
            ENTRY_POINTS_AVAILABLE.put(1, "API REST IMPERATIVE");
            ENTRY_POINTS_AVAILABLE.put(2, "API REST REACTIVE");
        }
        return ENTRY_POINTS_AVAILABLE.get(numberEntryPoint);
    }

    public static String getSettingsApiRestContent() {
        return "\ninclude \":api-rest\"\n" +
                "project(':api-rest').projectDir = file('./infrastructure/entry-points/api-rest')\n";
    }

    public static String getSettingsReactiveWebContent() {
        return "\ninclude \":reactive-web\"\n" +
                "project(':reactive-web').projectDir = file('./infrastructure/entry-points/reactive-web')\n";
    }
}
