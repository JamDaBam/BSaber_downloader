package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Config {
	private static final Logger cvLogger = LogManager.getLogger(Config.class);

	// Inipath
	private static final String INI_FILENAME = "config.ini";

	// Iniproperties
	public static final String PROPERTY_PATH = "downloadpath";
	
	//Iniproperties defaults
	private static final String PROPERTY_PATH_DEFAULT = "downloads";

	private static Config cvInstance;

	private Properties ivProperties;

	private Config() {
		ivProperties = initializeConfig();
	}

	private Properties initializeConfig() {
		Properties properties = new Properties();

		if (new File(INI_FILENAME).exists()) {
			try (InputStream input = new FileInputStream(INI_FILENAME)) {
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (OutputStream output = new FileOutputStream(INI_FILENAME)) {
				properties.setProperty(PROPERTY_PATH, PROPERTY_PATH_DEFAULT);
				properties.store(output, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

	public static Config getInstance() {
		if (cvInstance == null) {
			cvInstance = new Config();
		}

		return cvInstance;
	}

	public void saveConfig() {
		try (OutputStream output = new FileOutputStream(INI_FILENAME)) {
			ivProperties.store(output, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String aKey) {
		return ivProperties.getProperty(aKey);
	}

	public void putProperty(String aKey, String aProperty) {
		ivProperties.put(aKey, aProperty);
	}
}
