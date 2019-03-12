package com.sevenshop.data;

import com.sevenshop.SevenShopApplication;
import com.sevenshop.data.dao.Address;
import com.sevenshop.data.dao.AddressDao;
import com.sevenshop.data.dao.User;
import com.sevenshop.data.dao.UserDao;

import java.util.List;

/**
 * <pre>
 *     desc   : 数据库管理类
 *     version: 1.0
 * </pre>
 */

public class DataManager {

    /**
     * 添加数据
     *
     * @param user
     */
    public static void insertUser(User user) {
        SevenShopApplication.getDaoSession().getUserDao().insert(user);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteUser(Long id) {
        SevenShopApplication.getDaoSession().getUserDao().deleteByKey(id);
    }

    /**
     * 更新数据
     *
     * @param user
     */
    public static void updateUser(User user) {
        SevenShopApplication.getDaoSession().getUserDao().update(user);
    }

    /**
     * 查询条件为Type=Phone的数据
     *
     * @return
     */
    public static List<User> queryUser(String phone) {
        return SevenShopApplication.getDaoSession().getUserDao().queryBuilder().where
                (UserDao.Properties.Phone.eq(phone)).list();
    }


    /**
     * 添加数据
     *
     * @param address
     */
    public static void insertAddress(Address address) {
        SevenShopApplication.getDaoSession().getAddressDao().insert(address);
    }

    /**
     * 删除数据
     *
     * @param id
     */
    public static void deleteAddress(Long id) {
        SevenShopApplication.getDaoSession().getAddressDao().deleteByKey(id);
    }

    /**
     * 更新数据
     *
     * @param address
     */
    public static void updateAddress(Address address) {
        SevenShopApplication.getDaoSession().getAddressDao().update(address);
    }

    /**
     * 查询条件为Type=UserId的数据
     *
     * @return
     */
    public static List<Address> queryAddress(Long userId) {
        return SevenShopApplication.getDaoSession().getAddressDao().queryBuilder().where
                (AddressDao.Properties.UserId.eq(userId)).list();
    }
}
