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
package net.sf.jguiraffe.locators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;

import org.apache.commons.beanutils.ConversionException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code LocatorConverter}.
 *
 * @author Oliver Heger
 * @version $Id: TestLocatorConverter.java 211 2012-07-10 19:49:13Z oheger $
 */
public class TestLocatorConverter
{
    /** The converter to be tested. */
    private LocatorConverter converter;

    @Before
    public void setUp() throws Exception
    {
        converter = new LocatorConverter();
    }

    /**
     * Tries to convert a null object.
     */
    @Test(expected = ConversionException.class)
    public void testConvertNull()
    {
        converter.convert(Locator.class, null);
    }

    /**
     * Tries to convert a string which does not have the expected format.
     */
    @Test(expected = ConversionException.class)
    public void testConvertInvalidSyntax()
    {
        converter.convert(Locator.class,
                "not a valid string!");
    }

    /**
     * Tries to convert a string with an unknown prefix.
     */
    @Test(expected = ConversionException.class)
    public void testConvertUnknownPrefix()
    {
        converter.convert(Locator.class, "unknown:test");
    }

    /**
     * Tests whether a class path locator can be converted.
     */
    @Test
    public void testConvertClassPath()
    {
        ClassPathLocator loc =
                (ClassPathLocator) converter.convert(
                        Locator.class, "classpath:myresource.properties");
        assertEquals("Wrong resource", "myresource.properties",
                loc.getResourceName());
        assertNull("Got a class loader", loc.getClassLoader());
    }

    /**
     * Tests whether a class path locator can be constructed with a class loader
     * reference if no provider was set.
     */
    @Test
    public void testConvertClassPathNoCLP()
    {
        ClassPathLocator loc =
                (ClassPathLocator) converter.convert(Locator.class,
                        "classpath:myresource.properties;loader");
        assertEquals("Wrong resource", "myresource.properties",
                loc.getResourceName());
        assertNull("Got a class loader", loc.getClassLoader());
    }

    /**
     * Tests whether the class loader provider is used to resolve class loaders.
     */
    @Test
    public void testConvertClassPathWithCLP()
    {
        ClassLoader cl = EasyMock.createMock(ClassLoader.class);
        ClassLoader clDef = EasyMock.createMock(ClassLoader.class);
        EasyMock.replay(cl, clDef);
        DefaultClassLoaderProvider clp = new DefaultClassLoaderProvider();
        clp.registerClassLoader("loader", cl);
        clp.registerClassLoader("def", clDef);
        clp.setDefaultClassLoaderName("def");
        converter = new LocatorConverter(clp);
        ClassPathLocator loc =
                (ClassPathLocator) converter.convert(Locator.class,
                        "classpath:myresource.properties;loader");
        assertSame("Wrong class loader", cl, loc.getClassLoader());
    }

    /**
     * Tests whether a default class loader is obtained from the provider.
     */
    @Test
    public void testConvertClassPathWithCLPDefault()
    {
        ClassLoader cl = EasyMock.createMock(ClassLoader.class);
        DefaultClassLoaderProvider clp = new DefaultClassLoaderProvider();
        clp.registerClassLoader("loader", cl);
        clp.setDefaultClassLoaderName("loader");
        converter = new LocatorConverter(clp);
        ClassPathLocator loc =
                (ClassPathLocator) converter.convert(Locator.class,
                        "classpath:myresource.properties");
        assertSame("Wrong class loader", cl, loc.getClassLoader());
    }

    /**
     * Tests whether a file locator can be converted.
     */
    @Test
    public void testConvertFile()
    {
        FileLocator loc =
                (FileLocator) converter.convert(
                        Locator.class, "FILE:test.txt");
        assertEquals("Wrong file locator", FileLocator.getInstance("test.txt"),
                loc);
    }

    /**
     * Tests whether a URL locator can be converted.
     */
    @Test
    public void testConvertURL()
    {
        URLLocator loc =
                (URLLocator) converter.convert(
                        Locator.class, "Url:http://jguirafe.sf.net");
        assertEquals("Wrong URL", "http://jguirafe.sf.net", loc.getURL()
                .toString());
    }

    /**
     * Tests a conversion if the creation of the locator throws an exception.
     */
    @Test(expected = ConversionException.class)
    public void testConvertLocatorEx()
    {
        converter.convert(Locator.class,
                "URL:not a valid URL?!");
    }

    /**
     * Tests a conversion if no data for the locator is passed in.
     */
    @Test(expected = ConversionException.class)
    public void testConvertNoData()
    {
        converter.convert(Locator.class, "CLASSPAth:");
    }
}
