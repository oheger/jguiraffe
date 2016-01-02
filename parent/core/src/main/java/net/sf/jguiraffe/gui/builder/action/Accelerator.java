/*
 * Copyright 2006-2016 The JGUIraffe Team.
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
package net.sf.jguiraffe.gui.builder.action;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.sf.jguiraffe.gui.builder.event.Keys;
import net.sf.jguiraffe.gui.builder.event.Modifiers;

import org.apache.commons.lang.ObjectUtils;

/**
 * <p>
 * A class that represents an <em>accelerator</em> for invoking an action.
 * </p>
 * <p>
 * An accelerator is a key (or a combination of keys, typically associated with
 * some modifier keys like SHIFT or ALT) that triggers an action when pressed.
 * Thus it is a keyboard short cut that has the same effect as clicking a tool
 * bar button or selecting a menu element. When a {@link FormAction} is created
 * (defined by an {@link ActionData} object) an accelerator can be specified.
 * This can have effect on GUI elements associated with this action. For
 * instance, menus will typically display the keyboard combinations that
 * correspond to the menu elements.
 * </p>
 * <p>
 * This class has the same purpose as <code>javax.swing.KeyStroke</code>:
 * serving as an abstract description of a combination of key presses. However,
 * it is more tailored towards the builder approach followed by this library.
 * This means that the main use case of this class is being created indirectly
 * in a builder script (mostly using a text representation) and then being
 * passed to an implementation of <code>ActionManager</code>. A concrete
 * <code>ActionManager</code> implementation is responsible for converting a
 * generic <code>Accelerator</code> object into a platform-specific
 * representation of a key stroke.
 * </p>
 * <p>
 * Instances of this class store a set of modifiers (like SHIFT or CONTROL) that
 * must be pressed together with the key. The actual key can be specified in the
 * following different ways:
 * <ul>
 * <li>If it is a "normal" (i.e. printable) character, it can be queried using
 * the <code>getKey()</code> method, which returns a <code>Character</code>.</li>
 * <li>For special keys like the function keys, Escape, or Enter enumeration
 * literals are defined. If such a key is used for the accelerator, the
 * <code>getSpecialKey()</code> method will return a non-<b>null</b> value.</li>
 * <li>It is also possible to use a key code specific to a concrete GUI library,
 * e.g. a virtual key code used within Swing (the <code>VK_XXX</code> constants
 * of the <code>KeyEvent</code> class). If such a code is set, it can be queried
 * using the <code>getKeyCode()</code> method. Note that this variant is not
 * portable.</li>
 * </ul>
 * Exactly one of the methods listed above will return a non-<b>null</b> value.
 * </p>
 * <p>
 * Implementation note: Instances of this class are immutable and can be shared
 * among multiple threads.
 * </p>
 *
 * @author Oliver Heger
 * @version $Id: Accelerator.java 205 2012-01-29 18:29:57Z oheger $
 */
public final class Accelerator
{
    /** Constant for the regex for splitting a string to be parsed. */
    private static final String REGEX_SPLIT = "\\s";

    /** Constant for the separator used by the toString() method. */
    private static final char SEPARATOR = ' ';

    /** Constant for the padding character for single digit key codes. */
    private static final char PAD = '0';

    /** Constant for determining single digit key codes. */
    private static final int MULTI_DIGITS = 10;

    /** Constant for the seed value of the hash code computation. */
    private static final int HASH_SEED = 17;

    /** Constant for the factory for the hash code computation. */
    private static final int HASH_FACTOR = 37;

    /** Stores the modifiers associated with this accelerator. */
    private final Set<Modifiers> modifiers;

    /** Stores the special key for this accelerator. */
    private final Keys specialKey;

    /** Stores the character for this accelerator. */
    private final Character key;

    /** Stores a platform-specific key code. */
    private final Integer keyCode;

    /**
     * Creates a new instance of <code>Accelerator</code> and initializes it.
     * Clients obtain instances through the static factory methods.
     *
     * @param mods the modifiers
     * @param specKey the special key
     * @param c the character
     * @param code the key code
     */
    private Accelerator(Set<Modifiers> mods, Keys specKey, Character c,
            Integer code)
    {
        specialKey = specKey;
        key = c;
        keyCode = code;
        if (mods == null)
        {
            modifiers = Collections.emptySet();
        }
        else
        {
            modifiers = Collections.unmodifiableSet(EnumSet.copyOf(mods));
        }
    }

    /**
     * Returns a set with the modifiers set for this accelerator. These are
     * special mode keys (like SHIFT or CONTROL) that must be pressed together
     * with the actual key for triggering this accelerator.
     *
     * @return a set with the modifiers (this set cannot be modified)
     */
    public Set<Modifiers> getModifiers()
    {
        return modifiers;
    }

