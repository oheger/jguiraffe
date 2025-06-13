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
package net.sf.jguiraffe.examples.tutorial.createfile;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import net.sf.jguiraffe.examples.tutorial.model.DirectoryData;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.builder.action.FormAction;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.cmd.CommandBase;

/**
 * <p>
 * A command class for creating a new file.
 * </p>
 * <p>
 * This command is associated with the OK button of the <em>create new file</em>
 * dialog. It actually creates the file. The data of the file to be created is
 * obtained from the model of the dialog which is injected. After the file was
 * created the current directory is refreshed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CreateFileCommand.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CreateFileCommand extends CommandBase
{
    /** Constant for the resource ID of the title for the error message. */
    private static final String RES_ERR_TITLE = "newfile_errmsg_tit";

    /** Constant for the resource ID of the text for the error message. */
    private static final String RES_ERR_TEXT = "newfile_errmsg_txt";

    /** Stores the directory of the new file. */
    private final File targetDirectory;

    /** The application context. */
    private final ApplicationContext appContext;

    /** The data object for the new file. */
    private final CreateFileData fileData;

    /** The refresh action. */
    private final FormAction refreshAction;

    /**
     * Creates a new instance of {@code CreateFileCommand} and initializes it
     * with the application context and the data of the file to be created.
     *
     * @param ctx the application context
     * @param cfd the data for the new file
     * @param actRefresh the refresh action
     */
    public CreateFileCommand(ApplicationContext ctx, CreateFileData cfd,
            FormAction actRefresh)
    {
        super(true);
        appContext = ctx;
        DirectoryData dd = ctx.getTypedProperty(DirectoryData.class);
        assert dd != null : "No current directory!";
        targetDirectory = dd.getDirectory();
        fileData = cfd;
        refreshAction = actRefresh;
    }

    /**
     * Executes this command. Writes the new file.
     *
     * @throws Exception in case of an error
     */
    @Override
    public void execute() throws Exception
    {
        File newFile = new File(targetDirectory, fileData.getFileName());
        getLog().info("Creating new file " + newFile.getAbsolutePath());
        PrintWriter out = new PrintWriter(new FileWriter(newFile));
        try
        {
            out.print(fileData.getFileContent());
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Updates the UI after a successful execution of this command. This
     * implementation invokes the refresh action, so that the newly created file
     * is displayed.
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
