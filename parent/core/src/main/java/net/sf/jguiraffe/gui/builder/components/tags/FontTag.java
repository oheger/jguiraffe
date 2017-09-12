/*
 * Copyright 2006-2017 The JGUIraffe Team.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.gui.builder.components.ComponentManager;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A specialized tag handler class for creating fonts.
 * </p>
 * <p>
 * With this tag handler class it is possible to create new font objects and
 * store them in the current context or assign them to graphical components.
 * Fonts are platform-specific and hence are created by the
 * {@link ComponentManager}. This tag supports a limited set of standard
 * attributes describing the font which should be supported by most platform. In
 * addition, a reference to a map with platform-specific attributes can be
 * provided. These attributes are taken into account by the
 * {@link ComponentManager}. That way core attributes of fonts can be defined in
 * a portable way while specific attributes an also be set.
 * </p>
 * <p>
 * The following table lists the attributes supported by this tag:
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Description</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">name</td>
 * <td>The name of the font. This can be one of the reserved font names
 * supported by every JVM or the name of any font available on the system.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">size</td>
 * <td>The size of the font in point.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">bold</td>
 * <td>A boolean flag indicating whether the font should have the style
 * <em>Bold</em>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">italic</td>
 * <td>A boolean flag indicating whether the font should have the style
 * <em>Italic</em>.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">var</td>
 * <td>Here the name of a variable can be specified. If this attribute is set,
 * the newly created font is stored in the Jelly context under this name. It can
 * then be referenced from other tags defining graphical components.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">attributes</td>
 * <td>Using this attribute a map can be referenced which defines additional
 * properties of the font to be created. A bean with this name is looked up in
 * the current bean context. It must be a {@code Map<?, ?>}. The properties
 * defined by this map are evaluated by the {@link ComponentManager}.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Tags of this type can be placed in the body of a tag derived from
 * {@link ComponentBaseTag}. In this case, the font is passed to the enclosing
 * tag. Otherwise, the {@code var} attribute must be specified. If a tag is
 * neither nested in a {@link ComponentBaseTag} tag nor has the {@code var}
 * attribute set, an exception is thrown.
 * </p>
 * <p>
 * The following example shows how this tag can be used to set the font of a
 * label component:
 *
 * <pre>
 * &lt;!-- A map with additional properties --&gt;
 * &lt;di:map name="fontProperties"&gt;
 *   ...
 * &lt;/di:map&gt;
 *
 * &lt;!-- A label with a special font --&gt;
 * &lt;f:label text="Text in a special font"&gt;
 *   &lt;f:font name="Serif" bold="true" italic="true" size=18"
 *     attributes="fontProperties"/&gt;
 * &lt;/f:label&gt;
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: FontTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class FontTag extends FormBaseTag
{
    /** A map with additional attributes of the font. */
    private Map<?, ?> attributesMap;

    /** The font name. */
    private String name;

    /** The variable name. */
    private String var;

    /** The reference to the map with additional properties. */
    private String attributes;

    /** The font size. */
    private int size;

    /** The bold flag. */
    private boolean bold;

    /** The italic flag. */
    private boolean italic;

    /**
     * Returns the name of the font.
     *
     * @return the font name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set method of the {@code name} attribute.
     *
     * @param name the attribute's value
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns the name of the variable under which the font is to be stored in
     * the Jelly context.
     *
     * @return the variable name
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
     * Returns the name of a bean with additional attributes for the font to be
     * created.
     *
     * @return the name of the bean with additional attributes
     */
    public String getAttributes()
    {
        return attributes;
    }

    /**
     * Set method of the {@code attributes} attribute.
     *
     * @param attributes the attribute's value
     */
    public void setAttributes(String attributes)
    {
        this.attributes = attributes;
    }

    /**
     * Returns the size of the font.
     *
     * @return the font size
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Set method of the {@code size} attribute.
     *
     * @param size the attribute's value
     */
    public void setSize(int size)
    {
        this.size = size;
    }

    /**
     * Returns a flag whether the font has the bold style.
     *
     * @return the bold flag
     */
    public boolean isBold()
    {
        return bold;
    }

    /**
     * Set method of the {@code bold} attribute.
     *
     * @param bold the attribute's value
     */
    public void setBold(boolean bold)
    {
        this.bold = bold;
    }

    /**
     * Returns a flag whether the font has the italic style.
     *
     * @return the italic flag
     */
    public boolean isItalic()
    {
        return italic;
    }

    /**
     * Set method of the {@code italic} attribute.
     *
     * @param italic the attribute's value
     */
    public void setItalic(boolean italic)
    {
        this.italic = italic;
    }

    /**
     * Returns a (unmodifiable) map with additional attributes of the font to be
     * created. This map is created during processing of the tag. It has to be
     * evaluated by the {@link ComponentManager} when the font is to be created.
     *
     * @return a map with additional attributes for the font to be created
     */
    public Map<?, ?> getAttributesMap()
    {
        return attributesMap;
    }

    /**
     * Performs processing before this tag's body is evaluated. This
     * implementation performs some checks of the attributes and obtains the map
     * with additional font attributes.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void processBeforeBody() throws JellyTagException,
            FormBuilderException
    {
        checkAttributes();
        attributesMap = Collections.unmodifiableMap(fetchAttributesMap());
    }

    /**
     * Processes this tag. This implementation delegates to the
     * {@link ComponentManager} to actually create the font. Then the new font
     * object is passed to the corresponding receivers.
     *
     * @throws JellyTagException if the tag is used incorrectly
     * @throws FormBuilderException if an error occurs
     */
    @Override
    protected void process() throws JellyTagException, FormBuilderException
    {
        Object font = createFont(getBuilderData().getComponentManager());
        storeFont(font);
    }

    /**
     * Obtains the map with the font's attributes. This method is called before
     * the tag's body is processed. It must return a non-<b>null</b> map. This
     * implementation checks whether the {@code attributes} attribute is
     * provided. If this is the case, the corresponding map bean is retrieved
     * (an exception is thrown if this fails). Otherwise, an empty map is
     * returned.
     *
     * @return the map with attributes of the font
     * @throws JellyTagException if the map cannot be retrieved
     */
    protected Map<?, ?> fetchAttributesMap() throws JellyTagException
    {
        if (getAttributes() == null)
        {
            return Collections.emptyMap();
        }

        BeanContext bc = getBuilderData().getBeanContext();
        if (!bc.containsBean(getAttributes()))
        {
            throw new JellyTagException(
                    "Map with attributes cannot be resolved under the name "
                            + getAttributes());
        }

        Object bean = bc.getBean(getAttributes());
        if (!(bean instanceof Map<?, ?>))
        {
            throw new JellyTagException("Bean is not a map: " + getAttributes());
        }

        Map<?, ?> map = (Map<?, ?>) bean;
        return new HashMap<Object, Object>(map);
    }

    /**
     * Calls the {@link ComponentManager} to create the font represented by this
     * tag. This method is called by {@link #process()}.
     *
     * @param manager the {@code ComponentManager}
     * @return the font object created by this method
     * @throws FormBuilderException if an error occurs
     */
    protected Object createFont(ComponentManager manager)
            throws FormBuilderException
    {
        return manager.createFont(this);
    }

    /**
     * Checks this tag's attributes and throws an exception if they are invalid.
     *
     * @throws JellyTagException if invalid or missing attributes are detected
     */
    private void checkAttributes() throws JellyTagException
    {
        if (getVar() == null && !(getParent() instanceof ComponentBaseTag))
        {
            throw new JellyTagException("No target for font available! "
                    + "Set the var attribute or place the tag in the body "
                    + "of a ComponentBaseTag.");
        }
    }

    /**
     * Stores the newly created font. This method passes the font to all
     * receivers.
     *
     * @param font the font
     */
    private void storeFont(Object font)
    {
        if (getVar() != null)
        {
            getContext().setVariable(getVar(), font);
        }

        if (getParent() instanceof ComponentBaseTag)
        {
            ((ComponentBaseTag) getParent()).setFont(font);
        }
    }
}
