/*
 * Copyright 2006-2017 The JGUIraffe Team.
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
package net.sf.jguiraffe.di.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.LinkedList;
import java.util.List;

import net.sf.jguiraffe.di.BeanContext;
import net.sf.jguiraffe.di.BeanCreationEvent;
import net.sf.jguiraffe.di.BeanCreationListener;
import net.sf.jguiraffe.di.BeanProvider;
import net.sf.jguiraffe.di.DependencyProvider;

/**
 * A test implementation of a bean creation listener for testing whether events
 * are correctly sent.
 *
 * @author Oliver Heger
 * @version $Id: BeanCreationListenerTestImpl.java 205 2012-01-29 18:29:57Z oheger $
 */
class BeanCreationListenerTestImpl implements BeanCreationListener
{
    /** A list with the events received. */
    private final List<BeanCreationEvent> events = new LinkedList<BeanCreationEvent>();

    /**
     * Records this invocation and stores the passed in event.
     */
    public void beanCreated(BeanCreationEvent event)
    {
        events.add(event);
    }

    /**
     * Tests the next received event.
     *
     * @param expCtx the expected bean context
     * @param expBean the expected bean
     * @param expProvider the expected bean provider
     * @param expDepProvider the expected dependency provider
     */
    public void checkNextEvent(BeanContext expCtx, Object expBean,
            BeanProvider expProvider, DependencyProvider expDepProvider)
    {
        assertFalse("No more events", events.isEmpty());
        BeanCreationEvent event = events.remove(0);
        assertEquals("Wrong bean context", expCtx, event.getBeanContext());
        assertEquals("Wrong bean", expBean, event.getBean());
        assertEquals("Wrong provider", expProvider, event.getBeanProvider());
        assertEquals("Wrong dependency provider", expDepProvider, event
                .getDependencyProvider());
    }

    /**
     * Returns a flag whether more events are available.
     *
     * @return a flag if there are more events
     */
    public boolean hasMoreEvents()
    {
        return !events.isEmpty();
    }
}
