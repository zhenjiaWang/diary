package com.diary.service.res;

import com.diary.common.StoreException;
import com.diary.entity.res.ResEvent;
import com.diary.entity.res.ResEventNo;
import com.diary.entity.res.ResEventYes;
import com.diary.providers.store.res.ResEventStore;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.guiceside.commons.Page;
import org.guiceside.persistence.TransactionType;
import org.guiceside.persistence.Transactional;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.HQuery;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;

import java.util.List;

/**
 * Created by Lara Croft on 2016/12/21.
 */
@Singleton
public class ResEventService extends HQuery implements ResEventStore {


    @Inject
    private ResEventNoService resEventNoService;

    @Inject
    private ResEventYesService resEventYesService;

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public ResEvent getById(Long id, Selector... selectors) throws StoreException {
        return $(id, selectors).get(ResEvent.class);
    }

    @Override
    @Transactional(type = TransactionType.READ_ONLY)
    public Page<ResEvent> getPageList(int start, int limit, List<Selector> selectorList) throws StoreException {
        return $(selectorList).page(ResEvent.class, start, limit);
    }

    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void save(ResEvent resEvent, Persistent persistent, ResEventYes resEventYes, Persistent resEventYesPersistent, ResEventNo resEventNo, Persistent resEventNoPersistent) throws StoreException {
        $(resEvent).save(persistent);
        if(resEventYes!=null){
            resEventYesService.save(resEventYes,resEventYesPersistent);
        }
        if(resEventNo!=null){
            resEventNoService.save(resEventNo,resEventNoPersistent);
        }
    }

    @Override
    @Transactional(type = TransactionType.READ_WRITE)
    public void delete(ResEvent resEvent) throws StoreException {
        $(resEvent).delete();
    }
}
