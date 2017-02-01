package org.omnifaces.cdi.settings;


import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.omnifaces.utils.Lang.isEmpty;
import static org.omnifaces.utils.properties.PropertiesUtils.getStage;
import static org.omnifaces.utils.properties.PropertiesUtils.loadPropertiesFromClasspath;
import static org.omnifaces.utils.properties.PropertiesUtils.loadXMLPropertiesStagedFromClassPath;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
	public Map<String, String> getSettings(InjectionPoint injectionPoint) {
		if (injectionPoint == null) {
			return settings;
		}

		String prefix = getApplicationSettings(injectionPoint).prefixedBy();

		if (prefix.isEmpty()) {
			return settings;
		}

		return settings.entrySet().stream().filter(e -> e.getKey().startsWith(prefix)).collect(toMap(e -> e.getKey(), e -> e.getValue()));
	}

	@Produces
	@ApplicationSetting
	public String getStringSetting(InjectionPoint injectionPoint) {
		String value = settings.get(injectionPoint.getMember().getName());

		if (value == null) {
			value = getApplicationSetting(injectionPoint).defaultValue();
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
	public List<String> getSeparatedStringSetting(InjectionPoint injectionPoint) {
		String setting = getStringSetting(injectionPoint);

		if (isEmpty(setting)) {
			return emptyList();
		}

		String separator = getApplicationSetting(injectionPoint).separatedBy();
		return unmodifiableList(asList(setting.split("\\s*" + quote(separator) + "\\s*")));
	}

	@Produces
	@ApplicationSetting
	public List<Long> getSeparatedLongSetting(InjectionPoint injectionPoint) {
		return unmodifiableList(getSeparatedStringSetting(injectionPoint).stream().map(Long::valueOf).collect(toList()));
	}

	private static ApplicationSetting getApplicationSetting(InjectionPoint injectionPoint) {
		return getQualifier(injectionPoint, ApplicationSetting.class);
	}

	private static ApplicationSettings getApplicationSettings(InjectionPoint injectionPoint) {
		return getQualifier(injectionPoint, ApplicationSettings.class);
	}

	@SuppressWarnings("unchecked")
	private static <A extends Annotation> A getQualifier(InjectionPoint injectionPoint, Class<A> qualifier) {
		for (Annotation annotation : injectionPoint.getQualifiers()) {
			if (qualifier.isInstance(annotation)) {
				return (A) annotation;
			}
		}

		return null;
	}

}