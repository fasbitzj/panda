package com.panda.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
@Controller
@RequestMapping(value = "/")
public class HelloController {

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public ModelAndView hello(String name) {
        ModelAndView mav = new ModelAndView("hello");
        mav.addObject("name", name);
        return mav;
    }
}
