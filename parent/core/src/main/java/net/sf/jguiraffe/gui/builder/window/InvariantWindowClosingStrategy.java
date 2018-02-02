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
package net.sf.jguiraffe.gui.builder.window;

/**
 * <p>
 * A very simple implementation of the <code>WindowClosingStrategy</code>
 * interface.
 * </p>
 * <p>
 * The {@link #canClose(Window)} method of this implementation returns always
 * <b>true</b>. So an instance of this class can be used as a default window
 * closing strategy that is used when no specific strategy was specified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvariantWindowClosingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InvariantWindowClosingStrategy implements WindowClosingStrategy
{
    /**
     * Stores the default instance of this class. Because this implementation is
     * thread safe, it can be shared.
     */
    public static final InvariantWindowClosingStrategy DEFAULT_INSTANCE =
            new InvariantWindowClosingStrategy();

    /**
     * Checks if the window can be closed. This implementation returns always
     * <b>true</b>.
     *
     * @param window the affected window
     * @return a flag if the window can be closed
     */
    public boolean canClose(Window window)
    {
        return true;
    }
}
