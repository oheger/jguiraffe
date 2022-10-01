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
package net.sf.jguiraffe.gui.builder.components.tags;

import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.CompositeComponentHandler;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.DefaultRadioButtonHandler;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag for creating a radio group.
 * </p>
 * <p>
 * When working with radio buttons it is not enough to insert these components
 * into the generated GUI (by using the corresponding tags), but the groups the
 * radio buttons belong to must also be defined. Without this information the
 * special property of radio buttons - that only one button can be selected -
 * could not be realized.
 * </p>
 * <p>
 * Because the group a number of radio buttons belong to need not be correlated
 * with the controls' layout, the form builder library treats radio groups as
 * non visible, non GUI components. So they are not themselves added to the
 * generated GUI; they are merely logic elements implementing the radio button
 * functionality. However, they are added as field to the enclosing {@code Form}
 * , and thus the data of the group can be queried.
 * </p>
 * <p>
 * To define a radio group the same concept as is used for logic component
 * groups is used: A <code>RadioGroupTag</code> internally creates a component
 * group analogously to the {@link ComponentGroupTag}. Components can be added
 * to this group by either nesting them inside the {@code RadioGroupTag} tag or
 * by specifying the group's name in the <code>groups</code> attribute.
 * </p>
 * <p>
 * The main difference between this tag and the {@link ComponentGroupTag} lies
 * in the fact that this tag calls the component manager's
 * <code>createRadioGroup()</code> method at the very end of the building
 * process and ensures that all elements of the group are added to the radio
 * group. It is then in the responsibility of the <code>ComponentManager</code>
 * implementation to provide a suitable implementation of the radio group
 * behavior (e.g. in Swing there is a corresponding non visual object
 * implementing the desired functionality). Though it is a non visible
 * component, the new radio group will be added to the component list of the
 * central {@link net.sf.jguiraffe.gui.builder.components.ComponentBuilderData
 * ComponentBuilderData} instance.
 * </p>
 * <p>
 * This tag is derived from {@link FieldTag} and therefore supports the typical
 * attributes of input components. However, in this context only the {@code
 * name} attribute makes sense which also determines the name of the component
 * group created for the radio group. In the body of this tag a
 * {@link ComponentHandlerTag} can be placed for specifying a custom {@code
 * ComponentHandler}. Note that the {@code ComponentHandler} specified here must
 * be derived from {@code CompositeComponentHandler} because the component
 * handlers for the single radio buttons are added to it. If no {@code
 * ComponentHandler} is specified, a default component handler for the radio
 * button group is created: an instance of the {@link DefaultRadioButtonHandler}
 * class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RadioGroupTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RadioGroupTag extends FieldTag
{
    /**
     * Performs processing before the tag's body is evaluated. This
     * implementation sets a default {@code ComponentHandler} and creates the
     * component group for managing the radio buttons.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        checkName();
        ComponentGroup.createGroup(getContext(), getName());
        setComponentHandler(new DefaultRadioButtonHandler());

        super.processBeforeBody();
    }

    /**
     * Executes this tag. After the processing of the base class this
     * implementation creates and initializes a call back object that will
     * create the radio group at the very end of the building process.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        super.process();

        ComponentHandler<?> handler = getComponentHandler();
        if (!(handler instanceof CompositeComponentHandler<?, ?>))
        {
            throw new FormBuilderException(
                    "The ComponentHandler of a radio group "
                            + "must be a CompositeComponentHandler!");
        }

        getBuilderData().addCallBack(
                new RadioGroupCreator(ComponentGroup.fromContext(getContext(),
                        getName()), (CompositeComponentHandler<?, ?>) handler,
                        getName()), null);
    }

    /**
     * A special builder call back implementation for creating the radio group
     * at the end of the building process. This class adds the component
     * handlers of all child radio buttons to the group's composite handler. It
     * creates the new radio group by calling the {@code ComponentManager} and
     * stores it in the {@code ComponentBuilderData} object.
     */
    private static class RadioGroupCreator implements ComponentBuilderCallBack
    {
        /** The group with the radio buttons that belong to the group. */
        private final ComponentGroup group;

        /** The component handler for the radio group. */
        private final CompositeComponentHandler<?, ?> groupHandler;

        /** Stores the name of the radio group to create. */
        private final String groupName;

        /**
         * Creates a new instance of {@code RadioGroupCreator} and initializes
         * it.
         *
         * @param grp the {@code ComponentGroup}
         * @param handler the handler for the group
         * @param name the name of the new group
         */
        public RadioGroupCreator(ComponentGroup grp,
                CompositeComponentHandler<?, ?> handler, String name)
        {
            group = grp;
            groupHandler = handler;
            groupName = name;
        }

        /**
         * The main call back method. Lets the component manager create the
         * radio group and adds it to the builder data under the specified name.
         *
         * @param builderData the builder data object
         * @param params the parameter object (ignored)
         * @throws FormBuilderException if a radio button component cannot be
         *         found
         */
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            Map<String, Object> comps = group.getComponents(builderData);
            builderData.storeComponent(groupName, builderData
                    .getComponentManager().createRadioGroup(comps));

            ComponentHandlerTag.addGroupToCompositeHandler(builderData,
                    groupHandler, group);
        }
    }
}
