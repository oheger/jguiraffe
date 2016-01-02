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
package net.sf.jguiraffe.gui.builder.window.tags;

import org.apache.commons.jelly.TagLibrary;

/**
 * <p>The tag library with the window builder tags.</p>
 *
 * @author Oliver Heger
 * @version $Id: WindowBuilderTagLibrary.java 205 2012-01-29 18:29:57Z oheger $
 */
public class WindowBuilderTagLibrary extends TagLibrary
{
    /**
     * Creates a new instance of <code>WindowBuilderTagLibrary</code>.
     */
    public WindowBuilderTagLibrary()
    {
        super();
        registerTag("controller", WindowControllerTag.class);
        registerTag("dialog", DialogTag.class);
        registerTag("formController", FormControllerTag.class);
        registerTag("formControllerListener", FormControllerListenerTag.class);
        registerTag("frame", FrameTag.class);
        registerTag("iframe", InternalFrameTag.class);
        registerTag("windowEvent", WindowListenerTag.class);
    }
}
