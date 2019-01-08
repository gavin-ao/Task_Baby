package data.driven.cm.business.taskbaby;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.entity.taskbaby.CustomerServiceInfoEntity;

/**
 * @Author: lxl
 * @describe 客服信息
 * @Date: 2019/1/8 10:54
 * @Version 1.0
 */
public interface CustomerServiceInfoService {

    /**
     * @description 随机获取公众号下的客服的MediaId一个现采用随机，以后需要改成权重值来取值
     * @author lxl
     * @date 2019-01-08 10:55
     * @param authorizationAppid 公众号appId
     * @param keyWord 回复的关键词
     * @return
     */
    JSONObject getRandomMediaId(String authorizationAppid, String keyWord);
}
