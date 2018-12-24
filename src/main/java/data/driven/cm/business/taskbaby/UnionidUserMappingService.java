package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.UnionidUserMappingEntity;

import java.util.List;

/**
 * @Author: lxl
 * @describe unionid用户关系Service
 * @Date: 2018/12/19 12:22
 * @Version 1.0
 */
public interface UnionidUserMappingService {

    /**
     * @description 新增unionid用户关系Entity
     * @author lxl
     * @date 2018-12-19 16:39
     * @param actId 活动id
     * @param fromUnionid 被助力者unionid
     * @param toUnionid 助力者unionid
     * @param subscribeWechatAccount 订阅号的原始id
     * @return
     */
    String insertUnionidUserMappingEntity(String actId,String fromUnionid,String toUnionid,String subscribeWechatAccount);

    /**
     * 通过活动id、被助力者Unionid、助力者Unionid
     * @param actId 活动id
     * @param fromUnionid 活动id
     * @return 返回 unionid用户关系id
     */

    String getUnionidUserMappingId(String actId,String fromUnionid, String toUnionid);

    /**
    * 根据活动id,助力者(扫码者)unionId,匹配被助力者(发起者)的unionid;
    * @author Logan备注`
    * @date 2018-12-21 11:29
    * @param actId 活动id
    * @param toUnionId 助力者(扫码者)unionId

    * @return 被助力者(发起者)的unionid列表
    */
    List<String> getFormUnionIdList(String actId,String toUnionId);  
    
    /**
    * 根据订阅号的原始id,助力者(扫码者)unionId,被助力者(发起者)的unionid,匹配活动id (actId);
    * @author Logan 
    * @date 2018-12-21 15:28
    * @param sbuscribeWechatAccount 订阅号的原始id
    * @param toUnionId
    
    * @return 
    */

    List<UnionidUserMappingEntity> getUnionidUserMappingList(String sbuscribeWechatAccount, String toUnionId);

    /**
     * @description 修改状态
     * @author lxl
     * @date 2018-12-24 14:33
     * @param stats 处理状态 0 未处理,1已助力 2 助力失败
     * @param actId 活动id
     * @param fromUnionid 被助力者的Unionid
     * @param toUnionid 助力者的Unionid
     * @return
     */
    void updateStatus(Integer stats,String actId,String fromUnionid,String toUnionid);


}
