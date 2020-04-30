package co.com.bancolombia.templates.properties;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesTemplateTest {

    @Test
    public void propertiesTemplateNotNull() {
        PropertiesTemplate template = new PropertiesTemplate();
        Assert.assertNotNull(template);
    }

}
