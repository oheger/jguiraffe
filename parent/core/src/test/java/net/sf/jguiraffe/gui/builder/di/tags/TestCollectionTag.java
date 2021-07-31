/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import java.util.Collection;

import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.impl.ClassDescription;
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
 * Test class for CollectionTag.
 *
 * @author Oliver Heger
 * @version $Id: TestCollectionTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestCollectionTag
{
    /** Constant for a test collection element. */
    private static final Integer ELEM = 42;

    /** Constant for the dependency to the test element. */
    private static final Dependency ELEM_DEP = ConstantBeanProvider
            .getInstance(ELEM);

    /** Stores the builder data object. */
    private DIBuilderData builderData;

    /** The tag to be tested. */
    private CollectionTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new CollectionTagTestImpl();
        JellyContext ctx = new JellyContext();
        tag.setContext(ctx);
        tag.setParent(new SetPropertyTag());
        builderData = new DIBuilderData();
        builderData.put(ctx);
        builderData.setClassLoaderProvider(EasyMock
                .createNiceMock(ClassLoaderProvider.class));
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNotNull("No class desc data", tag.getElementClassData());
        assertFalse("Class desc data is defined", tag.getElementClassData()
                .isDefined());
        assertNull("Already a class description", tag.getElementClassDesc());
        assertTrue("Already dependencies set", tag.getElementDependencies()
                .isEmpty());
    }

    /**
     * Tests preparing the tag when no class desc data is defined.
     */
    @Test
    public void testProcessBeforeBodyNoElemDesc() throws JellyTagException
    {
        tag.processBeforeBody();
        assertNull("Got an element class description", tag
                .getElementClassDesc());
    }

    /**
     * Tests processBeforeBody() when the element class is set.
     */
    @Test
    public void testProcessBeforeBodyElemClass() throws JellyTagException
    {
        tag.setElementClass(getClass());
        tag.processBeforeBody();
        ClassDescription cd = tag.getElementClassDesc();
        assertEquals("Wrong element class", getClass(), cd
                .getTargetClass(EasyMock
                        .createNiceMock(ClassLoaderProvider.class)));
    }

    /**
     * Tests processBeforeBody() when the class name of the elements is set.
     */
    @Test
    public void testProcessBeforeBodyElemClassName() throws JellyTagException
    {
        tag.setElementClassName(getClass().getName());
        tag.setElementClassLoader(ClassLoaderProvider.CONTEXT_CLASS_LOADER);
        tag.processBeforeBody();
        ClassDescription cd = tag.getElementClassDesc();
        assertEquals("Wrong class name", getClass().getName(), cd
                .getTargetClassName());
        assertEquals("Wrong class loader",
                ClassLoaderProvider.CONTEXT_CLASS_LOADER, cd
                        .getClassLoaderName());
    }

    /**
     * Tests processBeforeBody() when the class data is invalid. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyInvalidClassData()
            throws JellyTagException
    {
        tag.setElementClass(getClass());
        tag.setElementClassName("AdifferentClassName");
        tag.processBeforeBody();
    }

    /**
     * Tests processBeforeBody() when this tag is not nested inside a dependency
     * tag.
     */
    @Test(expected = JellyTagException.class)
    public void testProcessBeforeBodyInvalidParent() throws JellyTagException
    {
        tag.setParent(new ConstructorInvocationTag());
        tag.processBeforeBody();
    }

    /**
     * Tests adding an element.
     */
    @Test
    public void testAddElement() throws JellyTagException
    {
        tag.processBeforeBody();
        tag.addElement(ELEM_DEP);
        Collection<Dependency> deps = tag.getElementDependencies();
        assertEquals("Wrong number of elements", 1, deps.size());
        assertTrue("Test element not found", deps.contains(ELEM_DEP));
    }

    /**
     * Tests adding a null dependency. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddElementNull()
    {
        tag.addElement(null);
    }

    /**
     * Tests whether the collection returned by getElementDependencies() can be
     * modified.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetElementDependenciesModify()
    {
        tag.addElement(ELEM_DEP);
        tag.getElementDependencies().clear();
    }

    /**
     * Tests a complete execution of the tag.
     */
    @Test
    public void testDoTag() throws JellyTagException
    {
        BeanProvider provider = EasyMock.createNiceMock(BeanProvider.class);
        tag.provider = provider;
        tag.setBody(new TagScript());
        tag.doTag(new XMLOutput());
        SetPropertyTag parent = (SetPropertyTag) tag.getParent();
        assertNotNull("No reference set", parent.getRefName());
        assertEquals("Bean provider not added", provider, builderData
                .getRootBeanStore().getBeanProvider(parent.getRefName()));
    }

    /**
     * A specialized implementation of CollectionTag that is easier to test.
     */
    private static class CollectionTagTestImpl extends CollectionTag
    {
        /** Stores the bean provider to be returned by createBeanProvider(). */
        BeanProvider provider;

        @Override
        protected BeanProvider createBeanProvider() throws JellyTagException
        {
            return provider;
        }
    }
}
