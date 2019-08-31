package bsaber.tools.bsaber_scrapper;

import java.io.IOException;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BSaberSongScrapper extends BaseSongScrapper {
	private static final Logger cvLogger = LogManager.getLogger(BSaberSongScrapper.class);

	// Html tags
	private static final String QUERY_SONG_ENTRY = "div.post-header";
	private static final String QUERY_SONG_ENTRIES = "article";

	@Override
	public void downloadPages(String aPath, Float aRatio, int... aPageNumbers) {
		for (int pageNumber : aPageNumbers) {
			downloadPage(aPath, aRatio, pageNumber);
		}
	}

	private void downloadPage(String aPath, Float aRatio, int aPageNumber) {
		Constants.EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			try {
				String urlString = aPageNumber == 1 ? Constants.BSABER_BASE_SONGS_URL
						: Constants.BSABER_SONGS_PAGE_URL + aPageNumber;

				URL url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntryElements = doc.select(QUERY_SONG_ENTRIES);

				long end = System.currentTimeMillis();
				cvLogger.debug("DONE " + urlString + " (" + (end - start) + ")");

				for (SongEntry songEntry : BSaberParser.parse(songEntryElements)) {
					if (songEntry.checkUpVoteRatio(aRatio)) {
						songEntry.download(aPath);
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + songEntry.getRatio() + ": "
								+ songEntry.toString());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void downloadSongs(String aPath, Float aRatio, String... aSongKeys) {
		if (aSongKeys != null) {
			for (String aSongId : aSongKeys) {
				downloadSong(aPath, aRatio, aSongId);
			}
		}
	}

	private void downloadSong(String aPath, Float aRatio, String aSongKey) {
		Constants.EXECUTOR.submit(() -> {
			long start = System.currentTimeMillis();

			try {
				String urlString = Constants.BSABER_BASE_SONGS_URL + aSongKey;

				URL url = new URL(urlString);

				Document doc = Jsoup.parse(url, 30000);
				Elements songEntryElements = doc.select(QUERY_SONG_ENTRY);

				for (SongEntry songEntry : BSaberParser.parse(songEntryElements)) {
					if (songEntry.checkUpVoteRatio(aRatio)) {
						songEntry.download(aPath);
					} else {
						cvLogger.debug("Skip by ratio: " + aRatio + " > " + songEntry.getRatio() + ": "
								+ songEntry.toString());
					}
				}

				long end = System.currentTimeMillis();
				cvLogger.debug("DONE " + urlString + " --> (" + (end - start) + ")");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
