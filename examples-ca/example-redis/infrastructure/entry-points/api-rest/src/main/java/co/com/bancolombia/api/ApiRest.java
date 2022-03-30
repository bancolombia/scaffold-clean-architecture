package co.com.bancolombia.api;
import co.com.bancolombia.model.tuple.Tuple;
import co.com.bancolombia.usecase.tuple.TupleUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    private final TupleUseCase useCase;

    @GetMapping(path = "/tuple")
    public List<Tuple> getAll() {
        return useCase.listAll();
    }

    @GetMapping(path = "/tuple/{id}")
    public Tuple getById(@PathVariable String id) {
        return useCase.get(id);
    }

    @PostMapping(path = "/tuple")
    public Tuple save(@RequestBody Tuple tuple) {
        return useCase.save(tuple);
    }

    @DeleteMapping(path = "/tuple/{id}")
    public void delete(@PathVariable String id) {
        useCase.delete(id);
    }
}