    /**
     * Returns the special key. If this accelerator is represented by a special
     * key (for which a constant is available), this key is returned by this
     * method. Otherwise <b>null</b> is returned.
     *
     * @return the special key representing this accelerator or <b>null</b>
     */
    public Keys getSpecialKey()
    {
        return specialKey;
    }

    /**
     * Returns the character. If the key of this accelerator is a printable
     * character, it is returned by this method. Otherwise result is
     * <b>null</b>.
     *
     * @return the character of this accelerator or <b>null</b>
     */
    public Character getKey()
    {
        return key;
    }

    /**
     * Returns the key code. If this accelerator is represented by a
     * (platform-specific) key code, this code is returned here. Otherwise
     * result is <b>null</b>.
     *
     * @return the key code representing this accelerator or <b>null</b>
     */
    public Integer getKeyCode()
    {
        return keyCode;
    }

    /**
     * Returns a string representation of this object. Strings returned by this
     * method are compatible with the <code>{@link #parse(String)}</code>
     * method, i.e. they can be used for creating <code>Accelerator</code>
     * instances. They are normalized in the following way:
     * <ul>
     * <li>Modifiers are listed in their natural order (this is the order in
     * which the <code>enum</code> constants are declared and happens to be
     * alphabetic order).</li>
     * <li>For each component of the string a single space is used as separator.
     * </li>
     * <li>All constants are written in capital letters.</li>
     * </ul>
     *
     * @return a string for this object
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();

        for (Modifiers m : getModifiers())
        {
            append(buf, m);
        }

        if (getKey() != null)
        {
            append(buf, getKey());
        }
        else if (getSpecialKey() != null)
        {
            append(buf, getSpecialKey().name());
        }
        else
        {
            if (getKeyCode() < MULTI_DIGITS)
            {
                append(buf, PAD).append(getKeyCode());
            }
            else
            {
                append(buf, getKeyCode());
            }
        }

        return buf.toString();
    }

    /**
     * Compares this object with another one. Two objects are equal if and only
     * if they use the same way of describing the key and have the same
     * modifiers.
     *
     * @param obj the object to compare
     * @return a flag whether these objects are equal
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof Accelerator))
        {
            return false;
        }

        Accelerator c = (Accelerator) obj;
        return ObjectUtils.equals(getKey(), c.getKey())
                && ObjectUtils.equals(getSpecialKey(), c.getSpecialKey())
                && ObjectUtils.equals(getKeyCode(), c.getKeyCode())
                && getModifiers().equals(c.getModifiers());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code
     */
    @Override
    public int hashCode()
    {
        int result = HASH_SEED;

        if (getKey() != null)
        {
            result = HASH_FACTOR * result + getKey().hashCode();
        }
        else if (getKeyCode() != null)
        {
            result = HASH_FACTOR * result + getKeyCode().hashCode();
        }
        else
        {
            result = HASH_FACTOR * result + getSpecialKey().hashCode();
        }
        result = HASH_FACTOR * result + getModifiers().hashCode();

        return result;
    }

