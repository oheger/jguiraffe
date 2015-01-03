/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
import java.util.StringTokenizer;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanStore;
import net.sf.jguiraffe.di.impl.DefaultBeanStore;
import net.sf.jguiraffe.di.impl.providers.ConstantBeanProvider;
import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.builder.components.DefaultFieldHandlerFactory;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.AbstractTagTest;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.forms.ComponentHandlerImpl;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;
import org.apache.commons.jelly.JellyTagException;
import org.easymock.EasyMock;

/**
 * Test class for the tag handler classes implementing the table component.
 *
 * @author Oliver Heger
 * @version $Id: TestTableTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTableTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "table";

    /** Constant for the test table builder. */
    private static final String BUILDER_TESTTABLE = "TEST_TABLE";

    /** Constant for the test table with scroll size builder. */
    private static final String BUILDER_TESTSCROLL = "TEST_TABLESCROLL";

    /** Constant for the test table percent builder. */
    private static final String BUILDER_TESTTABLEPERCENT = "TEST_TABLEPERCENT";

    /** Constant for the test empty renderer builder. */
    private static final String BUILDER_TESTEMPTYRENDER = "TEST_EMPTYRENDER";

    /** Constant for the test edit table builder. */
    private static final String BUILDER_TESTTABLEEDIT = "TEST_TABLEEDIT";

    /** Constant for the error no model builder. */
    private static final String BUILDER_ERRNOMODEL = "ERR_NOMODEL";

    /** Constant for the error invalid model builder. */
    private static final String BUILDER_ERRINVMODEL = "ERR_INVALIDMODEL";

    /** Constant for the error wrong content builder. */
    private static final String BUILDER_ERRWRONGCONTENT = "ERR_WRONGCONTENT";

    /** Constant for the error renderer too many components builder. */
    private static final String BUILDER_ERRRENDERTOOMANY = "ERR_RENDERERTOOMANY";

    /** Constant for the error column nested builder. */
    private static final String BUILDER_ERRCOLNESTED = "ERR_COLNESTED";

    /** Constant for the error renderer nested builder. */
    private static final String BUILDER_ERRRENDERERNESTED = "ERR_RENDERERNESTED";

    /** Constant for the error no columns builder. */
    private static final String BUILDER_ERRNOCOLUMN = "ERR_NOCOLUMNS";

    /** Constant for the error layout builder. */
    private static final String BUILDER_ERRLAYOUT = "ERR_LAYOUT";

    /** Constant for the error invalid number builder. */
    private static final String BUILDER_ERRINVNUMBER = "ERR_INVNUMBER";

    /** Constant for the error renderer not nested in table builder. */
    private static final String BUILDER_ERRRENDERERNOTABLE = "ERR_RENDERERNESTEDNOTAB";

    /** Constant for the error percent width builder. */
    private static final String BUILDER_ERRPERCENT = "ERR_TABLEPERCENT";

    /** Constant for the test callback builder.*/
    private static final String BUILDER_TESTCALLBACK = "TEST_CALLBACK";

    /** Constant for the test handlers builder.*/
    private static final String BUILDER_TESTHANDLERS = "TEST_HANDLERS";

    /** Constant for the error selection handler nested builder. */
    private static final String BUILDER_ERRSELHANDLERNESTED = "ERR_SELHANDLERNESTED";

    /** Constant for the error validation handler nested builder. */
    private static final String BUILDER_ERRVALHANDLERNESTED = "ERR_VALHANDLERNESTED";

    /** Constant for the test validation error title builder. */
    private static final String BUILDER_TESTVALERRTITLE = "TEST_VALERRTITLE";

    /** Constant for the test validation error titleres builder. */
    private static final String BUILDER_TESTVALERRTITLERES = "TEST_VALERRTITLERES";

    /** Constant for the test validation error titleresgrp builder. */
    private static final String BUILDER_TESTVALERRTITLERESGRP = "TEST_VALERRTITLERESGRP";

    /** Constant for the test multi select builder.*/
    private static final String BUILDER_TESTMULTISELECT = "TEST_MULTISELECT";

    /** Constant for the test no field false builder.*/
    private static final String BUILDER_TESTNOFIELDFALSE = "TEST_NOFIELDFALSE";

    /** Constant for the name of the table model variable. */
    private static final String VAR_MODEL = "tabModel";

    /** Constant for the name of the variable with the invalid table model. */
    private static final String VAR_MODEL_INVALID = "InvalidModel";

    /** Constant for the variable name, under which the table tag is stored. */
    private static final String VAR_TABLE = "tableTag";

    /** Constant for the name of the table.*/
    private static final String TABLE_NAME = "testTable";

    /** An array with the column names of the test table. */
    private static final String[] COLUMNS =
    { "firstName", "lastName", "age" };

    /** An array with the data of the table model. */
    private static final String[] MODEL_DATA =
    { "Harry H. Hirsch 45", "Arthur E. Dent 39", "R. Daneel Oliva 50",
            "Susan S. Calvin 40" };

    /** Format string for generating a representation for a column. */
    private static final String FMT_COLUMN = " COLUMN [ NAME = %s FLDVAL = "
            + ValidatorReference.class.getName() + " READTR = "
            + TransformerReference.class.getName() + " WRITETR = "
            + TransformerReference.class.getName() + " %s ]";

    /** Constant for the column definitions of the test table script. */
    private static final String TABLE_COLUMNS = " COLUMNS {"
            + column("firstName",
                    "HEADER = First name CLASS = java.lang.String "
                            + "WIDTH = NumberWithUnit [ 25px ] "
                            + "EDITABLE = false")
            + column("lastName", "HEADER = Last name LOGICCLASS = STRING "
                    + "WIDTH = NumberWithUnit [ 5.0cm ] " + "EDITABLE = false")
            + column("age", "HEADER = Age " + "CLASS = java.lang.Integer "
                    + "WIDTH = NumberWithUnit [ 1.0in ] " + "EDITABLE = true")
            + " } ] }";

    /**
     * Constant for the column definitions of the test table that uses percent
     * values for its columns widths.
     */
    private static final String TABLE_COLUMNS_PERCENT = " COLUMNS {"
            + column("firstName",
                    "HEADER = First name CLASS = java.lang.String "
                            + "PERCENTWIDTH = 25 " + "EDITABLE = false")
            + column("lastName", "HEADER = Last name LOGICCLASS = STRING "
                    + "PERCENTWIDTH = 35 " + "EDITABLE = false")
            + column("age", "HEADER = Age " + "CLASS = java.lang.Integer "
                    + "PERCENTWIDTH = 30 " + "EDITABLE = true") + " } ] }";

    /** Constant for the table result prefix. */
    private static final String TABLE_RES_PREFIX = "Container: ROOT { TABLE [ "
            + "NAME = testTable ";

    /** Constant for the default result prefix. */
    private static final String DEFAULT_RESULT_PREFIX = TABLE_RES_PREFIX
            + "SELECTIONFG = " + colorString(255, 255, 255) + " SELECTIONBG = "
            + colorString(0, 0, 255) + " EDITABLE = false";

    /** Constant for the results of a simple table definition. */
    private static final String TABLE_RESULT = DEFAULT_RESULT_PREFIX
            + TABLE_COLUMNS;

    /** Constant for the results of a simple table with percent widths. */
    private static final String TABLE_RESULT_PERCENT = DEFAULT_RESULT_PREFIX
            + TABLE_COLUMNS_PERCENT;

    /** Constant for the results of a table with a validation error title. */
    private static final String TABLE_VAL_RESULT = TABLE_RES_PREFIX
            + "EDITABLE = false VALIDATIONERRTITLE = Validation error"
            + TABLE_COLUMNS;

    /** Constant for the results of a table defining a scroll size. */
    private static final String TABLE_SCROLL_RESULT = TABLE_RES_PREFIX
            + "EDITABLE = false SCROLLWIDTH = NumberWithUnit [ 10.0cm ] "
            + "SCROLLHEIGHT = NumberWithUnit [ 3.0in ]" + TABLE_COLUMNS;

    /** The central application object. */
    private Application application;

    /**
     * Generates a string representation for a column.
     *
     * @param name the name of the column
     * @param content the string representation of the other attributes
     * @return a string representation for this column
     */
    private static String column(String name, String content)
    {
        return String.format(FMT_COLUMN, name, content);
    }

    /**
     * Sets up the Jelly context. Creates the model for the table and stores it
     * as a variable in the context. Also, a special bean context is put into
     * the builder data object.
     */
    @Override
    protected void setUpJelly()
    {
        super.setUpJelly();
        List<ModelBean> model = new ArrayList<ModelBean>(MODEL_DATA.length);
        for (String s : MODEL_DATA)
        {
            model.add(ModelBean.fromString(s));
        }
        context.setVariable(VAR_MODEL, model);
    }

    /**
     * {@inheritDoc} This implementation adds the central application bean to
     * the parent bean store.
     */
    @Override
    protected BeanStore createParentBeanStore()
    {
        DefaultBeanStore store = new DefaultBeanStore();
        store.addBeanProvider(Application.BEAN_APPLICATION,
                ConstantBeanProvider.getInstance(Application.class,
                        createApplicationBean()));
        return store;
    }

    /**
     * Creates a bean for the central application. This is needed by the tag
     * when it creates the default editor validation handler.
     *
     * @return the application bean
     */
    private Application createApplicationBean()
    {
        application = EasyMock.createMock(Application.class);
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        MessageOutput msgOutput = EasyMock.createMock(MessageOutput.class);
        EasyMock.expect(appCtx.getMessageOutput()).andReturn(msgOutput)
                .anyTimes();
        EasyMock.expect(application.getApplicationContext()).andReturn(appCtx)
                .anyTimes();
        EasyMock.replay(appCtx, application, msgOutput);
        return application;
    }

    /**
     * Tests whether a table can be correctly created.
     */
    public void testCreateTableContent() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        checkScript(SCRIPT, TABLE_RESULT);
    }

    /**
     * Tests whether a table can be created that defines a scroll size.
     */
    public void testCreateTableScrollSize() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTSCROLL);
        checkScript(SCRIPT, TABLE_SCROLL_RESULT);
    }

    /**
     * Tests creating a simple table and then examines the form constructed
     * behind the scenes.
     */
    public void testCreateTableForm() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        checkSimpleTableTag();
    }

    /**
     * Tests accessing the single columns of the table.
     */
    public void testCreateTableGetColumn() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        TableTag tt = getTableTag();
        assertEquals("Wrong number of columns", COLUMNS.length, tt
                .getColumnCount());
        for (int i = 0; i < COLUMNS.length; i++)
        {
            TableColumnTag colTag = tt.getColumn(i);
            assertEquals("Wrong column " + i, COLUMNS[i], colTag.getName());
        }
    }

    /**
     * Tests whether the column width controller is correctly initialized.
     */
    public void testCreateTableGetColumnWidthController() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        TableTag tt = getTableTag();
        TableColumnWidthController ctrl = tt.getColumnWidthController();
        assertEquals("Wrong number of columns", COLUMNS.length, ctrl
                .getColumnCount());
        assertEquals("Got columns with percent width", 0, ctrl
                .getNumberOfColumnWithPercentWidth());
    }

    /**
     * Tests that only a single controller instance is created.
     */
    public void testCreateTableGetColumnWidthControllerCached()
            throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        TableTag tt = getTableTag();
        TableColumnWidthController ctrl = tt.getColumnWidthController();
        assertSame("Multiple controller instances", ctrl, tt
                .getColumnWidthController());
    }

    /**
     * Tests whether a table with percent values for the column widths can be
     * created.
     */
    public void testCreateTablePercentWidth() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLEPERCENT);
        checkScript(SCRIPT, TABLE_RESULT_PERCENT);
    }

    /**
     * Tests a simple table definition with empty renderer and editor tags.
     */
    public void testCreateTableEmptyRendererContent() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTEMPTYRENDER);
        checkScript(SCRIPT, TABLE_RESULT);
    }

    /**
     * Tests the content of the forms constructed by a simple table definition
     * with empty renderers and editors.
     */
    public void testCreateTableEmptyRendererForm() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTEMPTYRENDER);
        executeScript(SCRIPT);
        checkSimpleTableTag();
    }

    /**
     * Tests a form created for a simple table.
     *
     * @param form the form instance to be checked
     */
    private void checkSimpleTableForm(Form form)
    {
        assertEquals("Wrong number of fields in form", COLUMNS.length, form
                .getFieldNames().size());
        for (String fld : COLUMNS)
        {
            assertTrue("Wrong field handler instance for field " + fld, form
                    .getField(fld) instanceof ColumnFieldHandler);
            assertTrue(
                    "Wrong component handler instance for field " + fld,
                    form.getField(fld).getComponentHandler() instanceof ComponentHandlerImpl);
            assertNull("Component set for field handler " + fld, form.getField(
                    fld).getComponentHandler().getComponent());
        }
    }

    /**
     * Checks the data produced by a simple table declaration.
     */
    private void checkSimpleTableTag()
    {
        TableTag tt = getTableTag();
        assertNotNull("Table tag was not stored in context", tt);
        assertNotNull("Table has no model", tt.getTableModel());
        assertEquals("Wrong size of table model", MODEL_DATA.length, tt
                .getTableModel().size());
        assertNull("Editor selection handler is set", tt
                .getEditorSelectionHandler());
        checkSimpleTableForm(tt.getRowRenderForm());
        checkSimpleTableForm(tt.getRowEditForm());
        for (TableColumnTag tct : tt.getColumns())
        {
            assertNull("A renderer component was set for column "
                    + tct.getName(), tct.getRendererComponent());
            assertNull("An editor component was set for column "
                    + tct.getName(), tct.getEditorComponent());
            assertTrue("Render fields are not empty for column "
                    + tct.getName(), tct.getRenderFields().isEmpty());
            assertTrue("Edit fields are not empty for column " + tct.getName(),
                    tct.getEditFields().isEmpty());
        }
        TableFormController controller = tt.getTableFormController();
        assertSame("Wrong table tag in controller", tt, controller.getTableTag());
    }

    /**
     * Returns the table tag from the Jelly context.
     *
     * @return the table tag
     */
    private TableTag getTableTag()
    {
        return (TableTag) context.getVariable(VAR_TABLE);
    }

    /**
     * Tests the correct default value of the noField attribute. No field
     * handler for the table should have been added to the form, but a component
     * handler should be available.
     */
    public void testCreateTableNoField() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        assertNull("Field handler was added to form", builderData.getForm()
                .getField(TABLE_NAME));
        assertNotNull("No component handler was added", builderData
                .getComponentHandler(TABLE_NAME));
    }

    /**
     * Tests whether the noField attribute is correctly evaluated. If set to
     * false, a field must be created in the form.
     */
    public void testCreateTableNoFieldFalse() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTNOFIELDFALSE);
        executeScript(SCRIPT);
        assertNotNull("No component handler was added", builderData
                .getComponentHandler(TABLE_NAME));
        assertNotNull("No field handler was added to form", builderData
                .getForm().getField(TABLE_NAME));
    }

    /**
     * Tests creating a complex, editable table that also contains columns with
     * specialized renderers and editors.
     */
    public void testCreateEditableTableContent() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLEEDIT);
        checkScript(
                SCRIPT,
                TABLE_RES_PREFIX
                        + "EDITABLE = true COLUMNS {"
                        + column(
                                "firstName",
                                "HEADER = First name "
                                        + "CLASS = java.lang.Object WIDTH = "
                                        + "NumberWithUnit [ 25px ] EDITABLE = true "
                                        + "RENDERER { STATICTEXT [ NAME = firstName ALIGN = LEFT ] } "
                                        + "EDITOR { TEXTFIELD [ NAME = firstName MAXLEN = 25 ] }")
                        + column(
                                "lastName",
                                "HEADER = Last name "
                                        + "CLASS = java.lang.String WIDTH = "
                                        + "NumberWithUnit [ 5.0cm ] EDITABLE = true "
                                        + "EDITOR { Container: PANEL { TEXTFIELD [ NAME = middleName "
                                        + "MAXLEN = 25 ], TEXTFIELD [ NAME = lastName MAXLEN = 25 ] } }")
                        + column("age",
                                "HEADER = Age CLASS = java.lang.Object "
                                        + "WIDTH = NumberWithUnit [ 1.0in ] "
                                        + "EDITABLE = false") + " } ] }");
    }

    /**
     * Checks the form objects created for a complex table with columns that
     * define their own renderers and editors.
     */
    public void testCreateEditableTableForm() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLEEDIT);
        executeScript(SCRIPT);
        final String[] fields =
        { "firstName", "lastName", "middleName", "age" };
        TableTag tt = getTableTag();
        List<TableColumnTag> lstColumns = new ArrayList<TableColumnTag>(tt
                .getColumns());
        TableColumnTag col = lstColumns.get(0);
        assertEquals("Wrong number of render fields for col 0", 1, col
                .getRenderFields().size());
        assertTrue("Test field not found as render field in col 0", col
                .getRenderFields().contains(fields[0]));
        assertEquals("Wrong number of edit fields for col 0", 1, col
                .getEditFields().size());
        assertTrue("Test field not found as edit field in col 0", col
                .getEditFields().contains(fields[0]));
        col = lstColumns.get(1);
        assertTrue("Render fields found for column 1", col.getRenderFields()
                .isEmpty());
        assertEquals("Wrong number of edit fields for col 1", 2, col
                .getEditFields().size());
        assertTrue("Field 1 not found as edit field in col 1", col
                .getEditFields().contains(fields[1]));
        assertTrue("Field 2 not found as edit field in col 1", col
                .getEditFields().contains(fields[2]));
        assertNotNull("No editor component set for col 1", col
                .getEditorComponent());
        Form renderForm = tt.getRowRenderForm();
        assertEquals("Wrong number of elements in render form",
                fields.length - 1, renderForm.getFieldNames().size());
        assertNotNull("No render component for field 0", renderForm.getField(
                fields[0]).getComponentHandler().getComponent());
        Form editForm = tt.getRowEditForm();
        assertEquals("Wrong number of elements in edit form", fields.length,
                editForm.getFieldNames().size());
        for (int i = 0; i < fields.length - 1; i++)
        {
            assertNotNull("No component set for field " + fields[i], editForm
                    .getField(fields[i]).getComponentHandler().getComponent());
        }
    }

    /**
     * Tries to create a table when no model is specified. This should cause an
     * error.
     */
    public void testErrNoModel() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRNOMODEL,
                "Could create table without a model!");
    }

    /**
     * Tries to create a table when the specified model does not exist. This
     * should cause an error.
     */
    public void testErrNonExistingModel() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRINVMODEL,
                "Could create table with non-existing model!");
    }

    /**
     * Tries to create a table when the specified model is no collection. This
     * should cause an exception.
     */
    public void testErrInvalidModel() throws Exception
    {
        context.setVariable(VAR_MODEL_INVALID, SCRIPT);
        errorScript(SCRIPT, BUILDER_ERRINVMODEL,
                "Could create table with invalid model!");
    }

    /**
     * Tests a table tag that contains invalid tags in its body. This should
     * fail.
     */
    public void testErrWrongContent() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRWRONGCONTENT,
                "Could create table with invalid tags in body!");
    }

    /**
     * Tests a renderer tag that defines too many renderer components. Only one
     * is allowed. This should cause an exception.
     */
    public void testErrTooManyRenderComponents() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRRENDERTOOMANY,
                "Could create render tag with too many components!");
    }

    /**
     * Tests a column tag that is not nested inside a table. This should cause
     * an exception.
     */
    public void testErrNestedColumn() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRCOLNESTED,
                "Incorrectly nested column tag not detected!");
    }

    /**
     * Tests a renderer tag that is not nested inside a column tag. This is not
     * allowed.
     */
    public void testErrNestedRenderer() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRRENDERERNESTED,
                "Incorrectly nested renderer tag not detected!");
    }

    /**
     * Tests a table that does not define any columns. This should cause an
     * error.
     */
    public void testErrNoColumns() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRNOCOLUMN,
                "Could create table without columns!");
    }

    /**
     * Tests a table definition with a nested layout. This should cause an
     * error.
     */
    public void testErrLayout() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRLAYOUT,
                "Could create table with layout definition!");
    }

    /**
     * Tests a column definition with an invalid width attribute. This should
     * cause an error.
     */
    public void testErrInvalidNumber() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRINVNUMBER,
                "Invalid number was not detected!");
    }

    /**
     * Tests a renderer tag that is not nested inside a table tag. This should
     * cause an error.
     */
    public void testErrRendererNotNestedInTable() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRRENDERERNOTABLE,
                "Could process renderer tag outside a table!");
    }

    /**
     * Tests creating a table with a complex column whose editor form requires
     * the callback mechanism. Tests whether callbacks work as expected in sub
     * forms.
     */
    public void testCreateTableCallbacks() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTCALLBACK);
        checkScript(SCRIPT, TABLE_RES_PREFIX
                + "EDITABLE = true COLUMNS {"
                + column("firstName", "HEADER = First name "
                        + "CLASS = java.lang.Object WIDTH = "
                        + "NumberWithUnit [ 25px ] EDITABLE = true "
                        + "EDITOR { Container: PANEL { "
                        + "LABEL [ TEXT = First name: ALIGN = LEFT "
                        + "COMP = firstName ]<linked>, "
                        + "TEXTFIELD [ NAME = firstName MAXLEN = 25 ] } }")
                + " } ] }");
    }

    /**
     * Tests whether custom handler objects are correctly set.
     */
    public void testCreateTableHandlers() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTHANDLERS);
        TableSelectionHandler rendererSelHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        TableSelectionHandler editorSelHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        EasyMock.replay(rendererSelHandler, editorSelHandler, valHandler);
        context.setVariable("rendererSelectionHandler", rendererSelHandler);
        context.setVariable("editorSelectionHandler", editorSelHandler);
        context.setVariable("validationHandler", valHandler);
        executeScript(SCRIPT);
        TableTag tt = getTableTag();
        assertSame("Renderer selection handler not set", rendererSelHandler, tt
                .getRendererSelectionHandler());
        assertSame("Editor selection handler not set", editorSelHandler, tt
                .getEditorSelectionHandler());
        assertSame("Validation handler not set", valHandler, tt
                .getEditorValidationHandler());
    }

    /**
     * Tests an incorrectly nested selection handler tag. This should cause an
     * exception.
     */
    public void testErrNestedSelectionHandler() throws Exception
    {
        TableSelectionHandler selHandler = EasyMock
                .createMock(TableSelectionHandler.class);
        EasyMock.replay(selHandler);
        context.setVariable("selectionHandler", selHandler);
        errorScript(SCRIPT, BUILDER_ERRSELHANDLERNESTED,
                "Incorrectly nested selection handler not detected!");
    }

    /**
     * Tests an incorrectly nested validation handler tag. This should cause an
     * exception.
     */
    public void testErrNestedValidationHandler() throws Exception
    {
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        EasyMock.replay(valHandler);
        context.setVariable("validationHandler", valHandler);
        errorScript(SCRIPT, BUILDER_ERRVALHANDLERNESTED,
                "Incorrectly nested validation handler not detected!");
    }

    /**
     * Tests a table with columns that uses percents widths that sum up to more
     * than 100.
     */
    public void testErrPercentColumns() throws Exception
    {
        errorScript(SCRIPT, BUILDER_ERRPERCENT,
                "Invalid relative columns widths not detected!");
    }

    /**
     * Tests a table definition that defines a validation error title.
     */
    public void testCreateTableValidationErrorTitle() throws Exception
    {
        checkValidationErrorTitle(BUILDER_TESTVALERRTITLE);
    }

    /**
     * Tests a table definition that defines a validation error title from a
     * resource key.
     */
    public void testCreateTableValidationErrorTitleRes() throws Exception
    {
        checkValidationErrorTitle(BUILDER_TESTVALERRTITLERES);
    }

    /**
     * Tests a table definition that defines a validation error title from a
     * resource key and a resource group.
     */
    public void testCreateTableValidationErrorTitleResGrp() throws Exception
    {
        checkValidationErrorTitle(BUILDER_TESTVALERRTITLERESGRP);
    }

    /**
     * Helper method for checking whether a validation error title is correctly
     * set.
     *
     * @param builderName the name of the builder
     * @throws Exception if an error occurs
     */
    private void checkValidationErrorTitle(String builderName) throws Exception
    {
        builderData.setBuilderName(builderName);
        checkScript(SCRIPT, TABLE_VAL_RESULT);
        TableTag tt = (TableTag) context.findVariable(VAR_TABLE);
        assertNull("A specific validation handler was set", tt
                .getEditorValidationHandler());
    }

    /**
     * Tests creating a table with multi-selection support.
     */
    public void testCreateTableMultiSelect() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTMULTISELECT);
        checkScript(SCRIPT, TABLE_RES_PREFIX + "EDITABLE = false MULTISELECT"
                + TABLE_COLUMNS);
    }

    /**
     * Tests resolving the table model from the current bean context.
     */
    public void testResolveModel() throws JellyTagException
    {
        BeanContext bc = EasyMock.createMock(BeanContext.class);
        Collection<ModelBean> data = new ArrayList<ModelBean>();
        EasyMock.expect(bc.getBean(VAR_MODEL)).andReturn(data);
        EasyMock.replay(bc);
        builderData.setBeanContext(bc);
        TableTag tag = new TableTag();
        tag.setContext(context);
        assertSame("Wrong model collection", data, tag.resolveModel(VAR_MODEL));
        EasyMock.verify(bc);
    }

    /**
     * Tests whether the logic column class can be specified as an enumeration
     * constant.
     */
    public void testColumnTagGetLogicDataClassEnum() throws JellyTagException,
            FormBuilderException
    {
        TableTag parent = new TableTag();
        TableColumnTag tag = new TableColumnTag();
        tag.setParent(parent);
        tag.setContext(context);
        tag.setColumnClass(ColumnClass.ICON);
        tag.processBeforeBody();
        assertEquals("Wrong logic class", ColumnClass.ICON, tag
                .getLogicDataClass());
    }

    /**
     * Tests that during a build operation a special field handler factory is
     * installed which can later be queried from the tag.
     */
    public void testTableFieldHandlerFactoryInstalled() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);
        assertTrue(
                "FieldHandlerFactory changed",
                builderData.getFieldHandlerFactory() instanceof DefaultFieldHandlerFactory);
        TableTag tt = getTableTag();
        TableFieldHandlerFactory factory = tt.getFieldHandlerFactory();
        for (String col : COLUMNS)
        {
            checkReferences(factory, col, tt.getRowRenderForm());
            checkReferences(factory, col, tt.getRowEditForm());
        }
    }

    /**
     * Checks whether for a specific field handler references for transformers
     * and validators have been created.
     *
     * @param factory the {@code TableFieldHandlerFactory}
     * @param col the name of the column
     * @param form the form instance to be checked
     */
    private static void checkReferences(TableFieldHandlerFactory factory,
            String col, Form form)
    {
        FieldHandler field = form.getField(col);
        assertNotNull("No read transformer for " + col,
                factory.getReadTransformerReference(field));
        assertNotNull("No write transformer for " + col,
                factory.getWriteTransformerReference(field));
        assertNotNull("No validator for " + col,
                factory.getValidatorReference(field));
    }

    /**
     * Tests whether the tag initializes a validation handler.
     */
    public void testValidationHandlerInitialized() throws Exception
    {
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);

        TableTag tt = getTableTag();
        assertNotNull("No validation handler set",
                tt.getEditorValidationHandler());
        assertSame("Validation handler not initialized", application,
                ((DefaultTableEditorValidationHandler) tt
                        .getEditorValidationHandler()).getApplication());
    }

    /**
     * Tests whether a default renderer selection handler is created.
     */
    public void testDefaultRendererSelectionHandler() throws Exception
    {
        Object component = EasyMock.createMock(Object.class);
        EasyMock.replay(component);
        builderData.setBuilderName(BUILDER_TESTTABLE);
        executeScript(SCRIPT);

        TableTag tt = getTableTag();
        TableSelectionHandler selectionHandler =
                tt.getRendererSelectionHandler();
        selectionHandler.prepareComponent(null, tt, component, false, false, 0,
                0);
    }

    /**
     * Definition of a simple bean class representing the model for the table.
     */
    static class ModelBean
    {
        private String firstName;

        private String lastName;

        private String middleName;

        private int age;

        public int getAge()
        {
            return age;
        }

        public void setAge(int age)
        {
            this.age = age;
        }

        public String getFirstName()
        {
            return firstName;
        }

        public void setFirstName(String firstName)
        {
            this.firstName = firstName;
        }

        public String getLastName()
        {
            return lastName;
        }

        public void setLastName(String lastName)
        {
            this.lastName = lastName;
        }

        public String getMiddleName()
        {
            return middleName;
        }

        public void setMiddleName(String middleName)
        {
            this.middleName = middleName;
        }

        /**
         * Extracts the data of a bean from a string. The single fields of the
         * string must be separated by whitespace.
         *
         * @param s the string to be parsed
         * @return a new instance with the content of the string
         */
        public static ModelBean fromString(String s)
        {
            StringTokenizer tok = new StringTokenizer(s, " \t");
            ModelBean b = new ModelBean();
            b.setFirstName(tok.nextToken());
            b.setMiddleName(tok.nextToken());
            b.setLastName(tok.nextToken());
            b.setAge(Integer.parseInt(tok.nextToken()));
            return b;
        }
    }
}
