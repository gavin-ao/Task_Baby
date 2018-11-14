package data.driven.cm.business.wechat;

import com.alibaba.fastjson.JSONObject;

/**
 * 微信统计servcie
 * @author hejinkai
 * @date 2018/7/3
 */
public interface WechatTotalService {

    /**
     * 根据时间范围统计活跃度
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalActivityNum(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计活跃度，返回数据走势图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalActivityNumView(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计传播范围
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalSpreadRangeNum(String appInfoId, String startDate, String endDate);
    /**
     * 根据时间范围统计传播范围，返回数据走势图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalSpreadRangeNumView(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计裂变效果新增人数
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalFissionEffectNewPeopleNum(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计裂变效果新增人数，返回数据走势图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalFissionEffectNewPeopleNumView(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计分享次数
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalShareNum(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计分享次数，返回数据走势图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalShareNumView(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计分享人数
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalSharePeopleNum(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计分享人数，返回数据走势图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalSharePeopleNumView(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计新老用户占比
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalOldAndNewUser(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计传播轨迹 - 默认为0的方式统计
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalSpreadTrajectory(String appInfoId, String startDate, String endDate);

    /**
     * 根据时间范围统计传播轨迹
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @param type  统计方式  A-C 在 B-A之前， 为0时按照A-C和B独立出，为1时按照A-C和B-A出现
     * @return
     */
    public JSONObject totalSpreadTrajectory(String appInfoId, String startDate, String endDate, Integer type);

    /**
     * 漏斗图
     * @param appInfoId
     * @param startDate
     * @param endDate
     * @return
     */
    public JSONObject totalFunnelView(String appInfoId, String startDate, String endDate);
}
