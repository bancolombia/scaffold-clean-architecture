package co.com.bancolombia.config;

import co.com.bancolombia.model.IAuthenticationRepository;
import co.com.bancolombia.usecase.AuthenticationUseCase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public AuthenticationUseCase createAuthUseCase(IAuthenticationRepository authRepo){
		return new AuthenticationUseCase(authRepo);
	}

}
