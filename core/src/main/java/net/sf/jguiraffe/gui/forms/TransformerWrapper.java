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
 * Definition of an interface for objects that wrap a transformer.
 * </p>
 * <p>
 * The main task of a <code>TransformerWrapper</code> is to provide a
 * specialized
 * <code>{@link net.sf.jguiraffe.transform.TransformerContext TransformerContext}</code>
 * for a <code>{@link net.sf.jguiraffe.transform.Transformer Transformer}</code>.
 * A client can use the wrapper exactly as a normal transformer, but does not
 * have to care for the context. Occurring exceptions when invoking the
 * transformer must also be caught and redirected as runtime exceptions.
 * </p>
 * <p>
 * This simplifies working with transformers and also makes it possible to
 * inject alternative context implementations.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: TransformerWrapper.java 205 2012-01-29 18:29:57Z oheger $
 * @see net.sf.jguiraffe.transform.Transformer
 */
public interface TransformerWrapper
{
    /**
     * Invokes the wrapped transformer for transforming the passed in object.
     * For a client of this interface this method call should almost be
     * equivalent with calling a transformer directly (with the exception that
     * no checked exception is thrown). It is up to a concrete implementation
     * how this call is delegated to a real transformer.
     *
     * @param o the object to be transformed
     * @return the result of the transformation
     */
    Object transform(Object o);
}
