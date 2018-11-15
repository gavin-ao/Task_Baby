package data.driven.cm.business.taskBaby;

import data.driven.cm.entity.taskBaby.wechatUserInfoEntity;

public interface WechatUserInfoService {
    //public boolean insertUser();
    public wechatUserInfoEntity getUserInfoById(String wechatAccount, String openId);

}
