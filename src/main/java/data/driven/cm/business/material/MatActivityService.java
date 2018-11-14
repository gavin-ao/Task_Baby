package data.driven.cm.business.material;

import data.driven.cm.vo.material.MatActivityVO;

/**
 * 活动-素材service
 * @author hejinkai
 * @date 2018/7/15
 */
public interface MatActivityService {

    /**
     * 根据活动id获取图片的url
     * @param actId
     * @return
     */
    public String getMatActivityPictureUrl(String actId);

    /**
     * 根据活动id获取图片的url，活动规则，兑换规则
     * @param actId
     * @return
     */
    public MatActivityVO getMatActivityInfo(String actId);

    /**
     * 根据获取有效的活动信息
     * @param actId
     * @return
     */
    public MatActivityVO getValidMatActivityInfo(String actId);

    /**
     * 根据门店id获取图片的url，活动规则，兑换规则
     * @param storeId
     * @return
     */
    public MatActivityVO getMatActivityInfoByStore(String storeId);

    /**
     * 根据门店id获取活动id，活动奖励url
     * @param storeId
     * @return
     */
    public MatActivityVO getAnyMatActivityInfoByStore(String storeId);

}
