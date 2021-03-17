package co.com.bancolombia.usecase;

import java.time.LocalDateTime;

import co.com.bancolombia.model.Authentication;
import co.com.bancolombia.model.IAuthenticationRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.message.BusinessErrorMessage;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthenticationUseCase {

	private final IAuthenticationRepository authRepository;

	public Mono<Authentication> saveAuthentication(Authentication authentication){
		authentication.setAuthenticationTime(LocalDateTime.now());
		return authRepository.save(authentication);
	}

	public Mono<Authentication> findByUsernameAndChannelId(String username, String channelID){
		return authRepository.findByUserNameAndChannelId(username, channelID)
				.switchIfEmpty(getBusinessError(BusinessErrorMessage.AUTHENTICATION_NOT_FOUND));
	}

	public Mono<Void> deleteAuthentication(Authentication authentication){
		return authRepository.delete(authentication);

	}

	private <T> Mono<T> getBusinessError(BusinessErrorMessage businessErrorMessage) {
		return Mono.error(getBusinessException(businessErrorMessage));
	}

	private BusinessException getBusinessException(BusinessErrorMessage businessErrorMessage) {
		return new BusinessException(businessErrorMessage);
	}
}
