/*
 * Copyright 2006-2021 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.layout.CellAlignment;
import net.sf.jguiraffe.gui.layout.CellConstraints;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * A concrete cell constraints tag implementation.
 * </p>
 * <p>
 * This tag adds row constraints to a percent layout.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PercentRowConstraintsTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PercentRowConstraintsTag extends PercentCellConstraintsTag
{
    /** Constant for the name of the constraints builder in the context. */
    private static final String BUILDER = PercentCellConstraintsTag.class
            .getName()
            + ".rowConstraintsBuilder";

    /**
     * Creates the constraints object. This implementation creates a constraints
     * object for rows.
     *
     * @return the constraints
     */
    @Override
    protected CellConstraints createConstraints()
    {
        CellConstraints.Builder builder = getRowConstraintsBuilder(getContext());
        return builder.fromString(getConstr());
    }

    /**
     * Adds the new constraints to the percent layout tag.
     *
     * @param parent the percent layout tag
     * @param c the constraints
     */
    @Override
    protected void addConstraints(PercentLayoutTag parent, CellConstraints c)
    {
        parent.addRowConstraints(c);
    }

    /**
     * Returns the shared builder for row constraints from the given Jelly
     * context.
     *
     * @param context the Jelly context
     * @return the builder for {@code CellConstraints} for rows
     * @see PercentCellConstraintsTag#getConstraintsBuilder(JellyContext,
     *      String, CellAlignment)
     */
    protected static CellConstraints.Builder getRowConstraintsBuilder(
            JellyContext context)
    {
        return getConstraintsBuilder(context, BUILDER, CellAlignment.CENTER);
    }
}
