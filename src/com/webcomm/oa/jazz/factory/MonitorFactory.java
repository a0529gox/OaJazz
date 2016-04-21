package com.webcomm.oa.jazz.factory;

import org.eclipse.core.runtime.IProgressMonitor;

import com.webcomm.oa.jazz.data.SysoutProgressMonitor;

public class MonitorFactory {
	public static IProgressMonitor getMonitor() {
		return new SysoutProgressMonitor();
	}
}
