/*
 * Copyright 2006-2013 The JGUIraffe Team.
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
package net.sf.jguiraffe.di;

import junit.framework.TestCase;

/**
 * A test class used for testing method invocations via reflection.
 *
 * @author Oliver Heger
 * @version $Id: ReflectionTestClass.java 207 2012-02-09 07:30:13Z oheger $
 */
public class ReflectionTestClass
{
    /** A test constant. */
    public static final Integer ANSWER = 42;

    /** A constant with restricted access. */
    protected static final Integer SECRET_ANSWER = 56;

    /** A test member field. */
    private String stringProp;

    /** A test member field. */
    private int intProp;

    /** A property storing arbitrary data.*/
    private Object data;

    /** A property of an enumeration class. */
    private Mode mode;

    /** A flag indicating whether the shutdown method was called. */
    private boolean shutdown;

    /**
     * Creates a new instance of {@code ReflectionTestClass}. Default
     * constructor.
     */
    public ReflectionTestClass()
    {
        this(null, 0);
    }

    /**
     * Creates a new instance of {@code ReflectionTestClass} and initializes
     * the properties.
     *
     * @param s the string property
     * @param i the int property
     */
    public ReflectionTestClass(String s, int i)
    {
        this(i);
        stringProp = s;
    }

    /**
     * Creates a new instance of {@code ReflectionTestClass} and initializes
     * the string property.
     *
     * @param s the string property
     */
    public ReflectionTestClass(String s)
    {
        this(s, 0);
    }

    /**
     * Creates a new instance of {@code ReflectionTestClass} and initializes the
     * object and the number property.
     *
     * @param obj the data for the object property
     * @param i the numeric property
     */
    public ReflectionTestClass(Object obj, int i)
    {
        this(i);
        data = obj;
    }

    /**
     * Creates a new instance of {@code ReflectionTestClass} and sets the int
     * property. Used for testing invocations of private constructors.
     *
     * @param i the int property
     */
    private ReflectionTestClass(int i)
    {
        intProp = i;
    }

    public String getStringProp()
    {
        return stringProp;
    }

    public void setStringProp(String stringProp)
    {
        this.stringProp = stringProp;
    }

    /**
     * An overloaded method for making parameter matching more interesting.
     *
     * @param buf the buffer with the string to set
     * @return the buffer for method chaining
     */
    public StringBuilder setStringProp(StringBuilder buf)
    {
        this.stringProp = buf.toString();
        return buf;
    }

    public int getIntProp()
    {
        return intProp;
    }

    public void setIntProp(int intProp)
    {
        this.intProp = intProp;
    }

    public Object getData()
    {
        return data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

    public Mode getMode()
    {
        return mode;
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    public void methodThatThrowsAnException()
    {
        throw new UnsupportedOperationException("Don't call me!");
    }

    /**
     * A synthetic property that can only be read.
     *
     * @return the value
     */
    public String getReadOnlyProperty()
    {
        StringBuilder buf = new StringBuilder();
        if (getStringProp() != null)
        {
            buf.append(getStringProp());
        }
        buf.append(getIntProp());
        return buf.toString();
    }

    /**
     * A property with a not supported custom type.
     *
     * @param t the value
     */
    public void setTestCase(TestCase t)
    {
        // just a dummy
    }

    /**
     * A non accessible property.
     *
     * @param s the value
     */
    @SuppressWarnings("unused")
    private void setPrivateProperty(String s)
    {
        // just a dummy
    }

    /**
     * A property that will cause an exception when being set.
     *
     * @param s the value
     */
    public void setExceptionProperty(String s)
    {
        throw new UnsupportedOperationException("Don't set me!");
    }

    /**
     * Initializes the properties of this instance.
     *
     * @param s the string property
     * @param i the int property
     * @return the old value of the string property
     */
    public String initialize(String s, int i)
    {
        String result = getStringProp();
        setStringProp(s);
        setIntProp(i);
        return result;
    }

    /**
     * An overloaded method for initializing some properties.
     *
     * @param s the string property
     * @param i the numeric property
     * @param mode the mode property
     */
    public void initializeOverloaded(String s, int i, Mode mode)
    {
        initializeOverloaded(s, null, i, mode);
    }

    /**
     * Another overloaded initializing method with a different order of
     * arguments.
     *
     * @param mode the mode property
     * @param s the string property
     * @param i the numeric property
     */
    public void initializeOverloaded(Mode mode, String s, int i)
    {
        initializeOverloaded(s, i, mode);
    }

    /**
     * Another overloaded initializing method which affects some other property
     * types.
     *
     * @param s the string property
     * @param i the numeric property
     * @param dataStr the data property as string
     */
    public void initializeOverloaded(String s, int i, String dataStr)
    {
        initializeOverloaded(s, dataStr, i, null);
    }

    /**
     * And still another overloaded initializing method which also initializes
     * the data property.
     *
     * @param s the string property
     * @param data the data property
     * @param i the numeric property
     * @param mode the mode property
     */
    public void initializeOverloaded(String s, Object data, int i, Mode mode)
    {
        setStringProp(s);
        setIntProp(i);
        setMode(mode);
        setData(data);
    }

    /**
     * Shuts down this object. (This only sets a flag.
     */
    public void shutdown()
    {
        shutdown = true;
    }

    /**
     * Returns a flag whether the {@link #shutdown()} method was called.
     *
     * @return a flag whether {@link #shutdown()} was called
     */
    public boolean isShutdown()
    {
        return shutdown;
    }

    /**
     * A static factory method for testing static method invocations.
     *
     * @param s the string property
     * @param i the int property
     * @return the newly created instance
     */
    public static ReflectionTestClass getInstance(String s, int i)
    {
        return new ReflectionTestClass(s, i);
    }

    /**
     * A method for for testing whether a factory method from another bean can
     * be invoked. This method returns a String based on the current string and
     * number property.
     *
     * @param prefix a prefix for the generated string
     * @return the newly created instance
     */
    public String create(String prefix)
    {
        StringBuilder buf = new StringBuilder();
        buf.append(prefix).append(getStringProp());
        buf.append('_').append(getIntProp());
        return buf.toString();
    }

    /**
     * An enumeration class for testing conversions to enumeration literals.
     */
    public static enum Mode
    {
        DEVELOPMENT, TEST, PRODUCTION, CRITICAL
    }
}
