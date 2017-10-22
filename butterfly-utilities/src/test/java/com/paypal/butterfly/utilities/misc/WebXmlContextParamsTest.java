package com.paypal.butterfly.utilities.misc;

import com.paypal.butterfly.extensions.api.TUExecutionResult;
import com.paypal.butterfly.utilities.TransformationUtilityTestHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Unit tests for {@link WebXmlContextParams}
 *
 * @author facarvalho
 */
public class WebXmlContextParamsTest extends TransformationUtilityTestHelper {

    @Test
    public void test() {
        WebXmlContextParams webXmlContextParams = new WebXmlContextParams().relative("/src/main/webapp/WEB-INF/web.xml");
        TUExecutionResult executionResult = webXmlContextParams.execution(transformedAppFolder, transformationContext);
        Assert.assertEquals(executionResult.getType(), TUExecutionResult.Type.VALUE);
        Assert.assertNotNull(executionResult.getValue());

        Map<String, String> map = (Map<String, String>) executionResult.getValue();
        Assert.assertEquals(map.size(), 2);
        Assert.assertEquals(map.get("contextConfigLocation"), "/WEB-INF/spring/context.xml");
        Assert.assertEquals(map.get("contextClass"), "org.springframework.web.context.support.XmlWebApplicationContext");
        Assert.assertEquals(webXmlContextParams.getDescription(), "Parses Java web deployment descriptor file (/src/main/webapp/WEB-INF/web.xml), identifies all context parameters, and save them into a map");
    }

}
