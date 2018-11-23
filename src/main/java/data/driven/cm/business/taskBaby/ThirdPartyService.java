package data.driven.cm.business.taskBaby;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lxl
 * @describe 第三方平台Service
 * @Date: 2018/11/23 9:24
 * @Version 1.0
 */
public interface ThirdPartyService {
    /**
     * 解密第三方发送的xml
     * @param request
     * @param response
     * @throws Exception
     */
    public void hadleAuthorize(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
