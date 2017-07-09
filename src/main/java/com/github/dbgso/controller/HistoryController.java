package com.github.dbgso.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.github.dbgso.model.Commit;
import com.github.dbgso.service.GitManagementService;

@Controller
@EnableAutoConfiguration
@RequestMapping(value = "/history/{repository}")
public class HistoryController {

	@Autowired
	GitManagementService service;

	@GetMapping()
	public String history(@PathVariable(name = "repository") String repoName,
			@RequestParam(name = "limit", required = false) Integer limit, Model model)
			throws IOException, NoHeadException, GitAPIException {
		if (limit == null)
			limit = 50;
		List<RevCommit> log = service.log(repoName, limit);
		model.addAttribute("raw", log);
		model.addAttribute("branches", service.getBranches(repoName));
		return "history";
	}

	@GetMapping(value = "/{hash}")
	public String getCommitInfo(@PathVariable(name = "repository") String repoName,
			@PathVariable(name = "hash") String hash, Model model)
			throws NoHeadException, GitAPIException, IOException {
		Commit commit = service.getCommit(repoName, hash);
		List<Commit> parentCommits = new ArrayList<>(commit.getParents().size());
		for (String parentHash : commit.getParents()) {
			parentCommits.add(service.getCommit(repoName, parentHash));
		}
		model.addAttribute("commit", commit);
		model.addAttribute("parents", parentCommits);
		return "commit";
	}

	@GetMapping(value = "/{hash}/download-zip")
	public ModelAndView downloadAsZip(ModelAndView mav) {
		// TODO implement
		mav.addObject("filename", "test-name");
		mav.addObject("products", "test-pro".getBytes());

		return mav;
	}

	@RequestMapping(value = "/{hash}/download-csv", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadAsCsv(@PathVariable(name = "repository") String repoName,
			@PathVariable(name = "hash") String hash) throws IOException, NoHeadException, GitAPIException {
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv; charset=MS932");
		header.setContentDispositionFormData("filename", "hoge.csv");

		Commit commit = service.getCommit(repoName, hash);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("author, \"%s\"\n", commit.getAuthor()));
		sb.append(String.format("commit message, \"%s\"\n", commit.getMessage()));
		sb.append(String.format("date, \"%s\"\n", commit.getDate().toString()));
		commit.getModifiedFiles().stream()//
				.forEach(file -> {
					sb.append(String.format("\"%s\", \"%s\"\n", file.getChangeType().toString(), file.getPath()));

				});

		return new ResponseEntity<>(sb.toString().getBytes("UTF-8"), header, HttpStatus.OK);
	}

	@GetMapping(value = "/{hash}/diff")
	public String showDiff(@RequestParam("path") String path) {
		return "diff";
	}

}
