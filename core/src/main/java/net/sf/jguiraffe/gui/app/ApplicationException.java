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
package net.sf.jguiraffe.gui.app;

/**
 * <p>
 * An exception class that indicates error conditions related to the
 * <code>Application</code> class.
 * </p>
 * <p>
 * The application framework contains a startup class for starting a Java GUI
 * application. Error conditions occurring during the startup phase will cause
 * exceptions of this type being thrown.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ApplicationException extends Exception
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 3940825554523387981L;

    /**
     * Creates a new instance of <code>ApplicationException</code> without
     * further information.
     */
    public ApplicationException()
    {
        super();
    }

    /**
     * Creates a new instance of <code>ApplicationException</code> and
     * initializes it with an error message.
     *
     * @param msg the error message
     */
    public ApplicationException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>ApplicationException</code> and
     * initializes it with an error message and a root cause.
     *
     * @param msg the error message
     * @param cause the root cause for this exception
     */
    public ApplicationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Creates a new instance of <code>ApplicationException</code> and
     * initializes it with the root cause of this exception.
     *
     * @param cause the root cause for this exception
     */
    public ApplicationException(Throwable cause)
    {
        super(cause);
    }
}
