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
package net.sf.jguiraffe.gui.builder.components;

import org.apache.commons.lang.exception.NestableException;

/**
 * <p>
 * An exception class for reporting errors of the form builder framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormBuilderException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormBuilderException extends NestableException
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4113609511081254811L;

    /**
     * Creates a new instance of <code>FormBuilderException</code> and sets
     * the error message.
     *
     * @param msg the error message
     */
    public FormBuilderException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>FormBuilderException</code> and sets
     * the root cause of the exception.
     *
     * @param cause the root cause
     */
    public FormBuilderException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a new instance of <code>FormBuilderException</code> and sets
     * the error message and the root cause.
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public FormBuilderException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
