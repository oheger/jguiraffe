/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.window.ctrl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.ToolTipManager;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidationMessageFormat;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for {@code ToolTipFieldMarker}.
 *
 * @author Oliver Heger
 * @version $Id: TestToolTipFieldMarker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestToolTipFieldMarker
{
    /** Constant for a prefix of a validation message text. */
    private static final String MESSAGE = "Validation message for field ";

    /** Constant for the prefix for field names. */
    private static final String FIELD = "field";

    /** Constant for the prefix for a valid field name. */
    private static final String VALID_FIELD = "validField";

    /** Constant for a field that has no validation messages. */
    private static final String FIELD_NOMSG = "noMessageField";

    /** A collection with validation messages used for invalid fields. */
    private static Collection<ValidationMessage> messages;

    /** An empty collection with validation messages used for valid fields. */
    private static Collection<ValidationMessage> emptyMessages;

    /** Constant for the number of fields in the validation results object. */
    private static final int FIELD_COUNT = 5;

    /** A mock object with validation results. */
    private FormValidatorResults validationResults;

    /** A list with validation result mock objects. */
    private List<ValidationResult> results;

    /** The controller object used by the tests. */
    private FormController controller;

    /** The tool tip marker object to be tested. */
    private ToolTipFieldMarker marker;

    @BeforeClass
    public static void setUpBeforeClass()
    {
        ValidationMessage msg = EasyMock
                .createNiceMock(ValidationMessage.class);
        messages = Collections.singleton(msg);
        emptyMessages = Collections.emptySet();
    }

    @Before
    public void setUp() throws Exception
    {
        marker = new ToolTipFieldMarker(
                new FormValidationMessageFormatTestImpl());
    }

    /**
     * Creates the test controller with all related helper objects.
     */
    private void setUpController()
    {
        controller = new FormController();
        ComponentBuilderData data = new ComponentBuilderData();
        data.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        data.setToolTipManager(EasyMock.createMock(ToolTipManager.class));
        controller.setComponentBuilderData(data);
    }

    /**
     * Creates a mock for validation results that returns the specified field
     * names. Fields starting with the VALID_FIELD prefix have validation
     * results without messages. All others have messages.
     *
     * @param fields a set with field names
     */
    private void setUpValidationResults(Set<String> fields)
    {
        if (validationResults == null)
        {
            validationResults = EasyMock.createMock(FormValidatorResults.class);
        }
        EasyMock.expect(validationResults.getFieldNames()).andReturn(fields);
        results = new ArrayList<ValidationResult>(fields.size());
        for (String field : fields)
        {
            ValidationResult vres = EasyMock.createMock(ValidationResult.class);
            Collection<ValidationMessage> msgs = (field.startsWith(VALID_FIELD)) ? emptyMessages
                    : messages;
            EasyMock.expect(vres.getValidationMessages()).andReturn(msgs);
            EasyMock.replay(vres);
            results.add(vres);
            EasyMock.expect(validationResults.getResultsFor(field)).andReturn(
                    vres);
        }
    }

    /**
     * Returns a set with the default field names contained in the validation
     * result object.
     *
     * @return the set with the default field names
     */
    private static Set<String> setUpDefaultFields()
    {
        Set<String> set = new LinkedHashSet<String>();
        for (int i = 0; i < FIELD_COUNT; i++)
        {
            set.add(FIELD + i);
            set.add(VALID_FIELD + i);
        }
        return set;
    }

    /**
     * Returns the tool tip manager mock.
     *
     * @return the tool tip manager
     */
    private ToolTipManager getToolTipManager()
    {
        return controller.getComponentBuilderData().getToolTipManager();
    }

    /**
     * Helper method for generating the tool tip message for the specified
     * field.
     *
     * @param field the field
     * @return the corresponding message
     */
    private static String message(String field)
    {
        return MESSAGE + field;
    }

    /**
     * Creates a test event.
     *
     * @return the test event
     */
    private FormControllerValidationEvent event()
    {
        return new FormControllerValidationEvent(controller, validationResults);
    }

    /**
     * Tries to create an instance without a format object. This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInitNull()
    {
        new ToolTipFieldMarker(null);
    }

    /**
     * Tests whether validation results are correctly processed.
     */
    @Test
    public void testValidationPerformed()
    {
        setUpController();
        setUpValidationResults(setUpDefaultFields());
        for (int i = 0; i < FIELD_COUNT; i++)
        {
            String field = FIELD + i;
            getToolTipManager().setAdditionalToolTip(field, message(field));
        }
        EasyMock.replay(getToolTipManager(), validationResults);
        marker.validationPerformed(event());
        EasyMock.verify(results.toArray());
        EasyMock.verify(getToolTipManager(), validationResults);
    }

    /**
     * Tests whether a field for which no messages are produced is treated
     * correctly.
     */
    @Test
    public void testValidationPerformedNoMessage()
    {
        setUpController();
        setUpValidationResults(Collections.singleton(FIELD_NOMSG));
        EasyMock.replay(getToolTipManager(), validationResults);
        marker.validationPerformed(event());
        EasyMock.verify(results.toArray());
        EasyMock.verify(getToolTipManager(), validationResults);
    }

    /**
     * Tests whether tool tips with validation messages are removed if the
     * messages disappear.
     */
    @Test
    public void testValidationPerformedResetToolTips()
    {
        setUpController();
        Set<String> fields = new HashSet<String>();
        String fld1 = FIELD + "0";
        fields.add(FIELD);
        fields.add(fld1);
        setUpValidationResults(fields);
        getToolTipManager().setAdditionalToolTip(FIELD, message(FIELD));
        getToolTipManager().setAdditionalToolTip(fld1, message(fld1));
        EasyMock.expectLastCall().times(2);
        setUpValidationResults(Collections.singleton(fld1));
        getToolTipManager().setAdditionalToolTip(FIELD, null);
        EasyMock.replay(getToolTipManager(), validationResults);
        marker.validationPerformed(event());
        marker.validationPerformed(event());
        EasyMock.verify(results.toArray());
        EasyMock.verify(getToolTipManager(), validationResults);
    }

    /**
     * A test implementation of FormValidationMessageFormat. This implementation
     * tests whether formatField() is called with expected parameters and
     * returns deterministic texts.
     */
    private class FormValidationMessageFormatTestImpl extends
            FormValidationMessageFormat
    {
        @Override
        public String formatField(FormValidatorResults res, Form form,
                String field)
        {
            assertEquals("Wrong validation results", validationResults, res);
            assertEquals("Wrong form", controller.getForm(), form);
            return FIELD_NOMSG.equals(field) ? "" : message(field);
        }
    }
}
