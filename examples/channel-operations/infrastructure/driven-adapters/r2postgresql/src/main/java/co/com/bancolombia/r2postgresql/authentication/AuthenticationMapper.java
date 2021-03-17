package co.com.bancolombia.r2postgresql.authentication;

import co.com.bancolombia.model.Authentication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {

    Authentication toModel(AuthenticationData authenticationData);
    AuthenticationData toData(Authentication authentication);
}
