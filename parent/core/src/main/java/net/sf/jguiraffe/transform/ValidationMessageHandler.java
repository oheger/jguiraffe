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
package net.sf.jguiraffe.transform;

/**
 * <p>
 * Definition of an interface for a central instance that manages validation
 * messages.
 * </p>
 * <p>
 * This interface is used by concrete <code>{@link Validator}</code>
 * implementations to obtain meaningful error messages when they decide that
 * user input is not valid. An object implementing this interface can be queried
 * from the <code>{@link TransformerContext}</code>. It can then be used for
 * requesting <code>{@link ValidationMessage}</code> objects for specified
 * keys.
 * </p>
 * <p>
 * Because all validation error messages are obtained through this interface, it
 * provides a way for hooking into the mechanism of creating error messages.
 * While the framework ships a fully functional default implementation, an
 * application with completely different requirements can choose to use a custom
 * implementation.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ValidationMessageHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ValidationMessageHandler
{
    /**
     * Returns a <code>ValidationMessage</code> object for the specified key.
     * The message will be initialized with the given parameters. From this
     * object the final error message can be obtained.
     *
     * @param context the transformer context
     * @param key the key for the validation message
     * @param params an array with additional parameters
     * @return the corresponding <code>ValidationMessage</code> object
     */
    ValidationMessage getValidationMessage(TransformerContext context,
            String key, Object... params);
}
