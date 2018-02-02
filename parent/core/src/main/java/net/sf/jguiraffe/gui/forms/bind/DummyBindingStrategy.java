/*
 * Copyright 2006-2018 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.forms.bind;

import net.sf.jguiraffe.gui.forms.BindingStrategy;

/**
 * <p>
 * A dummy implementation of the {@code BindingStrategy} interface.
 * </p>
 * <p>
 * This strategy can be used when no specific (functional) {@code
 * BindingStrategy} implementation is available or is needed (for instance as an
 * application of the <em>null object</em> pattern). All methods are implemented
 * as dummies that do not provide any specific functionality - refer to the
 * documentation of the single methods for more details.
 * </p>
 * <p>
 * It is not possible to create instances of this class. Instead the static
 * {@code INSTANCE} field can be used. This instance can be shared between
 * multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: DummyBindingStrategy.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class DummyBindingStrategy implements BindingStrategy
{
    /**
     * Constant for the shared instance of this class that can be used.
     */
    public static final DummyBindingStrategy INSTANCE = new DummyBindingStrategy();

    /**
     * Private constructor so no instances can be created.
     */
    private DummyBindingStrategy()
    {
    }

    /**
     * Reads a property from the given model object. This is just a dummy
     * implementation that returns always <b>null</b>.
     *
     * @param model the model object
     * @param propertyName the name of the property
     * @return the value of this property
     */
    public Object readProperty(Object model, String propertyName)
    {
        return null;
    }

    /**
     * Writes a property of the given model object. This is just a dummy
     * implementation. No property is actually written.
     *
     * @param model the model object
     * @param propertyName the name of the property
     * @param value the value of the property
     */
    public void writeProperty(Object model, String propertyName, Object value)
    {
    }
}
