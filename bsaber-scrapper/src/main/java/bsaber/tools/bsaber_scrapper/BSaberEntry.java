package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BSaberEntry {
	private static final String DOWNLOAD_PATH = "Z:\\BSaberSongs\\";
	
	private static Logger cvLogger = LogManager.getLogger(BSaberEntry.class);
	private static int cvDownloadedTotal = 0;
	

	private String ivSongID;
	private String ivName;
	private String ivDownloadUrl;

	public BSaberEntry(String aSongID, String aName, String aDownloadUrl) {
		ivSongID = aSongID;
		ivName = aName;
		ivDownloadUrl = aDownloadUrl;
	}

	public String getSongID() {
		return ivSongID;
	}

	public String getName() {
		return ivName;
	}

	public String getDownloadUrl() {
		return ivDownloadUrl;
	}

	public static int getDownloadedTotal() {
		return cvDownloadedTotal;
	}
	
	private String getDownloadName() {
		String name = getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		return getSongID() + " (" + name + ").zip";
	}

	public boolean download() {
		File downloadFile = new File(DOWNLOAD_PATH + getDownloadName());
		try {
			if (!downloadFile.exists()) {
				URL url = new URL(getDownloadUrl());
				FileUtils.copyURLToFile(url, downloadFile);

				cvDownloadedTotal++;

				return true;
			} else {
				cvDownloadedTotal++;

				return true;
			}
		} catch (IOException e) {
		}

		return false;
	}

	@Override
	public String toString() {
		return getSongID() + " --> " + getName() + " (" + getDownloadUrl() + ")";
	}
}
