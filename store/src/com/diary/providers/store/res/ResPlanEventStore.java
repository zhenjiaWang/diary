package com.diary.providers.store.res;


import com.diary.common.StoreException;
import com.diary.entity.res.ResEvent;
import com.diary.entity.res.ResEventNo;
import com.diary.entity.res.ResEventYes;
import com.diary.entity.res.ResPlanEvent;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;


public interface ResPlanEventStore {


    ResPlanEvent getById(Long id, Selector... selectors) throws StoreException;

    List<ResPlanEvent> getListByPlanId(Long jobId) throws StoreException;

    List<ResPlanEvent> getList(List<Selector> selectorList) throws StoreException;

    void save(ResPlanEvent resPlanEvent, Persistent persistent) throws StoreException;

    void save(ResPlanEvent resPlanEvent, Persistent persistent, ResEvent resEvent, Persistent resEventPersistent,
              ResEventYes resEventYes, Persistent resEventYesPersistent,
              ResEventNo resEventNo, Persistent resEventNoPersistent) throws StoreException;

    void delete(ResPlanEvent resPlanEvent) throws StoreException;

}
