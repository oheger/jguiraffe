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
package net.sf.jguiraffe.gui.builder.components.model;

/**
 * <p>
 * An enumeration class that defines the alignment of the text and the icon of a
 * label or button-like component.
 * </p>
 * <p>
 * This enumeration type is always used to define alignments of GUI controls
 * that support both a text and an icon. The names of the defined alginment
 * constants can also be passed to the <code>alignment</code> property of the
 * tags for those components.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TextIconAlignment.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum TextIconAlignment
{
    /**
     * Constant for the alignment LEFT: The icon is left to the text. This is
     * the default alignment.
     */
    LEFT,

    /** Constant for the alignment CENTER: The icon is centered.*/
    CENTER,

    /** Constant for the alignment RIGHT: The icon is right to the text. */
    RIGHT
}
