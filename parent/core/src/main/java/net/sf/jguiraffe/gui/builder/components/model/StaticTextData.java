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
package net.sf.jguiraffe.gui.builder.components.model;


/**
 * <p>
 * Definition of an interface for describing the properties of a <em>static
 * text</em> element.
 * </p>
 * <p>
 * A <em>static text</em> is an element that appears like a label, but can be
 * changed after the GUI was created. For this purpose the
 * {@link net.sf.jguiraffe.gui.builder.components.ComponentHandler
 * ComponentHandler} of the static text element is used. It maintains a data
 * object of the type of this interface. The methods defined by this interface
 * can then be used to query the current properties of the static text or modify
 * them.
 * </p>
 * <p>
 * Note: static text elements are created by the
 * {@link net.sf.jguiraffe.gui.builder.components.tags.StaticTextTag
 * StaticTextTag} tag handler class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: StaticTextData.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface StaticTextData
{
    /**
     * Returns the text of the affected element.
     *
     * @return the text (can be <b>null</b> if there is no text)
     */
    String getText();

    /**
     * Sets the text of the affected element.
     *
     * @param s the new text
     */
    void setText(String s);

    /**
     * Returns the icon of the affected element.
     *
     * @return the icon (can be <b>null</b> if none was defined)
     */
    Object getIcon();

    /**
     * Sets the icon of the affected element. The passed in object must be a
     * valid icon that is compatible with the used GUI library. It may have been
     * created using the
     * {@link net.sf.jguiraffe.gui.builder.components.tags.IconTag IconTag} tag
     * handler class for instance.
     *
     * @param icon the icon
     */
    void setIcon(Object icon);

    /**
     * Returns the alignment of the text and the icon.
     *
     * @return the alignment
     */
    TextIconAlignment getAlignment();

    /**
     * Sets the alignment of the text and the icon.
     *
     * @param alignment the new alignment (must not be <b>null</b>)
     */
    void setAlignment(TextIconAlignment alignment);
}
