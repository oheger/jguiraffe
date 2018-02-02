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

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A helper class that provides some functionality for components that can
 * display an internationalized text.
 * </p>
 * <p>
 * This class supports several ways for defining a text that can be displayed by
 * a component. The text can be directly defined or by specifying a resource ID
 * and an optional resource group. Tags that want to support these features for
 * defining texts can create an instance of this class and route their attribute
 * setter methods to this instance.
 * </p>
 * <p>
 * There is an additional method to find out whether text is defined at all.
 * With the <code>getCaption()</code> method the final text can be retrieved
 * no matter how it was specified. This should greatly simplify the
 * implementation of tags with text attributes that can be internationalized.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TextData
{
    /** Holds a reference to the tag object this instance belongs to. */
    private final FormBaseTag tag;

    /** Stores the text. */
    private String text;

    /** Stores the text resource ID. */
    private String textres;

    /** Stores the resource group for the text. */
    private String resgrp;

    /**
     * Creates a new instance of <code>TextData</code> and initializes it with
     * the given tag.
     *
     * @param t the associated tag
     */
    public TextData(FormBaseTag t)
    {
        tag = t;
    }

    /**
     * Returns the associated tag.
     * @return the tag
     */
    public FormBaseTag getTag()
    {
        return tag;
    }

    /**
     * Returns the text.
     *
     * @return the text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text.
     *
     * @param text the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the resource group for the text.
     *
     * @return the resource group
     */
    public String getResgrp()
    {
        return resgrp;
    }

    /**
     * Sets the resource group. The text can be defined either directly or by
     * specifying a resource group (optional) and a resource ID.
     *
     * @param resgrp the resource group
     */
    public void setResgrp(String resgrp)
    {
        this.resgrp = resgrp;
    }

    /**
     * Returns the resource ID.
     *
     * @return the text resource ID
     */
    public String getTextres()
    {
        return textres;
    }

    /**
     * Sets the resource ID of the text. The text can be defined either directly
     * or by specifying a resource group (optional) and a resource ID.
     *
     * @param textres the text resource ID
     */
    public void setTextres(String textres)
    {
        this.textres = textres;
    }

    /**
     * Returns the final text of the associated component. This method can deal
     * with all supported ways of defining the text.
     *
     * @return the final text of this component
     */
    public String getCaption()
    {
        if (getText() != null)
        {
            return getText();
        }
        else if (getTextres() != null)
        {
            return tag.getResourceText(getResgrp(), getTextres());
        }
        else
        {
            return null;
        }
    }

    /**
     * Checks whether this object is defined. To be defined either a direct text
     * or a text resource must be specified.
     *
     * @return a flag whether this object is defined
     */
    public boolean isDefined()
    {
        return !StringUtils.isEmpty(getText())
                || !StringUtils.isEmpty(getTextres());
    }
}
