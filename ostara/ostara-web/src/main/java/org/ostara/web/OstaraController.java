package org.ostara.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OstaraController {
	  private static Logger logger = LoggerFactory.getLogger(OstaraController.class);
	  
	  private static final String OSTARA_SERVICE_URL;
	  
	  static {
		  String sysProp = System.getProperty("ostaraServiceURL");
		  if(sysProp == null) {
			  OSTARA_SERVICE_URL = ""; // Local
		  } else {
			  OSTARA_SERVICE_URL = sysProp;
		  }
	  }

	  @RequestMapping(value="login", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	  @ResponseBody
	  public ModelAndView login() {
	    return new OstaraModelAndView("login", OSTARA_SERVICE_URL);
	  }

	  @RequestMapping(value="index", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	  @ResponseBody
	  public ModelAndView index() {
		return new OstaraModelAndView("login", OSTARA_SERVICE_URL);
	  }
}
