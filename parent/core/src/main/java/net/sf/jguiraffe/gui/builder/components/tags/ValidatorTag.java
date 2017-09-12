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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.transform.Validator;

/**
 * <p>
 * A tag handler class for creating
 * <code>{@link sun.security.validator.Validator Validator}</code> objects and
 * associating them with input components.
 * </p>
 * <p>
 * The class of the validator can be defined either as a class name using the
 * <code>class</code> attribute or by referencing an existing validator using
 * the <code>ref</code> attribute. The <code>phase</code> attributes
 * determines whether the validator should be used during field validation or
 * form validation phase. Here a name from the
 * <code>{@link net.sf.jguiraffe.gui.forms.ValidationPhase ValidationPhase}</code>
 * class can be specified.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidatorTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class ValidatorTag extends ValidatorBaseTag<Validator>
{
}
