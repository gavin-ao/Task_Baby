package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.WechatUserInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskBaby.WechatUserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: Task_Baby
 * @description: 从数据库里面获取微信粉丝的服务
 * @author: Logan
 * @create: 2018-11-15 11:49
 **/
@Service
public class WechatUserInfoServiceImpl implements WechatUserInfoService {
    @Autowired
    private JDBCBaseDao dao;
    @Override
    public WechatUserInfoEntity getUserInfoById(String wechatAccount, String openId) {
        return null;
    }
}
