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
package net.sf.jguiraffe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A helper class with functionality that can be used by test classes.
 * </p>
 * <p>
 * This class contains a set of utility methods that are useful for many test
 * classes. Because of that this functionality was extracted into its own
 * utility class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: JGuiraffeTestHelper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class JGuiraffeTestHelper
{
    /** Constant for the buffer size for reading data from streams.*/
    private static final int BUF_SIZE = 4096;

    /**
     * Tests the implementation of <code>equals()</code> and (partly)
     * <code>hashCode()</code>. The passed in first object is compared with the
     * second with the expected result. Then it is checked whether equals() is
     * symmetric. If the expected result is <b>true</b>, the hash codes must
     * also match.
     *
     * @param o1 the first object (must not be <b>null</b>)
     * @param o2 the second object (can be <b>null</b>)
     * @param expected the expected result of the comparison
     */
    public static void checkEquals(Object o1, Object o2, boolean expected)
    {
        assertEquals("Wrong result of equals()", expected, o1.equals(o2));
        if (o2 != null)
        {
            assertEquals("Not symmetric", expected, o2.equals(o1));
        }
        if (expected)
        {
            assertEquals("Different hash codes", o1.hashCode(), o2.hashCode());
        }
    }

    /**
     * Helper method for performing some trivial checks of the equals() method
     * of an object. This method checks
     * <ul>
     * <li>whether the object equals itself</li>
     * <li>whether equals() for a null argument returns false</li>
     * <li>whether equals() for an arbitrary object returns false</li>
     * </ul>
     *
     * @param obj the object to check (must not be <b>null</b>
     */
    public static void testTrivialEquals(Object obj)
    {
        assertTrue("Object not equals itself", obj.equals(obj));
        assertFalse("Object equals null", obj.equals(null));
        assertFalse("Object equals object of another class", obj
                .equals(new Object()));
    }

    /**
     * Reads the content of the specified input stream and provides it as byte
     * array stream. This may be useful for reading the content of test files.
     * The <code>close</code> parameter determines whether the input stream is
     * to be closed after it was completely read. Occurring exceptions are
     * re-thrown as runtime exceptions.
     *
     * @param in the input stream to read
     * @param close a flag whether the stream is to be closed
     * @return a byte array stream with the data read from the input stream
     */
    public static ByteArrayOutputStream readStream(InputStream in, boolean close)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try
        {
            byte[] buf = new byte[BUF_SIZE];
            int c;

            while ((c = in.read(buf)) > 0)
            {
                out.write(buf, 0, c);
            }

            if (close)
            {
                in.close();
            }

            return out;
        }
        catch (IOException ioex)
        {
            throw new RuntimeException("Error when reading stream", ioex);
        }
    }

    /**
     * Helper method for creating a date object based on the given year, month,
     * and day. This method internally creates a {@code Calendar}, populates it
     * with the given date values, and returns the corresponding {@code Date}.
     *
     * @param year the year
     * @param month the month (note: this is 0-based!)
     * @param day the day
     * @return the date object
     */
    public static Date createDate(int year, int month, int day)
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, day);
        return cal.getTime();
    }

    /**
     * Tests whether the specified collections are equal. This method can be
     * used for instance when dealing with unmodifiable collections, which do
     * not implement equals(). All comparisons are null safe.
     *
     * @param c1 collection 1
     * @param c2 collection 2
     * @return a flag whether these collections are equal
     */
    public static boolean collectionEquals(Collection<?> c1, Collection<?> c2)
    {
        if (c1 == null && c2 == null)
        {
            return true;
        }
        if (c1 == null || c2 == null)
        {
            return false;
        }

        if (c1.size() != c2.size())
        {
            return false;
        }

        Iterator<?> it1 = c1.iterator();
        Iterator<?> it2 = c2.iterator();
        while (it1.hasNext())
        {
            if (!ObjectUtils.equals(it1.next(), it2.next()))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Helper method for serializing an object. This method can be used for
     * serialization tests. The passed in object is serialized (to an in-memory
     * stream) and then read again. The copy is returned. Exceptions are thrown
     * if serialization fails.
     *
     * @param <T> the type of the object
     * @param obj the object to be serialized
     * @return the copy created through serialization
     * @throws IOException if an IO exception occurs (typically because the
     *         object cannot be serialized)
     */
    public static <T> T serialize(T obj) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.close();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                bos.toByteArray()));
        try
        {
            @SuppressWarnings("unchecked")
            T result = (T) ois.readObject();
            return result;
        }
        catch (ClassNotFoundException cfex)
        {
            // this should not happen
            fail("Class not found on deserialization: " + cfex);
            return null;
        }
    }
}
