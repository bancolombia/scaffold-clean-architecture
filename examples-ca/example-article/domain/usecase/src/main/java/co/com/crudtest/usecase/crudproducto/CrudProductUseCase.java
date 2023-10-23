package co.com.crudtest.usecase.crudproducto;

import co.com.crudtest.model.product.Product;
import co.com.crudtest.model.product.gateways.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrudProductUseCase {

  private final ProductRepository productRepository;

  public void create(Product product) {
    productRepository.create(product);
  }

  public Product read(String id) {
    return productRepository.read(id);
  }

  public void update(String id, Product product) throws Exception {
    productRepository.update(id, product);
  }

  public void delete(String id) {
    productRepository.delete(id);
  }

  public List<Product> getAll() {
    return productRepository.getAll();
  }
}
