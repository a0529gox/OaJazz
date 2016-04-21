package com.webcomm.oa.jazz.factory;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.common.TeamRepositoryException;
import com.webcomm.oa.jazz.repository.Repository;
import com.webcomm.oa.jazz.util.SettingReader;

public class RepositoryFactory {
	private static final String JAZZURL = SettingReader.getInstance().getJazzUrl();
	private static final String PARTIAL_NAME = SettingReader.getInstance().getPartialName();
	private static final String PARTIAL_OWNER_NAME = SettingReader.getInstance().getPartialOwnerName();
    private static final String USR = SettingReader.getInstance().getUsername();
    private static final String PWD = SettingReader.getInstance().getPassword();
    
    private static IProgressMonitor monitor = MonitorFactory.getMonitor();
	
	public static Repository getRepository() throws TeamRepositoryException {
		Repository repo = Repository.getRepository(JAZZURL, PARTIAL_NAME, PARTIAL_OWNER_NAME, USR, PWD);
		if (repo.getiTeamRepository() == null) {
			repo.setMonitor(monitor);
			repo.login();
		}
		return repo;
	}
}
