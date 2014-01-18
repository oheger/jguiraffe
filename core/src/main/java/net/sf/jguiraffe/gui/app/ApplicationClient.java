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
package net.sf.jguiraffe.gui.app;

/**
 * <p>
 * Definition of an interface for objects that need a reference to the central
 * {@link Application} object.
 * </p>
 * <p>
 * This interface can be implemented by objects that are automatically created
 * by the dependency injection framework during the initialization phase of an
 * application. It indicates that the implementing object needs access to the
 * global {@link Application} object. The framework will recognize this and pass
 * a reference to the {@link Application} to the object after its creation.
 * </p>
 *
 * @see Application
 * @author Oliver Heger
 * @version $Id: ApplicationClient.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ApplicationClient
{
    /**
     * Sets a reference to the global {@code Application} object. This method is
     * automatically invoked by the framework.
     *
     * @param app the reference to the global application
     */
    void setApplication(Application app);
}
