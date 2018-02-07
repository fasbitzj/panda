package com.panda.data.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
@Controller
@RequestMapping(value = "/")
public class HelloController {

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public ModelAndView hello(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("hello");
        mav.addObject("name", request.getRemoteUser());
        return mav;
    }
}
