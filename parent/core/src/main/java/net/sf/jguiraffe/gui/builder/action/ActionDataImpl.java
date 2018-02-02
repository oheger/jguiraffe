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
package net.sf.jguiraffe.gui.builder.action;

/**
 * <p>
 * A straight forward implementation of the <code>ActionData<code> interface.
 * </p>
 * <p>
 * This class defines member variables for all the get and set methods defined
 * by the <code>ActionData</code> interface. The access methods operate directly
 * on these fields.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionDataImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionDataImpl implements ActionData
{
    /** The name of the action. */
    private String name;

    /** The text of the action. */
    private String text;

    /** The tool tip text. */
    private String toolTip;

    /** The mnemonic key. */
    private int mnemonicKey;

    /** The accelerator. */
    private Accelerator accelerator;

    /** The icon. */
    private Object icon;

    /** The task. */
    private Object task;

    /**
     * Returns the icon for the represented action.
     *
     * @return the icon
     */
    public Object getIcon()
    {
        return icon;
    }

    /**
     * Sets the icon for the represented action. The object passed to this
     * method must be a valid icon for the GUI library used.
     *
     * @param icon the icon
     */
    public void setIcon(Object icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the mnemonic key.
     *
     * @return the mnemonic
     */
    public int getMnemonicKey()
    {
        return mnemonicKey;
    }

    /**
     * Sets the mnemonic key.
     *
     * @param mnemonicKey the key code
     */
    public void setMnemonicKey(int mnemonicKey)
    {
        this.mnemonicKey = mnemonicKey;
    }

    /**
     * Returns the accelerator.
     *
     * @return the accelerator
     */
    public Accelerator getAccelerator()
    {
        return accelerator;
    }

    /**
     * Sets the accelerator.
     *
     * @param accelerator the accelerator
     */
    public void setAccelerator(Accelerator accelerator)
    {
        this.accelerator = accelerator;
    }

    /**
     * Returns the name of this action.
     *
     * @return the action name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this action.
     *
     * @param name the action name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the task of this action.
     *
     * @return the task
     */
    public Object getTask()
    {
        return task;
    }

    /**
     * Allows setting the task of this action. This task will be executed when
     * the action is triggered. The object passed to this method must be a valid
     * task. However, this method does not perform any validity checks; it just
     * stores the object.
     *
     * @param t the task
     */
    public void setTask(Object t)
    {
        this.task = t;
    }

    /**
     * Returns the text of this action.
     *
     * @return the action text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text of this action.
     *
     * @param text the text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Returns the tool tip of this action.
     *
     * @return the tool tip text
     */
    public String getToolTip()
    {
        return toolTip;
    }

    /**
     * Sets the tool tip text of this action.
     *
     * @param toolTip the tool tip text
     */
    public void setToolTip(String toolTip)
    {
        this.toolTip = toolTip;
    }

    /**
     * Initializes this object from the specified {@code ActionData} object. The
     * properties are copied from this object. Note: This is not a typical
     * property set method because there is no corresponding {@code
     * getActionData()} method. However, using this name simplifies bean
     * declarations for this class in builder scripts.
     *
     * @param c the source object (must not be <b>null</b>)
     * @throws IllegalArgumentException if the {@code ActionData} object is
     *         <b>null</b>
     */
    public void setActionData(ActionData c)
    {
        if (c == null)
        {
            throw new IllegalArgumentException(
                    "Source data objec must not be null!");
        }

        setName(c.getName());
        setAccelerator(c.getAccelerator());
        setIcon(c.getIcon());
        setMnemonicKey(c.getMnemonicKey());
        setTask(c.getTask());
        setText(c.getText());
        setToolTip(c.getToolTip());
    }
}
