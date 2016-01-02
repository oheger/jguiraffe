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

import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A simple tag for creating objects of type
 * {@link net.sf.jguiraffe.gui.builder.action.ActionData ActionData}.
 * </p>
 * <p>
 * This class can be used if only properties of actions are to be defined, but
 * no complete action objects. The properties can later be used by other
 * components (e.g. handlers for popup menus) in order to define real actions or
 * other graphical components with similar properties.
 * </p>
 * <p>
 * The major part of the functionality provided by this tag is already
 * implemented by the super class. What this class does is to store a reference
 * to itself (which is actually an {@code ActionData} implementation) in the
 * Jelly context where it can be retrieved later on. For this purpose the
 * mandatory {@code var} attribute must be specified. Of course, all other
 * attributes defined by the super class are available, too. A usage example for
 * this tag could look as follows:
 *
 * <pre>
 * &lt;a:actionData textres=&quot;ACT_FILE_OPEN_TXT&quot;
 *   mnemonicres=&quot;ACT_FILE_OPEN_MNEMO&quot; tooltipres=&quot;ACT_FILE_OPEN_TIP&quot;&gt;
 *   &lt;f:icon resource=&quot;icon.gif&quot;/&gt;
 * &lt;/a:actionData&gt;
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ActionDataTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ActionDataTag extends AbstractActionDataTag
{
    /** The variable name where to store the data. */
    private String var;

    /**
     * Returns the name of the variable where to store the data object created
     * by this tag.
     *
     * @return the name of the target variable
     */
    public String getVar()
    {
        return var;
    }

    /**
     * Set method of the {@code var} attribute.
     *
     * @param var the attribute's value
     */
    public void setVar(String var)
    {
        this.var = var;
    }

    /**
     * Executes this tag. This implementation stores an {@code ActionData}
     * object in the current context under the name specified by the {@code var}
     * attribute. If the attribute is missing, an exception is thrown.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        if (getVar() == null)
        {
            throw new MissingAttributeException("var");
        }

        getContext().setVariable(getVar(), this);
    }

    /**
     * Returns the task for the represented action. Tasks are not supported by
     * this tag, so this method always returns <b>null</b>.
     *
     * @return the task of this action
     */
    public Object getTask()
    {
        return null;
    }
}
