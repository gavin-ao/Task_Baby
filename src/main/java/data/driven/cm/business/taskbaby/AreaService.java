package data.driven.cm.business.taskbaby;

import data.driven.cm.entity.taskbaby.AreaEntity;

import java.util.List;

/**
 * @Author: lxl
 * @describe 行政区划Service
 * @Date: 2018/11/29 18:58
 * @Version 1.0
 */
public interface AreaService {
    /**
     * @description 得到所有省份信息
     * @author lxl
     * @date 2018-11-29 19:10
     * @return 省行政区划集合
     */
    List<AreaEntity> getProvinces();

    /**
     * @description 通过省区划码得到该省下的所有城市信息
     * @author lxl
     * @date 2018-11-29 19:17
     * @param province 省区划码
     * @return 市行政区划集合
     */
    List<AreaEntity> getCities(String province);

    /**
     * @description 通过城市区划码得到该城市下的所有的区县
     * @author lxl
     * @date 2018-11-29 19:21
     * @param city 城市区码
     * @return 区行政区划集合
     */
    List<AreaEntity> getDistricts(String city);
}
