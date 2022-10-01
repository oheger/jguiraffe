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
package net.sf.jguiraffe.gui.platform.swing.builder.components.table;

import net.sf.jguiraffe.gui.app.ApplicationClient;
import net.sf.jguiraffe.gui.builder.components.tags.table.DefaultTableEditorValidationHandler;

/**
 * <p>
 * A Swing-specific default implementation of the
 * {@code TableEditorValidationHandler} interface.
 * </p>
 *
 * @deprecated The functionality provided by this class was not Swing-specific,
 *             but could be used for other UI tool kits as well. Therefore, it
 *             has been extracted into the new
 *             {@link DefaultTableEditorValidationHandler} class; this class
 *             should be used instead.
 * @author Oliver Heger
 * @version $Id: SwingTableEditorValidationHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class SwingTableEditorValidationHandler extends
        DefaultTableEditorValidationHandler implements ApplicationClient
{
}
