package org.omnifaces.settings;

import java.util.Map;

/**
 * Convenience helper class that provides the correct staging name for the load properties methods.
 * TODO: use service loader
 * 
 * @author arjan
 *
 */
public final class PropertiesUtils {

	private PropertiesUtils() {
	}

	public static Map<String, String> loadPropertiesList(String fileName) {
		return org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesListStagedFromEar(fileName, "omni.staging");
	}

	public static Map<String, String> loadXMLProperties(String fileName) {
		return org.omnifaces.utils.properties.PropertiesUtils.loadXMLPropertiesStagedFromEar(fileName, "omni.staging");
	}

}
