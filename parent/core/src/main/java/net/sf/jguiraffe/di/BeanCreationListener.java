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
package net.sf.jguiraffe.di;

import java.util.EventListener;

/**
 * <p>
 * Definition of an interface for objects that are interested in the creation of
 * beans by a {@link BeanContext}.
 * </p>
 * <p>
 * Objects implementing this interface can be registered at a {@code
 * BeanContext} as <em>bean creation listeners</em>. They will then be notified
 * whenever a new bean is created by the context. This is a very powerful means
 * of intercepting the bean creation process. For instance, a listener can
 * perform enhanced initialization on certain types of beans and inject property
 * values that are known at runtime only.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanCreationListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanCreationListener extends EventListener
{
    /**
     * Notifies this listener about the creation of a new bean. The passed in
     * event object contains all information available about the new bean.
     *
     * @param event the event with information about the bean creation
     */
    void beanCreated(BeanCreationEvent event);
}
