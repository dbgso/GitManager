package com.github.dbgso.model;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import lombok.Data;

@Data
public class ModifiedFile {

	private ChangeType changeType;
	private String oldPath;
	private String newPath;
	private String patch;

	public static ModifiedFile valueOf(DiffEntry diff) {
		ModifiedFile modifiedFile = new ModifiedFile();
		modifiedFile.changeType = diff.getChangeType();

		modifiedFile.oldPath = diff.getOldPath();
		modifiedFile.newPath = diff.getNewPath();

		return modifiedFile;
	}

}
