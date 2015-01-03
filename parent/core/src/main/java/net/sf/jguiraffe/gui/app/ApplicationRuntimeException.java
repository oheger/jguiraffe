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
package net.sf.jguiraffe.gui.app;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * <p>
 * An exception class for reporting runtime exceptions that are related to
 * <code>{@link Application}</code> objects or operations invoked on them.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ApplicationRuntimeException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ApplicationRuntimeException extends NestableRuntimeException
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -6002346874472164381L;

    /**
     * Creates a new instance of <code>ApplicationRuntimeException</code> and
     * sets the error message.
     *
     * @param msg the error message
     */
    public ApplicationRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>ApplicationRuntimeException</code> and
     * sets the error message and the root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public ApplicationRuntimeException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

    /**
     * Creates a new instance of <code>ApplicationRuntimeException</code> and
     * sets the root cause.
     *
     * @param cause the root cause
     */
    public ApplicationRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
