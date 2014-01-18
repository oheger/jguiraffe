/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for LocatorWrapper.
 *
 * @author Oliver Heger
 * @version $Id: TestLocatorWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestLocatorWrapper
{
    /** The mock for the underlying locator. */
    private Locator locator;

    /** The locator to be tested. */
    private LocatorWrapper wrapper;

    @Before
    public void setUp() throws Exception
    {
        locator = EasyMock.createMock(Locator.class);
    }

    /**
     * Returns the locator wrapper to be tested. It is created on demand.
     *
     * @return the wrapper to be tested
     */
    private LocatorWrapper getWrapper()
    {
        if (wrapper == null)
        {
            wrapper = new LocatorWrapper(locator);
        }
        return wrapper;
    }

    /**
     * Tests creating a new instance.
     */
    @Test
    public void testInit()
    {
        EasyMock.replay(locator);
        assertEquals("Wrong wrapped locator", locator, getWrapper()
                .getWrappedLocator());
    }

    /**
     * Tests creating an instance without a wrapped locator. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNull()
    {
        new LocatorWrapper(null);
    }

    /**
     * Tests the getFile() implementation.
     */
    @Test
    public void testGetFile()
    {
        final File f = new File("TestFile");
        EasyMock.expect(locator.getFile()).andReturn(f);
        EasyMock.replay(locator);
        assertEquals("Wrong file", f, getWrapper().getFile());
        EasyMock.verify(locator);
    }

    /**
     * Tests the getInputStream() implementation.
     */
    @Test
    public void testGetInputStream() throws IOException
    {
        final InputStream in = new ByteArrayInputStream("test".getBytes());
        EasyMock.expect(locator.getInputStream()).andReturn(in);
        EasyMock.replay(locator);
        assertEquals("Wrong input stream", in, getWrapper().getInputStream());
        EasyMock.verify(locator);
    }

    /**
     * Tests the getURL() implementation.
     */
    @Test
    public void testGetURL() throws MalformedURLException
    {
        final URL url = new URL("http://jguiraffe.sf.net");
        EasyMock.expect(locator.getURL()).andReturn(url);
        EasyMock.replay(locator);
        assertEquals("Wrong URL", url, getWrapper().getURL());
        EasyMock.verify(locator);
    }
}
