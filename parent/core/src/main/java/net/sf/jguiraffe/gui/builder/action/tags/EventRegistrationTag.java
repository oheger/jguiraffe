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
package net.sf.jguiraffe.gui.builder.action.tags;

import java.util.EventListener;

import net.sf.jguiraffe.gui.builder.components.ComponentBuilderCallBack;
import net.sf.jguiraffe.gui.builder.components.tags.FormBaseTag;
import net.sf.jguiraffe.gui.builder.components.tags.InputComponentTag;
import net.sf.jguiraffe.gui.builder.components.tags.UseBeanBaseTag;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * <p>
 * A tag handler class for registering an event listener at a single or multiple
 * components.
 * </p>
 * <p>
 * This tag handler class has a similar purpose as {@link EventListenerTag}.
 * However, it uses a different approach for connecting an event listener with a
 * component. While {@link EventListenerTag} and its sub classes allow
 * connecting actions to standard component events (e.g. action or change
 * events), this class operates on plain event listener implementations. It can
 * also be used for working with non-standard event listeners, i.e. event
 * listener interfaces defined in the {@code
 * net.sf.jguiraffe.gui.builder.components.model} package that do not extend the
 * {@code FormEventListener} interface or even 3rd party or custom event
 * listener interfaces.
 * </p>
 * <p>
 * This tag handler class extends {@code UseBeanBaseTag} and thus allows the
 * definition of a bean class in multiple ways. The bean specified using the
 * tag's attributes must implement the appropriate event listener interface.
 * With the mandatory {@code type} attribute the event listener type must be
 * specified. This can be either one of the standard types ({@code ACTION},
 * {@code CHANGE}, or {@code FOCUS}) or a non-standard event listener type. In
 * the latter case the type name is derived from the name of the method for
 * adding event listeners. As an example take the
 * {@link net.sf.jguiraffe.gui.builder.components.model.TreeHandler} interface
 * that defines the {@code addExpansionListener()} method for registering an
 * event listener to be notified when a node of the tree is expanded or
 * collapsed. The corresponding event type is <em>Expansion</em>, i.e. the
 * string between <em>add</em> and <em>Listener</em>.
 * </p>
 * <p>
 * The component(s) the event listener should be registered at can be specified
 * in multiple ways:
 * <ul>
 * <li>This tag can be placed in the body of a tag defining a component. Then it
 * registers the listener at the component handler of this component.</li>
 * <li>The name of a single component can be specified in the {@code component}
 * attribute. The listener will be registered at this component's {@code
 * ComponentHandler}.</li>
 * <li>Event handlers may also be registered at arbitrary beans accessible
 * through the current {@code BeanContext}. To do this the {@code targetBean}
 * attribute must be specified. The tag obtains this bean from the context and
 * calls the appropriate {@code add()} method for adding the handler. Note that
 * if the tag is used in this way really all beans of the current bean context
 * can be accessed, e.g. the platform-specific components or the current window.
 * </li>
 * <li>It is also possible to register a listener at all components that support
 * the specified event type. In this case the {@code multiple} attribute must be
 * set to <b>true</b>, and no component name or bean name must be specified.
 * (The additional {@code multiple} attribute was introduced to provide a better
 * error diagnosis: If the {@code component} attribute is omitted accidently, an
 * exception is thrown rather than silently registering the listener at all
 * components.)</li>
 * </ul>
 * </p>
 * <p>
 * The following table lists the attributes supported by this tag handler class.
 * Of course, all attributes defined by the {@link UseBeanBaseTag} class for
 * specifying the bean are also supported.
 * </p>
 * <table border="1">
 * <tr>
 * <th>Attribute</th>
 * <th>Meaning</th>
 * <th>Optional</th>
 * </tr>
 * <tr>
 * <td valign="top">component</td>
 * <td>The name of the component the event listener is to be registered at.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">targetBean</td>
 * <td>With this attribute an arbitrary object available in the current {@code
 * BeanContext} can be selected to which the event listener is added. This is a
 * more generic form than using the {@code component} attribute. The bean
 * specified here does not need to implement the {@code ComponentHandler}
 * interface; as the event listener is added through reflection, it can be an
 * arbitrary object defining an appropriate {@code addXXXListener()} method.
 * <strong>Note:</strong> This attribute should not be used for registering
 * standard event handlers (<em>ACTION</em>, <em>FOCUS</em>, and <em>CHANGE</em>
 * ).</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">multiple</td>
 * <td>If this attribute is specified (with an arbitrary value), the event
 * listener will be registered at all compatible components. In this case the
 * {@code component} attribute must be empty.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * <tr>
 * <td valign="top">eventType</td>
 * <td>Determines the type of the event listener. The bean specified by this tag
 * must implement a compatible event listener interface.</td>
 * <td valign="top">No</td>
 * </tr>
 * <tr>
 * <td valign="top">ignoreFail</td>
 * <td>With this attribute error handling can be controlled. Per default, the
 * tag causes an error if no event listener can be registered. This can have
 * various reasons, e.g. the name of the component is wrong, there is a typo in
 * the event listener type, or there is no compatible component supporting the
 * listener type. In most cases it is preferable to throw an exception in such a
 * situation instead of ignoring the problem silently. There may be use cases
 * though, where it is considered normal behavior that no fitting component for
 * registering an event listener can be found. In this cases the {@code
 * ignoreFail} attribute should be set - it can have an arbitrary value. If it
 * is present, the tag will not throw an exception if the registration of the
 * event listener is not possible.</td>
 * <td valign="top">Yes</td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * The following fragment shows an example how this tag can be used for
 * registering a listener for tree expansion events:
 *
 * <pre>
 * &lt;f:tree name=&quot;myTree&quot; model=&quot;treeModel&quot;&gt;
 * &lt;/f:tree&gt;
 * &lt;!-- The event listener bean --&gt;
 * &lt;di:bean name=&quot;treeListener&quot;
 *   beanClass=&quot;com.mypackage.MyTreeListenerClass&quot;&gt;
 * &lt;/di:bean&gt;
 * &lt;!-- Register the event listener --&gt;
 * &lt;a:eventListener component=&quot;myTree&quot; eventType=&quot;Expansion&quot;
 *   beanName=&quot;treeListener&quot;/&gt;
 * </pre>
 *
 * </p>
 * <p>
 * Note that it does not matter where in the builder script the event
 * registration tags are placed as the actual registration is performed after
 * all components have been created.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: EventRegistrationTag.java 205 2012-01-29 18:29:57Z oheger $
 */
