/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;
import net.sf.jguiraffe.gui.builder.window.WindowBuilderData;
import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ColorFieldMarker.
 *
 * @author Oliver Heger
 * @version $Id: TestColorFieldMarker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestColorFieldMarker
{
    /** Constant for the normal background color. */
    private static final Color NORMAL_BG = ColorHelper.NamedColor.WHITE
            .getColor();

    /** Constant for the normal foreground color. */
    private static final Color NORMAL_FG = ColorHelper.NamedColor.BLACK
            .getColor();

    /** Constant for the error background color. */
    private static final Color ERROR_BG = ColorHelper.NamedColor.RED.getColor();

    /** Constant for the error foreground color. */
    private static final Color ERROR_FG = ColorHelper.NamedColor.LIGHT_GRAY
            .getColor();

    /** Constant for the name of the test field. */
    private static final String FIELD = "testField";

    /** A mock widget handler. */
    private WidgetHandler mockWidgetHandler;

    /** The marker to be tested. */
    private ColorFieldMarkerTestImpl marker;

    @Before
    public void setUp() throws Exception
    {
        marker = new ColorFieldMarkerTestImpl();
    }

    /**
     * Creates a test controller instance.
     *
     * @return the controller
     */
    private FormControllerTestImpl setUpController()
    {
        FormControllerTestImpl ctrl = new FormControllerTestImpl();
        ctrl.setComponentBuilderData(new ComponentBuilderDataTestImpl());
        ctrl.setWindowBuilderData(new WindowBuilderData());
        return ctrl;
    }

    /**
     * Creates a mock for a widget handler. Optionally the mock can be prepared
     * to expect a query for its current colors.
     *
     * @param queryColors a flag if the colors should be queried
     * @return the initialized mock
     */
    private WidgetHandler initHandler(boolean queryColors)
    {
        WidgetHandler handler = EasyMock.createMock(WidgetHandler.class);
        if (queryColors)
        {
            EasyMock.expect(handler.getBackgroundColor()).andReturn(NORMAL_BG);
            EasyMock.expect(handler.getForegroundColor()).andReturn(NORMAL_FG);
        }
        return handler;
    }

    /**
     * Tests a newly created instance.
     */
    @Test
    public void testInit()
    {
        assertNull("Invalid foreground set", marker.getInvalidForeground());
        assertNull("Invalid background set", marker.getInvalidBackground());
        assertNull("Not visited foreground set", marker
                .getNotVisitedInvalidForeground());
        assertNull("Not visited background set", marker
                .getNotVisitedInvalidBackground());
        assertNull("Warning foreground set", marker.getWarningForeground());
        assertNull("Warning background set", marker.getWarningBackground());
        assertNull("Not visited warning foreground set", marker
                .getNotVisitedWarningForeground());
        assertNull("Not visited warning background set", marker
                .getNotVisitedWarningBackground());
    }

    /**
     * Tests whether the invalid foreground color can be set correctly.
     */
    @Test
    public void testSetInvalidForeground()
    {
        marker.setForegroundColor(FieldValidationStatus.INVALID, ERROR_FG);
        assertEquals("Wrong color (1)", ERROR_FG, marker.getInvalidForeground());
        marker.setInvalidForeground(NORMAL_FG);
        assertEquals("Wrong color (2)", NORMAL_FG, marker
                .getForegroundColor(FieldValidationStatus.INVALID));
    }

    /**
     * Tests whether the invalid background color can be set correctly.
     */
    @Test
    public void testSetInvalidBackground()
    {
        marker.setBackgroundColor(FieldValidationStatus.INVALID, ERROR_BG);
        assertEquals("Wrong color (1)", ERROR_BG, marker.getInvalidBackground());
        marker.setInvalidBackground(NORMAL_BG);
        assertEquals("Wrong color (2)", NORMAL_BG, marker
                .getBackgroundColor(FieldValidationStatus.INVALID));
    }

    /**
     * Tests whether the not visited-invalid background color can be set
     * correctly.
     */
    @Test
    public void testSetNotVisitedInvalidBackground()
    {
        marker.setBackgroundColor(FieldValidationStatus.NOT_VISITED_INVALID,
                ERROR_BG);
        assertEquals("Wrong color (1)", ERROR_BG, marker
                .getNotVisitedInvalidBackground());
        marker.setNotVisitedInvalidBackground(NORMAL_BG);
        assertEquals("Wrong color (2)", NORMAL_BG, marker
                .getBackgroundColor(FieldValidationStatus.NOT_VISITED_INVALID));
    }

    /**
     * Tests whether the not visited-invalid foreground color can be set
     * correctly.
     */
    @Test
    public void testSetNotVisitedInvalidForeground()
    {
        marker.setForegroundColor(FieldValidationStatus.NOT_VISITED_INVALID,
                ERROR_FG);
        assertEquals("Wrong color (1)", ERROR_FG, marker
                .getNotVisitedInvalidForeground());
        marker.setNotVisitedInvalidForeground(NORMAL_FG);
        assertEquals("Wrong color (2)", NORMAL_FG, marker
                .getForegroundColor(FieldValidationStatus.NOT_VISITED_INVALID));
    }

    /**
     * Tests whether the warning foreground color can be set correctly.
     */
    @Test
    public void testSetWarningForeground()
    {
        marker.setForegroundColor(FieldValidationStatus.WARNING, ERROR_FG);
        assertEquals("Wrong color (1)", ERROR_FG, marker.getWarningForeground());
        marker.setWarningForeground(NORMAL_FG);
        assertEquals("Wrong color (2)", NORMAL_FG, marker
                .getForegroundColor(FieldValidationStatus.WARNING));
    }

    /**
     * Tests whether the warning background color can be set correctly.
     */
    @Test
    public void testSetWarningBackground()
    {
        marker.setBackgroundColor(FieldValidationStatus.WARNING, ERROR_BG);
        assertEquals("Wrong color (1)", ERROR_BG, marker.getWarningBackground());
        marker.setWarningBackground(NORMAL_BG);
        assertEquals("Wrong color (2)", NORMAL_BG, marker
                .getBackgroundColor(FieldValidationStatus.WARNING));
    }

    /**
     * Tests whether the not visited-warning background color can be set
     * correctly.
     */
    @Test
    public void testSetNotVisitedWarningBackground()
    {
        marker.setBackgroundColor(FieldValidationStatus.NOT_VISITED_WARNING,
                ERROR_BG);
        assertEquals("Wrong color (1)", ERROR_BG, marker
                .getNotVisitedWarningBackground());
        marker.setNotVisitedWarningBackground(NORMAL_BG);
        assertEquals("Wrong color (2)", NORMAL_BG, marker
                .getBackgroundColor(FieldValidationStatus.NOT_VISITED_WARNING));
    }

    /**
     * Tests whether the not visited-warning foreground color can be set
     * correctly.
     */
    @Test
    public void testSetNotVisitedWarningForeground()
    {
        marker.setForegroundColor(FieldValidationStatus.NOT_VISITED_WARNING,
                ERROR_FG);
        assertEquals("Wrong color (1)", ERROR_FG, marker
                .getNotVisitedWarningForeground());
        marker.setNotVisitedWarningForeground(NORMAL_FG);
        assertEquals("Wrong color (2)", NORMAL_FG, marker
                .getForegroundColor(FieldValidationStatus.NOT_VISITED_WARNING));
    }

    /**
     * Tests whether the validation status of a field can be changed.
     */
    @Test
    public void testSetLastValidationStatus()
    {
        marker.setLastValidationStatus(FIELD,
                FieldValidationStatus.NOT_VISITED_WARNING);
        assertEquals("Status not changed",
                FieldValidationStatus.NOT_VISITED_WARNING, marker
                        .getLastValidationStatus(FIELD));
    }

    /**
     * Tests whether the correct status for an unknown field is returned.
     */
    @Test
    public void testGetLastValidationStatusUnknown()
    {
        assertEquals("Wrong status", FieldValidationStatus.NOT_VISITED_VALID,
                marker.getLastValidationStatus(FIELD));
    }

    /**
     * Tests whether the default colors of a widget can be queried.
     */
    @Test
    public void testGetWidgetColor()
    {
        mockWidgetHandler = initHandler(true);
        EasyMock.replay(mockWidgetHandler);
        FormControllerTestImpl ctrl = setUpController();
        assertEquals("Wrong widget background color", NORMAL_BG, marker
                .getWidgetBackgroundColor(ctrl, FIELD));
        assertEquals("Wrong widget foreground color", NORMAL_FG, marker
                .getWidgetForegroundColor(ctrl, FIELD));
        EasyMock.verify(mockWidgetHandler);
    }

    /**
     * Tests whether the default colors of a widget are cached after they are
     * fetched.
     */
    @Test
    public void testGetWidgetColorCached()
    {
        mockWidgetHandler = initHandler(true);
        EasyMock.replay(mockWidgetHandler);
        FormControllerTestImpl ctrl = setUpController();
        int testCount = 12;
        for (int i = 0; i < testCount; i++)
        {
            assertEquals("Wrong widget background color at " + i, NORMAL_BG,
                    marker.getWidgetBackgroundColor(ctrl, FIELD));
            assertEquals("Wrong widget foreground color at " + i, NORMAL_FG,
                    marker.getWidgetForegroundColor(ctrl, FIELD));
        }
        EasyMock.verify(mockWidgetHandler);
    }

    /**
     * Tries to obtain the color from an unknown widget. This should cause an
     * exception.
     */
    @Test(expected = FormBuilderRuntimeException.class)
    public void testGetWidgetColorNotFound()
    {
        FormController ctrl = setUpController();
        ComponentBuilderData compData = new ComponentBuilderData();
        compData.initializeForm(new TransformerContextImpl(),
                new BeanBindingStrategy());
        ctrl.setComponentBuilderData(compData);
        marker.getWidgetBackgroundColor(ctrl, FIELD);
    }

    /**
     * Tests updateField() if the field status has not changed.
     */
    @Test
    public void testUpdateFieldNoChange()
    {
        mockWidgetHandler = initHandler(false);
        EasyMock.replay(mockWidgetHandler);
        FormValidatorResults vres = new DefaultFormValidatorResults(
                new HashMap<String, ValidationResult>());
        marker.updateField(setUpController(), FIELD, vres, false);
        EasyMock.verify(mockWidgetHandler);
    }

    /**
     * Tests updateField() if new colors must be set.
     */
    @Test
    public void testUpdateFieldChangeColors()
    {
        mockWidgetHandler = initHandler(true);
        ValidationResult vres = EasyMock.createMock(ValidationResult.class);
        EasyMock.expect(vres.isValid()).andReturn(Boolean.FALSE).anyTimes();
        mockWidgetHandler.setBackgroundColor(ERROR_BG);
        mockWidgetHandler.setForegroundColor(ERROR_FG);
        EasyMock.replay(mockWidgetHandler, vres);
        marker.setInvalidBackground(ERROR_BG);
        marker.setInvalidForeground(ERROR_FG);
        Map<String, ValidationResult> map = new HashMap<String, ValidationResult>();
        map.put(FIELD, vres);
        FormValidatorResults validationResults = new DefaultFormValidatorResults(
                map);
        FormControllerTestImpl ctrl = setUpController();
        marker.updateField(ctrl, FIELD, validationResults, true);
        EasyMock.verify(mockWidgetHandler, vres);
    }

    /**
     * Tests updateFields() if the field status changes, but no colors are
     * defined for the new status. In this case the widget's default colors must
     * be set.
     */
    @Test
    public void testUpdateFieldChangeNoColors()
    {
        mockWidgetHandler = initHandler(true);
        ValidationResult vres = EasyMock.createMock(ValidationResult.class);
        EasyMock.expect(vres.isValid()).andReturn(Boolean.FALSE).anyTimes();
        mockWidgetHandler.setBackgroundColor(NORMAL_BG);
        mockWidgetHandler.setForegroundColor(NORMAL_FG);
        EasyMock.replay(mockWidgetHandler, vres);
        Map<String, ValidationResult> map = new HashMap<String, ValidationResult>();
        map.put(FIELD, vres);
        FormValidatorResults validationResults = new DefaultFormValidatorResults(
                map);
        FormControllerTestImpl ctrl = setUpController();
        marker.updateField(ctrl, FIELD, validationResults, false);
        EasyMock.verify(mockWidgetHandler, vres);
    }

    /**
     * Tests whether a validation event is correctly processed.
     */
    @Test
    public void testValidationPerformed()
    {
        int fieldCount = 16;
        Map<String, ValidationResult> resmap = new HashMap<String, ValidationResult>();
        for (int i = 0; i < fieldCount; i++)
        {
            resmap.put(FIELD + i, EasyMock
                    .createNiceMock(ValidationResult.class));
        }
        FormValidatorResults validationRes = new DefaultFormValidatorResults(
                resmap);
        FormControllerTestImpl ctrl = setUpController();
        marker.mockUpdateField = true;
        FormControllerValidationEvent event = new FormControllerValidationEvent(
                ctrl, validationRes);
        marker.validationPerformed(event);
        assertEquals("Wrong results", validationRes,
                marker.updateValidationResults);
        assertEquals("Wrong number of update fields", fieldCount,
                marker.updateFields.size());
        for (int i = 0; i < fieldCount; i++)
        {
            String fld = FIELD + i;
            assertTrue("Field not found: " + fld, marker.updateFields
                    .contains(fld));
        }
    }

    /**
     * Tests whether a field status changed event is correctly processed.
     */
    @Test
    public void testFieldStatusChanged()
    {
        Map<String, ValidationResult> resmap = new HashMap<String, ValidationResult>();
        resmap.put(FIELD, EasyMock.createNiceMock(ValidationResult.class));
        FormValidatorResults validationRes = new DefaultFormValidatorResults(
                resmap);
        FormControllerTestImpl ctrl = setUpController();
        ctrl.validatorResults = validationRes;
        marker.mockUpdateField = true;
        FormControllerFieldStatusEvent event = new FormControllerFieldStatusEvent(
                ctrl, FIELD);
        marker.fieldStatusChanged(event);
        assertEquals("Wrong results", validationRes,
                marker.updateValidationResults);
        assertEquals("Wrong number of update fields", 1, marker.updateFields
                .size());
        assertTrue("Wrong field", marker.updateFields.contains(FIELD));
    }

    /**
     * A specialized ComponentBuilderData implementation used for injecting
     * special widget handler mocks.
     */
    private class ComponentBuilderDataTestImpl extends ComponentBuilderData
    {
        /**
         * Always returns the mock widget handler. Checks the field name.
         */
        @Override
        public WidgetHandler getWidgetHandler(String name)
        {
            assertEquals("Wrong field name", FIELD, name);
            return mockWidgetHandler;
        }
    }

    /**
     * A specialized form controller implementation used for injecting
     * validation results.
     */
    private static class FormControllerTestImpl extends FormController
    {
        /** The validation results to be returned. */
        FormValidatorResults validatorResults;

        /**
         * Returns the mock validator results.
         */
        @Override
        public FormValidatorResults getLastValidationResults()
        {
            return validatorResults;
        }
    }

    /**
     * A test implementation of ColorFieldMarker.
     */
    private static class ColorFieldMarkerTestImpl extends ColorFieldMarker
    {
        /** A set for recording the field names passed to updateField(). */
        final Set<String> updateFields = new HashSet<String>();

        /** The validation results passed to updateField(). */
        FormValidatorResults updateValidationResults;

        /** A flag whether updateField() should be mocked. */
        boolean mockUpdateField;

        /**
         * Records this invocation. Optionally mocks this call.
         */
        @Override
        protected void updateField(FormController controller, String field,
                FormValidatorResults validationResults, boolean visited)
        {
            updateFields.add(field);
            updateValidationResults = validationResults;
            if (!mockUpdateField)
            {
                super
                        .updateField(controller, field, validationResults,
                                visited);
            }
        }
    }
}
