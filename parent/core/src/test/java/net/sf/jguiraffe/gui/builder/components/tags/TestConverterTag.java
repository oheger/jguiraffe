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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import net.sf.jguiraffe.di.ClassLoaderProvider;
import net.sf.jguiraffe.di.ConversionHelper;
import net.sf.jguiraffe.di.InjectionException;
import net.sf.jguiraffe.gui.builder.di.DIBuilderData;
import net.sf.jguiraffe.gui.builder.di.tags.ClassDescData;
import net.sf.jguiraffe.gui.builder.di.tags.MockClassDescData;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code ConverterTag}.
 *
 * @author Oliver Heger
 * @version $Id: TestConverterTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestConverterTag
{
    /** Constant for an object to be converted. */
    private static final Object CONVOBJ = "ToBeConverted...";

    /** The DI builder data object. */
    private DIBuilderData diData;

    /** The tag to be tested. */
    private ConverterTagTestImpl tag;

    @Before
    public void setUp() throws Exception
    {
        JellyContext context = new JellyContext();
        diData = new DIBuilderData();
        diData.put(context);
        tag = new ConverterTagTestImpl();
        tag.setContext(context);
    }

    /**
     * Tests whether the correct base class is set for the tag.
     */
    @Test
    public void testGetBaseClass()
    {
        assertEquals("Wrong base class", Converter.class, tag.getBaseClass());
    }

    /**
     * Tests whether the target class is fetched correctly.
     */
    @Test
    public void testFetchTargetClass() throws JellyTagException
    {
        ClassLoaderProvider clp =
                EasyMock.createMock(ClassLoaderProvider.class);
        EasyMock.replay(clp);
        diData.setClassLoaderProvider(clp);
        MockClassDescData cdd = new MockClassDescData(clp, getClass());
        assertEquals("Wrong target class", getClass(),
                tag.fetchTargetClass(cdd));
        EasyMock.verify(clp);
    }

    /**
     * Tests whether the class of the converter can be set.
     */
    @Test
    public void testAttrClass() throws JellyTagException
    {
        tag.targetClass = getClass();
        tag.setAttribute(ConverterTag.ATTR_CONVERTER_TARGET_CLASS, getClass());
        tag.passResults(EasyMock.createNiceMock(Converter.class));
        assertEquals("Wrong class", getClass(),
                tag.targetClassDesc.getTargetClass());
    }

    /**
     * Tests whether the target class can be specified as class name.
     */
    @Test
    public void testAttrClassAsName() throws JellyTagException
    {
        tag.targetClass = getClass();
        tag.setAttribute(ConverterTag.ATTR_CONVERTER_TARGET_CLASS, getClass()
                .getName());
        tag.passResults(EasyMock.createNiceMock(Converter.class));
        assertEquals("Wrong class", getClass(),
                tag.targetClassDesc.getTargetClass());
    }

    /**
     * Tests how an invalid class name passed to the target class attribute is
     * handled.
     */
    @Test(expected = InjectionException.class)
    public void testAttrClassAsNameInvalid() throws JellyTagException
    {
        tag.targetClass = getClass();
        tag.setAttribute(ConverterTag.ATTR_CONVERTER_TARGET_CLASS,
                "not a valid class name!");
        tag.passResults(EasyMock.createNiceMock(Converter.class));
    }

    /**
     * Tests whether converter class name and loader attributes can be set.
     */
    @Test
    public void testAttrClassNameAndLoader() throws JellyTagException
    {
        final String clsName = getClass().getName();
        final String clsLoader = "SpecialClassLoader";
        tag.targetClass = getClass();
        tag.setAttribute(ConverterTag.ATTR_CONVERTER_TARGET_CLASS_NAME, clsName);
        tag.setAttribute(ConverterTag.ATTR_CONVERTER_TARGET_CLASS_LOADER,
                clsLoader);
        tag.passResults(EasyMock.createNiceMock(Converter.class));
        assertEquals("Wrong class name", clsName,
                tag.targetClassDesc.getTargetClassName());
        assertEquals("Wrong class loader", clsLoader,
                tag.targetClassDesc.getClassLoaderName());
    }

    /**
     * Tests passResults() if no target class for the converter is provided.
     */
    @Test(expected = JellyTagException.class)
    public void testPassResultsNoCnverterClass() throws JellyTagException
    {
        tag.passResults(EasyMock.createNiceMock(Converter.class));
    }

    /**
     * Tests whether a base class converter can be registered.
     */
    @Test
    public void testPassResultsBaseClassConverter() throws JellyTagException
    {
        Converter conv = EasyMock.createMock(Converter.class);
        List<?> lst = EasyMock.createMock(List.class);
        EasyMock.expect(conv.convert(List.class, CONVOBJ)).andReturn(lst);
        EasyMock.replay(conv, lst);
        tag.targetClass = Collection.class;
        tag.setAttribute(ConverterTag.ATTR_BASE_CLASS_CONVERTER, Boolean.TRUE);
        assertTrue("Wrong result", tag.passResults(conv));
        ConversionHelper convHlp =
                diData.getInvocationHelper().getConversionHelper();
        assertSame("Wrong conversion result", lst,
                convHlp.convert(List.class, CONVOBJ));
        EasyMock.verify(conv, lst);
    }

    /**
     * Tests whether a standard converter can be registered.
     */
    @Test
    public void testPassResultsStdConverter() throws JellyTagException
    {
        Converter conv = EasyMock.createMock(Converter.class);
        List<?> lst = EasyMock.createMock(List.class);
        EasyMock.expect(conv.convert(Collection.class, CONVOBJ)).andReturn(lst);
        EasyMock.replay(conv, lst);
        tag.targetClass = Collection.class;
        assertTrue("Wrong result", tag.passResults(conv));
        ConversionHelper convHlp =
                diData.getInvocationHelper().getConversionHelper();
        assertSame("Wrong conversion result", lst,
                convHlp.convert(Collection.class, CONVOBJ));
        try
        {
            convHlp.convert(List.class, CONVOBJ);
            fail("Could convert List.class!");
        }
        catch (InjectionException iex)
        {
            EasyMock.verify(conv, lst);
        }
    }

    /**
     * A test implementation of ConverterTag which provides some mocking
     * facilities.
     */
    private static class ConverterTagTestImpl extends ConverterTag
    {
        /** Stores the class description passed to fetchTargetClass(). */
        ClassDescData targetClassDesc;

        /** The target class to be returned by fetchTargetClass(). */
        Class<?> targetClass;

        /**
         * Either returns the mock target class or calls the super method.
         * Records this invocation.
         */
        @Override
        protected Class<?> fetchTargetClass(ClassDescData cdd)
                throws JellyTagException
        {
            targetClassDesc = cdd;
            return (targetClass != null) ? targetClass : super
                    .fetchTargetClass(cdd);
        }
    }
}
