package co.com.bancolombia.redis.repository.helper;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.StreamSupport.stream;

public abstract class RepositoryAdapterOperations<E, D, I, R extends KeyValueRepository<D, I> & QueryByExampleExecutor<D>> {
    protected R repository;
    private final Class<D> dataClass;
    protected ObjectMapper mapper;
    private final Function<D, E> toEntityFn;

    @SuppressWarnings("unchecked")
    protected RepositoryAdapterOperations(R repository, ObjectMapper mapper, Function<D, E> toEntityFn) {
        this.repository = repository;
        this.mapper = mapper;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<D>) genericSuperclass.getActualTypeArguments()[1];
        this.toEntityFn = toEntityFn;
    }

    protected D toData(E entity) {
        return mapper.map(entity, dataClass);
    }

    protected E toEntity(D data) {
        return data != null ? toEntityFn.apply(data) : null;
    }

    public E save(E entity) {
        D data = toData(entity);
        return toEntity(saveData(data));
    }

    protected List<E> saveAllEntities(List<E> entities) {
        List<D> list = entities.stream().map(this::toData).toList();
        return toList(saveData(list));
    }

    public List<E> toList(Iterable<D> iterable) {
        return stream(iterable.spliterator(), false).map(this::toEntity).toList();
    }

    protected D saveData(D data) {
        return repository.save(data);
    }

    protected Iterable<D> saveData(List<D> data) {
        return repository.saveAll(data);
    }

    public E findById(I id) {
        return toEntity(repository.findById(id).orElse(null));
    }

    public void delete(E data) {
        repository.delete(toData(data));
    }

    public List<E> findByExample(E entity) {
        return toList(repository.findAll(Example.of(toData(entity))));
    }

    public List<E> findAll() {
        return toList(repository.findAll());
    }
}