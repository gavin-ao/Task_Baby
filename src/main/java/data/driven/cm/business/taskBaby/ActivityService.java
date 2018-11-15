package data.driven.cm.business.taskBaby;

import data.driven.cm.entity.taskBaby.MatActivityEntity;
import sun.font.TrueTypeFont;

/**
 * Activyty数据服务
 */
public interface ActivityService {
    /**
     *
     * @param wechatAccount 微信账号
     * @param keyWord 关键字
     * @param status 0是关闭，1是开启
     * @return 活动实体
     */
    public String getMatActivityId(String wechatAccount, String keyWord,Integer status);
}
