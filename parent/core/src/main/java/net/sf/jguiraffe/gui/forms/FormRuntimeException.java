/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms;

/**
 * <p>
 * An exception class for reporting runtime exceptions related to the form
 * framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormRuntimeException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormRuntimeException extends RuntimeException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -1843826607259553025L;

    /**
     * Creates a new instance of <code>FormRuntimeException</code>.
     */
    public FormRuntimeException()
    {
        super();
    }

    /**
     * Creates a new instance of <code>FormRuntimeException</code> and
     * initializes it with an error message.
     *
     * @param msg the error message
     */
    public FormRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>FormRuntimeException</code> and
     * initializes it with the root cause.
     *
     * @param cause the root cause
     */
    public FormRuntimeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a new instance of <code>FormRuntimeException</code> and
     * initializes it with an error message and the root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public FormRuntimeException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
