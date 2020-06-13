package co.com.bancolombia.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TemplateDefinition {
    private Map<String, String> files;
    private List<String> folders;
}
