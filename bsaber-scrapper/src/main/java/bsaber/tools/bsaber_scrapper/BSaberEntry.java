package bsaber.tools.bsaber_scrapper;

public class BSaberEntry {
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

	public String getFolderName() {
		// TODO
		return "";
	}

	@Override
	public String toString() {
		return ivSongID + " --> " + ivName + " (" + ivDownloadUrl + ")";
	}
}
