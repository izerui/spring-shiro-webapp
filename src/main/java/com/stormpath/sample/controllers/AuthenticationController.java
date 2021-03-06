package com.stormpath.sample.controllers;

import com.stormpath.sample.service.LoginService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that supports the authentication URLs for the application.
 *
 * @author josebarrueta
 *
 */
@Controller
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView showLogin(){
        Subject currentSubject = SecurityUtils.getSubject();
        if(currentSubject.isAuthenticated() || currentSubject.isRemembered()){
            currentSubject.logout();
        }
        //TODO: Read error codes in case of authentication failure.
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam(value = "rememberMe", required = false, defaultValue = "false") String rememberMe){
        try {
            loginService.doLogin(username,password,Boolean.valueOf(rememberMe));
        } catch (Exception e) {
            logger.error(String.format("Error occurred while authenticating user. Description [%s].", e.getCause().getMessage()));
            //TODO: Set error codes.
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(){
        Subject currentSubject = SecurityUtils.getSubject();

        if(currentSubject.isAuthenticated() || currentSubject.isRemembered()){
            logger.info(String.format("User [%s] is logging out from the app.", currentSubject.getPrincipal()));
            currentSubject.logout();
        }
        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public ModelAndView unauthorized(){
        return new ModelAndView("error403");
    }

}
