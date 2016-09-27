package org.omnifaces.cdi.settings;


import static org.omnifaces.utils.properties.PropertiesUtils.getStage;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;
import static org.omnifaces.utils.properties.PropertiesUtils.loadXMLPropertiesStagedFromClassPath;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

@ApplicationScoped
public class ApplicationSettingsLoader {

	private Map<String, String> settings;
	
	@PostConstruct
	public void init() {

		// TODO: use service loader
		Map<String, String> internalSettings = loadPropertiesFromClasspath("META-INF/omni-settings");
		
		Map<String, String> mutableSettings = new HashMap<>();
		
		 String stageSystemPropertyName = internalSettings.getOrDefault("stageSystemPropertyName", "omni.stage");
		 String defaultStage = internalSettings.get("defaultStage");
		
		mutableSettings.putAll(loadXMLPropertiesStagedFromClassPath(
			internalSettings.getOrDefault("fileName", "application-settings.xml"),
			stageSystemPropertyName,
			defaultStage));
		
		// Non-overridable special setting
		mutableSettings.put("actualStageName", getStage(stageSystemPropertyName, defaultStage));
		
		settings = Collections.unmodifiableMap(mutableSettings);
	}

	@Produces
	@Named("applicationSettings")
	@ApplicationSettings
	public Map<String, String> getSettings() {
		return settings;
	}

	@Produces
	@ApplicationSetting
	public String getStringSetting(InjectionPoint injectionPoint) {
		String value = settings.get(injectionPoint.getMember().getName());
		if (value == null) {
			for (Annotation annotation : injectionPoint.getQualifiers()) {
				if (annotation instanceof ApplicationSetting) {
					value = ((ApplicationSetting) annotation).defaultValue();
					break;
				}
			}
		}

		return value;
	}

	@Produces
	@ApplicationSetting
	public Long getLongSetting(InjectionPoint injectionPoint) {
		return Long.valueOf(getStringSetting(injectionPoint));
	}

	@Produces
	@ApplicationSetting
	public Integer getIntegerSetting(InjectionPoint injectionPoint) {
		return Integer.valueOf(getStringSetting(injectionPoint));
	}

	@Produces
	@ApplicationSetting
	public Boolean getBooleanSetting(InjectionPoint injectionPoint) {
		return Boolean.valueOf(getStringSetting(injectionPoint));
	}


}
