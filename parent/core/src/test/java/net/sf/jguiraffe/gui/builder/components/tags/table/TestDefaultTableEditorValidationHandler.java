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
package net.sf.jguiraffe.gui.builder.components.tags.table;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import javax.swing.JTable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContextImpl;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.Form;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.gui.forms.TransformerContextImpl;
import net.sf.jguiraffe.gui.forms.bind.BeanBindingStrategy;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class TestDefaultTableEditorValidationHandler
{
    /** Constant for the used default locale. */
    private static final Locale DEF_LOCALE = Locale.ENGLISH;

    /** Constant for the table object to be passed to the handler. */
    private static final JTable TABLE = new JTable();

    /** Constant for the name of the resource group. */
    private static final String RES_GROUP = "testformbuilderresources";

    /** Constant for the prefix of a validation error message. */
    private static final String VAL_MSG = "Validation error ";

    /** Constant for the name of the error field. */
    private static final String FLD_NAME = "errorField";

    /** Constant for the display name of the error field. */
    private static final String FLD_DISPNAME = "Error field";

    /** Constant for the title of the validation error message box. */
    private static final String MSG_TITLE = "Validation error";

    /** Constant for the field name separator. */
    private static final String FLD_NAMESEP = ": ";

    /** Constant for the validation message separator. */
    private static final String VAL_MSGSEP = "\n";

    /** Constant for the number of validation errors. */
    private static final int ERR_COUNT = 3;

    /** Stores the handler under test. */
    private DefaultTableEditorValidationHandler handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new DefaultTableEditorValidationHandler();
    }

    /**
     * Initializes an application object for testing. The resource manager is
     * also initialized.
     *
     * @return the application object
     */
    private Application setUpApplication()
    {
        Application app = new Application();
        ApplicationContextImpl ctx = new ApplicationContextImpl(DEF_LOCALE);
        app.setApplicationContext(ctx);
        ctx.setResourceManager(new ResourceManagerImpl(
                new BundleResourceLoader()));
        ctx.getResourceManager().setDefaultResourceGroup(RES_GROUP);
        Window wnd = EasyMock.createMock(Window.class);
        EasyMock.replay(wnd);
        ctx.setMainWindow(wnd);
        return app;
    }

    /**
     * Tests whether the application reference is correctly set.
     */
    @Test
    public void testSetApplication()
    {
        Application app = setUpApplication();
        MessageOutput out = EasyMock.createMock(MessageOutput.class);
        app.getApplicationContext().setMessageOutput(out);
        handler.setApplication(app);
        assertSame("Application was not set", app, handler.getApplication());
        assertSame("Output object was not set", out, handler.getOutput());
    }

    /**
     * Tests that the message output object is not overridden by
     * setApplication() if it already exists.
     */
    @Test
    public void testSetApplicationMessageOutputAvailable()
    {
        MessageOutput output = EasyMock.createMock(MessageOutput.class);
        Application app = setUpApplication();

        handler.setOutput(output);
        handler.setApplication(app);
        assertSame("Application was not set", app, handler.getApplication());
        assertSame("Output object was not set", output, handler.getOutput());
    }

    /**
     * Tests a call to validationPerformed() if there are some validation
     * errors.
     */
    @Test
    public void testValidationPerformedErrors() throws Exception
    {
        MessageOutput out = EasyMock.createMock(MessageOutput.class);
        Application app = setUpApplication();
        app.getApplicationContext().setMessageOutput(out);
        TableTag tt = new TableTag();
        tt.setValidationErrorCaption(MSG_TITLE);
        EasyMock.expect(
                out.show(app.getApplicationContext().getMainWindow(),
                        errorMessage(), MSG_TITLE, MessageOutput.MESSAGE_ERROR,
                        MessageOutput.BTN_OK)).andReturn(MessageOutput.RET_OK);
        EasyMock.replay(out);
        Form editForm =
                new Form(new TransformerContextImpl(),
                        new BeanBindingStrategy());
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setDisplayName(FLD_DISPNAME);
        editForm.addField(FLD_NAME, fh);
        handler.setApplication(app);
        FormValidatorResults vres = setUpFormValidationResults();
        assertFalse("Wrong return value",
                handler.validationPerformed(TABLE, editForm, tt, vres, 0, 0));
        EasyMock.verify(out);
        verifyResults(vres);
    }

    /**
     * Tests the validationPerformed() method if there are no validation errors.
     * In this case nothing should be done.
     */
    @Test
    public void testValidationPerformedNoErrors() throws Exception
    {
        MessageOutput out = EasyMock.createMock(MessageOutput.class);
        EasyMock.replay(out);
        Application app = setUpApplication();
        app.getApplicationContext().setMessageOutput(out);
        TableTag tt = new TableTag();
        handler.setApplication(app);
        handler.validationPerformed(TABLE, tt.getRowEditForm(), tt,
                new DefaultFormValidatorResults(
                        new HashMap<String, ValidationResult>()), 0, 0);
        EasyMock.verify(out);
    }

    /**
     * Tests the validationPerformed() method if no output object is set. In
     * this case no action needs to be performed. We can here only check that no
     * exception is thrown.
     */
    @Test
    public void testValidationPerformedNoOutput() throws Exception
    {
        TableTag tt = new TableTag();
        handler.validationPerformed(TABLE, tt.getRowEditForm(), tt,
                setUpFormValidationResults(), 0, 0);
    }

    /**
     * Tests the validationPerformed() method if not application object is set.
     * In this case no action needs to be performed even if an output object is
     * set and validation errors occurred.
     */
    @Test
    public void testValidationPerformedNoApplication() throws Exception
    {
        MessageOutput out = EasyMock.createMock(MessageOutput.class);
        EasyMock.replay(out);
        handler.setOutput(out);
        TableTag tt = new TableTag();
        handler.validationPerformed(TABLE, tt.getRowEditForm(), tt,
                setUpFormValidationResults(), 0, 0);
        EasyMock.verify(out);
    }

    /**
     * Returns the error message text that corresponds to the test validation
     * results object.
     *
     * @return the test error message
     */
    private String errorMessage()
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 1; i <= ERR_COUNT; i++)
        {
            if (i > 1)
            {
                buf.append(VAL_MSGSEP);
            }
            buf.append(FLD_DISPNAME);
            buf.append(FLD_NAMESEP);
            buf.append(VAL_MSG).append(i);
        }
        return buf.toString();
    }

    /**
     * Creates a form validation results object containing a field that caused
     * validation errors. This object can be used for testing the display of
     * error messages.
     *
     * @return the validation result object
     */
    private DefaultFormValidatorResults setUpFormValidationResults()
    {
        DefaultValidationResult.Builder builder =
                new DefaultValidationResult.Builder();
        for (int i = 1; i <= ERR_COUNT; i++)
        {
            ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
            EasyMock.expect(vm.getMessage()).andReturn(VAL_MSG + i);
            EasyMock.expect(vm.getLevel())
                    .andReturn(ValidationMessageLevel.ERROR).anyTimes();
            EasyMock.replay(vm);
            builder.addValidationMessage(vm);
        }
        return new DefaultFormValidatorResults(Collections.singletonMap(
                FLD_NAME, builder.build()));
    }

    /**
     * Validates the mock objects in the given validation results object.
     *
     * @param vres the results object to be verified
     */
    private void verifyResults(FormValidatorResults vres)
    {
        ValidationResult vr = vres.getResultsFor(FLD_NAME);
        EasyMock.verify((Object[]) vr.getValidationMessages().toArray());
    }
}
