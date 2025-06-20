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
package net.sf.jguiraffe.gui.platform.swing.dlg.filechooser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.dlg.filechooser.FileChooserDialogService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@code SwingFileChooserDialogServiceFactory}.
 */
public class TestSwingFileChooserDialogServiceFactory
{
    /** A mock for the application context. */
    private ApplicationContext applicationContext;

    /** The factory to be tested. */
    private SwingFileChooserDialogServiceFactory factory;

    @Before
    public void setUp()
    {
        applicationContext = EasyMock.createMock(ApplicationContext.class);
        EasyMock.replay(applicationContext);
        factory = new SwingFileChooserDialogServiceFactory();
    }

    /**
     * Tests the service factory method.
     */
    @Test
    public void testNewServiceInstanceIsCreated()
    {
        FileChooserDialogService service =
                factory.createService(applicationContext);
        assertThat("Wrong service instance", service,
                instanceOf(SwingFileChooserDialogService.class));

        SwingFileChooserDialogService swingService =
                (SwingFileChooserDialogService) service;
        assertEquals("Wrong application context", applicationContext,
                swingService.getApplicationContext());
    }
}
