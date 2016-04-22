package com.webcomm.oa.jazz.util;

import java.io.File;

import com.webcomm.oa.jazz.factory.PrinterFactory;

public class FileUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static boolean remove(File f) {
		if (f.isDirectory()) {
			for (File f2 : f.listFiles()) {
				remove(f2);
			}
		}
		f.delete();
		PrinterFactory.getPrinter().print("Remove " + f.getAbsolutePath());
		return true;
	}
}
