package org.omnifaces.cdi.settings;


import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;
import static org.omnifaces.utils.properties.PropertiesUtils.loadXMLPropertiesStagedFromClassPath;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.servlet.ServletContext;

@ApplicationScoped
public class ApplicationSettingsLoader {

	private Map<String, String> settings;
	
	public void eager(@Observes @Initialized(ApplicationScoped.class) ServletContext init) {
		// NOOP
	}
	
	@PostConstruct
	public void init() {
		
		Map<String, String> internalSettings = loadPropertiesFromClasspath("META-INF/omni-settings");
		
		// TODO: use service loader
		settings = loadXMLPropertiesStagedFromClassPath(
					internalSettings.getOrDefault("fileName", "application-settings.xml"),
					internalSettings.getOrDefault("stageSystemPropertyName", "omni.stage"),
					internalSettings.get("defaultStage"));
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
