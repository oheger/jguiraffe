/*
 * Copyright 2006-2025 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.layout.CellGroup;

/**
 * <p>
 * A concrete cell group tag that creates a column group for a percent layout.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentColGroupTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentColGroupTag extends PercentCellGroupTag
{
    /**
     * Adds the specified group object as a column group to the given percent
     * layout tag.
     *
     * @param parent the percent layout tag
     * @param g the new cell group
     */
    protected void addGroup(PercentLayoutTag parent, CellGroup g)
    {
        parent.addColGroup(g);
    }
}
