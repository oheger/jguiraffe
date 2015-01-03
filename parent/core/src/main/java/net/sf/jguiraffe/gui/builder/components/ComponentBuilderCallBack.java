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
package net.sf.jguiraffe.gui.builder.components;

/**
 * <p>
 * An interface for describing call back operations that can be registered at
 * the central builder data object.
 * </p>
 * <p>
 * Constructing a user GUI and an accompaning
 * {@link net.sf.jguiraffe.gui.forms.Form Form} object is a complex
 * and non linear process. For instance it can happen that a component is
 * created that must be connected to another component, which has not yet been
 * created (e.g. a label that is associated with another GUI widget). Such
 * references cannot savely be resolved before the building process has
 * finished.
 * </p>
 * <p>
 * This interface provides a solution to the mentioned problem. It allows
 * interested objects to register themselves as call backs at the builder data
 * object. These call backs are executed when the building process ends. At this
 * time all GUI components have been created and are accessable through the
 * builder data object. So the registered objects should be able to resolve all
 * valid references to components created during the building operation.
 * </p>
 * <p>
 * But implementations of this interface are not limited to resolving
 * references. They can perform arbitrary actions that need to be executed after
 * the building process has been completed.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ComponentBuilderCallBack.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ComponentBuilderCallBack
{
    /**
     * Executes the call back. In this method implementing classes can pack
     * arbitrary logic that should be executed after the building process has
     * been completed. The passed in parameters can be used to access all
     * information gathered at the building process. If an implementation throws
     * an exception, this exception is passed through to the caller of the
     * building operation.
     *
     * @param builderData the builder data object of the current building
     *        process
     * @param params an arbitrary parameter object that has been registered with
     *        the call back object
     * @throws FormBuilderException an implementation can throw this exception
     *         to indicate that the building process should fail
     */
    void callBack(ComponentBuilderData builderData, Object params)
            throws FormBuilderException;
}
