package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BSaberEntry {
	private static final Logger cvLogger = LogManager.getLogger(BSaberEntry.class);

//	private static final String DOWNLOAD_PATH = "Z:\\BSaberSongs\\";
	private static final String ILLEGAL_CHARACTERS = "[^a-zA-Z0-9\\.\\-]";

	private static int cvNewDownloads = 0;
	private static int cvAlreadyDownloads = 0;
	private static int cvDownloadedTotal = 0;
	private static Set<BSaberEntry> cvNewSongs = new HashSet<>();
	private static Set<BSaberEntry> cvSongsWithErrors = new HashSet<>();

	private String ivSongID;
	private String ivName;
	private String ivDownloadUrl;
	private String ivPath;
	private boolean ivDownloaded = false;

	public BSaberEntry(String aSongID, String aName, String aDownloadUrl, String aPath) {
		ivSongID = aSongID;
		ivName = aName;
		ivDownloadUrl = aDownloadUrl;
		ivPath = aPath;
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

	public String getPath() {
		return ivPath;
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
		String name = getName().replaceAll(ILLEGAL_CHARACTERS, "_");
		return getSongID() + " (" + name + ").zip";
	}

	public boolean download() {
		File downloadFile = new File(getPath() + getDownloadName());
		try {
			if (!downloadFile.exists()) {
				if (!downloadFile.getParentFile().exists()) {
					downloadFile.getParentFile().mkdirs();
				}

				URL url = new URL(getDownloadUrl());
				saveUrl(downloadFile.toPath(), url, 30, 30);

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

	private static void saveUrl(final Path aFile, final URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout)
			throws MalformedURLException, IOException {
		try (InputStream in = streamFromUrl(aUrl, aSecsConnectTimeout, aSecsReadTimeout)) {
			Files.copy(in, aFile);
		}
	}

	private static InputStream streamFromUrl(URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout)
			throws IOException {
		URLConnection conn = aUrl.openConnection();
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		if (aSecsConnectTimeout > 0)
			conn.setConnectTimeout(aSecsConnectTimeout * 1000);
		if (aSecsReadTimeout > 0)
			conn.setReadTimeout(aSecsReadTimeout * 1000);
		return conn.getInputStream();
	}

	@Override
	public String toString() {
		return getSongID() + " --> " + getName() + " (" + getDownloadUrl() + ")";
	}
}
