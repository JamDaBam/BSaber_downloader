package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BSaberEntry {
	private static final Logger cvLogger = LogManager.getLogger(BSaberEntry.class);

	private static final String DOWNLOAD_PATH = "Z:\\BSaberSongs\\";
	private static final String ILLEGAL_CHARACTERS = "[^a-zA-Z0-9\\.\\-]";

	private static int cvNewDownloads = 0;
	private static int cvAlreadyDownloads = 0;
	private static int cvDownloadedTotal = 0;
	private static List<BSaberEntry> cvNewSongs = new ArrayList<>();
	private static List<BSaberEntry> cvSongsWithErrors = new ArrayList<>();

	private String ivSongID;
	private String ivName;
	private String ivDownloadUrl;
	private boolean ivDownloaded = false;

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

	public boolean isDownloaded() {
		return ivDownloaded;
	}

	private void downloadFinish() {
		ivDownloaded = true;
	}

	public static int getNewDownloads() {
		return cvNewDownloads;
	}

	public static int getAlreadyDownloads() {
		return cvAlreadyDownloads;
	}

	public static int getDownloadedTotal() {
		return cvDownloadedTotal;
	}

	public static List<BSaberEntry> getNewSongs() {
		return cvNewSongs;
	}

	public static List<BSaberEntry> getSongsWithErrors() {
		return cvSongsWithErrors;
	}

	public static void printNewSongs() {

		if (!getNewSongs().isEmpty()) {
			cvLogger.debug("NEW SONGS:");
			for (BSaberEntry bSaberEntry : getNewSongs()) {
				cvLogger.debug(bSaberEntry.toString());
			}
			cvLogger.debug("");
		}
	}

	public static void printSongsWithErrors() {

		if (!getSongsWithErrors().isEmpty()) {
			cvLogger.debug("SONGS WITH ERRORS");
			for (BSaberEntry bSaberEntry : getSongsWithErrors()) {
				cvLogger.debug(bSaberEntry.toString());
			}
			cvLogger.debug("");
		}
	}

	private String getDownloadName() {
		String name = getName().replaceAll(ILLEGAL_CHARACTERS, "_");
		return getSongID() + " (" + name + ").zip";
	}

	public boolean download() {
		File downloadFile = new File(DOWNLOAD_PATH + getDownloadName());
		try {
			if (!downloadFile.exists()) {
				URL url = new URL(getDownloadUrl());
				FileUtils.copyURLToFile(url, downloadFile);

				cvNewDownloads++;
				cvDownloadedTotal++;
				downloadFinish();

				cvNewSongs.add(this);

				return isDownloaded();
			} else {
				cvAlreadyDownloads++;
				cvDownloadedTotal++;
				downloadFinish();

				return isDownloaded();
			}
		} catch (IOException e) {
			e.printStackTrace();
			cvSongsWithErrors.add(this);
		}

		return isDownloaded();
	}

	@Override
	public String toString() {
		return getSongID() + " --> " + getName() + " (" + getDownloadUrl() + ")";
	}
}
