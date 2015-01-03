/*
 * Copyright 2006-2015 The JGUIraffe Team.
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

import net.sf.jguiraffe.transform.ValidationResult;

/**
 * <p>
 * Definition of an interface for objects that wrap a
 * <code>{@link net.sf.jguiraffe.transform.Validator Validator}</code>.
 * </p>
 * <p>
 * This interface is analogous to the <code>{@link TransformerWrapper}</code>
 * interface, but it operates on validators.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidatorWrapper.java 205 2012-01-29 18:29:57Z oheger $
 * @see net.sf.jguiraffe.transform.Validator
 * @see TransformerWrapper
 */
public interface ValidatorWrapper
{
    /**
     * Validates the specified object.
     *
     * @param o the object to validate
     * @return an object with information about the results of the validation
     */
    ValidationResult isValid(Object o);
}
