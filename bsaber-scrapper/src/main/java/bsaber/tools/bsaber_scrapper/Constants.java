package bsaber.tools.bsaber_scrapper;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Constants {
	// Urls
	public static final String BSABER_BASE_SONGS_URL = "https://bsaber.com/songs/";
	public static final String BSABER_BASE_DOWNLOAD_URL = "https://beatsaver.com/api/download/key/";
	public static final String BSABER_SONGS_PAGE_URL = "https://bsaber.com/songs/page/";

	// Beatsaver
	public static enum SearchTypes {
		LATEST("/latest/"), HOT("/hot/"), DOWNLOADS("/downloads/"), PLAYS("/plays/"), RATING("/rating/");

		private String ivSearchType;

		private SearchTypes(String aSearchType) {
			ivSearchType = aSearchType;
		}

		public String getCallUrl(int aPageNumber) {
			return BEATSAVER_API_BASE_URL + ivSearchType + aPageNumber;
		}
	}

	public static final String BEATSAVER_BASE_URL = "https://beatsaver.com";
	public static final String BEATSAVER_API_BASE_URL = BEATSAVER_BASE_URL + "/api";
	public static final String BEATSAVER_DOWNLOAD_BASE_URL = BEATSAVER_API_BASE_URL + "/download/";

	// Threads
	public static final int CORE_SIZE = 3;
	public static final ThreadPoolExecutor EXECUTOR = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(Constants.CORE_SIZE);

}
