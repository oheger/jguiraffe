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
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.ClassDescription;
import net.sf.jguiraffe.di.impl.ClassDescriptionMock;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Tag;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ValueData.
 *
 * @author Oliver Heger
 * @version $Id: TestValueData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestValueData
{
    /** Constant for the test value. */
    private static final Integer VALUE = 42;

    /** Constant for the value class. */
    private static final Class<?> VAL_CLASS = VALUE.getClass();

    /** A mock for the dependency provider. */
    private DependencyProvider depProvider;

    /** Stores the owning tag (a mock). */
    private Tag owner;

    /** The object to be tested. */
    private ValueData data;

    @Before
    public void setUp() throws Exception
    {
        owner = EasyMock.createMock(Tag.class);
        data = new ValueData(owner);
    }

    /**
     * Creates a Jelly context and populates it with a DI builder data object.
     * The owner tag mock is prepared to expect a getContext() invocation.
     *
     * @return the context
     */
    private JellyContext setUpContext()
    {
        JellyContext context = new JellyContext();
        DIBuilderData data = new DIBuilderData();
        data.put(context);
        EasyMock.expect(owner.getContext()).andStubReturn(context);
        EasyMock.replay(owner);
        return context;
    }

    /**
     * Convenience method for obtaining the builder data object.
     *
     * @return the builder data object
     */
    private DIBuilderData getBuilderData()
    {
        return DIBuilderData.get(owner.getContext());
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
     * Tests a newly created object.
     */
    @Test
    public void testInit()
    {
        assertEquals("Wrong owner", owner, data.getOwner());
        assertNull("Object has a value", data.getValue());
        assertNotNull("No value class data", data.getValueClassData());
        assertFalse("Already defined", data.isValueDefined());
    }

    /**
     * Tests creating a tag without an owner. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoOwner()
    {
        new ValueData(null);
    }

    /**
     * Tests resolving a class description.
     */
    @Test
    public void testResolveClassDescription()
    {
        JellyContext context = setUpContext();
        DIBuilderData builderData = getBuilderData();
        ClassLoaderProvider clp =
                EasyMock.createNiceMock(ClassLoaderProvider.class);
        builderData.setClassLoaderProvider(clp);
        ClassDescriptionMock cd = new ClassDescriptionMock(VAL_CLASS);
        cd.expectClassLoaderProvider(clp);
        assertEquals("Wrong class resolved", VAL_CLASS,
                ValueData.resolveClassDescription(context, cd));
        cd.verify();
    }

    /**
     * Tests resolving a null class description.
     */
    @Test
    public void testResolveClassDescriptionNull()
    {
        assertNull("Wrong class for null class desc",
                ValueData.resolveClassDescription(setUpContext(), null));
    }

    /**
     * Tests resolving a ClassDescData object that is initialized.
     */
    @Test
    public void testResolveClassDescData() throws JellyTagException
    {
        JellyContext context = setUpContext();
        getBuilderData().setClassLoaderProvider(
                EasyMock.createNiceMock(ClassLoaderProvider.class));
        ClassDescData cd = new ClassDescData();
        cd.setTargetClass(VAL_CLASS);
        assertEquals("Wrong class resolved", VAL_CLASS,
                ValueData.resolveClassDescData(context, cd));
    }

    /**
     * Tests resolving an undefined ClassDescData object.
     */
    @Test
    public void testResolveClassDescDataUndefined() throws JellyTagException
    {
        JellyContext context = setUpContext();
        getBuilderData().setClassLoaderProvider(
                EasyMock.createNiceMock(ClassLoaderProvider.class));
        ClassDescData cd = new ClassDescData();
        assertNull("Got a class", ValueData.resolveClassDescData(context, cd));
    }

    /**
     * Tests resolving an invalid ClassDescData object. This should cause an
     * exception.
     */
    @Test(expected = JellyTagException.class)
    public void testResolveClassDescDataInvalid() throws JellyTagException
    {
        JellyContext context = setUpContext();
        getBuilderData().setClassLoaderProvider(
                EasyMock.createNiceMock(ClassLoaderProvider.class));
        ClassDescData cd = new ClassDescData();
        cd.setTargetClass(VAL_CLASS);
        cd.setTargetClassName(getClass().getName());
        ValueData.resolveClassDescData(context, cd);
    }

    /**
     * Tests setting the value class.
     */
    @Test
    public void testSetValueClass()
    {
        data.setValueClass(VAL_CLASS);
        assertEquals("Value class not set", VAL_CLASS, data.getValueClassData()
                .getTargetClass());
    }

    /**
     * Tests setting the value class name.
     */
    @Test
    public void testSetValueClassName()
    {
        final String clsName = VAL_CLASS.getName();
        data.setValueClassName(clsName);
        assertEquals("Value class name not set", clsName, data
                .getValueClassData().getTargetClassName());
    }

    /**
     * Tests setting the class loader name for the value class.
     */
    @Test
    public void testSetValueClassLoader()
    {
        final String loaderName = "myTestLoader";
        data.setValueClassLoader(loaderName);
        assertEquals("Class loader not set", loaderName, data
                .getValueClassData().getClassLoaderName());
    }

    /**
     * Tests the isValueDefined() method.
     */
    @Test
    public void testIsValueDefined()
    {
        data.setValue(VALUE);
        assertTrue("Not defined", data.isValueDefined());
    }

    /**
     * Tests whether isValueDefined() returns true if a null value was set.
     */
    @Test
    public void testIsValueDefinedSetNull()
    {
        data.setValue(null);
        assertTrue("Not defined", data.isValueDefined());
    }

    /**
     * Tests creating a value provider when no type conversion is required.
     */
    @Test
    public void testCreateValueProviderNoConvert() throws JellyTagException
    {
        setUpContext();
        data.setValue(VALUE);
        ConstantBeanProvider provider = data.createValueProvider();
        assertEquals("Wrong value", VALUE,
                provider.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a value provider when a type conversion is required.
     */
    @Test
    public void testCreateValueProviderConvert() throws JellyTagException
    {
        setUpContext();
        data.setValue(VALUE.toString());
        data.setValueClass(VAL_CLASS);
        ConstantBeanProvider provider = data.createValueProvider();
        assertEquals("Wrong value", VALUE,
                provider.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a value provider when no data is defined. This should
     * create a provider for a null value.
     */
    @Test
    public void testCreateValueProviderUndefined() throws JellyTagException
    {
        setUpContext();
        ConstantBeanProvider provider = data.createValueProvider();
        assertNull("Wrong bean", provider.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a value provider when a default class description is
     * specified that has to be used.
     */
    @Test
    public void testCreateValueProviderDefaultClassDesc()
            throws JellyTagException
    {
        setUpContext();
        data.setValue(VALUE.toString());
        ConstantBeanProvider provider =
                data.createValueProvider(ClassDescription
                        .getInstance(VAL_CLASS));
        assertEquals("Wrong value", VALUE,
                provider.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a value provider when both a default and a specific class
     * description are defined.
     */
    @Test
    public void testCreateValueProviderOwnAndDefaultClassDesc()
            throws JellyTagException
    {
        setUpContext();
        data.setValue(VALUE.toString());
        data.setValueClass(VAL_CLASS);
        ClassDescription defDescr = ClassDescription.getInstance(String.class);
        ConstantBeanProvider provider = data.createValueProvider(defDescr);
        assertEquals("Wrong value", VALUE,
                provider.getBean(getDependencyProvider()));
    }
}
