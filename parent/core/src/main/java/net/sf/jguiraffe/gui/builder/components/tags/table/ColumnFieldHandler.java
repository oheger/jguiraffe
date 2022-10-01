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

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * A specialized field handler class used internally by the form constructed by
 * the table tags.
 * </p>
 * <p>
 * For columns that do not define their own renderer or editor a default field
 * handler will be created and added to both the renderer and the editor form.
 * If now for a row in the table both the renderer and the editor form are
 * requested, the default field handlers will be called twice. While this is not
 * really a problem, it may be inefficient, especially if complex transforming
 * operations are involved.
 * </p>
 * <p>
 * To avoid this tables use a special implementation of the
 * <code>FieldHandler</code> interface that is merely a wrapper arround
 * another field handler implementation. Most methods are directly delegated to
 * the wrapped handler. Only the the <code>setData()</code> method checks
 * whether the data to be set is equal to the data object set for the last time.
 * If this is the case, the call is ignored (because it can be assumed that the
 * associated component handler already contains the correct data).
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColumnFieldHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class ColumnFieldHandler implements FieldHandler
{
    /** Stores the wrapped handler. */
    private FieldHandler wrappedHandler;

    /** Stores the data value that was set the last time. */
    private Object lastData;

    /** Stores a flag whether setData() must be called. */
    private boolean forceSetData;

    /**
     * Creates a new instance of <code>ColumnFieldHandler</code> and
     * initializes it with the handler to wrap.
     *
     * @param handler the underlying handler
     */
    public ColumnFieldHandler(FieldHandler handler)
    {
        if (handler == null)
        {
            throw new IllegalArgumentException(
                    "Wrapped handler must not be null!");
        }
        wrappedHandler = handler;
        forceSetData = true;
    }

    /**
     * Returns a reference to the wrapped field handler.
     *
     * @return the underlying field handler
     */
    public FieldHandler getWrappedHandler()
    {
        return wrappedHandler;
    }

    /**
     * Returns the component handler. Delegates to the wrapped handler.
     *
     * @return the component handler
     */
    public ComponentHandler<?> getComponentHandler()
    {
        return getWrappedHandler().getComponentHandler();
    }

    /**
     * Returns the data of this field. Delegates to the wrapped handler.
     *
     * @return the data
     */
    public Object getData()
    {
        return getWrappedHandler().getData();
    }

    /**
     * Returns the property name of this field. Delegates to the wrapped
     * handler.
     *
     * @return the property name
     */
    public String getPropertyName()
    {
        return getWrappedHandler().getPropertyName();
    }

    /**
     * Returns the display name of this field. Delegates to the wrapped handler.
     *
     * @return the display name
     */
    public String getDisplayName()
    {
        return getWrappedHandler().getDisplayName();
    }

    /**
     * Returns the type of this field. Delegates to the wrapped handler.
     *
     * @return the type
     */
    public Class<?> getType()
    {
        return getWrappedHandler().getType();
    }

    /**
     * Sets the data for this field. This implementation ensures that the call
     * is only delegated to the wrapped handler if necessary.
     *
     * @param data the data to be set
     */
    public void setData(Object data)
    {
        boolean delegate;
        if (forceSetData)
        {
            delegate = true;
            forceSetData = false;
        }
        else
        {
            delegate = (lastData == null) ? data != null : !lastData
                    .equals(data);
        }
        if (delegate)
        {
            getWrappedHandler().setData(data);
            lastData = data;
        }
    }

    /**
     * Performs validation. Delegates to the wrapped handler.
     *
     * @param phase the validation phase
     * @return the validation result
     */
    public ValidationResult validate(ValidationPhase phase)
    {
        forceSetData = true;
        return getWrappedHandler().validate(phase);
    }
}
