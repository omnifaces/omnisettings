package org.omnifaces.settings;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.servlet.ServletContext;

@ApplicationScoped
public class ApplicationSettingsLoaderX {
	
	public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext init) {
		int a;
		a = 4;
	}
}