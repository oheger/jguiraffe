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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * <p>
 * An internally used document class for text components with a limited text
 * length.
 * </p>
 * <p>
 * This document class is initialized with a length parameter. It ensures that
 * only text with this maximum length can be typed in the corresponding text
 * component.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingLimitedTextModel.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingLimitedTextModel extends PlainDocument
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /** Stores the maximum text length. */
    private int maximumLength;

    /**
     * Creates a new instance of <code>SwingLimitedTextModel</code> and
     * initializes it with the maximum allowed text length.
     *
     * @param maxlen the maximum text length
     */
    public SwingLimitedTextModel(int maxlen)
    {
        super();
        maximumLength = maxlen;
    }

    /**
     * Returns the maximum text length.
     *
     * @return the maximum text length
     */
    public int getMaximumLength()
    {
        return maximumLength;
    }

    /**
     * Inserts text into this document. This implementation ensures that the
     * maximum text length is taken into account.
     *
     * @param offset the insert position
     * @param str the string to insert
     * @param a attributes
     * @throws BadLocationException if the position is invalid
     */
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException
    {
        if (getLength() + str.length() <= getMaximumLength())
        {
            super.insertString(offset, str, a);
        } /* if */
    }
}
