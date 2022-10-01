/*
 * Copyright 2006-2022 The JGUIraffe Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jguiraffe.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.sf.jguiraffe.resources.ResourceManager;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for TransformerContextPropertiesWrapper.
 *
 * @author Oliver Heger
 * @version $Id: TestTransformerContextPropertiesWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTransformerContextPropertiesWrapper
{
    /**
     * Tests querying the locale.
     */
    @Test
    public void testGetLocale()
    {
        final Locale locale = Locale.GERMAN;
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.expect(ctx.getLocale()).andReturn(locale);
        EasyMock.replay(ctx);
        TransformerContextPropertiesWrapper wrapper = new TransformerContextPropertiesWrapper(
                ctx, new HashMap<String, Object>());
        assertEquals("Wrong locale", locale, wrapper.getLocale());
        EasyMock.verify(ctx);
    }

    /**
     * Tests querying the resource manager.
     */
    @Test
    public void testGetResourceManager()
    {
        ResourceManager resMan = EasyMock.createMock(ResourceManager.class);
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.expect(ctx.getResourceManager()).andReturn(resMan);
        EasyMock.replay(resMan, ctx);
        TransformerContextPropertiesWrapper wrapper = new TransformerContextPropertiesWrapper(
                ctx, new HashMap<String, Object>());
        assertEquals("Wrong resource manager", resMan, wrapper
                .getResourceManager());
        EasyMock.verify(resMan, ctx);
    }

    /**
     * Tests querying the validation message handler.
     */
    @Test
    public void testGetValidationMessageHandler()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        ValidationMessageHandler handler = EasyMock
                .createMock(ValidationMessageHandler.class);
        EasyMock.expect(ctx.getValidationMessageHandler()).andReturn(handler);
        EasyMock.replay(ctx, handler);
        TransformerContextPropertiesWrapper wrapper = new TransformerContextPropertiesWrapper(
                ctx, new HashMap<String, Object>());
        assertEquals("Wrong validation message handler", handler, wrapper
                .getValidationMessageHandler());
        EasyMock.verify(ctx, handler);
    }

    /**
     * Tests the properties() method.
     */
    @Test
    public void testProperties()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        Map<String, Object> oldProps = new HashMap<String, Object>();
        oldProps.put("prop1", "value1");
        oldProps.put("prop2", "value2");
        EasyMock.expect(ctx.properties()).andReturn(oldProps);
        EasyMock.replay(ctx);
        Map<String, Object> newProps = new HashMap<String, Object>();
        newProps.put("prop2", "newvalue");
        newProps.put("prop3", "value3");
        TransformerContextPropertiesWrapper wrapper = new TransformerContextPropertiesWrapper(
                ctx, newProps);
        Map<String, Object> props = wrapper.properties();
        EasyMock.verify(ctx);
        assertEquals("Wrong number of properties", 3, props.size());
        assertEquals("Wrong value for prop1", "value1", props.get("prop1"));
        assertEquals("Wrong value for prop2", "newvalue", props.get("prop2"));
        assertEquals("Wrong value for prop3", "value3", props.get("prop3"));
    }

    /**
     * Tests the implementation of getTypedProperty().
     */
    @Test
    public void testGetTypedProperty()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        final Integer prop = 42;
        EasyMock.expect(ctx.getTypedProperty(Integer.class)).andReturn(prop);
        EasyMock.replay(ctx);
        TransformerContextPropertiesWrapper wrapper = new TransformerContextPropertiesWrapper(
                ctx, new HashMap<String, Object>());
        assertEquals("Wrong typed property", prop, wrapper
                .getTypedProperty(Integer.class));
        EasyMock.verify(ctx);
    }

    /**
     * Tests creating an instance with a null context. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNullContext()
    {
        new TransformerContextPropertiesWrapper(null,
                new HashMap<String, Object>());
    }

    /**
     * Tests creating an instance with null properties. This should cause an
     * exception.
     */
    @Test
    public void testInitNullProps()
    {
        TransformerContext ctx = EasyMock.createMock(TransformerContext.class);
        EasyMock.replay(ctx);
        try
        {
            new TransformerContextPropertiesWrapper(ctx, null);
            fail("Could create instance with null properties!");
        }
        catch (IllegalArgumentException iex)
        {
            EasyMock.verify(ctx);
        }
    }
}
