/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import java.util.EnumMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.forms.DefaultTransformerWrapper;
import net.sf.jguiraffe.gui.forms.DefaultValidatorWrapper;
import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.FormRuntimeException;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.transform.DateTransformer;
import net.sf.jguiraffe.transform.DoubleTransformer;
import net.sf.jguiraffe.transform.LongTransformer;
import net.sf.jguiraffe.transform.ToStringTransformer;
import net.sf.jguiraffe.transform.Transformer;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.Validator;

import org.apache.commons.jelly.Tag;

/**
 * <p>
 * An internally used helper class for creating {@link Transformer} and
 * {@link Validator} objects based on logic column types.
 * </p>
 * <p>
 * For columns that are assigned a logic column type, platform-specific
 * implementations of table components may require specific transformers to be
 * installed. This factory class provides default transformers and validators
 * for most of the logic column types.
 * </p>
 * <p>
 * The default implementations for transformers and validators can be shared and
 * re-used. Therefore, instances are created once and then cached. This class is
 * not thread-safe! It is intended to be used only within a single builder
 * operation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id$
 */
class TransformerFactory
{
    /** A map acting as cache for transformers. */
    private final Map<ColumnClass, TransformerWrapper> transformerCache;

    /** A map acting as cache for validators. */
    private final Map<ColumnClass, ValidatorWrapper> validatorCache;

    /** The write transformer. */
    private TransformerWrapper writeTransformer;

    /**
     * Creates a new instance of {@code TransformerFactory}.
     */
    public TransformerFactory()
    {
        transformerCache =
                new EnumMap<ColumnClass, TransformerWrapper>(ColumnClass.class);
        validatorCache =
                new EnumMap<ColumnClass, ValidatorWrapper>(ColumnClass.class);
        initInitialCaches();
    }

    /**
     * Returns a {@code TransformerWrapper} object for the specified logic
     * column class for reading data from an input field. This transformer
     * is normally the problematic one because it has to convert from a
     * text representation entered by the user to the correct data type.
     *
     * @param tag the current tag (for obtaining context information)
     * @param columnClass the {@code ColumnClass}
     * @return the read {@code TransformerWrapper} for this column class
     */
    public TransformerWrapper getReadTransformer(Tag tag, ColumnClass columnClass)
    {
        if (!transformerCache.containsKey(columnClass))
        {
            initializeObjectsForClass(tag, columnClass);
        }
        return transformerCache.get(columnClass);
    }

    /**
     * Returns a {@code TransformerWrapper} object for writing data into an
     * input field. This implementation returns a plain
     * {@link ToStringTransformer}; basically, the original object value has to
     * be transformed to a string to be displayed in the text input field.
     *
     * @param tag the current tag (for obtaining context information)
     * @return the default write {@code TransformerWrapper}
     */
    public TransformerWrapper getWriteTransformer(Tag tag)
    {
        if (writeTransformer == null)
        {
            writeTransformer =
                    createTransformer(tag, new ToStringTransformer());
        }
        return writeTransformer;
    }

    /**
     * Returns a {@code ValidatorWrapper} object for the specified logic column
     * class.
     *
     * @param tag the current tag (for obtaining context information)
     * @param columnClass the {@code ColumnClass}
     * @return the {@code ValidatorWrapper} for this column class
     */
    public ValidatorWrapper getValidator(Tag tag, ColumnClass columnClass)
    {
        if (!validatorCache.containsKey(columnClass))
        {
            initializeObjectsForClass(tag, columnClass);
        }
        return validatorCache.get(columnClass);
    }

    /**
     * Initializes the transformer and validator for the specified column class
     * and stores them in the caches. This method is called on first access to a
     * column class.
     *
     * @param tag the current tag (for obtaining context information)
     * @param columnClass the {@code ColumnClass}
     * @throws FormRuntimeException if the {@code ColumnClass} is <b>null</b>
     */
    private void initializeObjectsForClass(Tag tag, ColumnClass columnClass)
    {
        if (columnClass == null)
        {
            throw new FormRuntimeException("ColumnClass must not be null!");
        }

        switch (columnClass)
        {
        case NUMBER:
        {
            LongTransformer transformer = new LongTransformer();
            putTransformer(tag, columnClass, transformer);
            putValidator(tag, columnClass, transformer);
            break;
        }
        case FLOAT:
        {
            DoubleTransformer transformer = new DoubleTransformer();
            putTransformer(tag, columnClass, transformer);
            putValidator(tag, columnClass, transformer);
            break;
        }
        case STRING:
        {
            putTransformer(tag, columnClass, new ToStringTransformer());
            validatorCache.put(columnClass, DummyWrapper.INSTANCE);
            break;
        }
        case DATE:
        {
            DateTransformer transformer = new DateTransformer();
            putTransformer(tag, columnClass, transformer);
            putValidator(tag, columnClass, transformer);
            break;
        }
        }
    }

    /**
     * Stores a {@code Transformer} object in the cache for transformers.
     *
     * @param tag the current tag (for obtaining context information)
     * @param columnClass the {@code ColumnClass}
     * @param transformer the {@code Transformer} to be stored
     */
    private void putTransformer(Tag tag, ColumnClass columnClass,
            Transformer transformer)
    {
        transformerCache.put(columnClass, createTransformer(tag, transformer));
    }

    /**
     * Stores a {@code Validator} object in the cache for validators.
     *
     * @param tag the current tag (for obtaining context information)
     * @param columnClass the {@code ColumnClass}
     * @param validator the {@code Validator} to be stored
     */
    private void putValidator(Tag tag, ColumnClass columnClass,
            Validator validator)
    {
        validatorCache.put(columnClass, createValidator(tag, validator));
    }

    /**
     * Initializes the maps serving as caches with initial values. Here dummy
     * objects are stored for column classes not directly supported.
     */
    private void initInitialCaches()
    {
        transformerCache.put(ColumnClass.BOOLEAN, DummyWrapper.INSTANCE);
        transformerCache.put(ColumnClass.ICON, DummyWrapper.INSTANCE);
        validatorCache.put(ColumnClass.BOOLEAN, DummyWrapper.INSTANCE);
        validatorCache.put(ColumnClass.ICON, DummyWrapper.INSTANCE);
    }

    /**
     * Helper method for creating a fully initialized {@code TransformerWrapper}
     * for the passed in {@code Transformer}.
     *
     * @param tag the current tag (for obtaining context information)
     * @param transformer the {@code Transformer} to be wrapped
     * @return the {@code TransformerWrapper}
     */
    private static TransformerWrapper createTransformer(Tag tag,
            Transformer transformer)
    {
        return new DefaultTransformerWrapper(transformer,
                fetchTransformerContext(tag));
    }

    /**
     * Helper method for creating a fully initialized {@code ValidatorWrapper}
     * for the specified {@code Validator}.
     *
     * @param tag the current tag (for obtaining context information)
     * @param validator the {@code Validator} to be wrapped
     * @return the {@code ValidatorWrapper}
     */
    private static ValidatorWrapper createValidator(Tag tag, Validator validator)
    {
        return new DefaultValidatorWrapper(validator,
                fetchTransformerContext(tag));
    }

    /**
     * Obtains the {@code TransformerContext} from the given tag.
     *
     * @param tag the current tag (for obtaining context information)
     * @return the {@code TransformerContext}
     */
    private static TransformerContext fetchTransformerContext(Tag tag)
    {
        return ComponentBuilderData.get(tag.getContext())
                .getTransformerContext();
    }
}