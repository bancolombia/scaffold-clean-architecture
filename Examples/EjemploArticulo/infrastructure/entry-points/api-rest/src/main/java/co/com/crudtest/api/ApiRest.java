package co.com.crudtest.api;

import co.com.crudtest.model.product.Product;
import co.com.crudtest.usecase.crudproducto.CrudProductoUseCase;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@ResponseBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {
  private final CrudProductoUseCase crudProductoUseCase;

  @GetMapping(path = "/producto/{id}")
  public Product read(@PathVariable String id) {
    return crudProductoUseCase.read(id);
  }

  @PostMapping(path = "/producto")
  public void create(@RequestBody Product product) {
    crudProductoUseCase.create(product);
  }

  @PutMapping(path = "/producto/{id}")
  public void update(@PathVariable String id, @RequestBody Product product) {
    try {
      crudProductoUseCase.update(id, product);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping(path = "/producto/{id}")
  public void delete(@PathVariable String id) {
    crudProductoUseCase.delete(id);
  }

  @GetMapping(path = "/producto")
  public List<Product> getAll() {
    return crudProductoUseCase.getAll();
  }
}
