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
 * Definition of the <code>Transformer</code> interface.
 * </p>
 * <p>
 * A <code>Transformer</code> is an object that converts a given object into a
 * different format or type. It is completely up to a concrete implementation
 * how this conversion works. An example would be a formatter object that
 * creates formatted string representations for objects. The other direction
 * (from a formatted user input to a specific Java class) could also be done by
 * a transformer.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Transformer.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface Transformer
{
    /**
     * The main method of the <code>Transformer</code> interface. This method
     * takes the object to be transformed and returns an appropriate converted
     * representation of it. The also passed in <code>TransformerContext</code>
     * object can be used to access system information that may be needed for
     * generating the transformed representation, e.g. the actual
     * <code>Locale</code>.
     *
     * @param o the object to be transformed
     * @param ctx the <code>TransformerContext</code> object
     * @return the transformed instance
     * @throws Exception Transformers can throw arbitrary exceptions if the
     * conversion is not possible
     */
    Object transform(Object o, TransformerContext ctx) throws Exception;
}