    /**
     * Returns an <code>Accelerator</code> for the specified special key and the
     * (optional) modifiers.
     *
     * @param key the special key for this accelerator (must not be <b>null</b>)
     * @param modifiers a set with modifiers (can be <b>null</b>)
     * @return the <code>Accelerator</code> instance
     * @throws IllegalArgumentException if the key is undefined
     */
    public static Accelerator getInstance(Keys key, Set<Modifiers> modifiers)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null");
        }
        return new Accelerator(modifiers, key, null, null);
    }

    /**
     * Returns an <code>Accelerator</code> for the specified printable key and
     * the (optional) modifiers.
     *
     * @param key the character for this accelerator (must not be <b>null</b>)
     * @param modifiers a set with modifiers (can be <b>null</b>)
     * @return the <code>Accelerator</code> instance
     * @throws IllegalArgumentException if the key is undefined
     */
    public static Accelerator getInstance(Character key,
            Set<Modifiers> modifiers)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null!");
        }
        return new Accelerator(modifiers, null, key, null);
    }

    /**
     * Returns an <code>Accelerator</code> for the specified platform-specific
     * key code and the (optional) modifiers.
     *
     * @param keyCode the special key code for this accelerator (must not be
     *        <b>null</b>)
     * @param modifiers a set with modifiers (can be <b>null</b>)
     * @return the <code>Accelerator</code> instance
     * @throws IllegalArgumentException if the key is undefined
     */
    public static Accelerator getInstance(Integer keyCode,
            Set<Modifiers> modifiers)
    {
        if (keyCode == null)
        {
            throw new IllegalArgumentException("Key code must not be null!");
        }
        return new Accelerator(modifiers, null, null, keyCode);
    }

    /**
     * <p>
     * Returns an <code>Accelerator</code> instance from the specified string
     * representation. The string must be a valid representation of an
     * <code>Accelerator</code> instance as it is returned by the
     * <code>toString()</code> method. Otherwise an
     * <code>IllegalArgumentException</code> exception will be thrown.
     * </p>
     * <p>
     * Valid strings have the following form:
     *
     * <pre>
     * acceleratorString ::= (&lt;modifier&gt;)* keySpec
     * keySpec           ::= &lt;character&gt; | &lt;specialKey&gt; | &lt;keyCode&gt;
     * </pre>
     *
     * With other words: The string can contain multiple components all
     * separated by whitespace. The last component defines the actual keys, all
     * others are interpreted as modifiers. They must conform to literals of the
     * <code>Modifiers</code> enumeration (case does not matter). For the last
     * component, there are the following possibilities:
     * <ul>
     * <li>If it is a string of length 1, it is interpreted as a printable
     * character.</li>
     * <li>If it is a literal defined in the <code>Keys</code> enumeration, the
     * corresponding special key is set (the comparison is not case sensitive).</li>
     * <li>If it is a number, it is parsed to an integer and interpreted as key
     * code. Note that there is an ambiguity for key codes consisting of a
     * single digit (0-9). Because they are represented by a string with the
     * length 1 they are interpreted as characters (see above). To avoid this,
     * add a leading 0, e.g. "05".</li>
     * </ul>
     * </p>
     * <p>
     * Here are some examples:
     * <dl>
     * <dt><code>"A"</code></dt>
     * <dd>This is simply the letter A without any modifiers.</dd>
     * <dt><code>control A</code></dt>
     * <dd>The letter A with the CONTROL modifier.</dd>
     * <dt><code>Shift CONTROL A</code></dt>
     * <dd>The letter A with both the SHIFT and the CONTROL modifier. Note that
     * case of the modifiers does not matter. The same is true for the order in
     * which they are given.</dd>
     * <dt><code>Alt Backspace</code></dt>
     * <dd>The backspace key plus the ALT modifier (as is often used as undo
     * command). For special keys case does not matter either.</dd>
     * <dt><code>F1</code></dt>
     * <dd>The F1 function key, as is often used for the help command.</dd>
     * <dt><code>42</code></dt>
     * <dd>A special key code with the numeric value of 42.</dd>
     * <dt><code>CONTROL 5</code></dt>
     * <dd>The character '5' plus the CONTROL modifier.</dd>
     * <dt><code>CONTROL 05</code></dt>
     * <dd>The numeric key code 5 (whatever this means) plus the CONTROL
     * modifier. Note that here a leading 0 must be used, otherwise the number
     * will be interpreted as character.</dd>
     * </dl>
     * </p>
     * <p>
     * If the whole string is <b>null</b> or empty, <b>null</b> is returned.
     * </p>
     *
     * @param s the string to be parsed (can be <b>null</b>)
     * @return the corresponding <code>Accelerator</code> instance
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    public static Accelerator parse(String s)
    {
        if (s == null || s.length() == 0)
        {
            return null;
        }

        String[] comps = s.split(REGEX_SPLIT);
        if (comps.length == 0)
        {
            return null;
        }

        Set<Modifiers> mods = EnumSet.noneOf(Modifiers.class);
        for (int i = 0; i < comps.length - 1; i++)
        {
            if (comps[i].length() > 0)
            {
                mods.add(Modifiers.fromString(comps[i]));
            }
        }

        return parseKeySpec(mods, comps[comps.length - 1]);
    }

    /**
     * Helper method for parsing the key specification after the modifiers have
     * been parsed successfully.
     *
     * @param mods the modifiers
     * @param keySpec the string with the key specification
     * @return the corresponding accelerator instance
     * @throws IllegalArgumentException if the key cannot be parsed
     */
    private static Accelerator parseKeySpec(Set<Modifiers> mods, String keySpec)
    {
        if (keySpec.length() == 1)
        {
            // a character
            return getInstance(Character.valueOf(keySpec.charAt(0)), mods);
        }

        // test for a special key
        String keySpecCase = keySpec.toUpperCase();
        for (Keys k : Keys.values())
        {
            if (k.name().equals(keySpecCase))
            {
                return getInstance(k, mods);
            }
        }

        // Can it be converted to an integer? Then it is a key code.
        try
        {
            return getInstance(Integer.parseInt(keySpec), mods);
        }
        catch (NumberFormatException nfex)
        {
            throw new IllegalArgumentException("Invalid key specificaton: "
                    + keySpec);
        }
    }

    /**
     * Helper method for appending data to a string builder. If required, this
     * method adds a separator before the new data is appended.
     *
     * @param buf the buffer
     * @param data the data to be added
     * @return a reference to the same buffer
     */
    private static StringBuilder append(StringBuilder buf, Object data)
    {
        if (buf.length() > 0)
        {
            buf.append(SEPARATOR);
        }
        return buf.append(data);
    }
}
