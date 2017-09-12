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
package net.sf.jguiraffe.gui.builder.di.tags;

import static org.junit.Assert.assertEquals;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.NameDependency;
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
 * Test class for ElementTag.
 *
 * @author Oliver Heger
 * @version $Id: TestElementTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestElementTag
{
    /** Constant for the value of the test element. */
    private static final Object TEST_ELEM = 42;

    /** Stores the parent tag. */
    private CollectionTagAddTestImpl colTag;

    /** The test output object. */
    private XMLOutput output;

    /** The tag to be tested. */
    private ElementTag tag;

    @Before
    public void setUp() throws Exception
    {
        tag = new ElementTag();
        colTag = new CollectionTagAddTestImpl();
        JellyContext context = new JellyContext();
        tag.setContext(context);
        colTag.setContext(context);
        tag.setParent(colTag);
        tag.setBody(new TagScript());
        output = new XMLOutput();
        DIBuilderData builderData = new DIBuilderData();
        builderData.setClassLoaderProvider(EasyMock
                .createNiceMock(ClassLoaderProvider.class));
        builderData.put(context);
    }

    /**
     * Tests executing the tag when the value class is defined.
     */
    @Test
    public void testDoTagWithClassDesc() throws JellyTagException
    {
        tag.setValue(TEST_ELEM.toString());
        tag.setValueClass(Integer.class);
        tag.doTag(output);
        colTag.verifyValue(TEST_ELEM);
    }

    /**
     * Tests executing the tag when no value class is defined.
     */
    @Test
    public void testDoTagNoClassDesc() throws JellyTagException
    {
        tag.setValue(TEST_ELEM.toString());
        tag.doTag(output);
        colTag.verifyValue(TEST_ELEM.toString());
    }

    /**
     * Tests executing the tag when the class description has to be fetched from
     * the parent.
     */
    @Test
    public void testDoTagClassDescFromParent() throws JellyTagException
    {
        ClassDescription cdesc = ClassDescription.getInstance(TEST_ELEM
                .getClass());
        colTag.setElementClassDesc(cdesc);
        tag.setValue(TEST_ELEM.toString());
        tag.doTag(output);
        colTag.verifyValue(TEST_ELEM);
    }

    /**
     * Tests whether the class description defined for the parent tag is
     * overridden form the description set directly.
     */
    @Test
    public void testDoTagClassDescOverride() throws JellyTagException
    {
        colTag.setElementClassDesc(ClassDescription.getInstance(String.class));
        tag.setValue(TEST_ELEM.toString());
        tag.setValueClass(TEST_ELEM.getClass());
        tag.doTag(output);
        colTag.verifyValue(TEST_ELEM);
    }

    /**
     * Tests executing the tag when no value is defined. We allow null values in
     * collections, so this is legal.
     */
    @Test
    public void testDoTagNoValue() throws JellyTagException
    {
        tag.doTag(output);
        colTag.verifyValue(null);
    }

    /**
     * Tests executing a class with an invalid class description.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidClassDesc() throws JellyTagException
    {
        tag.setValue(TEST_ELEM);
        tag.setValueClass(Integer.class);
        tag.setValueClassName(getClass().getName());
        tag.doTag(output);
    }

    /**
     * Tests executing a tag when the parent tag is no collection tag. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagInvalidParent() throws JellyTagException
    {
        tag.setParent(new ConstructorTag());
        tag.setValue(TEST_ELEM);
        tag.doTag(output);
    }

    /**
     * Tests executing the tag when a reference dependency is set.
     */
    @Test
    public void testDoTagReference() throws JellyTagException
    {
        final String refName = "myRef";
        tag.setRefName(refName);
        tag.doTag(output);
        NameDependency dep = NameDependency.getInstance(refName);
        colTag.verify(dep);
    }

    /**
     * Tests executing the tag when more than one reference is set. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagMultipleReferences() throws JellyTagException
    {
        tag.setValue(TEST_ELEM);
        tag.setRefName("testRef");
        tag.doTag(output);
    }

    /**
     * A dummy collection tag, which is used for testing whether the correct
     * elements are added.
     */
    private static class CollectionTagAddTestImpl extends CollectionTag
    {
        /** Stores the added dependency. */
        private Dependency element;

        @Override
        protected BeanProvider createBeanProvider()
        {
            // just a dummy, this method won't be called
            return null;
        }

        /**
         * Records the passed in parameters.
         */
        @Override
        public void addElement(Dependency elemDep)
        {
            element = elemDep;
        }

        /**
         * Verifies that the correct dependency was passed to addElement().
         *
         * @param expDep the expected dependency
         */
        public void verify(Dependency expDep)
        {
            assertEquals("Wrong dependency", expDep, element);
        }

        /**
         * Verifies that the correct value dependency was passed to
         * addElement().
         *
         * @param value the expected value
         */
        public void verifyValue(Object value)
        {
            DependencyProvider depProvider =
                    EasyMock.createMock(DependencyProvider.class);
            EasyMock.expect(depProvider.getInvocationHelper())
                    .andReturn(new InvocationHelper()).anyTimes();
            EasyMock.replay(depProvider);
            ConstantBeanProvider provider = (ConstantBeanProvider) element;
            assertEquals("Wrong value", value, provider.getBean(depProvider));
        }
    }
}
