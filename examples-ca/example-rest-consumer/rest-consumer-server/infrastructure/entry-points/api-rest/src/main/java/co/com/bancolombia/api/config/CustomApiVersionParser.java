package co.com.bancolombia.api.config;

import org.springframework.web.accept.ApiVersionParser;

public class CustomApiVersionParser implements ApiVersionParser<String> {

    @Override
    public String parseVersion(String version) {
        // Remove "v" prefix if it exists (v1 becomes 1, v2 becomes 2)
        if (version.startsWith("v") || version.startsWith("V")) {
            version = version.substring(1);
        }

        // Add .0 if it's just a single number (1 becomes 1.0, 2 becomes 2.0)
        if (!version.contains(".")) {
            version = version + ".0";
        }

        return version;
    }
}
