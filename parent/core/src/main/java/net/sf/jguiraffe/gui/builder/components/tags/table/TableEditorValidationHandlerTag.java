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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import net.sf.jguiraffe.gui.builder.components.tags.TextData;
import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class for defining the
 * <code>{@link TableEditorValidationHandler}</code> to be used for a table
 * component.
 * </p>
 * <p>
 * With this tag - that can be placed in the body of a
 * <code>{@link TableTag}</code> - a component can be registered at a table
 * that is triggered for every validation of user input. This component has the
 * opportunity of reacting on validation errors or post-processing user input.
 * </p>
 * <p>
 * This tag is derived from <code>{@link UseBeanBaseTag}</code>, so the
 * <code>TableEditorValidationHandler</code> to be installed can either be
 * defined directly by providing the fully qualified class name or as a
 * reference to a variable in the Jelly context. In addition to the attributes
 * inherited from the base class the following ones are supported: <table
 * border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">validationErrorTitle</td>
 * <td>Here the title of an error message can be specified that is displayed in
 * case of validation errors. This text can be evaluated by the
 * <code>{@link TableEditorValidationHandler}</code> object. It is also
 * possible to define only the error message without defining a concrete
 * implementation object. Then the platform-specific default implementation will
 * be used that accesses this error message.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">validationErrorTitleres</td>
 * <td>This attribute has the same purpose than the
 * <code>validationErrorTitle</code> attribute, but the title is set as a
 * resource ID.</td>
 * <td valign="top">yes</td>
 * </tr>
 * <tr>
 * <td valign="top">validationErrorTitlegroup</td>
 * <td>If the title of the validation error message box is specified as a
 * resource ID, here the corresponding resource group can be defined. If this
 * attribute is missing, the default resource group will be used.</td>
 * <td valign="top">yes</td>
 * </tr>
 * </table>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableEditorValidationHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TableEditorValidationHandlerTag extends UseBeanBaseTag
{
    /** Constant for the validationErrorTitle attribute. */
    private static final String ATTR_VALERRTITLE = "validationErrorTitle";

    /** Constant for the validationErrorTitleres attribute. */
    private static final String ATTR_VALERRTITLERES = "validationErrorTitleres";

    /** Constant for the validationErrorTitlegroup attribute. */
    private static final String ATTR_VALERRTITLEGRP = "validationErrorTitlegroup";

    /**
     * Creates a new instance of <code>TableEditorValidationHandlerTag</code>.
     */
    public TableEditorValidationHandlerTag()
    {
        super();
        setBaseClass(TableEditorValidationHandler.class);
        addIgnoreProperties(ATTR_VALERRTITLE, ATTR_VALERRTITLEGRP,
                ATTR_VALERRTITLERES);
    }

    /**
     * Passes the results of this tag. This implementation tries to find the
     * enclosing table tag and sets the editor validation handler.
     *
     * @param bean the bean to be installed
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        TableTag tableTag = (TableTag) findAncestorWithClass(TableTag.class);
        if (tableTag == null)
        {
            return false;
        }

        tableTag
                .setEditorValidationHandler((TableEditorValidationHandler) bean);

        if (getAttributes().get(ATTR_VALERRTITLE) != null
                || getAttributes().get(ATTR_VALERRTITLERES) != null)
        {
            TextData td = new TextData(tableTag);
            td.setText(getAttributeStr(ATTR_VALERRTITLE));
            td.setResgrp(getAttributeStr(ATTR_VALERRTITLEGRP));
            td.setTextres(getAttributeStr(ATTR_VALERRTITLERES));
            tableTag.setValidationErrorCaption(td.getCaption());
        }
        return true;
    }

    /**
     * Indicates if this bean definition is optional. This is the case for this
     * tag because it is possible to define only properties for the
     * <code>{@link TableEditorValidationHandler}</code> without actually
     * creating an implementation.
     *
     * @return the optional flag
     */
    @Override
    protected boolean isOptional()
    {
        return true;
    }
}
