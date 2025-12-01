package co.com.bancolombia.api;

import co.com.bancolombia.model.usuario.Usuario;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {

    @GetMapping(path = "/{version}/list-users", version = "1.0")
    public List<Usuario> commandName() {
        return Arrays.asList(Usuario.builder()
                .name("Juan")
                .age(25)
                .build(), Usuario.builder()
                .name("Pedro")
                .age(35)
                .build(), Usuario.builder()
                .name("Santiago")
                .age(25)
                .build());
    }

    @GetMapping(path = "/{version}/list-users", version = "2.0" )
    public List<Usuario> commandNameV2() {
        return Arrays.asList(Usuario.builder()
                .name("Carlos")
                .age(21)
                .build(), Usuario.builder()
                .name("Andres")
                .age(20)
                .build(), Usuario.builder()
                .name("Diego")
                .age(40)
                .build());
    }

    @GetMapping(path = "/{version}/sum/{x}/{z}", version = "1.0")
    public Integer sum(@PathVariable("x") Integer x, @PathVariable("z") Integer z) {
        return x + z;
    }
}
