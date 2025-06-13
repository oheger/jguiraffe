/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.di.ReflectionTestClass;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DefaultClassLoaderProvider}.
 *
 * @author Oliver Heger
 * @version $Id: TestDefaultClassLoaderProvider.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestDefaultClassLoaderProvider
{
    /** Constant for a special class loader. */
    private static final ClassLoader TEST_CL = new ClassLoaderTestImpl();

    /** Constant for the name of a class loader. */
    private static final String CL_NAME = "TestClassLoader";

    /** The object to be tested. */
    private DefaultClassLoaderProvider clp;

    @Before
    public void setUp() throws Exception
    {
        clp = new DefaultClassLoaderProvider();
    }

    /**
     * Tests whether a default class loader can be obtained if no default name
     * was set.
     */
    @Test
    public void testGetClassLoaderDefaultUndefined()
    {
        assertNull("Got a default class loader name",
                clp.getDefaultClassLoaderName());
        assertEquals("Wrong default class loader", clp.getClass()
                .getClassLoader(), clp.getClassLoader(null));
    }

    /**
     * Tests whether the name for a default class loader can be set.
     */
    @Test
    public void testSetDefaultClassLoader()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        clp.setDefaultClassLoaderName(CL_NAME);
        assertSame("Wrong default class loader", TEST_CL,
                clp.getClassLoader(null));
    }

    /**
     * Tests registering a class loader.
     */
    @Test
    public void testRegisterClassLoader()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        assertSame("Wrong class loader", TEST_CL, clp.getClassLoader(CL_NAME));
        Set<String> clNames = clp.classLoaderNames();
        assertEquals("Wrong number of loaders", 1, clNames.size());
        assertTrue("CL name not found", clNames.contains(CL_NAME));
    }

    /**
     * Tests registering a class loader with a null name. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testRegisterClassLoaderNullName()
    {
        clp.registerClassLoader(null, TEST_CL);
    }

    /**
     * Tests unregistering a class loader. This can be done by passing a null
     * reference to registerClassLoader().
     */
    @Test
    public void testUnregisterClassLoader()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        clp.registerClassLoader(CL_NAME, null);
        assertTrue("Loader still present", clp.classLoaderNames().isEmpty());
    }

    /**
     * Tries unregistering a non existing class loader. This should have no
     * effect.
     */
    @Test
    public void testUnregisterClassLoaderNonExisting()
    {
        final String clName2 = CL_NAME + "2";
        clp.registerClassLoader(CL_NAME, TEST_CL);
        assertTrue("Name not found", clp.classLoaderNames().contains(CL_NAME));
        clp.registerClassLoader(clName2, null);
        assertTrue("Name not found after unregister", clp.classLoaderNames()
                .contains(CL_NAME));
    }

    /**
     * Tests loading a class.
     */
    @Test
    public void testLoadClass()
    {
        Class<?> cls = clp.loadClass(getClass().getName(), null);
        assertEquals("Wrong class", getClass(), cls);
    }

    /**
     * Tries to load a null class.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadClassNull() {
        clp.loadClass(null, null);
    }

    /**
     * Tests querying the context class loader.
     */
    @Test
    public void testGetClassLoaderContext()
    {
        ClassLoader oldCtxLoader = Thread.currentThread()
                .getContextClassLoader();
        Thread.currentThread().setContextClassLoader(TEST_CL);
        try
        {
            assertEquals("Wrong loader", TEST_CL, clp
                    .getClassLoader(ClassLoaderProvider.CONTEXT_CLASS_LOADER));
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(oldCtxLoader);
        }
    }

    /**
     * Tests querying a class loader that has been registered.
     */
    @Test
    public void testGetClassLoaderRegistered()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        assertEquals("Wrong class loader", TEST_CL, clp.getClassLoader(CL_NAME));
    }

    /**
     * Tests querying an unknown class loader. This should cause an exception.
     */
    @Test(expected = InjectionException.class)
    public void testGetClassLoaderUnregistered()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        clp.getClassLoader(CL_NAME + "?");
    }

    /**
     * Tests that the set with class loader names cannot be modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testClassLoaderNamesModify()
    {
        clp.registerClassLoader(CL_NAME, TEST_CL);
        Iterator<String> it = clp.classLoaderNames().iterator();
        it.next();
        it.remove();
    }

    /**
     * Tests whether the context class loader can be set as default class
     * loader.
     */
    @Test
    public void testCCLAsDefault()
    {
        clp.setDefaultClassLoaderName(ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        assertSame("Wrong class loader", Thread.currentThread()
                .getContextClassLoader(), clp.getClassLoader(null));
    }

    /**
     * Tests the default value of the handleInternalClasses flag.
     */
    @Test
    public void testIsHandleInternalClassesDefault()
    {
        assertTrue("Wrong flag value", clp.isHandleInternalClasses());
    }

    /**
     * Tests whether the flag for loading internal classes is evaluated.
     */
    @Test
    public void testLoadInternalClass()
    {
        ClassLoader loader = EasyMock.createMock(ClassLoader.class);
        EasyMock.replay(loader);
        clp.registerClassLoader(CL_NAME, loader);
        clp.setDefaultClassLoaderName(CL_NAME);
        clp.loadClass(ReflectionTestClass.class.getName(), CL_NAME);
    }

    /**
     * Tests class loading if the flag for handling internal classes is
     * disabled.
     */
    @Test
    public void testLoadInternalClassNoSpecialTreatment()
    {
        clp = new DefaultClassLoaderProvider(false);
        clp.registerClassLoader(CL_NAME, getClass().getClassLoader());
        assertNotNull("Class not loaded",
                clp.loadClass(ReflectionTestClass.class.getName(), CL_NAME));
    }

    /**
     * A specialized class loader implementation used for testing.
     */
    private static class ClassLoaderTestImpl extends ClassLoader
    {
    }
}
