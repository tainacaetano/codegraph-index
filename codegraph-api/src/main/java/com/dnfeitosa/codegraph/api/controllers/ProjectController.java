package com.dnfeitosa.codegraph.api.controllers;

import com.dnfeitosa.codegraph.api.converters.ProjectResourceConverter;
import com.dnfeitosa.codegraph.api.resources.ProjectResource;
import com.dnfeitosa.codegraph.api.resources.ProjectResources;
import com.dnfeitosa.codegraph.api.services.ItemDoesNotExistException;
import com.dnfeitosa.codegraph.api.services.ProjectService;
import com.dnfeitosa.codegraph.core.models.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;
    private final ProjectResourceConverter projectResourceConverter;

    @Autowired
    public ProjectController(ProjectService projectService, ProjectResourceConverter projectResourceConverter) {
        this.projectService = projectService;
        this.projectResourceConverter = projectResourceConverter;
    }

    @RequestMapping(value = "/projects", method = GET)
    public ResponseEntity<ProjectResources> getProjects() {
        List<Project> projects = projectService.loadAll();
        return new ResponseEntity<>(projectResourceConverter.toResources(projects), HttpStatus.OK);
    }

    @RequestMapping(value = "/projects", method = POST)
    public ResponseEntity<ProjectResource> addProject(@RequestBody ProjectResource projectResource) {
        Project project = projectResourceConverter.toModel(projectResource);
        Project created = projectService.addProject(project);
        return new ResponseEntity<>(projectResourceConverter.toResource(created), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/projects/{id}")
    public ResponseEntity<ProjectResource> getProject(@PathVariable("id") Long id) {
        try {
            Project project = projectService.loadProject(id);

            return new ResponseEntity<>(projectResourceConverter.toResource(project), HttpStatus.OK);
        } catch (ItemDoesNotExistException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
