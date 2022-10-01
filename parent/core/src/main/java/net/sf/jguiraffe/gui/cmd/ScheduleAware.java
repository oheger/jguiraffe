/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
 * Definition of an interface to be implemented by {@link Command} objects that
 * are interested in the point of time they are passed to a {@link CommandQueue}
 * .
 * </p>
 * <p>
 * This interface is evaluated by implementations of the
 * {@link CommandQueue#execute(Command)} method. If the {@code Command} object
 * to be executed implements this interface, the {@code onSchedule()} method is
 * invoked. Invocation of this method happens in the same thread that has called
 * the {@code execute()} method. This is usually the event dispatch thread in
 * typical GUI applications, when a user triggered an action, which causes the
 * execution of a command.
 * </p>
 * <p>
 * The idea behind this interface is that often some initialization has to be
 * performed before the actual execution of a command in a background thread. An
 * example of such an initialization is changing the status of UI controls
 * affected by the current command. This has typically to be done in the event
 * dispatch thread, immediately after the invocation of the action that caused
 * the execution of this command. By implementing this interface this
 * initialization logic can be placed in the {@code Command} implementation
 * itself and need not to be implemented somewhere else.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ScheduleAware.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ScheduleAware
{
    /**
     * Notifies this object that it was passed to a {@code CommandQueue} for
     * execution. This method is invoked by the
     * {@link CommandQueue#execute(Command)} method (in the current thread).
     *
     * @param queue the {@code CommandQueue} to which this object was passed
     */
    void commandScheduled(CommandQueue queue);
}
