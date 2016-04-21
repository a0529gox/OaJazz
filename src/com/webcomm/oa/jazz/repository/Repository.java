package com.webcomm.oa.jazz.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.links.client.ILinkManager;
import com.ibm.team.links.common.IItemReference;
import com.ibm.team.links.common.ILink;
import com.ibm.team.links.common.ILinkCollection;
import com.ibm.team.links.common.IReference;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ILoginHandler2;
import com.ibm.team.repository.client.ILoginInfo2;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.client.login.UsernameAndPasswordLoginInfo;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.common.IChange;
import com.ibm.team.scm.common.IChangeSet;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IFolder;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IAncestorReport;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;
import com.ibm.team.scm.common.internal.dto.NameItemPair;
import com.ibm.team.scm.common.internal.dto.WorkspaceSearchCriteria;
import com.ibm.team.scm.common.links.ILinkConstants;
import com.ibm.team.workitem.client.IWorkItemClient;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.webcomm.oa.jazz.data.FileData;
import com.webcomm.oa.jazz.factory.PrinterFactory;
import com.webcomm.oa.jazz.print.Printer;

public class Repository {
	private static Repository self = null;
	private final String jazzUrl;
	private final String partialName;
	private final String partialOwnerName;
	private final String username;
	private final String password;
	
	private IProgressMonitor monitor = null;
	private ITeamRepository itRepo = null;
	private IWorkspaceManager workspaceManager = null;
	private IItemManager itemManager = null;
	private IWorkItemClient workItemClient = null;
	private ILinkManager linkManager = null;
	
	private Printer printer = PrinterFactory.getPrinter();
	
	
	private Repository(String jazzUrl, String partialName, String partialOwnerName, String username, String password) {
		this.jazzUrl = jazzUrl;
		this.partialName = partialName;
		this.partialOwnerName = partialOwnerName;
		this.username = username;
		this.password = password;
	}
	
	public static Repository getRepository(String jazzUrl, String partialName, String partialOwnerName, String username, String password) {
		if (self == null) {
			synchronized(Repository.class){
                if(self == null) {
                	self = new Repository(jazzUrl, partialName, partialOwnerName, username, password);
                }
            }
		}
		return self;
	}
	
	public void login() throws TeamRepositoryException {
		itRepo = TeamPlatform.getTeamRepositoryService().getTeamRepository(jazzUrl);
		itRepo.registerLoginHandler(new ILoginHandler2() {
			@Override
			public ILoginInfo2 challenge(ITeamRepository arg0) {
				return new UsernameAndPasswordLoginInfo(username, password);
			}
		});
		itRepo.login(monitor);
		
		initParamsAfterLogin();
	}
	
	private void initParamsAfterLogin() {
		workspaceManager = (IWorkspaceManager)itRepo.getClientLibrary(IWorkspaceManager.class);
		itemManager = itRepo.itemManager();
		workItemClient = (IWorkItemClient)itRepo.getClientLibrary(IWorkItemClient.class);
		linkManager = (ILinkManager) itRepo.getClientLibrary(ILinkManager.class);
	}
	
	public List<IChangeSet> getChangeSets(int workItemId) throws TeamRepositoryException {
		
		IWorkItem workItem;
		try {
			workItem = workItemClient.findWorkItemById(workItemId, IWorkItem.FULL_PROFILE, monitor);
			
			printer.print("["+workItem.getId()+"] "+workItem.getHTMLSummary().getPlainText());
		} catch (Exception e) {
			printer.print("WorkItem編號: [" + workItemId + "] 你沒有權限取得或者不存在。");
			return new ArrayList<IChangeSet>();
		}
		
		IItemReference workItemReference = linkManager.referenceFactory().createReferenceToItem(workItem);
		ILinkCollection linkCollection = linkManager.findLinksByTarget(ILinkConstants.CHANGESET_WORKITEM_LINKTYPE_ID, workItemReference, monitor).getAllLinksFromHereOn();
		
		if (linkCollection.isEmpty()) {
			printer.print("Work item has no change sets.");
			System.exit(0);
		}

		List<IChangeSetHandle> changeSetHandles = new ArrayList<IChangeSetHandle>();
		List<IReference> changeSetReferences = new ArrayList<IReference>();
		
		for (ILink link: linkCollection) {
			IChangeSetHandle changeSetHandle = (IChangeSetHandle) link.getSourceRef().resolve();
			changeSetReferences.add(link.getSourceRef());
			changeSetHandles.add(changeSetHandle);
		}
		
		@SuppressWarnings("unchecked")
		List<IChangeSet> changeSets = itemManager.fetchCompleteItems(changeSetHandles, IItemManager.DEFAULT, monitor);
		return changeSets;
	}
	
