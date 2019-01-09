package data.driven.cm.business.taskbaby;

/**
 * @Author: lxl
 * @describe 公众号详细信息表Service
 * @Date: 2018/11/22 17:39
 * @Version 1.0
 */
public interface WechatPublicDetailService {

    /**
     * 增加公众号详细信息
     * @author lxl
     * @param wechatPublicId     公众号信息表外键
     * @param nickName           授权方昵称
     * @param headImg            授权方头像
     * @param serviceTypeInfo    授权方公众号类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param verifyTypeInfo     授权方认证类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param userName           授权方公众号的原始ID
     * @param principalName      公众号的主体名称
     * @param alias              授权方公众号所设置的微信号
     * @param businessInfo       能的开通状况，微信公众号发过来的是json格式的字符串，现直接存储到字段里
     * @param qrcodeUrl          二维码图片的URL
     * @param authorizationAppid 授权方appid
     * @param funcInfo           公众号授权给开发者的权限集列表
     * @return 公众号详细信息ID
     */
    String insertWechatPublicDetailEntity(String wechatPublicId, String nickName, String headImg, String serviceTypeInfo,
                                          String verifyTypeInfo, String userName, String principalName, String alias,
                                          String businessInfo, String qrcodeUrl, String authorizationAppid, String funcInfo);

    /**
     * 根据AppId,查询detialId
     * @author: Logan
     * @param authorizationAppid  公众号appid
     * @return id 公众号详细信息表id
     */
    String getWechatPublicDetailIdByAppId(String authorizationAppid);

    /**
     * 修改公众号详细信息表
     * @param wechatPublicId     公众号信息表外键
     * @param nickName           授权方昵称
     * @param headImg            授权方头像
     * @param serviceTypeInfo    授权方公众号类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param verifyTypeInfo     授权方认证类型,在微信公众号上用的是数组，现改成用逗号拼接到一个字段里
     * @param userName           授权方公众号的原始ID
     * @param principalName      公众号的主体名称
     * @param alias              授权方公众号所设置的微信号
     * @param businessInfo       能的开通状况，微信公众号发过来的是json格式的字符串，现直接存储到字段里
     * @param qrcodeUrl          二维码图片的URL
     * @param authorizationAppid 授权方appid
     * @param funcInfo           公众号授权给开发者的权限集列表*
     * @author: Logan
     * @date: 2018/11/23 17:27
     **/
    void updateWechatPublicDetail(String wechatPublicId, String nickName, String headImg, String serviceTypeInfo,
                                  String verifyTypeInfo, String userName, String principalName, String alias,
                                  String businessInfo, String qrcodeUrl, String authorizationAppid, String funcInfo);

    /**
    * 是否服务号
    * @author Logan
    * @date 2018-12-19 15:04
    * @param appId

    * @return
    */
   boolean isServiceType(String appId);

    /**
     * @description 通过AppId 查询公众号昵称
     * @author lxl
     * @date 2019-01-09 11:47
     * @param authorizationAppid 公众号appid
     * @return 公众号昵称
     */
    String getNickNameByAppId(String authorizationAppid);
}
