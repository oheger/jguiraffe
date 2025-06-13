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
package net.sf.jguiraffe.gui.builder.di.tags;

import org.apache.commons.jelly.TagLibrary;

/**
 * <p>The tag library for the tags of the dependency injection framework.</p>
 *
 * @author Oliver Heger
 * @version $Id: DITagLibrary.java 207 2012-02-09 07:30:13Z oheger $
 */
public class DITagLibrary extends TagLibrary
{
    /**
     * Creates a new instance of {@code DITagLibrary} and registers the
     * supported tags.
     */
    public DITagLibrary()
    {
        registerTag("bean", BeanTag.class);
        registerTag("const", ConstantValueTag.class);
        registerTag("constructor", ConstructorTag.class);
        registerTag("constructorInvocation", ConstructorInvocationTag.class);
        registerTag("contextBean", ContextBeanTag.class);
        registerTag("element", ElementTag.class);
        registerTag("entry", EntryTag.class);
        registerTag("entryKey", EntryKeyTag.class);
        registerTag("factory", FactoryTag.class);
        registerTag("invocationTarget", InvocationTargetTag.class);
        registerTag("list", ListTag.class);
        registerTag("map", MapTag.class);
        registerTag("methodInvocation", MethodInvocationTag.class);
        registerTag("null", NullTag.class);
        registerTag("param", ParameterTag.class);
        registerTag("properties", PropertiesTag.class);
        registerTag("set", SetTag.class);
        registerTag("setProperty", SetPropertyTag.class);
        registerTag("shutdown", ShutdownHandlerTag.class);
        registerTag("store", BeanStoreTag.class);
        registerTag("value", ValueTag.class);
        registerTag("resource", ResourceTag.class);
    }
}
