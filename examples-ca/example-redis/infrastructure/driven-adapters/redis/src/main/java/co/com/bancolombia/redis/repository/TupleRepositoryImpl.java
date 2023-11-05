package co.com.bancolombia.redis.repository;

import co.com.bancolombia.model.tuple.Tuple;
import co.com.bancolombia.model.tuple.gateways.TupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TupleRepositoryImpl implements TupleRepository {

    private final RedisRepositoryAdapter adapter;

    @Override
    public Tuple save(Tuple tuple) {
        return adapter.save(tuple);
    }

    @Override
    public List<Tuple> listAll() {
        return adapter.findAll();
    }

    @Override
    public boolean delete(String tupleId) {
        Tuple deleteOrder = adapter.findById(tupleId);
        if(deleteOrder != null){
            adapter.delete(deleteOrder);
            return true;
        }
        return false;
    }

    @Override
    public Tuple get(String tupleId) {
        return adapter.findById(tupleId);
    }
}
