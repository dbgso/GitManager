package com.github.dbgso.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import lombok.Data;

@Data
public class Commit {

	private String message;
	private String author;
	private Date date;
	private String sha1;
	private List<String> parents;
	private String branch;
	private List<ModifiedFile> modifiedFiles;

	public static Commit valueOf(RevCommit revCommit) {
		Commit commit = new Commit();
		commit.message = revCommit.getFullMessage();
		PersonIdent author = revCommit.getAuthorIdent();
		commit.author = String.format("%s <%s>", author.getName(), author.getEmailAddress());
		commit.date = new Date(revCommit.getCommitTime());
		commit.sha1 = revCommit.getId().getName();

		RevCommit[] parents = revCommit.getParents();
		commit.parents = Arrays.stream(parents)//
				.map(revC -> revC.getId().getName())//
				.collect(Collectors.toList());

		return commit;
	}
}
