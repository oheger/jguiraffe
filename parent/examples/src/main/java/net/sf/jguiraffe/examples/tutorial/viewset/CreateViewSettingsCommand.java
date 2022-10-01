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

import java.io.File;

import net.sf.jguiraffe.examples.tutorial.model.DirectoryData;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.cmd.CommandBase;

/**
 * <p>
 * A command class for storing view settings for a given directory.
 * </p>
 * <p>
 * A command of this type is executed when the user clicks <em>Save</em> in the
 * dialog with view settings. The command class stores the current
 * {@link ViewSettings} instance and triggers the <em>refresh</em> action.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CreateViewSettingsCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CreateViewSettingsCommand extends CommandBase
{
    /** Constant for the resource ID of the title for the error message. */
    private static final String RES_ERR_TITLE = "viewset_errmsg_tit";

    /** Constant for the resource ID of the text for the error message. */
    private static final String RES_ERR_TEXT = "viewset_errmsg_txt";

    /** Stores the directory of the view settings file. */
    private final File targetDirectory;

    /** The application context. */
    private final ApplicationContext appContext;

    /** The data object containing the view settings to be saved. */
    private final ViewSettings viewSettings;

    /** The refresh action. */
    private final FormAction refreshAction;

    public CreateViewSettingsCommand(ApplicationContext appctx,
            ViewSettings vs, FormAction actRefresh)
    {
        super(true);
        appContext = appctx;
        DirectoryData dd = appctx.getTypedProperty(DirectoryData.class);
        assert dd != null : "No current directory!";
        targetDirectory = dd.getDirectory();
        viewSettings = vs;
        refreshAction = actRefresh;
    }

    /**
     * Executes this command. Stores the current {@code ViewSettings} object in
     * the associated directory.
     *
     * @throws Exception if an error occurs
     */
    @Override
    public void execute() throws Exception
    {
        viewSettings.save(targetDirectory);
    }

    /**
     * Updates the UI after a successful execution of this command. This
     * implementation invokes the refresh action, so that the new view settings
     * become active. If an error occurred, a message box is displayed.
     */
    @Override
    protected void performGUIUpdate()
    {
        if (getException() != null)
        {
            getLog().error("Could not create file", getException());
            appContext.messageBox(RES_ERR_TEXT, RES_ERR_TITLE,
                    MessageOutput.MESSAGE_ERROR, MessageOutput.BTN_OK);
        }
        else
        {
            refreshAction.execute(null);
        }
    }
}
