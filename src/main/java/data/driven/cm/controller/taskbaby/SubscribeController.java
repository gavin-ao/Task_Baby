package data.driven.cm.controller.taskbaby;

import com.sun.image.codec.jpeg.JPEGCodec;
import data.driven.cm.business.taskbaby.SubscribeServiceMappingService;
import data.driven.cm.business.taskbaby.SubscribeWeChatResponseService;
import data.driven.cm.business.taskbaby.SysPictureService;
import data.driven.cm.business.taskbaby.UnionidUserMappingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.sun.image.codec.jpeg.*;//sun公司仅提供了jpg图片文件的编码api
import sun.awt.image.codec.JPEGImageEncoderImpl;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
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
        //服务号appid
        String serviceAppId = strs[0].toString();
        logger.info("服务号appid "+serviceAppId);
        //活动id
        String actId = strs[1].toString();
        logger.info("活动id "+actId);
        //被助力者unionid
        String fromUnionid = strs[2].toString();
        logger.info("被助力者unionid "+fromUnionid);
        //订阅号的原始id
        String subscribeWechatAccount = strs[3].toString();
        logger.info("订阅号的原始id "+subscribeWechatAccount);

        String toUnionid = subscribeWeChatResponseService.getCodeByUnionid(code, serviceAppId);
        unionidUserMappingService.insertUnionidUserMappingEntity(actId, fromUnionid, toUnionid);
        ModelAndView modelAndView = new ModelAndView("/taskbaby/subscribeIndex");
        String qrPicId = subscribeServiceMappingService.getQrPicIdBySubscribeWechatAccount(subscribeWechatAccount);
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
        String imagePath = sysPictureService.getPictureURL(qrPicId);
        response.reset();

        //得到输出流
        OutputStream output = response.getOutputStream();
        //使用编码处理文件流的情况
        //设定输出的类型
        response.setContentType(JPG);
        //得到图片的真实路径
        //得到图片的文件流
        InputStream imageIn = new FileInputStream(new File(imagePath));
        //得到输入的编码器，将文件流进行jpg格式编码
        JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
        //得到编码后的图片对象
        BufferedImage image = decoder.decodeAsBufferedImage();
        //得到输出的编码器
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);
        // 对图片进行输出编码
        encoder.encode(image);
        //关闭文件流
        imageIn.close();
        output.close();
    }
}






















