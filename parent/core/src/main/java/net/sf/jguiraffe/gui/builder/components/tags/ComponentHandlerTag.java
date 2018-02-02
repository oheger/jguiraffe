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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.CompositeComponentHandler;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class that allows constructing custom {@link ComponentHandler}
 * objects.
 * </p>
 * <p>
 * This tag can be placed in the body of a {@link FieldTag} to define the
 * <code>ComponentHandler</code> that should be used by the input component to
 * be constructed. The class of the new component handler must be specified in
 * the <code>class</code> attribute. An instance of this class is created,
 * initialized (which can be done by using other attributes representing
 * properties of the newly created object), and finally passed to the enclosing
 * <code>FieldTag</code> instance. This tag will then construct a suitable
 * {@link net.sf.jguiraffe.gui.forms.FieldHandler FieldHandler} object and
 * ensure that the represented component is added to the current
 * {@link net.sf.jguiraffe.gui.forms.Form Form} object and to the enclosing GUI
 * container element.
 * </p>
 * <p>
 * With this tag handler class two common use cases can be addressed:
 * <ul>
 * <li>Components not supported by the form builder library (e.g. custom
 * controls) can be added to both the resulting <code>Form</code> object and the
 * generated GUI. For this purpose a suitable implementation of the
 * <code>ComponentHandler</code> interface must be provided, which wraps the
 * component to be added. The name of the implementation class must then be
 * specified as the value of this tag's <code>class</code> attribute.</li>
 * <li>Sometimes the default data provided by the supported GUI components is
 * not sufficient. A typical example is a group of radio buttons: Each radio
 * button has a boolean data object that tells whether this button is the
 * selected one. Instead of storing a boolean value for each radio button that
 * belongs to the group in the form's bean it is probably better to use a
 * different storage format, e.g. storing only the index of the selected button
 * as an int or using some kind of mapping to other values. This can be achieved
 * by first defining the components that should be manipulated (in this example
 * the radio buttons) in the usual way in the Jelly script, but setting their
 * <code>noField</code> attribute to <b>true</b>. This ensures that they are not
 * added as concrete data fields to the constructed <code>Form</code> object.
 * Then an implementation of the {@link CompositeComponentHandler} interface
 * must be created and specified in this tag's <code>class</code> attribute. In
 * this tag's body an arbitrary number of {@link ReferenceTag} elements can be
 * placed defining the components that should be wrapped by the new composite
 * component handler. The components specified this way will be retrieved and
 * added to the composite component handler. Then this composite handler will be
 * added to the <code>Form</code> object as a complex data member. It can then
 * perform an arbitrary transformation from the containing components' native
 * data format to another format.</li>
 * </p>
 * <p>
 * This tag of course supports all attributes already defined in its base class.
 * If references are used to define components that are to be added to a
 * composite component handler, these references are resolved at the very end of
 * the builder process, thus ensuring that the referred components have already
 * been created.
 * </p>
 *
 * @see ReferenceTag
 * @author Oliver Heger
 * @version $Id: ComponentHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ComponentHandlerTag extends UseBeanBaseTag
{
    /** Stores a collection with references to other components. */
    private Collection<Reference> references;

    /**
     * Adds a reference to another component to this tag. The reference will be
     * resolved, and the corresponding component handler will be added to the
     * newly created component handler (which must be a composite handler).
     *
     * @param ref the name of the referenced component
     */
    public void addComponentReference(String ref)
    {
        addReference(new ComponentReference(ref));
    }

    /**
     * Adds a reference to a component group to this tag. The component handlers
     * of all components contained in this group will be fetched and added to
     * the newly created component handler (which must be a composite handler).
     *
     * @param ref the name of the referenced group
     */
    public void addGroupReference(String ref)
    {
        addReference(new GroupReference(ref, getContext()));
    }

    /**
     * Adds a reference to the internal list.
     *
     * @param ref the new reference
     */
    protected void addReference(Reference ref)
    {
        if (references == null)
        {
            references = new LinkedList<Reference>();
        }
        references.add(ref);
    }

    /**
     * Passes the results of this tag to its target tag. This implementation
     * checks whether the parent tag of this tag is of type {@link FieldTag}. If
     * this is the case, the newly created bean instance is added as a component
     * handler. If references to other components or groups are defined, the
     * corresponding reference objects are scheduled for resolving (the new bean
     * must then implement the <code>CompositeComponentHandler</code>
     * interface).
     *
     * @param bean the newly created bean
     * @return a flag whether the bean could be passed to the target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        if (getParent() instanceof FieldTag)
        {
            ((FieldTag) getParent())
                    .setComponentHandler((ComponentHandler<?>) bean);

            if (references != null)
            {
                if (!(bean instanceof CompositeComponentHandler<?, ?>))
                {
                    throw new JellyTagException(
                            "A CompositeComponentHandler must be specified "
                                    + "when defining references!");
                }
                ComponentBuilderData builderData = FormBaseTag
                        .getBuilderData(getContext());
                for (Reference ref : references)
                {
                    builderData.addCallBack(ref, bean);
                }
            }
            return true;
        }

        return false;
    }

    /**
     * Helper method for adding the {@code ComponentHandler} of a component to a
     * {@code CompositeComponentHandler}. This method tries to obtain the
     * component handler for the component with the specified name from the
     * builder data object. Then it adds this handler to the complex handler.
     * Note that it cannot be verified that the child {@code ComponentHandler}
     * is actually of the expected data type. Therefore it is not possible to
     * exclude that a {@code ClassCastException} might be thrown.
     *
     * @param <S> the type of the child {@code ComponentHandler}
     * @param builderData the builder data object
     * @param handler the {@code CompositeComponentHandler}
     * @param compName the name of the component to add
     * @throws FormBuilderException if the component cannot be found
     */
    protected static <S> void addComponentToCompositeHandler(
            ComponentBuilderData builderData,
            CompositeComponentHandler<?, S> handler, String compName)
            throws FormBuilderException
    {
        ComponentHandler<S> ch = (ComponentHandler<S>) builderData
                .getComponentHandler(compName);
        if (ch == null)
        {
            throw new FormBuilderException(
                    "Cannot find component handler with name " + compName);
        }
        handler.addHandler(compName, ch);
    }

    /**
     * Helper method for adding the {@code ComponentHandler} objects for all
     * components in the specified group to a {@code CompositeComponentHandler}.
     * This method iterates over all components in the specified group and calls
     * {@code #addComponentToCompositeHandler()} for each.
     *
     * @param builderData the builder data object
     * @param handler the {@code CompositeComponentHandler}
     * @param group the {@code ComponentGroup}
     * @throws FormBuilderException if a component cannot be resolved
     */
    protected static void addGroupToCompositeHandler(
            ComponentBuilderData builderData,
            CompositeComponentHandler<?, ?> handler, ComponentGroup group)
            throws FormBuilderException
    {
        for (String name : group.getComponentNames())
        {
            addComponentToCompositeHandler(builderData, handler, name);
        }
    }

    /**
     * An abstract base class for resolving references to other components.
     * Instances of this class will be added as callbacks to the global
     * <code>ComponentBuilderData</code> object. They are then invoked after all
     * components have been created, which ensures that indeed all references
     * can be resolved. Concrete sub classes deal with different types of
     * references.
     */
    abstract static class Reference implements ComponentBuilderCallBack
    {
        /** Stores the name of the reference. */
        private String referenceName;

        /**
         * Creates a new instance of <code>Reference</code> and initializes it
         * with the reference name.
         *
         * @param name the reference name
         */
        protected Reference(String name)
        {
            referenceName = name;
        }

        /**
         * Returns the reference name.
         *
         * @return the name of the reference
         */
        public String getReferenceName()
        {
            return referenceName;
        }

        /**
         * The callback method. Invokes <code>handleReference()</code> with the
         * correct arguments.
         *
         * @param builderData the builder data object
         * @param params the parameter object (a composite component handler)
         * @throws FormBuilderException if an error occurs
         */
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            handleReference(builderData, (CompositeComponentHandler<?, ?>) params,
                    getReferenceName());
        }

        /**
         * Processes the specified reference and adds all components it refers
         * to to the given composite component handler.
         *
         * @param builderData the builder data object
         * @param handler the handler to be initialized
         * @param refName the name of this reference
         * @throws FormBuilderException if an error occurs
         */
        protected abstract void handleReference(
                ComponentBuilderData builderData,
                CompositeComponentHandler<?, ?> handler, String refName)
                throws FormBuilderException;
    }

    /**
     * A concrete <code>Reference</code> sub class that deals with (simple)
     * component references.
     */
    static class ComponentReference extends Reference
    {
        /**
         * Creates a new instance of <code>ComponentReference</code> and sets
         * the reference name.
         *
         * @param refName the name of the reference
         */
        public ComponentReference(String refName)
        {
            super(refName);
        }

        /**
         * Processes this component reference.
         *
         * @param builderData the builder data object
         * @param handler the handler to be initialized
         * @param refName the name of this reference
         * @throws FormBuilderException if an error occurs
         */
        @Override
        protected void handleReference(ComponentBuilderData builderData,
                CompositeComponentHandler<?, ?> handler, String refName)
                throws FormBuilderException
        {
            addComponentToCompositeHandler(builderData, handler, refName);
        }
    }

    /**
     * A concrete <code>Reference</code> sub class that deals with group
     * references. All components belonging to the specified group will be added
     * to the composite handler.
     */
    static class GroupReference extends Reference
    {
        /** Stores a reference to the Jelly context. */
        private JellyContext context;

        /**
         * Creates a new instance of <code>GroupReference</code> and sets the
         * reference name. The Jelly context must be passed in, too, because it
         * is needed for resolving the group name.
         *
         * @param refName the name of the reference
         * @param ctx the Jelly context
         */
        public GroupReference(String refName, JellyContext ctx)
        {
            super(refName);
            context = ctx;
        }

        /**
         * Processes this group reference.
         *
         * @param builderData the builder data object
         * @param handler the handler to be initialized
         * @param refName the name of this reference
         * @throws FormBuilderException if an error occurs
         */
        @Override
        protected void handleReference(ComponentBuilderData builderData,
                CompositeComponentHandler<?, ?> handler, String refName)
                throws FormBuilderException
        {
            try
            {
                ComponentGroup group = ComponentGroup.fromContext(context,
                        refName);
                addGroupToCompositeHandler(builderData, handler, group);
            }
            catch (NoSuchElementException nex)
            {
                throw new FormBuilderException("Cannot obtain group with name "
                        + refName);
            }
        }
    }
}
