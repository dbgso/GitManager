package com.github.dbgso.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.dbgso.model.Commit;
import com.github.dbgso.model.GitProject;
import com.github.dbgso.model.GitProjectRepository;
import com.github.dbgso.service.GitManagementService;

@RestController
@RequestMapping("/api/commits")
public class GitRestController {

	@Autowired
	GitProjectRepository projects;
	@Autowired
	GitManagementService service;

	@GetMapping
	public List<String> respositoryNames() {
		Iterable<GitProject> proj = projects.findAll();
		List<String> names = new ArrayList<String>();
		proj.forEach(p -> names.add(p.getName()));
		return names;
	}

	@GetMapping(value = "/{name}")
	public GitProject respository(@PathVariable String name) {
		GitProject project = projects.findByName(name);
		return project;
	}

	@GetMapping(value = "/{name}/commits")
	public List<Commit> getCommits(@PathVariable String name) throws NoHeadException, GitAPIException, IOException {
		GitProject project = projects.findByName(name);
		return service.getCommits(name);
	}

}
