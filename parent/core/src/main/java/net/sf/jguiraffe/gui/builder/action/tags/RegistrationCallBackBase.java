/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An abstract {@code ComponentBuilderCallBack} base implementation that
 * registers an event listener at an object at the end of the processing of the
 * current builder script.
 * </p>
 * <p>
 * This class is used by tag handler classes for registering event listeners.
 * There are concrete sub classes for registering listeners at graphical
 * components and at beans.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RegistrationCallBackBase.java 205 2012-01-29 18:29:57Z oheger $
 */
abstract class RegistrationCallBackBase implements ComponentBuilderCallBack
{
    /** The logger. */
    protected final Log log = LogFactory.getLog(getClass());

    /** The name of the target of the registration. */
    private final String targetName;

    /** The listener type. */
    private final String listenerType;

    /** The event listener to register. */
    private final EventListener listener;

    /** The ignore failure flag. */
    private final boolean ignoreFailure;

    /**
     * Creates a new instance of {@code RegistrationCallBackBase} and
     * initializes it.
     *
     * @param name the name of the target of this registration
     * @param type the listener type
     * @param l the listener
     * @param ignore the ignore failure flag
     */
    protected RegistrationCallBackBase(String name, String type,
            EventListener l, boolean ignore)
    {
        targetName = name;
        listenerType = type;
        listener = l;
        ignoreFailure = ignore;
    }

    /**
     * Executes this callback. Delegates to the
     * {@link #register(ComponentBuilderData, Object)} method to actually
     * register the event listener. If no registration can be performed and the
     * ignore failure flag is not set, an exception is thrown.
     *
     * @param builderData the {@code ComponentBuilderData} object
     * @param params additional parameters
     * @throws FormBuilderException if registration is not possible
     */
    public void callBack(ComponentBuilderData builderData, Object params)
            throws FormBuilderException
    {
        boolean success = false;

        try
        {
            if (register(builderData, params))
            {
                success = true;
            }
        }
        catch (Exception ex)
        {
            log.error("Error when registering event listener at target "
                    + getTargetName(), ex);
        }

        if (!success && !isIgnoreFailure())
        {
            throw new FormBuilderException(
                    "Could not register listener of type " + getListenerType()
                            + " at target " + getTargetName());
        }
    }

    /**
     * Returns the target name of the object to which the register is to be
     * added.
     *
     * @return the name of the target object
     */
    protected String getTargetName()
    {
        return targetName;
    }

    /**
     * Returns the event listener type.
     *
     * @return the name of the event listener type
     */
    protected String getListenerType()
    {
        return listenerType;
    }

    /**
     * Returns the event listener to be registered.
     *
     * @return the event listener
     */
    protected EventListener getListener()
    {
        return listener;
    }

    /**
     * Returns a flag whether errors are to be ignored when doing the
     * registration.
     *
     * @return a flag whether errors are to be ignored
     */
    protected boolean isIgnoreFailure()
    {
        return ignoreFailure;
    }

    /**
     * Performs the actual registration of the event listener. This method is
     * called by {@code callBack()}. Failures (indicated by the return value)
     * and exceptions are handled correspondingly.
     *
     * @param builderData the {@code ComponentBuilderData} object
     * @param params additional parameters that have been passed to the {@code
     *        ComponentBuilderData} object when registering the callback
     * @return a flag whether registration was successful
     * @throws Exception if an error occurs
     */
    protected abstract boolean register(ComponentBuilderData builderData,
            Object params) throws Exception;
}
