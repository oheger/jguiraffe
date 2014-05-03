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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.Composite;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.builder.components.tags.ScrollSizeSupport;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag that creates a table component.
 * </p>
 * <p>
 * Tables certainly belong to the most complex GUI elements. With this tag and
 * some auxiliary tags that can be placed in the body of this tag such tables
 * can be created and initialized. Though tables can be used for editing data
 * they are no typical input components because they do not store their data in
 * a form bean using a <code>ComponentHandler</code>. Instead they are
 * initialized with a <em>model</em>, which is simply a collection of Java
 * beans. For each element in this collection a row will be displayed in the
 * table. (The column's are derived from the table's column definition; see
 * below.) So tables can be directly used for visualizing collections of data or
 * manipulating single records.
 * </p>
 * <p>
 * This tag is used like a normal component tag: simply place it in a builder
 * script at the desired position according to the current layout. With the
 * tag's attributes the model (i.e. the collection with the data objects) is
 * specified. The exact structure of the table is specified by nested tags.
 * </p>
 * <p>
 * For each column to be displayed in the table a <code>&lt;column&gt;</code>
 * tag (implemented by the {@link TableColumnTag} class) must be placed in the body
 * of the table tag. This tag determines the column's header and the name of the
 * property of the model objects that is to be displayed in this column.
 * Further, a {@link net.sf.jguiraffe.transform.Transformer Transformer} can be
 * specified for formatting the data, and - in case of an editable column - a
 * validator. These attributes are analogous to standard input components. More
 * information can be found in the documentation of the <code>ColumnTag</code>.
 * </p>
 * <p>
 * Table tags are input component tags, which means that for each table a
 * component handler will be created. This is a specialized handler that can be
 * used to access specific table functionality. Per default this handler will
 * not be added to the form's fields, but with setting the <code>noField</code>
 * attribute to <b>false</b> this behavior can be changed.
 * </p>
 * <p>
 * The following table lists the attributes supported by the
 * <code>TableTag</code> tag handler class:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">model</td>
 * <td>Here the name of a bean must be provided that serves as the data model
 * for the table. This bean must be a collection. If it implements the
 * <code>java.util.List</code> interface, it is directly used. Otherwise it is
 * copied into a new list because direct access to the elements by index is
 * needed. The collection can contain arbitrary Java beans whose properties will
 * be accessed using reflection. The bean is looked up in the current bean
 * context.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">editable</td>
 * <td>This boolean property determines whether the table is read-only or can be
 * edited. Here only the default value is set; it is possible to override this
 * value for specific columns. If this attribute is not provided, <b>false</b>
 * is assumed as the default value.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">selectionBackground</td>
 * <td>With this property the background color for selected cells can be set. If
 * ommitted, the default color will be used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">selectionForeground</td>
 * <td>With this property the foreground color for selected cells can be set. If
 * ommitted, the default color will be used.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">multiSelection</td>
 * <td>This boolean property indicates whether the created table should support
 * multi-selection, i.e. multiple rows can be selected at the same time. If the
 * attribute is missing, <b>false</b> is the default value.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollWidth</td>
 * <td>Here the preferred width of the scroll pane enclosing the table can be
 * specified as a number with unit (e.g. &quot;1.5cm&quot;). If specified, the
 * scroll pane will have exactly this preferred width. Otherwise, the width is
 * determined by the preferred width of the table.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valing="top">scrollHeight</td>
 * <td>Here the preferred height of the scroll pane enclosing the table can be
 * specified as a number with unit (e.g. &quot;10dlu&quot;). If specified, the
 * scroll pane will have exactly this preferred height. Otherwise, the height is
 * determined by the preferred height of the table.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>If this attribute is set, the table tag will store a reference to itself
 * in the Jelly context under the name specified here. This is especially useful
 * for testing purposes. The variable created by this meachanism can be used for
 * instance to query the internally created form objects.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * There are some further objects that can be associated with a table and are
 * defined by nested tags:
 * <ul>
 * <li>{@link TableSelectionHandler}s for both renderer and editor components.
 * These objects can be specified using nested {@link TableSelectionHandlerTag}
 * tags.</li>
 * <li>A {@link TableEditorValidationHandler} for getting notifications about
 * validation events related to column editors. A concrete implementation of
 * this interface can be specified using the
 * {@link TableEditorValidationHandlerTag} tag.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TableTag extends InputComponentTag implements Composite,
        ScrollSizeSupport
{
    /** Stores the form for rendering a row. */
    private Form renderForm;

    /** Stores the form for editing a row. */
    private Form editForm;

    /** A collection for storing the columns defined in the body. */
    private final List<TableColumnTag> columns;

    /** Stores the model of this table. */
    private Collection<?> tableModel;

    /** Stores the editor validation handler if one was set.*/
    private TableEditorValidationHandler editorValidationHandler;

    /** Stores the selection handler for renderer components.*/
    private TableSelectionHandler rendererSelectionHandler;

    /** Stores the selection handler for editor components.*/
    private TableSelectionHandler editorSelectionHandler;

    /** The controller for the table's columns. */
    private TableColumnWidthController columnWidthController;

    /** Stores the selection foreground color.*/
    private Color selectionForegroundColor;

    /** Stores the selection background color.*/
    private Color selectionBackgroundColor;

    /** Stores the preferred scroll width as a number with unit. */
    private NumberWithUnit preferredScrollWidth;

    /** Stores the preferred scroll height as a number with unit. */
    private NumberWithUnit preferredScrollHeight;

    /** Stores the selection foreground color as string. */
    private String selectionForeground;

    /** Stores the selection background color as string. */
    private String selectionBackground;

    /** The specification of the preferred scroll width as string. */
    private String scrollWidth;

    /** The specification of the preferred scroll height as string. */
    private String scrollHeight;

    /** Stores the name of the table's model. */
    private String modelName;

    /** Stores the name of the variable, under which this tag is to be stored. */
    private String varName;

    /**
     * The title of the message box that will be displayed if validation fails
     * when using a custom editor.
     */
    private String validationErrorTitle;

    /** Stores the editable flag. */
    private boolean editable;

    /** Stores the multi-selection flag.*/
    private boolean multiSelection;

    /** Stores a flag whether invalid components have been added. */
    private boolean invalidContent;

    /**
     * Creates a new instance of {@code TableTag}.
     */
    public TableTag()
    {
        columns = new ArrayList<TableColumnTag>();
        setNoField(true);
    }

    /**
     * Returns the selection background color as a string.
     *
     * @return the selection background color (can be <b>null</b>)
     */
    public String getSelectionBackground()
    {
        return selectionBackground;
    }

    /**
     * Set method for the selectionBackground attribute.
     *
     * @param selectionBackground the value of the attribute
     */
    public void setSelectionBackground(String selectionBackground)
    {
        this.selectionBackground = selectionBackground;
    }

    /**
     * Returns the selection foreground color as a string.
     *
     * @return the selection foreground color (can be <b>null</b>)
     */
    public String getSelectionForeground()
    {
        return selectionForeground;
    }

    /**
     * Set method for the selectionForeground attribute.
     *
     * @param selectionForeground the attribute's value
     */
    public void setSelectionForeground(String selectionForeground)
    {
        this.selectionForeground = selectionForeground;
    }

    /**
     * Returns the preferred scroll width as a string.
     *
     * @return the preferred scroll width specification (can be <b>null</b>)
     */
    public String getScrollWidth()
    {
        return scrollWidth;
    }

    /**
     * Set method of the {@code scrollWidth} attribute.
     *
     * @param scrollWidth the attribute's value
     */
    public void setScrollWidth(String scrollWidth)
    {
        this.scrollWidth = scrollWidth;
    }

    /**
     * Returns the preferred scroll height as a string.
     *
     * @return the preferred scroll height specification (can be <b>null</b>)
     */
    public String getScrollHeight()
    {
        return scrollHeight;
    }

    /**
     * Set method of the {@code scrollHeight} attribute.
     *
     * @param scrollHeight the attribute's value
     */
    public void setScrollHeight(String scrollHeight)
    {
        this.scrollHeight = scrollHeight;
    }

    /**
     * Returns the selection background color as <code>Color</code> object.
     * This is the value set by the <code>setSelectionBackground()</code>
     * method transformed into a <code>Color</code> representation.
     *
     * @return the selection background color
     */
    public Color getSelectionBackgroundColor()
    {
        return selectionBackgroundColor;
    }

    /**
     * Returns the selection foreground color as <code>Color</code> object.
     * This is the value set by the <code>setSelectionForeground()</code>
     * method transformed into a <code>Color</code> representation.
     *
     * @return the selection foreground color
     */
    public Color getSelectionForegroundColor()
    {
        return selectionForegroundColor;
    }

    /**
     * Returns the preferred scroll width as a number with unit. This is the
     * value set using the {@link #setScrollWidth(String)} method. It has been
     * converted to a {@link NumberWithUnit}. If no preferred scroll width has
     * been set, this method returns {@link NumberWithUnit#ZERO}.
     *
     * @return the preferred scroll width
     */
    public NumberWithUnit getPreferredScrollWidth()
    {
        return preferredScrollWidth;
    }

    /**
     * Returns the preferred scroll height as a number with unit. This is the
     * value set using the {@link #setScrollHeight(String)} method. It has been
     * converted to a {@link NumberWithUnit}. If no preferred scroll height has
     * been set, this method returns {@link NumberWithUnit#ZERO}.
     *
     * @return the preferred scroll height
     */
    public NumberWithUnit getPreferredScrollHeight()
    {
        return preferredScrollHeight;
    }

    /**
     * Set method for the model attribute.
     *
     * @param s the value of the attribute
     */
    public void setModel(String s)
    {
        modelName = s;
    }

    /**
     * Set method of the editable attribute.
     *
     * @param f the value of the attribute
     */
    public void setEditable(boolean f)
    {
        editable = f;
    }

    /**
     * Returns a flag whether multi-selection is enabled for this table.
     *
     * @return the multi selection flag
     */
    public boolean isMultiSelection()
    {
        return multiSelection;
    }

    /**
     * Set method for the multiSelection attribute.
     *
     * @param multiSelection the attribute's value
     */
    public void setMultiSelection(boolean multiSelection)
    {
        this.multiSelection = multiSelection;
    }

    /**
     * Set method of the var attribute.
     *
     * @param s the value of the attribute
     */
    public void setVar(String s)
    {
        varName = s;
    }

    /**
     * Returns a flag whether the table is editable. Note that this flag does
     * not control the editable state of the full table, it only defines the
     * default state for columns. A column can override this flag.
     *
     * @return a flag whether the table is editable
     */
    public boolean isTableEditable()
    {
        return editable;
    }

    /**
     * Returns a flag whether the specified column is editable. This method
     * should be used to find out whether the column can be edited or not. It
     * also takes the table's default settings into account.
     *
     * @param colTag the tag representing the column to test
     * @return a flag whether this column can be edited
     */
    public boolean isColumnEditable(TableColumnTag colTag)
    {
        return (colTag.getEditable() != null) ? colTag.getEditable()
                .booleanValue() : isTableEditable();
    }

    /**
     * Returns the selection handler for editor components.
     *
     * @return the editor selection handler
     */
    public TableSelectionHandler getEditorSelectionHandler()
    {
        return editorSelectionHandler;
    }

    /**
     * Sets the selection handler for editor components. This method will be
     * invoked by tags in the body to set a concrete implementation of the
     * <code>{@link TableSelectionHandler}</code> interface.
     *
     * @param editorSelectionHandler the selection handler for editor components
     */
    public void setEditorSelectionHandler(
            TableSelectionHandler editorSelectionHandler)
    {
        this.editorSelectionHandler = editorSelectionHandler;
    }

    /**
     * Returns the editor validation handler.
     *
     * @return the editor validation handler
     */
    public TableEditorValidationHandler getEditorValidationHandler()
    {
        return editorValidationHandler;
    }

    /**
     * Sets the editor validation handler. This method will be called by nested
     * tags to set a concrete implementation of the
     * <code>{@link TableEditorValidationHandler}</code> interface.
     *
     * @param editorValidationHandler the editor validation handler
     */
    public void setEditorValidationHandler(
            TableEditorValidationHandler editorValidationHandler)
    {
        this.editorValidationHandler = editorValidationHandler;
    }

    /**
     * Returns the selection handler for renderer components.
     *
     * @return the renderer selection handler
     */
    public TableSelectionHandler getRendererSelectionHandler()
    {
        return rendererSelectionHandler;
    }

    /**
     * Sets the selection handler for renderer components. This method will be
     * invoked by tags in the body to set a concrete implementation of the
     * <code>{@link TableSelectionHandler}</code> interface.
     *
     * @param rendererSelectionHandler the selection handler for renderer
     * components
     */
    public void setRendererSelectionHandler(
            TableSelectionHandler rendererSelectionHandler)
    {
        this.rendererSelectionHandler = rendererSelectionHandler;
    }

    /**
     * Returns the title of the message box that is displayed if validation
     * fails.
     *
     * @return the message box title
     */
    public String getValidationErrorTitle()
    {
        return validationErrorTitle;
    }

    /**
     * Sets the title of the message box that is displayed if validation fails.
     * This property can be evaluated by the current
     * <code>{@link TableEditorValidationHandler}</code>. This object is
     * triggered whenever validation of user input in a custom editor fails. In
     * this case typically a message box with the validation error message(s) is
     * displayed. With this property the caption of this message box is
     * specified. It is typically set by a nested
     * {@link TableEditorValidationHandlerTag} tag handler.
     *
     * @param validationErrorTitle the caption of a validation error message box
     */
    public void setValidationErrorCaption(String validationErrorTitle)
    {
        this.validationErrorTitle = validationErrorTitle;
    }

    /**
     * Returns a collection with the column tags defined for this table. With
     * this method all information about the existing columns can be obtained.
     *
     * @return a collection with the tags representing the columns of this table
     */
    public Collection<TableColumnTag> getColumns()
    {
        return columns;
    }

    /**
     * Returns the tag for the column with the given index.
     *
     * @param index the column index (0-based)
     * @return the column with this index
     */
    public TableColumnTag getColumn(int index)
    {
        return columns.get(index);
    }

    /**
     * Returns the number of columns of this table.
     *
     * @return the number of columns of the represented table
     */
    public int getColumnCount()
    {
        return columns.size();
    }

    /**
     * Returns the form with the renderers defined for this table. All
     * components defined in <code>&lt;colrenderer&gt;</code> tags for columns
     * of this table will be collected into a <code>Form</code> object. For
     * columns that do not define their own renderers default
     * {@link net.sf.jguiraffe.gui.forms.FieldHandler FieldHandler} objects will
     * be added to this form. So this form object contains a complete
     * representation of the data of a single row. Platform specific
     * implementations can make use of this object when the table is to be
     * displayed.
     *
     * @return a form containing the components for rendering a row
     */
    public Form getRowRenderForm()
    {
        return renderForm;
    }

    /**
     * Returns the form with the editors defined for this table. This method is
     * analogous to <code>getRowRenderForm()</code>, but the returned
     * <code>Form</code> object contains the defined editor components (plus the
     * default {@link net.sf.jguiraffe.gui.forms.FieldHandler FieldHandler}
     * objects to be used for columns that do not have their own editor).
     *
     * @return a form containing the components for rendering a row
     * @see #getRowRenderForm()
     */
    public Form getRowEditForm()
    {
        return editForm;
    }

    /**
     * Returns the model of this table. This is a collection with arbitrary
     * beans, which is fetched from the Jelly context.
     *
     * @return the collection that acts as the table's model
     */
    public Collection<?> getTableModel()
    {
        return tableModel;
    }

    /**
     * Adds a tag representing a column to this table tag. This method will be
     * called by nested tags.
     *
     * @param colTag the column tag to be added
     */
    public void addColumn(TableColumnTag colTag)
    {
        columns.add(colTag);
    }

    /**
     * Returns the {@code TableColumnWidthController} for the columns defined
     * for this table. This method can be called after execution of this tag. It
     * creates the {@code TableColumnWidthController} object on demand and
     * initializes with all columns defined for this table.
     *
     * @return the {@code TableColumnWidthController} for this table
     * @throws FormBuilderException if the controller cannot be created
     */
    public TableColumnWidthController getColumnWidthController()
            throws FormBuilderException
    {
        if (columnWidthController == null)
        {
            columnWidthController = TableColumnWidthController
                    .newInstance(this);
        }
        return columnWidthController;
    }

    /**
     * Adds an element to this container. This tag implements the
     * {@link Composite} interface so that the components created by the columns
     * defined in its body get automatically added and do not mess up the
     * hosting container tag. This makes it also possible to verify that only
     * {@link TableColumnTag} tags are placed in the body - these tags create
     * component handlers that do not have a component.
     *
     * @param comp the component to be added
     * @param constraints a constraints object
     */
    public void addComponent(Object comp, Object constraints)
    {
        if (comp != null && !(comp instanceof ColumnComponentTag))
        {
            invalidContent = true;
        }
    }

    /**
     * Returns an object representing the container. This method from the
     * <code>Composite</code> interface is not supported by this tag.
     *
     * @return the container object
     */
    public Object getContainer()
    {
        throw new UnsupportedOperationException(
                "getContainer() method not supported!");
    }

    /**
     * Sets a layout object for this container. This method from the
     * <code>Composite</code> interface is not supported by this tag.
     *
     * @param layout the layout to be set
     */
    public void setLayout(Object layout)
    {
        throw new UnsupportedOperationException(
                "setLayout() method not supported!");
    }

    /**
     * Processes this tag before the body is executed. This implementation
     * checks for some required attributes and performs some initialization.
     *
     * @throws JellyTagException if a jelly related problem occurs
     * @throws FormBuilderException if the tag is incorrectly used
     * @throws net.sf.jguiraffe.di.InjectionException if the model bean cannot
     *         be resolved
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        if (modelName == null)
        {
            throw new MissingAttributeException("model");
        }
        Object model = resolveModel(modelName);
        if (model == null)
        {
            throw new JellyTagException("Model variable not found: "
                    + modelName);
        }
        if (!(model instanceof Collection<?>))
        {
            throw new FormBuilderException(
                    "Invalid model: Model is no collection!");
        }
        tableModel = (Collection<?>) model;

        renderForm = createForm();
        editForm = createForm();
        if (varName != null)
        {
            getContext().setVariable(varName, this);
        }

        selectionBackgroundColor = ColorHelper
                .resolveColor(getSelectionBackground());
        selectionForegroundColor = ColorHelper
                .resolveColor(getSelectionForeground());
        preferredScrollWidth = convertToNumberWithUnit(getScrollWidth(),
                NumberWithUnit.ZERO);
        preferredScrollHeight = convertToNumberWithUnit(getScrollHeight(),
                NumberWithUnit.ZERO);

        super.processBeforeBody();
    }

    /**
     * Processes this tag. This implementation performs additional validity
     * checks regarding the definitions of the table's columns.
     *
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is used incorrectly
     */
    @Override
    protected void process() throws FormBuilderException, JellyTagException
    {
        super.process();
        // test width definitions of the columns
        getColumnWidthController();
    }

    /**
     * Obtains the model for the table. This implementation tries to resolve the
     * model bean from the current bean context.
     *
     * @param modelName the name of the model
     * @return the bean serving as table model
     * @throws net.sf.jguiraffe.di.InjectionException if the
     *         model bean cannot be resolved
     */
    protected Object resolveModel(String modelName)
    {
        return getBuilderData().getBeanContext().getBean(modelName);
    }

    /**
     * Creates the table component. Delegates to the component manager.
     *
     * @param manager the manager
     * @param create the create flag
     * @return the new component
     * @throws FormBuilderException if an error occurs
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected ComponentHandler<?> createComponentHandler(ComponentManager manager,
            boolean create) throws FormBuilderException, JellyTagException
    {
        if (!create)
        {
            if (invalidContent)
            {
                throw new FormBuilderException(
                        "Table tag contains invalid content!");
            }
            if (getColumns().size() < 1)
            {
                throw new FormBuilderException(
                        "Table must have at least one column!");
            }
        }
        return manager.createTable(this, create);
    }

    /**
     * Creates a form object.
     *
     * @return the new form
     */
    private Form createForm()
    {
        return new Form(getBuilderData().getTransformerContext(),
                getBuilderData().getForm().getBindingStrategy());
    }
}
