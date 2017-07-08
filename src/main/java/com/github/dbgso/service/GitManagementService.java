package com.github.dbgso.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dbgso.config.GitConfig;
import com.github.dbgso.model.Commit;

@Service
public class GitManagementService {

	private Git git;
	@Autowired
	GitConfig config;

	public void init(String path) throws IOException {
		git = Git.open(new File(path));
	}

	public void clone(String path, String uri) throws InvalidRemoteException, TransportException, GitAPIException {
		File gitDir = new File(path);
		if (!gitDir.exists())
			git = Git.cloneRepository().setURI(uri).setDirectory(gitDir).call();
	}

	public void pull(String uri) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		git.pull();
	}

	public void pull() {

	}

	public List<RevCommit> Log() throws IOException, NoHeadException, GitAPIException {
		LogCommand log = git.log();
		Iterable<RevCommit> call = log.call();
		List<RevCommit> list = new ArrayList<>();
		call.forEach(c -> list.add(c));
		return list;
	}

	public List<Commit> getCommits(String name) throws NoHeadException, GitAPIException, IOException {
		initFromRepositoryName(name);
		List<Commit> commits = new ArrayList<>();
		RevCommit HEAD = git.log().call().iterator().next();
		git.log().call().forEach(c -> {
			commits.add(Commit.valueOf(c));
		});
		Collections.sort(commits, (b, a) -> {
			return a.getDate().compareTo(b.getDate());
		});
		return commits;
	}

	public List<String> getBranches(String repoName) throws IOException, GitAPIException {
		initFromRepositoryName(repoName);
		List<Ref> call = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		return call.stream().map(b -> b.getName()).collect(Collectors.toList());
	}

	public void initFromRepositoryName(String name) throws IOException {
		File path = new File(new File(config.getRepositoryPath()), name);
		init(path.getAbsolutePath());
	}

	public List<Commit> searchByMessage(String message) throws NoHeadException, GitAPIException {
		return StreamSupport.stream(git.log().call().spliterator(), false)//
				.filter(c -> c.getShortMessage().contains(message))//
				.map(c -> Commit.valueOf(c))//
				.collect(Collectors.toList());
	}
}
