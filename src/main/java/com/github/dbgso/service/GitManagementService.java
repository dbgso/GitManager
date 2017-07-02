package com.github.dbgso.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

@Service
public class GitManagementService {

	private Git git;

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

}
