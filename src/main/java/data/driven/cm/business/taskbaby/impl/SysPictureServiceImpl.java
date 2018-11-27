package data.driven.cm.business.taskbaby.impl;

import data.driven.cm.business.taskbaby.SysPictureService;
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
     * 通过图片id返回图片的URL
     * @author lxl
     * @param picId 图片id,主键
     * @return 返回图片的URL
     */
    @Override
    public String getPictureURL(String picId) {
        String sql = "select file_path as filePath from sys_picture where picture_id = ?";
        Object filePath = dao.getColumn(sql,picId);
        return filePath.toString();
    }
}
