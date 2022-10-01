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

import net.sf.jguiraffe.gui.builder.components.model.StaticTextData;
import net.sf.jguiraffe.gui.builder.components.model.TextIconAlignment;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * A default implementation of the <code>StaticTextData</code> interface.
 * </p>
 * <p>
 * This class defines the required properties and corresponding accessor
 * methods.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: StaticTextDataImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class StaticTextDataImpl implements StaticTextData
{
    /** Stores the text. */
    private String text;

    /** Stores the icon. */
    private Object icon;

    /** Stores the alignment. */
    private TextIconAlignment alignment;

    /**
     * Creates a new instance of <code>StaticTextDataImpl</code>.
     */
    public StaticTextDataImpl()
    {
        setAlignment(TextIconAlignment.LEFT);
    }

    /**
     * Returns the alignment of the text and icon.
     *
     * @return the alignment
     */
    public TextIconAlignment getAlignment()
    {
        return alignment;
    }

    /**
     * Sets the alignment of the text and icon.
     *
     * @param alignment the alignment
     */
    public void setAlignment(TextIconAlignment alignment)
    {
        if (alignment == null)
        {
            throw new IllegalArgumentException("Alignment must not be null!");
        }
        this.alignment = alignment;
    }

    /**
     * Returns the icon.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
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
     * @param text the text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Compares this object with another one. Two
     * <code>StaticTextDataImpl</code> objects are equal if and only if all
     * their member fields are equal.
     *
     * @param obj the object to compare to
     * @return a flag whether the objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof StaticTextDataImpl))
        {
            return false;
        }
        if (this == obj)
        {
            return true;
        }

        StaticTextDataImpl c = (StaticTextDataImpl) obj;
        return new EqualsBuilder().append(text, c.text).append(icon, c.icon)
                .append(alignment, c.alignment).isEquals();
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(text)
                .append(icon).append(alignment).toHashCode();
    }
}
