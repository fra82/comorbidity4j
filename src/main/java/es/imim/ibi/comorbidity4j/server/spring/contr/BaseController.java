package es.imim.ibi.comorbidity4j.server.spring.contr;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.ui.ModelMap;

import com.google.common.base.Strings;

import es.imim.ibi.comorbidity4j.server.spring.ControllerUtil;
import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;

public class BaseController {
	
	private static Random rnd = new Random();
	
	public void setMenu(ModelMap model, int step, String mStr, UserInputContainer so) {
		// Set side menu
		model.addAttribute("menuItemsList", ControllerUtil.getMenuMap(step, mStr, so));

		// Set base path and CSSJScounter
		String baseCSS_JSpath = "";
		model.put("baseCSS_JSpath", baseCSS_JSpath);
		model.put("CSSJScount", "N" + rnd.nextInt(100000));
	}
	
	public UserInputContainer getSessionObj(HttpServletRequest request) {
		System.gc();
		
		if(request.getSession().getAttribute("so") == null) {
			UserInputContainer so = new UserInputContainer();
			request.getSession().setAttribute("so", so);
		}
		
		return (UserInputContainer) request.getSession().getAttribute("so");
	}
	
	public ImmutablePair<String, String> checkParameterString(String paramString, String paramName) {
		String causeLeft = "";
		String valueRight = "";

		if(Strings.isNullOrEmpty(paramString)) {
			causeLeft = "Null or empty String parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramString;
		}

		return new ImmutablePair<String, String>(causeLeft, valueRight);
	}

	public ImmutablePair<String, Integer> checkParameterInteger(String paramString, String paramName) {
		String causeLeft = "";
		Integer valueRight = null;

		Integer paramInt = null;
		try {
			paramInt = Integer.valueOf(paramString);
		}
		catch(Exception e) {
			/* Do nothing */
		}

		if(Strings.isNullOrEmpty(paramString) || paramInt == null) {
			causeLeft = "Null, empty or not Integer parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramInt;
		}

		return new ImmutablePair<String, Integer>(causeLeft, valueRight);
	}

	public ImmutablePair<String, Double> checkParameterDouble(String paramString, String paramName) {
		String causeLeft = "";
		Double valueRight = null;

		Double paramDouble = null;
		try {
			paramDouble = Double.valueOf(paramString);
		}
		catch(Exception e) {
			/* Do nothing */
		}

		if(Strings.isNullOrEmpty(paramString) || paramDouble == null) {
			causeLeft = "Null, empty or not Double parameter: " + ((paramName != null) ? paramName : "-");
		}
		else {
			causeLeft = null;
			valueRight = paramDouble;
		}

		return new ImmutablePair<String, Double>(causeLeft, valueRight);
	}

}
