package com.github.dbgso.controller;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.dbgso.service.GitManagementService;

@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/history")
public class HistoryController {

	@Autowired
	GitManagementService service;

	@GetMapping(value = "/{repository}")
	public String history(@PathVariable(name = "repository") String repoName, Model model)
			throws IOException, NoHeadException, GitAPIException {
		service.init("/tmp/git/test/" + repoName);
		List<RevCommit> log = service.Log();
		model.addAttribute("raw", log);
		model.addAttribute("branches", service.getBranches(repoName));
		return "history";
	}
}
