package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.AreaService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.taskbaby.AreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: lxl
 * @describe 行政区划Impl
 * @Date: 2018/11/29 18:58
 * @Version 1.0
 */
@Service
public class AreaServiceImpl implements AreaService {
    @Autowired
    JDBCBaseDao jdbcBaseDao;
    /**
     * @description 得到所有省份信息
     * @author lxl
     * @date 2018-11-29 19:10
     * @return 省行政区划集合
     */
    @Override
    public List<AreaEntity> getProvinces() {
        String sql = "select province,name from area where city is null and province is not null";
        List<AreaEntity> areaEntities = jdbcBaseDao.queryList(AreaEntity.class,sql);
        return areaEntities;
    }

    /**
     * @description 通过省区划码得到该省下的所有城市信息
     * @author lxl
     * @date 2018-11-29 19:17
     * @param province 省区划码
     * @return 城市行政区划集合
     */
    @Override
    public List<AreaEntity> getCities(String province) {
        String sql = "select city,name from area where  province = ? and city is not null and district is null";
        List<AreaEntity> areaEntities = jdbcBaseDao.queryList(AreaEntity.class,sql,province);
        return areaEntities;
    }

    /**
     * @description 通过城市区划码得到该城市下的所有的区县
     * @author lxl
     * @date 2018-11-29 19:21
     * @param city 城市区码
     * @return 区行政区划集合
     */
    @Override
    public List<AreaEntity> getDistricts(String city) {
        String sql = "select district,name from area where city = ? and district is not null";
        List<AreaEntity> areaEntities = jdbcBaseDao.queryList(AreaEntity.class,sql,city);
        return areaEntities;
    }
}
