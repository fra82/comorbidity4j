package es.imim.ibi.comorbidity4j.server.spring.contr;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.imim.ibi.comorbidity4j.server.spring.UserInputContainer;

@Controller
public class Home extends BaseController {

	@RequestMapping(value={"", "/", "home", "index"})
	public String index(@RequestParam(value = "res", required = false) String resetSession,
			@ModelAttribute("md") ModelMap model, HttpServletRequest request) {
		
		// if(!Strings.isEmpty(resetSession)) {
			request.getSession().invalidate();
			System.gc();
		// }
		
		UserInputContainer so = getSessionObj(request);
		setMenu(model, 0, "", so);
		
		model.put("sessionName", getSessionObj(request).getSessionObj() + "");
		
		return "home";
	}

}