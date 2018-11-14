package data.driven.cm.business.user.impl;

import data.driven.cm.business.user.UserInfoService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.user.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户信息
 * @author hejinkai
 * @date 2018/7/1
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public UserInfoEntity getUser(String userName, String pwd) {
        String sql = "select user_id,user_name,nick_name,real_name,email,mobile_phone,wechat_number,sex,company_name,job,create_at from sys_user_info where user_name = ? and pwd = ?";
        List<UserInfoEntity> list = jdbcBaseDao.queryList(UserInfoEntity.class, sql, userName, pwd);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }
}
