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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.TagScript;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for UseBeanBaseTag.
 *
 * @author Oliver Heger
 * @version $Id: TestUseBeanBaseTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestUseBeanBaseTag
{
    /** Constant for the class attribute. */
    private static final String ATTR_CLASS = "class";

    /** Constant for the var attribute. */
    private static final String ATTR_VAR = "var";

    /** Constant for the bean class to be created. */
    private static final Class<?> BEAN_CLASS = Object.class;

    /** Constant for a (faked) test bean. */
    private static final String TEST_BEAN = "A test bean";

    /** Constant for the name of the reference to the test bean. */
    private static final String REF_NAME = "reference";

    /** Stores the object to be tested. */
    private UseBeanBaseTagTestImpl tag;

    /** A map with attributes. */
    private Map<String, Object> attributes;

    /** A dummy XMLOutput object. */
    private XMLOutput output;

    @Before
    public void setUp() throws Exception
    {
        tag = createTag(null);
        ComponentBuilderData data = new ComponentBuilderData();
        data.put(tag.getContext());
        tag.setAttribute(ATTR_CLASS, BEAN_CLASS);
        attributes = new HashMap<String, Object>();
        attributes.put(ATTR_CLASS, BEAN_CLASS);
        output = new XMLOutput();
    }

    /**
     * Creates the test tag.
     *
     * @param defCls the default class
     * @return the new test tag instance
     */
    private UseBeanBaseTagTestImpl createTag(Class<?> defCls)
            throws JellyTagException
    {
        JellyContext ctx = new JellyContext();
        if (defCls == null)
        {
            tag = new UseBeanBaseTagTestImpl();
        }
        else
        {
            tag = new UseBeanBaseTagTestImpl(defCls);
        }
        tag.setContext(ctx);
        return tag;
    }

    /**
     * Tests creating a new bean instance the default way.
     */
    @Test
    public void testNewInstanceDefault() throws JellyTagException
    {
        tag.setBody(new TagScript());
        tag.doTag(output);
        assertEquals("Incorrect default bean created", Object.class, tag.bean
                .getClass());
    }

    /**
     * Tests if the passResults() method is invoked on processing the bean.
     */
    @Test
    public void testProcessBean() throws JellyTagException
    {
        tag.processBean(null, TEST_BEAN);
        assertEquals("passResults() was not called", TEST_BEAN, tag.bean);
    }

    /**
     * Tests whether the base class is evaluated. The correct base class is set.
     */
    @Test
    public void testCheckBaseClass() throws JellyTagException
    {
        tag.setBaseClass(Object.class);
        tag.processBean(null, new Object());
    }

    /**
     * Tests if an invalid bean class is detected.
     */
    @Test(expected = JellyTagException.class)
    public void testCheckBaseClassInvalid() throws JellyTagException
    {
        tag.setBaseClass(getClass());
        tag.processBean(null, new Object());
    }

    /**
     * Tests accessing an existing bean by reference.
     */
    @Test
    public void testUseExistingBean() throws JellyTagException
    {
        prepareExistingBeanTest();
        tag.doTag(output);
        assertEquals("Bean was not fetched", TEST_BEAN, tag.bean);
    }

    /**
     * Tests accessing an existing bean when the base class is invalid. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testUseExistingBeanInvalidBaseClass() throws JellyTagException
    {
        prepareExistingBeanTest();
        tag.setBaseClass(StringBuffer.class); // set a different base class
        tag.doTag(output);
    }

    /**
     * Tests whether the var attribute is evaluated if an existing bean is used.
     */
    @Test
    public void testUseExistingBeanWithVarAttribute() throws JellyTagException
    {
        prepareExistingBeanTest();
        final String variable = "myTestVar";
        tag.setAttribute("var", variable);
        tag.doTag(output);
        assertEquals("Bean not stored as variable", TEST_BEAN, tag.getContext()
                .findVariable(variable));
    }

    /**
     * Tests a reference to a non existing bean. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testUseExistingBeanNonExistingRef() throws JellyTagException
    {
        prepareExistingBeanTest();
        tag.getContext().setVariable(REF_NAME, null);
        tag.doTag(output);
    }

    /**
     * Helper method for preparing a test for accessing an existing variable.
     */
    private void prepareExistingBeanTest()
    {
        tag.getContext().setVariable(REF_NAME, TEST_BEAN);
        tag.setAttribute(UseBeanBaseTag.ATTR_REF, REF_NAME);
        tag.setAttribute(ATTR_CLASS, null);
        tag.setBody(new TagScript());
    }

    /**
     * Tests the default optional flag. This should be false.
     */
    @Test
    public void testIsOptionalDefault()
    {
        assertFalse("Tag is optional", tag.isOptional());
    }

    /**
     * Tests behavior of optional tags when no class attribute is specified.
     */
    @Test
    public void testOptionalBean() throws JellyTagException
    {
        tag.setAttribute(ATTR_CLASS, null);
        tag.optional = Boolean.TRUE;
        tag.bean = "A bean";
        tag.doTag(output);
        assertNull("passResults() was not called", tag.bean);
    }

    /**
     * Tests a tag with too many bean definition attributes set. This should
     * cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testTooManyBeanDefinitions() throws JellyTagException
    {
        tag.setAttribute(ATTR_CLASS, getClass());
        tag.setAttribute(UseBeanBaseTag.ATTR_REF, "test");
        tag.doTag(output);
    }

    /**
     * Tests whether the beanName attribute is correctly handled.
     */
    @Test
    public void testBeanName() throws JellyTagException
    {
        BeanContext bc = prepareDITest();
        EasyMock.expect(bc.getBean(REF_NAME)).andReturn(TEST_BEAN);
        EasyMock.replay(bc);
        tag.setAttribute(UseBeanBaseTag.ATTR_BEAN_NAME, REF_NAME);
        tag.doTag(output);
        assertEquals("Wrong bean returned", TEST_BEAN, tag.bean);
        EasyMock.verify(bc);
    }

    /**
     * Tests whether the beanClass attribute is correctly handled.
     */
    @Test
    public void testBeanClass() throws JellyTagException
    {
        BeanContext bc = prepareDITest();
        bc.getBean(TEST_BEAN.getClass());
        EasyMock.expectLastCall().andReturn(TEST_BEAN);
        EasyMock.replay(bc);
        tag.setAttribute(UseBeanBaseTag.ATTR_BEAN_CLASS, TEST_BEAN.getClass());
        tag.doTag(output);
        assertEquals("Wrong bean returned", TEST_BEAN, tag.bean);
        EasyMock.verify(bc);
    }

    /**
     * Prepares a test using the DI framework.
     *
     * @return a mock for the bean context
     */
    private BeanContext prepareDITest()
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        ComponentBuilderData builderData = FormBaseTag.getBuilderData(tag
                .getContext());
        builderData.setBeanContext(bc);
        tag.setBody(new TagScript());
        tag.setAttribute(ATTR_CLASS, null);
        return bc;
    }

    /**
     * Tests the tag when no bean is defined. This should cause an exception.
     */
    @Test(expected = MissingAttributeException.class)
    public void testNoBeanDefinition() throws JellyTagException
    {
        tag.setAttribute(ATTR_CLASS, null);
        tag.doTag(output);
    }

    /**
     * Tests the tag when no bean definition is defined, but a default class is
     * provided. This is okay.
     */
    @Test
    public void testNoBeanDefinitionDefClass() throws JellyTagException
    {
        tag = createTag(getClass());
        tag.setAttribute(ATTR_CLASS, null);
        tag.setBody(new TagScript());
        tag.doTag(output);
        assertEquals("Wrong class created", getClass(), tag.bean.getClass());
    }

    /**
     * Tests a tag that creates a new bean and does not define a target. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testNoTargetCreateBean() throws JellyTagException
    {
        tag.passResultsFlag = false;
        tag.setBody(new TagScript());
        tag.doTag(output);
    }

    /**
     * Tests a tag that creates a new bean when passResults() returns false, but
     * a var attribute is defined.
     */
    @Test
    public void testNoTargetCreateBeanWithVar() throws JellyTagException
    {
        tag.passResultsFlag = false;
        tag.setAttribute(ATTR_VAR, REF_NAME);
        tag.setBody(new TagScript());
        tag.doTag(output);
        assertNotNull("No bean was stored", tag.getContext().getVariable(
                REF_NAME));
    }

    /**
     * Tests a tag that uses an existing bean and does not define a target. This
     * should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testNoTargetExistingBean() throws JellyTagException
    {
        prepareExistingBeanTest();
        tag.passResultsFlag = false;
        tag.doTag(output);
    }

    /**
     * Tests a tag that uses an existing bean when passResults() returns false,
     * but a var attribute is defined.
     */
    @Test
    public void testNoTargetExistingBeanWithVar() throws JellyTagException
    {
        prepareExistingBeanTest();
        tag.passResultsFlag = false;
        tag.setAttribute(ATTR_VAR, "myvar");
        tag.doTag(output);
        assertEquals("Reference not stored", TEST_BEAN, tag.getContext()
                .getVariable("myvar"));
    }

    /**
     * Tests setting a property.
     */
    @Test
    public void testSetProperty()
    {
        tag.setProperty(REF_NAME, TEST_BEAN);
        assertEquals("Attribute not set from property", TEST_BEAN, tag
                .getAdditionalProperties().get(REF_NAME));
    }

    /**
     * A test implementation of the use bean tag that allows easy access to the
     * created/obtained bean.
     */
    static class UseBeanBaseTagTestImpl extends UseBeanBaseTag
    {
        /** Stores the processed bean. */
        Object bean;

        /** The optional flag. */
        Boolean optional;

        /** The return value for passResults(). */
        boolean passResultsFlag = true;

        public UseBeanBaseTagTestImpl()
        {
            super();
        }

        public UseBeanBaseTagTestImpl(Class<?> defaultClass)
        {
            super(defaultClass);
        }

        @Override
        protected boolean passResults(Object bean) throws JellyTagException
        {
            this.bean = bean;
            return passResultsFlag;
        }

        /**
         * Allows to mark this tag as optional. If an optional flag is set, this
         * value is returned. Otherwise the inherited implementation is called.
         *
         * @return the optional flag
         */
        @Override
        protected boolean isOptional()
        {
            return (optional != null) ? optional.booleanValue() : super
                    .isOptional();
        }
    }
}
