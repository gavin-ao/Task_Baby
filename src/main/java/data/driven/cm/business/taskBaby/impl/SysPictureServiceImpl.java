package data.driven.cm.business.taskBaby.impl;

import data.driven.cm.business.taskBaby.SysPictureService;
import data.driven.cm.dao.JDBCBaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @program: Task_Baby
 * @description: 系统图片服务
 * @author: Logan
 * @create: 2018-11-15 19:22
 **/
@Service
public class SysPictureServiceImpl implements SysPictureService {

    @Autowired
    private JDBCBaseDao dao;
    /**
     * 获取系统图片的简要信息
     * @param picId
     * @return  Key：【picId, filePath,realName】
     */
    @Override
    public Map<String, Object> getPictureSimpleInfo(String picId) {
        String sql=" select picture_id as picId, file_path as filePath,real_name as realName where picture_id=?";
        return dao.getMapResult(sql,picId);
    }

    /**
     * TODO 根据picId主键获取图片的url
     * @param picId
     * @return
     */
    @Override
    public String getPictureURL(String picId) {
        String sql = "select file_path as filePath where picture_id = ?";
        Object filePath = dao.getColumn(sql,picId);
        return filePath.toString();
    }
}
