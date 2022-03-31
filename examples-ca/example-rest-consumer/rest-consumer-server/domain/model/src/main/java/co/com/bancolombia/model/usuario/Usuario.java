package co.com.bancolombia.model.usuario;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Usuario {
    private String name;
    private Integer age;

}
