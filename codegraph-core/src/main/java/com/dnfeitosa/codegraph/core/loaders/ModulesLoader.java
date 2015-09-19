package com.dnfeitosa.codegraph.core.loaders;

import com.dnfeitosa.codegraph.core.loaders.finders.IvyFileFinder;
import com.dnfeitosa.codegraph.core.model.Module;
import com.dnfeitosa.codegraph.core.descriptors.ModuleDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dnfeitosa.coollections.Coollections.$;

@Component
public class ModulesLoader {

	private final IvyFileFinder finder;
	private ModuleLoader moduleLoader;

    @Autowired
	public ModulesLoader(IvyFileFinder finder, ModuleLoader moduleLoader) {
		this.finder = finder;
		this.moduleLoader = moduleLoader;
	}

	public List<Module> loadAllFrom(String applicationLocation) {
		return $(ivyFilesFrom(applicationLocation)).map(toModule());
	}

	private ModuleLoader toModule() {
		return moduleLoader;
	}

	private List<ModuleDescriptor> ivyFilesFrom(String applicationLocation) {
		return finder.findFilesIn(applicationLocation);
	}
}