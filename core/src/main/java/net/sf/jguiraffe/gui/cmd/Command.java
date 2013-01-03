/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
 * Definition of an interface for command objects.
 * </p>
 * <p>
 * This interface is a typical application of the famous <em>Command</em>
 * pattern. It allows constructing objects that can be executed in a worker
 * thread in the background of a Java GUI (e.g. Swing) application. This is
 * important for every longer running task that would block the whole GUI (i.e.
 * the event dispatch thread) otherwise. The life-cycle of a {@code Command}
 * object is a follows:
 * </p>
 * <p>
 * Objects implementing this interface can be passed to a {@link CommandQueue}
 * object. When the {@code CommandQueue} object decides to execute the command
 * it invokes its {@link #execute()} method in a background thread. {@code
 * execute()} can implement whatever logic is needed for the command. It can
 * also throw an arbitrary exception. If an exception is thrown, the command's
 * {@link #onException(Throwable)} method is invoked (this also happens in the
 * background thread). After completing {@code execute()} the
 * {@link #onFinally()} method is invoked (it does not matter whether an
 * exception occurred or not; this method is always called), also in the
 * background thread.
 * </p>
 * <p>
 * Command typically need to update the UI after executing their business logic.
 * This must be done in the event dispatch thread. The {@code Command} interface
 * supports updating the UI through its {@link #getGUIUpdater()} method. Here an
 * implementation can return a {@code Runnable} object that will be executed
 * synchronously on the event dispatch thread; here arbitrary updates of the
 * application's UI can be performed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Command.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Command
{
    /**
     * Executes this command. Here the business logic of this command has to be
     * implemented. The {@code execute()} method is invoked on a background
     * thread so that the event dispatch thread of the application is not
     * blocked.
     *
     * @throws Exception if an error occurs
     */
    void execute() throws Exception;

    /**
     * Callback method that is invoked if an exception occurs during command
     * execution. If {@link #execute()} throws an exception, this method is
     * called (also on the background thread). Here actions like logging or
     * special exception handling can be performed. Note that UI updates are not
     * allowed in this method because it runs on a separate thread.
     *
     * @param t the exception that occurred
     */
    void onException(Throwable t);

    /**
     * This method will be executed after each command execution, no matter if
     * an exception has occurred or not. It can be used for instance to free
     * resources. Like {@code execute()} it is called on a background thread, so
     * no UI updates are allowed.
     */
    void onFinally();

    /**
     * This method is invoked after the background processing of the command
     * ends. It can return a {@code Runnable} object that will be executed in
     * the application's event dispatching thread, which means that it is
     * allowed to update GUI widgets. The idea of this method is that after a
     * command has been executed often GUI updates have to be performed. These
     * can be done using this mechanism in a safe manner.
     *
     * @return an object to be executed in the event dispatching thread (may be
     *         <b>null</b>
     */
    Runnable getGUIUpdater();
}
