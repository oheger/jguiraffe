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
 * An enumeration class that defines possible levels for validation messages.
 * </p>
 * <p>
 * Messages produced by {@link Validator} implementations do not necessarily
 * indicate fatal errors. It is also possible to issue warnings to the user.
 * This enumeration class defines levels for validation messages supported by
 * this library.
 * </p>
 * <p>
 * Only the level {@code ERROR} represents a real validation error. If an input
 * component contains a {@link ValidationMessage} with the level {@code ERROR},
 * it is considered invalid, and the associated form cannot be closed using the
 * OK button. Other levels are only informative and do not have any real
 * consequences.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationMessageLevel.java 205 2012-01-29 18:29:57Z oheger $
 */
public enum ValidationMessageLevel
{
    /**
     * The validation message level <em>ERROR</em>. This level indicates a real
     * validation error. Messages with this level cause input elements to be
     * considered invalid.
     */
    ERROR,

    /**
     * The validation message level <em>WARNING</em>. This level can be used if
     * user input violates a recommendation, but nevertheless can be accepted.
     */
    WARNING
}
