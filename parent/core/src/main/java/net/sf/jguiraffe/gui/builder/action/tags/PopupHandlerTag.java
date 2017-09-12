/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action.tags;

import net.sf.jguiraffe.gui.builder.action.ActionManager;
import net.sf.jguiraffe.gui.builder.action.PopupMenuHandler;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.ComponentBuilderData;
import net.sf.jguiraffe.gui.builder.components.FormBuilderException;
import net.sf.jguiraffe.gui.builder.components.tags.ComponentBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;

import org.apache.commons.jelly.JellyTagException;

/**
 * <p>
 * A tag handler class that allows registering a handler for a popup menu at a
 * component.
 * </p>
 * <p>
 * Tags of this type can be placed in the body of arbitrary component tags (i.e.
 * tags derived from <code>{@link ComponentBaseTag}</code>). They fetch the
 * <code>{@link PopupMenuHandler}</code> specified by the attributes and call
 * the <code>{@link ActionManager}</code>'s
 * <code>{@link ActionManager#registerPopupMenuHandler(Object, PopupMenuHandler)}</code>
 * method. (Implementation note: This is done at the end of the builder
 * operation because it cannot be guaranteed that the component is fully
 * initialized at an earlier point of time.)
 * </p>
 * <p>
 * By inheriting from <code>{@link UseBeanBaseTag}</code> the typical ways of
 * defining beans are available; especially the dependency injection framework
 * can be used. Refer to the documentation of the base class for a full list of
 * supported configuration options.
 * </p>
 * <p>
 * The following example fragment shows how this tag can be used for assigning a
 * context menu to a tree component. We expect that the bean for the context
 * menu handler is defined somewhere else:
 *
 * <pre>
 * &lt;f:tree name=&quot;myTree&quot; model=&quot;treeModel&quot;&gt;
 *   &lt;a:popup beanName=&quot;myPopupHandler&quot;/&gt;
 * &lt;/f:tree&gt;
 * </pre>
 *
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: PopupHandlerTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PopupHandlerTag extends UseBeanBaseTag
{
    /**
     * Creates a new instance of <code>PopupHandlerTag</code>.
     */
    public PopupHandlerTag()
    {
        super(null, PopupMenuHandler.class);
    }

    /**
     * Processes the bean managed by this tag. This implementation checks
     * whether the parent tag is derived from
     * <code>{@link ComponentBaseTag}</code>. If this is the case, a call back
     * object will be created, which registers the handler bean at the component
     * managed by the parent tag.
     *
     * @param bean the menu handler bean
     * @return a flag whether the bean could be processed
     * @throws JellyTagException if the tag is incorrectly used
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        if (!(getParent() instanceof ComponentBaseTag))
        {
            return false;
        }

        ComponentBuilderCallBack callBack = createCallBack(
                (ComponentBaseTag) getParent(), (PopupMenuHandler) bean);
        ActionBaseTag.getBuilderData(getContext()).addCallBack(callBack, null);
        return true;
    }

    /**
     * Creates the call back object for actually registering the popup menu
     * handler. This object will be added at the
     * <code>{@link ComponentBuilderData}</code> object to be invoked at the end
     * of the builder operation.
     *
     * @param componentTag the parent component tag managing the component where
     *        to register the handler
     * @param handler the handler
     * @return the call back, which performs the registration
     */
    protected ComponentBuilderCallBack createCallBack(
            ComponentBaseTag componentTag, PopupMenuHandler handler)
    {
        return new RegisterPopupHandlerCallBack(ActionBaseTag
                .getActionManager(getContext()), componentTag, handler);
    }

    /**
     * A call back class that actually calls the action manager to register the
     * menu handler.
     */
    private static class RegisterPopupHandlerCallBack implements
            ComponentBuilderCallBack
    {
        /** The action manager. */
        private final ActionManager actionManager;

        /** The component tag for the affected component. */
        private final ComponentBaseTag compTag;

        /** The handler to be registered. */
        private final PopupMenuHandler handler;

        /**
         * Creates a new instance of <code>RegisterPopupHandlerCallBack</code>
         * and initializes the member fields.
         *
         * @param actMan a reference to the current action manager
         * @param tag the component tag with the affected component
         * @param hndlr the menu handler
         */
        public RegisterPopupHandlerCallBack(ActionManager actMan,
                ComponentBaseTag tag, PopupMenuHandler hndlr)
        {
            actionManager = actMan;
            compTag = tag;
            handler = hndlr;
        }

        /**
         * This method gets called at the end of the builder operation. It
         * invokes the action manager to actually register the menu handler.
         *
         * @param builderData the component builder data object
         * @param params additional parameters
         * @throws FormBuilderException if an error occurs
         */
        public void callBack(ComponentBuilderData builderData, Object params)
                throws FormBuilderException
        {
            actionManager.registerPopupMenuHandler(compTag.getComponent(),
                    handler, builderData);
        }
    }
}
