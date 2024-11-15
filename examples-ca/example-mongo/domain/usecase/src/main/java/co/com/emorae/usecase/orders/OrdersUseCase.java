package co.com.emorae.usecase.orders;

import co.com.emorae.model.order.Order;
import co.com.emorae.model.order.Product;
import co.com.emorae.model.order.gateways.OrderRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
public class OrdersUseCase {

    private final OrderRepository repository;

    public Flux<Order> getOrders(){
        return repository.getAll();
    }

    public Flux<Order> getOrdersByStatus(String status){
        return repository.findByStatus(status);
    }

    public Mono<Order> save(Order order){
        BigDecimal totalProducts = order.getProducts().stream()
                .map(this::getTotalProducts).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDiscount = order.getProducts().stream()
                .map(this::getTotalDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalDiscounts(totalDiscount.toPlainString());
        order.setTotalProducts(totalProducts.toPlainString());
        order.setCostDelivery(calculateDeliveryCost(order.getDeliveryInfo()));
        order.setTotal(totalProducts.add(BigDecimal.valueOf(order.getCostDelivery())).subtract(totalDiscount).toPlainString());
        return repository.save(order);
    }

    private BigDecimal getTotalDiscount(Product product) {
        return BigDecimal.valueOf(product.getDiscount())
                .multiply(BigDecimal.valueOf(product.getQuantity()));
    }

    private BigDecimal getTotalProducts(Product product) {
        return BigDecimal.valueOf(product.getPrice())
                .add(getTotalToppings(product))
                .multiply(BigDecimal.valueOf(product.getQuantity()));
    }

    private static BigDecimal getTotalToppings(Product product) {
        return product.getToppings().stream().map(topping ->
                        BigDecimal.valueOf(topping.getPrice())
                                .multiply(BigDecimal.valueOf(topping.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Double calculateDeliveryCost(Map<String,String> deliveryInfo){
        //TODO : you should calculate with delivery info
        return 6500.0;
    }

    public Mono<Order> findById(String id){
        return repository.findById(id);
    }

    public Mono<Void> delete(String id){
        return repository.delete(id);
    }

}
