/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.locators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ClassPathLocator.
 *
 * @author Oliver Heger
 * @version $Id: TestClassPathLocator.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestClassPathLocator
{
    /** Constant for the resource name. */
    private static final String RESOURCE_NAME = "icon.gif";

    /** Constant for the expected URL. */
    private static final URL EXPECTED_URL = TestClassPathLocator.class
            .getResource("/" + RESOURCE_NAME);

    /** The locator to be tested. */
    private ClassPathLocator locator;

    @Before
    public void setUp() throws Exception
    {
        locator = ClassPathLocator.getInstance(RESOURCE_NAME);
    }

    /**
     * Tests obtaining an instance for a null resource name. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNull()
    {
        ClassPathLocator.getInstance(null);
    }

    /**
     * Tests the getInputStream() method.
     */
    @Test
    public void testGetInputStream() throws IOException
    {
        assertNull("Non null input stream returned", locator.getInputStream());
    }

    /**
     * Tests the getFile() method.
     */
    @Test
    public void testGetFile()
    {
        assertNull("Non null file returned", locator.getFile());
    }

    /**
     * Tests the getURL() method.
     */
    @Test
    public void testGetURL()
    {
        assertEquals("Wrong URL returned", EXPECTED_URL, locator.getURL());
    }

    /**
     * Tests whether a class loader is taken into account when doing the lookup.
     */
    @Test
    public void testGetURLWithCL()
    {
        ClassLoader oldCtxCL = Thread.currentThread().getContextClassLoader();
        try
        {
            ClassLoader clMock = EasyMock.createMock(ClassLoader.class);
            EasyMock.replay(clMock);
            Thread.currentThread().setContextClassLoader(clMock);
            locator =
                    ClassPathLocator.getInstance(RESOURCE_NAME,
                            LocatorUtils.class.getClassLoader());
            assertEquals("Wrong URL", EXPECTED_URL, locator.getURL());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtxCL);
        }
    }

    /**
     * Tests getURL() when the resource does not exist. This should cause an
     * exception.
     */
    @Test(expected = LocatorException.class)
    public void testGetURLInvalidResource()
    {
        locator = ClassPathLocator.getInstance("A non existing resource!");
        locator.getURL();
    }

    /**
     * Performs some trivial checks of the equals() implementation.
     */
    @Test
    public void testTrivialEquals()
    {
        JGuiraffeTestHelper.testTrivialEquals(locator);
    }

    /**
     * Tests equals() for equal objects.
     */
    @Test
    public void testEqualsTrue()
    {
        ClassPathLocator loc2 = ClassPathLocator.getInstance(RESOURCE_NAME);
        JGuiraffeTestHelper.checkEquals(locator, loc2, true);
        locator =
                ClassPathLocator.getInstance(RESOURCE_NAME, getClass()
                        .getClassLoader());
        loc2 =
                ClassPathLocator.getInstance(RESOURCE_NAME, getClass()
                        .getClassLoader());
        JGuiraffeTestHelper.checkEquals(locator, loc2, true);
    }

    /**
     * Tests equals() for objects that are not equal.
     */
    @Test
    public void testEqualsFalse()
    {
        ClassPathLocator loc2 = ClassPathLocator.getInstance("anotherResource");
        JGuiraffeTestHelper.checkEquals(locator, loc2, false);
        loc2 =
                ClassPathLocator.getInstance(RESOURCE_NAME, getClass()
                        .getClassLoader());
        JGuiraffeTestHelper.checkEquals(locator, loc2, false);
        locator =
                ClassPathLocator.getInstance(RESOURCE_NAME,
                        EasyMock.createMock(ClassLoader.class));
        JGuiraffeTestHelper.checkEquals(locator, loc2, false);
    }

    /**
     * Tests whether the string representation of a locator contains the
     * resource name.
     */
    @Test
    public void testToString()
    {
        String s = locator.toString();
        assertTrue("Resource name not found: " + s,
                s.indexOf(RESOURCE_NAME) > 0);
    }
}
