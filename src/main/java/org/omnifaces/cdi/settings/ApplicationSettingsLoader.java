package org.omnifaces.cdi.settings;

import static org.omnifaces.cdi.settings.PropertiesUtils.loadXMLProperties;

import java.lang.annotation.Annotation;
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
		settings = loadXMLProperties("application-settings.xml");
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
