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
package net.sf.jguiraffe.examples.tutorial.mainwnd;

/**
 * <p>
 * The task of the refresh action.
 * </p>
 * <p>
 * The refresh action causes the content of the current directory to be read in
 * again.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: RefreshActionTask.java 205 2012-01-29 18:29:57Z oheger $
 */
public class RefreshActionTask implements Runnable
{
    /** A reference to the main controller. */
    private final MainWndController controller;

    /**
     * Creates a new instance of {@code RefreshActionTask} and sets the main
     * controller.
     *
     * @param ctrl the main controller
     */
    public RefreshActionTask(MainWndController ctrl)
    {
        controller = ctrl;
    }

    /**
     * Executes this task. Delegates to the main controller.
     */
    @Override
    public void run()
    {
        controller.refresh();
    }
}
