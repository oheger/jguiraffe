/*
 * Copyright 2006-2012 The JGUIraffe Team.
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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.FormBuilderRuntimeException;
import net.sf.jguiraffe.gui.builder.components.WidgetHandler;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;

/**
 * <p>
 * A specialized {@link FormControllerValidationListener} implementation that
 * can change the colors of fields depending on their validation status.
 * </p>
 * <p>
 * An instance of this class can be registered at a {@link FormController} as
 * {@link FormControllerValidationListener} and
 * {@link FormControllerFieldStatusListener}. Whenever an event is received from
 * the {@link FormController} the current status of the affected field(s) is
 * checked. The instance can be configured with multiple colors corresponding to
 * the possible field states:
 * <ul>
 * <li>not visited, invalid</li>
 * <li>visited, invalid</li>
 * <li>not visited, validation warning</li>
 * <li>visited, validation warning</li>
 * </ul>
 * The foreground and background colors of the field(s) are set to the
 * corresponding colors.
 * </p>
 * <p>
 * Valid fields do not change their colors. If colors for a certain field status
 * are undefined, the default colors are used, too. For instance, if fields for
 * which a validation warning exists should not be highlighted in a special way,
 * just leave the properties for these colors undefined. This class implements a
 * very simple means for marking invalid input fields. If the background color
 * of a text field is changed to, say, red, it will be pretty obvious that
 * something is wrong with this field.
 * </p>
 * <p>
 * Instances of this class can be associated with a single form controller only
 * and should not be reused (because they keep an internal state about the input
 * fields involved). If they are declared in a bean definition file of the
 * dependency injection framework, the {@code singleton} attribute should be set
 * to <b>false</b>.
 *
 * @author Oliver Heger
 * @version $Id: ColorFieldMarker.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ColorFieldMarker implements FormControllerValidationListener,
        FormControllerFieldStatusListener
{
    /** Constant for the index of the background color. */
    private static final int IDX_BG = 0;

    /** Constant for the index of the foreground color. */
    private static final int IDX_FG = 1;

    /** A map that records the original colors of input fields. */
    private final Map<String, Color[]> orgColors;

    /** A map for storing the various colors. */
    private final Map<FieldValidationStatus, Color[]> markColors;

    /** A map for the validation status values of known fields. */
    private final Map<String, FieldValidationStatus> validationStates;

    /**
     * Creates a new instance of <code>ColorFieldMarker</code>.
     */
    public ColorFieldMarker()
    {
        orgColors = new HashMap<String, Color[]>();
        markColors = new EnumMap<FieldValidationStatus, Color[]>(
                FieldValidationStatus.class);
        validationStates = new HashMap<String, FieldValidationStatus>();
    }

    /**
     * Returns the background color to be set for invalid input fields.
     *
     * @return the background color for invalid input fields
     */
    public Color getInvalidBackground()
    {
        return getBackgroundColor(FieldValidationStatus.INVALID);
    }

    /**
     * Sets the background color for invalid input fields. If an input field
     * changes to the state <em>invalid</em>, it is assigned this background
     * color. (If this property is not set, the background color of invalid
     * input fields won't be changed.)
     *
     * @param invalidBackground the background color for invalid input fields
     */
    public void setInvalidBackground(Color invalidBackground)
    {
        setBackgroundColor(FieldValidationStatus.INVALID, invalidBackground);
    }

    /**
     * Returns the foreground color to be set for invalid input fields.
     *
     * @return the foreground color for invalid input fields
     */
    public Color getInvalidForeground()
    {
        return getForegroundColor(FieldValidationStatus.INVALID);
    }

    /**
     * Sets the foreground color for invalid input fields. If an input field
     * changes to the state <em>invalid</em>, it is assigned this foreground
     * color. (If this property is not set, the foreground color of invalid
     * input fields won't be changed.)
     *
     * @param invalidForeground the foreground color for invalid input fields
     */
    public void setInvalidForeground(Color invalidForeground)
    {
        setForegroundColor(FieldValidationStatus.INVALID, invalidForeground);
    }

    /**
     * Returns the background color to be set for input fields in the state
     * <em>not visited, invalid</em>.
     *
     * @return the background color for not visited invalid input fields
     */
    public Color getNotVisitedInvalidBackground()
    {
        return getBackgroundColor(FieldValidationStatus.NOT_VISITED_INVALID);
    }

    /**
     * Sets the background color for input fields in the state <em>not visited,
     * invalid</em>. If an input field changes to this state, it is assigned
     * this background color. (If this property is not set, the background color
     * of not visited, invalid input fields won't be changed.)
     *
     * @param c the background color for not visited invalid input fields
     */
    public void setNotVisitedInvalidBackground(Color c)
    {
        setBackgroundColor(FieldValidationStatus.NOT_VISITED_INVALID, c);
    }

    /**
     * Returns the foreground color to be set for input fields in the state
     * <em>not visited, invalid</em>.
     *
     * @return the foreground color for not visited invalid input fields
     */
    public Color getNotVisitedInvalidForeground()
    {
        return getForegroundColor(FieldValidationStatus.NOT_VISITED_INVALID);
    }

    /**
     * Sets the foreground color for input fields in the state <em>not visited,
     * invalid</em>. If an input field changes to this state, it is assigned
     * this foreground color. (If this property is not set, the foreground color
     * of not visited, invalid input fields won't be changed.)
     *
     * @param c the foreground color for not visited invalid input fields
     */
    public void setNotVisitedInvalidForeground(Color c)
    {
        setForegroundColor(FieldValidationStatus.NOT_VISITED_INVALID, c);
    }

    /**
     * Returns the foreground color for input fields in the state
     * <em>warning</em>.
     *
     * @return the foreground color for input fields with a validation warning
     */
    public Color getWarningForeground()
    {
        return getForegroundColor(FieldValidationStatus.WARNING);
    }

    /**
     * Sets the foreground color for input fields in the state <em>warning</em>.
     * If an input field changes to this state, it is assigned this foreground
     * color. (If this property is not set, the foreground color for fields with
     * a validation warning won't be changed.)
     *
     * @param c the foreground color for fields with a validation warning
     */
    public void setWarningForeground(Color c)
    {
        setForegroundColor(FieldValidationStatus.WARNING, c);
    }

    /**
     * Returns the background color for input fields in the state
     * <em>warning</em>.
     *
     * @return the background color for input fields with a validation warning
     */
    public Color getWarningBackground()
    {
        return getBackgroundColor(FieldValidationStatus.WARNING);
    }

    /**
     * Sets the background color for input fields in the state <em>warning</em>.
     * If an input field changes to this state, it is assigned this background
     * color. (If this property is not set, the background color for fields with
     * a validation warning won't be changed.)
     *
     * @param c the background color for fields with a validation warning
     */
    public void setWarningBackground(Color c)
    {
        setBackgroundColor(FieldValidationStatus.WARNING, c);
    }

    /**
     * Returns the foreground color for input fields in the state
     * <em>not visited,
     * warning</em>.
     *
     * @return the foreground color for fields with a validation warning that
     *         have not yet been visited
     */
    public Color getNotVisitedWarningForeground()
    {
        return getForegroundColor(FieldValidationStatus.NOT_VISITED_WARNING);
    }

    /**
     * Sets the foreground color for input fields in the state <em>not visited,
     * warning</em>. If an input field changes to this state, it is assigned
     * this foreground color. (If this property is not set, the foreground color
     * for fields with a validation warning won't be changed.)
     *
     * @param c the foreground color for fields with a validation warning that
     *        have not yet been visited
     */
    public void setNotVisitedWarningForeground(Color c)
    {
        setForegroundColor(FieldValidationStatus.NOT_VISITED_WARNING, c);
    }

    /**
     * Returns the background color for input fields in the state
     * <em>not visited,
     * warning</em>.
     *
     * @return the background color for fields with a validation warning that
     *         have not yet been visited
     */
    public Color getNotVisitedWarningBackground()
    {
        return getBackgroundColor(FieldValidationStatus.NOT_VISITED_WARNING);
    }

    /**
     * Sets the background color for input fields in the state <em>not visited,
     * warning</em>. If an input field changes to this state, it is assigned
     * this background color. (If this property is not set, the background color
     * for fields with a validation warning won't be changed.)
     *
     * @param c the background color for fields with a validation warning that
     *        have not yet been visited
     */
    public void setNotVisitedWarningBackground(Color c)
    {
        setBackgroundColor(FieldValidationStatus.NOT_VISITED_WARNING, c);
    }

    /**
     * Returns the foreground color for fields with the specified {@code
     * FieldValidationStatus}.
     *
     * @param status the {@code FieldValidationStatus}
     * @return the corresponding foreground color (<b>null</b> if undefined)
     */
    public Color getForegroundColor(FieldValidationStatus status)
    {
        return getColor(status, IDX_FG);
    }

    /**
     * Sets the foreground color for fields with the specified {@code
     * FieldValidationStatus}. This is a generic method for setting foreground
     * colors. Internally, the other set methods for foreground colors delegate
     * to this method.
     *
     * @param status the {@code FieldValidationStatus} (must not be <b>null</b>)
     * @param c the new foreground color
     * @throws IllegalArgumentException if the {@code FieldValidationStatus} is
     *         <b>null</b>
     */
    public void setForegroundColor(FieldValidationStatus status, Color c)
    {
        setColor(status, IDX_FG, c);
    }

    /**
     * Returns the background color for fields with the specified {@code
     * FieldValidationStatus}.
     *
     * @param status the {@code FieldValidationStatus}
     * @return the corresponding background color (<b>null</b> if undefined)
     */
    public Color getBackgroundColor(FieldValidationStatus status)
    {
        return getColor(status, IDX_BG);
    }

    /**
     * Sets the background color for fields with the specified {@code
     * FieldValidationStatus}. This is a generic method for setting background
     * colors. Internally, the other set methods for background colors delegate
     * to this method.
     *
     * @param status the {@code FieldValidationStatus} (must not be <b>null</b>)
     * @param c the new background color
     * @throws IllegalArgumentException if the {@code ValidationMessageLevel} is
     *         <b>null</b>
     */
    public void setBackgroundColor(FieldValidationStatus status, Color c)
    {
        setColor(status, IDX_BG, c);
    }

    /**
     * A validation operation was performed by the {@code FormController}. This
     * implementation checks which fields need to be updated and sets the
     * corresponding new colors.
     *
     * @param event the validation event
     */
    public void validationPerformed(FormControllerValidationEvent event)
    {
        for (String field : event.getValidationResults().getFieldNames())
        {
            updateField(event.getFormController(), field, event
                    .getValidationResults(), event.getFormController()
                    .isFieldVisited(field));
        }
    }

    /**
     * The visited status of a field managed by the {@code FormController} has
     * changed. This implementation checks whether new colors need to be set for
     * this field.
     *
     * @param event the {@code FormControllerFieldStatusEvent}
     */
    public void fieldStatusChanged(FormControllerFieldStatusEvent event)
    {
        updateField(event.getFormController(), event.getFieldName(), event
                .getFormController().getLastValidationResults(), event
                .getFormController().isFieldVisited(event.getFieldName()));
    }

    /**
     * Returns the last known validation status of the specified field. This
     * method is used to find out whether the status of a field has changed. If
     * no status is known, the field is considered valid and not visited.
     *
     * @param field the name of the field in question
     * @return the last {@code FieldValidationStatus} of this field
     */
    protected FieldValidationStatus getLastValidationStatus(String field)
    {
        FieldValidationStatus res = validationStates.get(field);
        return (res != null) ? res : FieldValidationStatus.NOT_VISITED_VALID;
    }

    /**
     * Sets the last {@code FieldValidationStatus} for the specified field. With
     * this method the status of a field can be updated.
     *
     * @param field the name of the field in question
     * @param status the new {@code FieldValidationStatus} for this field
     */
    protected void setLastValidationStatus(String field,
            FieldValidationStatus status)
    {
        validationStates.put(field, status);
    }

    /**
     * Returns the default foreground color of the specified widget. This color
     * is set if no specific color is defined for the validation status this
     * field is in. It is obtained once on first access and then cached.
     *
     * @param controller the {@code FormController}
     * @param field the name of the field
     * @return the default foreground color of this field
     */
    protected Color getWidgetForegroundColor(FormController controller,
            String field)
    {
        return getWidgetColor(controller, field, IDX_FG);
    }

    /**
     * Returns the default background color of the specified widget. This color
     * is set if no specific color is defined for the validation status this
     * field is in. It is obtained once on first access and then cached.
     *
     * @param controller the {@code FormController}
     * @param field the name of the field
     * @return the default background color of this field
     */
    protected Color getWidgetBackgroundColor(FormController controller,
            String field)
    {
        return getWidgetColor(controller, field, IDX_BG);
    }

    /**
     * Updates the colors for the given field. This method is called whenever an
     * event is received from the {@code FormController} that indicates a status
     * change of a field. This implementation checks whether the status of the
     * given field has actually changed. If this is the case, the colors for the
     * new status are obtained and set.
     *
     * @param controller the {@code FormController}
     * @param field the name of the field affected by the change
     * @param validationResults the current {@code FormValidatorResults} object
     * @param visited the visited state of the current field
     * @throws FormBuilderRuntimeException if the field name cannot be resolved
     */
    protected void updateField(FormController controller, String field,
            FormValidatorResults validationResults, boolean visited)
    {
        FieldValidationStatus status = FieldValidationStatus.getStatus(
                validationResults.getResultsFor(field), visited);
        if (status != getLastValidationStatus(field))
        {
            // status has changed
            setLastValidationStatus(field, status);

            Color orgFg = getWidgetForegroundColor(controller, field);
            Color orgBg = getWidgetBackgroundColor(controller, field);
            Color newFg = getForegroundColor(status);
            Color newBg = getBackgroundColor(status);

            if (newFg == null)
            {
                newFg = orgFg;
            }
            if (newBg == null)
            {
                newBg = orgBg;
            }

            WidgetHandler wh = controller.getComponentBuilderData()
                    .getWidgetHandler(field);
            wh.setForegroundColor(newFg);
            wh.setBackgroundColor(newBg);
        }
    }

    /**
     * Helper method for obtaining one of the colors for marking input fields.
     *
     * @param status the {@code FieldValidationStatus}
     * @param idx the index of the color (background or foreground)
     * @return the corresponding color or <b>null</b> if undefined
     */
    private Color getColor(FieldValidationStatus status, int idx)
    {
        Color[] col = markColors.get(status);
        return (col != null) ? col[idx] : null;
    }

    /**
     * Helper method for setting one of the colors for marking input fields.
     *
     * @param status the {@code FieldValidationStatus}
     * @param idx the index of the color (background or foreground)
     * @param col the new color
     */
    private void setColor(FieldValidationStatus status, int idx, Color col)
    {
        Color[] cols = markColors.get(status);
        if (cols == null)
        {
            cols = new Color[2];
            markColors.put(status, cols);
        }

        cols[idx] = col;
    }

    /**
     * Helper method for obtaining the color of a widget. The original widget
     * colors are retrieved once and then cached.
     *
     * @param controller the {@code FormController}
     * @param field the name of the field in question
     * @param idx the index of the color (foreground or background)
     * @return the corresponding color
     * @throws FormBuilderRuntimeException if the widget cannot be resolved
     */
    private Color getWidgetColor(FormController controller, String field,
            int idx)
    {
        Color[] widgetColors = orgColors.get(field);

        if (widgetColors == null)
        {
            WidgetHandler wh = controller.getComponentBuilderData()
                    .getWidgetHandler(field);
            if (wh == null)
            {
                throw new FormBuilderRuntimeException(
                        "Widget cannot be found: " + field);
            }

            widgetColors = new Color[2];
            widgetColors[IDX_BG] = wh.getBackgroundColor();
            widgetColors[IDX_FG] = wh.getForegroundColor();
            orgColors.put(field, widgetColors);
        }

        return widgetColors[idx];
    }
}
