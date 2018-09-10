package com.diary.providers.store.app;


import com.diary.common.StoreException;
import com.diary.entity.app.AppUserCar;
import org.guiceside.commons.Page;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface AppUserCarStore {


    AppUserCar getById(Long id, Selector... selectors) throws StoreException;


    Page<AppUserCar> getPageList(int start,
                                 int limit, List<Selector> selectorList) throws StoreException;

    List<AppUserCar> getByUserId(Long userId) throws StoreException;

    void save(AppUserCar appUserCar, Persistent persistent) throws StoreException;

    void delete(AppUserCar appUserCar) throws StoreException;

}