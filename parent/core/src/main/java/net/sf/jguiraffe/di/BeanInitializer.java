/*
 * Copyright 2006-2022 The JGUIraffe Team.
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
 * Definition of an interface for objects that perform some kind of
 * initialization on beans that are managed by a
 * <code>{@link BeanContext}</code>.
 * </p>
 * <p>
 * The <code>{@link DependencyProvider}</code> interface allows adding an
 * arbitrary number of objects implementing this interface. These objects will
 * then be invoked after all beans affected by the current transaction have been
 * created. This way certain cyclic dependencies can be resolved.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: BeanInitializer.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface BeanInitializer
{
    /**
     * Performs the desired initialization. It is up to a concrete
     * implementation, which actions are performed here. When this method is
     * called all beans affected by the current transaction have been created
     * (if possible). Some of them might not have been initialized yet. Note
     * that this method is called in any case, even if the current transaction
     * fails for some reason.
     *
     * @param dependencyProvider the dependency provider
     */
    void initialize(DependencyProvider dependencyProvider);
}
