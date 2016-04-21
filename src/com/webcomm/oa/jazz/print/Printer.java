package com.webcomm.oa.jazz.print;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.webcomm.oa.jazz.util.SettingReader;

public class Printer {

	private StringBuilder msgs = null;
	private String output = SettingReader.getInstance().getOutputDir();
	private String fileName = "list.txt"; 
	
	private boolean append = false;
	
	public Printer() {
		newMsgs();
	}
	
	private void newMsgs() {
		msgs = new StringBuilder();
	}
	
	public void print(String str) {
		msgs.append(str)
			.append(System.getProperty("line.separator"));
		System.out.println(str);
	}
	
	private String getFilePath() {
		if (output != null || output.trim().length() != 0) {
			char last = output.charAt(output.length() - 1);
			if (last != '\\' || last != '/') {
				output += '/';
			}
			return output + fileName;
		}
		return null;
	}
	
	private void writeFile() {
		File file = new File(getFilePath());
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file, append));
			bw.write(msgs.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void flush() {
		writeFile();
		newMsgs();
	}
}
