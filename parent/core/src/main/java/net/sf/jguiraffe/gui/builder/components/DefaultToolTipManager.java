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
package net.sf.jguiraffe.gui.builder.components;

import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.gui.builder.utils.GUIRuntimeException;

/**
 * <p>
 * A default implementation of the {@code ToolTipManager} interface.
 * </p>
 * <p>
 * This class provides fully functional implementations for all methods defined
 * by the {@code ToolTipManager} interface. An instance is initialized with a
 * reference to a {@link ComponentBuilderData} object. From this object the
 * {@link WidgetHandler} objects associated with the components to manipulate
 * are obtained. The instance also maintains data about the tool tips (both the
 * static and the dynamic ones) of all components managed by the associated
 * {@link ComponentBuilderData} object.
 * </p>
 * <p>
 * Implementation note: This class is not thread-safe. It must only be accessed
 * from the event dispatch thread. It can only be associated with a single
 * {@link ComponentBuilderData} object.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DefaultToolTipManager.java 205 2012-01-29 18:29:57Z oheger $
 */
public class DefaultToolTipManager implements ToolTipManager
{
    /** Constant for the default separator for the tip and the additional tip. */
    private static final String TIP_SEPARATOR = "\n";

    /** A map with the information about the tool tips for the known components. */
    private final Map<Object, TipData> tipData;

    /** Stores the associated component builder data object. */
    private final ComponentBuilderData componentBuilderData;

    /** Stores the separator between the standard and the additional tool tip. */
    private String additionalTipSeparator;

    /**
     * Creates a new instance of {@code DefaultToolTipManager} and initializes
     * it with the given {@code ComponentBuilderData} object.
     *
     * @param compData the {@code ComponentBuilderData} object (must not be
     *        <b>null</b>)
     * @throws IllegalArgumentException if the {@code ComponentBuilderData}
     *         object is <b>null</b>
     */
    public DefaultToolTipManager(ComponentBuilderData compData)
    {
        if (compData == null)
        {
            throw new IllegalArgumentException(
                    "ComponentBuilderData must not be null!");
        }

        componentBuilderData = compData;
        tipData = new HashMap<Object, TipData>();
    }

    /**
     * Returns the separator between the standard and the additional tool tip.
     *
     * @return the tool tip separator
     */
    public String getAdditionalTipSeparator()
    {
        return (additionalTipSeparator != null) ? additionalTipSeparator
                : TIP_SEPARATOR;
    }

    /**
     * Sets the separator between the standard and the additional tool tip. If a
     * component has both a standard and an additional tool tip, the final tool
     * tip that is to be passed to the component is constructed by appending the
     * additional tool tip to the standard tool tip using this string as
     * separator. If no separator is set, a new line character is used as
     * default.
     *
     * @param additionalTipSeparator the tool tip separator
     */
    public void setAdditionalTipSeparator(String additionalTipSeparator)
    {
        this.additionalTipSeparator = additionalTipSeparator;
    }

    /**
     * Returns the associated {@code ComponentBuilderData}.
     *
     * @return the {@code ComponentBuilderData}
     */
    public final ComponentBuilderData getComponentBuilderData()
    {
        return componentBuilderData;
    }

    /**
     * Queries the additional tool tip for the specified component.
     *
     * @param component the component in question
     * @return the additional tool tip for this component
     */
    public String getAdditionalToolTip(Object component)
    {
        return fetchTipData(component).getAdditionalTip();
    }

    /**
     * Returns the additional tool tip for the component with the given name.
     *
     * @param componentName the name of the component
     * @return the additional tool tip for this component
     * @throws GUIRuntimeException if the name cannot be resolved
     */
    public String getAdditionalToolTip(String componentName)
    {
        return getAdditionalToolTip(fetchComponent(componentName));
    }

    /**
     * Queries the standard tool tip for the specified component.
     *
     * @param component the component in question
     * @return the standard tool tip of this component
     */
    public String getToolTip(Object component)
    {
        return fetchTipData(component).getStandardTip();
    }

    /**
     * Returns the standard tool tip for the component with the given name.
     *
     * @param componentName the name of the component
     * @return the standard tool tip for this component
     * @throws GUIRuntimeException if the name cannot be resolved
     */
    public String getToolTip(String componentName)
    {
        return getToolTip(fetchComponent(componentName));
    }

    /**
     * Sets the additional tool tip for the specified component.
     *
     * @param component the component
     * @param tip the new additional tool tip
     */
    public void setAdditionalToolTip(Object component, String tip)
    {
        TipData data = fetchTipData(component);
        data.setAdditionalTip(tip);
        fetchWidgetHandler(component).setToolTip(constructTip(data));
    }

