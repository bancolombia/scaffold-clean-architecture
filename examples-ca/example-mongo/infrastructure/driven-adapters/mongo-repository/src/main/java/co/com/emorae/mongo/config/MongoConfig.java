package co.com.emorae.mongo.config;

import com.mongodb.ConnectionString;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.mongodb.autoconfigure.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDBSecret dbSecret(Environment env) {
        return MongoDBSecret.builder()
                .uri(env.getProperty("spring.data.mongodb.uri"))
                .build();
    }

    @Bean
    public ReactiveMongoClientFactory mongoProperties(MongoDBSecret secret) {
        List<MongoClientSettingsBuilderCustomizer> list = new ArrayList<>();
        list.add(mongoDBDefaultSettings(secret.getUri()));
        return new ReactiveMongoClientFactory(list);
    }

    private MongoClientSettingsBuilderCustomizer mongoDBDefaultSettings(String uri) {
        return builder -> builder.applyConnectionString(new ConnectionString(uri))
                .applyToSslSettings(blockBuilder -> blockBuilder.enabled(false));
    }
}
