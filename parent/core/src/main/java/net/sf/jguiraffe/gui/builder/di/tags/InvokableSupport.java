/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.di.tags;

import org.apache.commons.jelly.JellyTagException;

import net.sf.jguiraffe.di.impl.Invokable;

/**
 * <p>
 * Definition of an interface for objects that support
 * <code>{@link Invokable}</code> objects.
 * </p>
 * <p>
 * This interface is implemented by tag handler classes that create objects,
 * which can deal with <code>Invokable</code> objects. The tags that create
 * <code>Invokable</code> objects use this interface to pass their results to
 * the correct target objects.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: InvokableSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
interface InvokableSupport
{
    /**
     * Passes the specified <code>Invokable</code> object to its target. This
     * method will be called after a new Invokable object was created. Concrete
     * implementations have to decide what to do with the received object. If
     * the passed in <code>Invokable</code> object is of a different type than
     * expected, they should throw a <code>JellyTagException</code> exception
     * (because in this case there is probably an error in the Jelly
     * configuration script).
     *
     * @param inv the <code>Invokable</code> object to add
     * @throws JellyTagException if the passed in object is not acceptable for
     * the target object
     */
    void addInvokable(Invokable inv) throws JellyTagException;
}
