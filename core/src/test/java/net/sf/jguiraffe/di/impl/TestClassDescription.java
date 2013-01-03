/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.di.ClassLoaderProvider;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for ClassDescription.
 *
 * @author Oliver Heger
 * @version $Id: TestClassDescription.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestClassDescription
{
    /**
     * Tests obtaining an instance for a known class.
     */
    @Test
    public void testGetInstanceClass()
    {
        ClassDescription d = ClassDescription.getInstance(getClass());
        assertEquals("Wrong class name", getClass().getName(), d
                .getTargetClassName());
        assertNull("Specific class loader is set", d.getClassLoaderName());
    }

    /**
     * Tries to obtain an instance for a null class. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceClassNull()
    {
        ClassDescription.getInstance((Class<?>) null);
    }

    /**
     * Tests obtaining an instance for a class name and a class loader name.
     */
    @Test
    public void testGetInstanceClassName()
    {
        ClassDescription d = ClassDescription.getInstance(getClass().getName(),
                ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        assertEquals("Wrong class name", getClass().getName(), d
                .getTargetClassName());
        assertEquals("Wrong class loader name",
                ClassLoaderProvider.CONTEXT_CLASS_LOADER, d.getClassLoaderName());
    }

    /**
     * Tests obtaining an instance for a class name and the default class
     * loader.
     */
    @Test
    public void testGetInstanceClassNameDefLoader()
    {
        ClassDescription d = ClassDescription.getInstance(getClass().getName());
        assertEquals("Wrong class name", getClass().getName(), d
                .getTargetClassName());
        assertNull("Specific class loader is set", d.getClassLoaderName());
    }

    /**
     * Tries obtaining an instance for a null class name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetInstanceClassNameNull()
    {
        ClassDescription.getInstance(null, null);
    }

    /**
     * Tests querying the target class when it is already known.
     */
    @Test
    public void testGetTargetClassNoResolve()
    {
        ClassLoaderProvider clProvider = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clProvider);
        ClassDescription d = ClassDescription.getInstance(getClass());
        assertEquals("Wrong target class", getClass(), d
                .getTargetClass(clProvider));
        EasyMock.verify(clProvider);
    }

    /**
     * Tests querying the target class when it has to be resolved.
     */
    @Test
    public void testGetTargetClassResolve()
    {
        ClassLoaderProvider clProvider = EasyMock
                .createMock(ClassLoaderProvider.class);
        clProvider.loadClass(getClass().getName(),
                ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        EasyMock.expectLastCall().andReturn(getClass());
        EasyMock.replay(clProvider);
        ClassDescription d = ClassDescription.getInstance(getClass().getName(),
                ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        assertEquals("Wrong target class", getClass(), d
                .getTargetClass(clProvider));
        EasyMock.verify(clProvider);
    }

    /**
     * Tests whether a once resolved class name is cached.
     */
    @Test
    public void testGetTargetClassResolveCache()
    {
        ClassLoaderProvider clProvider = EasyMock
                .createMock(ClassLoaderProvider.class);
        clProvider.loadClass(getClass().getName(), null);
        EasyMock.expectLastCall().andReturn(getClass());
        EasyMock.replay(clProvider);
        ClassDescription d = ClassDescription.getInstance(getClass().getName());
        assertEquals("Wrong target class (1)", getClass(), d
                .getTargetClass(clProvider));
        assertEquals("Wrong target class (2)", getClass(), d
                .getTargetClass(clProvider));
        EasyMock.verify(clProvider);
    }

    /**
     * Tests getTargetClass() if a null provider is passed in.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTargetClassNullProvider()
    {
        ClassDescription.getInstance(getClass().getName()).getTargetClass(
                null);
    }

    /**
     * Tests the equals method.
     */
    @Test
    public void testEquals()
    {
        ClassDescription d1 = ClassDescription.getInstance(getClass());
        checkEquals(d1, d1, true);
        ClassDescription d2 = ClassDescription.getInstance(getClass());
        checkEquals(d1, d2, true);
        d2 = ClassDescription.getInstance(getClass().getName());
        checkEquals(d1, d2, true);
        d2 = ClassDescription.getInstance(getClass().getName(), "aLoader");
        checkEquals(d1, d2, false);
        checkEquals(ClassDescription.getInstance(getClass().getName(),
                "aLoader"), d2, true);
        d2 = ClassDescription.getInstance("anotherClass");
        checkEquals(d1, d2, false);
        checkEquals(d1, null, false);
        checkEquals(d1, "test", false);
    }

    /**
     * Tests then equals() and hashCode() implementations.
     *
     * @param d the description object
     * @param obj the object to compare to
     * @param expected the expected result
     */
    private void checkEquals(ClassDescription d, Object obj, boolean expected)
    {
        JGuiraffeTestHelper.checkEquals(d, obj, expected);
    }

    /**
     * Tests the toString() implementation. We test whether the class name and
     * the class loader can be found in the resulting string.
     */
    @Test
    public void testToStringWithClassLoader()
    {
        String s = ClassDescription.getInstance(getClass().getName(),
                "testLoader").toString();
        assertTrue("Class name not found in string " + s, s.indexOf(getClass()
                .getName()) >= 0);
        assertTrue("Class loader name not found in string " + s, s
                .indexOf("testLoader") >= 0);
    }

    /**
     * Tests the toString() method when no class loader is specified.
     */
    @Test
    public void testToStringNoClassLoader()
    {
        String s = ClassDescription.getInstance(getClass()).toString();
        assertTrue("Class name not found in string " + s, s.indexOf(getClass()
                .getName()) >= 0);
    }
}
