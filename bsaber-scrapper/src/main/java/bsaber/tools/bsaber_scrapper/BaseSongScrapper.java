package bsaber.tools.bsaber_scrapper;

public abstract class BaseSongScrapper implements SongScrapper {
	@Override
	public void processCommandLine(CommandLineWrapper aCommandLineWrapper) {
		if (aCommandLineWrapper.getPages() != null) {
			downloadPages(aCommandLineWrapper.getPath(), aCommandLineWrapper.getRatio(),
					aCommandLineWrapper.getPages());
		}
		
		if(aCommandLineWrapper.getSongKeys() != null) {
			downloadSongs(aCommandLineWrapper.getPath(), aCommandLineWrapper.getRatio(), aCommandLineWrapper.getSongKeys());
		}
	}
}
