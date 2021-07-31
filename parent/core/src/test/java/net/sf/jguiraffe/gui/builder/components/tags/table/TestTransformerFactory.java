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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.DefaultTransformerWrapper;
import net.sf.jguiraffe.gui.forms.DefaultValidatorWrapper;
import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.DateTransformer;
import net.sf.jguiraffe.transform.DoubleTransformer;
import net.sf.jguiraffe.transform.DummyTransformer;
import net.sf.jguiraffe.transform.LongTransformer;
import net.sf.jguiraffe.transform.ToStringTransformer;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.Validator;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Tag;
import org.apache.commons.lang.mutable.MutableObject;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code TransformerFactory}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestTransformerFactory
{
    /** A mock for a transformer context. */
    private TransformerContext transformerContext;

    /** A mock tag. */
    private Tag tag;

    /** The factory to be tested. */
    private TransformerFactory factory;

    @Before
    public void setUp() throws Exception
    {
        transformerContext = EasyMock.createMock(TransformerContext.class);
        tag = setUpTag();
        factory = new TransformerFactory();
    }

    /**
     * Prepares a mock for a tag that allows access to the transformer context.
     *
     * @return an initialized mock for a tag
     */
    private Tag setUpTag()
    {
        Tag t = EasyMock.createMock(Tag.class);
        JellyContext context = new JellyContext();
        ComponentBuilderData builderData = new ComponentBuilderData();
        builderData.initializeForm(transformerContext,
                new BeanBindingStrategy());
        builderData.put(context);
        EasyMock.expect(t.getContext()).andReturn(context).anyTimes();
        EasyMock.replay(t);
        return t;
    }

    /**
     * Checks a transformer wrapper and extracts the expected transformer.
     *
     * @param transCls the expected transformer class
     * @param wrapper the wrapper
     * @param <T> the type of the expected transformer
     * @return the extracted transformer instance
     */
    private <T extends Transformer> T checkAndExtractTransformer(
            Class<T> transCls, TransformerWrapper wrapper)
    {
        DefaultTransformerWrapper defWrapper =
                (DefaultTransformerWrapper) wrapper;
        assertSame("Wrong transformer context", transformerContext,
                defWrapper.getTransformerContext());
        assertTrue(
                "Wrong transformer instance: " + defWrapper.getTransformer(),
                transCls.isInstance(defWrapper.getTransformer()));
        return transCls.cast(defWrapper.getTransformer());
    }

    /**
     * Checks a validator wrapper and extracts the expected validator.
     *
     * @param valCls the expected validator class
     * @param wrapper the wrapper
     * @param <T> the type of the expected validator
     * @return the extracted validator instance
     */
    private <T extends Validator> T checkAndExtractValidator(Class<T> valCls,
            ValidatorWrapper wrapper)
    {
        DefaultValidatorWrapper defWrapper = (DefaultValidatorWrapper) wrapper;
        assertSame("Wrong transformer context", transformerContext,
                defWrapper.getTransformerContext());
        assertTrue("Wrong validator instance: " + defWrapper.getValidator(),
                valCls.isInstance(defWrapper.getValidator()));
        return valCls.cast(defWrapper.getValidator());
    }

    /**
     * Tests whether a string transformer can be queried.
     */
    @Test
    public void testGetTransformerString()
    {
        TransformerWrapper wrapper =
                factory.getReadTransformer(tag, ColumnClass.STRING);
        ToStringTransformer transformer =
                checkAndExtractTransformer(ToStringTransformer.class, wrapper);
        assertEquals("Wrong date style", DateFormat.SHORT,
                transformer.getDateFormatStyle());
    }

    /**
     * Tests whether a validator for the logic type String can be queried.
     */
    @Test
    public void testGetValidatorString()
    {
        assertSame("Wrong validator", DummyWrapper.INSTANCE,
                factory.getValidator(tag, ColumnClass.STRING));
    }

    /**
     * Tests whether transformers are cached after they have been created once.
     */
    @Test
    public void testGetTransformerCached()
    {
        for (ColumnClass c : Arrays.asList(ColumnClass.STRING,
                ColumnClass.NUMBER))
        {
            TransformerWrapper wrapper = factory.getReadTransformer(tag, c);
            assertSame("Multiple instances for class " + c, wrapper,
                    factory.getReadTransformer(tag, c));
        }
    }

    /**
     * Tests whether a transformer for the type Number can be queried.
     */
    @Test
    public void testGetTransformerNumber()
    {
        TransformerWrapper wrapper =
                factory.getReadTransformer(tag, ColumnClass.NUMBER);
        LongTransformer transformer =
                checkAndExtractTransformer(LongTransformer.class, wrapper);
        assertNull("Got a maximum", transformer.getMaximum());
        assertNull("Got a minimum", transformer.getMinimum());
    }

    /**
     * Tests whether a validator for the type Number can be queried.
     */
    @Test
    public void testGetValidatorNumber()
    {
        ValidatorWrapper wrapper =
                factory.getValidator(tag, ColumnClass.NUMBER);
        LongTransformer validator =
                checkAndExtractValidator(LongTransformer.class, wrapper);
        assertSame(
                "Different number validator instance",
                checkAndExtractTransformer(LongTransformer.class,
                        factory.getReadTransformer(tag, ColumnClass.NUMBER)),
                validator);
    }

    /**
     * Tests whether a transformer of type Float can be queried.
     */
    @Test
    public void testGetTransformerFloat()
    {
        TransformerWrapper wrapper =
                factory.getReadTransformer(tag, ColumnClass.FLOAT);
        DoubleTransformer transformer =
                checkAndExtractTransformer(DoubleTransformer.class, wrapper);
        assertNull("Got a maximum", transformer.getMaximum());
        assertNull("Got a minimum", transformer.getMinimum());
    }

    /**
     * Tests whether a validator of type Float can be queried.
     */
    @Test
    public void testGetValidatorFloat()
    {
        ValidatorWrapper wrapper = factory.getValidator(tag, ColumnClass.FLOAT);
        DoubleTransformer validator =
                checkAndExtractValidator(DoubleTransformer.class, wrapper);
        assertSame(
                "Different float validator instance",
                checkAndExtractTransformer(DoubleTransformer.class,
                        factory.getReadTransformer(tag, ColumnClass.FLOAT)),
                validator);
    }

    /**
     * Tests whether a transformer of type Date can be queried.
     */
    @Test
    public void testGetTransformerDate()
    {
        TransformerWrapper wrapper =
                factory.getReadTransformer(tag, ColumnClass.DATE);
        DateTransformer transformer =
                checkAndExtractTransformer(DateTransformer.class, wrapper);
        assertEquals("Wrong style", DateFormat.SHORT, transformer.getStyle());
        assertNull("Got a reference date", transformer.getReferenceDate());
    }

    /**
     * Tests whether a validator of type Date can be queried.
     */
    @Test
    public void testGetValidatorDate()
    {
        ValidatorWrapper wrapper = factory.getValidator(tag, ColumnClass.DATE);
        DateTransformer validator =
                checkAndExtractValidator(DateTransformer.class, wrapper);
        assertSame(
                "Different date validator instance",
                checkAndExtractTransformer(DateTransformer.class,
                        factory.getReadTransformer(tag, ColumnClass.DATE)),
                validator);
    }

    /**
     * Helper method for testing a column type for which no read transformer is
     * supported.
     *
     * @param columnClass the column class
     */
    private void checkDummyReadTransformer(ColumnClass columnClass)
    {
        assertSame("Wrong transformer", DummyWrapper.INSTANCE,
                factory.getReadTransformer(tag, columnClass));
    }

    /**
     * Helper method for testing a column type for which no validator is
     * supported.
     *
     * @param columnClass the column class
     */
    private void checkDummyValidator(ColumnClass columnClass)
    {
        assertSame("Wrong validator", DummyWrapper.INSTANCE,
                factory.getValidator(tag, columnClass));
    }

    /**
     * Tests whether a transformer of type boolean can be queried.
     */
    @Test
    public void testGetTransformerBoolean()
    {
        checkDummyReadTransformer(ColumnClass.BOOLEAN);
    }

    /**
     * Tests whether a validator of type boolean can be queried.
     */
    @Test
    public void testGetValidatorBoolean()
    {
        checkDummyValidator(ColumnClass.BOOLEAN);
    }

    /**
     * Tests whether a transformer of type ICON can be queried.
     */
    @Test
    public void testGetTransformerIcon()
    {
        checkDummyReadTransformer(ColumnClass.ICON);
    }

    /**
     * Tests whether a validator of type ICON can be queried.
     */
    @Test
    public void testGetValidatorIcon()
    {
        checkDummyValidator(ColumnClass.ICON);
    }

    /**
     * Tries to query a transformer if no column class is provided.
     */
    @Test(expected = FormRuntimeException.class)
    public void testGetTransformerNull()
    {
        factory.getReadTransformer(tag, null);
    }

    /**
     * Tries to query a validator if no column class is provided.
     */
    @Test(expected = FormRuntimeException.class)
    public void testGetValidatorNull()
    {
        factory.getValidator(tag, null);
    }

    /**
     * Tests whether the correct write transformer is returned for the default
     * column classes.
     */
    @Test
    public void testGetWriteTransformerForDefaultColumnClasses()
    {
        Set<ColumnClass> colClasses =
                EnumSet.of(ColumnClass.DATE, ColumnClass.FLOAT,
                        ColumnClass.NUMBER, ColumnClass.STRING);
        for (ColumnClass columnClass : colClasses)
        {
            checkAndExtractTransformer(ToStringTransformer.class,
                    factory.getWriteTransformer(tag, columnClass));
        }
    }

    /**
     * Helper method for testing whether a dummy write transformer is created
     * for a given column class.
     *
     * @param columnClass the column class
     */
    private void checkDummyWriteTransformer(ColumnClass columnClass)
    {
        assertSame(
                "Wrong write transformer",
                DummyTransformer.getInstance(),
                checkAndExtractTransformer(DummyTransformer.class,
                        factory.getWriteTransformer(tag, columnClass)));
    }

    /**
     * Tests the write transformer returned for column class ICON.
     */
    @Test
    public void testGetWriteTransformerIcon()
    {
        checkDummyReadTransformer(ColumnClass.ICON);
    }

    /**
     * Tests the write transformer returned for column class BOOLEAN.
     */
    @Test
    public void testGetWriteTransformerBoolean()
    {
        checkDummyWriteTransformer(ColumnClass.BOOLEAN);
    }

    /**
     * Tests that the write transformer is cached.
     */
    @Test
    public void testGetWriteTransformerCached()
    {
        TransformerWrapper transformer = factory.getWriteTransformer(tag, ColumnClass.STRING);
        assertSame("Multiple write transformers", transformer,
                factory.getWriteTransformer(tag, ColumnClass.DATE));
    }

    /**
     * Tests that the dummy write transformer is cached.
     */
    @Test
    public void testGetDummyWriteTransformerCached()
    {
        TransformerWrapper transformer = factory.getWriteTransformer(tag, ColumnClass.BOOLEAN);
        assertSame("Multiple write transformers", transformer,
                factory.getWriteTransformer(tag, ColumnClass.ICON));
    }

    /**
     * Helper method for testing whether data can be converted using the
     * transformers produced by this factory.
     *
     * @param columnClass the column class
     * @param data the data object to be converted
     */
    private void checkTransformerRoundTrip(ColumnClass columnClass, Object data)
    {
        EasyMock.expect(transformerContext.properties())
                .andReturn(Collections.<String, Object> emptyMap()).anyTimes();
        EasyMock.expect(transformerContext.getLocale())
                .andReturn(Locale.ENGLISH).anyTimes();
        EasyMock.replay(transformerContext);
        MutableObject model = new MutableObject(data);
        Form form = new Form(transformerContext, new BeanBindingStrategy());
        DefaultFieldHandler fieldHandler = new DefaultFieldHandler();
        fieldHandler.setReadTransformer(factory.getReadTransformer(tag,
                columnClass));
        fieldHandler.setWriteTransformer(factory.getWriteTransformer(tag, columnClass));
        fieldHandler.setSyntaxValidator(factory.getValidator(tag, columnClass));
        fieldHandler.setComponentHandler(new ComponentHandlerImpl());
        form.addField("value", fieldHandler);

        form.initFields(model);
        model = new MutableObject();
        assertTrue("Not valid", form.validate(model).isValid());
        assertEquals("Wrong model value", data, model.getValue());
    }

    /**
     * Tests whether the transformers for the type Number work correctly.
     */
    @Test
    public void testRoundTripNumber()
    {
        checkTransformerRoundTrip(ColumnClass.NUMBER, 42L);
    }

    /**
     * Tests whether the transformers for the type Float work correctly.
     */
    @Test
    public void testRoundTripFloat()
    {
        checkTransformerRoundTrip(ColumnClass.FLOAT, 3.14);
    }

    /**
     * Tests whether the transformers for the type String work correctly.
     */
    @Test
    public void testRoundTripString()
    {
        checkTransformerRoundTrip(ColumnClass.STRING, "Hello World");
    }

    /**
     * Tests whether the transformers for the type Date work correctly.
     */
    @Test
    public void testRoundTripDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2014, Calendar.JUNE, 26);
        checkTransformerRoundTrip(ColumnClass.DATE, cal.getTime());
    }
}
