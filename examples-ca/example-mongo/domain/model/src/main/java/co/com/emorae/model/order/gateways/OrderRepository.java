package co.com.emorae.model.order.gateways;

import co.com.emorae.model.order.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {

    Mono<Order> findById(String id);

    Mono<Void> delete(String id);

    Mono<Order> save(Order order);

    Flux<Order> getAll();

    Flux<Order> findByStatus(String status);

}
