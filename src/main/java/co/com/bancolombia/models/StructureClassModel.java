package co.com.bancolombia.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StructureClassModel {
    private String packpage;
    private List<String> imports;
    private List<String> tags;
    private String name_class;
    private String name_test_class;
    private List<AttributeClassModel> attributes;
    private boolean builder;
}
