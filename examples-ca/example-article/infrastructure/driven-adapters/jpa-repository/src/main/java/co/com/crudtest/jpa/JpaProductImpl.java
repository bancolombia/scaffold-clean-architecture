package co.com.crudtest.jpa;

import co.com.crudtest.model.product.Product;
import co.com.crudtest.model.product.gateways.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProductImpl implements ProductRepository {

  private final JPARepositoryAdapter jpaRepositoryAdapter;

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
        Product productDb = Optional.of(jpaRepositoryAdapter.findById(id))
                .orElseThrow(() -> new Exception("Product Not Found : " + id));

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
