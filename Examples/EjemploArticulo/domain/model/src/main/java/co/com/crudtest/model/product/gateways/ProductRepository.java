package co.com.crudtest.model.product.gateways;

import co.com.crudtest.model.product.Product;
import java.util.List;

public interface ProductRepository {

  void create(Product product);

  Product read(String id);

  void update(String id, Product product) throws Exception;

  void delete(String id);

  List<Product> getAll();
}
