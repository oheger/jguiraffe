/*
 * Copyright 2006-2015 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnEditorTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.ColumnRendererTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableColumnTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableEditorValidationHandlerTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableSelectionHandlerTag;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;

import org.apache.commons.jelly.TagLibrary;

/**
 * <p>The tag library for the form builder tags.</p>
 *
 * @author Oliver Heger
 * @version $Id: FormBuilderTagLibrary.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FormBuilderTagLibrary extends TagLibrary
{
    /**
     * Creates a new instance of <code>FormBuilderTagLibrary</code> and registers
     * the tags.
     */
    public FormBuilderTagLibrary()
    {
        super();
        registerTag("borderconstr", BorderConstraintsTag.class);
        registerTag("borderlayout", BorderLayoutTag.class);
        registerTag("button", ButtonTag.class);
        registerTag("buttonlayout", ButtonLayoutTag.class);
        registerTag("checkbox", CheckboxTag.class);
        registerTag("colconstr", PercentColConstraintsTag.class);
        registerTag("coleditor", ColumnEditorTag.class);
        registerTag("colgroup", PercentColGroupTag.class);
        registerTag("colrenderer", ColumnRendererTag.class);
        registerTag("column", TableColumnTag.class);
        registerTag("combo", ComboBoxTag.class);
        registerTag("componentHandler", ComponentHandlerTag.class);
        registerTag("converter", ConverterTag.class);
        registerTag("desktopPanel", DesktopPanelTag.class);
        registerTag("field", FieldTag.class);
        registerTag("font", FontTag.class);
        registerTag("group", ComponentGroupTag.class);
        registerTag("icon", IconTag.class);
        registerTag("label", LabelTag.class);
        registerTag("list", ListBoxTag.class);
        registerTag("listModelItem", ListModelItemTag.class);
        registerTag("localized", LocalizedTag.class);
        registerTag("panel", PanelTag.class);
        registerTag("password", PasswordFieldTag.class);
        registerTag("percentconstr", PercentConstraintsTag.class);
        registerTag("percentlayout", PercentLayoutTag.class);
        registerTag("progressbar", ProgressBarTag.class);
        registerTag("properties", PropertiesTag.class);
        registerTag("property", PropertyTag.class);
        registerTag("radio", RadioButtonTag.class);
        registerTag("radioGroup", RadioGroupTag.class);
        registerTag("reference", ReferenceTag.class);
        registerTag("rowconstr", PercentRowConstraintsTag.class);
        registerTag("rowgroup", PercentRowGroupTag.class);
        registerTag("selectionHandler", TableSelectionHandlerTag.class);
        registerTag("slider", SliderTag.class);
        registerTag("splitter", SplitterTag.class);
        registerTag("statictext", StaticTextTag.class);
        registerTag("tab", TabTag.class);
        registerTag("tabbedpane", TabbedPaneTag.class);
        registerTag("table", TableTag.class);
        registerTag("textfield", TextFieldTag.class);
        registerTag("textarea", TextAreaTag.class);
        registerTag("textListModel", TextListModelTag.class);
        registerTag("toggleButton", ToggleButtonTag.class);
        registerTag("transformer", TransformerTag.class);
        registerTag("tree", TreeTag.class);
        registerTag("treeIcon", TreeIconTag.class);
        registerTag("validationHandler", TableEditorValidationHandlerTag.class);
        registerTag("validator", ValidatorTag.class);
        registerTag("validators", ValidatorsTag.class);
    }
}
