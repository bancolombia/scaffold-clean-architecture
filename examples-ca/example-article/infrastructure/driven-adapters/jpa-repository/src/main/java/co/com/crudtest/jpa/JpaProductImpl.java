package co.com.crudtest.jpa;

import co.com.crudtest.model.product.Product;
import co.com.crudtest.model.product.gateways.ProductRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JpaProductImpl implements ProductRepository {

  private JPARepositoryAdapter jpaRepositoryAdapter;

  @Override
  public void create(Product product) {
    String id = UUID.randomUUID().toString();
    product.setId(id);
    jpaRepositoryAdapter.save(product);
  }

  @Override
  public Product read(String id) {
    return jpaRepositoryAdapter.findById(id);
  }

  @Override
  public void update(String id, Product product) throws Exception {
    Product productDb = jpaRepositoryAdapter.findById(id);

    if (productDb == null) throw new Exception("Product Not Found : " + id);

    productDb.setName(product.getName());
    productDb.setPrice(product.getPrice());

    jpaRepositoryAdapter.save(productDb);
  }

  @Override
  public void delete(String id) {
    jpaRepositoryAdapter.deleteById(id);
  }

  @Override
  public List<Product> getAll() {
    return jpaRepositoryAdapter.findAll();
  }
}
