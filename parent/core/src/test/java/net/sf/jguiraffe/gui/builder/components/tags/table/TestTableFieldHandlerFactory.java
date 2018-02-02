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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertSame;

import net.sf.jguiraffe.gui.builder.components.FieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextFieldTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;

import org.apache.commons.lang.mutable.MutableObject;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code TableFieldHandlerFactory}.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class TestTableFieldHandlerFactory
{
    /** A mock for the wrapped factory. */
    private FieldHandlerFactory wrappedFactory;

    /** The factory to be tested. */
    private TableFieldHandlerFactory factory;

    @Before
    public void setUp() throws Exception
    {
        wrappedFactory = EasyMock.createMock(FieldHandlerFactory.class);
        factory = new TableFieldHandlerFactory(wrappedFactory);
    }

    /**
     * Tests whether a field handler can be created and is correctly
     * initialized.
     */
    @Test
    public void testCreateFieldHandler() throws FormBuilderException
    {
        final FieldHandler handler = EasyMock.createMock(FieldHandler.class);
        TransformerWrapper readTransformer =
                EasyMock.createMock(TransformerWrapper.class);
        TransformerWrapper writeTransformer =
                EasyMock.createMock(TransformerWrapper.class);
        ValidatorWrapper validator =
                EasyMock.createMock(ValidatorWrapper.class);
        ComponentHandler<?> compHandler =
                EasyMock.createMock(ComponentHandler.class);
        InputComponentTag tag = new TextFieldTag();
        final MutableObject refReadTransformer = new MutableObject();
        final MutableObject refWriteTransformer = new MutableObject();
        final MutableObject refValidator = new MutableObject();
        EasyMock.expect(wrappedFactory.createFieldHandler(tag, compHandler))
                .andAnswer(new IAnswer<FieldHandler>() {
                    public FieldHandler answer() throws Throwable {
                        InputComponentTag argTag =
                                (InputComponentTag) EasyMock
                                        .getCurrentArguments()[0];
                        refReadTransformer.setValue(argTag.getReadTransformer());
                        refWriteTransformer.setValue(argTag
                                .getWriteTransformer());
                        refValidator.setValue(argTag.getFieldValidator());
                        return handler;
                    }
                });
        EasyMock.replay(wrappedFactory, handler, readTransformer,
                writeTransformer, validator, compHandler);
        tag.setReadTransformer(readTransformer);
        tag.setWriteTransformer(writeTransformer);
        tag.setFieldValidator(validator);

        ColumnFieldHandler colHandler =
                (ColumnFieldHandler) factory.createFieldHandler(tag,
                        compHandler);
        assertSame("Wrong field handler", handler,
                colHandler.getWrappedHandler());
        TransformerReference reference =
                factory.getReadTransformerReference(colHandler);
        assertSame("Wrong read transformer in reference", readTransformer,
                reference.getTransformer());
        reference = factory.getWriteTransformerReference(colHandler);
        assertSame("Wrong write transformer in reference", writeTransformer,
                reference.getTransformer());
        ValidatorReference validatorReference =
                factory.getValidatorReference(colHandler);
        assertSame("Wrong validator in reference", validator,
                validatorReference.getValidator());
        EasyMock.verify(wrappedFactory);
    }

    /**
     * Tries to query a read transformer reference for an unknown field handler.
     */
    @Test(expected = FormRuntimeException.class)
    public void testGetReadTransformerReferenceUnknownFieldHandler()
    {
        factory.getReadTransformerReference(EasyMock
                .createMock(FieldHandler.class));
    }

    /**
     * Tries to query a write transformer reference for an unknown field
     * handler.
     */
    @Test(expected = FormRuntimeException.class)
    public void testGetWriteTransformerReferenceUnknownFieldHandler()
    {
        factory.getWriteTransformerReference(EasyMock
                .createMock(FieldHandler.class));
    }

    /**
     * Tries to query a validator reference for an unknown field handler.
     */
    @Test(expected = FormRuntimeException.class)
    public void testGetValidatorReferenceUnknownFieldHandler()
    {
        factory.getValidatorReference(EasyMock.createMock(FieldHandler.class));
    }
}
