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
package net.sf.jguiraffe.gui.forms;

/**
 * <p>
 * A simple default implementation of the <code>ComponentHandler</code>
 * interface.
 * </p>
 * <p>
 * This class implements all methods required by the
 * <code>ComponentHandler</code> interface without being backed by a really
 * GUI component. All get and set methods operate on internal properties. The
 * class can be used for testing or for components that need a faked component
 * handler.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentHandlerImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentHandlerImpl implements ComponentHandler<Object>
{
    /** Stores the data of this handler. */
    private Object data;

    /** Holds a reference to the associated component. */
    private Object component;

    /** Stores the data type of this handler. */
    private Class<?> type;

    /** A flag whether this handler is enabled. */
    private boolean enabled;

    /**
     * Returns the underlying component.
     *
     * @return the component
     */
    public Object getComponent()
    {
        return component;
    }

    /**
     * Sets the underlying component. The passed in object is simply stored in
     * an internal property.
     *
     * @param c the component
     */
    public void setComponent(Object c)
    {
        component = c;
    }

    /**
     * Returns data of this handler. This data is simply stored in an internal
     * property.
     *
     * @return the component's data
     */
    public Object getData()
    {
        return data;
    }

    /**
     * Sets data of this handler. The passed in data object is stored in an
     * internal property.
     *
     * @param data the data object
     */
    public void setData(Object data)
    {
        this.data = data;
    }

    /**
     * Returns the data type of this handler.
     *
     * @return the data type
     */
    public Class<?> getType()
    {
        return type;
    }

    /**
     * Allows to set this handler's data type. The type set with this method
     * will be returned by the <code>getType()</code> method. It should always
     * be initialized first.
     *
     * @param type the type of this handler
     */
    public void setType(Class<?> type)
    {
        this.type = type;
    }

    /**
     * Returns the outer component of this handler. This is the same as the
     * normal component.
     *
     * @return the handler's outer component
     */
    public Object getOuterComponent()
    {
        return getComponent();
    }

    /**
     * Returns the enabled flag.
     *
     * @return the enabled flag
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the enabled flag. This flag is simply realized by an internal member
     * variable.
     *
     * @param f the flag value
     */
    public void setEnabled(boolean f)
    {
        enabled = f;
    }
}
