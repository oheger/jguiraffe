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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.jguiraffe.PersonBean;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.TextData;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.DummyWrapper;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerWrapper;
import net.sf.jguiraffe.gui.forms.ValidatorWrapper;
import net.sf.jguiraffe.gui.layout.NumberWithUnit;
import net.sf.jguiraffe.gui.layout.UnitSizeHandler;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code TableFormController}.
 */
public class TestTableFormController
{
    /** An array with the names of the columns. */
    private static final String[] COLUMN_NAMES = {
            "firstName", "lastName", "idNo", "salary"
    };

    /** An array with the editable flags for the columns. */
    private static final boolean[] EDIT_FLAGS = {
            false, false, true, true
    };

    /** An array with test data for the table model. */
    public static final Object[][] TEST_DATA = {
            {
                    "John", "Falstaff", 123, 775.0
            }, {
                    "Julia", "Capulet", 428, 1225.5
            }, {
                    "Romeo", "Montegue", 312, 1225.5
            }, {
                    "Timon", "of Athens", 764, 1099.25
            }, {
                    "Titus", "Andronicus", 111, 980.0
            }, {
                    "Cathrine", "Babtista", 456, 1000.0
            }
    };

    /** Constant for the prefix of a column field name. */
    private static final String FIELD_PREFIX = "field_";

    /** A mock for the table tag. */
    private TableTag tableTag;

    /** A mock for the row render form. */
    private Form renderForm;

    /** A mock for the row edit form. */
    private Form editForm;

    @Before
    public void setUp() throws Exception
    {
        tableTag = EasyMock.createMock(TableTag.class);
    }

    /**
     * Creates a collection with the data model of the table.
     *
     * @return the data model collection
     */
    private static List<Object> createModel()
    {
        List<Object> data = new ArrayList<Object>(TEST_DATA.length);
        for (Object[] item : TEST_DATA)
        {
            PersonBean bean = new PersonBean();
            bean.setFirstName(item[0].toString());
            bean.setLastName(item[1].toString());
            bean.setIdNo((Integer) item[2]);
            bean.setSalary((Double) item[3]);
            data.add(bean);
        }
        return data;
    }

