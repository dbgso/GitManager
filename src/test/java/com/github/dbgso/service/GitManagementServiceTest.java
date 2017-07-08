package com.github.dbgso.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class GitManagementServiceTest {

	private static final String TMP_GIT_TEST_REPOSITORY = "/tmp/git/test-repository";
	GitManagementService service = new GitManagementService();

	@Test
	public void cloneが成功すること() throws InvalidRemoteException, TransportException, IOException, GitAPIException {
		String path = TMP_GIT_TEST_REPOSITORY;
		service.clone(path, "https://github.com/bug-so/emacs.d.git");
		assertEquals(true, new File(path).exists());
	}

	@Test
	public void logの確認() throws IOException, NoHeadException, GitAPIException {
		// service.init(TMP_GIT_TEST_REPOSITORY);
		// List<RevCommit> log = service.Log();
		// assertEquals(42, log.size());
		fail();
	}
}
