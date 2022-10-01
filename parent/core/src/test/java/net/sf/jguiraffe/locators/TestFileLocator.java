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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.jguiraffe.JGuiraffeTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FileLocator.
 *
 * @author Oliver Heger
 * @version $Id: TestFileLocator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestFileLocator
{
    /** Constant for the test file. */
    private static final File TEST_FILE = new File(new File("target"),
            "test.txt");

    @Before
    public void setUp() throws Exception
    {
        // Create a simple test file
        PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE));
        out.println("A test file");
        out.close();
    }

    @After
    public void tearDown() throws Exception
    {
        if (TEST_FILE.exists())
        {
            assertTrue("Test file cannot be deleted", TEST_FILE.delete());
        }
    }

    /**
     * Creates a test locator object.
     *
     * @return the test locator
     */
    private FileLocator setUpLocator()
    {
        return FileLocator.getInstance(TEST_FILE);
    }

    /**
     * Tests getting the input stream, which should be null.
     */
    @Test
    public void testGetInputStream() throws IOException
    {
        assertNull("Non null input stream returned", setUpLocator()
                .getInputStream());
    }

    /**
     * Tests obtaining the file.
     */
    @Test
    public void testGetFile()
    {
        assertEquals("Wrong file returned", TEST_FILE, setUpLocator().getFile());
    }

    /**
     * Tests obtaining the URL of the file.
     */
    @Test
    public void testGetURL()
    {
        assertEquals("Wrong URL", LocatorUtils.fileURL(TEST_FILE),
                setUpLocator().getURL());
    }

    /**
     * Tests querying the file name.
     */
    @Test
    public void testGetFileName()
    {
        FileLocator loc = setUpLocator();
        assertEquals("Wrong file name", TEST_FILE.getAbsolutePath(), loc
                .getFileName());
    }

    /**
     * Tests creating an instance for a null file. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullFile()
    {
        FileLocator.getInstance((File) null);
    }

    /**
     * Tests creating an instance for a null file name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceNullName()
    {
        FileLocator.getInstance((String) null);
    }

    /**
     * Tests basic operations of the equals() method.
     */
    @Test
    public void testTrivialEquals()
    {
        JGuiraffeTestHelper.testTrivialEquals(setUpLocator());
    }

    /**
     * Tests equals() for objects that are indeed equal.
     */
    @Test
    public void testEqualsTrue()
    {
        FileLocator loc1 = setUpLocator();
        FileLocator loc2 = setUpLocator();
        JGuiraffeTestHelper.checkEquals(loc1, loc2, true);
    }

    /**
     * Tests equals() for objects that are not equal.
     */
    @Test
    public void testEqualsFalse()
    {
        FileLocator loc1 = setUpLocator();
        FileLocator loc2 = FileLocator.getInstance("AnotherTest.file");
        JGuiraffeTestHelper.checkEquals(loc1, loc2, false);
    }

    /**
     * Tests whether the string representation of the file locator contains the
     * file name.
     */
    @Test
    public void testToString()
    {
        FileLocator loc = setUpLocator();
        String s = loc.toString();
        assertTrue("File name not found: " + s, s.indexOf(TEST_FILE
                .getAbsolutePath()) > 0);
    }
}
