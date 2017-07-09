package com.github.dbgso.controller;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.gitrepo.RepoCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.dbgso.model.GitProject;
import com.github.dbgso.model.GitProjectRepository;
import com.github.dbgso.service.GitManagementService;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class MainController {

	@Autowired
	GitManagementService service;

	@Autowired
	GitProjectRepository gitProjects;

	@GetMapping
	public String index(Model model, GitProject project) {
		model.addAttribute("project", project);
		model.addAttribute("projects", gitProjects.findAll());
		return "index";
	}

	@RequestMapping(value = "clone", method = RequestMethod.POST)
	public String clone(@Validated @ModelAttribute GitProject project, Model model, BindingResult result)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		if (result.hasErrors()) {
			return index(model, project);
		}

		GitProject existedProject = gitProjects.findByName(project.getName());
		if (existedProject != null)
			return index(model, project);
		service.clone("/tmp/git/test/" + project.getName(), project.getUrl());
		gitProjects.save(project);
		return "redirect:/";
	}

	@DeleteMapping(value = "/{id}")
	public String deleteProject(@PathVariable("id") Long id) {
		GitProject foundProject = gitProjects.findOne(id);
		if (foundProject == null)
			throw new IllegalArgumentException(String.format("project is not found."));

		String projectName = foundProject.getName();
		boolean isDeleteSuccess = service.delete(projectName);
		if (!isDeleteSuccess)
			throw new IllegalAccessError("fail to delete project files.");

		gitProjects.delete(id);
		return "redirect:/";
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String illegalArgumentExceptionHandler(IllegalArgumentException e) {
		return e.getMessage();
	}
}
