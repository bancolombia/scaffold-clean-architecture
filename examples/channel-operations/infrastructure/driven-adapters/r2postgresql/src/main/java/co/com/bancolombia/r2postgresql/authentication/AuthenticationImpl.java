package co.com.bancolombia.r2postgresql.authentication;

import co.com.bancolombia.model.Authentication;
import co.com.bancolombia.model.IAuthenticationRepository;
import co.com.bancolombia.model.exception.TechnicalException;
import co.com.bancolombia.model.exception.message.TechnicalErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AuthenticationImpl implements IAuthenticationRepository {

  private final AuthenticationRepository repository;
  private final AuthenticationMapper mapper;

  @Override
  @Transactional
  public Mono<Authentication> save(Authentication authentication) {
    return repository
        .save(mapper.toData(authentication))
        .map(mapper::toModel)
        .onErrorMap(
            Exception.class,
            e -> new TechnicalException(e, TechnicalErrorMessage.AUTHENTICATION_SAVE));
  }

  @Override
  public Mono<Authentication> findByUserNameAndChannelId(String userName, String channel) {
    return repository
        .findByUserNameAndClientId(userName, channel)
        .map(mapper::toModel)
        .onErrorMap(
            Exception.class,
            e -> new TechnicalException(e, TechnicalErrorMessage.AUTHENTICATION_FIND));
  }

  @Override
  public Mono<Void> delete(Authentication authentication) {
    return repository
        .delete(mapper.toData(authentication))
        .onErrorMap(
            Exception.class,
            e -> new TechnicalException(e, TechnicalErrorMessage.AUTHENTICATION_DELETE));
  }
}
