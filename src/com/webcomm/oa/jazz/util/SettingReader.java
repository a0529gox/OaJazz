package com.webcomm.oa.jazz.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingReader {
	
	public static final String SETTING_PATH = "setting.ini";
	public static final String PARAM_SEPARATOR = "=";
	
	private static final Path settingFilePath = Paths.get(System.getProperty("user.dir"), SETTING_PATH);
	
	private static SettingReader self = null;
	
	private String jazzUrl = null;
	private String partialName = null;
	private String partialOwnerName = null;
	private String username = null;
	private String password = null;
	private String localProjectDir = null;
	private String outputDir = null;

	private SettingReader() {
		init();
	}
	
	public static SettingReader getInstance() {
		if (self == null) {
			self = new SettingReader();
		}
		return self;
	}

	public static void main(String[] args) throws Exception {
		SettingReader obj = new SettingReader();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			System.out.println(field.get(obj));
		}
	}
	
	private void init() {
		try {
			Files.lines(settingFilePath)
				.filter(line -> line.indexOf(PARAM_SEPARATOR) > 0)
				.forEach(line -> {
					int idx = line.indexOf(PARAM_SEPARATOR);
					String name = line.substring(0, idx);
					String value = line.substring(idx + 1);
					
					for (Field field : this.getClass().getDeclaredFields()) {
						try {
							String fieldName = field.getName();
							if (fieldName.equals(name)) {
								field.set(this, value);
								break;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getJazzUrl() {
		return jazzUrl;
	}

	public String getPartialName() {
		return partialName;
	}

	public String getPartialOwnerName() {
		return partialOwnerName;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getLocalProjectDir() {
		return localProjectDir;
	}

	public String getOutputDir() {
		return outputDir;
	}
	
}
