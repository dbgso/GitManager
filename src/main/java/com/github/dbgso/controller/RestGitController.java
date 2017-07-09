package com.github.dbgso.controller;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.dbgso.model.Commit;
import com.github.dbgso.service.GitManagementService;

@RequestMapping(value = "/api")
@RestController
public class RestGitController {

	@Autowired
	GitManagementService service;

	@GetMapping(value = "/{repositoryName}/message")
	public List<Commit> searchByCommitMessage(@PathVariable(name = "repositoryName") String repositoryName,
			@RequestParam(required = true, name = "message") String message)
			throws NoHeadException, GitAPIException, IOException {
		service.initFromRepositoryName(repositoryName);
		return service.searchByMessage(repositoryName, message);
	}

	@GetMapping("/{repoName}/{hash}")
	public List<String> getModifiedFilePair(@PathVariable("repoName") String name, @PathVariable("hash") String hash,
			@RequestParam("path") String path) throws IOException {
		return service.getTextPair(name, hash, path);
	}

	@ExceptionHandler(value = { NoHeadException.class })
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String internalErrorHandling() {
		return "nohead";
	}

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String badrequest(IllegalArgumentException e) {
		return e.getMessage();
	}

}
