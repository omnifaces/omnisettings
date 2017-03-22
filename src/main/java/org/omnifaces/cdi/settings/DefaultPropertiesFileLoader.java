package org.omnifaces.cdi.settings;

import java.net.URL;
import java.util.Map;

import org.omnifaces.utils.properties.PropertiesUtils;

public class DefaultPropertiesFileLoader implements PropertiesFileLoader {

	@Override
	public void load(URL url, Map<? super String, ? super String> settings) {
		PropertiesUtils.loadXMLFromURL(url, settings);
	}
	
}
