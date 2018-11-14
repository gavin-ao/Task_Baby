package data.driven.cm.component;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.common.WechatApiSession;
import data.driven.cm.common.WechatApiSessionBean;
import data.driven.cm.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 过滤微信的非法请求或者失效请求
 * @author hejinkai
 * @date 2018/6/27
 */
public class WechatApiFilter implements Filter{

    private static Logger logger = LoggerFactory.getLogger(WechatApiFilter.class);

    /** 不过滤的url集合 **/
    private static Set<String> EXCLUDE_URL_SET = new HashSet<String>();

    static{
        //静态资源文件不需要过滤
        EXCLUDE_URL_SET.add("/static");
        //微信登录和同步接口不需要过滤
        EXCLUDE_URL_SET.add("/wechatapi/service/");
        //不需要授权/登录就可以访问的接口
        EXCLUDE_URL_SET.add("/wechatapi/nologin");
        EXCLUDE_URL_SET.add("/wechatapi/wsva");
        EXCLUDE_URL_SET.add("/wechatapi/wechat");
        EXCLUDE_URL_SET.add("/wechatapi/public/login");


    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("WechatApiFilter init ...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = System.currentTimeMillis();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        String sessionID = request.getParameter("sessionID");
        String helpId = request.getParameter("helpId");
        String actId = request.getParameter("actId");
        if(canFilter(uri)){
            if(sessionID == null){
                noAuthority(request, response);
                return;
            }else{

                WechatApiSessionBean wechatApiSessionBean = WechatApiSession.getSessionBean(sessionID);
                if(wechatApiSessionBean == null || wechatApiSessionBean.getUserInfo() == null || wechatApiSessionBean.getUserInfo().getWechatUserId()== null){
                    noAuthority(request, response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
        long end = System.currentTimeMillis();
        logger.info("请求地址：" + uri + "-----过滤器耗时：" + (end - start)/1000.0 + "秒" + "--------sessionID="+sessionID+"----------helpId="+helpId+"----------actId="+actId);
    }

    /**
     * 无权限请求处理
     * @param request
     */
    private void noAuthority(HttpServletRequest request, HttpServletResponse response){
        JSONObject result = JSONUtil.putMsg(false, "101", "noAuthority");
        request.setAttribute("success", false);
        request.setAttribute("msg", "noAuthority");
        try{
            response.getOutputStream().print(result.toJSONString());
        }catch (IOException e){
            logger.error(e.getMessage(), e);
        }
    }
    /**
     * 判断该次请求是否需要过滤
     * @return  需要过滤 true， 不需要过滤 - false
     */
    public boolean canFilter(String uri){
        for(String url : EXCLUDE_URL_SET){
            if(uri.startsWith(url)){
                return false;
            }
        }
        return true;
    }


    @Override
    public void destroy() {

    }
}