    /**
     * Creates mock objects for the tags defining the table's columns. The mocks
     * are already prepared to return basic column information.
     *
     * @return an array with the mock objects created
     */
    private TableColumnTag[] createColumns()
    {
        TableColumnTag[] tags = new TableColumnTag[COLUMN_NAMES.length];
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            TableColumnTag tag = EasyMock.createMock(TableColumnTag.class);
            TextData td = EasyMock.createMock(TextData.class);
            EasyMock.expect(td.getCaption()).andReturn(COLUMN_NAMES[i])
                    .anyTimes();
            EasyMock.replay(td);
            EasyMock.expect(tag.getHeaderText()).andReturn(td).anyTimes();
            EasyMock.expect(tag.getName())
                    .andReturn(FIELD_PREFIX + COLUMN_NAMES[i]).anyTimes();
            EasyMock.expect(tableTag.getColumn(i)).andReturn(tag).anyTimes();
            EasyMock.expect(tableTag.isColumnEditable(tag))
                    .andReturn(EDIT_FLAGS[i]).anyTimes();
            tags[i] = tag;
        }
        EasyMock.expect(tableTag.getColumns()).andReturn(Arrays.asList(tags))
                .anyTimes();
        EasyMock.expect(tableTag.getColumnCount()).andReturn(tags.length)
                .anyTimes();
        return tags;
    }

    /**
     * Replays the mock for the table tag and optionally its associated form
     * mocks.
     */
    private void replay()
    {
        EasyMock.replay(tableTag);
        replayOptional(renderForm);
        replayOptional(editForm);
    }

    /**
     * Replays an optional mock. If the mock is null, this method has no effect.
     *
     * @param mock the optional mock to be replayed
     */
    private static void replayOptional(Object mock)
    {
        if (mock != null)
        {
            EasyMock.replay(mock);
        }
    }

    /**
     * Prepares the mock table tag to return the test table model.
     *
     * @param replay flag whether the table tag mock is to be replayed
     * @return the list with the model data
     */
    private List<Object> prepareModel(boolean replay)
    {
        List<Object> model = createModel();
        expectModel(model);
        if (replay)
        {
            replay();
        }
        return model;
    }

    /**
     * Prepares the mock for the table tag to return always the specified model.
     *
     * @param model the model collection
     */
    private void expectModel(Collection<?> model)
    {
        tableTag.getTableModel();
        EasyMock.expectLastCall().andReturn(model).anyTimes();
    }

    /**
     * Creates a test form controller instance and prepares the mocks for the
     * model.
     *
     * @return the test controller instance
     */
    private TableFormController prepareControllerWithModel()
    {
        prepareModel(true);
        return new TableFormController(tableTag);
    }

    /**
     * Tries to create an instance without a tag.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNoTag()
    {
        new TableFormController(null);
    }

    /**
     * Tests whether the number of rows can be determined.
     */
    @Test
    public void testRowCount()
    {
        TableFormController controller = prepareControllerWithModel();
        assertEquals("Wrong number of rows", TEST_DATA.length,
                controller.getRowCount());
    }

    /**
     * Tests whether the single beans of the table's model can be queried.
     */
    @Test
    public void testGetModelBean()
    {
        TableFormController controller = prepareControllerWithModel();
        int idx = 0;
        for (Object o : tableTag.getTableModel())
        {
            assertSame("Wrong model object at " + idx, o,
                    controller.getModelBean(idx++));
        }
    }

    /**
     * Tests that the table model passed to the tag can be accessed directly.
     */
    @Test
    public void testModelDirectAccess()
    {
        TableFormController controller = prepareControllerWithModel();
        tableTag.getTableModel().clear();
        assertEquals("Model not connected", 0, controller.getRowCount());
    }

    /**
     * Helper method for testing whether a correct table model was constructed.
     *
     * @param model the model collection
     */
    private void checkDataModel(Collection<?> model)
    {
        expectModel(model);
        replay();
        TableFormController controller = new TableFormController(tableTag);
        assertEquals("Wrong number of model items", model.size(), controller
                .getDataModel().size());
        assertTrue("Invalid content: " + controller.getDataModel(),
                model.containsAll(controller.getDataModel()));
        assertTrue("Not an array list: " + controller.getDataModel(),
                controller.getDataModel() instanceof ArrayList);
    }

    /**
     * Tests that a model is handled which is not a list.
     */
    @Test
    public void testModelNoList()
    {
        checkDataModel(new HashSet<Object>(createModel()));
    }

    /**
     * Tests that a model is handled which does not support access by index.
     */
    @Test
    public void testModelNoDirectAccessList()
    {
        checkDataModel(new LinkedList<Object>(createModel()));
    }

    /**
     * Creates a test controller instance and prepares mock objects for the
     * table model and its columns.
     *
     * @return the test controller instance
     */
    private TableFormController prepareControllerWithModelAndColumns()
    {
        EasyMock.replay(createColumns());
        return prepareControllerWithModel();
    }

    /**
     * Tests whether the correct number of columns is returned.
     */
    @Test
    public void testGetColumnCount()
    {
        TableFormController controller = prepareControllerWithModelAndColumns();
        assertEquals("Wrong number of columns", COLUMN_NAMES.length,
                controller.getColumnCount());
    }

    /**
     * Tests whether the name of a column can be queried.
     */
    @Test
    public void testGetColumnName()
    {
        TableFormController controller = prepareControllerWithModelAndColumns();
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            assertEquals("Wrong column name at " + i, COLUMN_NAMES[i],
                    controller.getColumnName(i));
        }
    }

    /**
     * Tests whether the field name associated with a column can be queried.
     */
    @Test
    public void testGetColumnFieldName()
    {
        TableFormController controller = prepareControllerWithModelAndColumns();
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            assertEquals("Wrong column field name at " + i, FIELD_PREFIX
                    + COLUMN_NAMES[i], controller.getColumnFieldName(i));
        }
    }

    /**
     * Creates the forms for the current table row and installs them at the
     * table tag.
     */
    private void createRowForms()
    {
        renderForm = EasyMock.createMock(Form.class);
        EasyMock.expect(tableTag.getRowRenderForm()).andReturn(renderForm)
                .anyTimes();
        editForm = EasyMock.createMock(Form.class);
        EasyMock.expect(tableTag.getRowEditForm()).andReturn(editForm)
                .anyTimes();
    }

    /**
     * Tests whether the current row in the table can be selected.
     */
    @Test
    public void testSelectCurrentRow()
    {
        TableColumnTag[] columns = createColumns();
        createRowForms();
        List<Object> model = prepareModel(false);
        final int row = 1;
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.replay(columns);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        EasyMock.verify(renderForm, editForm);
    }

    /**
     * Tests that selecting the current row for another time has no effect.
     */
    @Test
    public void testSelectSameCurrentRow()
    {
        TableColumnTag[] columns = createColumns();
        createRowForms();
        List<Object> model = prepareModel(false);
        final int row = 0;
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.replay(columns);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        controller.selectCurrentRow(row);
        EasyMock.verify(renderForm, editForm);
    }

    /**
     * Prepares mock objects to expect the invalidation of the current row.
     *
     * @param row the index of the current row
     */
    private void prepareInvalidationOfCurrentRow(int row)
    {
        TableColumnTag[] columns = createColumns();
        createRowForms();
        List<Object> model = prepareModel(false);
        renderForm.initFields(model.get(row));
        EasyMock.expectLastCall().times(2);
        editForm.initFields(model.get(row));
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(columns);
        replay();
    }

    /**
     * Tests whether the current row can be deselected.
     */
    @Test
    public void testResetCurrentRow()
    {
        final int row = 2;
        prepareInvalidationOfCurrentRow(row);

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        controller.resetCurrentRow();
        controller.selectCurrentRow(row);
        EasyMock.verify(renderForm, editForm);
    }

    /**
     * Tests whether a range of rows can be invalidated which contains the
     * current row.
     */
    @Test
    public void testInvalidateRangeWithCurrentRow()
    {
        final int row = 1;
        prepareInvalidationOfCurrentRow(row);

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        controller.invalidateRange(row - 1, row + 1);
        controller.selectCurrentRow(row);
        EasyMock.verify(renderForm, editForm);
    }

    /**
     * Tests whether a range of rows can be invalidated which does not contain
     * the current row.
     */
    @Test
    public void testInvalidateRangeNoCurrentRow()
    {
        TableColumnTag[] columns = createColumns();
        createRowForms();
        List<Object> model = prepareModel(false);
        final int row = TEST_DATA.length - 1;
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.replay(columns);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        controller.invalidateRange(0, row - 1);
        controller.selectCurrentRow(row);
        EasyMock.verify(renderForm, editForm);
    }

    /**
     * Tests whether the value of a given column in the current row can be
     * queried.
     */
    @Test
    public void testGetColumnValue()
    {
        FieldHandler fh = EasyMock.createMock(FieldHandler.class);
        ComponentHandler<?> ch = EasyMock.createMock(ComponentHandler.class);
        TableColumnTag[] columns = createColumns();
        createRowForms();
        EasyMock.expect(renderForm.getField(FIELD_PREFIX + COLUMN_NAMES[0]))
                .andReturn(fh);
        fh.getComponentHandler();
        EasyMock.expectLastCall().andReturn(ch);
        final String data = "Component Data";
        EasyMock.expect(ch.getData()).andReturn(data);
        prepareModel(true);
        EasyMock.replay(columns);
        EasyMock.replay(fh, ch);

        TableFormController controller = new TableFormController(tableTag);
        assertSame("Wrong column value", data, controller.getColumnValue(0));
    }

    /**
     * Helper method for testing a failed column validation.
     *
     * @param editFields the set of edit fields to be returned by the column
     * @param expectedFields the set of fields expected by the form mock
     */
    private void checkValidateColumnInvalid(Set<String> editFields,
            Set<String> expectedFields)
    {
        FormValidatorResults vres =
                EasyMock.createMock(FormValidatorResults.class);
        TableEditorValidationHandler valHandler = createValidationHandler();
        TableColumnTag[] columns = createColumns();
        EasyMock.expect(columns[1].getEditFields()).andReturn(editFields);
        createRowForms();
        Object table = new Object();
        EasyMock.expect(editForm.validateFields(expectedFields))
                .andReturn(vres);
        EasyMock.expect(
                valHandler.validationPerformed(table, editForm, tableTag, vres,
                        -1, 1)).andReturn(false);
        EasyMock.expect(vres.isValid()).andReturn(false);
        EasyMock.replay(columns);
        prepareModel(true);
        EasyMock.replay(vres, valHandler);

        TableFormController controller = new TableFormController(tableTag);
        assertFalse("Wrong result", controller.validateColumn(table, 1));
        EasyMock.verify(valHandler);
    }

    /**
     * Creates a mock for the table validation handler and installs it at the
     * table tag.
     *
     * @return the mock for the validation handler
     */
    private TableEditorValidationHandler createValidationHandler()
    {
        TableEditorValidationHandler valHandler =
                EasyMock.createMock(TableEditorValidationHandler.class);
        EasyMock.expect(tableTag.getEditorValidationHandler())
                .andReturn(valHandler).anyTimes();
        return valHandler;
    }

    /**
     * Tests a failed validation of a column which does not have an edit form.
     */
    @Test
    public void testValidateColumnInvalidNoEditForm()
    {
        checkValidateColumnInvalid(Collections.<String> emptySet(),
                Collections.singleton(FIELD_PREFIX + COLUMN_NAMES[1]));
    }

    /**
     * Tests a failed validation of a column which has an edit form.
     */
    @Test
    public void testValidateColumnInvalidWithEditForm()
    {
        Set<String> fields = new HashSet<String>(Arrays.asList(COLUMN_NAMES));
        checkValidateColumnInvalid(fields, fields);
    }

    /**
     * Tests a successful column validation if the validation handler performs
     * no override.
     */
    @Test
    public void testValidateColumnValidResult()
    {
        FormValidatorResults vres =
                EasyMock.createMock(FormValidatorResults.class);
        TableEditorValidationHandler valHandler = createValidationHandler();
        TableColumnTag[] columns = createColumns();
        EasyMock.expect(columns[1].getEditFields()).andReturn(
                Collections.<String> emptySet());
        createRowForms();
        Object table = new Object();
        Set<String> fields =
                Collections.singleton(FIELD_PREFIX + COLUMN_NAMES[1]);
        List<Object> model = prepareModel(false);
        final int row = 2;
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.expect(editForm.validateFields(fields)).andReturn(vres);
        EasyMock.expect(
                valHandler.validationPerformed(table, editForm, tableTag, vres,
                        row, 1)).andReturn(false);
        EasyMock.expect(vres.isValid()).andReturn(true);
        editForm.readFields(model.get(row), fields);
        EasyMock.replay(columns);
        EasyMock.replay(vres, valHandler);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        assertTrue("Wrong result", controller.validateColumn(table, 1));
        EasyMock.verify(valHandler, editForm);
    }

    /**
     * Tests whether validateColumn() correctly deals with validation handlers
     * that change input data.
     */
    @Test
    public void testValidateColumnValidationHandlerOverride()
    {
        FormValidatorResults vres =
                EasyMock.createMock(FormValidatorResults.class);
        TableEditorValidationHandler valHandler = createValidationHandler();
        TableColumnTag[] columns = createColumns();
        EasyMock.expect(columns[1].getEditFields()).andReturn(
                Collections.<String> emptySet());
        createRowForms();
        Object table = new Object();
        Set<String> fields =
                Collections.singleton(FIELD_PREFIX + COLUMN_NAMES[1]);
        List<Object> model = prepareModel(false);
        final int row = 2;
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.expect(editForm.validateFields(fields)).andReturn(vres);
        EasyMock.expect(
                valHandler.validationPerformed(table, editForm, tableTag, vres,
                        row, 1)).andReturn(true);
        EasyMock.expect(editForm.validateFields(fields)).andReturn(vres);
        editForm.readFields(model.get(row), fields);
        EasyMock.replay(columns);
        EasyMock.replay(vres, valHandler);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        assertTrue("Wrong result", controller.validateColumn(table, 1));
        EasyMock.verify(valHandler, editForm);
    }

    /**
     * Tests setColumnValue() if for this column an editor form exists. In this
     * case, updates are done via the form, and the method has no effect.
     */
    @Test
    public void testSetColumnValueEditForm()
    {
        TableColumnTag[] columns = createColumns();
        EasyMock.expect(columns[0].getEditorComponent()).andReturn(this);
        createRowForms();
        prepareModel(true);
        EasyMock.replay(columns);

        TableFormController controller = new TableFormController(tableTag);
        controller.setColumnValue(null, 0, this);
        // no interaction with mock objects
    }

    /**
     * Tests setColumnValue() for a column with no special edit form. In this
     * case the method is responsible for updating the value.
     */
    @Test
    public void testSetColumnValueNoEditForm()
    {
        FormValidatorResults vres =
                EasyMock.createMock(FormValidatorResults.class);
        TableEditorValidationHandler valHandler = createValidationHandler();
        FieldHandler fh = EasyMock.createMock(FieldHandler.class);
        @SuppressWarnings("unchecked")
        ComponentHandler<Object> ch =
                EasyMock.createMock(ComponentHandler.class);
        TableColumnTag[] columns = createColumns();
        createRowForms();
        fh.getComponentHandler();
        EasyMock.expectLastCall().andStubReturn(ch);
        Object table = new Object();
        Object data = 42;
        final int row = 1;
        final int col = 2;
        EasyMock.expect(columns[col].getEditFields()).andReturn(
                Collections.<String> emptySet());
        EasyMock.expect(columns[col].getEditorComponent()).andReturn(null);
        String field = FIELD_PREFIX + COLUMN_NAMES[col];
        EasyMock.expect(editForm.getField(field)).andReturn(fh);
        ch.setData(data);
        Set<String> fields = Collections.singleton(field);
        List<Object> model = prepareModel(false);
        renderForm.initFields(model.get(row));
        editForm.initFields(model.get(row));
        EasyMock.expect(editForm.validateFields(fields)).andReturn(vres);
        EasyMock.expect(
                valHandler.validationPerformed(table, editForm, tableTag, vres,
                        row, col)).andReturn(false);
        EasyMock.expect(vres.isValid()).andReturn(false);
        EasyMock.replay(columns);
        EasyMock.replay(vres, valHandler, fh, ch);
        replay();

        TableFormController controller = new TableFormController(tableTag);
        controller.selectCurrentRow(row);
        controller.setColumnValue(table, col, data);
        EasyMock.verify(ch, valHandler);
    }

    /**
     * Helper method for testing hasRenderer().
     *
     * @param rendererComponent the renderer component to install
     * @param expResult the expected method result
     */
    private void checkHasRenderer(Object rendererComponent, boolean expResult)
    {
        TableColumnTag[] columns = createColumns();
        final int col = COLUMN_NAMES.length - 1;
        EasyMock.expect(columns[col].getRendererComponent()).andReturn(
                rendererComponent);
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertEquals("Wrong result", expResult, controller.hasRenderer(col));
    }

    /**
     * Tests whether an existing column renderer is detected.
     */
    @Test
    public void testHasRendererTrue()
    {
        checkHasRenderer(this, true);
    }

    /**
     * Tests hasRenderer() for a column which does not have a renderer.
     */
    @Test
    public void testHasRendererFalse()
    {
        checkHasRenderer(null, false);
    }

    /**
     * Tests whether a column editor can be queried.
     */
    @Test
    public void testGetColumnEditor()
    {
        TableColumnTag[] columns = createColumns();
        final int col = 1;
        final Object editor = new Object();
        EasyMock.expect(columns[col].getEditorComponent()).andReturn(editor)
                .anyTimes();
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertSame("Wrong editor component", editor,
                controller.getColumnEditor(col));
    }

    /**
     * Tests whether a column renderer can be queried.
     */
    @Test
    public void testGetColumnRenderer()
    {
        TableColumnTag[] columns = createColumns();
        final int col = 1;
        final Object renderer = new Object();
        EasyMock.expect(columns[col].getRendererComponent())
                .andReturn(renderer).anyTimes();
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertSame("Wrong editor component", renderer,
                controller.getColumnRenderer(col));
    }

    /**
     * Tests whether the editable flags of columns can be queried.
     */
    @Test
    public void testIsColumnEditable()
    {
        TableColumnTag[] columns = createColumns();
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        for (int i = 0; i < EDIT_FLAGS.length; i++)
        {
            assertEquals("Wrong editable flag at " + i, EDIT_FLAGS[i],
                    controller.isColumnEditable(i));
        }
    }

    /**
     * Helper method for testing the editable flag for the whole table.
     *
     * @param editable the editable flag
     */
    private void checkTableEditable(boolean editable)
    {
        EasyMock.expect(tableTag.isTableEditable()).andReturn(editable)
                .anyTimes();
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertEquals("Wrong editable flag", editable,
                controller.isTableEditable());
    }

    /**
     * Tests the table editable flag if it is true.
     */
    @Test
    public void testIsTableEditableTrue()
    {
        checkTableEditable(true);
    }

    /**
     * Tests the table editable flag if it is false.
     */
    @Test
    public void testIsTableEditableFalse()
    {
        checkTableEditable(false);
    }

    /**
     * Tests whether the logic data class of a column can be queried.
     */
    @Test
    public void testGetLogicDataClass()
    {
        TableColumnTag[] columns = createColumns();
        final int col = 2;
        EasyMock.expect(columns[col].getLogicDataClass())
                .andReturn(ColumnClass.NUMBER).anyTimes();
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertEquals("Wrong logic column class", ColumnClass.NUMBER,
                controller.getLogicDataClass(col));
    }

    /**
     * Tests whether the Java data class of a column can be queried.
     */
    @Test
    public void testGetDataClass()
    {
        TableColumnTag[] columns = createColumns();
        final int col = 2;
        columns[col].getDataClass();
        EasyMock.expectLastCall().andReturn(String.class).anyTimes();
        EasyMock.replay(columns);
        prepareModel(true);

        TableFormController controller = new TableFormController(tableTag);
        assertEquals("Wrong column class", String.class,
                controller.getDataClass(col));
    }

    /**
     * Tests the default transformer factory.
     */
    @Test
    public void testDefaultTransformerFactory()
    {
        prepareModel(true);
        TableFormController controller = new TableFormController(tableTag);
        assertNotNull("No transformer factory",
                controller.getTransformerFactory());
    }

    /**
     * Tests that no transformers are installed if a renderer is defined for a
     * column.
     */
    @Test
    public void testInstallTransformersForColumnTypeRenderer()
    {
        final int col = 0;
        TableColumnTag[] columns = createColumns();
        TransformerFactory transformerFactory =
                EasyMock.createMock(TransformerFactory.class);
        TableFieldHandlerFactory fieldFactory =
                EasyMock.createMock(TableFieldHandlerFactory.class);
        EasyMock.expect(tableTag.getFieldHandlerFactory())
                .andReturn(fieldFactory).anyTimes();
        EasyMock.expect(columns[col].getRendererComponent()).andReturn(this)
                .anyTimes();
        EasyMock.expect(columns[col].getLogicDataClass())
                .andReturn(ColumnClass.NUMBER).anyTimes();
        EasyMock.replay(columns);
        EasyMock.replay(transformerFactory, fieldFactory);
        prepareModel(true);

        TableFormController controller =
                new TableFormController(tableTag, transformerFactory);
        assertFalse("Wrong result",
                controller.installTransformersForColumnType(col));
    }

    /**
     * Tests that no transformers are installed if no column type is defined.
     */
    @Test
    public void testInstallTransformersForColumnTypeUndefined()
    {
        final int col = 1;
        TableColumnTag[] columns = createColumns();
        TransformerFactory transformerFactory =
                EasyMock.createMock(TransformerFactory.class);
        TableFieldHandlerFactory fieldFactory =
                EasyMock.createMock(TableFieldHandlerFactory.class);
        EasyMock.expect(tableTag.getFieldHandlerFactory())
                .andReturn(fieldFactory).anyTimes();
        EasyMock.expect(columns[col].getLogicDataClass()).andReturn(null)
                .anyTimes();
        EasyMock.replay(columns);
        EasyMock.replay(transformerFactory, fieldFactory);
        prepareModel(true);

        TableFormController controller =
                new TableFormController(tableTag, transformerFactory);
        assertFalse("Wrong result",
                controller.installTransformersForColumnType(col));
    }

    /**
     * Helper method for creating a transformer reference.
     *
     * @param defined flag whether the reference has a defined transformer
     * @return the reference
     */
    private static TransformerReference createTransformerReference(
            boolean defined)
    {
        TransformerWrapper wrapper =
                defined ? EasyMock.createMock(TransformerWrapper.class)
                        : DummyWrapper.INSTANCE;
        return new TransformerReference(wrapper);
    }

    /**
     * Helper method for creating a validator reference.
     *
     * @param defined flag whether the reference has a defined validator
     * @return the reference
     */
    private static ValidatorReference createValidatorReference(boolean defined)
    {
        ValidatorWrapper wrapper =
                defined ? EasyMock.createMock(ValidatorWrapper.class)
                        : DummyWrapper.INSTANCE;
        return new ValidatorReference(wrapper);
    }

    /**
     * Helper method for testing installTransformersForColumnType() if already
     * transformers are set.
     *
     * @param read flag for the read transformer
     * @param write flag for the write transformer
     * @param validator flag for the validator
     */
    private void checkInstallTransformersForColumnTypeAlreadyDefined(
            boolean read, boolean write, boolean validator)
    {
        final int col = 1;
        TableColumnTag[] columns = createColumns();
        TransformerFactory transformerFactory =
                EasyMock.createMock(TransformerFactory.class);
        TableFieldHandlerFactory fieldFactory =
                EasyMock.createMock(TableFieldHandlerFactory.class);
        FieldHandler renderField = EasyMock.createMock(FieldHandler.class);
        createRowForms();
        EasyMock.expect(tableTag.getFieldHandlerFactory())
                .andReturn(fieldFactory).anyTimes();
        EasyMock.expect(columns[col].getLogicDataClass())
                .andReturn(ColumnClass.DATE).anyTimes();
        EasyMock.expect(columns[col].getRendererComponent()).andReturn(null)
                .anyTimes();
        EasyMock.expect(renderForm.getField(FIELD_PREFIX + COLUMN_NAMES[col]))
                .andReturn(renderField).anyTimes();
        EasyMock.expect(fieldFactory.getReadTransformerReference(renderField))
                .andReturn(createTransformerReference(read));
        EasyMock.expect(fieldFactory.getWriteTransformerReference(renderField))
                .andReturn(createTransformerReference(write));
        EasyMock.expect(fieldFactory.getValidatorReference(renderField))
                .andReturn(createValidatorReference(validator));
        EasyMock.replay(columns);
        EasyMock.replay(transformerFactory, fieldFactory);
        prepareModel(true);

        TableFormController controller =
                new TableFormController(tableTag, transformerFactory);
        assertFalse("Wrong result",
                controller.installTransformersForColumnType(col));
    }

    /**
     * Tests that no transformers are installed if the read transformer is set.
     */
    @Test
    public void testInstallTransformersForColumnTypeReadTransformerSet()
    {
        checkInstallTransformersForColumnTypeAlreadyDefined(true, false, false);
    }

    /**
     * Tests that no transformers are installed if the write transformer is set.
     */
    @Test
    public void testInstallTransformersForColumnTypeWriteTransformerSet()
    {
        checkInstallTransformersForColumnTypeAlreadyDefined(false, true, false);
    }

    /**
     * Tests that no transformers are installed if the validator is set.
     */
    @Test
    public void testInstallTransformersForColumnTypeValidatorTransformerSet()
    {
        checkInstallTransformersForColumnTypeAlreadyDefined(false, false, true);
    }

    /**
     * Tests a successful installation of transformers for a column type.
     */
    @Test
    public void testInstallTransformersForColumnTypeSuccess()
    {
        final int col = 0;
        TableColumnTag[] columns = createColumns();
        TransformerFactory transformerFactory =
                EasyMock.createMock(TransformerFactory.class);
        TableFieldHandlerFactory fieldFactory =
                EasyMock.createMock(TableFieldHandlerFactory.class);
        FieldHandler renderField = EasyMock.createMock(FieldHandler.class);
        FieldHandler editField = EasyMock.createMock(FieldHandler.class);
        TransformerWrapper readWrapper =
                EasyMock.createMock(TransformerWrapper.class);
        TransformerWrapper writeWrapper =
                EasyMock.createMock(TransformerWrapper.class);
        ValidatorWrapper validatorWrapper =
                EasyMock.createMock(ValidatorWrapper.class);
        TransformerReference renderReadTransformer =
                new TransformerReference(null);
        TransformerReference renderWriteTransformer =
                new TransformerReference(null);
        TransformerReference editReadTransformer =
                new TransformerReference(null);
        TransformerReference editWriteTransformer =
                new TransformerReference(null);
        ValidatorReference renderValidator = new ValidatorReference(null);
        ValidatorReference editValidator = new ValidatorReference(null);

        final ColumnClass columnClass = ColumnClass.FLOAT;
        createRowForms();
        EasyMock.expect(tableTag.getFieldHandlerFactory())
                .andReturn(fieldFactory).anyTimes();
        EasyMock.expect(columns[col].getLogicDataClass())
                .andReturn(columnClass).anyTimes();
        EasyMock.expect(columns[col].getRendererComponent()).andReturn(null)
                .anyTimes();
        EasyMock.expect(renderForm.getField(FIELD_PREFIX + COLUMN_NAMES[col]))
                .andReturn(renderField).anyTimes();
        EasyMock.expect(editForm.getField(FIELD_PREFIX + COLUMN_NAMES[col]))
                .andReturn(editField).anyTimes();
        EasyMock.expect(fieldFactory.getReadTransformerReference(renderField))
                .andReturn(renderReadTransformer).anyTimes();
        EasyMock.expect(fieldFactory.getWriteTransformerReference(renderField))
                .andReturn(renderWriteTransformer).anyTimes();
        EasyMock.expect(fieldFactory.getValidatorReference(renderField))
                .andReturn(renderValidator).anyTimes();
        EasyMock.expect(fieldFactory.getReadTransformerReference(editField))
                .andReturn(editReadTransformer).anyTimes();
        EasyMock.expect(fieldFactory.getWriteTransformerReference(editField))
                .andReturn(editWriteTransformer).anyTimes();
        EasyMock.expect(fieldFactory.getValidatorReference(editField))
                .andReturn(editValidator).anyTimes();
        EasyMock.expect(
                transformerFactory.getReadTransformer(tableTag, columnClass))
                .andReturn(readWrapper);
        EasyMock.expect(transformerFactory.getWriteTransformer(tableTag, columnClass))
                .andReturn(writeWrapper);
        EasyMock.expect(transformerFactory.getValidator(tableTag, columnClass))
                .andReturn(validatorWrapper);
        EasyMock.replay(columns);
        EasyMock.replay(transformerFactory, fieldFactory, renderField,
                editField, readWrapper, writeWrapper, validatorWrapper);
        prepareModel(true);

        TableFormController controller =
                new TableFormController(tableTag, transformerFactory);
        assertTrue("Wrong result",
                controller.installTransformersForColumnType(col));
        assertSame("Wrong edit read transformer", readWrapper,
                editReadTransformer.getTransformer());
        assertSame("Wrong edit write transformer", writeWrapper,
                editWriteTransformer.getTransformer());
        assertSame("Wrong edit validator", validatorWrapper,
                editValidator.getValidator());
        assertSame("Wrong render read transformer", readWrapper,
                renderReadTransformer.getTransformer());
        assertSame("Wrong render write transformer", writeWrapper,
                renderWriteTransformer.getTransformer());
        assertSame("Wrong render validator", validatorWrapper,
                renderValidator.getValidator());
    }

    /**
     * Prepares a test for accessing the column width controller and allows
     * setting the column widths to be used.
     *
     * @param refWidthController the reference to the width controller
     * @param colWidths an array with column widths; elements can be null; in
     *        that case, a percent width is assumed
     * @return the test form controller
     * @throws FormBuilderException if an error occurs
     */
    private TableFormController prepareWidthControllerTest(
            final AtomicReference<TableColumnWidthController> refWidthController,
            NumberWithUnit[] colWidths) throws FormBuilderException
    {
        TableColumnTag[] columns = createColumns();
        assertEquals("Wrong number of column widths", columns.length,
                colWidths.length);
        for (int i = 0; i < columns.length; i++)
        {
            EasyMock.expect(columns[i].getPercentWidth())
                    .andReturn((colWidths[i] == null) ? 10 + i : 0).anyTimes();
            EasyMock.expect(columns[i].getColumnWidth())
                    .andReturn(colWidths[i]).anyTimes();
        }
        EasyMock.replay(columns);
        EasyMock.expect(tableTag.getColumnWidthController())
                .andAnswer(new IAnswer<TableColumnWidthController>()
                {
                    public TableColumnWidthController answer() throws Throwable
                    {
                        if (refWidthController.get() == null)
                        {
                            refWidthController.set(TableColumnWidthController
                                    .newInstance(tableTag));
                        }
                        return refWidthController.get();
                    }
                }).anyTimes();

        return prepareControllerWithModel();
    }

    /**
     * Prepares a test for accessing the column width controller.
     *
     * @param refWidthController the reference to the width controller
     * @return the test form controller
     * @throws FormBuilderException if an error occurs
     */
    private TableFormController prepareWidthControllerTest(
            AtomicReference<TableColumnWidthController> refWidthController)
            throws FormBuilderException
    {
        NumberWithUnit[] colWidths = new NumberWithUnit[COLUMN_NAMES.length];
        for (int i = 0; i < colWidths.length; i++)
        {
            colWidths[i] = new NumberWithUnit((i + 1) * 10);
        }
        return prepareWidthControllerTest(refWidthController, colWidths);
    }

    /**
     * Tests whether the column width controller can be queried.
     */
    @Test
    public void testGetColumnWidthController() throws FormBuilderException
    {
        AtomicReference<TableColumnWidthController> refWidthController =
                new AtomicReference<TableColumnWidthController>();
        TableFormController controller =
                prepareWidthControllerTest(refWidthController);
        TableColumnWidthController widthController =
                controller.getColumnWidthController();
        assertEquals("Wrong width controller", refWidthController.get(),
                widthController);
    }

    /**
     * Tests whether the object for recalibrating table columns can be obtained.
     */
    @Test
    public void testGetTableColumnRecalibrator() throws FormBuilderException
    {
        AtomicReference<TableColumnWidthController> refWidthController =
                new AtomicReference<TableColumnWidthController>();
        TableFormController controller =
                prepareWidthControllerTest(refWidthController);
        TableColumnRecalibrator recalibrator =
                controller.getColumnRecalibrator();
        assertEquals("Wrong recalibrator", refWidthController.get(),
                recalibrator);
    }

    /**
     * Tests whether the object for calculating column widths can be obtained.
     */
    @Test
    public void testGetTableColumnWidthCalculator() throws FormBuilderException
    {
        AtomicReference<TableColumnWidthController> refWidthController =
                new AtomicReference<TableColumnWidthController>();
        TableFormController controller =
                prepareWidthControllerTest(refWidthController);
        TableColumnWidthCalculator widthCalculator =
                controller.getColumnWidthCalculator();
        assertEquals("Wrong width calculator", refWidthController.get(),
                widthCalculator);
    }

    /**
     * Tests whether the column width controller is correctly initialized.
     */
    @Test
    public void testInitWidthController() throws FormBuilderException
    {
        int[] widths = {
                10, 20, 0, 30
        };
        NumberWithUnit[] colWidths = new NumberWithUnit[widths.length];
        UnitSizeHandler sizeHandler =
                EasyMock.createMock(UnitSizeHandler.class);
        Object container = new Object();
        for (int i = 0; i < widths.length; i++)
        {
            if (widths[i] > 0)
            {
                NumberWithUnit nwu = EasyMock.createMock(NumberWithUnit.class);
                EasyMock.expect(nwu.toPixel(sizeHandler, container, false))
                        .andReturn(widths[i]);
                EasyMock.replay(nwu);
                colWidths[i] = nwu;
            }
            else
            {
                colWidths[i] = null;
            }
        }

        AtomicReference<TableColumnWidthController> refWidthController =
                new AtomicReference<TableColumnWidthController>();
        TableFormController controller =
                prepareWidthControllerTest(refWidthController, colWidths);
        assertEquals("Wrong total width", 60,
                controller.calculateFixedColumnWidths(sizeHandler, container));
        TableColumnWidthController widthController = refWidthController.get();
        for (int i = 0; i < widths.length; i++)
        {
            assertEquals("Wrong width for column " + i, widths[i],
                    widthController.getFixedWidth(i));
        }
    }

    /**
     * Tests whether the multi-selection state can be queried.
     */
    @Test
    public void testIsMultiSelection()
    {
        EasyMock.expect(tableTag.isMultiSelection()).andReturn(Boolean.TRUE);
        TableFormController controller = prepareControllerWithModel();

        assertTrue("Wrong result", controller.isMultiSelection());
        EasyMock.verify(tableTag);
    }
}
