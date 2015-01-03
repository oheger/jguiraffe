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
package net.sf.jguiraffe.gui.builder.components.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.model.EditableComboBoxModel;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for ListModelUtils.
 *
 * @author Oliver Heger
 * @version $Id: TestListModelUtils.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestListModelUtils
{
    /** Constant for the prefix used for value objects. */
    private static final String VALUE = "value";

    /** Constant for the prefix used for display objects. */
    private static final String DISPLAY = "display";

    /** Constant for the name of the model bean. */
    private static final String MODEL_NAME = "myListModel";

    /**
     * Tests the getValue() method.
     */
    @Test
    public void testGetValue()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        for (int i = 0; i < 10; i++)
        {
            assertEquals("Wrong value in model 1 for index " + i, VALUE + i,
                    ListModelUtils.getValue(model1, i));
        }

        IntListModel model2 = new IntListModel(10);
        for (int i = 0; i < 10; i++)
        {
            assertEquals("Wrong value in model 2 for index " + i, Integer
                    .valueOf(i), ListModelUtils.getValue(model2, i));
        }
    }

    /**
     * Tests the getValue() method if an invalid index is specified.
     */
    @Test
    public void testGetValueInvalidIndex()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        assertNull("Got value for too small index", ListModelUtils.getValue(
                model1, -1));
        assertNull("Got value for too big index", ListModelUtils.getValue(
                model1, 20));
    }

    /**
     * Tests the getIndex() method.
     */
    @Test
    public void testGetIndex()
    {
        SimpleListModel model = new SimpleListModel(10);
        for (int i = 0; i < 10; i++)
        {
            assertEquals(i, ListModelUtils.getIndex(model, VALUE + i));
        }
    }

    /**
     * Tests getIndex() if the model uses indices as values.
     */
    @Test
    public void testGetIndexIntegerModel()
    {
        IntListModel model = new IntListModel(10);
        for (int i = 0; i < 10; i++)
        {
            assertEquals(i, ListModelUtils.getIndex(model, Integer.valueOf(i)));
        }
    }

    /**
     * Tests getIndex() if an unknown value is specified.
     */
    @Test
    public void testGetIndexUnknownValue()
    {
        IntListModel model = new IntListModel(10);
        assertEquals("Wrong index for unknown value", -1, ListModelUtils
                .getIndex(model, "unknown value"));
    }

    /**
     * Tests getIndex() if a null value is specified for an integer model.
     */
    @Test
    public void testGetIndexNullValueIntegerModel()
    {
        IntListModel model = new IntListModel(10);
        assertEquals("Wrong index for null value", -1, ListModelUtils.getIndex(
                model, null));
    }

    /**
     * Tests the corner case that a model is empty when determining the index of
     * a value object.
     */
    @Test
    public void testGetIndexEmptyModel()
    {
        SimpleListModel model = new SimpleListModel(0);
        assertEquals("Wrong result", ListModelUtils.IDX_UNDEFINED,
                ListModelUtils.getIndex(model, VALUE));
    }

    /**
     * Tests whether the index of a display object can be determined.
     */
    @Test
    public void testGetDisplayIndexSuccess()
    {
        SimpleListModel model = new SimpleListModel(8);
        for (int i = 0; i < model.size(); i++)
        {
            assertEquals("Wrong index for " + i, i,
                    ListModelUtils.getDisplayIndex(model, DISPLAY + i));
        }
    }

    /**
     * Tests getDisplayIndex() if the display object cannot be resolved.
     */
    @Test
    public void testGetDisplayIndexUnknown()
    {
        SimpleListModel model = new SimpleListModel(4);
        assertEquals("Wrong result", ListModelUtils.IDX_UNDEFINED,
                ListModelUtils.getDisplayIndex(model, "unknown display object"));
    }

    /**
     * Tests the getValues() method.
     */
    @Test
    public void testGetValues()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        String[] values = (String[]) ListModelUtils.getValues(model1, new int[] {
                0, 5, 8
        });
        assertEquals("Wrong number of values", 3, values.length);
        assertEquals("Wrong value at 0", "value0", values[0]);
        assertEquals("Wrong value at 1", "value5", values[1]);
        assertEquals("Wrong value at 2", "value8", values[2]);
    }

    /**
     * Tests getValues() if no indices are specified.
     */
    @Test
    public void testGetValuesNull()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        assertEquals("Got values", 0,
                ListModelUtils.getValues(model1, null).length);
    }

    /**
     * Tests the getIndices() method.
     */
    @Test
    public void testGetIndices()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        int[] indices = ListModelUtils.getIndices(model1, new Object[] {
                "value3", "value6"
        });
        assertEquals("Wrong number of indices", 2, indices.length);
        assertEquals("Wrong index at 0", 3, indices[0]);
        assertEquals("Wrong index at 1", 6, indices[1]);
    }

    /**
     * Tests getIndices() if a null array for the values is passed.
     */
    @Test
    public void testGetIndicesNull()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        assertEquals("Got indices for null values", 0, ListModelUtils
                .getIndices(model1, null).length);
    }

    /**
     * Tests getIndices() if an empty array for the values is passed.
     */
    @Test
    public void testGetIndicesNoValues()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        assertEquals("Got indices for empty values", 0, ListModelUtils
                .getIndices(model1, new Object[0]).length);
    }

    /**
     * Tests whether invalid values passed to getIndices() are sorted out.
     */
    @Test
    public void testGetIndicesWithInvalidValue()
    {
        SimpleListModel model1 = new SimpleListModel(10);
        int[] indices = ListModelUtils.getIndices(model1, new Object[] {
                "unknown1", "value9", "value10", "value1"
        });
        assertEquals("Wrong number of indices", 2, indices.length);
        assertEquals("Wrong index at 0", 9, indices[0]);
        assertEquals("Wrong index at 1", 1, indices[1]);
    }

    /**
     * Tests the initializeListModel() method if the model is already set.
     */
    @Test
    public void testInitializeListModelAlreadySet()
            throws MissingAttributeException, FormBuilderException
    {
        ListModelSupport support = EasyMock.createMock(ListModelSupport.class);
        ListModel model = EasyMock.createMock(ListModel.class);
        EasyMock.expect(support.getListModel()).andReturn(model);
        EasyMock.replay(support, model);
        ListModelUtils.initializeListModel(support);
        EasyMock.verify(support, model);
    }

    /**
     * Prepares a component builder data object. A bean context mock is set.
     *
     * @return the builder data
     */
    private ComponentBuilderData setUpBuilderData()
    {
        ComponentBuilderData data = new ComponentBuilderData();
        BeanContext ctx = EasyMock.createMock(BeanContext.class);
        data.setBeanContext(ctx);
        return data;
    }

    /**
     * Tests a full initialization of the list model that succeeds.
     */
    @Test
    public void testInitializeListModelSuccess()
            throws MissingAttributeException, FormBuilderException
    {
        ListModelSupport support = EasyMock.createMock(ListModelSupport.class);
        ListModel model = EasyMock.createMock(ListModel.class);
        ComponentBuilderData data = setUpBuilderData();
        JellyContext ctx = new JellyContext();
        EasyMock.expect(support.getListModel()).andReturn(null);
        EasyMock.expect(support.getModelRef()).andReturn(MODEL_NAME);
        EasyMock.expect(support.getContext()).andReturn(ctx);
        EasyMock.expect(data.getBeanContext().containsBean(MODEL_NAME))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(data.getBeanContext().getBean(MODEL_NAME)).andReturn(
                model);
        support.setListModel(model);
        EasyMock.replay(support, model, data.getBeanContext());
        data.put(ctx);
        ListModelUtils.initializeListModel(support);
        EasyMock.verify(support, model, data.getBeanContext());
    }

    /**
     * Tries to initialize the list model if the model name is not specified.
     * This should cause an exception.
     */
    @Test
    public void testInitializeListModelNoModelName()
            throws MissingAttributeException, FormBuilderException
    {
        ListModelSupport support = EasyMock.createMock(ListModelSupport.class);
        EasyMock.expect(support.getListModel()).andReturn(null);
        EasyMock.expect(support.getModelRef()).andReturn(null);
        EasyMock.replay(support);
        try
        {
            ListModelUtils.initializeListModel(support);
            fail("Missing model name not detected!");
        }
        catch (MissingAttributeException maex)
        {
            EasyMock.verify(support);
        }
    }

    /**
     * Tests initializeListModel() if the model bean cannot be found. This
     * should cause an exception.
     */
    @Test
    public void testInitializeModelModelNotFound()
            throws MissingAttributeException, FormBuilderException
    {
        ListModelSupport support = EasyMock.createMock(ListModelSupport.class);
        ComponentBuilderData data = setUpBuilderData();
        JellyContext ctx = new JellyContext();
        EasyMock.expect(support.getListModel()).andReturn(null);
        EasyMock.expect(support.getModelRef()).andReturn(MODEL_NAME);
        EasyMock.expect(support.getContext()).andReturn(ctx);
        EasyMock.expect(data.getBeanContext().containsBean(MODEL_NAME))
                .andReturn(Boolean.FALSE);
        EasyMock.replay(support, data.getBeanContext());
        data.put(ctx);
        try
        {
            ListModelUtils.initializeListModel(support);
            fail("Missing model not detected!");
        }
        catch (FormBuilderException fex)
        {
            EasyMock.verify(support, data.getBeanContext());
        }
    }

    /**
     * Tests initializeListModel() if the model bean does not implement the
     * expected interface. This should cause an exception.
     */
    @Test
    public void testInitializeModelInvalidModelBean()
            throws MissingAttributeException, FormBuilderException
    {
        ListModelSupport support = EasyMock.createMock(ListModelSupport.class);
        ComponentBuilderData data = setUpBuilderData();
        JellyContext ctx = new JellyContext();
        EasyMock.expect(support.getListModel()).andReturn(null);
        EasyMock.expect(support.getModelRef()).andReturn(MODEL_NAME);
        EasyMock.expect(support.getContext()).andReturn(ctx);
        EasyMock.expect(data.getBeanContext().containsBean(MODEL_NAME))
                .andReturn(Boolean.TRUE);
        EasyMock.expect(data.getBeanContext().getBean(MODEL_NAME)).andReturn(
                this);
        EasyMock.replay(support, data.getBeanContext());
        data.put(ctx);
        try
        {
            ListModelUtils.initializeListModel(support);
            fail("Invalid model not detected!");
        }
        catch (FormBuilderException fex)
        {
            EasyMock.verify(support, data.getBeanContext());
        }
    }

    /**
     * Tests initializeListModel() if null is passed in. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitializeListModelNull() throws MissingAttributeException,
            FormBuilderException
    {
        ListModelUtils.initializeListModel(null);
    }

    /**
     * Tests whether a list model implementing the EditableComboBoxModel
     * interface is detected.
     */
    @Test
    public void testFetchEditableComboBoxModelImplemented()
    {
        ListModel model = EasyMock.createMock(EditableComboListModel.class);
        EasyMock.replay(model);
        assertSame("Wrong result", model,
                ListModelUtils.fetchEditableComboBoxModel(model));
    }

    /**
     * Tests the transformation to a display object for the dummy
     * EditableComboBoxModel.
     */
    @Test
    public void testFetchEditableComboBoxDummyToDisplay()
    {
        EditableComboBoxModel comboModel =
                ListModelUtils.fetchEditableComboBoxModel(null);
        assertSame("Object was transformed", this, comboModel.toDisplay(this));
    }

    /**
     * Tests the transformation to a value object for the dummy
     * EditableComboBoxModel.
     */
    @Test
    public void testFetchEditableComboBoxDummyToValue()
    {
        EditableComboBoxModel comboModel =
                ListModelUtils.fetchEditableComboBoxModel(null);
        assertSame("Object was transformed", this, comboModel.toValue(this));
    }

    /**
     * Test implementation of the ListModel interface. Returns different display
     * and value objects.
     */
    static class SimpleListModel implements ListModel
    {
        private final int size;

        public SimpleListModel(int sz)
        {
            size = sz;
        }

        public int size()
        {
            return size;
        }

        public Object getDisplayObject(int index)
        {
            return DISPLAY + index;
        }

        public Object getValueObject(int index)
        {
            return VALUE + index;
        }

        public Class<?> getType()
        {
            return String.class;
        }
    }

    /**
     * Another test implementation of the ListModel interface whose
     * getValueObject() method always returns null.
     */
    static class IntListModel extends SimpleListModel
    {
        public IntListModel(int sz)
        {
            super(sz);
        }

        @Override
        public Class<?> getType()
        {
            return Integer.class;
        }

        @Override
        public Object getValueObject(int index)
        {
            return null;
        }
    }

    /**
     * A combined interface used for testing whether implementations of
     * {@code EditableComboBoxModel} are detected.
     */
    private static interface EditableComboListModel extends ListModel,
            EditableComboBoxModel
    {
    }
}
