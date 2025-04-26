package com.record.collection.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.record.collection.beans.Users;
import com.record.collection.controllers.base.RequestControllerBase;
import com.record.collection.reload.ApplicationProperties;
import com.record.collection.service.imp.CollectionRecordService.QueryParam;
import com.record.collection.service.imp.CollectionRecordService.QueryRestriction;
import com.record.collection.utils.Key;

@Controller
public class AuthenticationController extends RequestControllerBase 
{
	private static final Logger _logger = Logger.getLogger(AuthenticationController.class.getName());

	@Autowired
	private ApplicationProperties appProperties;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public Object start(ModelMap model) {
		String successUrl = _config.getRequiredProperty("login.successurl");
		String username = getAuthenticatedUser();
		_logger.log(Level.FINEST, "User [" + username + "] has successfully logged in.");
		String params = "?loggedInUser" + "=" + username;
		
		String contributors = getContributorsAsUrlParam();
		if(contributors.length() > 0) {
			params += "&" + contributors;
		}
		
		params += "&accessLevel=" + getUserAcess(username);
		return (successUrl == null) ? new ModelAndView("index", model) : new RedirectView(successUrl + params, false);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("errorMsg", "<br>Your username and password are invalid.");

		if (logout != null)
			model.addAttribute("msg", "You have been logged out successfully. <br>");

		model.addAttribute("logo", appProperties.getRequiredProperty("login.page.logoPath"));
		return "login";
	}
	
	@RequestMapping(value="/logout", method = RequestMethod.GET)
	public String logoutPage (Model model, HttpServletRequest request, HttpServletResponse response) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    
	    for (Cookie cookie : request.getCookies()) {
            String cookieName = cookie.getName();
            Cookie cookieToDelete = new Cookie(cookieName, null);
            cookieToDelete.setMaxAge(0);
            response.addCookie(cookieToDelete);
        }
	    
	    model.addAttribute("logo", appProperties.getRequiredProperty("login.page.logoPath"));
	    return "redirect:/login?logout"; 
	}


	protected String getUserAcess(String name) {
		ArrayList<QueryParam> queryParms = new ArrayList<QueryParam>();
		queryParms.add(new QueryParam(Key.USERNAME, name, QueryRestriction.EQ));
		
		ArrayList<?> users = _collectionRecordService.getCollectionData(Users.class, queryParms, true);
		if(users != null && users.size() > 0) {
			return ((Users) users.get(0)).getAccessLevel();
		}
		
		return null;
	}

}
