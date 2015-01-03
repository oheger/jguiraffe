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
package net.sf.jguiraffe.di;

/**
 * <p>
 * Definition of an interface to be implemented by beans that need a reference
 * to the current {@link BeanContext}.
 * </p>
 * <p>
 * Sometimes beans defined using the <em>dependency injection framework</em>
 * also want to use the framework for accessing other beans through a
 * {@link BeanContext}. This is often the case if the dependencies of a bean are
 * not fully known at design time, but are determined based on certain criteria
 * at runtime. In such cases a reference to the current {@link BeanContext} must
 * somehow be passed to the affected beans.
 * </p>
 * <p>
 * This interface marks a bean as a client of a {@link BeanContext}. It defines
 * a single method, through which the {@link BeanContext} can be passed to the
 * object. The dependency injection framework checks for each bean that is newly
 * created whether it implements this interface. If this is the case, the
 * {@code setBeanContext()} method is automatically called. Thus the
 * {@link BeanContext} becomes known to the bean and can be used later for
 * accessing other dependencies. No additional configuration steps are required
 * to use this feature. Every bean implementing this interface is automatically
 * initialized with the current {@link BeanContext}.
 * </p>
 * <p>
 * One big advantage of dependency injection is that it tends to be
 * non-invasive: the beans managed by the framework need not implement specific
 * interfaces or extend certain base classes; the framework handles their
 * creation and initialization transparently. By implementing this interface
 * beans define a direct dependency to this framework, which makes it harder to
 * use them in a different context. So before making use of this feature
 * possible alternatives should be evaluated, or at least the developer should
 * be aware that a tight coupling to this specific dependency injection
 * framework is introduced.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanContextClient.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanContextClient
{
    /**
     * Passes the current {@code BeanContext} to this object. This method is
     * invoked by the framework after initialization of this object is complete.
     * At this time all dependencies to other beans have been resolved, but
     * {@link BeanCreationListener}s registered at the context have not yet been
     * invoked. An implementation will typical store the passed in {@code
     * BeanContext} for later use.
     *
     * @param context the current {@code BeanContext}
     */
    void setBeanContext(BeanContext context);
}
