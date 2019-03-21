/**
 * Copyright (c) 2009-2011 VMware, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.springsource.insight.plugin.struts2.test.model;


/**
 * Models a Person who registers.
 *
 * @author bruce phillips
 */
public class Person {
    private String firstName;
    private String lastName;
    private String email;
    private int age;

    public Person() {
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        this.email = mail;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int value) {
        this.age = value;
    }


    @Override
    public String toString() {
        return "First Name: " + getFirstName() + " Last Name:  " + getLastName()
                + " Email:      " + getEmail() + " Age:      " + getAge();
    }
}
