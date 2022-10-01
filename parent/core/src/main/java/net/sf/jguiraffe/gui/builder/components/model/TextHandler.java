/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specialized {@code ComponentHandler} implementation for text components.
 * </p>
 * <p>
 * This handler class provides some extended functionality specific for text
 * input components. Examples of this functionality include methods for querying
 * or manipulating the text field's selection or support for clipboard
 * operations. Using this handler interface applications gain more control of
 * text components.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface TextHandler extends ComponentHandler<String>
{
    /**
     * Returns a flag whether the text component has a non-empty selection. If
     * this method returns <b>true</b>, other methods can be called that operate
     * on the selection.
     *
     * @return a flag whether a selection exists
     */
    boolean hasSelection();

    /**
     * Returns the start index of the current selection in the text field. The
     * value returned by this method is only meaningful if
     * {@link #hasSelection()} returns <b>true</b>.
     *
     * @return the start index of the selection
     */
    int getSelectionStart();

    /**
     * Returns the end index of the current selection in the text field. The
     * value returned by this method is only meaningful if
     * {@link #hasSelection()} returns <b>true</b>.
     *
     * @return the end index of the selection
     */
    int getSelectionEnd();

    /**
     * Sets the selection of the text field. The parameters passed to this
     * method are the start index and the end index of the selection. Indices
     * are 0-based and are related to the characters stored in the text field.
     *
     * @param start the start index of the selection
     * @param end the end index of the selection
     */
    void select(int start, int end);

    /**
     * Selects all text contained in the text component.
     */
    void selectAll();

    /**
     * Clears the selection of the text component. This does not affect the text
     * stored in the component. Only the selection is reset.
     */
    void clearSelection();

    /**
     * Returns the currently selected text. The result of this method is only
     * meaningful if a selection exists, i.e. if {@link #hasSelection()} returns
     * <b>true</b>.
     *
     * @return the selected text
     */
    String getSelectedText();

    /**
     * Replaces the text currently selected by the specified string. If no
     * selection exists, the specified text is inserted at the current caret
     * position. If there is a selection, but the replacement text is
     * <b>null</b> or empty, the selected text is removed.
     *
     * @param text the replacement text
     */
    void replaceSelectedText(String text);

    /**
     * Copies the currently selected text to the system clipboard. This is the
     * standard edit copy action.
     */
    void copy();

    /**
     * Cuts the currently selected text and copies it into the system clipboard.
     * This is the standard edit cut action.
     */
    void cut();

    /**
     * Pastes the content of the system clipboard into this text component. This
     * is the standard edit paste action.
     */
    void paste();
}
