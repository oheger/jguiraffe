/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.sf.jguiraffe.gui.builder.components.model.TextHandler;

/**
 * <p>
 * A specific Swing component handler implementation that deals with text
 * components.
 * </p>
 * <p>
 * This component handler deals with single and multi line text fields. Data
 * type is String in both cases. Change events are mapped to edit events like
 * insert and remove. Action events are not supported.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTextHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTextHandler extends SwingComponentHandler<String> implements
        DocumentListener, TextHandler
{
    /**
     * Creates a new instance of <code>SwingTextHandler</code>.
     *
     * @param text the managed text component
     */
    public SwingTextHandler(JTextComponent text)
    {
        super(text);
    }

    /**
     * Returns the managed text component.
     *
     * @return the text component
     */
    public JTextComponent getTextComponent()
    {
        return (JTextComponent) getComponent();
    }

    /**
     * Returns the data of the managed text field. This is of type string.
     *
     * @return the text field's data
     */
    public String getData()
    {
        return getTextComponent().getText();
    }

    /**
     * Sets the text field's data.
     *
     * @param data the data, may be <b>null</b>
     */
    public void setData(String data)
    {
        getTextComponent().setText(data);
    }

    /**
     * Returns this handler's data type. This is String.
     *
     * @return the handler's data type
     */
    public Class<?> getType()
    {
        return String.class;
    }

    /**
     * Clears the selection. This implementation sets the selection end of the
     * wrapped text component to the same index as the selection start. This
     * means that there is no selection, but the caret position should have a
     * defined value.
     */
    public void clearSelection()
    {
        getTextComponent().setSelectionEnd(getSelectionStart());
    }

    /**
     * Performs a copy operation. This implementation just delegates to the
     * wrapped text component.
     */
    public void copy()
    {
        getTextComponent().copy();
    }

    /**
     * Performs a cut operation. This implementation just delegates to the
     * wrapped text component.
     */
    public void cut()
    {
        getTextComponent().cut();
    }

    /**
     * Returns the text which is currently selected. This implementation just
     * delegates to the wrapped text component.
     *
     * @return the selected text
     */
    public String getSelectedText()
    {
        return getTextComponent().getSelectedText();
    }

    /**
     * Returns the start index of the selection. This implementation just
     * delegates to the wrapped text component.
     *
     * @return the start index of the selection
     */
    public int getSelectionEnd()
    {
        return getTextComponent().getSelectionEnd();
    }

    /**
     * Returns the end index of the selection. This implementation just
     * delegates to the wrapped text component.
     *
     * @return the end index of the selection
     */
    public int getSelectionStart()
    {
        return getTextComponent().getSelectionStart();
    }

    /**
     * Tests whether a selection exists. This implementation checks the
     * selection start and the selection end from the wrapped text component.
     *
     * @return a flag whether currently text is selected
     */
    public boolean hasSelection()
    {
        return getTextComponent().getSelectionStart() < getTextComponent()
                .getSelectionEnd();
    }

    /**
     * Performs a paste operation. This implementation just delegates to the
     * wrapped text component.
     */
    public void paste()
    {
        getTextComponent().paste();
    }

    /**
     * Replaces the selected text. This implementation just delegates to the
     * wrapped text component.
     *
     * @param text the replacement text
     */
    public void replaceSelectedText(String text)
    {
        getTextComponent().replaceSelection(text);
    }

    /**
     * Selects a range of text. This implementation just delegates to the
     * wrapped text component. It is possible to set the new indices of the
     * selection to invalid values; the text component will trim them correctly.
     *
     * @param start the start index of the new selection
     * @param end the end index of the new selection
     */
    public void select(int start, int end)
    {
        getTextComponent().select(start, end);
    }

    /**
     * Selects the whole text. This implementation just delegates to the wrapped
     * text component.
     */
    public void selectAll()
    {
        getTextComponent().selectAll();
    }

    /**
     * Event listener callback for text change events.
     *
     * @param event the event
     */
    public void changedUpdate(DocumentEvent event)
    {
        fireChangeEvent(event);
    }

    /**
     * Event listener callback for text insert events.
     *
     * @param event the event
     */
    public void insertUpdate(DocumentEvent event)
    {
        fireChangeEvent(event);
    }

    /**
     * Event listener callback for text remove events.
     *
     * @param event the event
     */
    public void removeUpdate(DocumentEvent event)
    {
        fireChangeEvent(event);
    }

    /**
     * Registers this handler as change listener at the managed text component.
     * Incoming text update notifications will then be broadcasted as change
     * events.
     */
    @Override
    protected void registerChangeListener()
    {
        getTextComponent().getDocument().addDocumentListener(this);
    }

    /**
     * Unregisters this handler as change listener at the managed text
     * component.
     */
    @Override
    protected void unregisterChangeListener()
    {
        getTextComponent().getDocument().removeDocumentListener(this);
    }
}
