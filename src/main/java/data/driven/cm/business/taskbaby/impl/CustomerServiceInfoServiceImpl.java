package data.driven.cm.business.taskbaby.impl;

import com.alibaba.fastjson.JSONObject;
import data.driven.cm.business.taskbaby.CustomerServiceInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.CustomerServiceInfoEntity;
import data.driven.cm.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: lxl
 * @describe 客服信息Impl
 * @Date: 2019/1/8 10:58
 * @Version 1.0
 */
@Service
public class CustomerServiceInfoServiceImpl implements CustomerServiceInfoService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceInfoServiceImpl.class);
    @Autowired
    private JDBCBaseDao jdbcBaseDao;
    /**
     * @description 随机获取公众号下的客服的MediaId一个现采用随机，以后需要改成权重值来取值
     * @author lxl
     * @date 2019-01-08 10:55
     * @param authorizationAppid 公众号appId
     * @param keyWord 回复的关键词
     * @return
     */
    @Override
    public JSONObject getRandomMediaId(String authorizationAppid, String keyWord) {
        JSONObject result = new JSONObject();
        String sql= "select csi.name,csi.media_id,csi.qr_code_path from sys_user_info sui, customer_configure ccf," +
                "customer_service_info csi,customer_configure_mapping ccm where sui.authorization_appid = ? and" +
                " ccf.key_word = ? and sui.user_id = ccf.sys_user_id and " +
                "ccf.customer_configure_id = ccm.customer_configure_id and " +
                "ccm.customer_service_info_id = csi.customer_service_info_id";
        List<CustomerServiceInfoEntity> customerServiceInfoEntities = jdbcBaseDao.queryList(CustomerServiceInfoEntity.class,
                sql,authorizationAppid,keyWord);
        Random random = new Random();
       if (customerServiceInfoEntities.size() > 0){
           int n = random.nextInt(customerServiceInfoEntities.size());
           CustomerServiceInfoEntity customerServiceInfoEntity = customerServiceInfoEntities.get(n);
           result.put("success", true);
           result.put("code", "200");
           result.put("data",customerServiceInfoEntity.getMediaId());
           return result;
       }else{
           logger.info("客服信息列表无数据");
           return JSONUtil.putMsg(false,"200","无数据");
       }
    }

    public static void main(String[] args){
        List<String> strings = new ArrayList<>();
        strings.add("lxl");
        strings.add("ce");
        Random random = new Random();
        int n = random.nextInt(strings.size());
        System.out.println(strings.get(n));
    }
}
