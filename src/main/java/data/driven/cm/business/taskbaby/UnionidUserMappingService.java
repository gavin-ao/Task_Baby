package data.driven.cm.business.taskbaby;

/**
 * @Author: lxl
 * @describe unionid用户关系Service
 * @Date: 2018/12/19 12:22
 * @Version 1.0
 */
public interface UnionidUserMappingService {

    /**
     * @description 新增unionid用户关系Entity
     * @author lxl
     * @date 2018-12-19 16:39
     * @param actId 活动id
     * @param fromUnionid 被助力者unionid
     * @param toUnionid 助力者unionid
     * @return
     */
    String insertUnionidUserMappingEntity(String actId,String fromUnionid,String toUnionid);
}
