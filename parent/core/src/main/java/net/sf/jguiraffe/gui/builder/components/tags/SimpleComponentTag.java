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

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * An abstract base class for tags that create simple GUI components.
 * </p>
 * <p>
 * This base class is intended to be used for components that are not inserted
 * into a {@link net.sf.jguiraffe.gui.forms.Form Form} object. Derived tag
 * classes create a component of a specific type. This class ensures that the
 * new component is added to the next enclosing container element.
 * </p>
 * <p>
 * Simple GUI component can be assigned a name using the <code>name</code>
 * attribute. If this is done, they are stored in the component store of the
 * current builder operation. This makes it possible to obtain a
 * <code>WidgetHandler</code> and manipulate some of their properties later.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SimpleComponentTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class SimpleComponentTag extends ComponentBaseTag
{
    /** Stores a reference to the created component. */
    private Object component;

    /**
     * Returns the component that was created by this tag.
     *
     * @return the component created by this tag
     */
    @Override
    public Object getComponent()
    {
        return component;
    }

    /**
     * Callback before processing of the tag's body. Calls
     * {@link #createComponent(ComponentManager)} for the first time with the
     * <code>create</code> parameter set to <b>true</b>.
     *
     * @throws JellyTagException if a script related error occurs
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        component =
                createComponent(getBuilderData().getComponentManager(), true);
    }

    /**
     * Executes this tag. Calls
     * <code>{@link #createComponent(ComponentManager)}</code> for the second
     * time with the boolean parameter <code>create</code> set to <b>false
     * </b>. The new component is then added to the nesting container element.
     *
     * @throws JellyTagException if a script related error occurs
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        super.process();
        component = createComponent(getBuilderData().getComponentManager(),
                false);
        insertComponent(getName(), getComponent());
    }

    /**
     * Creates a specific component. This method must be implemented in derived
     * classes. An implementation may use the passed in component manager to
     * create an instance of a concrete GUI widget class. Note that this method
     * is called twice during execution of this tag: Once before the tag's body
     * is processed and once after that. The boolean argument can be used to
     * distinguish between the two phases. This allows concrete implementations
     * of the <code>ComponentManager</code> interface to use various
     * strategies for creating a component hierarchy.
     *
     * @param manager the component manager
     * @param create a flag whether the component should be newly created
     * @return the new GUI component
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs creating the component
     */
    protected abstract Object createComponent(ComponentManager manager,
            boolean create) throws JellyTagException,
            FormBuilderException;
}
