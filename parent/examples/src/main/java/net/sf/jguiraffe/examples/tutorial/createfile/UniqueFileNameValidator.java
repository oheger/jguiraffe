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
package net.sf.jguiraffe.examples.tutorial.createfile;

import net.sf.jguiraffe.examples.tutorial.model.DirectoryData;
import net.sf.jguiraffe.examples.tutorial.model.FileData;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.TransformerContext;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationResult;
import net.sf.jguiraffe.transform.Validator;

/**
 * <p>
 * A specialized {@link Validator} implementations for checking whether the name
 * entered for a new file does not exist in the current directory.
 * </p>
 * <p>
 * In the validation method this class obtains the data object with the content
 * of the current directory (which is available as a typed property from the
 * context). Then it checks whether the name entered by the user already exists
 * in this directory. If this is the case, validation fails and an error message
 * is produced.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: UniqueFileNameValidator.java 205 2012-01-29 18:29:57Z oheger $
 */
public class UniqueFileNameValidator implements Validator
{
    /** Constant for the error message produced by this validator. */
    public static final String ERR_FILE_EXISTS = "ERR_FILE_EXISTS";

    /**
     * Performs the validation.
     *
     * @param o the object to be validated (this is the file name; <b>null</b>
     *        names are accepted)
     * @param ctx the validation context
     * @return the validation results
     */
    @Override
    public ValidationResult isValid(Object o, TransformerContext ctx)
    {
        if (o != null) // null is okay
        {
            DirectoryData dirData = ctx.getTypedProperty(DirectoryData.class);
            assert dirData != null : "No current directory!";
            String fileName = o.toString();

            for (FileData fd : dirData.getContent())
            {
                if (fileName.equalsIgnoreCase(fd.getName()))
                {
                    ValidationMessage msg = ctx.getValidationMessageHandler()
                            .getValidationMessage(ctx, ERR_FILE_EXISTS,
                                    fileName);
                    return new DefaultValidationResult.Builder()
                            .addValidationMessage(msg).build();
                }
            }
        }

        return DefaultValidationResult.VALID;
    }
}
