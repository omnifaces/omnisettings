package org.omnifaces.cdi.settings;


import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.omnifaces.utils.Lang.isEmpty;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;
import static org.omnifaces.utils.properties.PropertiesUtils.loadXMLPropertiesStagedFromClassPath;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

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

	public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext init) {

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

	@Produces
	@ApplicationSetting
	public List<String> getCommaSeparatedStringSetting(InjectionPoint injectionPoint) {
		String setting = getStringSetting(injectionPoint);
		return isEmpty(setting) ? emptyList() : unmodifiableList(asList(setting.split("\\s*,\\s*")));
	}

	@Produces
	@ApplicationSetting
	public List<Long> getCommaSeparatedLongSetting(InjectionPoint injectionPoint) {
		return unmodifiableList(getCommaSeparatedStringSetting(injectionPoint).stream().map(Long::valueOf).collect(toList()));
	}

}
