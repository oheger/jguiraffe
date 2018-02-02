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
package net.sf.jguiraffe.gui.layout;

/**
 * <p>
 * An enumeration class that defines the possible alignments of components in
 * the cells of a <em>percent layout</em>.
 * </p>
 * <p>
 * The alignment determines how components are positioned in the cell(s) they
 * are contained. For instance, they can be configured to fill the whole cell.
 * If the size of the hosting container changes, the sizes of the components are
 * adapted to fit into the new cell size. It is also possible to specify that
 * components should keep their original size. Then they can be aligned at the
 * start of the cell, the end of the cell, or its center.
 * </p>
 * <p>
 * The alignment constants defined here work for both columns and rows.
 * Therefore generic names like {@code START} or {@code END} are used rather
 * than column- or row-specific terms like <em>left</em>, <em>right</em>, or
 * <em>top</em>. Nevertheless, the meaning should be obvious. The
 * {@link CellConstraints} class defines a property of this class.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CellAlignment.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum CellAlignment
{
    /**
     * The alignment <em>start</em>. This alignment means that components are
     * positioned at the beginning of the cell they belong to, i.e. at the left
     * side for columns and at the top for rows.
     */
    START,

    /**
     * The alignment <em>center</em>. This alignment means that components are
     * centered in the cell they belong to (either in X or in Y direction,
     * depending on the type of constraint).
     */
    CENTER,

    /**
     * The alignment <em>end</em>. This alignment means that components are
     * positioned at the end of the cell they belong to, i.e. at the right side
     * for columns and at the bottom for rows.
     */
    END,

    /**
     * The alignment <em>full</em>. This alignment means that components always
     * fill the whole cell they belong to (either in X or in Y direction,
     * depending on the type of constraint). If the size of the cell in the
     * corresponding direction changes, the size of the component is adjusted,
     * too.
     */
    FULL
}
