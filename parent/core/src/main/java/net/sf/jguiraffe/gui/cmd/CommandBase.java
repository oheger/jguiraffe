/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * An abstract base class for implementations of the <code>Command</code>
 * interface.
 * </p>
 * <p>
 * This abstract class provides some simple base implementations for methods
 * required by the <code>Command</code> interface. It also defines some
 * utility methods that are useful in GUI applications.
 * </p>
 * <p>
 * The main execution method of course must be implemented in concrete sub
 * classes. The <code>onException()</code> method passes the exception to a
 * logger. For <code>onFinally()</code> an empty dummy implementation is
 * provided. GUI updates can be performed in the <code>performGUIUpdate()</code>
 * method. This method will be executed by a <code>Runnable</code> object in
 * the application's event handler thread if the <code>UpdateGUI</code>
 * property is set to <b>true</b>.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CommandBase.java 205 2012-01-29 18:29:57Z oheger $
 */
public abstract class CommandBase implements Command
{
    /** A logger instance. */
    private final Log log = LogFactory.getLog(CommandBase.class);

    /** A flag if GUI updates should be performed. */
    private final boolean updateGUI;

    /** Stores the exception that was set using onException().*/
    private volatile Throwable exception;

    /**
     * Creates a new instance of {@code CommandBase}. The {@code UpdateGUI}
     * property is set to <b>true</b>.
     */
    protected CommandBase()
    {
        this(true);
    }

    /**
     * Creates a new instance of {@code CommandBase} and sets the
     * {@code UpdateGUI} property.
     *
     * @param updateGUI a flag if GUI updates are to be performed
     */
    protected CommandBase(boolean updateGUI)
    {
        this.updateGUI = updateGUI;
    }

    /**
     * This method is called if an exception occurs. This implementation calls
     * {@link #setException(Throwable)} to store the exception.
     *
     * @param t the exception
     */
    public void onException(Throwable t)
    {
        setException(t);
        getLog().info("Command execution caused an exception", t);
    }

    /**
     * This method is called after each command execution. This is an empty
     * dummy implementation.
     */
    public void onFinally()
    {
    }

    /**
     * Returns a <code>Runnable</code> object for updating the GUI. This
     * implementation returns either <b>null </b> or a <code>Runnable</code>
     * object that invokes the <code>performGUIUpdate()</code> method,
     * depending on the value of the <code>UpdateGUI</code> property.
     *
     * @return an object for updating the GUI
     */
    public Runnable getGUIUpdater()
    {
        return (isUpdateGUI()) ? new Runnable()
        {
            public void run()
            {
                performGUIUpdate();
            }
        } : null;
    }

    /**
     * Returns the value of the {@code UpdateGUI} property. This flag can be set
     * in the constructor. If set to <b>true</b>, the {@link #performGUIUpdate()}
     * method will be invoked on the event dispatch thread after the command was
     * executed. If a derived class needs a more complex logic that can be
     * implemented using a final flag, it can override this method to return a
     * value that is computed based on arbitrary criteria.
     *
     * @return the {@code UpdateGUI} property
     */
    public boolean isUpdateGUI()
    {
        return updateGUI;
    }

    /**
     * Returns an exception that was thrown during the execution of this
     * command. Result can be <b>null</b>, which means that no exception was
     * thrown.
     *
     * @return an exception thrown during the execution of this command
     */
    public Throwable getException()
    {
        return exception;
    }

    /**
     * Sets an exception that was thrown during the execution of this command.
     * The {@link #onException(Throwable)} implementation invokes this method to
     * store the exception passed to this method. It can then be queried by the
     * methods invoked later in the command's life-cycle (e.g. {@code
     * onFinally()} or {@code performGUIUpdate()}) to find out whether the
     * command's execution was successful.
     *
     * @param exception an exception
     */
    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    /**
     * Returns the logger used by this object.
     *
     * @return the logger
     */
    protected Log getLog()
    {
        return log;
    }

    /**
     * Performs GUI updates. Here the code for thread safe GUI updates can be
     * placed. If the {@code UpdateGUI} property is set, this method will
     * automatically be invoked in the event dispatch thread after the command
     * has been executed (also if an exception was thrown - in this case the
     * exception can be queried using the {@link #getException()} method). This
     * base implementation is empty.
     */
    protected void performGUIUpdate()
    {
    }
}