    /**
     * Sets the additional tool tip for the component with the given name.
     *
     * @param componentName the name of the component
     * @param tip the new additional tool tip
     * @throws GUIRuntimeException if the component name cannot be resolved
     */
    public void setAdditionalToolTip(String componentName, String tip)
    {
        setAdditionalToolTip(fetchComponent(componentName), tip);
    }

    /**
     * Sets the tool tip for the specified component.
     *
     * @param component the component
     * @param tip the new tool tip
     */
    public void setToolTip(Object component, String tip)
    {
        TipData data = fetchTipData(component);
        data.setStandardTip(tip);
        fetchWidgetHandler(component).setToolTip(constructTip(data));
    }

    /**
     * Sets the standard tool tip for the component with the given name.
     *
     * @param componentName the name of the component
     * @param tip the new standard tool tip
     * @throws GUIRuntimeException if the component name cannot be resolved
     */
    public void setToolTip(String componentName, String tip)
    {
        setToolTip(fetchComponent(componentName), tip);
    }

    /**
     * Creates a combined tool tip from the standard tool tip and the additional
     * tool tip. This method is called whenever the tool tip of a widget needs
     * to be changed and both components are specified. The default
     * implementation appends the additional tip to the standard one using the
     * separator defined by the {@link #setAdditionalTipSeparator(String)}
     * method.
     *
     * @param stdTip the standard tool tip
     * @param addTip the additional tool tip
     * @return the combined tool tip
     */
    protected String combineTips(String stdTip, String addTip)
    {
        StringBuilder buf = new StringBuilder();
        buf.append(stdTip);
        buf.append(getAdditionalTipSeparator());
        buf.append(addTip);
        return buf.toString();
    }

    /**
     * Obtains the component with the given name. This implementation delegates
     * to the {@code ComponentBuilderData} object. If the name cannot be
     * resolved, an exception is thrown.
     *
     * @param name the name of the component
     * @return the corresponding component
     * @throws GUIRuntimeException if the name cannot be resolved
     */
    private Object fetchComponent(String name)
    {
        Object comp = getComponentBuilderData().getComponent(name);
        if (comp == null)
        {
            throw new GUIRuntimeException("Unknown component name: " + name);
        }

        return comp;
    }

    /**
     * Helper method for obtaining the {@code WidgetHandler} for the specified
     * component. This handler is fetched from the {@code ComponentBuilderData}.
     *
     * @param comp the component in question
     * @return the corresponding {@code WidgetHandler}
     */
    private WidgetHandler fetchWidgetHandler(Object comp)
    {
        WidgetHandler handler = getComponentBuilderData()
                .getWidgetHandlerForComponent(comp);
        assert handler != null : "No WidgetHandler returned for component!";
        return handler;
    }

    /**
     * Fetches tool tip information for the specified component. Tests whether
     * tool tip information for the specified component is already available. If
     * not, it is fetched now.
     *
     * @param comp the component in question
     * @return the tool tip information for this component
     */
    private TipData fetchTipData(Object comp)
    {
        TipData res = tipData.get(comp);

        if (res == null)
        {
            res = new TipData();
            res.setStandardTip(fetchWidgetHandler(comp).getToolTip());
            tipData.put(comp, res);
        }

        return res;
    }

    /**
     * Constructs the final tool tip for the specified tip data. If one of the
     * components is undefined, the other one is returned. Only if both are
     * defined, a combined tip is constructed by calling the
     * {@link #combineTips(String, String)} method.
     *
     * @param data the tip data object
     * @return the resulting tool tip
     */
    private String constructTip(TipData data)
    {
        if (data.getAdditionalTip() == null)
        {
            return data.getStandardTip();
        }

        if (data.getStandardTip() == null)
        {
            return data.getAdditionalTip();
        }

        return combineTips(data.getStandardTip(), data.getAdditionalTip());
    }

    /**
     * A simple data class for storing information about the tool tips
     * associated with a component.
     */
    private static class TipData
    {
        /** The standard tip. */
        private String standardTip;

        /** The additional tip. */
        private String additionalTip;

        /**
         * Sets the standard tip.
         *
         * @param standardTip the standard tip
         */
        public void setStandardTip(String standardTip)
        {
            this.standardTip = standardTip;
        }

        /**
         * Returns the standard tip.
         *
         * @return the standard tip
         */
        public String getStandardTip()
        {
            return standardTip;
        }

        /**
         * Sets the additional tip.
         *
         * @param additionalTip the additional tip
         */
        private void setAdditionalTip(String additionalTip)
        {
            this.additionalTip = additionalTip;
        }

        /**
         * Returns the additional tip.
         *
         * @return the additional tip
         */
        private String getAdditionalTip()
        {
            return additionalTip;
        }
    }
}
