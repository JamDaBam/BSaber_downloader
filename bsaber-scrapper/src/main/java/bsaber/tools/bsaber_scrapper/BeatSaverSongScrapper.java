package bsaber.tools.bsaber_scrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class BeatSaverSongScrapper implements SongScrapper {
	private static final Logger cvLogger = LogManager.getLogger(BeatSaverSongScrapper.class);

	@Override
	public void downloadPages(String aPath, Float aRatio, int... aPageNumbers) {
		for (int pageNumber : aPageNumbers) {
			downloadPage(aPath, aRatio, pageNumber);
		}
	}

	@Override
	public void downloadSongs(String aPath, Float aRatio, String... aSongKeys) {
		for (String songKey : aSongKeys) {
			downloadSong(aPath, aRatio, songKey);
		}
	}

	private void downloadSong(String aPath, Float aRatio, String aSongKey) {
		Constants.EXECUTOR.submit(() -> {
			try {
				for (SongEntry songEntry : BeatSaverParser
						.parse(readUrl(Constants.SearchTypes.SINGLE.getCallUrl() + aSongKey))) {
					if (songEntry.checkUpVoteRatio(aRatio)) {
						songEntry.download(aPath);
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + songEntry.getRatio() + ": "
								+ songEntry.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void downloadPage(String aPath, Float aRatio, int aPageNumber) {
		Constants.EXECUTOR.submit(() -> {
			try {
				for (SongEntry songEntry : BeatSaverParser
						.parse(readUrl(Constants.SearchTypes.LATEST.getCallUrl() + aPageNumber))) {
					if (songEntry.checkUpVoteRatio(aRatio)) {
						songEntry.download(aPath);
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + songEntry.getRatio() + ": "
								+ songEntry.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}
}
