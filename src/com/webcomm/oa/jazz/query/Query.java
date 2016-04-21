package com.webcomm.oa.jazz.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.IContributor;
import com.ibm.team.workitem.client.IAuditableClient;
import com.ibm.team.workitem.client.IQueryClient;
import com.ibm.team.workitem.common.expression.AttributeExpression;
import com.ibm.team.workitem.common.expression.Expression;
import com.ibm.team.workitem.common.expression.IQueryableAttribute;
import com.ibm.team.workitem.common.expression.QueryableAttributes;
import com.ibm.team.workitem.common.expression.Term;
import com.ibm.team.workitem.common.expression.Term.Operator;
import com.ibm.team.workitem.common.model.AttributeOperation;
import com.ibm.team.workitem.common.model.IWorkItem;
import com.ibm.team.workitem.common.query.IQueryResult;
import com.ibm.team.workitem.common.query.IResolvedResult;
import com.webcomm.oa.jazz.factory.MonitorFactory;
import com.webcomm.oa.jazz.factory.RepositoryFactory;
import com.webcomm.oa.jazz.repository.Repository;

public class Query {

	public Query() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] main) throws Exception {
		TeamPlatform.startup();
		Repository repo = RepositoryFactory.getRepository();
		ITeamRepository iteamRepo = repo.getiTeamRepository();
		IProgressMonitor monitor = MonitorFactory.getMonitor();
		
		
		IAuditableClient auditableClient = (IAuditableClient) iteamRepo.getClientLibrary(IAuditableClient.class);

		IQueryClient queryClient = (IQueryClient) iteamRepo.getClientLibrary(IQueryClient.class);
		
		IProcessItemService connect = (IProcessItemService)iteamRepo.getClientLibrary(IProcessItemService.class);
		
		List<IProjectArea> projAreas = (List<IProjectArea>)connect.findAllProjectAreas(null, monitor);
		IProjectArea currentProjectArea = null;
		for (IProjectArea projectArea : projAreas) {
			if (projectArea.getName().equals("FDC-OA")) {
				currentProjectArea = projectArea;
			}
		}
		System.out.println(currentProjectArea);



		IQueryableAttribute attrProjArea = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(currentProjectArea, IWorkItem.PROJECT_AREA_PROPERTY, auditableClient, null);
		Expression expressionProjArea = new AttributeExpression(attrProjArea, AttributeOperation.EQUALS, currentProjectArea);
		
		IQueryableAttribute attrSummary = QueryableAttributes.getFactory(IWorkItem.ITEM_TYPE).findAttribute(currentProjectArea, IWorkItem.SUMMARY_PROPERTY, auditableClient, null);
		Expression expressionSummary = new AttributeExpression(attrSummary, AttributeOperation.CONTAINS, "QIF");
		
		Term expressions = new Term(Operator.AND);
		expressions.add(expressionProjArea);
		expressions.add(expressionSummary);

		IQueryResult<IResolvedResult<IWorkItem>> results = queryClient.getResolvedExpressionResults(currentProjectArea, expressions, IWorkItem.FULL_PROFILE);
		List<IWorkItem> workitems = new ArrayList<IWorkItem>();
		
		while (results.hasNext(monitor)) {
			IResolvedResult<IWorkItem> result = results.next(monitor);
			workitems.add(result.getItem());
		}
		
		for (IWorkItem workitem : workitems) {
			System.out.println("Id=" + workitem.getId() + ", Summary=" + workitem.getHTMLSummary() + ", Owner=" + ((IContributor)repo.getiTeamRepository().itemManager().fetchCompleteItem(workitem.getOwner(), IItemManager.DEFAULT, null)).getName() + ", HTMLDescription=" + workitem.getHTMLDescription());
		}
		System.out.println("done.");
	}
	

}
