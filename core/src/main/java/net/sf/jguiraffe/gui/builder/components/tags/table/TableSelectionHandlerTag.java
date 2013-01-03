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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import org.apache.commons.jelly.JellyTagException;

import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;

/**
 * <p>
 * A tag handler class for defining selection handlers for a table component.
 * </p>
 * <p>
 * With this tag a concrete implementation of the
 * <code>{@link TableSelectionHandler}</code> interface can be specified that
 * will be used for either renderer or editor components (depending on the value
 * of the boolean <code>editor</code> attribute) of the associated table. The
 * tag can appear in the body of a <code>{@link TableTag}</code> tag only.
 * </p>
 * <p>
 * By extending <code>UseBeanBaseTag</code> the typical attributes for
 * defining beans (<code>class</code> or <code>ref</code>) are supported.
 * In addition the <code>editor</code> attribute defines, which
 * <code>TableSelectionHandler</code> is to be set on the associated table:
 * <ul>
 * <li>a value of <b>false</b> (which is also the default value) indicates
 * that the <code>TableSelectionHandler</code> for renderer components is to
 * set.</li>
 * <li> a value of <b>true</b> in contrast will set the
 * <code>TableSelectionHandler</code> for editor components.</li>
 * </ul>
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TableSelectionHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TableSelectionHandlerTag extends UseBeanBaseTag
{
    /** Constant for the reserved editor attribute. */
    private static final String ATTR_EDITOR = "editor";

    /**
     * Creates a new instance of <code>TableSelectionHandlerTag</code>.
     */
    public TableSelectionHandlerTag()
    {
        super();
        setBaseClass(TableSelectionHandler.class);
        addIgnoreProperty(ATTR_EDITOR);
    }

    /**
     * Passes the <code>TableSelectionHandler</code> created by this tag to
     * the enclosing table tag. This implementation looks for the
     * <code>TableTag</code> this tag is nested into. It then sets the
     * corresponding handler. If this tag is not nested inside a
     * <code>TableTag</code> and no <code>var</code> attribute is set, an
     * exception will be thrown.
     *
     * @param bean the resulting bean
     * @return a flag whether the bean could be passed to a target
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        TableTag tableTag = (TableTag) findAncestorWithClass(TableTag.class);
        if (tableTag == null)
        {
            return false;
        }

        if (getAttributes().containsKey(ATTR_EDITOR)
                && Boolean.valueOf(String.valueOf(getAttributes().get(
                        ATTR_EDITOR))))
        {
            tableTag.setEditorSelectionHandler((TableSelectionHandler) bean);
        }
        else
        {
            tableTag.setRendererSelectionHandler((TableSelectionHandler) bean);
        }
        return true;
    }
}
