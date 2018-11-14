package data.driven.cm.controller.wechatapi;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.Constant;
import data.driven.cm.util.JSONUtil;
import data.driven.cm.util.QRCodeUtil;
import data.driven.cm.util.UUIDUtil;
import data.driven.cm.util.WXUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 二维码controller
 * @author 何晋凯
 * @date 2018/06/06
 */
@Controller
@RequestMapping(path = "/wechatapi/qrcode")
public class QRCodeApiController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeApiController.class);

    /**
     * 根据内容生成二维码，输出流返回.jpg图片
     * @param response
     * @param content
     */
    @RequestMapping(path = "/createQrcode")
    public void createQrcode(HttpServletResponse response, String content){
        OutputStream os = null;
        try{
            os = response.getOutputStream();
            QRCodeUtil.createQRCode(content, os);
            os.flush();
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            if(os != null){
                try{
                    os.close();
                }catch (Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 根据内容生成二维码，输出图片地址
     * @param response
     * @param content
     */
    @RequestMapping(path = "/createQrcodeFile")
    public JSONObject createQrcodeFile(HttpServletResponse response, String content){
        String tempFileName = Constant.WXQRCODE_TEMP_FILE_FOLDER + UUIDUtil.getUUID();
        String fileName = Constant.FILE_UPLOAD_PATH + tempFileName;
        try{
            QRCodeUtil.createQRCode(content, fileName);
            JSONObject result = JSONUtil.putMsg(true, "200", "二维码生成成功");
            result.put("qrcodeUrl", tempFileName);
            return result;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return JSONUtil.putMsg(false, "101", "二维码生成失败");
        }
    }

    /**
     * 调用微信接口生成二维码，返回二维码地址
     * @param appid
     * @param secret
     * @param scene 自定义参数
     * @param page  页面路径
     */
    @ResponseBody
    @RequestMapping(path = "/createWXQrcode")
    public JSONObject createWXQrcode(HttpServletRequest request, String appid, String secret, String scene, String page){
        JSONObject accessTokenJson = WXUtil.getAccessToken(appid, secret);
        String accessToken = null;
        if(accessTokenJson.containsKey("access_token")){
            accessToken = accessTokenJson.getString("access_token");
        }else{
            logger.warn("微信获取AccessToken失败", accessTokenJson.toJSONString());
            return JSONUtil.putMsg(false, "101", "access_token获取失败");
        }


        JSONObject result = WXUtil.createWXQrcodeB(scene, page, accessToken);
        if(!result.getBoolean("success")){
            logger.warn("微信调用接口创建二维码失败");
        }else{
//            String pathHead = RequestUtil.getStaticFilePath(request);
            result.put("qrcodeUrl", Constant.STATIC_FILE_PATH + result.getString("qrcodeUrl"));
        }
        return result;
    }

    /**
     * 调用微信接口生成二维码，返回二维码地址
     * @param appid
     * @param secret
     * @param path  页面路径
     */
    @ResponseBody
    @RequestMapping(path = "/createWXQrcodeA")
    public JSONObject createWXQrcodeA(HttpServletRequest request, String appid, String secret, String path){
        JSONObject accessTokenJson = WXUtil.getAccessToken(appid, secret);
        String accessToken = null;
        if(accessTokenJson.containsKey("access_token")){
            accessToken = accessTokenJson.getString("access_token");
        }else{
            logger.warn("微信获取AccessToken失败", accessTokenJson.toJSONString());
            return JSONUtil.putMsg(false, "101", "access_token获取失败");
        }

        JSONObject result = WXUtil.createWXQrcodeA(path, accessToken);
        if(!result.getBoolean("success")){
            logger.warn("微信调用接口创建二维码失败");
        }else{
//            String pathHead = RequestUtil.getStaticFilePath(request);
            result.put("qrcodeUrl", Constant.STATIC_FILE_PATH + result.getString("qrcodeUrl"));
        }
        return result;
    }
}
