package data.driven.cm.controller.system;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.util.FileUtil;
import data.driven.cm.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件上传测试
 * @author 何晋凯
 * @date 2018/06/05
 */
@Controller
@RequestMapping(path = "/system/file")
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @RequestMapping(path = "/fileUploadDemo")
    public ModelAndView fileUploadDemo(){
        ModelAndView modelAndView = new ModelAndView("demo/fileUploadDemo");
        return modelAndView;
    }

    /**
     * 文件上传
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public JSONObject upload(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartHttpServletRequest.getFiles("file");

        for (MultipartFile file : files){
            String fileName = file.getOriginalFilename();
            try {
                fileName = UUIDUtil.getUUID() + fileName.substring(fileName.lastIndexOf("."));
                JSONObject uploadResult = FileUtil.uploadFile(file.getBytes(), fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        result.put("success", true);
        return result;
    }

}
