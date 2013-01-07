/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.providers.MapBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for MapTag and PropertiesTag.
 *
 * @author Oliver Heger
 * @version $Id: TestMapTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestMapTag
{
    /** Constant for the key class. */
    private static final Class<?> KEY_CLS = Integer.class;

    /** Constant for the value class. */
    private static final Class<?> VAL_CLS = String.class;

    /** Constant for the number of test dependencies. */
    private static final int DEP_COUNT = 12;

    /** Stores the builder data object. */
    private DIBuilderData builderData;

    /** The tag to be tested. */
    private MapTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new MapTag();
        JellyContext ctx = new JellyContext();
        tag.setContext(ctx);
        tag.setParent(new SetPropertyTag());
        tag.setBody(new TagScript());
        builderData = new DIBuilderData();
        builderData.put(ctx);
        builderData.setClassLoaderProvider(EasyMock
                .createNiceMock(ClassLoaderProvider.class));
    }

    /**
     * Executes the tag. After that the object created by the tag is available.
     */
    private void runTag()
    {
        try
        {
            tag.doTag(new XMLOutput());
        }
        catch (JellyTagException jtex)
        {
            fail("Exception thrown by doTag: " + jtex);
        }
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNotNull("No key class data", tag.getKeyClassData());
        assertFalse("Key class data defined", tag.getKeyClassData().isDefined());
        assertNull("Key class desc exists", tag.getKeyClassDesc());
        assertNotNull("No value class data", tag.getValueClassData());
        assertFalse("Value class data defined", tag.getValueClassData()
                .isDefined());
        assertNull("Value class desc exists", tag.getValueClassDesc());
        assertFalse("Ordered attribute set", tag.isOrdered());
        assertTrue("Already key dependencies", tag.getKeyDependencies()
                .isEmpty());
        assertTrue("Already value dependencies", tag.getValueDependencies()
                .isEmpty());
    }

    /**
     * Tests a tag with an invalid parent. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyInvalidParent() throws JellyTagException
    {
        tag.setParent(new ConstructorInvocationTag());
        tag.processBeforeBody();
    }

    /**
     * Tests whether the key class description is correctly created when the key
     * class is specified.
     */
    @Test
    public void testProcessBeforeBodyKeyClass() throws JellyTagException
    {
        tag.setKeyClass(KEY_CLS);
        tag.processBeforeBody();
        assertEquals("Wrong key class desc", KEY_CLS.getName(), tag
                .getKeyClassDesc().getTargetClassName());
        assertNull("A class loader is set", tag.getKeyClassDesc()
                .getClassLoaderName());
    }

    /**
     * Tests whether the key class description is correctly created when the key
     * class name is specified.
     */
    @Test
    public void testProcessBeforeBodyKeyClassName() throws JellyTagException
    {
        tag.setKeyClassName(KEY_CLS.getName());
        tag.setKeyClassLoader(ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        tag.processBeforeBody();
        ClassDescription cd = tag.getKeyClassDesc();
        assertEquals("Wrong key class name", KEY_CLS.getName(), cd
                .getTargetClassName());
        assertEquals("Wrong class loader",
                ClassLoaderProvider.CONTEXT_CLASS_LOADER, cd
                        .getClassLoaderName());
    }

    /**
     * Tests whether the value class description is correctly created when the
     * value class is specified.
     */
    @Test
    public void testProcessBeforeBodyValueClass() throws JellyTagException
    {
        tag.setValueClass(VAL_CLS);
        tag.processBeforeBody();
        assertEquals("Wrong value class desc", VAL_CLS.getName(), tag
                .getValueClassDesc().getTargetClassName());
        assertNull("A class loader is set", tag.getValueClassDesc()
                .getClassLoaderName());
    }

    /**
     * Tests whether the value class description is correctly created when the
     * value class name is specified.
     */
    @Test
    public void testProcessBeforeBodyValueClassName() throws JellyTagException
    {
        tag.setValueClassName(VAL_CLS.getName());
        tag.setValueClassLoader(ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        tag.processBeforeBody();
        ClassDescription cd = tag.getValueClassDesc();
        assertEquals("Wrong value class name", VAL_CLS.getName(), cd
                .getTargetClassName());
        assertEquals("Wrong class loader",
                ClassLoaderProvider.CONTEXT_CLASS_LOADER, cd
                        .getClassLoaderName());
    }

    /**
     * Tests a tag execution when no class data is set. This should result in
     * undefined class descriptions.
     */
    @Test
    public void testProcessBeforeBodyNoClassData() throws JellyTagException
    {
        tag.processBeforeBody();
        assertNull("Key class desc is set", tag.getKeyClassDesc());
        assertNull("Value class desc is set", tag.getValueClassDesc());
    }

    /**
     * Tests whether an invalid class data object for the key class is detected.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyInvalidKeyClassData()
            throws JellyTagException
    {
        tag.setKeyClass(KEY_CLS);
        tag.setKeyClassName(VAL_CLS.getName());
        tag.processBeforeBody();
    }

    /**
     * Tests whether an invalid class data object for the value class is
     * detected.
     */
    @Test(expected = JellyTagException.class)
    public void testProcesBeforeBodyInvalidValueClassData()
            throws JellyTagException
    {
        tag.setValueClass(VAL_CLS);
        tag.setValueClassName(KEY_CLS.getName());
        tag.processBeforeBody();
    }

    /**
     * Creates a number of test dependencies and adds them as entries to the map
     * tag.
     *
     * @return an array with the dependencies (index 0 are key dependencies,
     *         index 1 value dependencies)
     */
    private Dependency[][] addDependencies()
    {
        Dependency[][] deps = new Dependency[2][DEP_COUNT];
        for (int i = 0; i < DEP_COUNT; i++)
        {
            deps[0][i] = EasyMock.createNiceMock(Dependency.class);
            deps[1][i] = EasyMock.createNiceMock(Dependency.class);
            tag.addEntry(deps[0][i], deps[1][i]);
        }
        return deps;
    }

    /**
     * Tests the given bean provider. We check whether the expected dependencies
     * are found and the ordered flag is correct.
     *
     * @param provider the provider to test
     * @param deps the dependencies
     * @param ordered the ordered flag
     */
    private void checkBeanProvider(BeanProvider provider, Dependency[][] deps,
            boolean ordered)
    {
        MapBeanProvider p = (MapBeanProvider) provider;
        assertEquals("Wrong ordered flag", ordered, p.isOrdered());
        int idx = 0;
        for (Dependency d : p.getKeyDependencies())
        {
            assertEquals("Wrong key dependency at " + idx, deps[0][idx++], d);
        }
        idx = 0;
        for (Dependency d : p.getValueDependencies())
        {
            assertEquals("Wrong key dependency at " + idx, deps[1][idx++], d);
        }
    }

    /**
     * Tests creating the map bean provider when ordered is false.
     */
    @Test
    public void testCreateBeanProviderNotOrdered() throws JellyTagException
    {
        runTag();
        Dependency[][] deps = addDependencies();
        checkBeanProvider(tag.createBeanProvider(), deps, false);
    }

    /**
     * Tests creating a map bean provider when ordered is true.
     */
    @Test
    public void testCreateObjectOrdered() throws JellyTagException
    {
        tag.setOrdered(true);
        runTag();
        Dependency[][] deps = addDependencies();
        checkBeanProvider(tag.createBeanProvider(), deps, true);
    }

    /**
     * Tests adding an entry without a key dependency.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddEntryNoKey()
    {
        runTag();
        tag.addEntry(null, EasyMock.createNiceMock(Dependency.class));
    }

    /**
     * Tests adding an entry without a value dependency.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddEntryKeyMapClassDesc()
    {
        runTag();
        tag.addEntry(EasyMock.createNiceMock(Dependency.class), null);
    }

    /**
     * Tests creating a properties tag.
     */
    @Test
    public void testInitProperties() throws JellyTagException
    {
        PropertiesTag ptag = new PropertiesTag();
        ptag.setParent(tag.getParent());
        ptag.setContext(tag.getContext());
        ptag.processBeforeBody();
        ClassDescription cdesc = ClassDescription.getInstance(String.class);
        assertEquals("Wrong key class desc", cdesc, ptag.getKeyClassDesc());
        assertEquals("Wrong value class desc", cdesc, ptag.getValueClassDesc());
    }
}
