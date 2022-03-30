package co.com.bancolombia.model.tuple.gateways;

import co.com.bancolombia.model.tuple.Tuple;

import java.util.List;

public interface TupleRepository {

    Tuple save(Tuple tuple);

    List<Tuple> listAll();

    boolean delete(String id);

    Tuple get(String id);
}
