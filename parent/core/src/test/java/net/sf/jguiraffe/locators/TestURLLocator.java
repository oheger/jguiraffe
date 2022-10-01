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
package net.sf.jguiraffe.locators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for URLLocator.
 *
 * @author Oliver Heger
 * @version $Id: TestURLLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestURLLocator
{
    /** Constant for the test URL as string. */
    private static final String URL_STRING = "http://jguiraffe.sourceforge.net/";

    /** The test URL. */
    private static URL testUrl;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        testUrl = new URL(URL_STRING);
    }

    /**
     * Tests creating an instance with a null URL. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullURL()
    {
        URLLocator.getInstance((URL) null);
    }

    /**
     * Tests creating an instance with a null string. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullString()
    {
        URLLocator.getInstance((String) null);
    }

    /**
     * Tests creating an instance from an invalid URL string. This should cause
     * an exception.
     */
    @Test(expected = LocatorException.class)
    public void testGetInstanceInvalidString()
    {
        URLLocator.getInstance("unknownProtokoll://invalid.url");
    }

    /**
     * Tests the getURL() method for an instance created from a URL.
     */
    @Test
    public void testGetURLFromURL()
    {
        URLLocator loc = URLLocator.getInstance(testUrl);
        assertEquals("Wrong URL", testUrl, loc.getURL());
    }

    /**
     * Tests the getURL() method for an instance created from a string.
     */
    @Test
    public void testGetURLFromString()
    {
        URLLocator loc = URLLocator.getInstance(URL_STRING);
        assertEquals("Wrong URL", testUrl, loc.getURL());
    }

    /**
     * Tests querying the file. Result should be null.
     */
    @Test
    public void testGetFile()
    {
        URLLocator loc = URLLocator.getInstance(testUrl);
        assertNull("Got a file", loc.getFile());
    }

    /**
     * Tests querying the input stream. Result should be null.
     */
    @Test
    public void testGetInputStream() throws IOException
    {
        URLLocator loc = URLLocator.getInstance(testUrl);
        assertNull("Got a stream", loc.getInputStream());
    }

    /**
     * Tests basic invocations of the equals() method.
     */
    @Test
    public void testTrivialEquals()
    {
        JGuiraffeTestHelper.testTrivialEquals(URLLocator.getInstance(testUrl));
    }

    /**
     * Tests equals() for equal instances.
     */
    @Test
    public void testEqualsTrue()
    {
        URLLocator loc1 = URLLocator.getInstance(testUrl);
        JGuiraffeTestHelper.checkEquals(loc1, loc1, true);
        URLLocator loc2 = URLLocator.getInstance(URL_STRING);
        JGuiraffeTestHelper.checkEquals(loc1, loc2, true);
    }

    /**
     * Tests equals() for non-equal instances.
     */
    @Test
    public void testEqualsFalse()
    {
        URLLocator loc1 = URLLocator.getInstance(testUrl);
        URLLocator loc2 = URLLocator
                .getInstance("http://sourceforge.net/projects/jguiraffe/");
        JGuiraffeTestHelper.checkEquals(loc1, loc2, false);
    }

    /**
     * Tests the string representation. We check whether the string contains at
     * least the URL.
     */
    @Test
    public void testToString()
    {
        URLLocator loc = URLLocator.getInstance(testUrl);
        String s = loc.toString();
        assertTrue("URL not found: " + s, s.indexOf(URL_STRING) > 0);
    }
}
