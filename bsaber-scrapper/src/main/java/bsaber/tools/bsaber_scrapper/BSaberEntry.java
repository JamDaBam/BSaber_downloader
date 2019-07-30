package bsaber.tools.bsaber_scrapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BSaberEntry {
	private static Logger cvLogger = LogManager.getLogger(BSaberEntry.class);

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

	private String getDownloadName() {
		String name = getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		return getSongID() + " (" + name + ").zip";
	}

	public void download() {
		File downloadFile = new File("Z:\\BSaberSongs\\" + getDownloadName());
		try {
			if (!downloadFile.exists()) {
				FileUtils.copyURLToFile(new URL(getDownloadUrl()), downloadFile);
				cvLogger.debug("DONE " + getDownloadName());
			} else {
				cvLogger.debug("SKIP " + downloadFile.getPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return getSongID() + " --> " + getName() + " (" + getDownloadUrl() + ")";
	}
}
