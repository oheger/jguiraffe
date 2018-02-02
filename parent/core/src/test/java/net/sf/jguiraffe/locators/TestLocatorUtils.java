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
package net.sf.jguiraffe.locators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test class for LocatorUtils.
 *
 * @author Oliver Heger
 * @version $Id: TestLocatorUtils.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestLocatorUtils
{
    /** Constant for a test URL. */
    private static final String TEST_URL = "http://www.testurl.com/test/resource.html";

    /** Constant for a test resource file. */
    private static final String TEST_RESOURCE = "icon.gif";

    /** Text contained in the test file. */
    private static final String TEST_FILE_CONTENT = "Hello World at"
            + new Date();

    private static URL resourceURL;

    /** A helper object for creating temporary files. */
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        resourceURL = TestLocatorUtils.class.getResource("/" + TEST_RESOURCE);
    }

    /**
     * Creates a test file and writes some content into it.
     *
     * @return the newly created test file
     * @throws Exception if an error occurs
     */
    private File createTempFile() throws IOException
    {
        File f = folder.newFile();
        PrintWriter out = new PrintWriter(new FileWriter(f));
        out.print(TEST_FILE_CONTENT);
        out.close();
        return f;
    }

    /**
     * Tests transforming a file to an URL.
     */
    @Test
    public void testFileURL() throws IOException
    {
        File f = createTempFile();
        URL url = LocatorUtils.fileURL(f);
        assertEquals("Wrong protocol", "file", url.getProtocol());
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        InputStream in = con.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int c;
        while ((c = in.read()) >= 0)
        {
            bos.write(c);
        }
        in.close();
        assertEquals("Wrong content", TEST_FILE_CONTENT, bos.toString());
    }

    /**
     * Tests transforming a file to an URL when an exception is thrown. This
     * should be re-thrown as a checked exception.
     */
    @Test(expected = LocatorException.class)
    public void testFileURLEx()
    {
        @SuppressWarnings("serial")
        File f = new File("testFile")
        {
            /**
             * Returns an URI with an unknown protocol, which will cause a
             * MalformedURLException later on.
             */
            @Override
            public URI toURI()
            {
                try
                {
                    return new URI("unknown://test/file.tst");
                }
                catch (URISyntaxException uex)
                {
                    fail("Invalid URI syntax!");
                    return null;
                }
            }
        };
        LocatorUtils.fileURL(f);
    }

    /**
     * Tests fileURL() with a null input.
     */
    @Test
    public void testFileURLNull()
    {
        assertNull("Wrong result for null file", LocatorUtils.fileURL(null));
    }

    /**
     * Tests locateURL() with a null input.
     */
    @Test
    public void testLocateURLNull()
    {
        assertNull("Wrong result for null input", LocatorUtils.locateURL(null));
    }

    /**
     * Tests locateURL() when a full URL is passed in.
     */
    @Test
    public void testLocateURLURL() throws MalformedURLException
    {
        URL url = new URL(TEST_URL);
        assertEquals("Wrong result for URL", url.toExternalForm(), LocatorUtils
                .locateURL(TEST_URL).toExternalForm());
    }

    /**
     * Tests locateURL() when a file name is passed in.
     */
    @Test
    public void testLocateURLFileName() throws IOException
    {
        File f = createTempFile();
        assertEquals("Wrong URL for file name", LocatorUtils.fileURL(f),
                LocatorUtils.locateURL(f.getAbsolutePath()));
    }

    /**
     * Tests the locateURL() method for a non existing resource.
     */
    public void testLocateURLNonExisting()
    {
        assertNull("Wrong result for non existing resource", LocatorUtils
                .locateURL("non existing url or file"));
    }

    /**
     * Tests locateResource() for null input.
     */
    @Test
    public void testLocateResourceNull()
    {
        assertNull("Wrong result for null input", LocatorUtils
                .locateResource(null));
    }

    /**
     * Tests locating a non existing resource.
     */
    @Test
    public void testLocateResourceNonExisting()
    {
        assertNull("Found non existing resource", LocatorUtils
                .locateResource("non existing resource"));
    }

    /**
     * Tests whether locateResource() queries the context class loader.
     */
    @Test
    public void testLocateResourceContextCL() throws MalformedURLException
    {
        ClassLoader oldCtx = Thread.currentThread().getContextClassLoader();
        final URL resURL = new URL(TEST_URL);
        ClassLoader newCtx = new ClassLoader()
        {
            @Override
            public URL getResource(String name)
            {
                return TEST_RESOURCE.equals(name) ? resURL : null;
            }
        };
        Thread.currentThread().setContextClassLoader(newCtx);
        try
        {
            assertEquals("Wrong URL", resURL.toExternalForm(), LocatorUtils
                    .locateResource(TEST_RESOURCE).toExternalForm());
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtx);
        }
    }

    /**
     * Tests the rare case that the context CL is null.
     */
    @Test
    public void testLocateResourceContextCLNull()
    {
        ClassLoader oldCtx = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(null);
            assertEquals("Wrong URL", resourceURL, LocatorUtils
                    .locateResource(TEST_RESOURCE));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtx);
        }
    }

    /**
     * Tests a normal invocation of the locateResource() method.
     */
    @Test
    public void testLocateResourceNormal() throws Exception
    {
        assertEquals("Wrong URL", resourceURL, LocatorUtils
                .locateResource(TEST_RESOURCE));
    }

    /**
     * Tests locateResource() for a resource that already has a leading slash.
     */
    @Test
    public void testLocateResourceSlash()
    {
        assertEquals("Wrong URL", resourceURL, LocatorUtils
                .locateResource("/" + TEST_RESOURCE));
    }

    /**
     * Tests whether a resource can be located if a class loader is provided.
     */
    @Test
    public void testLocateResourceWithCL()
    {
        ClassLoader oldCtx = Thread.currentThread().getContextClassLoader();
        try
        {
            ClassLoader mockLoader = EasyMock.createMock(ClassLoader.class);
            EasyMock.replay(mockLoader);
            Thread.currentThread().setContextClassLoader(mockLoader);
            assertEquals("Wrong URL", resourceURL,
                    LocatorUtils.locateResource(TEST_RESOURCE, getClass()
                            .getClassLoader()));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtx);
        }
    }

    /**
     * Tests locate() for null input.
     */
    @Test
    public void testLocateNull()
    {
        assertNull("Wrong result for null input", LocatorUtils.locate(null,
                null));
    }

    /**
     * Tests locate() when only a non existing resource is specified.
     */
    @Test
    public void testLocateNonExistingResource()
    {
        assertNull("Wrong result for non existing resource", LocatorUtils
                .locate(null, "non existing resource"));
    }

    /**
     * Tests locate() when only a non existing URL is specified.
     */
    @Test
    public void testLocateNonExistingURL()
    {
        assertNull("Wrong result for non existing URL", LocatorUtils.locate(
                "non existing url", null));
    }

    /**
     * Tests locate() when only invalid parameters are passed.
     */
    @Test
    public void testLocateNonExisting()
    {
        assertNull("Wrong result for non existing parameters", LocatorUtils
                .locate("non existing url", "non existing resource"));
    }

    /**
     * Tests the locate() method for valid URLs. Ensures that the resource
     * parameter does not impact the URL resolution.
     *
     * @param url the URL
     * @param expected the expected result
     */
    private void checkLocateValidURL(String url, URL expected)
    {
        assertEquals("Wrong result for null resource", expected
                .toExternalForm(), LocatorUtils.locate(url, null)
                .toExternalForm());
        assertEquals("Wrong result for non existing resource", expected
                .toExternalForm(), LocatorUtils.locate(url,
                "non existing resource").toExternalForm());
        assertEquals("Wrong result for valid resource", expected
                .toExternalForm(), LocatorUtils.locate(url, TEST_RESOURCE)
                .toExternalForm());
    }

    /**
     * Tests locate() when a valid URL is passed in.
     */
    @Test
    public void testLocateValidURL() throws IOException
    {
        File f = createTempFile();
        checkLocateValidURL(TEST_URL, new URL(TEST_URL));
        URL fileURL = LocatorUtils.fileURL(f);
        checkLocateValidURL(fileURL.toString(), fileURL);
        checkLocateValidURL(f.getAbsolutePath(), fileURL);
    }

    /**
     * Tests locate() when a valid resource name is passed in.
     */
    @Test
    public void testLocateValidResource()
    {
        URL resURL = resourceURL;
        assertEquals("Wrong result for null URL", resURL, LocatorUtils.locate(
                null, TEST_RESOURCE));
        assertEquals("Wrong result for non existing URL", resURL, LocatorUtils
                .locate("non existing URL", TEST_RESOURCE));
    }

    /**
     * Tests whether the class loader is taken into account by locate().
     */
    @Test
    public void testLocateWithCL()
    {
        ClassLoader oldCtxCL = Thread.currentThread().getContextClassLoader();
        try
        {
            ClassLoader clMock = EasyMock.createMock(ClassLoader.class);
            EasyMock.replay(clMock);
            Thread.currentThread().setContextClassLoader(clMock);
            assertEquals("Wrong result", resourceURL,
                    LocatorUtils.locate(null, TEST_RESOURCE, getClass()
                            .getClassLoader()));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtxCL);
        }
    }

    /**
     * Tests locateEx() when a valid parameter is passed in.
     */
    @Test
    public void testLocateExValid() throws IOException
    {
        assertEquals("Wrong result for valid URL", new URL(TEST_URL)
                .toExternalForm(), LocatorUtils.locateEx(TEST_URL, null)
                .toExternalForm());
    }

    /**
     * Tests locateEx() when invalid parameters are passed in. This should cause
     * an exception.
     */
    @Test(expected = LocatorException.class)
    public void testLocateExInvalid()
    {
        LocatorUtils.locateEx(null, null);
    }

    /**
     * Tests whether the class loader is taken into account by locateEx().
     */
    @Test
    public void testLocateExWithCL()
    {
        ClassLoader oldCtxCL = Thread.currentThread().getContextClassLoader();
        try
        {
            ClassLoader clMock = EasyMock.createMock(ClassLoader.class);
            EasyMock.replay(clMock);
            Thread.currentThread().setContextClassLoader(clMock);
            assertEquals("Wrong result", resourceURL,
                    LocatorUtils.locateEx(null, TEST_RESOURCE, getClass()
                            .getClassLoader()));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtxCL);
        }
    }

    /**
     * Tests the openStream() method when a null locator is provided. This
     * should cause an exception.
     */
    @Test(expected = LocatorException.class)
    public void testOpenStreamNull() throws IOException
    {
        LocatorUtils.openStream(null);
    }

    /**
     * Tests the openStream() method with a locator that always returns null.
     * This should cause an exception.
     */
    @Test
    public void testOpenStreamInvalidLocator() throws IOException
    {
        Locator mockLoc = EasyMock.createMock(Locator.class);
        EasyMock.expect(mockLoc.getInputStream()).andReturn(null);
        EasyMock.expect(mockLoc.getFile()).andReturn(null);
        EasyMock.expect(mockLoc.getURL()).andReturn(null);
        EasyMock.replay(mockLoc);
        try
        {
            LocatorUtils.openStream(mockLoc);
            fail("Could process invalid locator!");
        }
        catch (LocatorException lex)
        {
            EasyMock.verify(mockLoc);
        }
    }

    /**
     * Tests opening a stream when the locator only returns an URL.
     */
    @Test
    public void testOpenStreamFromURL() throws IOException
    {
        File f = createTempFile();
        Locator mockLoc = EasyMock.createMock(Locator.class);
        EasyMock.expect(mockLoc.getInputStream()).andReturn(null);
        EasyMock.expect(mockLoc.getFile()).andReturn(null);
        EasyMock.expect(mockLoc.getURL()).andReturn(
                LocatorUtils.fileURL(f));
        EasyMock.replay(mockLoc);
        checkStream(LocatorUtils.openStream(mockLoc));
        EasyMock.verify(mockLoc);
    }

    /**
     * Tests opening a stream when the locator returns a File.
     */
    @Test
    public void testOpenStreamFromFile() throws IOException
    {
        File f = createTempFile();
        Locator mockLoc = EasyMock.createMock(Locator.class);
        EasyMock.expect(mockLoc.getInputStream()).andReturn(null);
        EasyMock.expect(mockLoc.getFile()).andReturn(f);
        EasyMock.replay(mockLoc);
        checkStream(LocatorUtils.openStream(mockLoc));
        EasyMock.verify(mockLoc);
    }

    /**
     * Tests opening a stream when the locator directly returns a stream.
     */
    @Test
    public void testOpenSreamFromStream() throws IOException
    {
        File f = createTempFile();
        Locator mockLoc = EasyMock.createMock(Locator.class);
        EasyMock.expect(mockLoc.getInputStream()).andReturn(
                new FileInputStream(f));
        EasyMock.replay(mockLoc);
        checkStream(LocatorUtils.openStream(mockLoc));
        EasyMock.verify(mockLoc);
    }

    /**
     * Helper method for checking the content of the stream obtained from a
     * locator.
     *
     * @param in the stream
     * @throws IOException if an error occurs
     */
    private void checkStream(InputStream in) throws IOException
    {
        BufferedReader bin = new BufferedReader(new InputStreamReader(in));
        String content = bin.readLine();
        bin.close();
        assertEquals("Wrong content in stream", TEST_FILE_CONTENT, content);
    }

    /**
     * Tests whether the locator string starts with the expected prefix.
     *
     * @param locator the locator
     * @param s the locator string
     * @return the string shortened by the prefix
     */
    private String checkLocatorStringPrefix(Locator locator, String s)
    {
        assertTrue("No qualified class name: " + s, s.startsWith(locator
                .getClass().getName()));
        s = s.substring(locator.getClass().getName().length());
        assertEquals("No @: " + s, '@', s.charAt(0));
        s = s.substring(1);
        String hashCode = String.valueOf(System.identityHashCode(locator));
        assertTrue("No hash code: " + s, s.startsWith(hashCode));
        s = s.substring(hashCode.length());
        return s;
    }

    /**
     * Tests the locatorToString() method.
     */
    @Test
    public void testLocatorToString()
    {
        Locator locator = ClassPathLocator.getInstance(TEST_RESOURCE);
        final String data = "The data of this locator!";
        String s = checkLocatorStringPrefix(locator, LocatorUtils
                .locatorToString(locator, data));
        assertEquals("No data: " + s, "[ " + data + " ]", s);
    }

    /**
     * Tests locatorToString() when no data is specified.
     */
    @Test
    public void testLocatorToStringNoData()
    {
        Locator locator = ClassPathLocator.getInstance(TEST_RESOURCE);
        String s = checkLocatorStringPrefix(locator, LocatorUtils
                .locatorToString(locator, null));
        assertEquals("Wrong data: " + s, "[  ]", s);
    }

    /**
     * Tests locatorToString() when no locator is specified. This should cause
     * an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocatorToStringNullLocator()
    {
        LocatorUtils.locatorToString(null, "locatorData");
    }

    /**
     * Tests obtaining the data string for a locator.
     */
    @Test
    public void testLocatorToDataString()
    {
        Locator locator = ClassPathLocator.getInstance(TEST_RESOURCE);
        String s = LocatorUtils.locatorToDataString(locator);
        assertEquals("Wrong data string: " + s,
                "ClassPathLocator[ resourceName = " + TEST_RESOURCE + " ]",
                LocatorUtils.locatorToDataString(locator));
    }

    /**
     * Tests transforming a locator string to a data string when no qualified
     * class name is contained.
     */
    @Test
    public void testLocatorToDataStringUnqualified()
    {
        final String locStr = "ClassPathLocator@123[ myData ]";
        assertEquals("Wrong data string", "ClassPathLocator[ myData ]",
                LocatorUtils.locatorToDataString(locStr));
    }

    /**
     * Tests a minimum valid locator string.
     */
    @Test
    public void testLocatorToDataStringMinimum()
    {
        final String locStr = ".@[";
        assertEquals("Wrong data string", "[", LocatorUtils
                .locatorToDataString(locStr));
    }

    /**
     * Tests locatorToDataString() when a null locator is passed in. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocatorToDataStringNullLocator()
    {
        LocatorUtils.locatorToDataString((Locator) null);
    }

    /**
     * Tests locatorToDataString() when an invalid data string is passed in.
     * This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocatorToDataStringInvalid()
    {
        LocatorUtils.locatorToDataString(ClassPathLocator.class.getName()
                + "@irgendwas");
    }

    /**
     * Tests locatorToDataString() when a null string is passed in. This should
     * cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLocatorToDataStringNullString()
    {
        LocatorUtils.locatorToDataString((String) null);
    }
}
