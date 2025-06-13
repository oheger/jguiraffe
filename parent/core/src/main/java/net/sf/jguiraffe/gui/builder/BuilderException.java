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
package net.sf.jguiraffe.gui.builder;

import java.net.URL;

/**
 * <p>
 * An exception class for reporting error conditions related to the GUI builder.
 * </p>
 * <p>
 * As an additional property this exception class stores the URL of the script
 * that caused the error.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BuilderException.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BuilderException extends Exception
{
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -2511636810150042168L;

    /** Stores the URL of the failing script. */
    private URL scriptURL;

    /**
     * Creates a new instance of <code>BuilderException</code> and sets the
     * error message.
     *
     * @param msg the error message
     */
    public BuilderException(String msg)
    {
        super(msg);
    }

    /**
     * Creates a new instance of <code>BuilderException</code> and sets the
     * script URL and the root cause.
     *
     * @param script the URL of the script
     * @param cause the root cause
     */
    public BuilderException(URL script, Throwable cause)
    {
        super(cause);
        scriptURL = script;
    }

    /**
     * Creates a new instance of <code>BuilderException</code> and sets the
     * script URL, the root cause, and an additional message.
     *
     * @param script the script URL
     * @param msg the error message
     * @param cause the root cause
     */
    public BuilderException(URL script, String msg, Throwable cause)
    {
        super(msg, cause);
        scriptURL = script;
    }

    /**
     * Returns the URL of the failing script.
     *
     * @return the URL of the script
     */
    public URL getScriptURL()
    {
        return scriptURL;
    }
}
