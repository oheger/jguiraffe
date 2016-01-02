/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Locale;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;

import org.junit.Test;

/**
 * Test class for Message.
 *
 * @author Oliver Heger
 * @version $Id: TestMessage.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMessage
{
    /** Constant for a resource group. */
    private static final Object RES_GRP = "testmsgresources";

    /** Constant for a resource key. */
    private static final String RES_KEY = "testKey";

    /** Stores a reference to the resource manager. */
    private ResourceManager resman;

    /**
     * Returns the resource manager for resolving messages. It is created on
     * first access.
     *
     * @return the resource manager
     */
    private ResourceManager getResourceManager()
    {
        if (resman == null)
        {
            resman = new ResourceManagerImpl(new BundleResourceLoader());
            resman.setDefaultResourceGroup(RES_GRP);
        }
        return resman;
    }

    /**
     * Helper method for testing the parameters of a Message.
     *
     * @param msg the message instance
     * @param expectedParams the expected parameters
     */
    private static void checkParameters(Message msg, Object... expectedParams)
    {
        assertTrue("Wrong parameters", Arrays.equals(msg.getParameters(),
                expectedParams));
    }

    /**
     * Helper method for testing the content of a Message object.
     *
     * @param msg the Message to be tested
     * @param resGrp the expected resource group
     * @param resKey the expected resource key
     * @param expectedParams the expected parameters
     */
    private static void checkMessage(Message msg, Object resGrp, Object resKey,
            Object... expectedParams)
    {
        assertEquals("Wrong resource group", resGrp, msg.getResourceGroup());
        assertEquals("Wrong resource key", resKey, msg.getResourceKey());
        checkParameters(msg, expectedParams);
    }

    /**
     * Tests the constructor that only takes a resource key.
     */
    @Test
    public void testInitResKey()
    {
        Message msg = new Message(RES_KEY);
        checkMessage(msg, null, RES_KEY);
    }

    /**
     * Tests the constructor that takes the resource group and the resource key.
     */
    @Test
    public void testInitResKeyAndGroup()
    {
        Message msg = new Message(RES_GRP, RES_KEY);
        checkMessage(msg, RES_GRP, RES_KEY);
    }

    /**
     * Tests the constructor that takes a single parameter object.
     */
    @Test
    public void testInitSingleParameter()
    {
        Message msg = new Message(RES_GRP, RES_KEY, 1);
        checkMessage(msg, RES_GRP, RES_KEY, 1);
    }

    /**
     * Tests the constructor that takes exactly two parameters.
     */
    @Test
    public void testInitTwoParameters()
    {
        Message msg = new Message(RES_GRP, RES_KEY, 1, 42);
        checkMessage(msg, RES_GRP, RES_KEY, 1, 42);
    }

    /**
     * Tests whether more than 2 parameters can be passed to the constructor.
     */
    @Test
    public void testInitMultipleParameters()
    {
        Message msg = new Message(RES_GRP, RES_KEY, 1, 2, 3, 4, 5, "test", 100);
        checkMessage(msg, RES_GRP, RES_KEY, 1, 2, 3, 4, 5, "test", 100);
    }

    /**
     * Tests the constructor when null is passed for the var args parameter.
     */
    @Test
    public void testInitMultipleParametersNull()
    {
        Object[] params = null;
        Message msg = new Message(RES_GRP, RES_KEY, 1, 2, params);
        checkMessage(msg, RES_GRP, RES_KEY, 1, 2);
    }

    /**
     * Tries to create an instance without a resource key. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoResourceKey()
    {
        new Message(RES_GRP, null, 1, 2, 3);
    }

    /**
     * Tests whether arbitrary parameters can be passed to the static factory
     * method.
     */
    @Test
    public void testCreateWithParametersMultiple()
    {
        Message msg = Message
                .createWithParameters(RES_GRP, RES_KEY, 1, 2, 3, 4);
        checkMessage(msg, RES_GRP, RES_KEY, 1, 2, 3, 4);
    }

    /**
     * Tests the static factory method for exactly 2 parameters.
     */
    @Test
    public void testCreateWithParametersTwo()
    {
        Message msg = Message.createWithParameters(RES_GRP, RES_KEY, 1, 2);
        checkMessage(msg, RES_GRP, RES_KEY, 1, 2);
    }

    /**
     * Tests the static factory method if a single parameter is passed.
     */
    @Test
    public void testCreateWithParametersSingle()
    {
        Message msg = Message.createWithParameters(RES_GRP, RES_KEY, "test");
        checkMessage(msg, RES_GRP, RES_KEY, "test");
    }

    /**
     * Tests the static factory method if no parameter are passed.
     */
    @Test
    public void testCreateWithParametersNone()
    {
        Message msg = Message.createWithParameters(null, RES_KEY);
        checkMessage(msg, null, RES_KEY);
    }

    /**
     * Tests whether a null parameters array can be handled.
     */
    @Test
    public void testCreateWithParametersNull()
    {
        Message msg = Message.createWithParameters(RES_GRP, RES_KEY,
                (Object[]) null);
        checkMessage(msg, RES_GRP, RES_KEY);
    }

    /**
     * Tests the static factory method if no resource key is specified. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithParametersNoResKey()
    {
        Message.createWithParameters(RES_GRP, null, 1);
    }

    /**
     * Tests whether a message from another one can be created.
     */
    @Test
    public void testCreateFromMessage()
    {
        Message m1 = new Message(RES_GRP, RES_KEY, 1, 2, 3);
        Message m2 = Message.createFromMessage(m1, 5, 6);
        checkMessage(m2, RES_GRP, RES_KEY, 5, 6);
    }

    /**
     * Tests the createFromMessage() method if a null message is passed in. This
     * should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateFromMessageNull()
    {
        Message.createFromMessage(null, 1);
    }

    /**
     * Tests whether a modification of the parameters array does not affect the
     * message instance.
     */
    @Test
    public void testGetParametersModify()
    {
        Message msg = new Message(null, RES_KEY, 1, 2, 3);
        Object[] params = msg.getParameters();
        params[1] = "test";
        checkMessage(msg, null, RES_KEY, 1, 2, 3);
    }

    /**
     * Tests to resolve messages.
     */
    @Test
    public void testResolve()
    {
        Message msg = new Message(RES_GRP, "QUESTION");
        assertEquals(
                "Was ist der Sinn des Lebens, des Universums und des ganzen Rests?",
                msg.resolve(getResourceManager(), Locale.GERMAN));
        assertEquals(
                "What is the meaning of life, the universe and everything?",
                msg.resolve(getResourceManager(), Locale.ENGLISH));

        msg = new Message(RES_GRP, "ANSWER", new Integer(42));
        assertEquals("Die Antwort ist 42.", msg.resolve(getResourceManager(),
                Locale.GERMAN));
        assertEquals("The answer is 42.", msg.resolve(getResourceManager(),
                Locale.ENGLISH));

        msg = new Message(RES_GRP, "PROMPT", "User", "Helloween");
        assertEquals("Hallo User, heute ist Helloween.", msg.resolve(
                getResourceManager(), Locale.GERMAN));
        assertEquals("Hello User, today is Helloween.", msg.resolve(
                getResourceManager(), Locale.ENGLISH));

        msg = new Message(RES_GRP, "MULTIPARAMS", "Satz", "vielen", "ziemlich",
                "verstehen");
        assertEquals(
                "Dies ist ein Satz mit vielen Parametern, der ziemlich schwer zu verstehen ist.",
                msg.resolve(getResourceManager(), Locale.GERMAN));
        msg = Message.createFromMessage(msg, new Object[] {
                "text", new Integer(4), "kind of", "resolve"
        });
        assertEquals(
                "This is a text with 4 parameters, that is kind of difficult to resolve.",
                msg.resolve(getResourceManager(), Locale.ENGLISH));
    }

    /**
     * Tests if the default resource group works.
     */
    @Test
    public void testDefaultResourceGroup()
    {
        Message msg = new Message("QUESTION");
        assertEquals(
                "Was ist der Sinn des Lebens, des Universums und des ganzen Rests?",
                msg.resolve(getResourceManager(), Locale.GERMAN));
        assertEquals(
                "What is the meaning of life, the universe and everything?",
                msg.resolve(getResourceManager(), Locale.ENGLISH));
    }

    /**
     * Tests the equals() method if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        Message m1 = new Message(RES_KEY);
        JGuiraffeTestHelper.checkEquals(m1, m1, true);
        Message m2 = new Message(RES_KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, true);
        m1 = new Message(RES_GRP, RES_KEY);
        m2 = new Message(RES_GRP, RES_KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, true);
        m1 = new Message(RES_GRP, RES_KEY, 1, 2, 3, 4);
        m2 = Message.createWithParameters(RES_GRP, RES_KEY, 1, 2, 3, 4);
        JGuiraffeTestHelper.checkEquals(m1, m2, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        Message m1 = new Message(RES_KEY);
        Message m2 = new Message(RES_KEY + "different");
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        m2 = new Message(RES_GRP, RES_KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        m1 = new Message(RES_GRP + "other", RES_KEY);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        m1 = new Message(RES_GRP, RES_KEY, 1);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        m2 = new Message(RES_GRP, RES_KEY, 2);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
        m2 = new Message(RES_GRP, RES_KEY, 1, 2);
        JGuiraffeTestHelper.checkEquals(m1, m2, false);
    }

    /**
     * Tests the equals() method with non Message objects.
     */
    @Test
    public void testEqualsWithOtherObjects()
    {
        Message msg = new Message(RES_GRP, "TEST", 42);
        JGuiraffeTestHelper.testTrivialEquals(msg);
    }

    /**
     * Tests the string representation of a message.
     */
    @Test
    public void testToString()
    {
        final Object[] params = {
                1, 25, "param", "test"
        };
        Message msg = Message.createWithParameters(RES_GRP, RES_KEY, params);
        String s = msg.toString();
        assertTrue("ResGRP not found: " + s,
                s.indexOf("group = " + RES_GRP) > 0);
        assertTrue("ResKey not found: " + s, s.indexOf("key = " + RES_KEY) > 0);
        assertTrue("No parameters section: " + s,
                s.indexOf("parameters = ") > 0);
        for (Object p : params)
        {
            assertTrue("Param " + p + " not found: " + s, s.indexOf(String
                    .valueOf(p)) > 0);
        }
    }

    /**
     * Tests toString() if only the resource key is specified.
     */
    @Test
    public void testToStringKeyOnly()
    {
        Message msg = new Message(RES_KEY);
        String s = msg.toString();
        assertFalse("Got parameters: " + s, s.indexOf("parameters = ") > 0);
        assertFalse("Got a group: + s", s.indexOf("group = ") > 0);
    }

    /**
     * Tests the resource manager if it was not fully initialized. This should
     * cause an exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testResolveResManUninitialized()
    {
        ResourceManager rm = getResourceManager();
        rm.setResourceLoader(null);
        new Message(RES_KEY).resolve(rm, Locale.GERMAN);
    }
}
