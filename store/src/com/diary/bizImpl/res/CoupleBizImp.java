package com.diary.bizImpl.res;

import com.diary.common.BizException;
import com.diary.common.StoreException;
import com.diary.entity.app.AppUser;
import com.diary.entity.app.AppUserCouple;
import com.diary.entity.res.*;
import com.diary.entity.utils.DrdsIDUtils;
import com.diary.entity.utils.DrdsTable;
import com.diary.entity.utils.GameUtils;
import com.diary.providers.biz.res.CoupleBiz;
import com.diary.providers.store.app.AppUserCoupleStore;
import com.diary.providers.store.res.ResCoupleEffectStore;
import com.diary.providers.store.res.ResCoupleRequireStore;
import com.diary.providers.store.res.ResCoupleStore;
import com.google.inject.Inject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.JsonUtils;
import org.guiceside.commons.Page;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class CoupleBizImp extends BaseBiz implements CoupleBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String state(Integer gender) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            if (resCoupleStore != null&&appUserCoupleStore!=null) {
                List<Selector> selectorList = new ArrayList<>();
                if(gender!=null){
                    selectorList.add(SelectorUtils.$eq("gender", gender));
                }
                selectorList.add(SelectorUtils.$eq("useYn","Y"));
                List<ResCouple> coupleList= resCoupleStore.getList(selectorList);
                JSONObject stateObj = new JSONObject();
                if (coupleList != null && !coupleList.isEmpty()) {
                    for (ResCouple resCouple : coupleList) {
                       AppUserCouple appUserCouple= appUserCoupleStore.getByCoupleId(resCouple.getId());
                       if(appUserCouple!=null){
                           AppUser appUser=appUserCouple.getUserId();
                           if(appUser!=null){
                               JSONObject userObj = JsonUtils.formIdEntity(appUser, 0);
                               if(userObj!=null){
                                   GameUtils.minish(userObj);
                                   stateObj.put(resCouple.getId()+"",userObj);
                               }
                           }
                       }
                    }
                }
                resultObj.put("state", stateObj);
                resultObj.put("result", 0);
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String list(Integer start, Integer limit,Integer gender, String keyword) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            ResCoupleEffectStore resCoupleEffectStore = hsfServiceFactory.consumer(ResCoupleEffectStore.class);
            if (resCoupleStore != null&&resCoupleEffectStore!=null&&resCoupleRequireStore!=null) {
                List<Selector> selectorList = new ArrayList<>();
                if(gender!=null){
                    selectorList.add(SelectorUtils.$eq("gender", gender));
                }
                Page<ResCouple> resCouplePage = resCoupleStore.getPageList(start, limit, selectorList);
                JSONArray jobArray = new JSONArray();
                if (resCouplePage != null) {
                    List<ResCouple> jobList = resCouplePage.getResultList();
                    if (jobList != null && !jobList.isEmpty()) {
                        for (ResCouple resCouple : jobList) {

                            selectorList.clear();
                            selectorList.add(SelectorUtils.$eq("coupleId.id", resCouple.getId()));
                            List<ResCoupleEffect> jobEffectList = resCoupleEffectStore.getList(selectorList);
                            JSONArray jobEffectArray = new JSONArray();
                            if (jobEffectList != null && !jobEffectList.isEmpty()) {
                                for (ResCoupleEffect resCoupleEffect : jobEffectList) {
                                    JSONObject resCoupleEffectObj = JsonUtils.formIdEntity(resCoupleEffect);
                                    if (resCoupleEffectObj != null) {
                                        jobEffectArray.add(resCoupleEffectObj);
                                    }
                                }
                            }

                            selectorList.clear();
                            selectorList.add(SelectorUtils.$eq("coupleId.id", resCouple.getId()));
                            List<ResCoupleRequire> jobRequireList = resCoupleRequireStore.getList(selectorList);
                            JSONArray jobRequireArray = new JSONArray();
                            if (jobRequireList != null && !jobRequireList.isEmpty()) {
                                for (ResCoupleRequire resCoupleRequire : jobRequireList) {
                                    JSONObject resCoupleRequireObj = JsonUtils.formIdEntity(resCoupleRequire);
                                    if (resCoupleRequireObj != null) {
                                        jobRequireArray.add(resCoupleRequireObj);
                                    }
                                }
                            }

                            JSONObject jobObj = JsonUtils.formIdEntity(resCouple);
                            if (jobObj != null) {
                                jobObj.put("effectList",jobEffectArray);
                                jobObj.put("requireList",jobRequireArray);
                                jobArray.add(jobObj);
                            }
                        }
                    }
                    JSONObject pageObj = buildPage2Obj(resCouplePage);
                    resultObj.put("pageObj", pageObj);
                }
                resultObj.put("list", jobArray);
                resultObj.put("result", 0);
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String add(String title,  Integer gender, String remarks) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleStore != null) {
                ResCouple resCouple = new ResCouple();
                resCouple.setId(DrdsIDUtils.getID(DrdsTable.RES));
                resCouple.setTitle(title);
                resCouple.setGender(gender);
                resCouple.setRemarks(remarks);
                bind(resCouple, 1l);
                resCouple.setUseYn("Y");
                resCoupleStore.save(resCouple, Persistent.SAVE);
                resultObj.put("result", 0);
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String edit(Long id, String title,  Integer gender, String remarks) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleStore != null) {
                ResCouple resCouple = resCoupleStore.getById(id);
                if (resCouple != null) {
                    resCouple.setTitle(title);
                    resCouple.setGender(gender);
                    resCouple.setRemarks(remarks);
                    bind(resCouple, 1l);
                    resCoupleStore.save(resCouple, Persistent.UPDATE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String enable(Long id) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleStore != null) {
                ResCouple resCouple = resCoupleStore.getById(id);
                if (resCouple != null) {
                    bind(resCouple, 1l);
                    resCouple.setUseYn("Y");
                    resCoupleStore.save(resCouple, Persistent.UPDATE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String disable(Long id) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleStore != null) {
                ResCouple resCouple = resCoupleStore.getById(id);
                if (resCouple != null) {
                    bind(resCouple, 1l);
                    resCouple.setUseYn("N");
                    resCoupleStore.save(resCouple, Persistent.UPDATE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String addEffect(Long jobId, String operation, String attrKey, Integer value) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleEffectStore resCoupleEffectStore = hsfServiceFactory.consumer(ResCoupleEffectStore.class);
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleEffectStore != null && resCoupleStore != null) {
                ResCouple resCouple = resCoupleStore.getById(jobId);
                if (resCouple != null) {
                    ResCoupleEffect resCoupleEffect = new ResCoupleEffect();
                    resCoupleEffect.setId(DrdsIDUtils.getID(DrdsTable.RES));
                    resCoupleEffect.setCoupleId(resCouple);
                    resCoupleEffect.setOperation(operation);
                    resCoupleEffect.setAttrKey(attrKey);
                    resCoupleEffect.setValue(value);
                    bind(resCoupleEffect, 1l);
                    resCoupleEffect.setUseYn("Y");
                    resCoupleEffectStore.save(resCoupleEffect, Persistent.SAVE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String editEffect(Long id, String operation, String attrKey, Integer value) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleEffectStore resCoupleEffectStore = hsfServiceFactory.consumer(ResCoupleEffectStore.class);
            if (resCoupleEffectStore != null) {
                ResCoupleEffect resCoupleEffect = resCoupleEffectStore.getById(id);
                if (resCoupleEffect != null) {
                    resCoupleEffect.setOperation(operation);
                    resCoupleEffect.setAttrKey(attrKey);
                    resCoupleEffect.setValue(value);
                    bind(resCoupleEffect, 1l);
                    resCoupleEffectStore.save(resCoupleEffect, Persistent.UPDATE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String deleteEffect(Long id) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleEffectStore resCoupleEffectStore = hsfServiceFactory.consumer(ResCoupleEffectStore.class);
            if (resCoupleEffectStore != null) {
                ResCoupleEffect resCoupleEffect = resCoupleEffectStore.getById(id);
                if (resCoupleEffect != null) {
                    resCoupleEffectStore.delete(resCoupleEffect);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String effectList(Long jobId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleEffectStore resCoupleEffectStore = hsfServiceFactory.consumer(ResCoupleEffectStore.class);
            if (resCoupleEffectStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$alias("coupleId", "coupleId"));
                selectorList.add(SelectorUtils.$eq("coupleId.id", jobId));
                List<ResCoupleEffect> jobEffectList = resCoupleEffectStore.getList(selectorList);
                JSONArray jobEffectArray = new JSONArray();
                if (jobEffectList != null && !jobEffectList.isEmpty()) {
                    for (ResCoupleEffect resCoupleEffect : jobEffectList) {
                        JSONObject resCoupleEffectObj = JsonUtils.formIdEntity(resCoupleEffect);
                        if (resCoupleEffectObj != null) {
                            jobEffectArray.add(resCoupleEffectObj);
                        }
                    }
                }
                resultObj.put("list", jobEffectArray);
                resultObj.put("result", 0);
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String addRequire(Long jobId, String attrKey, Integer value) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            if (resCoupleRequireStore != null && resCoupleStore != null) {
                ResCouple resCouple = resCoupleStore.getById(jobId);
                if (resCouple != null) {
                    ResCoupleRequire resCoupleRequire = new ResCoupleRequire();
                    resCoupleRequire.setId(DrdsIDUtils.getID(DrdsTable.RES));
                    resCoupleRequire.setCoupleId(resCouple);
                    resCoupleRequire.setAttrKey(attrKey);
                    resCoupleRequire.setValue(value);
                    bind(resCoupleRequire, 1l);
                    resCoupleRequire.setUseYn("Y");
                    resCoupleRequireStore.save(resCoupleRequire, Persistent.SAVE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String editRequire(Long id, String attrKey, Integer value) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            if (resCoupleRequireStore != null) {
                ResCoupleRequire resCoupleRequire = resCoupleRequireStore.getById(id);
                if (resCoupleRequire != null) {
                    resCoupleRequire.setAttrKey(attrKey);
                    resCoupleRequire.setValue(value);
                    bind(resCoupleRequire, 1l);
                    resCoupleRequireStore.save(resCoupleRequire, Persistent.UPDATE);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String deleteRequire(Long id) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            if (resCoupleRequireStore != null) {
                ResCoupleRequire resCoupleRequire = resCoupleRequireStore.getById(id);
                if (resCoupleRequire != null) {
                    resCoupleRequireStore.delete(resCoupleRequire);
                    resultObj.put("result", 0);
                }
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }

    @Override
    public String requireList(Long jobId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            if (resCoupleRequireStore != null) {
                List<Selector> selectorList = new ArrayList<>();
                selectorList.add(SelectorUtils.$alias("coupleId", "coupleId"));
                selectorList.add(SelectorUtils.$eq("coupleId.id", jobId));
                List<ResCoupleRequire> jobRequireList = resCoupleRequireStore.getList(selectorList);
                JSONArray jobRequireArray = new JSONArray();
                if (jobRequireList != null && !jobRequireList.isEmpty()) {
                    for (ResCoupleRequire resCoupleRequire : jobRequireList) {
                        JSONObject resCoupleRequireObj = JsonUtils.formIdEntity(resCoupleRequire);
                        if (resCoupleRequireObj != null) {
                            jobRequireArray.add(resCoupleRequireObj);
                        }
                    }
                }
                resultObj.put("list", jobRequireArray);
                resultObj.put("result", 0);
            }
        } catch (Exception ex) {
            if (ex instanceof StoreException) {
                throw new StoreException(ex);
            } else {
                throw new BizException(ex);
            }
        }
        return resultObj.toString();
    }
}
