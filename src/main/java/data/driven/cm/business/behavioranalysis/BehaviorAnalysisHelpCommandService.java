package data.driven.cm.business.behavioranalysis;

/**
 * 行为分析_助力奖励埋点
 * @author hejinkai
 * @date 2018/9/19
 */
public interface BehaviorAnalysisHelpCommandService {

    /**
     * 领取奖励并弹出窗，插入行为数据
     * @param helpId
     * @param actId
     * @param storeId
     * @param appInfoId
     * @param wechatUserId
     * @return
     */
    public boolean openWindowInsert(String helpId, String actId, String storeId, String appInfoId, String wechatUserId);

    /**
     * 点击url触发的接口
     * @param id
     * @return
     */
    public boolean updateClickUrl(String id);

    /**
     * 判断是否存在
     * @param id
     * @return
     */
    public boolean exitsId(String id);

}
