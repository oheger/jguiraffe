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
package net.sf.jguiraffe.gui.builder.components;

/**
 * <p>
 * An exception class for reporting runtime errors of the form builder
 * framework.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormBuilderRuntimeException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormBuilderRuntimeException extends RuntimeException
{
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 4590445222867803920L;

    /**
     * Creates a new instance of <code>FormBuilderRuntimeException</code> and
     * initializes it with an error message.
     *
     * @param msg the error message
     */
    public FormBuilderRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>FormBuilderRuntimeException</code> and
     * initializes it with a root cause.
     *
     * @param cause the root cause
     */
    public FormBuilderRuntimeException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Creates a new instance of <code>FormBuilderRuntimeException</code> and
     * initializes it with an error message and a root cause,
     *
     * @param msg the error message
     * @param cause the root cause
     */
    public FormBuilderRuntimeException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
