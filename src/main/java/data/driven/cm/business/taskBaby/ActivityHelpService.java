package data.driven.cm.business.taskBaby;

import java.util.Map;

/**
 * @Author: lxl
 * @describe 活动助力Service
 * @Date: 2018/11/16 16:25
 * @Version 1.0
 */
public interface ActivityHelpService {

    /**
     * 新增活动助力信息
     * @param actId 活动Id
     * @param wechatAccount 公众号原始ID
     * @param formId 被助力者ID
     * @param helpSuccessStatus 助力成功状态,0 助力未成功 1 助力成功
     * @param helpNumber 助力人数
     * @return
     */
    public String insertActivityHelpEntity(String actId,String wechatAccount,String formId,Integer helpSuccessStatus,Integer helpNumber);

    /**
     * 得到还差多少人，已经有多少人领取
     * @param formId 被助力者Id
     * @param actId 活动Id
     * @return map 1.surplushHelpNumber 还需多少人 2.endTotal 已经有xx人领取 都是Integer
     */
    public Map<String,Integer> getTotalNumber(String formId,String actId);

    /**
     * 通过任务Id得到当前完成的人数
     * @param actId 任务ID
     * @return 返回完成人数
     */
    public Integer getEndHelpCount(String actId);










































}
