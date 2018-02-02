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
package net.sf.jguiraffe.gui.cmd;

/**
 * <p>
 * A wrapper implementation of the {@code Command} interface.
 * </p>
 * <p>
 * An instance of this class is initialized with another {@code Command} object.
 * It implements all methods defined by the {@code Command} interface by
 * delegating to the wrapped {@code Command} object. Thus this class can serve
 * as a base class for command implementations that need to alter certain
 * behavior of other command objects.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandWrapper.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CommandWrapper implements Command
{
    /** Stores the wrapped command. */
    private final Command wrappedCommand;

    /**
     * Creates a new instance of {@code CommandWrapper} and initializes it with
     * the wrapped command.
     *
     * @param wrappedCmd the wrapped command (must not be <b>null</b>)
     * @throws IllegalArgumentException if the wrapped command is <b>null</b>
     */
    public CommandWrapper(Command wrappedCmd)
    {
        if (wrappedCmd == null)
        {
            throw new IllegalArgumentException(
                    "Wrapped command must not be null!");
        }

        wrappedCommand = wrappedCmd;
    }

    /**
     * Returns the {@code Command} object that is wrapped by this object.
     *
     * @return the wrapped {@code Command}
     */
    public final Command getWrappedCommand()
    {
        return wrappedCommand;
    }

    /**
     * Executes this command. This implementation delegates to the wrapped
     * command.
     *
     * @throws Exception if an error occurs during execution
     */
    public void execute() throws Exception
    {
        getWrappedCommand().execute();
    }

    /**
     * Returns the object for the updating the UI after execution of the
     * command. This implementation delegates to the wrapped command.
     *
     * @return the object for updating the UI
     */
    public Runnable getGUIUpdater()
    {
        return getWrappedCommand().getGUIUpdater();
    }

    /**
     * Notifies this command about an exception that occurred during execution.
     * This implementation delegates to the wrapped command.
     *
     * @param t the exception
     */
    public void onException(Throwable t)
    {
        getWrappedCommand().onException(t);
    }

    /**
     * This method is called after the execution of the command. This
     * implementation delegates to the wrapped command.
     */
    public void onFinally()
    {
        getWrappedCommand().onFinally();
    }
}
