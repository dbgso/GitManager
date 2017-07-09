package com.github.dbgso.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffAlgorithm;
import org.eclipse.jgit.diff.DiffAlgorithm.SupportedAlgorithm;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.MaxCountRevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dbgso.config.GitConfig;
import com.github.dbgso.model.Commit;
import com.github.dbgso.model.ModifiedFile;

@Service
public class GitManagementService {

	@Autowired
	GitConfig config;

	private Git init(String path) throws IOException {
		return Git.open(new File(path));
	}

	public Git clone(String repositoryName, String uri)
			throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		File gitDir = getRepositoryPath(repositoryName);
		if (gitDir.exists())
			return init(gitDir.getAbsolutePath());
		return Git.cloneRepository().setURI(uri).setDirectory(gitDir).call();
	}

	public List<RevCommit> log(String name) throws IOException, NoHeadException, GitAPIException {
		Git git = initFromRepositoryName(name);
		return StreamSupport.stream(git.log().call().spliterator(), false)//
				.collect(Collectors.toList());
	}

	public List<RevCommit> log(String repoName, Integer limit) throws IOException, NoHeadException, GitAPIException {
		Git git = initFromRepositoryName(repoName);
		return StreamSupport
				.stream(git.log()//
						.setRevFilter(MaxCountRevFilter.create(limit))//
						.call().spliterator(), false)//
				.collect(Collectors.toList());
	}

	public List<RevCommit> log(String repoName, int start, int end) throws IOException {
		Git git = initFromRepositoryName(repoName);
		Map<String, Ref> refs = git.getRepository().getAllRefs();
		org.eclipse.jgit.revwalk.filter.MaxCountRevFilter.create(end);
		try (RevWalk walk = new RevWalk(git.getRepository())) {

		}
		throw new UnsupportedOperationException();
	}

	public List<Commit> getCommits(String name) throws NoHeadException, GitAPIException, IOException {
		Git git = initFromRepositoryName(name);
		return StreamSupport.stream(git.log().call().spliterator(), false)//
				.map(revc -> Commit.valueOf(revc))//
				.sorted((obj1, obj2) -> {
					return obj2.getDate().compareTo(obj1.getDate());
				})//
				.collect(Collectors.toList());
	}

	public List<String> getBranches(String repoName) throws IOException, GitAPIException {
		Git git = initFromRepositoryName(repoName);
		List<Ref> call = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
		return call.stream().map(b -> b.getName()).collect(Collectors.toList());
	}

	public Git initFromRepositoryName(String name) throws IOException {
		File path = getRepositoryPath(name);
		return init(path.getAbsolutePath());
	}

	public List<Commit> searchByMessage(String name, String message)
			throws NoHeadException, GitAPIException, IOException {
		Git git = initFromRepositoryName(name);
		return StreamSupport.stream(git.log().call().spliterator(), false)//
				.filter(c -> c.getShortMessage().contains(message))//
				.map(c -> Commit.valueOf(c))//
				.collect(Collectors.toList());
	}

	public Commit getCommit(String repoName, String hash) throws NoHeadException, GitAPIException, IOException {
		Git git = initFromRepositoryName(repoName);

		Repository repository = git.getRepository();
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit revCommit = getRevCommit(walk, repository, hash);
			RevCommit parentCommit = searchParent(repository, walk, revCommit);

			Commit commit = Commit.valueOf(revCommit);

			List<ModifiedFile> modifiedFiles = execDiff(repository, revCommit, parentCommit);
			commit.setModifiedFiles(modifiedFiles);

			return commit;
		}
	}

	private RevCommit searchParent(Repository repository, RevWalk walk, RevCommit commit)
			throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		int parentCount = commit.getParentCount();
		if (parentCount == 0)
			return null;
		RevCommit revParent = commit.getParent(parentCount - 1);

		String sha1 = revParent.getId().name();
		ObjectId objectId = repository.resolve(sha1);
		return walk.parseCommit(objectId);
	}

	private List<ModifiedFile> execDiff(Repository repository, RevCommit revCommit, RevCommit parentCommit)
			throws IOException {
		try (DiffFormatter diffFormatter = new DiffFormatter(System.out)) {
			diffFormatter.setRepository(repository);

			RevTree toTree = revCommit.getTree();
			RevTree parentTree = parentCommit == null ? null : parentCommit.getTree();

			List<DiffEntry> diffEntries = diffFormatter.scan(parentTree, toTree);

			List<ModifiedFile> modifiedFiles = diffEntries.stream()//
					.map(diff -> {
						ModifiedFile file = ModifiedFile.valueOf(diff);
						ChangeType changeType = diff.getChangeType();
						if (changeType == ChangeType.RENAME || changeType == ChangeType.COPY)
							return file;
						try {
							RawText old = RawText.EMPTY_TEXT;
							if (changeType != ChangeType.ADD)
								old = readText(diff.getOldId(), repository.newObjectReader());
							RawText newText = RawText.EMPTY_TEXT;
							if (changeType != ChangeType.DELETE)
								newText = readText(diff.getNewId(), repository.newObjectReader());
							DiffAlgorithm algorithm = DiffAlgorithm.getAlgorithm(repository.getConfig().getEnum(//
									ConfigConstants.CONFIG_DIFF_SECTION, //
									null, //
									ConfigConstants.CONFIG_KEY_ALGORITHM, //
									SupportedAlgorithm.HISTOGRAM));
							EditList editList = algorithm.diff(RawTextComparator.DEFAULT, old, newText);
							StringBuilder sb = new StringBuilder();
							editList.forEach(e -> {
								file.setAddCount(file.getAddCount() + e.getLengthB());
								file.setDeleteCount(file.getDeleteCount() + e.getLengthA());
							});
							file.setPatch(sb.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
						return file;
					})//
					.collect(Collectors.toList());
			return modifiedFiles;
		} catch (IOException e) {
			throw e;
		}
	}

	private static RawText readText(AbbreviatedObjectId blobId, ObjectReader reader) throws IOException {
		ObjectLoader oldLoader = reader.open(blobId.toObjectId(), Constants.OBJ_BLOB);
		return new RawText(oldLoader.getCachedBytes());
	}

	public String getText(String repoName, String hash, String path) throws IOException {
		Git git = initFromRepositoryName(repoName);
		Repository repository = git.getRepository();
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit revCommit = getRevCommit(walk, repository, hash);
			ObjectReader reader = repository.newObjectReader();
			return getFileString(path, revCommit, reader);
		}
	}

	private String getFileString(String path, RevCommit revCommit, ObjectReader reader) throws MissingObjectException,
			IncorrectObjectTypeException, CorruptObjectException, IOException, UnsupportedEncodingException {
		TreeWalk treeWalk = TreeWalk.forPath(reader, path, revCommit.getTree());
		// 存在しない場合はから文字列を返す
		if (treeWalk == null)
			return "";
		if (treeWalk.getTreeCount() == 0)
			throw new IllegalArgumentException("nee");
		byte[] data = reader.open(treeWalk.getObjectId(0)).getBytes();
		return new String(data, "utf-8");
	}

	public List<String> getTextPair(String name, String hash, String path) throws IOException {
		String parentHash = getParentHash(name, hash);

		String currentData = getText(name, hash, path);
		String parentData = parentHash == null ? "" : getText(name, parentHash, path);

		return Arrays.asList(currentData, parentData);
	}

	private String getParentHash(String name, String hash)
			throws IOException, MissingObjectException, IncorrectObjectTypeException, AmbiguousObjectException {
		Git git = initFromRepositoryName(name);
		Repository repository = git.getRepository();
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = getRevCommit(walk, repository, hash);
			RevCommit parentCommit = searchParent(repository, walk, commit);
			// 親コミットがなければnullを返す
			if (parentCommit == null)
				return null;
			return parentCommit.getId().name();
		}
	}

	private static RevCommit getRevCommit(RevWalk walk, Repository repository, String hash)
			throws RevisionSyntaxException, MissingObjectException, IncorrectObjectTypeException,
			AmbiguousObjectException, IOException {
		ObjectId objectId = repository.resolve(hash);
		if (objectId == null)
			throw new IllegalArgumentException();
		return walk.parseCommit(objectId);
	}

	/**
	 * Delete project directory.
	 * 
	 * @param projectName
	 */
	public boolean delete(String projectName) {
		File path = getRepositoryPath(projectName);
		try {
			FileUtils.delete(path, FileUtils.RECURSIVE);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private File getRepositoryPath(String projectName) {
		return new File(new File(config.getRepositoryPath()), projectName);
	}

}
