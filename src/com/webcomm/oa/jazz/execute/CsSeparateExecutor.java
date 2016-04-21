package com.webcomm.oa.jazz.execute;

import java.util.List;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.common.IChangeSet;
import com.webcomm.oa.jazz.factory.RepositoryFactory;
import com.webcomm.oa.jazz.repository.Repository;

public class CsSeparateExecutor extends Executor {

	public CsSeparateExecutor() {
		super();
	}

	@Override
	protected void doAction() throws TeamRepositoryException {
		Repository repo = RepositoryFactory.getRepository();
		List<IChangeSet> changeSets = repo.getChangeSets(getWorkItemIds());
		for (IChangeSet changeSet : changeSets) {
			repo.getFileDatasFromChangeSet(changeSet);
		}
	}

	@Override
	protected void writeFile() {
		getPrinter().flush();
	}

}
