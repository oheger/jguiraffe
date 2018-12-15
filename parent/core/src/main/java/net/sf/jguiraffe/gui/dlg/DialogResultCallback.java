/*
 * Copyright 2006-2018 The JGUIraffe Team.
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

/**
 * <p>
 * A callback interface for handling the result of a standard dialog.
 * </p>
 * <p>
 * When using one of the supported standard dialogs client code must provide an
 * object implementing this interface. The single callback method defined here
 * is invoked when the dialog was closed successfully. It is passed the result
 * of the dialog to be further processed together with a client-specific context
 * object. The latter can contain additional information required to process the
 * dialog result.
 * </p>
 *
 * @param <T> the type of the result produced by the standard dialog
 * @param <D> the type of the data object expected by this callback
 * @since 1.4
 */
public interface DialogResultCallback<T, D>
{
    /**
     * Callback method for processing a standard dialog result. This method is
     * called (in the event dispatch thread) with the dialog-specific result.
     *
     * @param result the result from the standard dialog
     * @param data client-specific data for this callback
     */
    void onDialogResult(T result, D data);
}
