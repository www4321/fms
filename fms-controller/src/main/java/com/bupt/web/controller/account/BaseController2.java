package com.bupt.web.controller.account;

import com.bupt.common.bean.Result;
import com.bupt.service.bean.User;
import com.bupt.service.dao.UserMapper;
import com.bupt.web.controller.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/")
public class BaseController2 extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseController2.class);
    @Autowired
    UserMapper userMapper;
    @ResponseBody
    @RequestMapping("/login")
    public Result login(@RequestParam(value="username") String userName,@RequestParam(value="password") String password){
        logger.info("login begin.RequestParam{userName:{},password:{}}",userName,password);
        List<User> users = userMapper.getAll();
        for(User user:users){
            logger.info(user.getUserName()+" "+user.getPassWord());
        }
        return success(null);
    }
}
