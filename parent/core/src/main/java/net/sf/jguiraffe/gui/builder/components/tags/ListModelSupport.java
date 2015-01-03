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
package net.sf.jguiraffe.gui.builder.components.tags;

import net.sf.jguiraffe.gui.builder.components.model.ListModel;

import org.apache.commons.jelly.JellyContext;

/**
 * <p>
 * Definition of an interface for tags that support a list model.
 * </p>
 * <p>
 * This interface will be implemented by tag handler classes that create
 * components like list boxes or combo boxes that are associated with a
 * {@link ListModel}. Other tags that create such models will look for enclosing
 * tags implementing this interface to set the newly created models.
 * </p>
 * <p>
 * This interface is also used for resolving the list model and looking it up in
 * a {@code BeanContext}. Therefore there are some other methods for querying
 * information related to the list model.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: ListModelSupport.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface ListModelSupport
{
    /**
     * Returns the current {@code ListModel}. This method can return <b>null</b>
     * if no model has been set so far.
     *
     * @return the current {@code ListModel}
     */
    ListModel getListModel();

    /**
     * Sets the {@code ListModel}.
     *
     * @param model the {@code ListModel} to be set
     */
    void setListModel(ListModel model);

    /**
     * Returns the reference name of the list model. This name is used for
     * looking up the {@code ListModel} in the current {@code BeanContext}.
     *
     * @return the name of the bean representing the list model
     */
    String getModelRef();

    /**
     * Returns the current Jelly context. This object is needed for performing a
     * lookup to find the list model object.
     *
     * @return the current Jelly context
     */
    JellyContext getContext();
}
