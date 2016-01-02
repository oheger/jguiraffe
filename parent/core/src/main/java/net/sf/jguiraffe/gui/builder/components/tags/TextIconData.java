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

import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A helper class that provides some functionality for components that can
 * display both a text and an icon.
 * </p>
 * <p>
 * This class simply holds attribute values for defining a text and/or an icon.
 * At least one of these elements must be specified. There are also some
 * evaluation methods, e.g. for retrieving the final caption (which may involve
 * a resource access).
 * </p>
 * <p>
 * Instances of this class are used by several tag classes for components that
 * provide the functionality of displaying text and icons. Because some of these
 * components are simple components and others are full featured input
 * components it is not possible to place the functionality provided by this
 * class in a common base class. Instead the delegation model has to be used.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextIconData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TextIconData extends TextData
{
    /** Stores the icon element. */
    private Object icon;

    /** Stores the alignment. */
    private TextIconAlignment alignment;

    /** Stores the mnemonic's resource ID. */
    private String mnemonicResID;

    /** Stores the mnemonic key code. */
    private String mnemonicKey;

    /**
     * Creates an instance of <code>TextIconData</code> and associates it with
     * the specified tag.
     *
     * @param tag the associated tag
     */
    public TextIconData(FormBaseTag tag)
    {
        super(tag);
        setAlignment(TextIconAlignment.LEFT);
    }

    /**
     * Returns the icon object.
     *
     * @return the icon (can be <b>null </b>)
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon object.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the alignment of the icon relative to the text.
     *
     * @return the alignment
     */
    public TextIconAlignment getAlignment()
    {
        return alignment;
    }

    /**
     * Sets the alignment of the icon relative to the text.
     *
     * @param alignment the alignment (must not be <b>null </b>)
     */
    public void setAlignment(TextIconAlignment alignment)
    {
        if (alignment == null)
        {
            throw new IllegalArgumentException("TextIconAlignment must not be null!");
        } /* if */
        this.alignment = alignment;
    }

    /**
     * Returns the name of the current alignment.
     *
     * @return the alignment name
     */
    public String getAlignmentString()
    {
        return getAlignment().name();
    }

    /**
     * Sets the alignment of the icon relative to the name as string. The passed
     * in string must be the name of one of the literals of the
     * <code>TextIconAlignment</code> class. Otherwise an
     * <code>IllegalArgumentException</code> exception will be thrown.
     *
     * @param name the name of the alignment literal
     */
    public void setAlignmentString(String name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException(
                    "Alignment string must not be null!");
        }
        setAlignment(TextIconAlignment.valueOf(name.toUpperCase()));
    }

    /**
     * Returns the resource ID for the mnemonic.
     *
     * @return the mnemonic's resource ID
     */
    public String getMnemonicResID()
    {
        return mnemonicResID;
    }

    /**
     * Sets the resource ID for the mnemonic. (As group the group set by
     * <code>{@link #setResgrp(String)}</code> will be used.)
     *
     * @param mnemonicResID the resource ID of the mnemonic
     */
    public void setMnemonicResID(String mnemonicResID)
    {
        this.mnemonicResID = mnemonicResID;
    }

    /**
     * Returns the key code of the mnemonic.
     *
     * @return the mnemonic's key code
     */
    public String getMnemonicKey()
    {
        return mnemonicKey;
    }

    /**
     * Sets the mnemonic's key code.
     *
     * @param mnemonicKey the key code
     */
    public void setMnemonicKey(String mnemonicKey)
    {
        this.mnemonicKey = mnemonicKey;
    }

    /**
     * Returns the mnemonic character. If the mnemonic is defined through a
     * resource, this resource is now resolved. If the mnemonic is a string with
     * more than a single character, only the first character is returned.
     *
     * @return the mnemonic (0 for undefined)
     */
    public char getMnemonic()
    {
        if (!StringUtils.isEmpty(getMnemonicKey()))
        {
            return mnemonicCode(getMnemonicKey());
        }
        else if (getMnemonicResID() != null)
        {
            return mnemonicCode(getTag().getResourceText(getResgrp(),
                    getMnemonicResID()));
        }
        else
        {
            return 0;
        }
    }

    /**
     * Checks whether this object is defined. To be defined at least one of the
     * elements text and icon must be specified.
     *
     * @return a flag whether this object is defined
     */
    @Override
    public boolean isDefined()
    {
        return getIcon() != null || super.isDefined();
    }

    /**
     * Converts the specified string into a mnemonic code. Extracts the first
     * character.
     * @param s the string
     * @return the key code of the mnemonic
     */
    private static char mnemonicCode(String s)
    {
        try
        {
            return s.charAt(0);
        }
        catch (IndexOutOfBoundsException iex)
        {
            return 0;
        }
    }
}
