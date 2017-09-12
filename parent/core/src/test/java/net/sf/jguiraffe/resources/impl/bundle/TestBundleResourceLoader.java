/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.resources.impl.bundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.resources.ResourceGroup;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for BundleResourceLoader.
 *
 * @author Oliver Heger
 * @version $Id: TestBundleResourceLoader.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestBundleResourceLoader
{
    /** Constant for the name of the test resources group. */
    private static final String TESTNAME = "testresources";

    /** Constant for the number of available resource keys. */
    private static final int KEY_COUNT = 4;

    /**
     * Tests whether test resources can be obtained.
     *
     * @param loader the loader to test
     */
    private void checkResourceAccess(BundleResourceLoader loader)
    {
        ResourceGroup groupDE = loader.loadGroup(Locale.GERMANY, TESTNAME);
        assertEquals("Guten Tag", groupDE.getResource("test1"));
        assertEquals("Fertig", groupDE.getResource("test2"));
        assertEquals("Abbrechen", groupDE.getResource("test3"));
        checkGroup(groupDE, Locale.GERMANY);

        ResourceGroup groupEN = loader.loadGroup(Locale.US, TESTNAME);
        assertEquals("Hello", groupEN.getResource("test1"));
        assertEquals("OK", groupEN.getResource("test2"));
        assertEquals("Cancel", groupEN.getResource("test3"));
        checkGroup(groupEN, Locale.US);
    }

    /**
     * Tests whether an instance created through the standard constructor can
     * load resources.
     */
    @Test
    public void testLoadDefaultInstance()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ClassLoaderProvider clp = loader.getClassLoaderProvider();
        assertTrue("Got class loaders", clp.classLoaderNames().isEmpty());
        assertNull("Got a class loader name", loader.getClassLoaderName());
        checkResourceAccess(loader);
    }

    /**
     * Tests whether resources can be loaded if a class loader provider is
     * available with a default class loader.
     */
    @Test
    public void testLoadWithCLP()
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        clp.registerClassLoader(TESTNAME, getClass().getClassLoader());
        clp.setDefaultClassLoaderName(TESTNAME);
        BundleResourceLoader loader = new BundleResourceLoader(clp, null);
        assertSame("Wrong class loader provider", clp,
                loader.getClassLoaderProvider());
        assertNull("Got a class loader name", loader.getClassLoaderName());
        checkResourceAccess(loader);
    }

    /**
     * Tests whether a specific class loader can be used for resource access.
     */
    @Test
    public void testLoadWithCLPAndCLName()
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        clp.registerClassLoader(TESTNAME, getClass().getClassLoader());
        clp.setDefaultClassLoaderName(TESTNAME + "_other");
        BundleResourceLoader loader = new BundleResourceLoader(clp, TESTNAME);
        assertEquals("Wrong class loader name", TESTNAME,
                loader.getClassLoaderName());
        checkResourceAccess(loader);
    }

    /**
     * Tests whether a fall-back class loader is tried if loading resources from
     * the class loader provider fails.
     */
    @Test
    public void testLoadWithCLPFallBack()
    {
        ClassLoaderProvider clp = new DefaultClassLoaderProvider();
        ClassLoader cl = createFailingCLMock();
        clp.registerClassLoader(TESTNAME, cl);
        clp.setDefaultClassLoaderName(TESTNAME);
        BundleResourceLoader loader = new BundleResourceLoader(clp, TESTNAME);
        checkResourceAccess(loader);
    }

    /**
     * Creates a mock for a class loader which always fails when asked to load a
     * resource bundle.
     *
     * @return the mock for the failing class loader
     */
    private static ClassLoader createFailingCLMock()
    {
        ClassLoader cl = EasyMock.createMock(ClassLoader.class);
        try
        {
            EasyMock.expect(cl.loadClass(EasyMock.anyObject(String.class)))
                    .andThrow(new ClassNotFoundException()).anyTimes();
            EasyMock.expect(
                    cl.getResourceAsStream(EasyMock.anyObject(String.class)))
                    .andReturn(null).anyTimes();
        }
        catch (ClassNotFoundException cnfex)
        {
            fail("Unexpected exception: " + cnfex);
        }
        EasyMock.replay(cl);
        return cl;
    }

    /**
     * Tests the load() method if it is access by multiple threads.
     */
    @Test
    public void testLoadConcurrent() throws InterruptedException
    {
        final BundleResourceLoader loader = new BundleResourceLoader();
        final CountDownLatch latch = new CountDownLatch(1);
        final int threadCount = 25;
        class LoadThread extends Thread
        {
            ResourceGroup group;

            @Override
            public void run()
            {
                try
                {
                    latch.await();
                    group = loader.loadGroup(Locale.GERMANY, TESTNAME);
                }
                catch (InterruptedException iex)
                {
                    // ignore
                }
            }
        }
        LoadThread[] threads = new LoadThread[threadCount];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new LoadThread();
            threads[i].start();
        }
        latch.countDown();
        ResourceGroup group = loader.loadGroup(Locale.GERMANY, TESTNAME);
        for (LoadThread t : threads)
        {
            t.join();
            assertSame("Wrong resource group", group, t.group);
        }
    }

    /**
     * Tests loading an unknown resource group. This should cause an exception.
     */
    @Test(expected = MissingResourceException.class)
    public void testLoadGroupUnknown()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        loader.loadGroup(Locale.GERMANY, "unknown");
    }

    /**
     * Tests loading a group with null name. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadGroupNull()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        loader.loadGroup(Locale.CANADA, null);
    }

    /**
     * Tests caching. A group that is requested multiple times must always point
     * to the same instance.
     */
    @Test
    public void testLoadGroupFromCache()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ResourceGroup grp1 = loader.loadGroup(Locale.GERMANY, TESTNAME);
        ResourceGroup grp2 = loader.loadGroup(Locale.GERMANY, TESTNAME);
        assertSame("Groups are not identical", grp1, grp2);
        assertNotSame("Same object returned for other locale", grp1, loader
                .loadGroup(Locale.GERMAN, TESTNAME));
    }

    /**
     * Tests accessing an unknown key from a resource group. This should cause
     * an exception.
     */
    @Test(expected = MissingResourceException.class)
    public void testGetUnknownKey()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ResourceGroup group = loader.loadGroup(Locale.GERMANY, TESTNAME);
        group.getResource("unknown");
    }

    /**
     * Tests accessing a resource with a null key. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetNullKey()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ResourceGroup group = loader.loadGroup(Locale.ENGLISH, TESTNAME);
        group.getResource(null);
    }

    /**
     * Tests if default resources are loaded if an unknown locale is specified.
     */
    @Test
    public void testLoadGroupDefaults()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ResourceGroup g = loader.loadGroup(Locale.CHINA, TESTNAME);
        assertEquals("Guten Tag", g.getResource("test1"));
    }

    /**
     * Tests if incomplete groups can be used, e.g. if for a locale not all
     * available keys are located, for the missing keys resources of the default
     * group should be returned.
     */
    @Test
    public void testIncompleteGroups()
    {
        BundleResourceLoader loader = new BundleResourceLoader();
        ResourceGroup grp = loader.loadGroup(Locale.ENGLISH, TESTNAME);
        checkGroup(grp, Locale.ENGLISH);
        assertEquals("Wrong resource from incomplete group", "Hallo Welt!", grp
                .getResource("test4"));
    }

    /**
     * Checks the specified resource group. This method tests whether all keys
     * can be found and the name and the locale are correctly reported.
     *
     * @param group the group to test
     * @param locale the locale
     */
    private void checkGroup(ResourceGroup group, Locale locale)
    {
        assertEquals("Wrong group name", TESTNAME, group.getName());
        assertEquals("Wrong locale", locale, group.getLocale());

        Set<Object> keys = group.getKeys();
        assertEquals("Wrong number of keys", KEY_COUNT, keys.size());
        for (int i = 1; i <= KEY_COUNT; i++)
        {
            assertTrue("Did not find key number " + i, keys
                    .contains("test" + i));
        }
    }
}
