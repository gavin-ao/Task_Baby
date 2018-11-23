package data.driven.cm.business.taskBaby;

/**
 * @Author: lxl
 * @describe 公众号详细信息表Service
 * @Date: 2018/11/22 17:39
 * @Version 1.0
 */
public interface WchatPublicDetailService {

    /**
     * 增加公众号详细信息
     * @param wechatPublicId 公众号信息表外键
     * @param nickName 授权方昵称
     * @param headImg 授权方头像
     * @param serviceTypeInfo 授权方公众号类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param verifyTypeInfo 授权方认证类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param userName 授权方公众号的原始ID
     * @param principalName 公众号的主体名称
     * @param alias 授权方公众号所设置的微信号
     * @param businessInfo 能的开通状况，微信公众号发过来的是json格式的字符串，现直接存储到字段里
     * @param qrcodeUrl 二维码图片的URL
     * @param authorizationAppid 授权方appid
     * @param funcInfo 公众号授权给开发者的权限集列表
     * @return 公众号详细信息ID
     */
    public String insertWechatPublicDetailEntity(String wechatPublicId, String nickName, String headImg, String serviceTypeInfo,
                                                 String verifyTypeInfo, String userName, String principalName, String alias,
                                                 String businessInfo, String qrcodeUrl, String authorizationAppid, String funcInfo);

    /**
     * 根据AppId,查询detialId
     * @author:     Logan
     * @date:       2018/11/23 17:26
     * @params:     [wechatPublicId]
     * @return:     java.lang.String
     **/
    public String getWechatPublicDetailIdByAppId(String authorizationAppid);
    
    /**
     * 
     * @author:     Logan
     * @date:       2018/11/23 17:27
     * @param wechatPublicId 公众号信息表外键
     * @param nickName 授权方昵称
     * @param headImg 授权方头像
     * @param serviceTypeInfo 授权方公众号类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param verifyTypeInfo 授权方认证类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param userName 授权方公众号的原始ID
     * @param principalName 公众号的主体名称
     * @param alias 授权方公众号所设置的微信号
     * @param businessInfo 能的开通状况，微信公众号发过来的是json格式的字符串，现直接存储到字段里
     * @param qrcodeUrl 二维码图片的URL
     * @param authorizationAppid 授权方appid
     * @param funcInfo 公众号授权给开发者的权限集列表*
    **/        
    public void updateWechatPublicDetail(String wechatPublicId, String nickName, String headImg, String serviceTypeInfo,
                                           String verifyTypeInfo, String userName, String principalName, String alias,
                                           String businessInfo, String qrcodeUrl, String authorizationAppid, String funcInfo);
}
