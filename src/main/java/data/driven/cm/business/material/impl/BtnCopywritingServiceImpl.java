package data.driven.cm.business.material.impl;

import data.driven.cm.business.material.BtnCopywritingService;
import data.driven.cm.dao.JDBCBaseDao;
import data.driven.cm.entity.material.BtnCopywritingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 按钮文案service
 * @author hejinkai
 * @date 2018/8/26
 */
@Service
public class BtnCopywritingServiceImpl implements BtnCopywritingService {

    @Autowired
    private JDBCBaseDao jdbcBaseDao;

    @Override
    public Map<String, String> findBtnCopyWritingMapByActId(String actId) {
        String sql = "select btn_code,btn_text from btn_copywriting where act_id = ?";
        List<BtnCopywritingEntity> list = jdbcBaseDao.queryList(BtnCopywritingEntity.class, sql, actId);
        if(list != null && list.size() > 0){
            return list.stream().collect(Collectors.toMap(o -> o.getBtnCode(), o -> o.getBtnText()));
        }
        return null;
    }
}
