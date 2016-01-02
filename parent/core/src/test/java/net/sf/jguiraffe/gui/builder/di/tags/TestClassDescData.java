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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.ClassDescription;

import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ClassDescData.
 *
 * @author Oliver Heger
 * @version $Id: TestClassDescData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestClassDescData
{
    /** Constant for the test class. */
    private static final Class<?> TEST_CLASS = TestClassDescData.class;

    /** Constant for the name of the test class. */
    private static final String TEST_CLASS_NAME = TEST_CLASS.getName();

    /** Constant for the name of a class loader. */
    private static final String LOADER_NAME = "MyClassLoader";

    /** Stores the data object to be tested. */
    private ClassDescData data;

    @Before
    public void setUp() throws Exception
    {
        data = new ClassDescData();
    }

    /**
     * Tests the properties of a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Class already set", data.getTargetClass());
        assertNull("Class name already set", data.getTargetClassName());
        assertNull("Class loader already set", data.getClassLoaderName());
    }

    /**
     * Tests the isDefined() method on a new instance.
     */
    @Test
    public void testIsDefinedInit()
    {
        assertFalse("New instance already defined", data.isDefined());
    }

    /**
     * Tests the isDefined() method when only a class loader is set.
     */
    @Test
    public void testIsDefinedLoaderOnly()
    {
        data.setClassLoaderName(LOADER_NAME);
        assertFalse("Instance with class loader only is defined", data
                .isDefined());
    }

    /**
     * Tests the isDefined() method when the target class is set.
     */
    @Test
    public void testIsDefinedClass()
    {
        data.setTargetClass(TEST_CLASS);
        assertTrue("Undefined", data.isDefined());
    }

    /**
     * Tests the isDefined() method when the target class name is set.
     */
    @Test
    public void testIsDefinedClassName()
    {
        data.setTargetClassName(TEST_CLASS_NAME);
        assertTrue("Undefined", data.isDefined());
    }

    /**
     * Tests the isDefined() method when all properties are set.
     */
    @Test
    public void testIsDefinedFull()
    {
        data.setClassLoaderName(LOADER_NAME);
        data.setTargetClass(TEST_CLASS);
        data.setTargetClassName(TEST_CLASS_NAME);
        assertTrue("Undefined", data.isDefined());
    }

    /**
     * Tests the isValid() method when only a class is defined.
     */
    @Test
    public void testIsValidClassOnly()
    {
        data.setTargetClass(TEST_CLASS);
        assertTrue("Invalid", data.isValid());
        data.setClassLoaderName(LOADER_NAME);
        assertTrue("Invalid with class loader", data.isValid());
    }

    /**
     * Tests the isValid() method when only a class name is defined.
     */
    @Test
    public void testIsValidNameOnly()
    {
        data.setTargetClassName(TEST_CLASS_NAME);
        assertTrue("Invalid", data.isValid());
        data.setClassLoaderName(LOADER_NAME);
        assertTrue("Invalid with class loader", data.isValid());
    }

    /**
     * Tests the isValid() method when both the class and its name are set.
     */
    @Test
    public void testIsValidBoth()
    {
        data.setTargetClass(TEST_CLASS);
        data.setTargetClassName(TEST_CLASS_NAME);
        data.setClassLoaderName(LOADER_NAME);
        assertTrue("Invalid", data.isValid());
    }

    /**
     * Tests the isValid() when both the class and its name are set, but they
     * point to different classes.
     */
    @Test
    public void testIsValidBothIncompatible()
    {
        data.setTargetClass(TEST_CLASS);
        data.setTargetClassName(ClassDescData.class.getName());
        assertFalse("Incompatible name not detected", data.isValid());
    }

    /**
     * Tests whether a class description can be created if a valid class is
     * provided.
     */
    @Test
    public void testCreateClassDescriptionValidClass() throws JellyTagException
    {
        data.setTargetClass(TEST_CLASS);
        ClassDescription cd = data.createClassDescription();
        assertEquals("Wrong class name", TEST_CLASS.getName(), cd
                .getTargetClassName());
        assertNull("A class loader is set", cd.getClassLoaderName());
    }

    /**
     * Tests creating a class description if a valid class name is provided.
     */
    @Test
    public void testCreateClassDescriptionValidClassName()
            throws JellyTagException
    {
        data.setTargetClassName(TEST_CLASS_NAME);
        data.setClassLoaderName(LOADER_NAME);
        ClassDescription cd = data.createClassDescription();
        assertEquals("Wrong class name", TEST_CLASS_NAME, cd
                .getTargetClassName());
        assertEquals("Wrong class loader", LOADER_NAME, cd.getClassLoaderName());
    }

    /**
     * Tries to create a class description if required properties are missing.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateClassDescriptionUndefined() throws JellyTagException
    {
        data.setClassLoaderName(LOADER_NAME);
        data.createClassDescription();
    }

    /**
     * Tries to create a class description if there are invalid properties.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateClassDescriptionInvalid() throws JellyTagException
    {
        data.setTargetClass(ClassDescData.class);
        data.setTargetClassName(TEST_CLASS_NAME);
        data.createClassDescription();
    }

    /**
     * Tests querying an optional class description if the data is defined and
     * valid.
     */
    @Test
    public void testGetOptionalClassDescriptionDefined()
            throws JellyTagException
    {
        data.setTargetClass(TEST_CLASS);
        ClassDescription cd = data.getOptionalClassDescription();
        assertEquals("Wrong class description", TEST_CLASS_NAME, cd
                .getTargetClassName());
    }

    /**
     * Tests querying an optional class description if no class is defined.
     */
    @Test
    public void testGetOptionalClassDescriptionUndefined()
            throws JellyTagException
    {
        assertNull("Wrong undefined class desc", data
                .getOptionalClassDescription());
    }

    /**
     * Tests querying an optional class description if the data is invalid.
     */
    @Test(expected = JellyTagException.class)
    public void testGetOptionalClassDescriptionInvalid()
            throws JellyTagException
    {
        data.setTargetClass(TEST_CLASS);
        data.setTargetClassName("Some invalid name");
        data.getOptionalClassDescription();
    }

    /**
     * Tests whether the class can be resolved.
     */
    @Test
    public void testResolveClass() throws JellyTagException
    {
        final ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        data.setTargetClass(TEST_CLASS);
        assertEquals("Wrong resolved class", TEST_CLASS, data.resolveClass(clp));
        EasyMock.verify(clp);
    }
}
