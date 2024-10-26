package co.com.bancolombia.api;

import co.com.bancolombia.model.Authentication;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.usecase.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {
  private final AuthenticationUseCase authUC;

  @PostMapping(path = "/auth/save")
  public Mono<ResponseEntity<Authentication>> saveAuth(@RequestBody Authentication authentication) {
    return authUC
        .saveAuthentication(authentication)
        .map(r -> ResponseEntity.ok().body(r))
        .onErrorResume(Mono::error);
  }

  @GetMapping(path = "/auth/{username}/{channelId}")
  public Mono<ResponseEntity<Authentication>> findAuthentication(
      @PathVariable String username, @PathVariable String channelId) {
    return authUC
        .findByUsernameAndChannelId(username, channelId)
        .map(response -> ResponseEntity.ok().body(response))
        .onErrorResume(BusinessException.class, error -> Mono.just(ResponseEntity.notFound().build()))
        .onErrorResume(Mono::error);
  }

  @DeleteMapping(path = "/auth/{id}")
  public Mono<ResponseEntity> deleteAuthentication(@PathVariable int id) {
    return authUC
        .deleteAuthentication(Authentication.builder().id(id).build())
        .map(response -> ResponseEntity.ok())
        .cast(ResponseEntity.class)
        .onErrorResume(BusinessException.class, error -> Mono.just(ResponseEntity.notFound().build()))
        .onErrorResume(Mono::error);
  }
}
