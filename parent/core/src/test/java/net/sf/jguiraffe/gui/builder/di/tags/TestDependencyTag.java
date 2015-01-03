/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.Dependency;
import net.sf.jguiraffe.di.DependencyProvider;
import net.sf.jguiraffe.di.InvocationHelper;
import net.sf.jguiraffe.di.impl.ChainedInvocation;
import net.sf.jguiraffe.di.impl.ClassDependency;
import net.sf.jguiraffe.di.impl.DefaultClassLoaderProvider;
import net.sf.jguiraffe.di.impl.NameDependency;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for DependencyTag.
 *
 * @author Oliver Heger
 * @version $Id: TestDependencyTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestDependencyTag
{
    /** Constant for a symbolic class loader name used by some tests. */
    private static final String LOADER_NAME = "myClassLoader";

    /** Stores the current Jelly context. */
    private JellyContext context;

    /** A mock for a dependency provider. */
    private DependencyProvider depProvider;

    /** Stores the tag to be tested. */
    private DependencyTag tag;

    @Before
    public void setUp() throws Exception
    {
        context = new JellyContext();
        tag = new DependencyTagTestImpl();
        tag.setContext(context);
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
     * Tests creating a named dependency.
     */
    @Test
    public void testCreateNameDependency() throws JellyTagException
    {
        final String depName = "myDependency";
        tag.setRefName(depName);
        NameDependency dep = (NameDependency) tag.createDependency();
        assertEquals("Wrong name set", depName, dep.getName());
    }

    /**
     * Tests creating a dependency to a class.
     */
    @Test
    public void testCreateClassDependency() throws JellyTagException
    {
        ClassLoaderProvider clp = EasyMock
                .createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        tag.setRefClass(getClass());
        ClassDependency cdep = (ClassDependency) tag.createDependency();
        assertEquals("Wrong class dependency", getClass(), cdep
                .getDependentClass().getTargetClass(clp));
        EasyMock.verify(clp);
    }

    /**
     * Tests creating a class dependency when the class name is specified.
     */
    @Test
    public void testCreateClassDependencyClsName() throws JellyTagException
    {
        tag.setRefClassName(getClass().getName());
        tag.setRefClassLoader(LOADER_NAME);
        ClassDependency cdep = (ClassDependency) tag.createDependency();
        assertEquals("Wrong class name", getClass().getName(), cdep
                .getDependentClass().getTargetClassName());
        assertEquals("Wrong class loader", LOADER_NAME, cdep
                .getDependentClass().getClassLoaderName());
    }

    /**
     * Tries to create a class dependency when ambiguous data is provided.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateClassDependencyAmbiguous() throws JellyTagException
    {
        tag.setRefClassName(getClass().getName());
        tag.setRefClass(DependencyTag.class);
        tag.createDependency();
    }

    /**
     * Tests creating a constant dependency to a value.
     */
    @Test
    public void testCreateValueDependency() throws JellyTagException
    {
        final Object value = "myValue";
        tag.setValue(value);
        ConstantBeanProvider dep =
                (ConstantBeanProvider) tag.createDependency();
        assertEquals("Wrong constant value", value,
                dep.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a constant dependency to a value including type
     * conversion.
     */
    @Test
    public void testCreateValueDependencyConvert() throws JellyTagException
    {
        setUpBuilderData();
        final Integer value = 42;
        tag.setValue(String.valueOf(value));
        tag.setValueClass(Integer.class);
        ConstantBeanProvider dep =
                (ConstantBeanProvider) tag.createDependency();
        assertEquals("Wrong constant value", value,
                dep.getBean(getDependencyProvider()));
    }

    /**
     * Tests creating a constant dependency with type conversion when the target
     * class is specified by name.
     */
    @Test
    public void testCreateValueDependencyConvertClsName()
            throws JellyTagException
    {
        setUpBuilderData();
        final Integer value = 42;
        tag.setValue(String.valueOf(value));
        tag.setValueClassName(Integer.class.getName());
        tag.setValueClassLoader(LOADER_NAME);
        ConstantBeanProvider dep =
                (ConstantBeanProvider) tag.createDependency();
        assertEquals("Wrong constant value", value,
                dep.getBean(getDependencyProvider()));
    }

    /**
     * Tries to create a constant dependency with type conversion when the
     * target class is incorrectly specified.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateValueDependencyConvertInvalidTarget()
            throws JellyTagException
    {
        setUpBuilderData();
        final Integer value = 42;
        tag.setValue(String.valueOf(value));
        tag.setValueClass(getClass());
        tag.setValueClassName(tag.getClass().getName());
        tag.createDependency();
    }

    /**
     * Tests creating a dependency to a local variable.
     */
    @Test
    public void testCreateVarDependency() throws JellyTagException
    {
        InvocationData idata = InvocationData.get(context);
        idata.registerInvokableSupport(new ChainedInvocation());
        tag.setVar("myVar");
        assertNotNull("Could not create dependency to variable", tag
                .createDependency());
    }

    /**
     * Tests creating a dependency to a local variable when no chain invocation
     * is in the context. This should cause an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateVarDependencyInvalid() throws JellyTagException
    {
        tag.setVar("myVar");
        tag.createDependency();
    }

    /**
     * Tries to create a dependency when no attributes are defined.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateDependencyUndefined() throws JellyTagException
    {
        tag.createDependency();
    }

    /**
     * Tries to create a dependency when multiple attributes are defined. This
     * is ambiguous and should create an exception.
     */
    @Test(expected = JellyTagException.class)
    public void testCreateDependencyAmbiguous() throws JellyTagException
    {
        tag.setValue(42);
        tag.setRefClassName(getClass().getName());
        tag.setRefName("myName");
        tag.createDependency();
    }

    /**
     * Tests the getDependency() method.
     */
    @Test
    public void testGetDependency() throws JellyTagException
    {
        tag.setRefName("testName");
        Dependency dep = tag.getDependency();
        Dependency dep2 = tag.getDependency();
        assertSame("Multiple dependencies created", dep, dep2);
    }

    /**
     * Tests the getDependency() method when the tag is incorrectly used. We
     * check that in this case the internal dependency reference is not set.
     */
    @Test
    public void testGetDependencyWithError() throws JellyTagException
    {
        tag.setRefName("myRef");
        tag.setRefClass(getClass());
        try
        {
            tag.getDependency();
            fail("Could obtain dependency when there are multiple definitions!");
        }
        catch (JellyTagException jtex)
        {
            // ok
        }
        tag.setRefClass(null);
        assertTrue("Wrong dependency returned",
                tag.getDependency() instanceof NameDependency);
    }

    /**
     * Tests the hasDependency() method when a dependency is defined.
     */
    @Test
    public void testHasDependencyDefined()
    {
        tag.setRefClass(getClass());
        assertTrue("Wrong result of hasDependency()", tag.hasDependency());
    }

    /**
     * Tests the hasDependency() method when no dependency is defined.
     */
    @Test
    public void testHasDependencyUndefined()
    {
        assertFalse("Wrong result of hasDependency() for undefined", tag
                .hasDependency());
    }

    /**
     * Tests the hasDependency() method when there are multiple dependency
     * definitions.
     */
    @Test
    public void testHasDependencyAmbiguous()
    {
        tag.setRefClass(getClass());
        tag.setValue(42);
        assertTrue(
                "Wrong result of hasDependency() for ambiguous dependencies",
                tag.hasDependency());
    }

    /**
     * Tests the hasDependency() method when creation of the dependency causes
     * an error.
     */
    @Test
    public void testHasDependencyWithError()
    {
        tag.setVar("myVar");
        assertTrue("Wrong result of hasDependency() with error", tag
                .hasDependency());
    }

    /**
     * Tests the hasDependency() method after a get operation.
     */
    @Test
    public void testHasDependencyAfterGet() throws JellyTagException
    {
        tag.setRefClass(getClass());
        assertNotNull("No dependency returned", tag.getDependency());
        assertTrue("Tag does not have a dependency", tag.hasDependency());
    }

    /**
     * Creates a DIBuilderData object and puts it into the Jelly context. The
     * ClassLoaderProvider will also be initialized.
     *
     * @return the builder data object
     */
    private DIBuilderData setUpBuilderData()
    {
        DIBuilderData builderData = new DIBuilderData();
        DefaultClassLoaderProvider clp = new DefaultClassLoaderProvider();
        clp.registerClassLoader(LOADER_NAME, getClass().getClassLoader());
        builderData.setClassLoaderProvider(clp);
        builderData.put(context);
        return builderData;
    }

    /**
     * A concrete test implementation of DependencyTag. This implementation just
     * provides dummy implementations of the abstract methods.
     */
    static class DependencyTagTestImpl extends DependencyTag
    {
        // dummy implementation of this abstract method
        public void doTag(XMLOutput arg0) throws MissingAttributeException,
                JellyTagException
        {
        }
    }
}
