package co.com.emorae.mongo;

import co.com.emorae.model.order.Order;
import co.com.emorae.model.order.gateways.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final MongoRepositoryOrderAdapter dao;
    private final MongoDBRepository repository;

    @Override
    public Mono<Order> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Void> delete(String id) {
        return dao.deleteById(id);
    }

    @Override
    public Mono<Order> save(Order order) {
        return dao.save(order);
    }

    @Override
    public Flux<Order> getAll() {
       return dao.findAll();
    }

    @Override
    public Flux<Order> findByStatus(String status) {
        return dao.doQueryMany(repository.findByStatus(status));
    }
}
