/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import org.mockito.Mock;

import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMN_NAMESPACE;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.DMO_SESSION;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.FILE_NAME;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.RULE_FLOW_GROUP;
import static org.drools.workbench.screens.scenariosimulation.client.TestProperties.SCENARIO_TYPE;
import static org.mockito.Mockito.when;

abstract class AbstractSettingsTest {

    @Mock
    protected LabelElement nameLabelMock;

    @Mock
    protected InputElement fileNameMock;

    @Mock
    protected LabelElement typeLabelMock;

    @Mock
    protected SpanElement scenarioTypeMock;

    @Mock
    protected DivElement ruleSettingsMock;

    @Mock
    protected Style ruleSettingsStyleMock;

    @Mock
    protected InputElement dmoSessionMock;

    @Mock
    protected InputElement ruleFlowGroupMock;

    @Mock
    protected DivElement dmnSettingsMock;

    @Mock
    protected Style dmnSettingsStyleMock;

    @Mock
    protected LabelElement dmnModelLabelMock;

    @Mock
    protected DivElement dmnFilePathPlaceholderMock;

    @Mock
    protected SpanElement dmnFilePathErrorLabelMock;

    @Mock
    protected Style dmnFilePathErrorLabelStyleMock;

    @Mock
    protected LabelElement dmnNamespaceLabelMock;

    @Mock
    protected InputElement dmnNamespaceMock;

    @Mock
    protected LabelElement dmnNameLabelMock;

    @Mock
    protected InputElement dmnNameMock;

    @Mock
    protected InputElement skipFromBuildMock;

    @Mock
    protected InputElement statelessMock;

    @Mock
    protected ButtonElement saveButtonMock;

    protected void setup() {
        when(fileNameMock.getValue()).thenReturn(FILE_NAME);
        when(scenarioTypeMock.getInnerText()).thenReturn(SCENARIO_TYPE);
        when(dmoSessionMock.getValue()).thenReturn(DMO_SESSION);
        when(ruleFlowGroupMock.getValue()).thenReturn(RULE_FLOW_GROUP);
        when(dmnFilePathPlaceholderMock.getInnerText()).thenReturn("");
        when(dmnNamespaceMock.getValue()).thenReturn(DMN_NAMESPACE);
        when(dmnNameMock.getValue()).thenReturn(DMN_NAME);
        when(ruleSettingsMock.getStyle()).thenReturn(ruleSettingsStyleMock);
        when(dmnSettingsMock.getStyle()).thenReturn(dmnSettingsStyleMock);
        when(dmnFilePathErrorLabelMock.getStyle()).thenReturn(dmnFilePathErrorLabelStyleMock);
    }
}