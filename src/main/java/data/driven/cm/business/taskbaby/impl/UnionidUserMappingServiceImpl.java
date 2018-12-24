package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.UnionidUserMappingService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.UnionidUserMappingEntity;
import data.driven.cm.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: lxl
 * @describe unionid用户关系Impl
 * @Date: 2018/12/19 12:23
 * @Version 1.0
 */
@Service
public class UnionidUserMappingServiceImpl implements UnionidUserMappingService {
    private static final Logger logger = LoggerFactory.getLogger(UnionidUserMappingServiceImpl.class);

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

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
    @Override
    public String insertUnionidUserMappingEntity(String actId, String fromUnionid, String toUnionid,String subscribeWechatAccount) {
        Date createAt = new Date();
        String unionidUserMappingId = getUnionidUserMappingId(actId,fromUnionid,toUnionid);
        if(unionidUserMappingId == null){
            try {
                unionidUserMappingId = UUIDUtil.getUUID();
                String sql = "INSERT INTO unionid_user_mapping (id,act_id,from_unionid,to_unionid,create_at,subscribe_wechat_account) VALUES (?,?,?,?,?,?)";
                jdbcBaseDao.executeUpdate(sql,unionidUserMappingId,actId,fromUnionid,toUnionid,createAt,subscribeWechatAccount);
                return unionidUserMappingId;
            }catch (Exception e){
                logger.info("新增unionid用户关系Entity失败！");
                e.printStackTrace();
            }
        }
        return unionidUserMappingId;
    }

    /**
     * 通过活动id、被助力者Unionid、助力者Unionid
     * @param actId 活动id
     * @param fromUnionid 活动id
     * @return 返回 unionid用户关系id
     */
    @Override
    public String getUnionidUserMappingId(String actId,String fromUnionid, String toUnionid) {
        String sql = "SELECT id from unionid_user_mapping where act_id = ? and from_unionid = ? and to_unionid = ?";
        Object id = jdbcBaseDao.getColumn(sql, actId,fromUnionid,toUnionid);
        if(id != null){
            return id.toString();
        }
        return null;
    }

    /**
     * 根据活动id,助力者(扫码者)unionId,匹配被助力者(发起者)的unionid;
     * @author Logan
     * @date 2018-12-21 11:29
     * @param actId 活动id
     * @param toUnionId 助力者(扫码者)unionId

     * @return 被助力者(发起者)的unionid列表
     */
    @Override
    public List<String> getFormUnionIdList(String actId, String toUnionId) {
        String sql = "select from_unionid from unionid_user_mapping where act_id =? and to_unionid = ?";
        return jdbcBaseDao.getColumns(String.class,sql,actId,toUnionId);
    }

    /**
     * 根据订阅号的原始id,助力者(扫码者)unionId,被助力者(发起者)的unionid,匹配活动id (actId);
     * @author Logan
     * @date 2018-12-21 15:28
     * @param sbuscribeWechatAccount 订阅号的原始id
     * @param toUnionId

     * @return
     */
    @Override
    public List<UnionidUserMappingEntity> getUnionidUserMappingList(String sbuscribeWechatAccount, String toUnionId) {
        String sql = "SELECT DISTINCT map.* FROM unionid_user_mapping map "+
        "JOIN mat_activity act ON act.act_id = map.act_id "+
        "WHERE act.STATUS = 1  AND subscribe_wechat_account = ? AND to_unionid = ?";

        return jdbcBaseDao.queryList(UnionidUserMappingEntity.class,sql,sbuscribeWechatAccount,toUnionId);
    }

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
    @Override
    public void updateStatus(Integer stats, String actId, String fromUnionid, String toUnionid) {
        String sql = "UPDATE unionid_user_mapping set status= ? where act_id = ? and from_unionid = ? and to_unionid = ?";
        jdbcBaseDao.executeUpdate(sql,stats,actId,fromUnionid,toUnionid);
    }
}
