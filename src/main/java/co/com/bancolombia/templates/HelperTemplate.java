package co.com.bancolombia.templates;

public class HelperTemplate {

    private HelperTemplate(){}

    public static final String MONGO_HELPER_CLASS = "AdapterOperations";
    public static final String JPA_HELPER_CLASS = "AdapterOperations";

    public static String getHelperJPARepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");

        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.data.domain.Example;\n" +
                "import org.springframework.data.repository.CrudRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.lang.reflect.ParameterizedType;\n" +
                "import java.util.List;\n" +
                "import java.util.function.Function;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import static java.util.stream.StreamSupport.stream;\n" +
                "\n" +
                "public abstract class " + JPA_HELPER_CLASS + "<E, D, I, R extends CrudRepository<D, I> & QueryByExampleExecutor<D>> {\n" +
                "\n" +
                "\n" +
                "    protected R repository;\n" +
                "    private Class<D> dataClass;\n" +
                "    protected ObjectMapper mapper;\n" +
                "    private Function<D, E> toEntityFn;\n" +
                "\n" +
                "    public " + JPA_HELPER_CLASS + "(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {\n" +
                "        this.repository = repository;\n" +
                "        this.mapper = mapper;\n" +
                "        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();\n" +
                "        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];\n" +
                "        this.toEntityFn = toEntityFn;\n" +
                "    }\n" +
                "\n" +
                "    protected D toData(E entity) {\n" +
                "        return mapper.map(entity, dataClass);\n" +
                "    }\n" +
                "\n" +
                "    protected E toEntity(D data) {\n" +
                "        return data != null ? toEntityFn.apply(data) : null;\n" +
                "    }\n" +
                "\n" +
                "    public E save(E entity) {\n" +
                "        D data = toData(entity);\n" +
                "        return toEntity(saveData(data));\n" +
                "    }\n" +
                "\n" +
                "    protected List<E> saveAllEntities(List<E> entities) {\n" +
                "        List<D> list = entities.stream().map(this::toData).collect(Collectors.toList());\n" +
                "        return toList(saveData(list));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> toList(Iterable<D> iterable) {\n" +
                "        return stream(iterable.spliterator(), false).map(this::toEntity).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    protected D saveData(D data) {\n" +
                "        return repository.save(data);\n" +
                "    }\n" +
                "\n" +
                "    protected Iterable<D> saveData(List<D> data) {\n" +
                "        return repository.saveAll(data);\n" +
                "    }\n" +
                "\n" +
                "    public E findById(I id) {\n" +
                "        return toEntity(repository.findById(id).orElse(null));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findByExample(E entity) {\n" +
                "        return toList(repository.findAll( Example.of(toData(entity))));\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "    public List<E> findAll(){\n" +
                "        return toList(repository.findAll());\n" +
                "    }\n" +
                "}";
    }

    public static String getHelperMongoRepositoryClassContent(String packageName) {
        packageName = packageName.replaceAll("\\/", "\\.");
        return "package " + packageName + ";\n" +
                "\n" +
                "import org.reactivecommons.utils.ObjectMapper;\n" +
                "import org.springframework.data.domain.Example;\n" +
                "import org.springframework.data.mongodb.repository.MongoRepository;\n" +
                "import org.springframework.data.repository.query.QueryByExampleExecutor;\n" +
                "\n" +
                "import java.lang.reflect.ParameterizedType;\n" +
                "import java.util.List;\n" +
                "import java.util.function.Function;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import static java.util.stream.StreamSupport.stream;\n" +
                "\n" +
                "public abstract class " + MONGO_HELPER_CLASS + "<E, D, I, R extends MongoRepository<D, I> & QueryByExampleExecutor<D>> {\n" +
                "\n" +
                "    protected R repository;\n" +
                "    private Class<D> dataClass;\n" +
                "    protected ObjectMapper mapper;\n" +
                "    private Function<D, E> toEntityFn;\n" +
                "\n" +
                "    public " + MONGO_HELPER_CLASS + "(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {\n" +
                "        this.repository = repository;\n" +
                "        this.mapper = mapper;\n" +
                "        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();\n" +
                "        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];\n" +
                "        this.toEntityFn = toEntityFn;\n" +
                "    }\n" +
                "\n" +
                "    protected D toData(E entity) {\n" +
                "        return mapper.map(entity, dataClass);\n" +
                "    }\n" +
                "\n" +
                "    protected E toEntity(D data) {\n" +
                "        return data != null ? toEntityFn.apply(data) : null;\n" +
                "    }\n" +
                "\n" +
                "    public E save(E entity) {\n" +
                "        D data = toData(entity);\n" +
                "        return toEntity(saveData(data));\n" +
                "    }\n" +
                "\n" +
                "    protected List<E> saveAllEntities(List<E> entities) {\n" +
                "        List<D> list = entities.stream().map(this::toData).collect(Collectors.toList());\n" +
                "        return toList(saveData(list));\n" +
                "    }\n" +
                "\n" +
                "    private List<E> toList(Iterable<D> iterable) {\n" +
                "        return stream(iterable.spliterator(), false).map(this::toEntity).collect(Collectors.toList());\n" +
                "    }\n" +
                "\n" +
                "    private D saveData(D data) {\n" +
                "        return repository.save(data);\n" +
                "    }\n" +
                "\n" +
                "    protected Iterable<D> saveData(List<D> data) {\n" +
                "        return repository.saveAll(data);\n" +
                "    }\n" +
                "\n" +
                "    public E findById(I id) {\n" +
                "        return toEntity(repository.findById(id).orElse(null));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findByExample(E entity) {\n" +
                "        return toList(repository.findAll( Example.of(toData(entity))));\n" +
                "    }\n" +
                "\n" +
                "    public List<E> findAll(){\n" +
                "        return toList(repository.findAll());\n" +
                "    }\n" +
                "}";
    }

    public static String getSettingsHelperJPAContent() {
        return "\ninclude \":jpa-repository-commons\"\n" +
                "project(':jpa-repository-commons').projectDir = file('./infrastructure/helpers/jpa-repository-commons')\n";
    }

    public static String getSettingsHelperMongoContent() {
        return "\ninclude \":mongo-repository-commons\"\n" +
                "project(':mongo-repository-commons').projectDir = file('./infrastructure/helpers/mongo-repository-commons')\n";
    }

    public static String getBuildGradleHelperJPARepository() {

        return "dependencies {\n" +
                "    implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "}";

    }

    public static String getBuildGradleHelperMongoRepository() {

        return "dependencies {\n" +
                "  implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'\n" +
                "    compile 'org.reactivecommons.utils:object-mapper-api:0.1.0'\n" +
                "}";

    }
}
