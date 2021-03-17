package co.com.bancolombia.model;

import reactor.core.publisher.Mono;

public interface IAuthenticationRepository {

	Mono<Authentication> save(Authentication authentication);

	Mono<Authentication> findByUserNameAndChannelId(String userName, String channel);

	Mono<Void> delete(Authentication authentication);
}
