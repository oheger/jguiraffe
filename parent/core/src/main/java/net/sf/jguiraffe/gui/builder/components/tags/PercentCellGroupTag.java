/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.layout.CellGroup;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A tag handler base class for tags that define cell groups for a percent
 * layout.
 * </p>
 * <p>
 * Tags of this type can appear in the body of a
 * {@link PercentLayoutTag}. Each tag defines a single cell
 * group. For simple groups that contain only two elements the attributes
 * <code>idx1</code> and <code>idx2</code> can be used, which take the index
 * of a column or row belonging to the group. For more complex group the
 * <code>indices</code> attribute is appropriate, which takes a string with
 * the comma separated list of cell indices.
 * </p>
 * <p>
 * This base class already implements the evaluation of the attributes and the
 * creation of the
 * {@link net.sf.jguiraffe.gui.layout.CellGroup CellGroup} object.
 * Concrete sub classes only have to ensure that the correct setter method on
 * the percent layout tag is called.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentCellGroupTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class PercentCellGroupTag extends FormBaseTag
{
    /** Stores the indices attribute. */
    private String indices;

    /** Stores the idx1 attribute. */
    private int idx1 = -1;

    /** Stores the idx2 attribute. */
    private int idx2 = -1;

    /**
     * Returns the index of the first cell that belongs to this group.
     *
     * @return the index of the first cell
     */
    public int getIdx1()
    {
        return idx1;
    }

    /**
     * Setter method for the idx1 attribute.
     *
     * @param idx1 the attribute value
     */
    public void setIdx1(int idx1)
    {
        this.idx1 = idx1;
    }

    /**
     * Returns the index of the second cell that belongs to this group.
     *
     * @return the index of the second cell
     */
    public int getIdx2()
    {
        return idx2;
    }

    /**
     * Setter method for the idx2 attribute.
     *
     * @param idx2 the attribute value
     */
    public void setIdx2(int idx2)
    {
        this.idx2 = idx2;
    }

    /**
     * Returns the indices of the cells of this group as string.
     *
     * @return the indices of the cells
     */
    public String getIndices()
    {
        return indices;
    }

    /**
     * Setter method for the indices attribute.
     *
     * @param indices the attribute value
     */
    public void setIndices(String indices)
    {
        this.indices = indices;
    }

    /**
     * Executes this tag. Creates a new cell group object and then calls
     * <code>addGroup()</code> to pass this object to the percent layout tag
     * this tag belongs to.
     *
     * @throws JellyTagException if no percent layout tag can be found
     */
    @Override
    protected void process() throws JellyTagException
    {
        PercentLayoutTag parent = (PercentLayoutTag) findAncestorWithClass(PercentLayoutTag.class);
        if (parent == null)
        {
            throw new JellyTagException(
                    "PercentCellGroupTag must be nested inside a PercentLayoutTag!");
        }
        addGroup(parent, createGroup());
    }

    /**
     * Creates the cell group object based on the attribute values.
     *
     * @return the cell group object
     * @throws MissingAttributeException if required attributes are missing
     */
    protected CellGroup createGroup() throws MissingAttributeException
    {
        if (getIdx1() >= 0 && getIdx2() >= 0)
        {
            return new CellGroup(getIdx1(), getIdx2());
        }
        else
        {
            if (!StringUtils.isEmpty(getIndices()))
            {
                return CellGroup.fromString(getIndices());
            }
            else
            {
                throw new MissingAttributeException("indices");
            }
        }
    }

    /**
     * Adds the newly created cell group object to the corresponding percent
     * layout tag. This method must be defined by concrete sub classes to call
     * the correct setter method.
     *
     * @param parent the percent layout tag
     * @param g the group to add
     */
    protected abstract void addGroup(PercentLayoutTag parent, CellGroup g);
}
