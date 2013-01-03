/*
 * Copyright 2006-2012 The JGUIraffe Team.
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
package net.sf.jguiraffe.transform;

/**
 * <p>
 * Definition of an interface describing a validation message.
 * </p>
 * <p>
 * If a {@link Validator} cannot successfully validate an input field, it
 * creates a {@link ValidationResult} object that not only indicates the
 * validation error, but also contains corresponding error messages to be
 * displayed to the user. These error messages are represented by this
 * interface.
 * </p>
 * <p>
 * The idea behind this interface is that there can be multiple ways of
 * obtaining the error message for the user. For instance it can be directly
 * specified as text or loaded from the application's resources. By making use
 * of an interface the actual source of the message is transparent for the
 * client.
 * </p>
 * <p>
 * A validation message consists of a text that can be directly displayed to the
 * user. This text should be informative, so that the user knows what is wrong
 * and how it can be fixed. The default validators shipped with this framework
 * are able to provide meaningful error messages. In addition to the text, a
 * validation message also contains a unique key. The existing validator
 * implementations document the error keys they can produce. Further, a level
 * for the severity of the message can be specified. The level determines
 * whether the message is considered an error or not.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationMessage.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ValidationMessage
{
    /**
     * Returns the key of this validation message. Validation messages have a
     * unique key. (This key can for instance be used for changing the text of a
     * specific validation message.)
     *
     * @return the key of this validation message
     */
    String getKey();

    /**
     * Returns the {@code ValidationMessageLevel} associated with this message.
     * This level determines the severity of this message.
     *
     * @return the {@code ValidationMessageLevel}
     */
    ValidationMessageLevel getLevel();

    /**
     * Returns the actual message. This is a text that can be directly displayed
     * to the user.
     *
     * @return the error message in plain text
     */
    String getMessage();
}
