/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;


/**
 * <p>
 * An exception class indicating a problem with the form action framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormActionException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormActionException extends FormBuilderException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -2259904885846361444L;

    /**
     * Creates a new instance of <code>FormActionException</code> and sets the
     * error message.
     *
     * @param msg the error message
     */
    public FormActionException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>FormActionException</code> and sets
     * both the error message and the root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public FormActionException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Creates a new instance of <code>FormActionException</code> and sets the
     * root cause.
     *
     * @param cause the root cause of this exception
     */
    public FormActionException(Throwable cause)
    {
        super(cause);
    }
}
