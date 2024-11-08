package co.com.bancolombia.model.postmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
