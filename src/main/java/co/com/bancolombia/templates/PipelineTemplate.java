package co.com.bancolombia.templates;

import java.util.Arrays;
import java.util.function.Supplier;

public class PipelineTemplate {
    public enum Pipelines {
        NO_AVAILABLE(-1),
        EMPTY(0),
        AZURE_DEVOPS(1);

        private int value;

        private Pipelines(int value) {
            this.value = value;
        }

        public static PipelineTemplate.Pipelines valueOf(int value, Supplier<? extends PipelineTemplate.Pipelines> byDef) {
            return Arrays.asList(values()).stream()
                    .filter(legNo -> legNo.value == value)
                    .findFirst().orElseGet(byDef);
        }
    }
}
