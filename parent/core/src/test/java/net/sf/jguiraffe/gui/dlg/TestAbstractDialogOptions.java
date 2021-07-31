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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.easymock.EasyMock;
import org.junit.Test;

import net.sf.jguiraffe.gui.app.ApplicationContext;

/**
 * Test class for {@code AbstractDialogOptions}.
 */
public class TestAbstractDialogOptions
{
    /**
     * Creates a mock for a result callback.
     *
     * @return the mock result callback
     */
    private static DialogResultCallback<Object, String> createResultCallbackMock()
    {
        @SuppressWarnings("unchecked")
        DialogResultCallback<Object, String> callback =
                EasyMock.createMock(DialogResultCallback.class);
        return callback;
    }

    /**
     * Creates a mock for a canceled callback.
     *
     * @return the mock canceled callback
     */
    private static DialogCanceledCallback<String> createCanceledCallbackMock()
    {
        @SuppressWarnings("unchecked")
        DialogCanceledCallback<String> callback =
                EasyMock.createMock(DialogCanceledCallback.class);
        return callback;
    }

    /**
     * Tries to create an instance with a null result callback.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullResultCallbackIsRejected()
    {
        new DialogOptionsTestImpl(null, 42);
    }

    /**
     * Tests whether the data object for the result callback is managed
     * correctly.
     */
    @Test
    public void testResultCallbackWithDataIsHandled()
    {
        final String data = "my callback data";
        final Object result = 42;
        DialogResultCallback<Object, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(result, data);
        EasyMock.replay(callback);
        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(callback, data);

        options.getResultCallback().onDialogResult(result, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests that a callback which does not expect a data object is handled
     * correctly.
     */
    @Test
    public void testResultCallbackWithoutDataIsHandled()
    {
        DialogResultCallback<Object, String> callback =
                createResultCallbackMock();
        callback.onDialogResult(this, null);
        EasyMock.replay(callback);
        DialogOptionsTestImpl options = new DialogOptionsTestImpl(callback);

        options.getResultCallback().onDialogResult(this, null);
        EasyMock.verify(callback);
    }

    /**
     * Tests whether a canceled callback can be set that expects a data object.
     */
    @Test
    public void testCanceledCallbackCanBeSetWithData()
    {
        final String data = "canceled callback data";
        DialogResultCallback<Object, String> resultCallback =
                createResultCallbackMock();
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallbackMock();
        canceledCallback.onDialogCanceled(data);
        EasyMock.replay(canceledCallback, resultCallback);

        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(resultCallback)
                        .setCanceledCallback(canceledCallback, data);
        Runnable cancelRun = options.getCancelInvoker();
        cancelRun.run();
        EasyMock.verify(canceledCallback);
    }

    /**
     * Tests whether a canceled callback can be set that does expect any data.
     */
    @Test
    public void testCanceledCallbackCanBeSetWithoutData()
    {
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallbackMock();
        canceledCallback.onDialogCanceled(null);
        EasyMock.replay(canceledCallback);

        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock())
                        .setCanceledCallback(canceledCallback);
        Runnable cancelRun = options.getCancelInvoker();
        cancelRun.run();
        EasyMock.verify(canceledCallback);
    }

    /**
     * Tests whether a dummy cancel invoker is returned if no canceled callback
     * has been set.
     */
    @Test
    public void testCancelInvokerIfNoCallbackHasBeenSet()
    {
        DialogResultCallback<Object, String> resultCallback =
                createResultCallbackMock();
        EasyMock.replay(resultCallback);
        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(resultCallback);

        Runnable cancelInvoker = options.getCancelInvoker();
        cancelInvoker.run();
    }

    /**
     * Tests that a null canceled callback is handled correctly.
     */
    @Test
    public void testCanceledCallbackCanBeSetToNull()
    {
        DialogCanceledCallback<String> canceledCallback =
                createCanceledCallbackMock();
        EasyMock.replay(canceledCallback);
        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock())
                        .setCanceledCallback(canceledCallback, "some data");

        options.setCanceledCallback(null, "other data");
        Runnable cancelInvoker = options.getCancelInvoker();
        cancelInvoker.run();
    }

    /**
     * Tests whether a dialog title can be set directly.
     */
    @Test
    public void testTitleCanBeSet()
    {
        final String title = "someTitle";
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.replay(appCtx);

        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock())
                        .setTitle(title);
        String resolvedTitle = options.resolveTitle(appCtx);
        assertEquals("Wrong resolved title", title, resolvedTitle);
        assertEquals("Wrong title property", title, options.getTitle());
    }

    /**
     * Tests whether a title resource can be set.
     */
    @Test
    public void testTitleCanBeSetAsResource()
    {
        final Object titleResource = "titleResource";
        final String title = "resolvedTitle";
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.expect(appCtx.getResourceText(titleResource)).andReturn(title);
        EasyMock.replay(appCtx);

        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock())
                        .setTitleResource(titleResource);
        assertEquals("Wrong resolved title", title,
                options.resolveTitle(appCtx));
        assertEquals("Wrong title resource", titleResource,
                options.getTitleResource());
    }

    /**
     * Tests whether a resolved title can be queried if none has been set.
     */
    @Test
    public void testNullResolvedTitleIsReturnedIfUnspecified()
    {
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.replay(appCtx);
        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock());

        assertNull("Got a title", options.resolveTitle(appCtx));
    }

    /**
     * Tests that a title text added later overrides a resource ID.
     */
    @Test
    public void testTitleTextOverridesResourceID()
    {
        final String title = "cool title";
        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock());

        options.setTitleResource("a resource").setTitle(title);
        assertNull("Got a resource ID", options.getTitleResource());
        assertEquals("Wrong title", title, options.getTitle());
    }

    /**
     * Tests that a title resource ID that is set later overrides a title text.
     */
    @Test
    public void testTitleResourceOverridesTitleText()
    {
        final Object titleRes = "foo";

        DialogOptionsTestImpl options =
                new DialogOptionsTestImpl(createResultCallbackMock())
                        .setTitle("bar").setTitleResource(titleRes);
        assertNull("Got a title", options.getTitle());
        assertEquals("Wrong title resource", titleRes,
                options.getTitleResource());
    }

    /**
     * A test options class.
     */
    private static class DialogOptionsTestImpl
            extends AbstractDialogOptions<Object, DialogOptionsTestImpl>
    {
        public <D> DialogOptionsTestImpl(
                DialogResultCallback<Object, D> resultCallback, D data)
        {
            super(resultCallback, data);
        }

        public DialogOptionsTestImpl(
                DialogResultCallback<Object, ?> resultCallback)
        {
            super(resultCallback);
        }

        protected DialogOptionsTestImpl getSelf()
        {
            return this;
        }
    }
}
