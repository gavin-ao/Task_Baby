package data.driven.cm.business.material;

import java.util.Map;

/**
 * 按钮文案service
 * @author hejinkai
 * @date 2018/8/26
 */
public interface BtnCopywritingService {
    /**
     * 根据活动id查询按钮文案,以code作为key，text作为value返回map
     * @param actId
     * @return
     */
    public Map<String, String> findBtnCopyWritingMapByActId(String actId);

}
