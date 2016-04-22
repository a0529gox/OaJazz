package com.webcomm.oa.jazz.file;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.ibm.team.scm.common.IChange;
import com.webcomm.oa.jazz.data.FileData;
import com.webcomm.oa.jazz.factory.PrinterFactory;
import com.webcomm.oa.jazz.print.Printer;
import com.webcomm.oa.jazz.util.FileUtils;
import com.webcomm.oa.jazz.util.SettingReader;

public class FileManager {
	private String inputLoc = SettingReader.getInstance().getLocalProjectDir();
	private String outputLoc = SettingReader.getInstance().getOutputDir();
	private Printer printer = PrinterFactory.getPrinter();
	
	public FileManager() {
		this(false);
	}
	
	public FileManager(boolean cleanOutput) {
		if (cleanOutput) {
			printer.print("Clean the files...");
			Path outputP = Paths.get(outputLoc);
			for (File f : outputP.toFile().listFiles()) {
				FileUtils.remove(f);
			}
		}
	}
	
	public void execute(List<FileData> fileDatas) {
		for (FileData fileData : fileDatas) {
			execute(fileData);
		}
	}
	
	public void execute(FileData fileData) {
		if (fileData == null) {
			return;
		}
		
		String fileName = fileData.getFullName();
		if (fileName == null || fileName.trim().length() == 0) {
			printer.print(fileData.getName() + " is not exist.");
			return;
		}
		
		if (fileData.getKind() == IChange.DELETE) {
			printer.print("DELETE kind " + fileName);
		} else {
			copy(fileName);
		}
	}
	
	private void copy(String fileName) {
		String path = inputLoc + fileName;
		File file = new File(path);
		if (file.isFile()) {
			Path input = Paths.get(file.getPath());
			Path output = Paths.get(outputLoc + fileName);
			copy(input, output);
		} else {
			printer.print("No exist " + path);
		}
	}
	
	private void copy(Path input, Path output) {
		boolean repeat = true;
		while (repeat) {
			try {
				if (!Files.exists(input, LinkOption.NOFOLLOW_LINKS)) {
				}
				Files.copy(input, output);
				printer.print("Copying to " + output.toString());
				repeat = false;
			} catch (NoSuchFileException e) {
				File path = output.getParent().toFile();
				path.mkdirs();
				printer.print("Create root " + path.getPath());
			} catch (FileAlreadyExistsException e) {
				printer.print("Already exist " + output.toString());
				repeat = false;
			} catch (Exception e) {
				e.printStackTrace();
				repeat = false;
			}
		}
	}

	public String getInputLoc() {
		return inputLoc;
	}

	public void setInputLoc(String inputLoc) {
		this.inputLoc = inputLoc;
	}

	public String getOutputLoc() {
		return outputLoc;
	}

	public void setOutputLoc(String outputLoc) {
		this.outputLoc = outputLoc;
	}
	
}
