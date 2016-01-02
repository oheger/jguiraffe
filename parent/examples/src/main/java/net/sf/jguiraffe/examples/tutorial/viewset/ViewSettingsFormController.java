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
package net.sf.jguiraffe.examples.tutorial.viewset;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.components.ComponentGroup;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.event.FormChangeEvent;
import net.sf.jguiraffe.gui.builder.event.FormChangeListener;
import net.sf.jguiraffe.gui.builder.window.WindowEvent;
import net.sf.jguiraffe.gui.builder.window.ctrl.FormController;
import net.sf.jguiraffe.gui.forms.ComponentHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * A specialized {@code FormController} class for the view settings dialog.
 * </p>
 * <p>
 * This class implements some additional event processing. It especially enables
 * or disables some components based on the status of other controls. For
 * instance, certain fields defining filter settings are only active if the
 * check box related to this filter is checked.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ViewSettingsFormController.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ViewSettingsFormController extends FormController implements
        FormChangeListener
{
    /** A mapping for check box names to group names. */
    private static final Map<String, String> FILTER_GROUPS;

    /** The logger. */
    private final Log log = LogFactory.getLog(getClass());

    /**
     * A change event was received. This method is called when one of the check
     * boxes representing filters changes its state. In this case the enabled
     * state of depending components has to be changed.
     *
     * @param e the change event
     */
    @Override
    public void elementChanged(FormChangeEvent e)
    {
        checkBoxChanged(e.getName());
    }

    /**
     * The associated window was opened. Perform some initializations.
     *
     * @param event the window event
     */
    @Override
    public void windowOpened(WindowEvent event)
    {
        super.windowOpened(event);

        // initialize the enabled states for the filter check boxes
        for (String name : FILTER_GROUPS.keySet())
        {
            checkBoxChanged(name);
        }
    }

    /**
     * The check box with the given name was changed. In this case the enabled
     * state of depending components has to be changed.
     *
     * @param name the name of the check box
     */
    private void checkBoxChanged(String name)
    {
        @SuppressWarnings("unchecked")
        // it's a handler for a check box
        ComponentHandler<Boolean> handler = (ComponentHandler<Boolean>) getComponentBuilderData()
                .getComponentHandler(name);
        enableGroup(FILTER_GROUPS.get(name), handler.getData());
    }

    /**
     * Helper method for enabling a component group. This method is called when
     * one of the filter check boxes is clicked. Depending on the check box's
     * state the input fields associated with this filter must be enabled or
     * disabled. The fields are organized in groups, so only the groups need to
     * be handled.
     *
     * @param groupName the name of the group
     * @param enabled the enabled flag
     */
    private void enableGroup(String groupName, boolean enabled)
    {
        ComponentGroup group = ComponentGroup.fromBeanContext(
                getComponentBuilderData().getBeanContext(), groupName);
        try
        {
            group.enableGroup(getComponentBuilderData(), enabled);
        }
        catch (FormBuilderException fex)
        {
            // should normally not happen
            log.warn("Error when enabling group " + groupName, fex);
        }
    }

    /**
     * Creates the mapping between check boxes and associated component groups.
     * The managed dialog contains some check boxes that represent certain types
     * of filters. Each check box is associated a group of components that
     * should only be active if the check box is checked. This method creates a
     * map that maps the name of a check box to the associated component group.
     *
     * @return the mapping
     */
    private static Map<String, String> initFilterGroupMapping()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("filterTypes", "filterTypesGroup");
        map.put("filterSize", "filterSizeGroup");
        map.put("filterDate", "filterDateGroup");
        return map;
    }

    static
    {
        FILTER_GROUPS = initFilterGroupMapping();
    }
}
