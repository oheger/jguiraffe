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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for EntryTag.
 *
 * @author Oliver Heger
 * @version $Id: TestEntryTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEntryTag
{
    /** Constant for the key. */
    private static final Integer KEY = 42;

    /** Constant for the value. */
    private static final Long VALUE = 1000L;

    /** Constant for the key class. */
    private static final Class<?> KEY_CLS = KEY.getClass();

    /** Constant for the value class. */
    private static final Class<?> VALUE_CLS = VALUE.getClass();

    /** The output object. */
    private XMLOutput output;

    /** The builder data object. */
    private DIBuilderData builderData;

    /** A mock for the dependency provider. */
    private DependencyProvider depProvider;

    /** The parent tag of the test tag. */
    private MapTagTestImpl parent;

    /** The tag to be tested. */
    private EntryTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new EntryTag();
        tag.setContext(context);
        tag.setBody(new TagScript());
        parent = new MapTagTestImpl();
        parent.setContext(context);
        parent.setParent(new SetPropertyTag());
        tag.setParent(parent);
        output = new XMLOutput();
        builderData = new DIBuilderData();
        builderData.put(context);
    }

    /**
     * Returns the mock object for the dependency provider. It is created on
     * first access.
     *
     * @return the dependency provider mock
     */
    private DependencyProvider getDependencyProvider()
    {
        if (depProvider == null)
        {
            depProvider = EasyMock.createMock(DependencyProvider.class);
            EasyMock.expect(depProvider.getInvocationHelper())
                    .andReturn(new InvocationHelper()).anyTimes();
            EasyMock.replay(depProvider);
        }
        return depProvider;
    }

    /**
     * Helper method for extracting the value from a dependency. This method
     * assumes that the dependency is a constant value. It performs a cast and
     * extracts the value.
     *
     * @param dep the dependency
     * @return the value of this dependency
     */
    private Object valueOf(Dependency dep)
    {
        return ((ConstantBeanProvider) dep).getBean(getDependencyProvider());
    }

    /**
     * Helper method for checking whether addEntry() was called correctly.
     *
     * @param key the expected key object
     * @param val the expected value object
     */
    private void checkEntry(Object key, Object val)
    {
        assertEquals("Wrong key", key, valueOf(parent.keyDep));
        assertEquals("Wrong value", val, valueOf(parent.valueDep));
    }

    /**
     * Tests a newly created tag.
     */
    @Test
    public void testInit()
    {
        assertNotNull("No key value data", tag.getKeyData());
        assertFalse("Key already defined", tag.getKeyData().isValueDefined());
        assertNotNull("No value class data", tag.getValueData()
                .getValueClassData());
        assertFalse("Value class already defined", tag.getValueData()
                .getValueClassData().isDefined());
        assertNull("Value is set", tag.getValueData().getValue());
        assertNull("Key dependency is set", tag.getKeyDependency());
    }

    /**
     * Tests executing the tag when no class descriptions are specified.
     */
    @Test
    public void testDoTagNoDesc() throws JellyTagException
    {
        tag.setKey(KEY);
        tag.setValue(VALUE);
        tag.doTag(output);
        assertNotNull("No key dependency set", tag.getKeyDependency());
        checkEntry(KEY, VALUE);
    }

    /**
     * Tests executing the tag when class descriptions are defined by class.
     */
    @Test
    public void testDoTagClsDescClass() throws JellyTagException
    {
        tag.setKey(KEY.toString());
        tag.setValue(VALUE.toString());
        tag.setKeyClass(KEY_CLS);
        tag.setValueClass(VALUE_CLS);
        tag.doTag(output);
        checkEntry(KEY, VALUE);
    }

    /**
     * Tests executing the tag when class descriptions are defined by class
     * name.
     */
    @Test
    public void testDoTagClsDescClassName() throws JellyTagException
    {
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        final String keyClsLoader = "keyLoader";
        final String valClsLoader = "valueLoader";
        clp.loadClass(KEY_CLS.getName(), keyClsLoader);
        EasyMock.expectLastCall().andReturn(KEY_CLS);
        clp.loadClass(VALUE_CLS.getName(), valClsLoader);
        EasyMock.expectLastCall().andReturn(VALUE_CLS);
        EasyMock.replay(clp);
        builderData.setClassLoaderProvider(clp);
        tag.setKey(KEY.toString());
        tag.setValue(VALUE.toString());
        tag.setKeyClassName(KEY_CLS.getName());
        tag.setValueClassName(VALUE_CLS.getName());
        tag.setKeyClassLoader("keyLoader");
        tag.setValueClassLoader("valueLoader");
        tag.doTag(output);
        checkEntry(KEY, VALUE);
        EasyMock.verify(clp);
    }

    /**
     * Tests executing the tag when the parent tag is invalid.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidParent() throws JellyTagException
    {
        tag.setParent(new ConstructorInvocationTag());
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when the class description for the key is
     * invalid.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidKeyClassDesc() throws JellyTagException
    {
        tag.setKeyClass(KEY_CLS);
        tag.setKeyClassName(VALUE_CLS.getName());
        tag.setKey(KEY);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when the class description for the value is
     * invalid.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidValueClassDesc() throws JellyTagException
    {
        tag.setValueClass(VALUE_CLS);
        tag.setValueClassName(KEY_CLS.getName());
        tag.setValue(VALUE);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when neither a key nor a value is defined. We
     * allow null values, so this is legal.
     */
    @Test
    public void testDoTagNullValues() throws JellyTagException
    {
        tag.doTag(output);
        checkEntry(null, null);
    }

    /**
     * Tests whether the class description of the map tag is used when no
     * description is provided at the entry level.
     */
    @Test
    public void testDoTagClassDescFromMap() throws JellyTagException
    {
        parent.setKeyClass(KEY_CLS);
        parent.setValueClass(VALUE_CLS);
        parent.processBeforeBody();
        tag.setKey(KEY.toString());
        tag.setValue(VALUE.toString());
        tag.doTag(output);
        checkEntry(KEY, VALUE);
    }

    /**
     * Tests whether a class description specified at the entry level takes
     * precedence over one of the map level.
     */
    @Test
    public void testDoTagClassDescFromEntry() throws JellyTagException
    {
        parent.setKeyClass(String.class);
        parent.setValueClass(String.class);
        parent.processBeforeBody();
        tag.setKey(KEY.toString());
        tag.setValue(VALUE.toString());
        tag.setKeyClass(KEY_CLS);
        tag.setValueClass(VALUE_CLS);
        tag.doTag(output);
        checkEntry(KEY, VALUE);
    }

    /**
     * A test implementation of MapTag. This implementation overrides the
     * addEntry() method to record the passed in parameters. This way it can be
     * checked whether this method is called correctly.
     */
    private static class MapTagTestImpl extends MapTag
    {
        /** The passed in key dependency. */
        Dependency keyDep;

        /** The passed in value dependency. */
        Dependency valueDep;

        @Override
        public void addEntry(Dependency k, Dependency v)
        {
            keyDep = k;
            valueDep = v;
        }
    }
}
