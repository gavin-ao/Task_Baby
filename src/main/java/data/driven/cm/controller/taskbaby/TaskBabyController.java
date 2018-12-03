package data.driven.cm.controller.taskbaby;

import data.driven.cm.business.taskbaby.AreaService;
import data.driven.cm.business.taskbaby.ReceivingAddressService;
import data.driven.cm.business.taskbaby.WeChatService;
import data.driven.cm.entity.taskbaby.AreaEntity;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: lxl
 * @describe 任务宝 Controller
 * @Date: 2018/11/12 16:51
 * @Version 1.0
 */
@Controller
@RequestMapping(path = "/wechatapi")
public class TaskBabyController {
    Logger logger = LoggerFactory.getLogger(TaskBabyController.class);
    @Autowired
    private WeChatService weChatService;
    /**
     * 行政区划Service
     */
    @Autowired
    private AreaService areaService;

    /**
     * 收货地址
     */
    @Autowired
    private ReceivingAddressService receivingAddressService;

    /**
     * 处理微信服务器发来的get请求，进行签名的验证
     * <p>
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
        System.out.println(WeChatUtil.checkSignature(signature, timestamp, nonce) ? echostr : null);
        return WeChatUtil.checkSignature(signature, timestamp, nonce) ? echostr : null;

    }

    /**
     * 此处是处理微信服务器的消息转发的
     * 处理微信服务器发来的post请求，进行签名的验证
     */
    @ResponseBody
    @RequestMapping(value = "/wechat/{appid}/callback", method = {RequestMethod.GET, RequestMethod.POST})
    public String processMsg(@PathVariable String appid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("appid: " + appid);
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        logger.info("nonce " + nonce);
        logger.info("timestamp " + timestamp);
        logger.info("signature " + signature);
//        if (WeChatUtil.checkSignature(signature, timestamp, nonce)){
        // 调用核心服务类接收处理请求
        return weChatService.processRequest(request, response, appid);
//        }
//        return "";
    }

    /**
     * 收货地址跳转页面
     *
     * @return
     */
    @GetMapping(value = "/taskbaby/{openId}/{actId}/address")
    public ModelAndView address(@PathVariable String openId, @PathVariable String actId) {

        ModelAndView modelAndView = new ModelAndView("/taskbaby/receiving_address");
        List<AreaEntity> areaEntityList = areaService.getProvinces();
        modelAndView.addObject("provinces", areaEntityList);
        return modelAndView;
    }

    /**
     * @param areaCode 区划码
     * @param type     判断是市还是区 1代表市 2 代表区
     * @return java.util.List<data.driven.cm.entity.taskbaby.AreaEntity>
     * @description 获取市、区 行政区划信息
     * @author lxl
     * @date 2018-11-30 10:06
     */
    @ResponseBody
    @GetMapping(value = "/taskbaby/{openId}/{actId}/areaInfo")
    public List<AreaEntity> getCityDistrict(@RequestParam(value = "areaCode") String areaCode, @RequestParam(value = "type") String type) {
        List<AreaEntity> areaEntityList = new ArrayList<>();
        switch (type) {
            case "1":
                areaEntityList = areaService.getCities(areaCode);
                break;
            case "2":
                areaEntityList = areaService.getDistricts(areaCode);
                break;
            default:
                break;
        }
        return areaEntityList;
    }

    /**
     * @description 收货地址保存，成功后跳转成功页面
     * @author lxl
     * @date 2018-12-03 17:43
     * @return
     */
    @PostMapping(value = "/taskbaby/{openId}/{actId}/saveAddress")
    public ModelAndView saveAddress(@PathVariable String openId, @PathVariable String actId,
                                    @RequestParam(value = "consignee") String consignee,
                                    @RequestParam(value = "phone") String phone,
                                    @RequestParam(value = "province") String province,
                                    @RequestParam(value = "city") String city,
                                    @RequestParam(value = "district") String district,
                                    @RequestParam(value = "detailedAddress") String detailedAddress) {
        receivingAddressService.insertReceivingAddressEntity(openId,actId,consignee,phone,province,city,district,detailedAddress);
        ModelAndView modelAndView = new ModelAndView("/taskbaby/receiving_address_success");
        return modelAndView;

    }

}
































