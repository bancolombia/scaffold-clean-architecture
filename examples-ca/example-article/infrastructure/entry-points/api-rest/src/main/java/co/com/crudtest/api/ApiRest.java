package co.com.crudtest.api;

import co.com.crudtest.model.product.Product;
import co.com.crudtest.usecase.crudproducto.CrudProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@ResponseBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
  private final CrudProductUseCase crudProductUseCase;

  @GetMapping(path = "/product/{id}")
  public Product read(@PathVariable String id) {
    return crudProductUseCase.read(id);
  }

  @PostMapping(path = "/product")
  public void create(@RequestBody Product product) {
    crudProductUseCase.create(product);
  }

  @PutMapping(path = "/product/{id}")
  public void update(@PathVariable String id, @RequestBody Product product) {
    try {
      crudProductUseCase.update(id, product);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @DeleteMapping(path = "/product/{id}")
  public void delete(@PathVariable String id) {
    crudProductUseCase.delete(id);
  }

  @GetMapping(path = "/product")
  public List<Product> getAll() {
    return crudProductUseCase.getAll();
  }
}
