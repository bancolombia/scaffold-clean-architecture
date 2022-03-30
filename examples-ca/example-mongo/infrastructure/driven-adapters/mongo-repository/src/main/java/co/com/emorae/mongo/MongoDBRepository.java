package co.com.emorae.mongo;

import co.com.emorae.mongo.document.OrderDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Flux;

public interface MongoDBRepository extends ReactiveMongoRepository<OrderDocument, String>, ReactiveQueryByExampleExecutor<OrderDocument> {
    Flux<OrderDocument> findByStatus(String status);
}
