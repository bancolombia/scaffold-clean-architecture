package co.com.bancolombia.dynamodb.helper;

import org.reactivecommons.utils.ObjectMapper;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;

public abstract class TemplateAdapterOperations<E, K, V> {
    private final Class<V> dataClass;
    private final Function<V, E> toEntityFn;
    protected ObjectMapper mapper;
    private final DynamoDbTable<V> table;
    private final DynamoDbIndex<V> tableByIndex;

    @SuppressWarnings("unchecked")
    protected TemplateAdapterOperations(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                        ObjectMapper mapper,
                                        Function<V, E> toEntityFn,
                                        String tableName,
                                        String... index) {
        this.toEntityFn = toEntityFn;
        this.mapper = mapper;

        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<V>) genericSuperclass.getActualTypeArguments()[2];
        table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(dataClass));
        tableByIndex = index.length > 0 ? table.index(index[0]) : null;
    }

    public E save(E model) {
        V entity = toEntity(model);
        table.putItem(entity);
        return toModel(entity);
    }

    public E getById(K id) {
        return toModel(table.getItem(Key.builder()
                .partitionValue(AttributeValue.builder().s((String) id).build())
                .build())
        );
    }

    public void delete(E model) {
        table.deleteItem(toEntity(model));
    }

    public List<E> query(QueryEnhancedRequest queryExpression) {
        PageIterable<V> pagePublisher = table.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    public List<E> queryByIndex(QueryEnhancedRequest queryExpression, String... index) {
        DynamoDbIndex<V> queryIndex = index.length > 0 ? table.index(index[0]) : tableByIndex;
        SdkIterable<Page<V>> pagePublisher = queryIndex.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    public List<E> scan() {
        return listOfModel(table.scan());
    }

    private List<E> listOfModel(PageIterable<V> pageIterable) {
        return pageIterable.items().stream().map(this::toModel).toList();
    }

    private List<E> listOfModel(SdkIterable<Page<V>> pageIterable) {
        return pageIterable.stream().flatMap(p -> p.items().stream().map(this::toModel)).toList();
    }

    protected V toEntity(E model) {
        return mapper.map(model, dataClass);
    }

    protected E toModel(V data) {
        return data != null ? toEntityFn.apply(data) : null;
    }
}
