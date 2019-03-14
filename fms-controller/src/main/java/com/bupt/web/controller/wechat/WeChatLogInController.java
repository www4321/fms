package com.bupt.web.controller.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/wechat")
public class WeChatLogInController  {
    private static Logger logger = LoggerFactory.getLogger(WeChatLogInController.class);
    @ResponseBody
    @RequestMapping("/login")
    public String LogIn(@RequestParam(value="code") String code){

        logger.info("wechat code:{}",code);

        return "www";
    }
}
