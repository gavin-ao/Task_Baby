package data.driven.cm.business.sys;

import data.driven.cm.entity.sys.StoreEntity;

/**
 * 门店service
 * @author hejinkai
 * @date 2018/10/27
 */
public interface StoreService {

    /**
     * 根据id查询数据对象
     * @param storeId
     * @return
     */
    public StoreEntity getStoreById(String storeId);

    /**
     * 根据用户id 查询数据对象
     * @param manager 用户 Id
     * @return 门店对象
     */
    public StoreEntity getStoreByUserId(String manager);

}
