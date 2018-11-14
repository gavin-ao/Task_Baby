package data.driven.cm.controller.system;

import data.driven.cm.util.QRCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 二维码controller
 * @author 何晋凯
 * @date 2018/06/06
 */
@Controller
@RequestMapping(path = "/system/qrcode")
public class QRCodeController {
    private static final Logger logger = LoggerFactory.getLogger(QRCodeController.class);

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
}
