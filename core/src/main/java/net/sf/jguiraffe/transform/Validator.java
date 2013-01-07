/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
 * Definition of a validator interface.
 * </p>
 * <p>
 * Validators are objects that are able to check if given objects are valid.
 * Often the objects to test have been obtained from user input. If validation
 * passes on these objects, this means that the user has entered correct data.
 * </p>
 * <p>
 * Implementing custom validators is very simple. There is only one main
 * validation method that has to be implemented. This method is passed the
 * object to be tested and can perform arbitrary actions to determine the
 * validity of this object. A {@link TransformerContext} object
 * is also passed, which can be used to obtain needed system information, e.g.
 * the current locale.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Validator.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Validator
{
    /**
     * Validates the passed in object. The returned result object should contain
     * all information about the validation: if the object is valid and error
     * messages if this is not the case.
     *
     * @param o the object to test
     * @param ctx the transformer context
     * @return an object with the validation results
     */
    ValidationResult isValid(Object o, TransformerContext ctx);
}
