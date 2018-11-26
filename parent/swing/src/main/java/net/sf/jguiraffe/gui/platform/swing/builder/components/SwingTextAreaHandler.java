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
package net.sf.jguiraffe.gui.platform.swing.builder.components;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>
 * A specific Swing component handler implementation that deals with text area
 * components.
 * </p>
 * <p>
 * This class inherits most of its functionality from its ancestor, the base
 * handler class for text components. The main difference to this class is the
 * fact that a text area is wrapped in a scroll pane.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: SwingTextAreaHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
class SwingTextAreaHandler extends SwingTextHandler
{
    /** Stores the scroll pane of the text area. */
    private final JScrollPane scrollPane;

    /**
     * Creates a new instance of {@code SwingTextAreaHandler} and initializes it
     * with the text area to manage.
     *
     * @param textArea the text area
     * @param scrollWidth the preferred scroll width
     * @param scrollHeight the preferred scroll height
     */
    public SwingTextAreaHandler(JTextArea textArea, int scrollWidth,
            int scrollHeight)
    {
        this(textArea, SwingComponentUtils.scrollPaneFor(textArea, scrollWidth,
                scrollHeight));
    }

    /**
     * Creates a new instance of {@code SwingTextAreaHandler} and sets the
     * scroll pane to be used. With this constructor the handler can be
     * initialized with a scroll pane created externally.
     *
     * @param textArea the text area
     * @param scr the scroll pane to be used
     * @since 1.4
     */
    public SwingTextAreaHandler(JTextArea textArea, JScrollPane scr)
    {
        super(textArea);
        scrollPane = scr;
    }

    /**
     * Returns the outer most component. This is the scroll pane.
     *
     * @return the outer component
     */
    @Override
    public Object getOuterComponent()
    {
        return scrollPane;
    }
}
