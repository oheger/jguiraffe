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
package net.sf.jguiraffe.gui.builder.event;

import java.util.EventListener;

/**
 * <p>
 * Base interface for listeners for form events.
 * </p>
 * <p>
 * The form and form builder framework defines a generic listener mechanism for
 * reacting on events fired by components. This is analogous (and indeed very
 * similar) to other typical GUI libraries like Swing or SWT, but independent on
 * a specific library.
 * </p>
 * <p>
 * This interface is only a marker interface without own method definitions. It
 * will be sub classed by all other listener interfaces dealing with form
 * events.
 * </p>
 * @see FormEvent
 *
 * @author Oliver Heger
 * @version $Id: FormEventListener.java 205 2012-01-29 18:29:57Z oheger $
 */
public interface FormEventListener extends EventListener
{
}
