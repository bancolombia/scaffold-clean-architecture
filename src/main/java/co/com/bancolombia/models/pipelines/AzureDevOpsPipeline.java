package co.com.bancolombia.models.pipelines;

import co.com.bancolombia.templates.deployment.AzureDevOpsPipelineTemplate;

import java.util.stream.Collectors;

public class AzureDevOpsPipeline extends AbstractModule {

    public AzureDevOpsPipeline(int code) {
        super(code);
    }

    @Override
    public String getPipelineContent(String settings) {
        super.setPropertiesToPipeline(settings);
        String pipelineContent = AzureDevOpsPipelineTemplate.generateAzureDevOpsPipelineTemplateContent();

        String binaries = super.getModulesPaths().stream()
                .map(x -> new String().concat(AzureDevOpsPipelineTemplate.BUILD_SOURCE_DIRECTORY)
                        .concat(x)
                        .concat(AzureDevOpsPipelineTemplate.BUILD_BINARIES_PATH))
                .collect(Collectors.joining(","));

        String test = super.getModulesPaths().stream()
                .filter(x -> !x.contains("app-service"))
                .map(x-> new String().concat(AzureDevOpsPipelineTemplate.BUILD_SOURCE_DIRECTORY
                        .concat(x)
                        .concat(AzureDevOpsPipelineTemplate.BUILD_TEST_PATH)))
                .collect(Collectors.joining(","));

        return pipelineContent.replace("[ProjectName]", super.getProjectName())
                .replace("[Binaries]", binaries)
                .replace("[Test]", test);
    }


}
