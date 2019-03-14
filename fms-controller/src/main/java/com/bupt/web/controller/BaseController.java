package com.bupt.web.controller;

import com.bupt.common.bean.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);
    public Result success (Object data) {
        Result result = new Result();
        result.setErrMsg("success");
        if (data == null){
            result.setData("");
        } else {
            result.setData(data);
        }
        logger.info("success response:{}",result.toString());
        return result;
    }

    public Result failed(String errMsg) {
        Result result = new Result();
        result.setErrCode(1);
        result.setErrMsg(errMsg);
        result.setData("");
        logger.info("fail response:{}",result.toString());
        return result;
    }
}
