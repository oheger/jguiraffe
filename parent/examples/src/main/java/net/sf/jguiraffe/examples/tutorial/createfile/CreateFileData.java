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
package net.sf.jguiraffe.examples.tutorial.createfile;

/**
 * <p>
 * A data class that stores all information for the creation of a new file.
 * </p>
 * <p>
 * This class acts as the <em>form bean</em> for the dialog for creating a new
 * file. This is a pretty simple dialog: It allows the user to specify a file
 * name and the content of the file. This bean class defines corresponding
 * properties. It is populated from the dialog when the user clicks the OK
 * button.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: CreateFileData.java 205 2012-01-29 18:29:57Z oheger $
 */
public class CreateFileData
{
    /** The file name. */
    private String fileName;

    /** The file content. */
    private String fileContent;

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileContent()
    {
        return fileContent;
    }

    public void setFileContent(String fileContent)
    {
        this.fileContent = fileContent;
    }
}
