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
package net.sf.jguiraffe.gui.platform.swing.dlg.filechooser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.app.ApplicationContext;
import net.sf.jguiraffe.gui.app.TextResource;
import net.sf.jguiraffe.gui.dlg.filechooser.FileExtensionFilter;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * Test class for {@code SwingFileExtensionFilter}.
 */
public class TestSwingFileExtensionFilter
{
    /**
     * Tests whether the filter description is resolved correctly.
     */
    @Test
    public void testDescriptionIsResolved()
    {
        final Object resID = "myResourceID";
        final String description = "This is my filter";
        ApplicationContext appCtx =
                EasyMock.createMock(ApplicationContext.class);
        EasyMock.expect(appCtx.getResourceText(resID)).andReturn(description);
        EasyMock.replay(appCtx);
        FileExtensionFilter filter =
                new FileExtensionFilter(TextResource.fromResourceID(resID));

        SwingFileExtensionFilter swingFilter =
                SwingFileExtensionFilter.fromExtensionFilter(filter, appCtx);
        assertEquals("Wrong description", description,
                swingFilter.getDescription());
    }

    /**
     * Tests the accept() method if the expected result is true.
     */
    @Test
    public void testAcceptTrue()
    {
        File[] files = new File[] {
                new File("image1.jpg"), new File("image2.JPG"),
                new File("image3.jpeg"), new File("image.3.jpg"),
                new File("test/imageSub.JpEg")
        };
        FileExtensionFilter filter =
                new FileExtensionFilter(TextResource.UNDEFINED, "jpg", "jpeg");

        SwingFileExtensionFilter swingFilter =
                SwingFileExtensionFilter.fromExtensionFilter(filter,
                        EasyMock.createMock(ApplicationContext.class));
        for (File file : files)
        {
            assertTrue("Not accepted: " + file, swingFilter.accept(file));
        }
    }

    /**
     * Tests the accept() method if the expected result is false.
     */
    @Test
    public void testAcceptFalse()
    {
        File[] files = new File[] {
                new File("image1.gif"), new File("image2.jpge"),
                new File("strange_image"), new File("strange2.")
        };
        FileExtensionFilter filter =
                new FileExtensionFilter(TextResource.UNDEFINED, "jpg", "jpeg");

        SwingFileExtensionFilter swingFilter =
                SwingFileExtensionFilter.fromExtensionFilter(filter,
                        EasyMock.createMock(ApplicationContext.class));
        for (File file : files)
        {
            assertFalse("Accepted: " + file, swingFilter.accept(file));
        }
    }

    /**
     * Tests whether an accepts all filter works as expected.
     */
    @Test
    public void testAcceptAll()
    {
        FileExtensionFilter filter = new FileExtensionFilter(
                TextResource.UNDEFINED, FileExtensionFilter.EXT_ALL_FILES);
        SwingFileExtensionFilter swingFilter =
                SwingFileExtensionFilter.fromExtensionFilter(filter,
                        EasyMock.createMock(ApplicationContext.class));

        assertTrue("Not accepted (1)",
                swingFilter.accept(new File("test.jpg")));
        assertTrue("Not accepted (2)", swingFilter.accept(new File("test")));
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        SwingFileExtensionFilter filter1 = new SwingFileExtensionFilter("desc",
                Collections.singleton("jpg"));
        SwingFileExtensionFilter filter2 = new SwingFileExtensionFilter(
                filter1.getDescription(), Collections.singleton("JPG"));
        JGuiraffeTestHelper.checkEquals(filter1, filter2, true);

        SwingFileExtensionFilter filter3 =
                new SwingFileExtensionFilter(null, new HashSet<String>());
        SwingFileExtensionFilter filter4 =
                new SwingFileExtensionFilter(null, new HashSet<String>());
        JGuiraffeTestHelper.checkEquals(filter3, filter4, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        Set<String> extensions = Collections.singleton("mp3");
        SwingFileExtensionFilter filter =
                new SwingFileExtensionFilter("filterDesc", extensions);

        JGuiraffeTestHelper.checkEquals(filter, new SwingFileExtensionFilter(
                filter.getDescription(), new HashSet<String>()), false);
        JGuiraffeTestHelper.checkEquals(filter,
                new SwingFileExtensionFilter("other", extensions), false);
    }

    /**
     * Tests corner cases of the equals() implementation.
     */
    @Test
    public void testEqualsOtherObjects()
    {
        JGuiraffeTestHelper.testTrivialEquals(new SwingFileExtensionFilter(
                "myFilter", Collections.singleton("foo")));
    }
}
