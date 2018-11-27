package data.driven.cm.business.taskbaby;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: lxl
 * @describe 只是消息的自动回复
 * @Date: 2018/11/12 17:56
 */
public interface WeChatService {

    /**
     *  调用核心服务类接收处理请求
     * @author lxl
     * @param request request内容
     * @param response response内容
     * @param appId 公众号appid
     * @return 返回信息
     * @throws UnsupportedEncodingException
     */
     String processRequest(HttpServletRequest request, HttpServletResponse response,String appId)
             throws UnsupportedEncodingException;

}
