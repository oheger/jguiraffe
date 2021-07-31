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

import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.layout.CellConstraints;
import net.sf.jguiraffe.gui.layout.PercentData;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * A specific layout constraints tag that creates a
 * <code>{@link net.sf.jguiraffe.gui.layout.PercentData PercentData}</code>
 * object, i.e. layout constraints for the <code>PercentLayout</code> class.
 * </p>
 * <p>
 * The following attributes are supported by this tag:
 * </p>
 * <p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">col</td>
 * <td>Defines the number of the column in the layout grid.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">row</td>
 * <td>Defines the number of the row in the layout grid.</td>
 * <td valign="top">no</td>
 * </tr>
 * <tr>
 * <td valign="top">spanx</td>
 * <td>Defines the number of columns occupied by the component. Defaults to 1.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">spany</td>
 * <td>Defines the number of rows occupied by the component. Defaults to 1.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">colconstr</td>
 * <td>Allows to define column constraints. If this feature is used, a string
 * must be specified that can be parsed by the cell constraints class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">rowconstr</td>
 * <td>Allows to define row constraints. If this feature is used, a string must
 * be specified that can be parsed by the cell constraints class.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetCol</td>
 * <td>Defines the target column. This is important only if the component spans
 * multiple columns.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetRow</td>
 * <td>Defines the target row. This is important only if the component spans
 * multiple rows.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentConstraintsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentConstraintsTag extends ConstraintsTag
{
    /** Constant for the name of the shared builder in the context. */
    private static final String BUILDER = PercentConstraintsTag.class.getName()
            + ".percentDataBuilder";

    /** Stores the column number. */
    private int col = -1;

    /** Stores the row number. */
    private int row = -1;

    /** Stores the span x. */
    private int spanx = 1;

    /** Stores the span y. */
    private int spany = 1;

    /** Stores the target column. */
    private int targetCol = -1;

    /** Stores the target row. */
    private int targetRow = -1;

    /** Stores the col constraints text. */
    private String colconstr;

    /** Stores the row constraints text. */
    private String rowconstr;

    /**
     * Returns the column number in the layout grid.
     *
     * @return the column number
     */
    public int getCol()
    {
        return col;
    }

    /**
     * Setter method for the col attribute.
     *
     * @param col the attribute value
     */
    public void setCol(int col)
    {
        this.col = col;
    }

    /**
     * Returns the column constraints as string.
     *
     * @return the column constraints
     */
    public String getColconstr()
    {
        return colconstr;
    }

    /**
     * Setter method for the colconstr attribute.
     *
     * @param colconstr the attribute value
     */
    public void setColconstr(String colconstr)
    {
        this.colconstr = colconstr;
    }

    /**
     * Returns the row number in the layout grid.
     *
     * @return the row number
     */
    public int getRow()
    {
        return row;
    }

    /**
     * Setter method for the row attribute.
     *
     * @param row the attribute value
     */
    public void setRow(int row)
    {
        this.row = row;
    }

    /**
     * Returns the row constraints as string.
     *
     * @return the row constraints
     */
    public String getRowconstr()
    {
        return rowconstr;
    }

    /**
     * Setter method for the rowconstr attribute.
     *
     * @param rowconstr the attribute value
     */
    public void setRowconstr(String rowconstr)
    {
        this.rowconstr = rowconstr;
    }

    /**
     * Returns the number of occupied columns.
     *
     * @return the column span
     */
    public int getSpanx()
    {
        return spanx;
    }

    /**
     * Setter method for the spanx attribute.
     *
     * @param spanx the attribute value
     */
    public void setSpanx(int spanx)
    {
        this.spanx = spanx;
    }

    /**
     * Returns the number of occupied rows.
     *
     * @return the row span
     */
    public int getSpany()
    {
        return spany;
    }

    /**
     * Setter method for the spany attribute.
     *
     * @param spany the attribute value
     */
    public void setSpany(int spany)
    {
        this.spany = spany;
    }

    /**
     * Returns the target column.
     *
     * @return the target column
     */
    public int getTargetCol()
    {
        return targetCol;
    }

    /**
     * Sets the target column.
     *
     * @param targetCol the target column
     */
    public void setTargetCol(int targetCol)
    {
        this.targetCol = targetCol;
    }

    /**
     * Returns the target row.
     *
     * @return the target row
     */
    public int getTargetRow()
    {
        return targetRow;
    }

    /**
     * Sets the target row.
     *
     * @param targetRow the target row
     */
    public void setTargetRow(int targetRow)
    {
        this.targetRow = targetRow;
    }

    /**
     * Creates the constraints object. This implementation creates an instance
     * of the
     * <code>{@link net.sf.jguiraffe.gui.layout.PercentData PercentData}</code>
     * class.
     *
     * @param manager the component manager (not used)
     * @return the new constraints
     * @throws FormBuilderException if an error occurs
     * @throws MissingAttributeException if a required attribute is missing
     */
    @Override
    protected Object createConstraints(ComponentManager manager)
            throws FormBuilderException, MissingAttributeException
    {
        if (getCol() < 0)
        {
            throw new MissingAttributeException("col");
        }
        if (getRow() < 0)
        {
            throw new MissingAttributeException("row");
        }

        PercentData.Builder pcb = getPercentDataBuilder();
        pcb.xy(getCol(), getRow());

        if (getSpanx() > 1)
        {
            pcb.spanX(getSpanx());
        }
        if (getSpany() > 1)
        {
            pcb.spanY(getSpany());
        }

        if (getTargetCol() >= 0)
        {
            pcb.withTargetColumn(getTargetCol());
        }
        if (getTargetRow() >= 0)
        {
            pcb.withTargetRow(getTargetRow());
        }

        if (!StringUtils.isEmpty(getColconstr()))
        {
            CellConstraints.Builder builder = PercentColConstraintsTag
                    .getColumnConstraintsBuilder(getContext());
            pcb.withColumnConstraints(builder.fromString(getColconstr()));
        }
        if (!StringUtils.isEmpty(getRowconstr()))
        {
            CellConstraints.Builder builder = PercentRowConstraintsTag
                    .getRowConstraintsBuilder(getContext());
            pcb.withRowConstraints(builder.fromString(getRowconstr()));
        }

        return pcb.create();
    }

    /**
     * Returns the shared builder for {@code PercentData} objects. This builder
     * lives in the context. It is created on first access. (No synchronization
     * is required as Jelly scripts are executed in a single thread.)
     *
     * @return the builder for {@code PercentData} objects
     */
    private PercentData.Builder getPercentDataBuilder()
    {
        PercentData.Builder builder = (PercentData.Builder) getContext()
                .getVariable(BUILDER);
        if (builder == null)
        {
            builder = new PercentData.Builder();
            getContext().setVariable(BUILDER, builder);
        }

        return builder;
    }
}
