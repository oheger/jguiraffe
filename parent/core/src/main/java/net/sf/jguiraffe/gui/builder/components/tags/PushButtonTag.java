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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * An abstract base class for tag handler classes that implement push buttons
 * like checkboxes or radio buttons.
 * </p>
 * <p>
 * Push buttons have multiple common attributes that are handled by this class,
 * among them a text and an icon definition. These are implemented using the
 * <code>{@link TextIconData}</code> class.
 * </p>
 * <p>
 * This tag handler base class is the counterpart of
 * <code>{@link TextIconTag}</code> for full featured input components.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PushButtonTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class PushButtonTag extends InputComponentTag implements
        IconSupport
{
    /** Stores the text icon data object. */
    private TextIconData textIconData;

    /**
     * Creates a new instance of <code>PushButtonTag</code>.
     */
    protected PushButtonTag()
    {
        textIconData = new TextIconData(this);
    }

    /**
     * Returns the text icon data object associated with this tag.
     *
     * @return the text icon data object
     */
    public TextIconData getTextIconData()
    {
        return textIconData;
    }

    /**
     * Setter method for the icon attribute.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        getTextIconData().setIcon(icon);
    }

    /**
     * Setter method for the text attribute.
     *
     * @param text the text to set
     */
    public void setText(String text)
    {
        getTextIconData().setText(text);
    }

    /**
     * Setter method for the resgrp attribute. Defines the group for resolving
     * the resource for the component's text.
     *
     * @param resgrp the resource group
     */
    public void setResgrp(String resgrp)
    {
        getTextIconData().setResgrp(resgrp);
    }

    /**
     * Setter method for the textres attribute. Specifies the resource ID for
     * resolving the component's text
     *
     * @param textres the text resource ID
     */
    public void setTextres(String textres)
    {
        getTextIconData().setTextres(textres);
    }

    /**
     * Setter method of the alignment attribute.
     *
     * @param al the attribute value
     */
    public void setAlignment(String al)
    {
        getTextIconData().setAlignmentString(al);
    }

    /**
     * Setter method of the mnemonic attribute.
     *
     * @param m the attribute value
     */
    public void setMnemonic(String m)
    {
        getTextIconData().setMnemonicKey(m);
    }

    /**
     * Setter method of the mnemonicres attribute.
     *
     * @param resID the attribute value
     */
    public void setMnemonicres(String resID)
    {
        getTextIconData().setMnemonicResID(resID);
    }

    /**
     * Creates the component handler for the new input component. Performs
     * parameter checks and invokes <code>createPushButton()</code> that
     * really creates the component.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected ComponentHandler<?> createComponentHandler(ComponentManager manager,
            boolean create) throws FormBuilderException, JellyTagException
    {
        if (!create)
        {
            validateTag();
        }
        return createPushButton(manager, create);
    }

    /**
     * Validates the attributes of this tag. This method is invoked before the
     * push button is created. This implementation tests if either a text or an
     * icon is provided. If both are undefined, an exception will be thrown.
     *
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    protected void validateTag() throws FormBuilderException, JellyTagException
    {
        if (!getTextIconData().isDefined())
        {
            throw new MissingAttributeException("text or icon");
        }
    }

    /**
     * Creates the push button component of the correct type. This method must
     * be implemented in a concrete sub class. It has to invoke the proper
     * creation method on the passed in <code>ComponentManager</code> object.
     * The base class ensures that this method is called at the right time and
     * that all required parameters have been provided.
     *
     * @param manager the component manager
     * @param create the create flag
     * @return the new component handler
     * @throws FormBuilderException if an error occurs
     */
    protected abstract ComponentHandler<?> createPushButton(
            ComponentManager manager, boolean create)
            throws FormBuilderException;
}
