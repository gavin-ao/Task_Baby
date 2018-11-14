package data.driven.cm.component;

import data.driven.cm.common.ApplicationSessionFactory;
import data.driven.cm.entity.user.UserInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * session过滤器，用于过滤受保护的资源
 * @author hejinkai
 * @date 2018/7/1
 */
public class SessionFilter implements Filter{

    private static Logger logger = LoggerFactory.getLogger(WechatApiFilter.class);

    /** 不过滤的url集合 **/
    public static Set<String> EXCLUDE_URL_SET = new HashSet<String>();
    /** 系统用户登录，注册服务url前缀 **/
    public static String SERVICE_URL = "/service";

    static{
        //静态资源文件不需要过滤
        EXCLUDE_URL_SET.add("/static");
        //微信接口不需要过滤
        EXCLUDE_URL_SET.add("/wechatapi");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("SessionFilter init ...");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        String serverName = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        if(canFilter(uri)){
            ApplicationSessionFactory.init(request, response);
            if(!uri.startsWith(SERVICE_URL)){
                UserInfoEntity user = ApplicationSessionFactory.getUser(request, response);
                if(user == null){
                    response.sendRedirect(serverName + SERVICE_URL + "/login");
                    return ;
                }
            }
        }
        filterChain.doFilter(request, response);
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
