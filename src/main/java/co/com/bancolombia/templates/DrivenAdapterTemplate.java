package co.com.bancolombia.templates;

import java.util.Arrays;
import java.util.function.Supplier;

public class DrivenAdapterTemplate {

    public static final String COMMON = "common";

    public static final String EVENT_BUS_GATEWAY_CLASS = "EventsGateway";
    public static final String JPA_REPOSITORY_INTERFACE = "JPARepository";
    public static final String JPA_REPOSITORY_CLASS = "JPARepositoryAdapter";
    public static final String MONGO_REPOSITORY_CLASS = "MongoRepositoryAdapter";
    public static final String MONGO_REPOSITORY_INTERFACE = "MongoRepository";
    public static final String EVENT_BUS_CLASS = "ReactiveEventsGateway";
    private static final String DEPENDENCIES = "dependencies {\n";
    private static final String MODEL_PROJECT = "    implementation project(':model')\n";
    public static final String SECRET_MODEL_NAME = "secret";
    public static final String EVENT_BUS_MODEL_NAME = "event";

    private DrivenAdapterTemplate() {
    }

    public enum DrivenAdapters {
        NO_AVAILABLE(-1),
        EMPTY(0),
        JPA_REPOSITORY(1),
        MONGO_REPOSITORY(2),
        ASYNC_EVENT_BUS(3);

        private int value;

        private DrivenAdapters(int value) {
            this.value = value;
        }

        public static DrivenAdapters valueOf(int value, Supplier<? extends DrivenAdapters> byDef) {
            return Arrays.asList(values()).stream()
                    .filter(legNo -> legNo.value == value)
                    .findFirst().orElseGet(byDef);
        }
    }


    public static String getBuildGradleJPARepository() {

        return DEPENDENCIES +
                MODEL_PROJECT +
                "    implementation project(':jpa-repository-commons')\n" +
                "\n" +
                "    compile 'org.springframework.boot:spring-boot-starter-data-jpa'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "    compile 'org.apache.commons:commons-dbcp2:2.2.0'\n" +
                "\n" +
                "    testCompile 'org.reactivecommons.utils:object-mapper:0.1.0'\n" +

                "}";

    }

    public static String getBuildGradleMongoRepository() {

        return DEPENDENCIES +
                MODEL_PROJECT +
                "    implementation project(':mongo-repository-commons')\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    implementation 'org.springframework:spring-context:5.2.0.RELEASE'\n" +
                "    implementation 'javax.persistence:javax.persistence-api:2.2'\n" +
                "}";
    }



    public static String getBuildGradleEventBus() {

        return DEPENDENCIES +
                "    compile project(':model')\n" +
                "    compile group: 'org.reactivecommons', name: 'async-commons-starter', version: '0.0.7-beta1'\n" +
                "    compile('org.springframework:spring-context')\n" +
                "}";

    }

    public static String getJPARepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Repository\n" +
                "public class " + JPA_REPOSITORY_CLASS + " extends AdapterOperations<Object, Object, String," + JPA_REPOSITORY_INTERFACE + ">\n" +
                "/**        implements <INTERFACE DOMAIN> **/" +
                "\n{\n" +
                "\n" +
                "    @Autowired\n" +
                "    public " + JPA_REPOSITORY_CLASS + "(" + JPA_REPOSITORY_INTERFACE + " repository, ObjectMapper mapper) {\n" +
                "        super(repository, mapper, d -> mapper.mapBuilder(d, Object.class));\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    public static String getJPARepositoryInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.springframework.data.jpa.repository.Query;\n" +
                "import org.springframework.data.repository.CrudRepository;\n" +
                "import org.springframework.data.repository.query.Param;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "public interface " + JPA_REPOSITORY_INTERFACE + " extends CrudRepository<Object, String>, QueryByExampleExecutor<Object> {\n" +
                "}";
    }

    public static String getMongoRepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "@Repository\n" +
                "public class " + MONGO_REPOSITORY_CLASS + " extends AdapterOperations<Object, Object, String, " + MONGO_REPOSITORY_INTERFACE + "> " +
                "/**        implements <INTERFACE DOMAIN> **/" +
                "\n{\n" +
                "\n" +
                "    @Autowired\n" +
                "    public " + MONGO_REPOSITORY_CLASS + "(" + MONGO_REPOSITORY_INTERFACE + " repository, ObjectMapper mapper) {\n" +
                "        super(repository, mapper, d -> mapper.mapBuilder(d, Object.class));\n" +
                "    }\n" +
                "\n" +
                "}";
    }

    public static String getMongoRepositoryInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.springframework.data.mongodb.repository.MongoRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "public interface " + MONGO_REPOSITORY_INTERFACE + " extends MongoRepository<Object, String> , QueryByExampleExecutor<Object> {\n" +
                "}";
    }

    public static String getEventBusClassContent(String packageName, String drivenAdapterPackage) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + "." + drivenAdapterPackage + ";\n" +
                "\n" +
                "import co.com.bancolombia.common.gateways.EventsGateway;\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import lombok.extern.java.Log;\n" +
                "import org.reactivecommons.api.domain.DomainEvent;\n" +
                "import org.reactivecommons.api.domain.DomainEventBus;\n" +
                "import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "import reactor.core.publisher.Mono;\n" +
                "import java.util.UUID;\n" +
                "import java.util.logging.Level;\n" +
                "import static reactor.core.publisher.Mono.from;\n" +
                "\n" +
                "@Log\n" +
                "@Component\n" +
                "@EnableDomainEventBus\n" +
                "@RequiredArgsConstructor\n" +
                "/**Permite personalizar la emisión de eventos, enriquecerla o interceptarla.\n" +
                "Por defecto delega el proceso en reactive-commons.\n" +
                " \n" +
                " Remplazar el tipo del objeto  event por le modelo correspondiente\n" +
                " **/\n" +
                "public class ReactiveEventsGateway implements EventsGateway {\n" +
                "\n" +
                "    private final DomainEventBus domainEventBus;\n" +
                "\n" +
                "    @Override\n" +
                "    public Mono<Void> emit(Object event) {\n" +
                "        log.log(Level.INFO, \"Emitiendo evento de dominio: {0}: {1}\", new String[]{(String) event, event.toString()});\n" +
                "        return from(domainEventBus.emit(new DomainEvent<>((String) event, UUID.randomUUID().toString(), event)));\n" +
                "    }\n" +
                "}";

    }

    public static String getEventBusInterfaceContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import reactor.core.publisher.Mono;\n" +
                "public interface " + EVENT_BUS_GATEWAY_CLASS + "<T> {\n" +
                "       Mono<Void> emit(Object event);\n" +
                "}";
    }

    public static String getJPAImportContent() {
        return "\n" +
                "    compile 'org.apache.commons:commons-dbcp2:2.2.0'\n" +
                "    compile group: 'co.com.bancolombia', name: 'secretsmanager', version: '2.0.1'\n";
    }

    public static String getSettingsJPARepositoryContent() {
        return "\ninclude \":jpa-repository\"\n" +
                "project(':jpa-repository').projectDir = file('./infrastructure/driven-adapters/jpa-repository')\n";
    }

    public static String getSettingsMongoRepositoryContent() {
        return "\ninclude \":mongo-repository\"\n" +
                "project(':mongo-repository').projectDir = file('./infrastructure/driven-adapters/mongo-repository')\n";
    }

    public static String getSettingsEventBusContent() {
        return "\ninclude \":async-event-bus\"\n" +
                "project(':async-event-bus').projectDir = file('./infrastructure/driven-adapters/async-event-bus')\n";
    }
}
