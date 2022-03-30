package co.com.bancolombia.redis.repository;

import co.com.bancolombia.model.tuple.Tuple;
import co.com.bancolombia.redis.repository.helper.RepositoryAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;


@Repository
public class RedisRepositoryAdapter extends RepositoryAdapterOperations<Tuple, TupleRedis, String, RedisRepository>
{
    public RedisRepositoryAdapter(RedisRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Tuple.class));
    }
}
