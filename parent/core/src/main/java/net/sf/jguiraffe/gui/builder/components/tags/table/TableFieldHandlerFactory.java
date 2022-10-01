/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.FieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;

/**
 * <p>
 * An internally used implementation of {@code FieldHandlerFactory} which is
 * used for creating the field handlers of the form representing a table row.
 * </p>
 * <p>
 * The main purpose of this class is to create instances of a specialized
 * {@code FieldHandler} implementation for table columns that also allow
 * manipulations of transformers and validators assigned to them. (Under certain
 * circumstances they have to be changed to support logic column types.) This is
 * done by replacing the {@code TransformerWrapper} objects in the
 * {@code InputComponentTag}s passed to the factory by
 * {@link TransformerReference} objects, and {@code ValidatorWrapper} objects
 * are replaced by {@link ValidatorReference} objects. These objects are also
 * stored, so that they can be obtained and manipulated later. The actual
 * creation of a {@code FieldHandler} is done by a wrapped
 * {@code FieldHandlerFactory} passed to the constructor.
 * </p>
 * <p>
 * {@link TableTag} ensures that an instance of this class is active while the
 * tag is processed. So all processed fields for columns are handled by this
 * instance.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
class TableFieldHandlerFactory implements FieldHandlerFactory
{
    /** The wrapped field handler factory. */
    private final FieldHandlerFactory wrappedFactory;

    /**
     * A map for storing the read transformer references created by this
     * factory.
     */
    private final Map<FieldHandler, TransformerReference> readTransformers;

    /**
     * A map for storing the write transformer references created by this
     * factory.
     */
    private final Map<FieldHandler, TransformerReference> writeTransformers;

    /** A map for storing the validator references created by this factory. */
    private final Map<FieldHandler, ValidatorReference> validators;

    /**
     * Creates a new instance of {@code TableFieldHandlerFactory} and
     * initializes it with the wrapped {@code FieldHandlerFactory}.
     *
     * @param factory the wrapped {@code FieldHandlerFactory} (expected to be
     *        not <b>null</b>)
     */
    public TableFieldHandlerFactory(FieldHandlerFactory factory)
    {
        wrappedFactory = factory;
        readTransformers = new HashMap<FieldHandler, TransformerReference>();
        writeTransformers = new HashMap<FieldHandler, TransformerReference>();
        validators = new HashMap<FieldHandler, ValidatorReference>();
    }

    /**
     * Returns the wrapped {@code FieldHandlerFactory}.
     *
     * @return the wrapped {@code FieldHandlerFactory}
     */
    public FieldHandlerFactory getWrappedFactory()
    {
        return wrappedFactory;
    }

    /**
     * {@inheritDoc} This implementation delegates to the wrapped factory, but
     * passes in a slightly modified {@code InputComponentTag}: the read and
     * write transformers have been replaced by {@link TransformerReference}
     * objects.
     */
    public FieldHandler createFieldHandler(InputComponentTag tag,
            ComponentHandler<?> componentHandler) throws FormBuilderException
    {
        TransformerReference readReference =
                new TransformerReference(tag.getReadTransformer());
        TransformerReference writeReference =
                new TransformerReference(tag.getWriteTransformer());
        ValidatorReference validatorReference =
                new ValidatorReference(tag.getFieldValidator());
        tag.setReadTransformer(readReference);
        tag.setWriteTransformer(writeReference);
        tag.setFieldValidator(validatorReference);

        FieldHandler fieldHandler =
                new ColumnFieldHandler(getWrappedFactory().createFieldHandler(
                        tag, componentHandler));
        readTransformers.put(fieldHandler, readReference);
        writeTransformers.put(fieldHandler, writeReference);
        validators.put(fieldHandler, validatorReference);

        return fieldHandler;
    }

    /**
     * Returns the {@code TransformerReference} created for the read transformer
     * of the specified {@code FieldHandler}.
     *
     * @param handler the {@code FieldHandler}
     * @return the {@code TransformerReference} for the read transformer
     * @throws net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException
     *         if the field handler cannot be resolved
     */
    public TransformerReference getReadTransformerReference(FieldHandler handler)
    {
        return checkReference(readTransformers.get(handler), handler);
    }

    /**
     * Returns the {@code TransformerReference} created for the write
     * transformer of the specified {@code FieldHandler}.
     *
     * @param handler the {@code FieldHandler}
     * @return the {@code TransformerReference} for the write transformer
     * @throws net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException
     *         if the field handler cannot be resolved
     */
    public TransformerReference getWriteTransformerReference(
            FieldHandler handler)
    {
        return checkReference(writeTransformers.get(handler), handler);
    }

    /**
     * Returns the {@code ValidatorReference} create for the validator of the
     * specified {@code FieldHandler}.
     *
     * @param handler the {@code FieldHandler}
     * @return the {@code ValidatorReference} for this field handler
     * @throws net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException
     *         if the field handler cannot be resolved
     */
    public ValidatorReference getValidatorReference(FieldHandler handler)
    {
        return checkReference(validators.get(handler), handler);
    }

    /**
     * Checks whether the specified reference is defined. Otherwise, an
     * exception is thrown.
     *
     * @param ref the reference to be checked
     * @param fieldHandler the {@code FieldHandler} that has been resolved
     * @param <T> the type of the reference
     * @return the reference again if it is valid
     * @throws net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException
     *         if the reference is <b>null</b>
     */
    private static <T> T checkReference(T ref, FieldHandler fieldHandler)
    {
        if (ref == null)
        {
            throw new FormRuntimeException("Cannot resolve FieldHandler "
                    + fieldHandler);
        }
        return ref;
    }
}
