package data.driven.cm.util;

import javax.servlet.http.HttpServletRequest;

import static data.driven.cm.common.Constant.STATIC_FILE_PATH;

/**
 * @author 何晋凯
 * @date 2018/06/04
 */
public class RequestUtil {

    public static String getHost(HttpServletRequest request){
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public static String getStaticFilePath(HttpServletRequest request){
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + STATIC_FILE_PATH;
    }
}
