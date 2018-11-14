package data.driven.cm.business.reward.impl;

import data.driven.cm.business.reward.RewardActContentService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.reward.RewardActContentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 活动奖励文案Service
 * @author hejinkai
 * @date 2018/8/8
 */
@Service
public class RewardActContentServiceImpl implements RewardActContentService{

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public RewardActContentEntity getRewardActContentByActAndType(String actId, Integer commandType) {
        String sql = "select content_id,act_id,content_title,content_head,content_mid,content_foot,content_btn,command_type,create_at,remark from reward_act_content where act_id = ? and command_type = ? order by create_at,content_id limit 1";
        List<RewardActContentEntity> list = jdbcBaseDao.queryList(RewardActContentEntity.class, sql, actId, commandType);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