public class EventRegistrationTag extends UseBeanBaseTag
{
    /** Constant for the component attribute. */
    private static final String ATTR_COMPONENT = "component";

    /** Constant for the targetBean attribute. */
    private static final String ATTR_TARGET_BEAN = "targetBean";

    /** Constant for the multiple attribute. */
    private static final String ATTR_MULTIPLE = "multiple";

    /** Constant for the event type attribute. */
    private static final String ATTR_EVENT_TYPE = "eventType";

    /** Constant for the ignore fail attribute. */
    private static final String ATTR_IGNORE_FAIL = "ignoreFail";

    /**
     * Creates a new instance of {@code EventRegistrationTag}.
     */
    public EventRegistrationTag()
    {
        super(null, EventListener.class);
        addIgnoreProperties(ATTR_COMPONENT, ATTR_MULTIPLE, ATTR_EVENT_TYPE,
                ATTR_IGNORE_FAIL, ATTR_TARGET_BEAN);
    }

    /**
     * Performs the registration of the event listener. The actual registration
     * is done by a callback object at the very end of the builder script. This
     * ensures that all components have already been created.
     *
     * @param bean the bean object
     * @return a flag whether the event listener could be registered
     * @throws JellyTagException if an error occurs
     */
    @Override
    protected boolean passResults(Object bean) throws JellyTagException
    {
        String eventType = getAttributeStr(ATTR_EVENT_TYPE);
        if (eventType == null)
        {
            throw new MissingAttributeException(ATTR_EVENT_TYPE);
        }

        String beanName = null;
        String componentName = null;
        if (getAttributes().get(ATTR_MULTIPLE) != null)
        {
            if (getAttributeStr(ATTR_COMPONENT) != null
                    || getAttributeStr(ATTR_TARGET_BEAN) != null)
            {
                throw new JellyTagException(
                        "A component or bean name must not be specified "
                                + "if the multiple attribute is set!");
            }
        }

        else if (getAttributeStr(ATTR_TARGET_BEAN) != null)
        {
            if (getAttributeStr(ATTR_COMPONENT) != null)
            {
                throw new JellyTagException(
                        "Cannot specify both a component name and "
                                + "a bean name!");
            }
            beanName = getAttributeStr(ATTR_TARGET_BEAN);
        }

        else
        {
            componentName = getAttributeStr(ATTR_COMPONENT);
            if (componentName == null)
            {
                if (getParent() instanceof InputComponentTag)
                {
                    componentName = ((InputComponentTag) getParent()).getName();
                }
            }
            if (componentName == null)
            {
                return false; // cannot pass listener to a component
            }
        }

        EventListener listener = (EventListener) bean;
        boolean ignoreErr = getAttributes().get(ATTR_IGNORE_FAIL) != null;
        ComponentBuilderCallBack cb = (beanName != null) ? new BeanRegistrationCallBack(
                beanName, eventType, listener, ignoreErr)
                : new ComponentRegistrationCallBack(componentName, eventType,
                        listener, ignoreErr);
        FormBaseTag.getBuilderData(getContext()).addCallBack(cb, null);
        return true;
    }
}
