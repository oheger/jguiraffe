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
package net.sf.jguiraffe.gui.app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import net.sf.jguiraffe.resources.Message;
import net.sf.jguiraffe.resources.ResourceManager;
import net.sf.jguiraffe.resources.impl.ResourceManagerImpl;
import net.sf.jguiraffe.resources.impl.bundle.BundleResourceLoader;

import org.junit.Test;

/**
 * Test class for {@code ApplicationResources}.
 *
 * @author Oliver Heger
 * @version $Id: TestApplicationResources.java 205 2012-01-29 18:29:57Z oheger $
 */
public class TestApplicationResources
{
    /**
     * Tests whether the resource ID can be extracted from a literal.
     */
    @Test
    public void testResourceID()
    {
        Object resID = ApplicationResources
                .resourceID(ApplicationResources.Keys.EXIT_PROMPT_MSG);
        assertEquals("Wrong resID", "EXIT_PROMPT_MSG", resID);
    }

    /**
     * Tries to pass a null resource ID. This should cause an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testResourceIDNull()
    {
        ApplicationResources.resourceID(null);
    }

    /**
     * Tests whether a Message can be created for a literal.
     */
    @Test
    public void testMessage()
    {
        Message msg = ApplicationResources
                .message(ApplicationResources.Keys.EXIT_PROMPT_MSG);
        assertEquals("Wrong group", "application", msg.getResourceGroup());
        assertEquals("Wrong resource ID", "EXIT_PROMPT_MSG", msg
                .getResourceKey());
        assertEquals("Got parameters", 0, msg.getParameters().length);
    }

    /**
     * Tries to pass a null literal to message(). This should cause an
     * exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMessageNull()
    {
        ApplicationResources.message(null);
    }

    /**
     * Tests whether there are application resource keys at all.
     */
    @Test
    public void testKeysAvailable()
    {
        assertTrue("No resource keys found",
                ApplicationResources.Keys.values().length > 0);
    }

    /**
     * Tests whether values for the specified locale are defined for all
     * resource keys.
     *
     * @param loc the locale
     */
    private void checkKeys(Locale loc)
    {
        ResourceManager rm =
                new ResourceManagerImpl(new BundleResourceLoader());
        for (ApplicationResources.Keys key : ApplicationResources.Keys.values())
        {
            assertNotNull("No text for key " + key, rm.getText(loc,
                    ApplicationResources.APPLICATION_RESOURCE_GROUP,
                    ApplicationResources.resourceID(key)));
        }
    }

    /**
     * Tests whether application resources are available in English.
     */
    @Test
    public void testKeysLocaleEnglish()
    {
        checkKeys(Locale.ENGLISH);
    }

    /**
     * Tests whether application resources are available in German.
     */
    @Test
    public void testKeysLocaleGerman()
    {
        checkKeys(Locale.GERMAN);
    }
}
