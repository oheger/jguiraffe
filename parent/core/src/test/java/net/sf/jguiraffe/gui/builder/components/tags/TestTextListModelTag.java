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

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * Test class for TextListModelTag.
 *
 * @author Oliver Heger
 * @version $Id: TestTextListModelTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestTextListModelTag extends AbstractTagTest
{
    /** Constant for the name of the test script. */
    private static final String SCRIPT = "textlistmodel";

    /** Constant for a variable that is referenced by an item tag. */
    private static final String VAR_NAME = "intConst";

    /** Constant for a test value referenced by a list item tag. */
    private static final Object ITEM_VALUE = 42;

    /**
     * Helper method for executing a builder script.
     *
     * @throws Exception if an error occurs
     */
    private void executeBuilderOperation() throws Exception
    {
        context.setVariable(VAR_NAME, ITEM_VALUE);
        executeScript(SCRIPT);
    }

    /**
     * Tests creating some list models.
     */
    public void testCreateModels() throws Exception
    {
        executeBuilderOperation();
        ListModel model = (ListModel) context.getVariable("model1");
        assertNotNull("Model 1 not found", model);
        assertEquals("Wrong number of items in model 1", 3, model.size());
        assertEquals("Wrong value class", Integer.class, model.getType());
        for (int i = 1; i <= model.size(); i++)
        {
            assertEquals("Wrong item in model 1 at " + i, "item" + i, model
                    .getDisplayObject(i - 1));
            assertEquals("Wrong value in model 1 at " + i, Integer
                    .valueOf(i - 1), ListModelUtils.getValue(model, i - 1));
        }

        model = (ListModel) context.getVariable("modelPasta");
        assertNotNull("Model 2 not found", model);
        assertEquals("Wrong number of items in model 2", 4, model.size());
        assertEquals("Wrong item class in model 2", String.class, model
                .getType());
        assertEquals("Wrong item in model 2 at 0", "Spaghetti", model
                .getDisplayObject(0));
        assertEquals("Wrong item in model 2 at 2", "Penne", model
                .getDisplayObject(2));
        assertEquals("Wrong item in model 2 at 3", "Taggliatelle", model
                .getDisplayObject(3));
        assertEquals("Wrong value in model 2 at 1", "2", model
                .getValueObject(1));
        assertEquals("Wrong value in model 2 at 3", String.valueOf(ITEM_VALUE),
                model.getValueObject(3));

        model = (ListModel) context.getVariable("modelMix");
        assertNotNull("Model 3 not found", model);
        assertEquals("Wrong number of elelements in model 3", 4, model.size());
        assertEquals("Wrong item class in model 3", String.class, model
                .getType());
        assertEquals("Wrong item int model 3 at 0", "onions", model
                .getDisplayObject(0));
        assertNull("Got value in model 3 at 0", model.getValueObject(0));
        assertEquals("Wrong item in model 3 at 1", "broccoli", model
                .getDisplayObject(1));
        assertEquals("Wrong value in model 3 at 1", "x", model
                .getValueObject(1));
        assertEquals("Wrong item in model 3 at 2", "extra cheese", model
                .getDisplayObject(2));
        assertNull("Got value in model 3 at 2", model.getValueObject(2));
        assertEquals("Wrong item in model 3 at 3", "tuna", model
                .getDisplayObject(3));
        assertEquals("Wrong value in model 3 at 3", "y", model
                .getValueObject(3));

        model = (ListModel) context.getVariable("modelEmpty");
        assertNotNull("Model 4 not found", model);
        assertEquals("Got elements in empty model", 0, model.size());
    }

    /**
     * Tests executing an undefined item tag.
     */
    public void testUndefinedItem() throws Exception
    {
        errorScript(SCRIPT, "ERR_UNDEFINED",
                "Could execute undefined list model item tag!");
    }

    /**
     * Tests executing a stand alone item tag.
     */
    public void testStandAloneItem() throws Exception
    {
        errorScript(SCRIPT, "ERR_ALONE",
                "Could execute stand alone list model item tag!");
    }

    /**
     * Tests a tag with an invalid value reference.
     */
    public void testInvalidValueRef() throws Exception
    {
        errorScript(SCRIPT, "ERR_UNKNOWNREF",
                "Invalid value reference not detected!");
    }

    /**
     * Tests whether type conversion is performed.
     */
    public void testTypeConversion() throws Exception
    {
        executeBuilderOperation();
        final int expSize = 3;
        ListModel model = (ListModel) context.getVariable("modelInt");
        assertNotNull("Model not found", model);
        assertEquals("Wrong number of elements", expSize, model.size());
        for (int i = 0; i < model.size(); i++)
        {
            Integer expValue = Integer.valueOf(i + 1);
            assertEquals("Wrong value at " + i, expValue, model
                    .getValueObject(i));
        }
    }
}
