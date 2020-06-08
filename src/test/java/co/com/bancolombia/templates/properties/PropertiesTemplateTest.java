package co.com.bancolombia.templates.properties;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesTemplateTest {

    @Test
    public void propertiesTemplateNotNull() {
        String template = PropertiesTemplate.getJpaPropertiesContent();
        Assert.assertNotNull(template);
    }

}
