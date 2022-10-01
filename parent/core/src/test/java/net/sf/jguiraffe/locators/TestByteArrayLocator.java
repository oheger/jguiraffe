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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.Test;

/**
 * Test class for ByteArrayLocator. This class also tests functionality of the
 * base class.
 *
 * @author Oliver Heger
 * @version $Id: TestByteArrayLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestByteArrayLocator
{
    /** Constant with some test data. */
    private static final String TEST_DATA = "This is some test data for the locator!";

    /**
     * Helper method for testing the content of a stream obtained from the
     * locator.
     *
     * @param in the stream
     */
    private void checkStream(InputStream in)
    {
        ByteArrayOutputStream bos = JGuiraffeTestHelper.readStream(in, true);
        assertEquals("Wrong content of stream", TEST_DATA, bos.toString());
    }

    /**
     * Tests creating an instance with a null array. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullArray()
    {
        ByteArrayLocator.getInstance((byte[]) null);
    }

    /**
     * Tests creating an instance with a null string. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullString()
    {
        ByteArrayLocator.getInstance((String) null);
    }

    /**
     * Tests querying the file. This should be null.
     */
    @Test
    public void testGetFile()
    {
        ByteArrayLocator locator = ByteArrayLocator.getInstance(TEST_DATA);
        assertNull("Got a file", locator.getFile());
    }

    /**
     * Tests obtaining an input stream from a string.
     */
    @Test
    public void testGetInputStreamFromString() throws IOException
    {
        ByteArrayLocator locator = ByteArrayLocator.getInstance(TEST_DATA);
        checkStream(locator.getInputStream());
    }

    /**
     * Tests obtaining an input stream from a locator created from a byte array.
     */
    @Test
    public void testGetInputStreamFromByteArray() throws IOException
    {
        ByteArrayLocator locator = ByteArrayLocator.getInstance(TEST_DATA
                .getBytes());
        checkStream(locator.getInputStream());
    }

    /**
     * Tests whether multiple streams can be requested.
     */
    @Test
    public void testGetInputStreamMultiple() throws IOException
    {
        ByteArrayLocator locator = ByteArrayLocator.getInstance(TEST_DATA);
        InputStream is1 = locator.getInputStream();
        InputStream is2 = locator.getInputStream();
        assertNotSame("Same stream returned", is1, is2);
        checkStream(is1);
        checkStream(is2);
    }

    /**
     * Tests whether a defensive copy is made from the passed in data.
     */
    @Test
    public void testGetInstanceDefensiveCopy() throws IOException
    {
        byte[] data = TEST_DATA.getBytes();
        ByteArrayLocator locator = ByteArrayLocator.getInstance(data);
        data[0] = 'X';
        data[data.length - 1] = 'Y';
        checkStream(locator.getInputStream());
    }

    /**
     * Tests creating URLs from different input data. The URLs should differ.
     * Because the URLs are created based on hash codes it is not guaranteed
     * that two distinct data arrays cause different URLs. So we try multiple
     * times.
     */
    @Test
    public void testGetURLDifferentData()
    {
        Random rnd = new Random(42L);
        final int size = 10;
        final int maxTrials = 25;
        String url = null;
        boolean ok = false;

        for (int i = 0; i < maxTrials && !ok; i++)
        {
            byte[] data = new byte[size];
            rnd.nextBytes(data);
            String u = ByteArrayLocator.getInstance(data).getURL().toString();
            if (url == null)
            {
                url = u;
            }
            else
            {
                ok = !url.equals(u);
            }
        }

        assertTrue("All URLs were equal", ok);
    }

    /**
     * Tests whether the stream of a URL obtained from the locator can be read.
     */
    @Test
    public void testGetURLStream() throws IOException
    {
        ByteArrayLocator locator = ByteArrayLocator.getInstance(TEST_DATA);
        URL url = locator.getURL();
        checkStream(url.openStream());
    }

    /**
     * Tests querying the URL concurrently by multiple threads. Only a single
     * URL must be returned.
     */
    @Test
    public void testGetURLConcurrent() throws InterruptedException
    {
        final int threadCount = 100;
        final CountDownLatch latch = new CountDownLatch(1);
        final ByteArrayLocator locator = ByteArrayLocator
                .getInstance(TEST_DATA);

        class TestThread extends Thread
        {
            URL url;

            @Override
            public void run()
            {
                // wait at the latch to ensure a maximum of competition
                try
                {
                    latch.await();
                    url = locator.getURL();
                }
                catch (InterruptedException iex)
                {
                    // should not happen
                }
            }
        }

        TestThread[] threads = new TestThread[threadCount];
        for (int i = 0; i < threadCount; i++)
        {
            threads[i] = new TestThread();
            threads[i].start();
        }
        latch.countDown();
        for (TestThread t : threads)
        {
            t.join();
        }
        URL refURL = locator.getURL();
        for (TestThread t : threads)
        {
            assertSame("Got different URL", refURL, t.url);
        }
    }

    /**
     * Tests querying the URL when an exception is thrown.
     */
    @Test
    public void testGetURLException()
    {
        final MalformedURLException ex = new MalformedURLException(
                "Test exception!");
        Locator locator = new AbstractStreamLocator()
        {
            @Override
            protected URL createURL(URLStreamHandler streamHandler)
                    throws MalformedURLException
            {
                throw ex;
            }
        };
        try
        {
            locator.getURL();
            fail("Exception was not detected!");
        }
        catch (LocatorException lex)
        {
            assertEquals("Wrong cause", ex, lex.getCause());
        }
    }

    /**
     * Tests basic functionality of the equals() implementation.
     */
    @Test
    public void testTrivialEquals()
    {
        JGuiraffeTestHelper.testTrivialEquals(ByteArrayLocator
                .getInstance(TEST_DATA));
    }

    /**
     * Tests equals() with equal objects.
     */
    @Test
    public void testEqualsTrue()
    {
        ByteArrayLocator loc1 = ByteArrayLocator.getInstance(TEST_DATA);
        ByteArrayLocator loc2 = ByteArrayLocator.getInstance(TEST_DATA
                .getBytes());
        JGuiraffeTestHelper.checkEquals(loc1, loc2, true);
    }

    /**
     * Tests equals() with objects that are not equal.
     */
    @Test
    public void testEqualsFalse()
    {
        ByteArrayLocator loc1 = ByteArrayLocator.getInstance(TEST_DATA);
        byte[] data = TEST_DATA.getBytes();
        data[0] = 'X';
        ByteArrayLocator loc2 = ByteArrayLocator.getInstance(data);
        JGuiraffeTestHelper.checkEquals(loc1, loc2, false);
    }

    /**
     * Tests the string representation of the locator for short arrays. In this
     * case the array should be fully contained in the string.
     */
    @Test
    public void testToString()
    {
        byte[] data = TEST_DATA.substring(0, 10).getBytes();
        ByteArrayLocator locator = ByteArrayLocator.getInstance(data);
        String s = locator.toString();
        String dataStr = Arrays.toString(data);
        assertTrue("Array not contained in string: " + s,
                s.indexOf(dataStr) > 0);
    }

    /**
     * Tests the string representation of the locator for long arrays. In this
     * case only a limited number of elements should be displayed followed by an
     * ellipsis.
     */
    @Test
    public void testToStringLongData()
    {
        Random rnd = new Random();
        byte[] data = new byte[2 * ByteArrayLocator.MAX_ELEMENTS];
        rnd.nextBytes(data);
        ByteArrayLocator locator = ByteArrayLocator.getInstance(data);
        String s = locator.toString();
        int sepCnt = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == ',')
            {
                sepCnt++;
            }
        }
        assertTrue("Too many separators",
                sepCnt <= ByteArrayLocator.MAX_ELEMENTS);
        assertTrue("No ellipsis: " + s, s.indexOf("...") > 0);
    }
}
