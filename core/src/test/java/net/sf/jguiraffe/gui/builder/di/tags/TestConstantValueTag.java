/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.ReflectionTestClass;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ConstantValueTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestConstantValueTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConstantValueTag
{
    /** Constant for the class used by the tests. */
    private static final Class<?> TEST_CLASS = ReflectionTestClass.class;

    /** Constant for the name of a test class loader. */
    private static final String LOADER = "testClassLoader";

    /** Constant for the test value of the constant field. */
    private static final Object VALUE = "a test value";

    /** Constant for the name of a test variable. */
    private static final String VAR = "testVar";

    /** Constant for the name of the test field to be read. */
    private static final String FIELD = "ANSWER";

    /** The class loader provider mock. */
    private ClassLoaderProvider classLoaderProvider;

    /** The Jelly context. */
    private JellyContext context;

    /** The tag to be tested. */
    private ConstantValueTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        classLoaderProvider = EasyMock
                .createNiceMock(ClassLoaderProvider.class);
        context = new JellyContext();
        DIBuilderData builderData = new DIBuilderData();
        builderData.setClassLoaderProvider(classLoaderProvider);
        builderData.put(context);
        tag = new ConstantValueTagTestImpl();
        tag.setContext(context);
    }

    /**
     * Tests whether the properties of the class description data object are
     * correctly filled.
     */
    @Test
    public void testPopulateClassDescData()
    {
        tag.setTargetClass(TEST_CLASS);
        tag.setTargetClassName(TEST_CLASS.getName());
        tag.setTargetClassLoader(LOADER);
        ClassDescData cdd = tag.getClassDescData();
        assertEquals("Wrong target class", TEST_CLASS, cdd.getTargetClass());
        assertEquals("Wrong target class name", TEST_CLASS.getName(), cdd
                .getTargetClassName());
        assertEquals("Wrong class loader", LOADER, cdd.getClassLoaderName());
    }

    /**
     * Tests whether a constant value can be resolved successfully.
     */
    @Test
    public void testResolveConstantValue() throws JellyTagException
    {
        tag.setField(FIELD);
        assertEquals("Wrong value", ReflectionTestClass.ANSWER, tag
                .resolveConstantValue(TEST_CLASS));
    }

    /**
     * Tests resolveConstantValue() if the field does not exist.
     */
    @Test(expected = JellyTagException.class)
    public void testResolveConstantValueNonExistingField()
            throws JellyTagException
    {
        tag.setField("non existing field!");
        tag.resolveConstantValue(TEST_CLASS);
    }

    /**
     * Tests resolveConstantValue() if the field has no public access.
     */
    @Test(expected = JellyTagException.class)
    public void testResolveConstantValueNoAccess() throws JellyTagException
    {
        tag.setField("SECRET_ANSWER");
        tag.resolveConstantValue(TEST_CLASS);
    }

    /**
     * Tests a successful execution of the tag if the result is stored in a
     * variable.
     */
    @Test
    public void testDoTagVar() throws JellyTagException
    {
        tag.setField(FIELD);
        tag.setVar(VAR);
        tag.mockClassDescData();
        tag.setMockResolveValue(true);
        tag.doTag(new XMLOutput());
        assertEquals("Wrong value", VALUE, context.getVariable(VAR));
    }

    /**
     * Tests a successful execution of the tag if the result is passed to the
     * parent tag.
     */
    @Test
    public void testDoTagParent() throws JellyTagException
    {
        ValueSupportTag parent = EasyMock.createMock(ValueSupportTag.class);
        parent.setValue(VALUE);
        EasyMock.replay(parent);
        tag.setParent(parent);
        tag.setField(FIELD);
        tag.mockClassDescData();
        tag.setMockResolveValue(true);
        tag.doTag(new XMLOutput());
        EasyMock.verify(parent);
    }

    /**
     * Tests whether a missing field attribute is detected.
     */
    @Test
    public void testDoTagNoField() throws JellyTagException
    {
        tag.setVar(VAR);
        tag.setTargetClass(TEST_CLASS);
        try
        {
            tag.doTag(new XMLOutput());
            fail("Missing field attribute not detected!");
        }
        catch (MissingAttributeException mex)
        {
            assertEquals("Wrong attribute", "field", mex.getMissingAttribute());
        }
    }

    /**
     * Tests whether a missing target for the resolved value is detected.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagNoVar() throws JellyTagException
    {
        tag.setField(FIELD);
        tag.setTargetClass(TEST_CLASS);
        tag.doTag(new XMLOutput());
    }

    /**
     * Tests whether a missing specification of the target class is detected.
     */
    @Test(expected = JellyTagException.class)
    public void testDoTagNoClass() throws JellyTagException
    {
        tag.setField(FIELD);
        tag.setVar(VAR);
        tag.doTag(new XMLOutput());
    }

    /**
     * A test implementation of ConstantValueTag that provides some mocking
     * facilities.
     */
    private class ConstantValueTagTestImpl extends ConstantValueTag
    {
        /** The mock class description data to return by getClassDescData(). */
        private ClassDescData mockClassDescData;

        /** A flag whether the resolving of the value should be mocked. */
        private boolean mockResolveValue;

        /**
         * Initializes a mock for the class description data.
         */
        public void mockClassDescData()
        {
            mockClassDescData =
                    new MockClassDescData(classLoaderProvider, TEST_CLASS);
        }

        /**
         * Returns a flag whether resolving of the value should be mocked.
         *
         * @return a flag whether value resolving should be mocked
         */
        public boolean isMockResolveValue()
        {
            return mockResolveValue;
        }

        /**
         * Sets a flag whether resolving of the value should be mocked.
         *
         * @param mockResolveValue the mock resolve value flag
         */
        public void setMockResolveValue(boolean mockResolveValue)
        {
            this.mockResolveValue = mockResolveValue;
        }

        /**
         * {@inheritDoc} Optionally mocks this method. If mocking is enabled,
         * the target class is checked, and the test value is returned.
         */
        @Override
        protected Object resolveConstantValue(Class<?> targetClass)
                throws JellyTagException
        {
            if (isMockResolveValue())
            {
                assertEquals("Wrong target class", TEST_CLASS, targetClass);
                return VALUE;
            }
            return super.resolveConstantValue(targetClass);
        }

        /**
         * {@inheritDoc} Either returns the mock description data or calls the
         * super method.
         */
        @Override
        ClassDescData getClassDescData()
        {
            return (mockClassDescData != null) ? mockClassDescData : super
                    .getClassDescData();
        }
    }

    /**
     * A combined interface needed for creating appropriate mock objects.
     */
    private static interface ValueSupportTag extends ValueSupport, Tag
    {
    }
}
