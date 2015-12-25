package com.dnfeitosa.codegraph.db.graph.repositories;

import com.dnfeitosa.codegraph.db.DatabaseDependentTest;
import com.dnfeitosa.codegraph.db.graph.nodes.ModuleNode;
import com.dnfeitosa.codegraph.db.graph.nodes.builders.ApplicationNodeBuilder;
import com.dnfeitosa.codegraph.db.graph.nodes.builders.ModuleNodeBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.dnfeitosa.codegraph.db.graph.nodes.builders.Builders.application;
import static com.dnfeitosa.codegraph.db.graph.nodes.builders.Builders.applications;
import static com.dnfeitosa.codegraph.db.graph.nodes.builders.Builders.module;
import static com.dnfeitosa.codegraph.db.graph.nodes.builders.Builders.modules;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ModuleRepositoryTest extends DatabaseDependentTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Test
    public void shouldReturnTheDependenciesOfAModule() {
        module("A1").exportedBy(application("a")).dependsOn(modules("A2", "C1"));
        module("A2").exportedBy(application("a")).dependsOn(modules("A3", "B1", "D1"));
        module("A3").exportedBy(application("a"));

        module("B1").exportedBy(application("b")).dependsOn(modules("C1", "junit", "log4j"));

        module("C1").exportedBy(application("c")).dependsOn(module("D1"));

        module("D1").exportedBy(application("d"));

        application("e").exports(modules("E1", "E2", "E3"));

        applications("a", "b", "c", "d", "e").each(app -> applicationRepository.save(app.node()));

        List<ModuleNode> nodes = moduleRepository.dependenciesOf("A1").stream()
                .sorted(comparing(node -> node.getName()))
                .collect(toList());

        assertThat(nodes.size(), is(8));

        assertIsModule(nodes.get(0), "A1", application("a"), modules("C1", "A2"));
        assertIsModule(nodes.get(1), "A2", application("a"), modules("A3", "B1", "D1"));
        assertIsModule(nodes.get(2), "A3", application("a"), modules());
        assertIsModule(nodes.get(3), "B1", application("b"), modules("C1", "junit", "log4j"));
        assertIsModule(nodes.get(4), "C1", application("c"), modules("D1"));
        assertIsModule(nodes.get(5), "D1", application("d"), modules());
        assertIsModule(nodes.get(6), "junit", null, modules());
        assertIsModule(nodes.get(7), "log4j", null, modules());
    }

    private void assertIsModule(ModuleNode moduleNode, String expectedName, ApplicationNodeBuilder expectedApplication,
                                ModuleNodeBuilders expectedDependencies) {
        assertThat(moduleNode.getName(), is(expectedName));
        assertModuleBelongsToApplication(moduleNode, expectedApplication);
        assertHasDependencies(moduleNode, expectedDependencies);
    }

    private void assertModuleBelongsToApplication(ModuleNode moduleNode, ApplicationNodeBuilder expectedApplication) {
        if (expectedApplication == null) {
            assertNull(moduleNode.getApplication());
        } else {
            assertThat(moduleNode.getApplication().getId(), is(expectedApplication.node().getId()));
        }
    }

    private void assertHasDependencies(ModuleNode moduleNode, ModuleNodeBuilders expectedDependencies) {
        List<Long> dependenciesIds = moduleNode.getDependencies().stream()
                .map(ModuleNode::getId)
                .collect(toList());

        Long[] longs = expectedDependencies.builders().stream()
            .map(builder -> builder.node().getId())
            .toArray(Long[]::new);
        assertThat(dependenciesIds, hasItems(longs));
    }
}