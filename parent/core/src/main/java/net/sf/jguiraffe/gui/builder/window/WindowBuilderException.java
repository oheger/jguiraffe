/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

/**
 * <p>
 * A specialized exception class used for reporting error conditions related to
 * the window builder.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: WindowBuilderException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowBuilderException extends FormBuilderException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 8471699744566726481L;

    /**
     * Creates a new instance of <code>WindowBuilderException</code> and sets
     * the error message.
     *
     * @param msg the error message
     */
    public WindowBuilderException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>WindowBuilderException</code> and sets
     * the root cause.
     *
     * @param cause the exception's root cause
     */
    public WindowBuilderException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a new instance of <code>WindowBuilderException</code> and sets
     * the error message and the root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public WindowBuilderException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
