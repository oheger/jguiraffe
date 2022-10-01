/*
 * Copyright 2006-2022 The JGUIraffe Team.
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

import net.sf.jguiraffe.gui.app.ApplicationBuilderData;
import net.sf.jguiraffe.gui.app.OpenWindowCommand;
import net.sf.jguiraffe.locators.ClassPathLocator;
import net.sf.jguiraffe.locators.Locator;

/**
 * <p>
 * A specialized command class for opening the dialog with the view settings.
 * </p>
 * <p>
 * The major part of the work is done by the super class {@code
 * OpenWindowCommand}. This class ensures that all required helper object (e.g.
 * models for lists) are passed to the builder.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: OpenViewSettingsDlgCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class OpenViewSettingsDlgCommand extends OpenWindowCommand
{
    /** Constant for the name of the builder script to be executed. */
    private static final String SCRIPT = "viewsettings.jelly";

    /** Constant for the locator to the script to be executed. */
    private static final Locator SCRIPT_LOCATOR = ClassPathLocator
            .getInstance(SCRIPT);

    /**
     * Creates a new instance of {@code OpenViewSettingsDlgCmd} and initializes
     * the super class with the locator of the script to be processed.
     */
    public OpenViewSettingsDlgCommand()
    {
        super(SCRIPT_LOCATOR);
    }

    /**
     * Prepares the {@code ApplicationBuilderData} object before the builder is
     * called. This implementation creates model objects required by elements in
     * the builder script and adds them to the builder data as additional
     * properties. Especially, the {@code ViewSettings} object to be edited is
     * fetched from the application context and passed to the builder data.
     *
     * @param builderData the {@code ApplicationBuilderData} object
     */
    @Override
    protected void prepareBuilderData(ApplicationBuilderData builderData)
    {
        ColorListModel colorListModel = new ColorListModel(getApplication()
                .getApplicationContext());
        builderData.addProperty(ColorListModel.CTX_NAME, colorListModel);
        builderData.addProperty(ViewSettings.CTX_NAME, getApplication()
                .getApplicationContext().getTypedProperty(ViewSettings.class));
    }
}
