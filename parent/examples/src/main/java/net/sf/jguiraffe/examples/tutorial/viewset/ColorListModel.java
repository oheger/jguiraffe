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
package net.sf.jguiraffe.examples.tutorial.viewset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.builder.components.Color;
import net.sf.jguiraffe.gui.builder.components.ColorHelper;
import net.sf.jguiraffe.gui.builder.components.ColorHelper.NamedColor;
import net.sf.jguiraffe.gui.builder.components.model.ListModel;

/**
 * <p>
 * A {@code ListModel} implementation for combo boxes that allow the user to
 * choose a color.
 * </p>
 * <p>
 * This model contains all default colors. For the default colors there is an
 * enumeration class in the {@code ColorHelper} class. The display names are
 * resolved from resources; the name of the resource ID is derived from the name
 * of the color.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ColorListModel.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ColorListModel implements ListModel
{
    /**
     * Constant for the name under which this model is stored in the builder
     * context.
     */
    public static final String CTX_NAME = "colorListModel";

    /** Constant for the prefix of the resource IDs for colors. */
    private static final String RESID_PREFIX = "viewset_color_";

    /** A list with the names of the colors to be displayed to the user. */
    private final List<String> colorNames;

    /** A list with the color objects. */
    private final List<Color> colors;

    /**
     * Creates a new instance of {@code ColorListModel}. The model is fully
     * initialized.
     *
     * @param appCtx the {@code ApplicationContext}
     */
    public ColorListModel(ApplicationContext appCtx)
    {
        // resolve the resources for all colors and store them in a sorted map
        Map<String, Color> colorMap = new TreeMap<String, Color>();
        for (NamedColor nc : ColorHelper.NamedColor.values())
        {
            String colName = appCtx.getResourceText(RESID_PREFIX + nc.name());
            colorMap.put(colName, nc.getColor());
        }

        // create the lists storing the data of the model
        colorNames = new ArrayList<String>(colorMap.keySet());
        colors = new ArrayList<Color>(colorNames.size());
        for (String name : colorNames)
        {
            colors.add(colorMap.get(name));
        }
    }

    /**
     * Returns the object to be displayed for the model element with the given
     * index.
     *
     * @param index the index
     * @return the display object for this index
     */
    @Override
    public Object getDisplayObject(int index)
    {
        return colorNames.get(index);
    }

    /**
     * Returns the type of this model. This is {@code Color}.
     *
     * @return the type of this model
     */
    @Override
    public Class<?> getType()
    {
        return Color.class;
    }

    /**
     * Returns the model object at the given index.
     *
     * @param index the index
     * @return the model object at this index
     */
    @Override
    public Object getValueObject(int index)
    {
        return colors.get(index);
    }

    /**
     * Returns the size of this model.
     *
     * @return the size of this model
     */
    @Override
    public int size()
    {
        return colors.size();
    }
}
