package bsaber.tools.bsaber_scrapper;

import java.util.Collection;

public class Tools {
	private Tools() {
	}

	
	public static <T> boolean isNullOrEmpty(Collection<T> aCollection) {
		return aCollection == null || aCollection.isEmpty();
	}
}
