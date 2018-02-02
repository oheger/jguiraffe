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
package net.sf.jguiraffe.gui.builder.components.model;

import net.sf.jguiraffe.gui.forms.ComponentHandler;

/**
 * <p>
 * A specialized component handler that represents a progress bar component.
 * </p>
 * <p>
 * The most important property of a progress bar is its current value. This is
 * an integer value indicating the progress of the operation that is visualized
 * by the bar. It is also possible to set a text value, which will be displayed
 * in front of the progress bar (as far as the used GUI library supports this
 * feature). A text will only be displayed if support for texts was enabled when
 * the progress bar component was created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ProgressBarHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ProgressBarHandler extends ComponentHandler<Integer>
{
    /**
     * Returns the current value of the progress bar.
     *
     * @return the current value
     */
    int getValue();

    /**
     * Sets the current value of the progress bar. This value will determine the
     * length of the bar. It should be between the minimum and the maximum value
     * defined during creation of the component.
     *
     * @param v the current value
     */
    void setValue(int v);

    /**
     * Returns the text of the progress bar.
     *
     * @return the current progress text
     */
    String getProgressText();

    /**
     * Sets the text of the progress bar. This text will be displayed in front
     * of the progress bar if this allowed.
     *
     * @param s the progress text
     */
    void setProgressText(String s);
}
