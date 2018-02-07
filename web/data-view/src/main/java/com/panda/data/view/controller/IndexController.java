package com.panda.data.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by jiang.zheng on 2018/2/7.
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/")
    public String index(){
        return "index";
    }
}
