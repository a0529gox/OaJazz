package com.webcomm.oa.jazz.execute;

import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.webcomm.oa.jazz.factory.PrinterFactory;
import com.webcomm.oa.jazz.print.Printer;


public abstract class Executor {
	private Printer printer = PrinterFactory.getPrinter();
	private int[] workItemIds = {};
	
	public void execute(int... workItemIds) {
		
		if (workItemIds.length == 0) {
			return;
		} else {
			setWorkItemIds(workItemIds);
		}
		
		try {
			TeamPlatform.startup();
			
			doAction();
			writeFile();
			
			System.out.println("done.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TeamPlatform.shutdown();
		}
	}
	
	protected abstract void doAction() throws TeamRepositoryException;
	
	protected abstract void writeFile();


	protected Printer getPrinter() {
		return printer;
	}

	public void setPrinter(Printer printer) {
		this.printer = printer;
	}

	public int[] getWorkItemIds() {
		return workItemIds;
	}

	public void setWorkItemIds(int[] workItemIds) {
		this.workItemIds = workItemIds;
	}
	
}
