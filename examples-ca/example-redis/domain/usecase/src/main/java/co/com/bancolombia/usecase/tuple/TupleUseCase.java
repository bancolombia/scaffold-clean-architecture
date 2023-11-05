package co.com.bancolombia.usecase.tuple;

import co.com.bancolombia.model.tuple.Tuple;
import co.com.bancolombia.model.tuple.gateways.TupleRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TupleUseCase {

    private final TupleRepository repository;

    public Tuple save(Tuple tuple) {
        return repository.save(tuple);
    }

    public List<Tuple> listAll() {
        return repository.listAll();
    }

    public boolean delete(String id) {
        return repository.delete(id);
    }

    public Tuple get(String id) {
        return repository.get(id);
    }

}
