package co.com.bancolombia.models;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TemplateDefinition {
  private Map<String, String> files;
  private List<String> folders;
}
