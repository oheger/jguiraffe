/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import java.util.Collections;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.FieldTag;
import net.sf.jguiraffe.gui.builder.components.tags.TextData;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class for defining columns of a table component.
 * </p>
 * <p>
 * The tag represented by this handler class can be placed in the body of a
 * {@link TableTag}. It defines the content of one of the table's columns. Its
 * header text and its width can also be specified.
 * </p>
 * <p>
 * In the most simple case a property from the beans that form the table's model
 * is directly output. In this case only the {@code name} attribute needs to be
 * set. The table implementation will then fetch this property from the model
 * bean for the current row and display it without further modifications. It is
 * recommended to set the {@code columnClass} attribute to the fully qualified
 * name of the value class; then a suitable cell renderer can automatically be
 * chosen (e.g. for rendering boolean values as checkboxes etc.).
 * </p>
 * <p>
 * If additional formatting is required (e.g. for adapting number or date values
 * to the locale of the current user), a <em>write transformer</em> can be
 * specified. This transformer will be invoked before the value is displayed.
 * </p>
 * <p>
 * To satisfy even more complex requirements a <code>&lt;colrenderer&gt;</code>
 * tag (implemented by the {@link ColumnRendererTag} class can be placed in the
 * body of this tag. This tag can be used to define an arbitrary component (even
 * a container with an arbitrary number of child components) that will be used
 * for painting the cells in this row. This is especially useful if a column in
 * a table should contain a complete (sub) form.
 * </p>
 * <p>
 * For editable columns similar variants are possible. In the simplest scenario
 * only the property name (and eventually the column class) is specified. The
 * table will then use an appropriate default cell editor for editing values in
 * this column. If formatting and validating is involved, corresponding
 * transformers and validators can be specified using nested tags. This happens
 * in an analogous way as for standard input components. The most complex
 * scenario is again to use a nested tag (an <code>&lt;coleditor&gt;</code> tag
 * in this case) for defining a complete (sub) form as editor for this column.
 * </p>
 * <p>
 * The following table lists all attributes supported by this tag:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>This attribute defines the name of the property (of the beans that belong
 * to the model) that is to be displayed in this column. The property will be
 * read using reflection from the model beans. This attribute is mandatory even
 * if specific forms are defined for both the column renderer and the column
 * editor.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">columnClass</td>
 * <td>Here the class of the data to be displayed in this column can be
 * specified. If no specific renderer and/or editor is defined for this column,
 * the class will be used to determine a suitable default renderer or editor
 * class. There are multiple ways of defining the column class using this
 * attribute:
 * <ul>
 * <li>An instance of {@link ColumnClass} or the name of a constant defined in
 * this class can be specified. In this case a logic type for the column is set.
 * It will be interpreted and transformed according to the UI toolkit in use.
 * This is the most portable way of defining the column class.</li>
 * <li>A Java class or the fully-qualified name of a class can be specified.
 * This class is passed to the underlying UI toolkit (which of course should be
 * able to deal with it).</li>
 * <li>If the column class is not specified, the most generic default renderer
 * is used for this column.</li>
 * </ul>
 * </td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">width</td>
 * <td>Defines the absolute width of this table. The value specified here can be
 * a {@link NumberWithUnit}.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">percentWidth</td>
 * <td>Using this attribute a relative width of this column in percent can be
 * specified. If columns with a percent width are used, the algorithm to
 * determine the widths of columns is as follows:
 * <ul>
 * <li>First columns with a fixed width are set. The remaining space is
 * calculated (i.e. the width of the table minus the sum of the widths of the
 * columns with a fixed width).</li>
 * <li>For all columns that do not have a fixed width the percent values are
 * determined: If a percent value is already set, it is used. For all other
 * columns the percent width is set to a value so that the percent values sum up
 * to 100 percent. (If this is not possible because the values specified sum up
 * to a number greater than 100, an exception is thrown when creating the
 * table.)</li>
 * <li>The remaining space is distributed to the columns based on their percent
 * value.</li>
 * </ul>
 * Using this algorithm it is possible to mix columns with fix and with relative
 * sizes.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">header</td>
 * <td>With this attribute the header text for this column can directly be
 * specified.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">headerres</td>
 * <td>Specifies the header text for this column using a resource ID.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">resgrp</td>
 * <td>Here are resource group for resolving the header text can be specified.
 * This attribute is only evaluated if the <code>headerres</code> attribute is
 * set. If no specific resource group is provided, the default group will be
 * used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">editable</td>
 * <td>This boolean attribute determines whether this column can be edited. If
 * it is undefined, the value of the enclosing <code>&lt;table&gt;</code> tag's
 * <code>editable</code> attribute is used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * In the body of this tag the <code>&lt;colrenderer&gt;</code> and
 * <code>&lt;coleditor&gt;</code> tags can appear. Other content is not allowed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableColumnTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TableColumnTag extends FieldTag
{
    /** Stores the fields used by the renderer of this column. */
    private Set<String> renderFields;

    /** Stores the fields used by the editor of this column. */
    private Set<String> editFields;

    /** Stores the data class for this column. */
    private Class<?> dataClass;

    /** The logic data class of this column if defined. */
    private ColumnClass logicClass;

    /** Holds a reference to the hosting table tag. */
    private TableTag tableTag;

    /** Stores the data object for managing the header. */
    private TextData headerText;

    /** Stores the width of this column as a number with unit. */
    private NumberWithUnit width;

    /** Stores the renderer component. */
    private Object rendererComponent;

    /** Stores the editor component. */
    private Object editorComponent;

    /** Stores the editable flag. */
    private Boolean editable;

    /** Specifies the column's data class. */
    private Object columnClass;

    /** Stores the width of this column as a string. */
    private String strWidth;

    /** Stores the percent width of this column. */
    private int percentWidth;

    /**
     * Creates a new instance of <code>TableColumnTag</code>.
     */
    public TableColumnTag()
    {
        headerText = new TextData(this);
    }

    /**
     * Set method for the header attribute.
     *
     * @param s the attribute's value
     */
    public void setHeader(String s)
    {
        getHeaderText().setText(s);
    }

    /**
     * Set method for the headerres attribute.
     *
     * @param s the attribute's value
     */
    public void setHeaderres(String s)
    {
        getHeaderText().setTextres(s);
    }

    /**
     * Set method for the resgrp attribute.
     *
     * @param s the attribute's value
     */
    public void setResgrp(String s)
    {
        getHeaderText().setResgrp(s);
    }

    /**
     * Set method for the width attribute.
     *
     * @param s the attribute's value
     */
    public void setWidth(String s)
    {
        strWidth = s;
    }

    /**
     * Set method of the {@code percentWidth} attribute.
     *
     * @param percentWidth the attribute's value
     */
    public void setPercentWidth(int percentWidth)
    {
        this.percentWidth = percentWidth;
    }

    /**
     * Set method for the editable attribute.
     *
     * @param f the attribute's value
     */
    public void setEditable(boolean f)
    {
        editable = Boolean.valueOf(f);
    }

    /**
     * Set method for the columnClass attribute.
     *
     * @param o the attribute's value
     */
    public void setColumnClass(Object o)
    {
        columnClass = o;
    }

    /**
     * Returns a set with the names of the fields of the render form that are
     * used for rendering this column. It is possible to define a renderer for a
     * column (by using a nested {@link ColumnRendererTag} tag,
     * which can consist of an arbitrary number of components. If this is done
     * for this column, this method returns the names of the components used in
     * this renderer. This information could be used for instance by a platform
     * specific implementation to decide, which fields needs to be updated
     * before the column can be painted. If no specific renderer is defined for
     * this column, an empty set will be returned here.
     *
     * @return a set with the names of the fields used by the renderer for this
     * column
     */
    public Set<String> getRenderFields()
    {
        return fetchSet(renderFields);
    }

    /**
     * Returns a set with the names of the fields of the editor form that are
     * used for editing this column. This method is analogous to
     * <code>getRenderFields()</code>, but addresses a column specific
     * editor.
     *
     * @return a set with the names of the fields used by the editor for this
     * column
     * @see #getRenderFields()
     */
    public Set<String> getEditFields()
    {
        return fetchSet(editFields);
    }

    /**
     * Returns the width of this column as a <code>NumberWithUnit</code>
     * object.
     *
     * @return the width of this column
     */
    public NumberWithUnit getColumnWidth()
    {
        return width;
    }

    /**
     * Returns the percent width of this column. A return value of 0 means that
     * no percent width is specified.
     *
     * @return the percent width of this column
     */
    public int getPercentWidth()
    {
        return percentWidth;
    }

    /**
     * Returns a <code>TextData</code> object defining the header text of this
     * column.
     *
     * @return a data object for this column's header
     */
    public TextData getHeaderText()
    {
        return headerText;
    }

    /**
     * Returns the logic data class of this column if available. It is possible
     * to define the type of the data to be displayed in this column in the
     * toolkit-independent way by passing the value of a constant of the
     * {@link ColumnClass} enumeration to the {@link #setColumnClass(Object)}
     * method. If this was done, the logic column class can be queried with this
     * method. It has to be interpreted and converted depending on the currently
     * used UI toolkit. If <b>null</b> is returned, no logic column class was
     * set, and {@link #getDataClass()} should be used instead to obtain the
     * "real" column class.
     *
     * @return the logic column class (can be <b>null</b>)
     */
    public ColumnClass getLogicDataClass()
    {
        return logicClass;
    }

    /**
     * Returns the class of the data to be displayed in this column. If
     * undefined, the generic base class <code>java.lang.Object</code> will be
     * returned.
     *
     * @return the data class for this column
     */
    public Class<?> getDataClass()
    {
        return (dataClass != null) ? dataClass : Object.class;
    }

    /**
     * Returns the component to be used for rendering this column. If a specific
     * column renderer has been set using a nested
     * {@link ColumnRendererTag} tag, this component is returned
     * here. Otherwise the return value is <b>null</b>.
     *
     * @return the renderer component for this column
     */
    public Object getRendererComponent()
    {
        return rendererComponent;
    }

    /**
     * Returns the component to be used for editing this column. If a specific
     * column editor has been set using a nested
     * {@link ColumnEditorTag} tag, this component is returned here.
     * Otherwise the return value is <b>null</b>.
     *
     * @return the editor component for this column
     */
    public Object getEditorComponent()
    {
        return editorComponent;
    }

    /**
     * Returns a reference to the hosting table tag.
     *
     * @return the table tag this column tag belongs to
     */
    protected TableTag getTableTag()
    {
        return tableTag;
    }

    /**
     * Inserts the field handler to the form that is automatically constructed
     * during the build process. This implementation checks both the table's
     * renderer and editor form whether a field handler for this column's
     * property has already been added. Only if this is not the case, the
     * default field handler for this column will be added. Further the table
     * tag will be informed about the new column.
     *
     * @param fieldHandler the field handler to be added
     */
    @Override
    protected void insertField(FieldHandler fieldHandler)
    {
        FieldHandler fh = new ColumnFieldHandler(fieldHandler);
        doInsertFieldHandler(getTableTag().getRowRenderForm(), fh);
        doInsertFieldHandler(getTableTag().getRowEditForm(), fh);
        getTableTag().addColumn(this);
    }

    /**
     * Performs processing before the tag's body is evaluated. This
     * implementation will perform some checks of the set attributes.
     *
     * @throws JellyTagException if the tag is incorrectly used
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        super.processBeforeBody();
        tableTag = (TableTag) findAncestorWithClass(TableTag.class);
        if (tableTag == null)
        {
            throw new FormBuilderException(
                    "Column tag must be nested inside a table tag!");
        }
        if (strWidth != null)
        {
            width = new NumberWithUnit(strWidth);
        }
        initColumnClass();

        ComponentHandlerImpl handler = new ComponentHandlerImpl();
        handler.setType(getDataClass());
        setComponentHandler(handler);
    }

    /**
     * Sets the names of the fields used in the specific renderer for this
     * column. This method will be called by nested tags.
     *
     * @param fields the set with the field names
     */
    protected void initRenderFields(Set<String> fields)
    {
        renderFields = fields;
    }

    /**
     * Sets the names of the fields used in the specific editor for this column.
     * This method will be called by nested tags.
     *
     * @param fields the set with the field names
     */
    protected void initEditFields(Set<String> fields)
    {
        editFields = fields;
    }

    /**
     * Sets the renderer component to be used for this column. This method will
     * be called by nested tags.
     *
     * @param rendererComponent the renderer component to be used for this
     * column
     */
    protected void initRendererComponent(Object rendererComponent)
    {
        this.rendererComponent = rendererComponent;
    }

    /**
     * Sets the editor component to be used for this column. This method will be
     * called by nested tags.
     *
     * @param editorComponent the editor component to be used for this column
     */
    protected void initEditorComponent(Object editorComponent)
    {
        this.editorComponent = editorComponent;
    }

    /**
     * Returns the editable flag. This may be <b>null</b> if no editable flag
     * was explicitly set. In this case the table's editable flag should be
     * used.
     *
     * @return the editable flag for this column
     * @see TableTag#isColumnEditable(TableColumnTag)
     */
    Boolean getEditable()
    {
        return editable;
    }

    /**
     * Conditionally inserts the specified field handler into the given form.
     *
     * @param form the form
     * @param fieldHandler the field handler
     */
    private void doInsertFieldHandler(Form form, FieldHandler fieldHandler)
    {
        if (form.getField(getName()) == null)
        {
            form.addField(getName(), fieldHandler);
        }
    }

    /**
     * Initializes the class of this column. If a logic column class is defined,
     * this one is used. Otherwise, it is checked whether a normal Java class is
     * specified.
     *
     * @throws net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException
     *         if a conversion error of the data class occurs
     */
    private void initColumnClass()
    {
        if (columnClass instanceof ColumnClass)
        {
            logicClass = (ColumnClass) columnClass;
        }

        else if (columnClass != null)
        {
            String clsName = columnClass.toString();
            for (ColumnClass cc : ColumnClass.values())
            {
                if (cc.name().equalsIgnoreCase(clsName))
                {
                    logicClass = cc;
                    break;
                }
            }

            if (logicClass == null)
            {
                dataClass = convertToClass(columnClass);
            }
        }
    }

    /**
     * Helper method for obtaining a set with column names. Ensures that always
     * a non-modifiable, non-null set is returned.
     *
     * @param s the source set
     * @return the resulting set
     */
    private static Set<String> fetchSet(Set<String> s)
    {
        Set<String> result;
        if (s != null)
        {
            result = Collections.unmodifiableSet(s);
        }
        else
        {
            result = Collections.emptySet();
        }
        return result;
    }
}
