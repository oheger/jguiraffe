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
package net.sf.jguiraffe.locators;


/**
 * <p>
 * An exception class for reporting error conditions related to
 * <code>{@link Locator}</code> objects.
 * </p>
 * <p>
 * Exceptions of this type can be thrown by the methods of the
 * <code>{@link Locator}</code> interface if the resources they should
 * represent are invalid or cannot be found. Note that this is a runtime
 * exception because error conditions of this type are usually caused by
 * programming errors and cannot be recovered from.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: LocatorException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class LocatorException extends RuntimeException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -2259033559671621966L;

    /**
     * Creates a new instance of <code>LocatorException</code>.
     */
    public LocatorException()
    {
        super();
    }

    /**
     * Creates a new instance of <code>LocatorException</code> and sets an
     * error message.
     *
     * @param msg the error message
     */
    public LocatorException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>LocatorException</code> and sets an
     * error message and a root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public LocatorException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Creates a new instance of <code>LocatorException</code> and sets the
     * root cause.
     *
     * @param cause the root cause
     */
    public LocatorException(Throwable cause)
    {
        super(cause);
    }
}
