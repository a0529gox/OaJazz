package com.webcomm.oa.jazz.execute;

import java.util.List;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IChangeSet;
import com.webcomm.oa.jazz.data.FileData;
import com.webcomm.oa.jazz.factory.RepositoryFactory;
import com.webcomm.oa.jazz.file.FileManager;
import com.webcomm.oa.jazz.repository.Repository;

public class CsCombineExecutor extends Executor {

	public CsCombineExecutor() {
		super();
	}

	@Override
	protected void doAction() throws TeamRepositoryException {
		Repository repo = RepositoryFactory.getRepository();
		
		List<IChangeSet> changeSets = repo.getChangeSets(getWorkItemIds());
		List<FileData> fileDatas = repo.getFileDatasFromChangeSets(changeSets);
		
		FileManager manager = new FileManager(true);
		manager.execute(fileDatas);
	}

	@Override
	protected void writeFile() {
		getPrinter().flush();
	}
}