	public List<FileData> getFileDatasFromChangeSet(IChangeSet changeSet) throws TeamRepositoryException {
		printer.print("Change Set " + changeSet.getLastChangeDate() + ", " + changeSet.getComment());
		IWorkspaceSearchCriteria wsSearchCriteria = WorkspaceSearchCriteria.FACTORY.newInstance(); 
		wsSearchCriteria.setPartialNameIgnoreCase(partialName);
		wsSearchCriteria.setPartialOwnerNameIgnoreCase(partialOwnerName);
		
		List<IWorkspaceHandle> workspaceHandles = workspaceManager.findWorkspaces(wsSearchCriteria, Integer.MAX_VALUE, monitor);
		for (IWorkspaceHandle workspaceHandle : workspaceHandles) {
			try {
				IWorkspaceConnection iWsConn = workspaceManager.getWorkspaceConnection(workspaceHandle, monitor);
				
				IConfiguration iConfig = iWsConn.configuration(changeSet.getComponent());
				
				List<FileData> result = new ArrayList<FileData>();
				
				for (Object o: changeSet.changes()) {
					IChange change = (IChange)o;
					FileData fileData = newFileData(change, workspaceManager, iConfig);
					printer.print(fileData.toString());
					result.add(fileData);
				}
				return result;
			} catch (Exception e) {
				
			}
		}
		System.out.println("ChangeSet[" + changeSet.getComment() + "]找不到對應的Workspace");
		return new ArrayList<>();
	}
	
	public List<FileData> getFileDatasFromChangeSets(List<IChangeSet> changeSets) throws TeamRepositoryException {
		Map<UUID, FileData> itemMap = new HashMap<UUID, FileData>();
		
		sortChangeSets(changeSets);
		
		for (IChangeSet changeSet : changeSets) {
			List<FileData> fileDatas = getFileDatasFromChangeSet(changeSet);
			for (FileData fileData : fileDatas) {
				itemMap.put(fileData.getItemId(), fileData);
			}
		}
		
		List<FileData> fileDatas = new ArrayList<FileData>(itemMap.values());
		
		sortFileDatas(fileDatas);
		
		return fileDatas;
	}
	
	public List<IChangeSet> getChangeSets(int... workItemIds) throws TeamRepositoryException {
		List<IChangeSet> changeSets = new ArrayList<IChangeSet>();
		for (int workItemId : workItemIds) {
			List<IChangeSet> cs = getChangeSets(workItemId);
			changeSets.addAll(cs);
		}
		return changeSets;
	}
	
	private List<IChangeSet> sortChangeSets(List<IChangeSet> changeSets) {
		Collections.sort(changeSets, new Comparator<IChangeSet>() {
			@Override
			public int compare(IChangeSet o1, IChangeSet o2) {
				return o1.getLastChangeDate().compareTo(o2.getLastChangeDate());
			}
		});
		return changeSets;
	}
	
	private List<FileData> sortFileDatas(List<FileData> fileDatas) {
		Collections.sort(fileDatas, new Comparator<FileData>() {
			@Override
			public int compare(FileData o1, FileData o2) {
				int compare = o1.getFullName().compareTo(o2.getFullName());
				if (compare != 0) {
					return compare;
				} else {
					return o1.getKind() - o2.getKind();
				}
			}
		});
		return fileDatas;
	}
	
	private FileData newFileData(IChange change, IWorkspaceManager workspaceManager, IConfiguration iConfig) throws TeamRepositoryException {
		FileData fileData = new FileData();
		int kind = change.kind();
		fileData.setKind(kind);
		
		IVersionableHandle itemHandel = null;
		if (kind != IChange.DELETE) {
			itemHandel = change.afterState();
		}
		if (change.kind() == IChange.RENAME
				|| change.kind() == IChange.DELETE) {
			itemHandel = change.beforeState();
		}
		IVersionable item = workspaceManager.versionableManager().fetchCompleteState(itemHandel, monitor);
		fileData.setItem(item);
		
		fileData.setItemId(item.getItemId());
		fileData.setStateId(item.getStateId());
		fileData.setName(item.getName());
	
		
		String directoryPath = "";

		List<IVersionableHandle> list = new ArrayList<IVersionableHandle>();
		list.add(itemHandel);
		
		IAncestorReport ancestor = (IAncestorReport)iConfig.determineAncestorsInHistory(list, monitor).get(0);
		@SuppressWarnings("unchecked")
		List<NameItemPair> nameItemPairList = ancestor.getNameItemPairs();
        for (NameItemPair nameItemPair : nameItemPairList) { 
        	IVersionableHandle pathItemHandle = nameItemPair.getItem();
        	if (pathItemHandle.getStateId() == null) {
//        		printer.print(fileData.getName());
        		continue;
        	}
            Object pathItem = workspaceManager.versionableManager() 
            			.fetchCompleteState(pathItemHandle, null); 
            String pathName = ""; 
            if (pathItem instanceof IFolder) { 
                pathName = ((IFolder) pathItem).getName(); 
            } 
            else if (pathItem instanceof IFileItem) { 
                pathName = ((IFileItem) pathItem).getName(); 
            } 
            if (!pathName.equals("")) {
                directoryPath = directoryPath + "\\" + pathName; 
            }
        } 
	    fileData.setFullName(directoryPath);
	    
	    return fileData;
	}
	
	public boolean isLogin() {
		return itRepo != null;
	}

	public ITeamRepository getiTeamRepository() {
		return itRepo;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
