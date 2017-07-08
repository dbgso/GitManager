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

import com.github.dbgso.model.Commit;
import com.github.dbgso.service.GitManagementService;

@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/history/{repository}")
public class HistoryController {

	@Autowired
	GitManagementService service;

	@GetMapping()
	public String history(@PathVariable(name = "repository") String repoName, Model model)
			throws IOException, NoHeadException, GitAPIException {
		List<RevCommit> log = service.log(repoName);
		model.addAttribute("raw", log);
		model.addAttribute("branches", service.getBranches(repoName));
		return "history";
	}

	@GetMapping(value = "/{hash}")
	public String hoge(@PathVariable(name = "repository") String repoName, @PathVariable(name = "hash") String hash,
			Model model) throws NoHeadException, GitAPIException, IOException {
		Commit commit = service.getCommit(repoName, hash);
		model.addAttribute("commit", commit);
		return "commit";
	}

	@GetMapping(value = "/{hash}/diff")
	public String aiueo() {
		
		
		return "diff";
	}

}
