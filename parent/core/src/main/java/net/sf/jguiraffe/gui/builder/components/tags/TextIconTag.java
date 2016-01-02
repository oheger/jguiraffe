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
package net.sf.jguiraffe.gui.builder.components.tags;

import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A base class for simple components that support a display text and an icon.
 * </p>
 * <p>
 * This abstract base class provides common functionality for maintaining the
 * data needed by components that support text and an icon. A basic set of
 * attributes is supported. Derived classes only have to define their specific
 * attributes and deal with the creation of the corresponding component.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextIconTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class TextIconTag extends SimpleComponentTag implements
        IconSupport
{
    /** Stores the text icon data object. */
    private TextIconData textIconData;

    /**
     * Creates a new instance of <code>TextIconTag</code>.
     */
    protected TextIconTag()
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
     * Checks if all mandatory attributes are defined.
     *
     * @throws MissingAttributeException if attributes are missing
     */
    protected void checkAttributes() throws MissingAttributeException
    {
        if (!getTextIconData().isDefined())
        {
            throw new MissingAttributeException("text or icon");
        } /* if */
    }
}
