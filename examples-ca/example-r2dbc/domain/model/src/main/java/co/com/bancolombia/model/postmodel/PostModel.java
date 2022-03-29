package co.com.bancolombia.model.postmodel;
import lombok.*;

@Data
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostModel {

    private Integer id;
    private String title;
    private String content;

}
