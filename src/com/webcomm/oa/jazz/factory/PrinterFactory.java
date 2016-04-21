package com.webcomm.oa.jazz.factory;

import com.webcomm.oa.jazz.print.Printer;

public class PrinterFactory {
	private static Printer printer = null;
	
	public static Printer getPrinter() {
		if (printer == null) {
			printer = new Printer();
		}
		return printer;
	}
}
