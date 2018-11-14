package data.driven.cm.controller.TaskBaby;

import data.driven.cm.business.taskBaby.WeChatService;
import data.driven.cm.util.WeChatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: lxl
 * @describe 任务宝 Controller
 * @Date: 2018/11/12 16:51
 * @Version 1.0
 */
@Controller
@RequestMapping(path = "/wechatapi")
public class Task_BabyController {

    @Autowired
    private WeChatService weChatService;
    /**
     * 处理微信服务器发来的get请求，进行签名的验证
     *
     * signature 微信端发来的签名
     * timestamp 微信端发来的时间戳
     * nonce     微信端发来的随机字符串
     * echostr   微信端发来的验证字符串
     */
    @ResponseBody
    @GetMapping(value = "/wechat")
    public String validate(@RequestParam(value = "signature") String signature,
                           @RequestParam(value = "timestamp") String timestamp,
                           @RequestParam(value = "nonce") String nonce,
                           @RequestParam(value = "echostr") String echostr) {
        System.out.println("dd");
        return WeChatUtil.checkSignature(signature, timestamp, nonce) ? echostr : null;

    }

    /**
     * 此处是处理微信服务器的消息转发的
     */
    @ResponseBody
    @PostMapping(value = "/wechat")
    public String processMsg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 调用核心服务类接收处理请求
        return weChatService.processRequest(request,response);
    }

    /**
     * 微信公众号 第三方认证WEB
     * @return
     */
    @GetMapping(value = "/taskBaby/login")
    public ModelAndView publicLogin(){
        ModelAndView modelAndView = new ModelAndView("/taskBaby/login");
        return modelAndView;
    }

}
































