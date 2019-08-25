package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class SongEntry {
	private static final Logger cvLogger = LogManager.getLogger(SongEntry.class);

	private static final String ILLEGAL_CHARACTERS = "[<>/*\"\\:?|]";

	private SongMetaData ivMetaData;

	public SongEntry(SongMetaData aMetaData) {
		ivMetaData = aMetaData;
	}

	private String getDownloadName() {
		String name = ivMetaData.getSongAuthorName() == null ? ""
				: (ivMetaData.getSongAuthorName() + " - ") + ivMetaData.getSongName() + " ("
						+ ivMetaData.getLevelAuthorName() + " " + " (" + ivMetaData.getDifficultiesAsString() + "))";
		name = name.replaceAll(ILLEGAL_CHARACTERS, "_");

		return ivMetaData.getKey() + " - " + name + ".zip";
	}

	public boolean download(String aPath) {
		boolean isDownloaded = false;

		String downloadName = getDownloadName();

		// Checks songidprefix of files in downloadpath if found one or more files skip.
		if (!checkSongIdAlreadyDownloaded(aPath, ivMetaData.getKey())) {
			File downloadFile = new File(aPath + downloadName);
			try {
				// Doublecheck if the new file exists
				if (!downloadFile.exists()) {
					if (!downloadFile.getParentFile().exists()) {
						downloadFile.getParentFile().mkdirs();
					}

					URL url = new URL(ivMetaData.getDownloadURL());
					saveUrl(downloadFile.toPath(), url, 30, 30);
					cvLogger.info("Downloaded: " + downloadName);
				} else {
					cvLogger.info("File exists: " + downloadName);
				}

				isDownloaded = true;
			} catch (IOException e) {
				if (e instanceof FileNotFoundException) {
					cvLogger.error("File not found " + downloadName);
				}
			}
		} else {
			isDownloaded = true;
			cvLogger.info("Key exists: " + downloadName);
		}

		return isDownloaded;
	}

	private boolean checkSongIdAlreadyDownloaded(String aPath, String aSongId) {
		File dir = new File(aPath);

		// list the files using a anonymous FileFilter
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.getName().startsWith(aSongId + " ");
			}
		});

		if (files != null && files.length > 0) {
			return true;
		} else {
			return false;
		}
	}

	private void saveUrl(final Path aFile, final URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout)
			throws MalformedURLException, IOException {
		try (InputStream in = streamFromUrl(aUrl, aSecsConnectTimeout, aSecsReadTimeout)) {
			Files.copy(in, aFile);
		}
	}

	private InputStream streamFromUrl(URL aUrl, int aSecsConnectTimeout, int aSecsReadTimeout) throws IOException {
		URLConnection conn = aUrl.openConnection();
		conn.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

		if (aSecsConnectTimeout > 0)
			conn.setConnectTimeout(aSecsConnectTimeout * 1000);
		if (aSecsReadTimeout > 0)
			conn.setReadTimeout(aSecsReadTimeout * 1000);
		return conn.getInputStream();
	}

	public Float getRatio() {
		float ratio = 0f;

		int downVotes = ivMetaData.getDownVotes() == null ? 0 : ivMetaData.getDownVotes();
		int updVotes = ivMetaData.getUpVotes() == null ? 0 : ivMetaData.getUpVotes();
		int votes = downVotes + updVotes;

		if (votes > 0) {
			ratio = (float) updVotes / (float) votes;
		}

		return ratio;
	}

	public boolean checkUpVoteRatio(Float aRatio) {
		return aRatio == null || aRatio <= getRatio();
	}

	public SongMetaData getMetaData() {
		return ivMetaData;
	}

	@Override
	public String toString() {
		return ivMetaData.toString();
	}
}
