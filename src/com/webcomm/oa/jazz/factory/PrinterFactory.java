package com.webcomm.oa.jazz.factory;

import com.webcomm.oa.jazz.print.Printer;

public class PrinterFactory {
	private static Printer printer = null;
	
	static {
		if (printer == null) {
			printer = new Printer();
		}
	}
	
	public static Printer getPrinter() {
		return printer;
	}
}
