package co.com.bancolombia.r2postgresql.authentication;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthenticationRepository extends ReactiveCrudRepository<AuthenticationData, String> {

  @Query("Select * from authentication where user_name = $1 and channel_id = $2")
  Mono<AuthenticationData> findByUserNameAndClientId(String userName, String channel);
}
