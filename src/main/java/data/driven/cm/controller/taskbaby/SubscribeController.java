package data.driven.cm.controller.taskbaby;

import data.driven.cm.business.taskbaby.*;
import data.driven.cm.entity.taskbaby.MatActivityEntity;
import data.driven.cm.entity.taskbaby.WechatUserInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @Author: lxl
 * @describe 订阅号Controller
 * @Date: 2018/12/19 14:20
 * @Version 1.0
 */
@Controller
@RequestMapping(value = "/subscribe")
public class SubscribeController {
    Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    // 设定输出的类型
    private static final String JPG = "image/jpeg;charset=GB2312";

    @Autowired
    private SubscribeWeChatResponseService subscribeWeChatResponseService;

    @Autowired
    private UnionidUserMappingService unionidUserMappingService;

    @Autowired
    private SubscribeServiceMappingService subscribeServiceMappingService;

    @Autowired
    private WechatUserInfoService wechatUserInfoService;

    @Autowired
    private ActivityService activityService;

    /**
     * 图片信息Service
     */
    @Autowired
    private SysPictureService sysPictureService;


    /**
     * @param code  code作为换取access_token的票据
     * @param state 参数,appid&&act_id&&unionid&&订阅号的原始id 说明：服务号appid、活动id、被助力者的unionid、
     * @return
     * @description 微信用户给服务号授权后的回调URL
     * @author lxl
     * @date 2018-12-19 14:22
     */
    @RequestMapping(value = "/authcallback", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public ModelAndView authorizeCallback(@RequestParam(value = "code") String code,
                                    @RequestParam(value = "state") String state) {
        logger.info("进入微信用户回调URL");
        String[] strs = state.split("@@");
        String serviceAppId = strs[0].toString();
        logger.info("服务号appid " + serviceAppId);
        String actId = strs[1].toString();
        logger.info("活动id " + actId);
        String fromUnionid = strs[2].toString();
        logger.info("被助力者unionid " + fromUnionid);
        String subscribeWechatAccount = strs[3].toString();
        logger.info("订阅号的原始id " + subscribeWechatAccount);

        String toUnionid = subscribeWeChatResponseService.getCodeByUnionid(code, serviceAppId);
        unionidUserMappingService.insertUnionidUserMappingEntity(actId, fromUnionid, toUnionid,subscribeWechatAccount);
//        String toUnionid = "oC3bV00BmWXi6LoZcAdKL1ToNO60";
        logger.info("toUnionid " + toUnionid);
        //如果用户扫自己的二维码就需要给用户发送统计的信息 start
        if (fromUnionid.equals(toUnionid)) {
            //取微信用户信息，通过任务id和用户unionid
            WechatUserInfoEntity wechatuserInfoEntity = wechatUserInfoService.getWechatUserInfoEntityByActIdAndUnionid(actId, toUnionid);
            //得到订阅号的appid
            String subscribeAppId = subscribeServiceMappingService.getAuthorizationAppidBySubscribeWechatAccount(subscribeWechatAccount);
            subscribeWeChatResponseService.subscribeSendMyActivityStatus(subscribeAppId, actId, wechatuserInfoEntity.getOpenid());
            //如果用户扫自己的二维码就需要给用户发送统计的信息 end
        }
        ModelAndView modelAndView = new ModelAndView("/taskbaby/subscribeIndex");
        String qrPicId = subscribeServiceMappingService.getQrPicIdBySubscribeWechatAccount(subscribeWechatAccount);
        MatActivityEntity matActivityEntity = activityService.getMatActivityEntityByActId(actId);
//        String qrPicId = subscribeServiceMappingService.getQrPicIdBySubscribeWechatAccount("gh_1b995980b921");
        modelAndView.addObject("actKeyWord",matActivityEntity.getActKeyWord());
        modelAndView.addObject("qrPicId", qrPicId);
        return modelAndView;
    }

    /**
     * @param qrPicId 图片id
     * @return
     * @description
     * @author lxl
     * @date 2018-12-19 18:18
     */
    @RequestMapping(value = "/ShowPic")
    @ResponseBody
    public void ShowPic(HttpServletRequest request,
                        HttpServletResponse response, @RequestParam(value = "qrPicId") String qrPicId) throws ServletException, IOException {
        logger.info("进入图片显示");
        FileInputStream fis = null;
        response.setContentType("image/gif");
        String imagePath = sysPictureService.getPictureURL(qrPicId);
        logger.info("图片地址" + imagePath);
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(imagePath);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}






















