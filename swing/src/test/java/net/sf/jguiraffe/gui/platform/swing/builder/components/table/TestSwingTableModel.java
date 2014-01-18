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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnClass;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableEditorValidationHandler;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.forms.ComponentHandler;
import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.FieldHandler;
import net.sf.jguiraffe.gui.forms.ValidationPhase;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for SwingTableModel.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableModel extends AbstractTableModelTest
{
    /** Constant for a new name. */
    private static final String NEW_NAME = "Hirsch";

    /** Stores the model to be tested. */
    private SwingTableModel model;

    /**
     * Sets up the test model.
     */
    protected void setUpModel()
    {
        model = createTableModel();
    }

    /**
     * Tests the number of columns in the model.
     */
    @Test
    public void testGetColumnCount()
    {
        setUpModel();
        assertEquals("Wrong number of columns", COLUMN_NAMES.length, model
                .getColumnCount());
    }

    /**
     * Tests querying the number of rows in the model.
     */
    @Test
    public void testGetRowCount()
    {
        setUpModel();
        assertEquals("Wrong number of rows", TEST_DATA.length, model
                .getRowCount());
    }

    /**
     * Tests whether the class for the columns can be queried if normal Java
     * classes are specified.
     */
    @Test
    public void testGetColumnClassJava()
    {
        setUpModel();
        for (int i = 0; i < COLUMN_TYPES.length; i++)
        {
            assertEquals("Wrong class for column " + i, COLUMN_TYPES[i], model
                    .getColumnClass(i));
        }
    }

    /**
     * Helper method for checking whether logic column classes are correctly
     * mapped to Java classes.
     *
     * @param logicClass the logic class
     * @param expected the expected Java class
     */
    private void checkGetColumnClassLogic(final ColumnClass logicClass,
            Class<?> expected)
    {
        setUpModel();
        TableTag parent = model.getTableTag();
        TableColumnTag colTag = new TableColumnTag()
        {
            @Override
            public ColumnClass getLogicDataClass()
            {
                return logicClass;
            }
        };
        parent.addColumn(colTag);
        model = new SwingTableModel(parent, new JTable());
        assertEquals("Wrong column class", expected, model.getColumnClass(model
                .getColumnCount() - 1));
    }

    /**
     * Tests whether the logic column class STRING is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicString()
    {
        checkGetColumnClassLogic(ColumnClass.STRING, String.class);
    }

    /**
     * Tests whether the logic column class BOOLEAN is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicBoolean()
    {
        checkGetColumnClassLogic(ColumnClass.BOOLEAN, Boolean.class);
    }

    /**
     * Tests whether the logic column class DATE is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicDate()
    {
        checkGetColumnClassLogic(ColumnClass.DATE, Date.class);
    }

    /**
     * Tests whether the logic column class FLOAT is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicFloat()
    {
        checkGetColumnClassLogic(ColumnClass.FLOAT, Double.class);
    }

    /**
     * Tests whether the logic column class NUMBER is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicNumber()
    {
        checkGetColumnClassLogic(ColumnClass.NUMBER, Number.class);
    }

    /**
     * Tests whether the logic column class ICON is correctly mapped.
     */
    @Test
    public void testGetColumnClassLogicIcon()
    {
        checkGetColumnClassLogic(ColumnClass.ICON, Icon.class);
    }

    /**
     * Tests querying the name for the columns.
     */
    @Test
    public void testGetColumnName()
    {
        setUpModel();
        for (int col = 0; col < COLUMN_NAMES.length; col++)
        {
            assertEquals("Wrong name for column " + col, COLUMN_NAMES[col],
                    model.getColumnName(col));
        }
    }

    /**
     * Tests the editable flags for the columns.
     */
    @Test
    public void testIsCellEditable()
    {
        setUpModel();
        for (int i = 0; i < EDIT_FLAGS.length; i++)
        {
            assertEquals("Wrong editable flag for column " + i, EDIT_FLAGS[i],
                    model.isCellEditable(0, i));
        }
    }

    /**
     * Tests accessing the model data if a conversion to a random access list is
     * needed.
     */
    @Test
    public void testGetModelDataConvert() throws Exception
    {
        Collection<PersonBean> dataList = setUpTestData();
        tableTag = setUpTableTag(dataList);
        setUpModel();
        assertNotSame("Data list was not changed", dataList, model
                .getModelData());
        assertTrue("List not converted to array list",
                model.getModelData() instanceof RandomAccess);
    }

    /**
     * Tests accessing the model data if the model collection is already a
     * random access list.
     */
    @Test
    public void testGetModelDataNoConvert() throws Exception
    {
        List<PersonBean> dataList = new ArrayList<PersonBean>(setUpTestData());
        tableTag = setUpTableTag(dataList);
        setUpModel();
        assertSame("Wrong data list", dataList, model.getModelData());
    }

    /**
     * Tests accessing data from the model.
     */
    @Test
    public void testGetValueAt()
    {
        setUpModel();
        for (int row = 0; row < TEST_DATA.length; row++)
        {
            for (int col = 0; col < TEST_DATA[row].length; col++)
            {
                assertEquals("Wrong value at cell (" + row + "," + col + ")",
                        TEST_DATA[row][col], model.getValueAt(row, col));
            }
        }
    }

    /**
     * Tests the hasEditor() method if no custom editor is set.
     */
    @Test
    public void testHasEditorFalse()
    {
        setUpModel();
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            assertFalse("Editor set for column " + i, model.hasEditor(i));
        }
    }

    /**
     * Checks for a custom editor if one is set.
     */
    @Test
    public void testHasEditorTrue()
    {
        setUpModel();
        TableColumnTagTestImpl colTag = (TableColumnTagTestImpl) model
                .getTableTag().getColumn(0);
        colTag.installEditor("testEditor");
        assertTrue("Custom editor not detected", model.hasEditor(0));
    }

    /**
     * Tests the hasRenderer() method if no custom renderer is set.
     */
    @Test
    public void testHasRendererFalse()
    {
        setUpModel();
        for (int i = 0; i < COLUMN_NAMES.length; i++)
        {
            assertFalse("Renderer set for column " + i, model.hasRenderer(i));
        }
    }

    /**
     * Tests whether a custom renderer is correctly detected.
     */
    @Test
    public void testHasRendererTrue()
    {
        setUpModel();
        TableColumnTagTestImpl colTag = (TableColumnTagTestImpl) model
                .getTableTag().getColumn(0);
        colTag.installRenderer("testRenderer");
        assertTrue("Custom renderer not detected", model.hasRenderer(0));
    }

    /**
     * Tests accessing the custom editor implementation for the table model.
     */
    @Test
    public void testGetEditor()
    {
        setUpModel();
        TableCellEditor editor = model.getEditor();
        assertTrue("Wrong editor returned",
                editor instanceof SwingTableCellEditor);
        SwingTableCellEditor swingEd = (SwingTableCellEditor) editor;
        assertSame("Model not initialized", model, swingEd.getModel());
    }

    /**
     * Tests whether always the same editor is returned.
     */
    @Test
    public void testGetEditorCached()
    {
        setUpModel();
        TableCellEditor editor = model.getEditor();
        assertSame("Multiple editor instances created", editor, model
                .getEditor());
    }

    /**
     * Tests accessing the custom renderer implementation for the table model.
     */
    @Test
    public void testGetRenderer()
    {
        setUpModel();
        TableCellRenderer renderer = model.getRenderer();
        assertTrue("Wrong renderer returned",
                renderer instanceof SwingTableCellRenderer);
        SwingTableCellRenderer swingRend = (SwingTableCellRenderer) renderer;
        assertSame("Model not initialized", model, swingRend.getModel());
    }

    /**
     * Tests whether always the same renderer instance is returned.
     */
    @Test
    public void testGetRendererCached()
    {
        setUpModel();
        TableCellRenderer renderer = model.getRenderer();
        assertSame("Multiple renderer instances created", renderer, model
                .getRenderer());
    }

    /**
     * Creates a component handler mock object and initializes it to expect some
     * default calls.
     *
     * @return the initialized mock object
     */
    private ComponentHandler<Object> createComponentHandlerMock()
    {
        @SuppressWarnings("unchecked")
        ComponentHandler<Object> cmpHandler = EasyMock
                .createMock(ComponentHandler.class);
        EasyMock.expect(cmpHandler.getComponent()).andStubReturn(
                new JTextField());
        return cmpHandler;
    }

    /**
     * Creates and initializes a field handler mock. Some default method calls
     * performed by the form will already be expected.
     *
     * @param index the index of the test field
     * @param cmpHandler the component handler to be used
     * @return the field handler mock
     */
    private FieldHandler createFieldHandlerMock(int index,
            ComponentHandler<?> cmpHandler)
    {
        FieldHandler fldHandler = EasyMock.createMock(FieldHandler.class);
        fldHandler.getComponentHandler();
        EasyMock.expectLastCall().andStubReturn(cmpHandler);
        EasyMock.expect(fldHandler.getPropertyName()).andStubReturn(null);
        fldHandler.getType();
        EasyMock.expectLastCall().andStubReturn(COLUMN_TYPES[index]);
        return fldHandler;
    }

    /**
     * Tests validation of a column that has no custom editor.
     */
    @Test
    public void testSetValueNoEditor()
    {
        final String newValue = "Harry";
        setUpModel();
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        ComponentHandler<Object> cmpHandler = createComponentHandlerMock();
        FieldHandler fldHandler = createFieldHandlerMock(0, cmpHandler);
        cmpHandler.setData(newValue);
        EasyMock.expect(fldHandler.validate(ValidationPhase.SYNTAX)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(fldHandler.validate(ValidationPhase.LOGIC)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(fldHandler.getData()).andReturn(newValue);
        Map<String, ValidationResult> map = new HashMap<String, ValidationResult>();
        map.put(COLUMN_NAMES[0], DefaultValidationResult.VALID);
        DefaultFormValidatorResults formResults = new DefaultFormValidatorResults(
                map);
        EasyMock.expect(
                valHandler.validationPerformed(model.getTable(), tableTag
                        .getRowEditForm(), tableTag, formResults, 0, 0))
                .andReturn(Boolean.FALSE);
        EasyMock.replay(cmpHandler, fldHandler, valHandler);
        tableTag.getRowEditForm().addField(COLUMN_NAMES[0], fldHandler);
        tableTag.setEditorValidationHandler(valHandler);
        model.setValueAt(newValue, 0, 0);
        List<?> dataList = model.getModelData();
        PersonBean bean = (PersonBean) dataList.get(0);
        assertEquals("New value not set in list", newValue, bean.getFirstName());
        EasyMock.verify(cmpHandler, fldHandler, valHandler);
    }

    /**
     * Tests the setValueAt() method when the corresponding column uses its own
     * editor. In this case the editor is responsible for validation and setting
     * the value in the model.
     */
    @Test
    public void testSetValueEditor()
    {
        ComponentHandler<?> cmpHandler = createComponentHandlerMock();
        FieldHandler fldHandler = createFieldHandlerMock(0, cmpHandler);
        EasyMock.replay(fldHandler, cmpHandler);
        setUpModel();
        tableTag.getRowEditForm().addField(COLUMN_NAMES[0], fldHandler);
        ((TableColumnTagTestImpl) tableTag.getColumn(0))
                .installEditor(new JTextField());
        model.setValueAt("NewValue", 0, 0);
        EasyMock.verify(fldHandler, cmpHandler);
    }

    /**
     * Tests setting a value when validation fails. In this case the value
     * should not be applied.
     */
    @Test
    public void testSetValueNonValid()
    {
        final String newValue = "Harry";
        setUpModel();
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        ComponentHandler<Object> cmpHandler = createComponentHandlerMock();
        FieldHandler fldHandler = createFieldHandlerMock(0, cmpHandler);
        cmpHandler.setData(newValue);
        ValidationMessage vmsg = EasyMock.createMock(ValidationMessage.class);
        EasyMock.expect(vmsg.getLevel()).andReturn(ValidationMessageLevel.ERROR);
        EasyMock.replay(vmsg);
        DefaultValidationResult vr = new DefaultValidationResult.Builder()
                .addValidationMessage(vmsg).build();
        EasyMock.expect(fldHandler.validate(ValidationPhase.SYNTAX)).andReturn(
                vr);
        DefaultFormValidatorResults formResults = new DefaultFormValidatorResults(
                Collections.singletonMap(COLUMN_NAMES[0], vr));
        EasyMock.expect(
                valHandler.validationPerformed(model.getTable(), tableTag
                        .getRowEditForm(), tableTag, formResults, 0, 0))
                .andReturn(Boolean.FALSE);
        EasyMock.replay(cmpHandler, fldHandler, valHandler);
        tableTag.getRowEditForm().addField(COLUMN_NAMES[0], fldHandler);
        tableTag.setEditorValidationHandler(valHandler);
        model.setValueAt(newValue, 0, 0);
        PersonBean bean = (PersonBean) model.getModelData().get(0);
        assertEquals("Bean data was changed", TEST_DATA[0][0], bean
                .getFirstName());
        EasyMock.verify(cmpHandler, fldHandler, valHandler, vmsg);
    }

    /**
     * Tests setting a value when the validation handler intercepts.
     */
    @Test
    public void testSetValueHandler()
    {
        final String newValue = "Harry";
        final String newValue2 = "Harry2";
        setUpModel();
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        ComponentHandler<Object> cmpHandler = createComponentHandlerMock();
        FieldHandler fldHandler = createFieldHandlerMock(0, cmpHandler);
        cmpHandler.setData(newValue);
        EasyMock.expect(fldHandler.validate(ValidationPhase.SYNTAX)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(fldHandler.validate(ValidationPhase.LOGIC)).andReturn(
                DefaultValidationResult.VALID);
        DefaultFormValidatorResults formResults = new DefaultFormValidatorResults(
                Collections.singletonMap(COLUMN_NAMES[0],
                        DefaultValidationResult.VALID));
        EasyMock.expect(
                valHandler.validationPerformed(model.getTable(), tableTag
                        .getRowEditForm(), tableTag, formResults, 0, 0))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(fldHandler.validate(ValidationPhase.SYNTAX)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(fldHandler.validate(ValidationPhase.LOGIC)).andReturn(
                DefaultValidationResult.VALID);
        EasyMock.expect(fldHandler.getData()).andReturn(newValue2);
        EasyMock.replay(cmpHandler, fldHandler, valHandler);
        tableTag.getRowEditForm().addField(COLUMN_NAMES[0], fldHandler);
        tableTag.setEditorValidationHandler(valHandler);
        model.setValueAt(newValue, 0, 0);
        List<?> dataList = model.getModelData();
        PersonBean bean = (PersonBean) dataList.get(0);
        assertEquals("New value not set in list", newValue2, bean
                .getFirstName());
        EasyMock.verify(cmpHandler, fldHandler, valHandler);
    }

    /**
     * Tests whether a newly set value can immediately be retrieved from the
     * model.
     */
    @Test
    public void testSetAndGetValue()
    {
        setUpModel();
        TableEditorValidationHandler valHandler = EasyMock
                .createMock(TableEditorValidationHandler.class);
        DefaultFormValidatorResults vres = new DefaultFormValidatorResults(
                Collections.singletonMap(COLUMN_NAMES[0],
                        DefaultValidationResult.VALID));
        EasyMock.expect(
                valHandler.validationPerformed(model.getTable(), tableTag
                        .getRowEditForm(), tableTag, vres, 1, 0)).andReturn(
                Boolean.TRUE);
        EasyMock.replay(valHandler);
        tableTag.setEditorValidationHandler(valHandler);
        final String newValue = "Harry";
        model.setValueAt(newValue, 1, 0);
        assertEquals("Value was not set", newValue, model.getValueAt(1, 0));
        EasyMock.verify(valHandler);
    }

    /**
     * Prepares a test of the fire() methods indicating an update of the table
     * model.
     */
    private void prepareFireUpdateTest()
    {
        setUpModel();
        List<Object> data = model.getModelData();
        assertEquals("Wrong model value",
                AbstractTableModelTest.TEST_DATA[0][1], model.getValueAt(0, 1));
        AbstractTableModelTest.PersonBean bean = (AbstractTableModelTest.PersonBean) data
                .get(0);
        bean.setLastName(NEW_NAME);
    }

    /**
     * Tests whether the new value from the model is returned after rows have
     * been inserted.
     */
    @Test
    public void testGetValueAtAfterRowsInserted()
    {
        prepareFireUpdateTest();
        model.fireTableRowsInserted(0, 1);
        assertEquals("Value not changed", NEW_NAME, model.getValueAt(0, 1));
    }

    /**
     * Tests whether the range of rows is taken into account by
     * fireTableRowsInserted().
     */
    @Test
    public void testGetValueAtAfterRowsInsertedOutOfRange()
    {
        prepareFireUpdateTest();
        model.fireTableRowsInserted(2, 3);
        assertEquals("Value not cached",
                AbstractTableModelTest.TEST_DATA[0][1], model.getValueAt(0, 1));
    }

    /**
     * Tests whether the new value from the model is returned after rows have
     * been updated.
     */
    @Test
    public void testGetValueAtAfterRowsUpdated()
    {
        prepareFireUpdateTest();
        model.fireTableRowsUpdated(0, 1);
        assertEquals("Value not changed", NEW_NAME, model.getValueAt(0, 1));
    }

    /**
     * Tests whether the range of rows is taken into account by
     * fireTableRowsUpdated().
     */
    @Test
    public void testGetValueAtAfterRowsUpdatedOutOfRange()
    {
        prepareFireUpdateTest();
        model.fireTableRowsUpdated(2, 3);
        assertEquals("Value not cached",
                AbstractTableModelTest.TEST_DATA[0][1], model.getValueAt(0, 1));
    }

    /**
     * Tests whether the new value from the model is returned after rows have
     * been deleted.
     */
    @Test
    public void testGetValueAtAfterRowsDeleted()
    {
        prepareFireUpdateTest();
        model.fireTableRowsDeleted(0, 1);
        assertEquals("Value not changed", NEW_NAME, model.getValueAt(0, 1));
    }

    /**
     * Tests whether the range of rows is taken into account by
     * fireTableRowsDeleted().
     */
    @Test
    public void testGetValueAtAfterRowsDeletedOutOfRange()
    {
        prepareFireUpdateTest();
        model.fireTableRowsDeleted(2, 3);
        assertEquals("Value not cached",
                AbstractTableModelTest.TEST_DATA[0][1], model.getValueAt(0, 1));
    }

    /**
     * Tests whether the new value from the model is returned after a table data
     * change event is received.
     */
    @Test
    public void testGetValueAtAfterDataChanged()
    {
        prepareFireUpdateTest();
        model.fireTableDataChanged();
        assertEquals("Value not changed", NEW_NAME, model.getValueAt(0, 1));
    }
}
