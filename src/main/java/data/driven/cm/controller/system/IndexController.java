package data.driven.cm.controller.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 何晋凯
 * @date 2018/06/04
 */
@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.sendRedirect("/wechat/total/dataStatistics");
//        ModelAndView modelAndView = new ModelAndView("/index");
//        modelAndView.addObject("success", true);
//        modelAndView.addObject("msg", "访问成功");
//        return modelAndView;
    }

}
