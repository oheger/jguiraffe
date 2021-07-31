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
package net.sf.jguiraffe.gui.forms;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This is a simple test bean class that acts as a form bean. It is used by some
 * test classes for checking whether a form's data model is correctly handled.
 *
 * @author Oliver Heger
 * @version $Id: PersonBean.java 205 2012-01-29 18:29:57Z oheger $
 */
public class PersonBean
{
    private String name;

    private String firstName;

    private Date birthDate;

    private double averageSalary;

    public double getAverageSalary()
    {
        return averageSalary;
    }

    public void setAverageSalary(double averageSalary)
    {
        this.averageSalary = averageSalary;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof PersonBean))
        {
            return false;
        }

        PersonBean c = (PersonBean) obj;
        return new EqualsBuilder().append(getFirstName(), c.getFirstName())
                .append(getName(), c.getName()).append(getBirthDate(),
                        c.getBirthDate()).append(getAverageSalary(),
                        c.getAverageSalary()).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getFirstName()).append(getName())
                .append(getBirthDate()).append(getAverageSalary()).toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("firstName", getFirstName())
                .append("name", getName()).append("birthDate", getBirthDate())
                .append("salary", getAverageSalary()).toString();
    }
}
