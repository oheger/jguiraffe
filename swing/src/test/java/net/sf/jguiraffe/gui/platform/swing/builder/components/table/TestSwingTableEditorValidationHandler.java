/*
 * Copyright 2006-2014 The JGUIraffe Team.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JTable;

import net.sf.jguiraffe.gui.app.Application;
import net.sf.jguiraffe.gui.app.ApplicationContextImpl;
import net.sf.jguiraffe.gui.builder.components.tags.table.TableTag;
import net.sf.jguiraffe.gui.builder.utils.MessageOutput;
import net.sf.jguiraffe.gui.builder.window.Window;
import net.sf.jguiraffe.gui.forms.DefaultFieldHandler;
import net.sf.jguiraffe.gui.forms.DefaultFormValidatorResults;
import net.sf.jguiraffe.gui.forms.FormValidatorResults;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;
import net.sf.jguiraffe.transform.DefaultValidationResult;
import net.sf.jguiraffe.transform.ValidationMessage;
import net.sf.jguiraffe.transform.ValidationMessageLevel;
import net.sf.jguiraffe.transform.ValidationResult;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for SwingTableEditorValidationHandler.
 *
 * @author Oliver Heger
 * @version $Id: TestSwingTableEditorValidationHandler.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestSwingTableEditorValidationHandler extends
        AbstractTableModelTest
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

    /** Stores the application object needed by the handler. */
    private Application application;

    /** Stores the handler under test. */
    private SwingTableEditorValidationHandler handler;

    @Before
    public void setUp() throws Exception
    {
        handler = new SwingTableEditorValidationHandler();
    }

    /**
     * Initializes an application object for testing. The resource manager is
     * also initialized.
     *
     * @return the application object
     */
    private Application setUpApplication()
    {
        if (application == null)
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
            application = app;
        }
        return application;
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
     * Tests whether an output object can be explicitly set.
     */
    @Test
    public void testSetOutput()
    {
        MessageOutput out1 = EasyMock.createMock(MessageOutput.class);
        handler.setOutput(out1);
        assertSame("Output was not set", out1, handler.getOutput());
        Application app = setUpApplication();
        MessageOutput out2 = EasyMock.createMock(MessageOutput.class);
        app.getApplicationContext().setMessageOutput(out2);
        handler.setApplication(app);
        assertSame("Output object from application not set", out2, handler
                .getOutput());
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
        TableTag tt = setUpTableTag(null);
        tt.setValidationErrorCaption(MSG_TITLE);
        EasyMock.expect(
                out.show(app.getApplicationContext().getMainWindow(),
                        errorMessage(), MSG_TITLE, MessageOutput.MESSAGE_ERROR,
                        MessageOutput.BTN_OK)).andReturn(MessageOutput.RET_OK);
        EasyMock.replay(out);
        DefaultFieldHandler fh = new DefaultFieldHandler();
        fh.setDisplayName(FLD_DISPNAME);
        tt.getRowEditForm().addField(FLD_NAME, fh);
        handler.setApplication(app);
        FormValidatorResults vres = setUpFormValidationResults();
        assertFalse("Wrong return value", handler.validationPerformed(TABLE, tt
                .getRowEditForm(), tt, vres, 0, 0));
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
        TableTag tt = setUpTableTag(null);
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
        TableTag tt = setUpTableTag(null);
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
        TableTag tt = setUpTableTag(null);
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
        DefaultValidationResult.Builder builder = new DefaultValidationResult.Builder();
        for (int i = 1; i <= ERR_COUNT; i++)
        {
            ValidationMessage vm = EasyMock.createMock(ValidationMessage.class);
            EasyMock.expect(vm.getMessage()).andReturn(VAL_MSG + i);
            EasyMock.expect(vm.getLevel()).andReturn(
                    ValidationMessageLevel.ERROR).anyTimes();
            EasyMock.replay(vm);
            builder.addValidationMessage(vm);
        }
        return new DefaultFormValidatorResults(Collections.singletonMap(FLD_NAME, builder.build()));
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
