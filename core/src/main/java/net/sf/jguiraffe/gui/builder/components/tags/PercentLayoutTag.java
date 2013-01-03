/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
import java.util.Collections;
import java.util.LinkedList;

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.CellConstraints;
import net.sf.jguiraffe.gui.layout.CellGroup;
import net.sf.jguiraffe.gui.layout.PercentLayout;
import net.sf.jguiraffe.gui.layout.PercentLayoutBase;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A specific layout tag that creates a Percent Layout manager.
 * </p>
 * <p>
 * With this tag an instance of the powerful percent layout can be created and
 * fully initialized. Configuration of the layout is done by nested tags that
 * define cell constraints ({@link PercentColConstraintsTag} or
 * {@link PercentRowConstraintsTag}) and cell groups ({@link PercentColGroupTag}
 * or {@link PercentRowGroupTag}). As an alternative, cell constraints can also
 * be specified using the attributes <code>columns</code> and <code>rows</code>
 * (which override constraints set by nested tags). These attributes can take
 * strings that must match the format expected by percent layout. The
 * constraints must be defined either as string attributes or by nested tags; a
 * mixture of both is not allowed.
 * </p>
 * <p>
 * As an additional attribute the {@code canShrink} attribute can be specified.
 * It determines the {@code canShrink} property of the {@code PercentLayout}
 * created by this tag, i.e. the flag whether the layout can shrink below its
 * preferred size. Default value is <b>false</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentLayoutTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentLayoutTag extends LayoutTag
{
    /** Stores the PercentLayoutBase object created from the tag's data. */
    private PercentLayoutBase percentLayout;

    /** A collection with the column constraints. */
    private Collection<CellConstraints> colConstraints;

    /** A collection with the row constraints. */
    private Collection<CellConstraints> rowConstraints;

    /** A collection with the column groups. */
    private Collection<CellGroup> colGroups;

    /** A collection with the row groups. */
    private Collection<CellGroup> rowGroups;

    /** Stores the value of the columns attribute. */
    private String columns;

    /** Stores the value of the rows attribute. */
    private String rows;

    /** The canShrink flag. */
    private boolean canShrink;

    /**
     * Returns the definition of column constraints as string.
     *
     * @return the column constraints definition as string
     */
    public String getColumns()
    {
        return columns;
    }

    /**
     * Setter method of the columns attribute.
     *
     * @param columns the attribute value
     */
    public void setColumns(String columns)
    {
        this.columns = columns;
    }

    /**
     * Returns the definition of row constraints as string.
     *
     * @return the row constraints definition as string
     */
    public String getRows()
    {
        return rows;
    }

    /**
     * Setter method of the rows attribute.
     *
     * @param rows the attribute value
     */
    public void setRows(String rows)
    {
        this.rows = rows;
    }

    /**
     * Returns the value of the {@code canShrink} flag.
     *
     * @return the flag whether the layout can shrink below its preferred size
     */
    public boolean isCanShrink()
    {
        return canShrink;
    }

    /**
     * Sets method of the {@code canShrink} attribute.
     *
     * @param canShrink the attribute's value
     */
    public void setCanShrink(boolean canShrink)
    {
        this.canShrink = canShrink;
    }

    /**
     * Returns the collection with the column groups definition.
     *
     * @return the column groups
     */
    public Collection<CellGroup> getColGroups()
    {
        return unmodifiableCollection(colGroups);
    }

    /**
     * Returns the collection with the column constraints.
     *
     * @return the column constraints
     */
    public Collection<CellConstraints> getColConstraints()
    {
        return unmodifiableCollection(colConstraints);
    }

    /**
     * Returns the collection with the row constraints.
     *
     * @return the row constraints
     */
    public Collection<CellConstraints> getRowConstraints()
    {
        return unmodifiableCollection(rowConstraints);
    }

    /**
     * Returns the collection with the row groups definition.
     *
     * @return the row groups
     */
    public Collection<CellGroup> getRowGroups()
    {
        return unmodifiableCollection(rowGroups);
    }

    /**
     * Adds the specified constraints object to the list of column constraints.
     *
     * @param c the constraints
     */
    public void addColConstraints(CellConstraints c)
    {
        if (colConstraints == null)
        {
            colConstraints = new LinkedList<CellConstraints>();
        }
        colConstraints.add(c);
    }

    /**
     * Adds the specified constraints object to the list of row constraints.
     *
     * @param c the constraints
     */
    public void addRowConstraints(CellConstraints c)
    {
        if (rowConstraints == null)
        {
            rowConstraints = new LinkedList<CellConstraints>();
        }
        rowConstraints.add(c);
    }

    /**
     * Adds the specified cell group object to the list of column groups.
     *
     * @param g the cell group
     */
    public void addColGroup(CellGroup g)
    {
        if (colGroups == null)
        {
            colGroups = new LinkedList<CellGroup>();
        }
        colGroups.add(g);
    }

    /**
     * Adds the specified cell group object to the list of row groups.
     *
     * @param g the cell group
     */
    public void addRowGroup(CellGroup g)
    {
        if (rowGroups == null)
        {
            rowGroups = new LinkedList<CellGroup>();
        }
        rowGroups.add(g);
    }

    /**
     * Returns the percent layout object managed by this tag.
     *
     * @return the percent layout object
     */
    public PercentLayoutBase getPercentLayout()
    {
        return percentLayout;
    }

    /**
     * Creates the percent layout object.
     *
     * @param manager the component manager
     * @return the newly created layout object
     * @throws MissingAttributeException if a required attribute is missing
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected Object createLayout(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException
    {
        percentLayout = createPercentLayout();
        return manager.createPercentLayout(this);
    }

    /**
     * Creates the <code>PercentLayoutBase</code> object based on the data
     * stored in this tag.
     *
     * @return the newly created layout object
     */
    protected PercentLayoutBase createPercentLayout()
    {
        PercentLayout l;

        if (StringUtils.isNotEmpty(getColumns())
                && StringUtils.isNotEmpty(getRows()))
        {
            l = new PercentLayout(getColumns(), getRows());
        }
        else
        {
            l = new PercentLayout(getColConstraints(), getRowConstraints());
        }

        for (CellGroup cg : getColGroups())
        {
            l.addColumnGroup(cg);
        }
        for (CellGroup cg : getRowGroups())
        {
            l.addRowGroup(cg);
        }

        l.setCanShrink(isCanShrink());
        return l;
    }

    /**
     * Helper method for returning an unmodifiable collection. This method
     * handles <b>null</b> collections gratefully.
     *
     * @param <T> the type of the collection
     * @param col the collection to be returned in an unmodifiable way
     * @return the corresponding unmodifiable collection
     */
    private static <T> Collection<T> unmodifiableCollection(Collection<T> col)
    {
        if (col != null)
        {
            return Collections.unmodifiableCollection(col);
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
