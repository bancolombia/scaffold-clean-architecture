package co.com.bancolombia.templates.deployment;

import org.junit.Assert;
import org.junit.Test;

public class AzureDevOpsPipelineTemplateTest {

    @Test
    public void createAzureDevOpsPipelineTemplateTest() {
        AzureDevOpsPipelineTemplate template = new AzureDevOpsPipelineTemplate();
        Assert.assertNotNull(template);
    }
}
