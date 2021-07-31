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

import net.sf.jguiraffe.gui.layout.CellAlignment;
import net.sf.jguiraffe.gui.layout.CellConstraints;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * An abstract base class for tags that define cell constraints of a percent
 * layout.
 * </p>
 * <p>
 * Sub classes of this tag handler class can appear in the body of a
 * {@link PercentLayoutTag} tag to define constraints for rows or
 * columns. Each of these tags fully defines one
 * {@link net.sf.jguiraffe.gui.layout.CellConstraints CellConstraints}
 * object. The constraints (as strings) are specified using the
 * <code>constr</code> attribute. The text value of this attribute must be
 * understandable by the <code>CellConstraints</code> class. This is an
 * alternative for defining all cell constraints in a single string.
 * </p>
 * <p>
 * This base class implements the major functionality. Sub classes have to deal
 * with creating the correct constraints object and passing it to the tag
 * defining the percent layout.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentCellConstraintsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class PercentCellConstraintsTag extends FormBaseTag
{
    /** Stores the value of the constr attribute. */
    private String constr;

    /**
     * Returns the constraints as string.
     *
     * @return the constraints
     */
    public String getConstr()
    {
        return constr;
    }

    /**
     * Setter method for the constr attribute.
     *
     * @param constr the attribute value
     */
    public void setConstr(String constr)
    {
        this.constr = constr;
    }

    /**
     * Executes this tag. Calls the <code>createConstraints()</code> and
     * <code>addConstraints()</code> methods to create and initialize the
     * constraints object.
     *
     * @throws JellyTagException if no parent tag can be found
     */
    @Override
    protected void process() throws JellyTagException
    {
        PercentLayoutTag parent =
                (PercentLayoutTag) findAncestorWithClass(PercentLayoutTag.class);
        if (parent == null)
        {
            throw new JellyTagException(
                    "PercentCellConstraintsTag must be nested inside a PercentLayout tag!");
        }

        if (StringUtils.isEmpty(getConstr()))
        {
            throw new MissingAttributeException("constr");
        }

        addConstraints(parent, createConstraints());
    }

    /**
     * Returns the {@code CellConstraints.Builder} for creating {@code
     * CellConstraints} objects with the given name. Constraints builders are
     * stored in the Jelly context, one for each type of constraints (rows and
     * columns). They are created on demand and then shared between all tags
     * that need to create constraints objects. Note: As the Jelly context is
     * confined to a single thread, this is no problem, and no synchronization
     * is needed.
     *
     * @param context the Jelly context
     * @param name the name of the variable which stores the builder
     * @param defAlignment the default alignment of the builder
     * @return the {@code CellConstraints.Builder}
     */
    protected static CellConstraints.Builder getConstraintsBuilder(
            JellyContext context, String name, CellAlignment defAlignment)
    {
        CellConstraints.Builder builder = (CellConstraints.Builder) context
                .getVariable(name);
        if (builder == null)
        {
            builder = new CellConstraints.Builder();
            builder.setDefaultAlignment(defAlignment);
            context.setVariable(name, builder);
        }

        return builder;
    }

    /**
     * Creates the constraints object for the string defined by the user. This
     * method must be defined in a concrete sub class to either return a column
     * or a row constraints object.
     *
     * @return the constraints
     */
    protected abstract CellConstraints createConstraints();

    /**
     * Passes the newly created constraints object to the corresponding percent
     * layout tag. This method must be defined by concrete sub classes to call
     * the correct setter method of the parent tag.
     *
     * @param parent the percent layout tag
     * @param c the constraints object
     */
    protected abstract void addConstraints(PercentLayoutTag parent,
            CellConstraints c);
}
