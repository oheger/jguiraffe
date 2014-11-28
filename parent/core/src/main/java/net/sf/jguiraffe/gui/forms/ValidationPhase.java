/*
 * Copyright 2006-2014 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms;

/**
 * <p>
 * An enumeration class that describes the different validation phases.
 * </p>
 * An instance of this class is passed to the
 * {@link FieldHandler#validate(ValidationPhase)} method. This
 * method can then determine, which validation to perform.
 * </p>
 * <p>
 * The form framework distinguishes between validation on the field and the form
 * layer. The field layer deals with syntactic checks. Here the user input is
 * verified to match the expected data type, e.g. is the text entered by the
 * user a valid number or a valid date? The form layer is more about semantic.
 * After the user's input has been transformed into the target data types
 * constraints for the single fields are checked, e.g. is the number between 0
 * and 100 or is the date in the future.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationPhase.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum ValidationPhase
{
    /** The validation phase &quot;field&quot;. */
    SYNTAX,

    /** The validation phase &quot;form&quot;. */
    LOGIC;
}
