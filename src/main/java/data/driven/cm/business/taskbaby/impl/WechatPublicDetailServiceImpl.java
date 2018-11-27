package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.WechatPublicDetailService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: lxl
 * @describe 公众号详细信息表Impl
 * @Date: 2018/11/22 17:40
 * @Version 1.0
 */
@Service
public class WechatPublicDetailServiceImpl implements WechatPublicDetailService {
    @Autowired
    JDBCBaseDao jdbcBaseDao;

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
    @Override
    public String insertWechatPublicDetailEntity(String wechatPublicId, String nickName, String headImg, String serviceTypeInfo,
                                                 String verifyTypeInfo, String userName, String principalName, String alias,
                                                 String businessInfo, String qrcodeUrl, String authorizationAppid,
                                                 String funcInfo) {
        Date createAt = new Date();
        String id = UUIDUtil.getUUID();
        String sql = "INSERT INTO wechat_public_detail (id,wechat_public_id,nick_name,head_img,service_type_info," +
                "verify_type_info,user_name,principal_name,alias,business_info,qrcode_url,authorization_appid," +
                "func_info,create_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        jdbcBaseDao.executeUpdate(sql,id,wechatPublicId,nickName,headImg,serviceTypeInfo,verifyTypeInfo,userName,
                principalName,alias,businessInfo,qrcodeUrl,authorizationAppid,funcInfo,createAt);
        return id;
    }
    /**
     * 根据AppId,查询detialId
     * @author: Logan
     * @param authorizationAppid  公众号appid
     * @return id 公众号详细信息表id
     */
    @Override
   public String getWechatPublicDetailIdByAppId(String authorizationAppid){
        String sql ="select id from wechat_public_detail where authorization_appid=?";
        Object id = jdbcBaseDao.getColumn(sql,authorizationAppid);
        if(id != null){
            return id.toString();
        }else{
            return null;
        }
   }

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
    @Override
    public void updateWechatPublicDetail(String wechatPublicId, String nickName,
                                           String headImg, String serviceTypeInfo,
                                           String verifyTypeInfo, String userName,
                                           String principalName, String alias,
                                           String businessInfo, String qrcodeUrl,
                                           String authorizationAppid, String funcInfo) {
       String sql = "update wechat_public_detail set nick_name=?,head_img=?,service_type_info=?," +
               "verify_type_info=?,user_name=?,principal_name=?,alias=?,business_info=?,qrcode_url=?,authorization_appid=?," +
               "func_info=? where wechat_public_id=?";

       jdbcBaseDao.executeUpdate(sql,nickName,headImg,serviceTypeInfo,verifyTypeInfo,
               userName,principalName,alias,businessInfo,qrcodeUrl, authorizationAppid,funcInfo,wechatPublicId);
    }
}
