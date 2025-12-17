package co.com.bancolombia.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .addSupportedVersions("1.0","2.0")
                .setDefaultVersion("1.0")
                .usePathSegment(1)
                //.useRequestHeader("X-API-Version");
                //.useQueryParam("version")
                //.useMediaTypeParameter(MediaType.APPLICATION_JSON, "version")
                .setVersionParser(new CustomApiVersionParser());
    }
}
