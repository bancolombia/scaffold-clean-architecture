package co.com.crudtest.jpa;

import co.com.crudtest.jpa.entities.ProductEntity;
import co.com.crudtest.jpa.helper.AdapterOperations;
import co.com.crudtest.model.product.Product;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class JPARepositoryAdapter
    extends AdapterOperations<
        Product /* change for domain model */,
        ProductEntity /* change for adapter model */,
        String,
        JPARepository>
// implements ModelRepository from domain
{

  public JPARepositoryAdapter(JPARepository repository, ObjectMapper mapper) {
    /**
     * Could be use mapper.mapBuilder if your domain model implement builder pattern
     * super(repository, mapper, d ->
     * mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build()); Or using mapper.map with
     * the class of the object model
     */
    super(repository, mapper, d -> mapper.map(d, Product.class /* change for domain model */));
  }
}
