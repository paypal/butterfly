package org.ostara.web;

import org.springframework.web.servlet.ModelAndView;

public class OstaraModelAndView extends ModelAndView {

	public OstaraModelAndView(String viewName, String ostaraServiceUrl) {
		super(viewName);
		addObject("OSTARA_SERVICE_URL", ostaraServiceUrl);
	}
	
}
