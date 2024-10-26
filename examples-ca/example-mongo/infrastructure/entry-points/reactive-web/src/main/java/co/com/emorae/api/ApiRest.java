package co.com.emorae.api;

import co.com.emorae.model.order.Order;
import co.com.emorae.model.order.OrderStatus;
import co.com.emorae.usecase.orders.OrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/orders")
@RequiredArgsConstructor
public class ApiRest {

    private final OrdersUseCase service;

    @GetMapping
    public Mono<ResponseEntity<Flux<Order>>> getAll() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getOrders())
        );
    }

    @GetMapping("/sent")
    public Mono<ResponseEntity<Flux<Order>>> getAllSent() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getOrdersByStatus(OrderStatus.SENT.name()))
        );
    }

    @GetMapping("/payment")
    public Mono<ResponseEntity<Flux<Order>>> getAllPayment() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getOrdersByStatus(OrderStatus.PAYMENT.name()))
        );
    }

    @GetMapping("/shopping-kart")
    public Mono<ResponseEntity<Flux<Order>>> getAllShoppingKart() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(service.getOrdersByStatus(OrderStatus.SHOPPING_KART.name()))
        );
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> create(@RequestBody Mono<Order> monoOrder) {
        Map<String, Object> response = new HashMap<>();

        return monoOrder.flatMap(order -> {
            if (order.getCreateAt() == null) {
                order.setCreateAt(new Date());
            }
            return service.save(Order.builder().createAt(new Date()).status(OrderStatus.SHOPPING_KART)
                    .products(order.getProducts()).clientInfo(order.getClientInfo()).build()
            ).map(o -> {
                response.put("order", o);
                response.put("message", "Order Create Successful");
                response.put("timestamp", new Date());
                return ResponseEntity
                        .created(URI.create("/api/orders/".concat(o.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response);
            });

        }).onErrorResume(t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(list -> {
                    response.put("errors", list);
                    response.put("timestamp", new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                }));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> read(@PathVariable String id) {
        return service.findById(id).map(order -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(order))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Order>> update(@RequestBody Order request, @PathVariable String id) {
        return service.findById(id).flatMap(order -> {
                    order.setProducts(request.getProducts());
                    order.setPhone(request.getPhone());
                    order.setClientInfo(request.getClientInfo());
                    order.setDeliveryInfo(request.getDeliveryInfo());
                    return order.getStatus().equals(OrderStatus.SHOPPING_KART) ? service.save(order) : Mono.just(order);
                }).map(p -> ResponseEntity.created(URI.create("/api/orders/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}/buy")
    public Mono<ResponseEntity<Order>> updateAndBuy(@RequestBody Order request, @PathVariable String id) {
        return service.findById(id).flatMap(order -> {
                    order.setStatus(OrderStatus.PAYMENT);
                    order.setPaymentDate(new Date());
                    order.setPaymentInfo(request.getPaymentInfo());
                    return order.getStatus().equals(OrderStatus.SHOPPING_KART) ? service.save(order) : Mono.just(order);
                }).map(p -> ResponseEntity.created(URI.create("/api/orders/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/sent")
    public Mono<ResponseEntity<Order>> updateAndSent(@PathVariable String id) {
        return service.findById(id).flatMap(order -> {
                    order.setStatus(OrderStatus.SENT);
                    order.setSentDate(new Date());
                    return order.getStatus().equals(OrderStatus.PAYMENT) ? service.save(order) : Mono.just(order);
                }).map(p -> ResponseEntity.created(URI.create("/api/orders/".concat(p.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.findById(id).flatMap(order -> service.delete(order.getId())
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
