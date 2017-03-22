package org.omnifaces.cdi.settings;

import java.net.URL;
import java.util.Map;

public interface PropertiesFileLoader {

	void load(URL url, Map<? super String, ? super String> settings);
}
