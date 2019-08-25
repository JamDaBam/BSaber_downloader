package bsaber.tools.bsaber_scrapper;

public interface SongScrapper {
	public void downloadPages(String aPath, Float aRatio, int... aPageNumbers);

	public void downloadSongs(String aPath, Float aRatio, String... aSongKeys);
}
