/*
 * Copyright 2006-2025 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.dlg;

import net.sf.jguiraffe.gui.app.ApplicationContext;

/**
 * <p>
 * A factory interface for the creation of a dialog service.
 * </p>
 * <p>
 * Using this interface, instances of services for the creation of standard
 * dialogs can be created. Each UI library provides implementations of this
 * generic interface for the different dialog services it supports. These
 * implementations can be referenced via the dependency injection framework.
 * From the concrete factory instances service objects can be obtained.
 * </p>
 *
 * @param <T> the type of the service returned by the factory
 * @since 1.4
 */
public interface DialogServiceFactory<T>
{
    /**
     * Returns a new instance of the managed service. The passed in application
     * context can be used to obtain properties required by the service, e.g. to
     * get access to resources or the application's main window.
     *
     * @param applicationContext the application context
     * @return the service object
     */
    T createService(ApplicationContext applicationContext);
}
