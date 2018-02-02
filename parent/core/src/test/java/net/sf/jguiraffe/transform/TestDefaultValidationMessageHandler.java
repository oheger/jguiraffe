/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.resources.ResourceManager;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DefaultValidationMessageHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultValidationMessageHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDefaultValidationMessageHandler
{
    /** Constant for the used locale. */
    private static final Locale LOCALE = Locale.GERMAN;

    /** Constant for a message key. */
    private static final String KEY = "myMessage";

    /** Constant for an alternative resource group. */
    private static final String RES_GRP = "alternativeGroup";

    /** Constant for a message. */
    private static final String MESSAGE = "This is a {0} message.";

    /** Constant for a parameter. */
    private static final String PARAM = "test";

    /** Constant for the final message. */
    private static final String PROCESSED_MESSAGE = "This is a test message.";

    /** Stores a mock for the transformer context. */
    private TransformerContext context;

    /** Stores a mock for the resource manager. */
    private ResourceManager resourceManager;

    /** Stores the handler to be tested. */
    private DefaultValidationMessageHandler handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new DefaultValidationMessageHandler();
    }

    /**
     * Sets up a mock for the transformer context and the resource manager.
     *
     * @param props a map with properties to be set
     */
    private void setUpTransformerContext(Map<String, Object> props)
    {
        context = EasyMock.createMock(TransformerContext.class);
        resourceManager = EasyMock.createMock(ResourceManager.class);
        EasyMock.expect(context.getLocale()).andStubReturn(LOCALE);
        EasyMock.expect(context.getResourceManager()).andStubReturn(
                resourceManager);
        EasyMock.expect(context.properties()).andStubReturn(
                (props != null) ? props : new HashMap<String, Object>());
    }

    /**
     * Calls replay() on the affected mock objects.
     */
    private void replayMocks()
    {
        EasyMock.replay(context, resourceManager);
    }

    /**
     * Verifies the mock objects.
     */
    private void verifyMocks()
    {
        EasyMock.verify(context, resourceManager);
    }

    /**
     * Tests properties of a newly created handler.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong default resource group",
                DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                handler.getDefaultResourceGroup());
        assertNull("Alternative groups are set", handler
                .getAlternativeResourceGroups());
    }

    /**
     * Tests modifying the default resource group.
     */
    @Test
    public void testSetDefaultResourceGroup()
    {
        handler.setDefaultResourceGroup(RES_GRP);
        assertEquals("Default group was not changed", RES_GRP, handler
                .getDefaultResourceGroup());
        handler.setDefaultResourceGroup(null);
        assertEquals("Default group was not reset",
                DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                handler.getDefaultResourceGroup());
    }

    /**
     * Tests obtaining a validation message when it is defined in the properties
     * of the context.
     */
    @Test
    public void testGetValidationMessageFromContext()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        assertEquals("Wrong message key", KEY, msg.getKey());
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation from the context's properties when
     * parameters are involved.
     */
    @Test
    public void testGetValidationMessageFromContextWithParams()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY,
                PARAM);
        assertEquals("Wrong message key", KEY, msg.getKey());
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation messages that must be loaded from resources.
     */
    @Test
    public void testGetValidationMessageFromResources()
    {
        setUpTransformerContext(null);
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andReturn(PROCESSED_MESSAGE);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        assertEquals("Wrong message key", KEY, msg.getKey());
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation message from resources when parameters are
     * involved.
     */
    @Test
    public void testGetValidationMessageFromResourcesWithParams()
    {
        setUpTransformerContext(null);
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andReturn(MESSAGE);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY,
                PARAM);
        assertEquals("Wrong message key", KEY, msg.getKey());
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        verifyMocks();
    }

    /**
     * Tests whether changing the default resource group affects resolving
     * messages from resources.
     */
    @Test
    public void testGetValidationMessageFromResourcesDifferentDefaultGroup()
    {
        setUpTransformerContext(null);
        final String defResGroup = "myDefResGroup";
        handler.setDefaultResourceGroup(defResGroup);
        EasyMock.expect(resourceManager.getText(LOCALE, defResGroup, KEY))
                .andReturn(PROCESSED_MESSAGE);
        replayMocks();
        assertEquals("Wrong message text", PROCESSED_MESSAGE, handler
                .getValidationMessage(context, KEY).getMessage());
        verifyMocks();
    }

    /**
     * Tests whether a validation message obtained from resources caches its
     * text on first access.
     */
    @Test
    public void testGetValidationMessageFromResourcesCached()
    {
        setUpTransformerContext(null);
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andReturn(PROCESSED_MESSAGE);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        assertEquals("Wrong message on 2nd access", PROCESSED_MESSAGE, msg
                .getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation message from the resources that cannot be
     * resolved.
     */
    @Test
    public void testGetValidationMessageFromResourcesNotFound()
    {
        setUpTransformerContext(null);
        MissingResourceException mrex = new MissingResourceException(
                "Not found", "test", KEY);
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andThrow(mrex);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        try
        {
            msg.getMessage();
            fail("No exception thrown on access!");
        }
        catch (MissingResourceException ex)
        {
            assertEquals("Wrong exception", mrex, ex);
        }
    }

    /**
     * Helper method for testing whether the correct message level is set.
     *
     * @param level the value for the level property
     * @param expectedLevel the expected level
     */
    private void checkValidationMessageLevel(Object level,
            ValidationMessageLevel expectedLevel)
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        if (level != null)
        {
            props
                    .put(DefaultValidationMessageHandler.PROP_MESSAGE_LEVEL,
                            level);
        }
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        assertEquals("Wrong level", expectedLevel, msg.getLevel());
        verifyMocks();
    }

    /**
     * Tests whether the message level is set to default if not specified
     * otherwise.
     */
    @Test
    public void testGetValidationMessageLevelDefault()
    {
        checkValidationMessageLevel(null, ValidationMessageLevel.ERROR);
    }

    /**
     * Tests whether an enumeration literal can be used directly as value for
     * the level property.
     */
    @Test
    public void testGetValidationMessageLevelPropertyLiteral()
    {
        checkValidationMessageLevel(ValidationMessageLevel.WARNING,
                ValidationMessageLevel.WARNING);
    }

    /**
     * Tests whether the validation level can be obtained from a property.
     */
    @Test
    public void testGetValidationMessageLevelProperty()
    {
        checkValidationMessageLevel(ValidationMessageLevel.WARNING.name(),
                ValidationMessageLevel.WARNING);
    }

    /**
     * Tests whether the property values for the level property are case
     * insensitive.
     */
    @Test
    public void testGetValidationMessageLevelPropertyCaseInsensitive()
    {
        checkValidationMessageLevel(ValidationMessageLevel.WARNING.name()
                .toLowerCase(Locale.ENGLISH), ValidationMessageLevel.WARNING);
    }

    /**
     * Tests whether an invalid level property is detected. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetValidationMessageLevelPropertyInvalid()
    {
        checkValidationMessageLevel("error or warning", null);
    }

    /**
     * Tests the equals() implementation of the validation messages if the
     * expected result is true.
     */
    @Test
    public void testEqualsValidationMessageTrue()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage m1 = handler.getValidationMessage(context, KEY);
        JGuiraffeTestHelper.checkEquals(m1, m1, true);
        ValidationMessage m2 = handler.getValidationMessage(context, KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, true);
        props.put(KEY, "another message");
        m2 = handler.getValidationMessage(context, KEY);
        assertNotSame("Same message returned", m1, m2);
        JGuiraffeTestHelper.checkEquals(m1, m2, true);
        verifyMocks();
    }

    /**
     * Tests the equals() implementation of the validation messages if the
     * expected result is false.
     */
    @Test
    public void testEqualsValidationMessageFalse()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage m1 = handler.getValidationMessage(context, KEY);
        final String key2 = KEY + "2";
        props.put(key2, PROCESSED_MESSAGE);
        ValidationMessage m2 = handler.getValidationMessage(context, key2);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        props.put(DefaultValidationMessageHandler.PROP_MESSAGE_LEVEL,
                ValidationMessageLevel.WARNING);
        m2 = handler.getValidationMessage(context, KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        verifyMocks();
    }

    /**
     * Tests the equals() implementation of the validation messages with other
     * objects.
     */
    @Test
    public void testEqualsValidationMessageOtherObjects()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage m1 = handler.getValidationMessage(context, KEY);
        JGuiraffeTestHelper.checkEquals(m1, null, false);
        JGuiraffeTestHelper.checkEquals(m1, this, false);
        verifyMocks();
    }

    /**
     * Tests the string representation of a validation message.
     */
    @Test
    public void testToStringValidationMessage()
    {
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(KEY, PROCESSED_MESSAGE);
        setUpTransformerContext(props);
        replayMocks();
        ValidationMessage m = handler.getValidationMessage(context, KEY);
        String s = m.toString();
        assertEquals("Wrong string for message", KEY + ": \""
                + PROCESSED_MESSAGE + '"', s);
        verifyMocks();
    }

    /**
     * Tests obtaining a validation message when alternative resource groups are
     * involved, but the message is only found in the default group.
     */
    @Test
    public void testGetValidationMessageAlternativeResGroupNotFound()
    {
        final int count = 4;
        StringBuilder buf = new StringBuilder(RES_GRP);
        buf.append(0);
        for (int i = 1; i < count; i++)
        {
            for (int j = 1; j < i; j++)
            {
                buf.append(' ');
            }
            buf.append(',').append(RES_GRP).append(i);
        }
        handler.setAlternativeResourceGroups(buf.toString());
        setUpTransformerContext(null);
        for (int i = 0; i < count; i++)
        {
            EasyMock.expect(resourceManager.getText(LOCALE, RES_GRP + i, KEY))
                    .andThrow(
                            new MissingResourceException(RES_GRP + i, RES_GRP,
                                    KEY));
        }
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andReturn(PROCESSED_MESSAGE);
        replayMocks();
        assertEquals("Wrong message", PROCESSED_MESSAGE, handler
                .getValidationMessage(context, KEY).getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation message from resources when a blanc string
     * was passed to the alternative resources.
     */
    @Test
    public void testGetValidationMessageFromResourcesAlternativeResGroupsBlanc()
    {
        setUpTransformerContext(null);
        handler.setAlternativeResourceGroups("");
        EasyMock
                .expect(
                        resourceManager
                                .getText(
                                        LOCALE,
                                        DefaultValidationMessageHandler.DEFAULT_RESOURCE_GROUP_NAME,
                                        KEY)).andReturn(PROCESSED_MESSAGE);
        replayMocks();
        ValidationMessage msg = handler.getValidationMessage(context, KEY);
        assertEquals("Wrong message text", PROCESSED_MESSAGE, msg.getMessage());
        verifyMocks();
    }

    /**
     * Tests obtaining a validation message when it can be found in an
     * alternative resource group.
     */
    @Test
    public void testGetValidationMessageAlternativeResGroupFound()
    {
        handler.setAlternativeResourceGroups(RES_GRP);
        setUpTransformerContext(null);
        EasyMock.expect(resourceManager.getText(LOCALE, RES_GRP, KEY))
                .andReturn(PROCESSED_MESSAGE);
        replayMocks();
        assertEquals("Wrong message", PROCESSED_MESSAGE, handler
                .getValidationMessage(context, KEY).getMessage());
        verifyMocks();
    }

    /**
     * Tests passing a null context to getValidationMessage(). This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetValidationMessageNullContext()
    {
        handler.getValidationMessage(null, KEY);
    }

    /**
     * Tests passing a null key to getValidationMessage(). This should cause an
     * exception.
     */
    @Test
    public void testGetValidationMessageNullKey()
    {
        setUpTransformerContext(null);
        replayMocks();
        try
        {
            handler.getValidationMessage(context, null);
            fail("Null key not detected!");
        }
        catch (IllegalArgumentException iex)
        {
            verifyMocks();
        }
    }
}
