package co.com.bancolombia.templates.config;

import org.junit.Assert;
import org.junit.Test;

public class JpaConfigTemplateTest {

    @Test
    public void createJpaConfigTemplateTest() {
        JpaConfigTemplate jpaConfigTemplate = new JpaConfigTemplate();
        Assert.assertNotNull(jpaConfigTemplate);
    }

}
