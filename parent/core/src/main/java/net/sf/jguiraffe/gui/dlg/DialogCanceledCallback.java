/*
 * Copyright 2006-2021 The JGUIraffe Team.
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
 * A callback interface to notify a client that the user has canceled a standard
 * dialog.
 * </p>
 * <p>
 * When working with standard dialogs client code can optionally provide an
 * object implementing this interface. This single method defined here is called
 * when the dialog is not closed via the OK button, but canceled. It is passed a
 * client-specific data object that can contain some context information, so
 * that it can react properly on the cancellation of the dialog.
 * </p>
 *
 * @param <D> the type of the data object associated with this callback
 * @since 1.4
 */
public interface DialogCanceledCallback<D>
{
    /**
     * Notifies this object that the associated standard dialog did not produce
     * a result, but was canceled by the user.
     *
     * @param data client-specific data for this callback
     */
    void onDialogCanceled(D data);
}
