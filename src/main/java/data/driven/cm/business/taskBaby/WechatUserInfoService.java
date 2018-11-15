package data.driven.cm.business.taskBaby;

import data.driven.cm.entity.taskBaby.WechatUserInfoEntity;

public interface WechatUserInfoService {
    //public boolean insertUser();
    public WechatUserInfoEntity getUserInfoById(String wechatAccount, String openId);

}
