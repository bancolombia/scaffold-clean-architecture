package co.com.bancolombia.redis.repository;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface RedisRepository extends KeyValueRepository<TupleRedis, String>, QueryByExampleExecutor<TupleRedis> {
}