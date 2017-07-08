package com.github.dbgso.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;

import lombok.Data;

@Data
public class Commit {

	private String message;
	private String author;
	private Date date;
	private String sha1;
	private String[] parents;
	private String branch;
	private ModifiedFile[] modifiedFiles;

	public static Commit valueOf(RevCommit revCommit) {
		Commit commit = new Commit();
		commit.message = revCommit.getFullMessage();
		PersonIdent author = revCommit.getAuthorIdent();
		commit.author = String.format("%s <%s>", author.getName(), author.getEmailAddress());
		commit.date = new Date(revCommit.getCommitTime());
		commit.sha1 = revCommit.getId().getName();

		RevCommit[] parents = revCommit.getParents();
		commit.parents = new String[parents.length];
		for (int i = 0; i < parents.length; i++) {
			RevCommit parent = parents[i];
			commit.parents[i] = parent.getId().getName();
		}

		return commit;
	}
}
