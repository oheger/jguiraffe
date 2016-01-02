/*
 * Copyright 2006-2016 The JGUIraffe Team.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.ContainerTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.ComponentStore;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * An abstract base class for tags that define renderers or editors for the
 * columns of a table.
 * </p>
 * <p>
 * This tag is a specialized container. By tags in its body a single component
 * can be added (which can be a container component containing arbitrary other
 * data). This component will then be added to either the column's render or
 * edit form (concrete sub classes decide, which form is used).
 * </p>
 * <p>
 * A concrete sub class has to implement the following functionality:
 * <ul>
 * <li>It must be able to obtain the correct form from the hosting table tag
 * that is used for this kind of column component. For this purpose the
 * <code>getTableForm()</code> method must be implemented.</li>
 * <li>The results of the processing of this tag must be passed to the hosting
 * column tag. This is done in the <code>initializeColumn()</code> method.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColumnComponentTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class ColumnComponentTag extends ContainerTag
{
    /** A set with the names of the components that are used by this tag. */
    private Set<String> componentNames;

    /** Stores a reference to the hosting table tag. */
    private TableTag tableTag;

    /**
     * Creates a new instance of <code>ColumnComponentTag</code>.
     */
    protected ColumnComponentTag()
    {
        componentNames = new HashSet<String>();
    }

    /**
     * Returns a reference to the table tag this tag is nested inside.
     *
     * @return the hosting table tag
     */
    public TableTag getTableTag()
    {
        return tableTag;
    }


    /**
     * Performs processing before this tag's body is evaluated. This
     * implementation will do some checks.
     *
     * @throws JellyTagException if an error occurs
     * @throws FormBuilderException if the tag is incorrectly used
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        tableTag = (TableTag) findAncestorWithClass(TableTag.class);
        if (tableTag == null)
        {
            throw new FormBuilderException(
                    "ColumnComponentTag must be nested inside a table tag!");
        }
        super.processBeforeBody();

        // Add newly created components to the associated form
        getBuilderData().pushFormContext(getTableForm(tableTag), this);
        // But use a component store that triggers us if necessary
        getBuilderData().pushComponentStore(
                new ComponentStoreDelegate(
                        getBuilderData().getComponentStore(), componentNames));
    }

    /**
     * Creates the container represented by this tag. For this tag handler we
     * only need a fake implementation because the returned object will never be
     * added into a root container or content will be added to it. So simply a
     * reference to <b>this</b> is returned. This also allows the table tag to
     * check that only valid content is defined in its body.
     *
     * @param manager the component manager
     * @param create the create flag
     * @param components a collection with the defined child components
     * @return a reference to the new container
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected Object createContainer(ComponentManager manager, boolean create,
            Collection<Object[]> components) throws FormBuilderException, JellyTagException
    {
        return this;
    }

    /**
     * Adds the components defined by nested tags to this container tag. This
     * implementation checks whether the tag's content is valid. If this is the
     * case, the hosting column tag will be correctly initialized.
     *
     * @param manager the component manager
     * @param container the created container
     * @param comps the created components
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void addComponents(ComponentManager manager, Object container,
            Collection<Object[]> comps) throws FormBuilderException
    {
        if (comps.size() > 1)
        {
            throw new FormBuilderException("Only a single component can be "
                    + "defined as column editor or renderer!");
        }
        TableColumnTag colTag = (TableColumnTag) findAncestorWithClass(TableColumnTag.class);
        if (colTag == null)
        {
            throw new FormBuilderException("ColumnComponentTag must be nested "
                    + "inside a TableColumnTag!");
        }

        if (!comps.isEmpty())
        {
            initializeColumn(colTag, comps.iterator().next()[0], componentNames);
        }

        getBuilderData().popComponentStore();
        getBuilderData().popFormContext(this);
    }

    /**
     * Returns the form of the hosting table that stores the components managed
     * by this tag. This method is always called when access to this form is
     * needed.
     *
     * @param tabTag the table tag
     * @return the form for storing the components defined in this tag
     */
    protected abstract Form getTableForm(TableTag tabTag);

    /**
     * Initializes the hosting column tag with the information gathered by this
     * tag. This method is called once at the end of the processing of this tag.
     * Its purpose is to initialize some of the fields required for the column.
     *
     * @param colTag a reference to the column tag this tag is nested inside
     * @param bodyComponent the component defined in the body of this tag
     * @param componentNames a set with the names of all components referred to
     * by this tag
     */
    protected abstract void initializeColumn(TableColumnTag colTag,
            Object bodyComponent, Set<String> componentNames);

    /**
     * A special implementation of the <code>ComponentStore</code> that is used
     * for monitoring the field handlers added to this column. This
     * implementation simply delegates all incoming method calls to the
     * specified delegate store (which is the store of the associated form
     * object). If a field handler is added, its name is added to a set. This
     * allows the tag to keep track of the fields that are used in this column.
     */
    static class ComponentStoreDelegate implements ComponentStore
    {
        /** Stores the underlying component store. */
        private ComponentStore delegate;

        /** Stores the set for keeping track of the added field handlers. */
        private Set<String> handlerNames;

        /**
         * Creates a new instance of <code>ComponentStoreDelegate</code> and
         * initializes it.
         *
         * @param store the underlying store
         * @param names the set with the names of the affected fields
         */
        public ComponentStoreDelegate(ComponentStore store, Set<String> names)
        {
            delegate = store;
            handlerNames = names;
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public void add(String name, Object component)
        {
            delegate.add(name, component);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public void addComponentHandler(String name, ComponentHandler<?> handler)
        {
            delegate.addComponentHandler(name, handler);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store. The field handler is also recorded.
         */
        public void addFieldHandler(String name, FieldHandler fldHandler)
        {
            delegate.addFieldHandler(name, fldHandler);
            // also remember this name
            handlerNames.add(name);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public Object findComponent(String name)
        {
            return delegate.findComponent(name);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public ComponentHandler<?> findComponentHandler(String name)
        {
            return delegate.findComponentHandler(name);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public FieldHandler findFieldHandler(String name)
        {
            return delegate.findFieldHandler(name);
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public Set<String> getComponentHandlerNames()
        {
            return delegate.getComponentHandlerNames();
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public Set<String> getComponentNames()
        {
            return delegate.getComponentNames();
        }

        /**
         * {@inheritDoc} This implementation just delegates to the wrapped
         * store.
         */
        public Set<String> getFieldHandlerNames()
        {
            return delegate.getFieldHandlerNames();
        }
    }
}
