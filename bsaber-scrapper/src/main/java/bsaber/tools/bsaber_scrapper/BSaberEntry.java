package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BSaberEntry {
	private static final Logger cvLogger = LogManager.getLogger(BSaberEntry.class);

	private static final String ILLEGAL_CHARACTERS = "[^a-zA-Z0-9()\\., \\-]";

	private static int cvNewDownloads = 0;
	private static int cvAlreadyDownloads = 0;
	private static int cvDownloadedTotal = 0;
	private static Set<BSaberEntry> cvNewSongs = new HashSet<>();
	private static Set<BSaberEntry> cvSongsWithErrors = new HashSet<>();

	private BSaberEntryRawData ivRawData;

	private String ivMapper;
	private String ivTitle;
	private String ivSongId;
	private String ivDownloadUrl;
	private boolean ivDownloaded = false;

	private int ivThumbsUp = 0;
	private int ivThumbsDown = 0;

	private List<String> ivDifficulties = new ArrayList<>();

	public BSaberEntry(BSaberEntryRawData aRawData) {
		ivRawData = aRawData;
	}

	public BSaberEntry(String aSongId, String aName, String aDownloadUrl) {
		ivSongId = aSongId;
		ivTitle = aName;
		ivDownloadUrl = aDownloadUrl;
	}

	public BSaberEntryRawData getRawData() {
		return ivRawData;
	}

	public String getMapper() {
		return ivMapper;
	}

	public void setMapper(String aMapper) {
		ivMapper = aMapper;
	}

	public int getThumbsUp() {
		return ivThumbsUp;
	}

	public void setThumbsUp(int aThumbsUp) {
		ivThumbsUp = aThumbsUp;
	}

	public int getThumbsDown() {
		return ivThumbsDown;
	}

	public void setThumbsDown(int aThumbsDown) {
		ivThumbsDown = aThumbsDown;
	}

	public List<String> getDifficulties() {
		return ivDifficulties;
	}

	public void setDifficulties(List<String> aDifficulties) {
		ivDifficulties = aDifficulties;
	}

	public String getSongId() {
		return ivSongId;
	}

	public void setSongId(String aSongId) {
		ivSongId = aSongId;
	}

	public String getTitle() {
		return ivTitle;
	}

	public void setTitle(String aTitle) {
		ivTitle = aTitle;
	}

	public String getDownloadUrl() {
		return ivDownloadUrl;
	}

	public void setDownloadUrl(String aDownloadUrl) {
		ivDownloadUrl = aDownloadUrl;
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

	public static Set<BSaberEntry> getNewSongs() {
		return cvNewSongs;
	}

	public static Set<BSaberEntry> getSongsWithErrors() {
		return cvSongsWithErrors;
	}

	public static void printNewSongs() {

		if (!getNewSongs().isEmpty()) {
			cvLogger.debug("_________");
			cvLogger.debug("NEW SONGS");
			for (BSaberEntry bSaberEntry : getNewSongs()) {
				cvLogger.debug(bSaberEntry.toString());
			}
		}
	}

	public static void printSongsWithErrors() {

		if (!getSongsWithErrors().isEmpty()) {
			cvLogger.debug("_________________");
			cvLogger.debug("SONGS WITH ERRORS");
			for (BSaberEntry bSaberEntry : getSongsWithErrors()) {
				cvLogger.debug(bSaberEntry.toString());
			}
		}
	}

	private String getDownloadName() {
		String name = getTitle() + " (" + getMapper() + " " + Tools.difficultiesToString(getDifficulties()) + ")";
		name = name.replaceAll(ILLEGAL_CHARACTERS, "_");

		return getSongId() + " - " + name + ".zip";
	}

	public boolean download(String aPath) {

		// Checks songidprefix of files in downloadpath if found one or more files skip.
		if (!Tools.checkSongIdAlreadyDownloaded(aPath, getSongId())) {
			File downloadFile = new File(aPath + getDownloadName());
			try {
				// Doublecheck if the new file exists
				if (!downloadFile.exists()) {
					if (!downloadFile.getParentFile().exists()) {
						downloadFile.getParentFile().mkdirs();
					}

					URL url = new URL(getDownloadUrl());
					Tools.saveUrl(downloadFile.toPath(), url, 30, 30);

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
				if (e instanceof FileNotFoundException) {
					cvLogger.error("File not found " + toString());
				}
				cvSongsWithErrors.add(this);
			}
		} else {
			cvAlreadyDownloads++;
			cvDownloadedTotal++;
			downloadFinish();
		}

		return isDownloaded();
	}

	@Override
	public String toString() {
		return getRawData().toString();
	}
}
