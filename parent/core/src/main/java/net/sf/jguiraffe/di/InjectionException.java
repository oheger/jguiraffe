/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.di;

/**
 * <p>
 * An exception class for reporting exceptions related to dependency injection.
 * </p>
 * <p>
 * When working with reflection naturally a whole bunch of exceptions can be
 * thrown. This library wraps these exceptions in a generic
 * <code>InjectionException</code>. Note that this is a runtime exception, so
 * there is no need for explicit catch blocks. (If dependency injection causes
 * exceptions, this is typically due to a wrong configuration; so there is
 * nothing the application itself can do about.)
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InjectionException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class InjectionException extends RuntimeException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -7010018659144863407L;

    /**
     * Creates a new instance of <code>InjectionException</code>
     */
    public InjectionException()
    {
        super();
    }

    /**
     * Creates a new instance of <code>InjectionException</code> and
     * initializes it with an error message.
     *
     * @param msg the error message
     */
    public InjectionException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>InjectionException</code> and
     * initializes it with a root cause.
     *
     * @param cause the root cause of this exception
     */
    public InjectionException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a new instance of <code>InjectionException</code> and
     * initializes it with both an error message and a root cause.
     *
     * @param msg the error message
     * @param cause the root cause of this exception
     */
    public InjectionException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
