/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import net.sf.jguiraffe.gui.builder.event.BuilderEvent;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * An implementation of the <code>FormAction</code> interface used for
 * testing.
 * </p>
 * <p>
 * This implementation does not do anything useful. It just provides more or
 * less dummy implementations for the required methods.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FormActionImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormActionImpl implements FormAction
{
    /** Stores the name. */
    private String name;

    /** Stores the enabled flag. */
    private boolean enabled;

    /** Stores the checked flag. */
    private boolean checked;

    /** Stores data as a plain text. */
    private String data;

    /** Stores the task for this action. */
    private Object task;

    /**
     * Creates a new, uninitialized instance.
     */
    public FormActionImpl()
    {
        this(null);
    }

    /**
     * Creates a new instance and sets the name.
     *
     * @param name the action's name
     */
    public FormActionImpl(String name)
    {
        setName(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean f)
    {
        enabled = f;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean f)
    {
        checked = f;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public Object getTask()
    {
        return task;
    }

    public void setTask(Object task)
    {
        this.task = task;
    }

    public void execute(BuilderEvent event)
    {
        // Dummy implementation
    }

    /**
     * Returns a string representation of this action. The returned string
     * contains all information available for this action (including the data if
     * defined).
     *
     * @return a string for this action
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("Action ");
        buf.append(getName()).append(" {");
        if (isChecked())
        {
            buf.append(" CHECKED");
        }
        if (isEnabled())
        {
            buf.append(" ENABLED");
        }
        if (StringUtils.isNotEmpty(getData()))
        {
            buf.append(' ').append(getData());
        }
        buf.append(" }");
        return buf.toString();
    }
}
