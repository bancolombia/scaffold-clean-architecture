package co.com.bancolombia.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface RedisRepository extends CrudRepository<TupleRedis, String>, QueryByExampleExecutor<TupleRedis> {
}
