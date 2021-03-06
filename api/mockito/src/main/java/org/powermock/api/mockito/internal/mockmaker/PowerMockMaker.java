/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.powermock.api.mockito.internal.mockmaker;

import org.mockito.internal.InternalMockHandler;
import org.mockito.internal.creation.CglibMockMaker;
import org.mockito.internal.creation.MockSettingsImpl;
import org.mockito.internal.stubbing.InvocationContainer;
import org.mockito.internal.util.MockNameImpl;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.MockHandler;
import org.mockito.mock.MockCreationSettings;
import org.mockito.plugins.MockMaker;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.VoidMethodStubbable;

import java.util.List;

/**
 * A PowerMock implementation of the MockMaker. Right now it simply delegates to the
 * {@link org.mockito.internal.creation.CglibMockMaker} but in the future we may use it more properly.
 * The reason for its existence is that the CglibMockMaker throws exception for when getting the name
 * from of a mock that is created by PowerMock but not know for Mockito. This is trigged when by the MockUtil class.
 * For more details see the {@link org.powermock.api.mockito.internal.invocation.ToStringGenerator}.
 */
public class PowerMockMaker implements MockMaker {
    private final CglibMockMaker cglibMockMaker = new CglibMockMaker();

    public <T> T createMock(MockCreationSettings<T> settings, MockHandler handler) {
        return cglibMockMaker.createMock(settings, handler);
    }

    public MockHandler getHandler(Object mock) {
        // Return a fake mock handler for static method mocks
        if (mock instanceof Class) {
            return new PowerMockInternalMockHandler((Class<?>) mock);
        } else {
            return cglibMockMaker.getHandler(mock);
        }
    }

    public void resetMock(Object mock, MockHandler newHandler, MockCreationSettings settings) {
        cglibMockMaker.resetMock(mock, newHandler, settings);
    }

    /**
     * It needs to extend InternalMockHandler because Mockito requires the type to be of InternalMockHandler and not MockHandler
     */
    private static class PowerMockInternalMockHandler implements InternalMockHandler<Object> {
        private final Class<?> mock;

        public PowerMockInternalMockHandler(Class<?> mock) {
            this.mock = mock;
        }

        public MockCreationSettings getMockSettings() {
            final MockSettingsImpl mockSettings = new MockSettingsImpl();
            mockSettings.setMockName(new MockNameImpl(mock.getName()));
            mockSettings.setTypeToMock(mock);
            return mockSettings;
        }

        public VoidMethodStubbable<Object> voidMethodStubbable(Object mock) {
            return null;
        }

        public void setAnswersForStubbing(List<Answer> answers) {
        }

        public InvocationContainer getInvocationContainer() {
            return null;
        }

        public Object handle(Invocation invocation) throws Throwable {
            return null;
        }
    }
}
