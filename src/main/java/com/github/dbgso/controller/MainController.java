package com.github.dbgso.controller;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.dbgso.service.GitManagementService;

@Controller
@EnableAutoConfiguration
@RequestMapping("/")
public class MainController {

	@Autowired
	GitManagementService service;

	@GetMapping
	public String index() {
		return "index";
	}

	@RequestMapping(value = "clone", method = RequestMethod.POST)
	public ResponseEntity<String> clone(@RequestParam("url") String url)
			throws InvalidRemoteException, TransportException, GitAPIException {
		service.clone("/tmp/git/test", url);

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
