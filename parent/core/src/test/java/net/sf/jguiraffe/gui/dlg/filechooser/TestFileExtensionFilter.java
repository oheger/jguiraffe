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
package net.sf.jguiraffe.gui.dlg.filechooser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.jguiraffe.JGuiraffeTestHelper;
import net.sf.jguiraffe.gui.app.TextResource;
import org.junit.Test;

/**
 * Test class for {@code FileExtensionFilter}.
 */
public class TestFileExtensionFilter
{
    /** A test resource for the filter description. */
    private static final TextResource DESC = TextResource.fromText("My filter");

    /**
     * Tests the management of the description resource.
     */
    @Test
    public void testDescriptionResourceCanBeSetAndQueried()
    {
        FileExtensionFilter filter =
                new FileExtensionFilter(DESC, Collections.singletonList("foo"));

        assertEquals("Wrong description resource", DESC,
                filter.getDescription());
    }

    /**
     * Tests the constructor that expects a collection with extensions.
     */
    @Test
    public void testInitFromExtensionCollection()
    {
        List<String> extensions = Arrays.asList("foo", "bar", "baz");

        FileExtensionFilter filter = new FileExtensionFilter(DESC, extensions);
        assertEquals("Wrong extensions", extensions, filter.getExtensions());
    }

    /**
     * Tests that the constructor creates a defensive copy of the list with file
     * extensions.
     */
    @Test
    public void testExtensionListIsCopiedByConstructor()
    {
        List<String> extensionsOrg = Arrays.asList("ext1", "ext2");
        List<String> extensions = new ArrayList<String>(extensionsOrg);
        FileExtensionFilter filter = new FileExtensionFilter(DESC, extensions);

        extensions.add("more");
        assertEquals("Extensions were changed", extensionsOrg,
                filter.getExtensions());
    }

    /**
     * Tests that getExtensions() returns an unmodifiable list.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testExtensionListCannotBeModified()
    {
        FileExtensionFilter filter =
                new FileExtensionFilter(DESC, Arrays.asList("a", "b", "c"));
        List<String> extensions = filter.getExtensions();

        extensions.add("more");
    }

    /**
     * Tests the constructor expecting extensions as var args parameter.
     */
    @Test
    public void testInitFromExtensionVarArg()
    {
        FileExtensionFilter filter = new FileExtensionFilter(DESC, "e1", "e2");

        assertEquals("Wrong description", DESC, filter.getDescription());
        assertEquals("Wrong extensions", Arrays.asList("e1", "e2"),
                filter.getExtensions());
    }

    /**
     * Tests equals() if the expected result is true.
     */
    @Test
    public void testEqualsTrue()
    {
        FileExtensionFilter filter = new FileExtensionFilter(DESC, "tst", "eq");
        FileExtensionFilter c = new FileExtensionFilter(
                TextResource.fromText(DESC.getPlainText()),
                filter.getExtensions());

        JGuiraffeTestHelper.checkEquals(filter, c, true);
    }

    /**
     * Tests equals() if the expected result is false.
     */
    @Test
    public void testEqualsFalse()
    {
        List<String> extensions = Arrays.asList("doc", "dic", "dac", "duc");
        FileExtensionFilter f1 = new FileExtensionFilter(DESC, extensions);

        JGuiraffeTestHelper.checkEquals(f1, new FileExtensionFilter(
                TextResource.fromText("other"), extensions), false);
        List<String> otherExtensions = new ArrayList<String>(extensions);
        otherExtensions.add("dec");
        JGuiraffeTestHelper.checkEquals(f1,
                new FileExtensionFilter(DESC, otherExtensions), false);
    }

    /**
     * Tests equals() with some corner cases.
     */
    @Test
    public void testEqualsOtherObjects()
    {
        FileExtensionFilter f = new FileExtensionFilter(DESC, "1", "2");

        JGuiraffeTestHelper.testTrivialEquals(f);
    }

    /**
     * Tests the string representation.
     */
    @Test
    public void testToString()
    {
        List<String> extensions = Arrays.asList("mp3", "mp4", "mp5");
        FileExtensionFilter filter = new FileExtensionFilter(DESC, extensions);

        String s = filter.toString();
        assertThat("No description", s, containsString("description=" + DESC));
        assertThat("No extensions", s,
                containsString("extensions=" + extensions));
    }
}
