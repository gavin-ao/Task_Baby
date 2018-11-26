package data.driven.cm.business.taskBaby;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @Author: lxl
 * @describe 只是消息的自动回复
 * @Date: 2018/11/12 17:56
 * @Version 1.0
 */
public interface WeChatService {

    /**
     *  调用核心服务类接收处理请求
     * @param request
     * @param response
     * @param appid
     * @return
     * @throws UnsupportedEncodingException
     */
    public String processRequest(HttpServletRequest request, HttpServletResponse response,String appid) throws UnsupportedEncodingException;

}
