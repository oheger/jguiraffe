/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.examples.tutorial.bgtask;

/**
 * <p>
 * A data class that stores all information required by the background task.
 * </p>
 * <p>
 * An instance of this class acts as the form bean for the dialog form that
 * defines the background task.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BgTaskData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class BgTaskData
{
    /** The duration of the task in seconds. */
    private int duration = 10;

    /** A flag whether there should be visual feedback. */
    private boolean visual = true;

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public boolean isVisual()
    {
        return visual;
    }

    public void setVisual(boolean visual)
    {
        this.visual = visual;
    }
}
