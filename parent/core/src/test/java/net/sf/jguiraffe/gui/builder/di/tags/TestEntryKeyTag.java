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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
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
 * Test class for EntryKeyTag.
 *
 * @author Oliver Heger
 * @version $Id: TestEntryKeyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestEntryKeyTag
{
    /** Constant for the key class. */
    private static final Class<?> KEY_CLS = Integer.class;

    /** Constant for the test key value. */
    private static final Integer KEY = 42;

    /** The parent tag. */
    private EntryTag parent;

    /** The output object. */
    private XMLOutput output;

    /** The tag to be tested. */
    private EntryKeyTag tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        tag = new EntryKeyTag();
        tag.setContext(context);
        tag.setBody(new TagScript());
        parent = new EntryTag();
        parent.setContext(context);
        tag.setParent(parent);
        DIBuilderData builderData = new DIBuilderData();
        builderData.setClassLoaderProvider(EasyMock
                .createNiceMock(ClassLoaderProvider.class));
        builderData.put(context);
        output = new XMLOutput();
    }

    /**
     * Tests whether the expected dependency was set for the parent.
     */
    private void checkKeyDependency()
    {
        ConstantBeanProvider dep =
                (ConstantBeanProvider) parent.getKeyDependency();
        DependencyProvider depProvider =
                EasyMock.createMock(DependencyProvider.class);
        EasyMock.expect(depProvider.getInvocationHelper())
                .andReturn(new InvocationHelper()).anyTimes();
        EasyMock.replay(depProvider);
        assertEquals("Wrong bean for parent dependency", KEY,
                dep.getBean(depProvider));
    }

    /**
     * Tests whether a value provider is correctly resolved.
     */
    @Test
    public void testDoTagValueNoConvert() throws JellyTagException
    {
        tag.setValue(KEY);
        tag.doTag(output);
        checkKeyDependency();
    }

    /**
     * Tests whether a value provider is correctly resolved when a type
     * conversion is required.
     */
    @Test
    public void testDoTagValueConvert() throws JellyTagException
    {
        tag.setValue(KEY.toString());
        tag.setValueClass(KEY_CLS);
        tag.doTag(output);
        checkKeyDependency();
    }

    /**
     * Tests a tag with an invalid class description. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testValueClassInvalid() throws JellyTagException
    {
        tag.setValueClass(KEY_CLS);
        tag.setValueClassName(getClass().getName());
        tag.setValue(KEY);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when no value is defined. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagNoValue() throws JellyTagException
    {
        tag.doTag(output);
    }

    /**
     * Tests executing a tag with multiple dependencies defined. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagMultipleValues() throws JellyTagException
    {
        tag.setValue(KEY);
        tag.setRefName("aRef");
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when the parent is no entry tag. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidParent() throws JellyTagException
    {
        tag.setValue(KEY);
        tag.setParent(new ConstructorInvocationTag());
        tag.doTag(output);
    }

    /**
     * Tests a parent tag that already has a value. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagParentAlreadyDefined() throws JellyTagException
    {
        parent.setKeyDependency(EasyMock.createNiceMock(Dependency.class));
        tag.setValue(KEY);
        tag.doTag(output);
    }
}
