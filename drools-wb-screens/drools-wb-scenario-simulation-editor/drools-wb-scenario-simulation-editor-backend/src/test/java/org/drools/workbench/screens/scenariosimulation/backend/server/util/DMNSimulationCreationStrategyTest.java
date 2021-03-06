/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.model.SimulationDescriptor;
import org.drools.workbench.screens.scenariosimulation.backend.server.AbstractDMNTest;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.model.FactMappingType.EXPECT;
import static org.drools.scenariosimulation.api.model.FactMappingType.GIVEN;
import static org.drools.scenariosimulation.api.model.FactMappingType.OTHER;
import static org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree.Type.DECISION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DMNSimulationCreationStrategyTest extends AbstractDMNTest {

    private DMNSimulationCreationStrategy dmnSimulationCreationStrategy;

    @Mock
    protected DMNTypeService dmnTypeServiceMock;

    @Before
    public void init() {
        super.init();
        dmnSimulationCreationStrategy = spy(new DMNSimulationCreationStrategy() {
            {
                this.dmnTypeService = dmnTypeServiceMock;
            }
        });
    }

    @Test
    public void createSimulation() throws Exception {
        FactModelTuple factModelTuple = getFactModelTuple();
        final Path pathMock = mock(Path.class);
        final String dmnFilePath = "test";
        doReturn(factModelTuple).when(dmnSimulationCreationStrategy).getFactModelTuple(any(), any());
        final Simulation retrieved = dmnSimulationCreationStrategy.createSimulation(pathMock, dmnFilePath);

        assertNotNull(retrieved);
        verify(dmnTypeServiceMock, times(1)).initializeNameAndNamespace(
                any(Simulation.class),
                eq(pathMock),
                eq(dmnFilePath));
    }

    @Test
    public void createSimulationCornerCases() throws Exception {
        // no inputs no outputs
        verifySimulationCreated(false, false);

        // only inputs
        verifySimulationCreated(true, false);

        // only outputs
        verifySimulationCreated(false, true);
    }

    @Test
    public void addEmptyColumnIfNeeded() {
        Simulation simulation = new Simulation();
        ScenarioWithIndex scenarioWithIndex = new ScenarioWithIndex(1, simulation.addScenario());
        ExpressionIdentifier givenExpressionIdentifier = ExpressionIdentifier.create("given1", GIVEN);
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.EMPTY, givenExpressionIdentifier);

        dmnSimulationCreationStrategy.addEmptyColumnsIfNeeded(simulation, scenarioWithIndex);
        assertEquals(2, simulationDescriptor.getFactMappings().size());
        assertTrue(simulationDescriptor.getFactMappings().stream()
                           .anyMatch(elem -> EXPECT.equals(elem.getExpressionIdentifier().getType())));

        simulation = new Simulation();
        scenarioWithIndex = new ScenarioWithIndex(1, simulation.addScenario());
        ExpressionIdentifier expectExpressionIdentifier = ExpressionIdentifier.create("expect1", EXPECT);
        simulationDescriptor = simulation.getSimulationDescriptor();
        simulationDescriptor.addFactMapping(FactIdentifier.EMPTY, expectExpressionIdentifier);

        dmnSimulationCreationStrategy.addEmptyColumnsIfNeeded(simulation, scenarioWithIndex);
        assertEquals(2, simulationDescriptor.getFactMappings().size());
        assertTrue(simulationDescriptor.getFactMappings().stream()
                           .anyMatch(elem -> GIVEN.equals(elem.getExpressionIdentifier().getType())));
    }

    @Test
    public void findNewIndexOfGroup() {
        SimulationDescriptor simulationDescriptorGiven = new SimulationDescriptor();
        ExpressionIdentifier givenExpressionIdentifier = ExpressionIdentifier.create("given1", GIVEN);
        simulationDescriptorGiven.addFactMapping(FactIdentifier.EMPTY, givenExpressionIdentifier);
        assertEquals(1, dmnSimulationCreationStrategy.findNewIndexOfGroup(simulationDescriptorGiven, GIVEN));
        assertEquals(1, dmnSimulationCreationStrategy.findNewIndexOfGroup(simulationDescriptorGiven, EXPECT));

        SimulationDescriptor simulationDescriptorExpect = new SimulationDescriptor();
        ExpressionIdentifier expectExpressionIdentifier = ExpressionIdentifier.create("expect1", EXPECT);
        simulationDescriptorExpect.addFactMapping(FactIdentifier.EMPTY, expectExpressionIdentifier);
        assertEquals(0, dmnSimulationCreationStrategy.findNewIndexOfGroup(simulationDescriptorExpect, GIVEN));
        assertEquals(1, dmnSimulationCreationStrategy.findNewIndexOfGroup(simulationDescriptorExpect, EXPECT));

        assertThatThrownBy(() -> dmnSimulationCreationStrategy.findNewIndexOfGroup(new SimulationDescriptor(), OTHER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This method can be invoked only with GIVEN or EXPECT as FactMappingType");
    }

    private void verifySimulationCreated(boolean hasInput, boolean hasOutput) throws Exception {
        final Path pathMock = mock(Path.class);
        final String dmnFilePath = "test";

        FactModelTuple factModelTuple = getFactModelTuple(hasInput, hasOutput);
        doReturn(factModelTuple).when(dmnSimulationCreationStrategy).getFactModelTuple(any(), any());
        Simulation simulation = dmnSimulationCreationStrategy.createSimulation(pathMock, dmnFilePath);

        assertNotNull(simulation);
        List<FactMapping> factMappings = simulation.getSimulationDescriptor().getFactMappings();
        if (hasInput) {
            assertTrue(factMappings.stream().anyMatch(elem -> GIVEN.equals(elem.getExpressionIdentifier().getType())));
        } else {
            assertEquals(1, factMappings.stream().filter(elem -> GIVEN.equals(elem.getExpressionIdentifier().getType())).count());
        }
        if (hasOutput) {
            assertTrue(factMappings.stream().anyMatch(elem -> EXPECT.equals(elem.getExpressionIdentifier().getType())));
        } else {
            assertEquals(1, factMappings.stream().filter(elem -> EXPECT.equals(elem.getExpressionIdentifier().getType())).count());
        }
    }

    private FactModelTuple getFactModelTuple() throws IOException {
        return getFactModelTuple(true, true);
    }

    private FactModelTuple getFactModelTuple(boolean hasInput, boolean hasOutput) throws IOException {

        SortedMap<String, FactModelTree> visibleFacts = new TreeMap<>();
        SortedMap<String, FactModelTree> hiddenFacts = new TreeMap<>();

        if (hasInput) {
            for (InputDataNode input : dmnModelLocal.getInputs()) {
                DMNType type = input.getType();
                visibleFacts.put(input.getName(), createFactModelTree(input.getName(), input.getName(), type, hiddenFacts, FactModelTree.Type.INPUT));
            }
        }

        if (hasOutput) {
            for (DecisionNode decision : dmnModelLocal.getDecisions()) {
                DMNType type = decision.getResultType();
                visibleFacts.put(decision.getName(), createFactModelTree(decision.getName(), decision.getName(), type, hiddenFacts, DECISION));
            }
        }
        return new FactModelTuple(visibleFacts, hiddenFacts);
    }

    private FactModelTree createFactModelTree(String name, String path, DMNType type, SortedMap<String, FactModelTree> hiddenFacts, FactModelTree.Type fmType) {
        Map<String, String> simpleFields = new HashMap<>();
        if (!type.isComposite()) {
            simpleFields.put("value", type.getName());
            FactModelTree simpleFactModelTree = new FactModelTree(name, "", simpleFields, new HashMap<>(), fmType);
            simpleFactModelTree.setSimple(true);
            return simpleFactModelTree;
        }
        FactModelTree factModelTree = new FactModelTree(name, "", simpleFields, new HashMap<>(), fmType);
        for (Map.Entry<String, DMNType> entry : type.getFields().entrySet()) {
            if (!entry.getValue().isComposite()) {
                simpleFields.put(entry.getKey(), entry.getValue().getName());
            } else {
                String expandableId = path + "." + entry.getKey();
                factModelTree.addExpandableProperty(entry.getKey(), expandableId);
                hiddenFacts.put(expandableId, createFactModelTree(entry.getKey(), expandableId, entry.getValue(), hiddenFacts, FactModelTree.Type.UNDEFINED));
            }
        }
        return factModelTree;
    }
}