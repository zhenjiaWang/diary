package com.diary.bizImpl.app;

import com.diary.common.BizException;
import com.diary.common.StoreException;
import com.diary.entity.app.*;
import com.diary.entity.res.*;
import com.diary.entity.utils.DrdsIDUtils;
import com.diary.entity.utils.DrdsTable;
import com.diary.entity.utils.GameUtils;
import com.diary.providers.biz.app.UserBiz;
import com.diary.providers.store.app.*;
import com.diary.providers.store.res.*;
import com.google.inject.Inject;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.guiceside.commons.JsonUtils;
import org.guiceside.commons.OKHttpUtil;
import org.guiceside.commons.Page;
import org.guiceside.commons.lang.NumberUtils;
import org.guiceside.commons.lang.StringUtils;
import org.guiceside.persistence.entity.search.SelectorUtils;
import org.guiceside.persistence.hibernate.dao.enums.Persistent;
import org.guiceside.persistence.hibernate.dao.hquery.Selector;
import org.guiceside.support.hsf.BaseBiz;
import org.guiceside.support.hsf.HSFServiceFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;


/**
 * @author zhenjiaWang
 * @version 1.0 2012-05
 * @since JDK1.5
 */

public class UserBizImp extends BaseBiz implements UserBiz {

    @Inject
    private HSFServiceFactory hsfServiceFactory;

    @Override
    public String login(String code, Long userId, String nickName, String avatarUrl,
                        Integer gender,
                        String city, String province, String country) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            if (appUserStore != null) {
                AppUser appUser = null;
                if (userId != null) {
                    appUser = appUserStore.getById(userId);
                }
                if (appUser != null) {
                    appUser.setNickName(nickName);
                    appUser.setGender(gender);
                    appUser.setAvatarUrl(avatarUrl);
                    appUser.setCity(city);
                    appUser.setProvince(province);
                    appUser.setCountry(country);
                    bind(appUser, 1l);
                    appUserStore.save(appUser, Persistent.UPDATE);
                } else {
                    String appId = "wxadc0c22656d6c116";
                    String secret = "890342da41f48c2dbbd1b4038060b056";
                    if (StringUtils.isNotBlank(secret)) {
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("appid", appId);
                        paramMap.put("secret", secret);
                        paramMap.put("js_code", code);
                        paramMap.put("grant_type", "authorization_code");
                        String resultWX = OKHttpUtil.get("https://api.weixin.qq.com/sns/jscode2session", paramMap);
                        if (StringUtils.isNotBlank(resultWX)) {
                            JSONObject wxObj = JSONObject.fromObject(resultWX);
                            if (wxObj != null) {
                                String openid = wxObj.getString("openid");
                                if (StringUtils.isNotBlank(openid)) {
                                    appUser = appUserStore.getByOpenId(openid);
                                    if (appUser == null) {
                                        appUser = new AppUser();
                                        appUser.setId(DrdsIDUtils.getID(DrdsTable.APP));
                                        appUser.setOpenId(openid);
                                        appUser.setNickName(nickName);
                                        appUser.setGender(gender);
                                        appUser.setAvatarUrl(avatarUrl);
                                        appUser.setCity(city);
                                        appUser.setProvince(province);
                                        appUser.setCountry(country);
                                        appUser.setUseYn("Y");
                                        bind(appUser, 1l);
                                        appUserStore.save(appUser, Persistent.SAVE);
                                    }

                                }
                            }
                        }
                    }
                }
                JSONObject userObj = JsonUtils.formIdEntity(appUser, 0);
                if (userObj != null) {
                    GameUtils.minish(userObj);
                    userObj.put("userId", appUser.getId() + "");
                    resultObj.put("userData", userObj);
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

    private void loadUserData(JSONObject resultObj, Long userId) throws BizException {
        JSONObject infoObj = null;
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserLimitStore appUserLimitStore = hsfServiceFactory.consumer(AppUserLimitStore.class);
            AppUserJobStore appUserJobStore = hsfServiceFactory.consumer(AppUserJobStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            AppUserClothesStore appUserClothesStore = hsfServiceFactory.consumer(AppUserClothesStore.class);
            AppUserLuxuryStore appUserLuxuryStore = hsfServiceFactory.consumer(AppUserLuxuryStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            AppUserFundStore appUserFundStore = hsfServiceFactory.consumer(AppUserFundStore.class);
            if (appUserStore != null && appUserManStore != null && appUserLadyStore != null && appUserLimitStore != null && appUserJobStore != null
                    && appUserCarStore != null && appUserHouseStore != null && appUserClothesStore != null && appUserLuxuryStore != null && appUserCoupleStore != null
                    && appUserFundStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    JSONArray attrArray = new JSONArray();
                    GameUtils.attrList(attrArray, appUser.getGender(), 1);
                    resultObj.put("attrList", attrArray);
                    AppUserJob appUserJob = appUserJobStore.getByUserId(userId);
                    AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                    String myJobId = "";
                    String myCoupleId = "";
                    if (appUserJob != null) {
                        myJobId = appUserJob.getJobId().getId() + "";
                    }
                    if (appUserCouple != null) {
                        myCoupleId = appUserCouple.getCoupleId().getId() + "";
                    }
                    Integer fund = appUserFundStore.getSumByUserId(userId);
                    if (fund == null) {
                        fund = 0;
                    }
                    if (fund < 0) {
                        fund = 0;
                    }
                    JSONArray myFundArray = new JSONArray();
                    JSONObject myFundDiff = new JSONObject();
                    List<AppUserFund> appUserFundList = appUserFundStore.getByUserId(userId);
                    if (appUserFundList != null && !appUserFundList.isEmpty()) {
                        for (AppUserFund appUserFund : appUserFundList) {
                            ResFund resFund = appUserFund.getFundId();
                            if (resFund != null) {
                                myFundArray.add(resFund.getId() + "");
                                myFundDiff.put(resFund.getId() + "",GameUtils.formatGroupingUsed((appUserFund.getMoney().longValue()-appUserFund.getBuy().longValue())));
                            }
                        }
                    }
                    if (appUser.getGender() == 1) {
                        AppUserMan appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            JSONArray myCarArray = new JSONArray();
                            JSONObject myCarNumber = new JSONObject();
                            List<AppUserCar> appUserCarList = appUserCarStore.getByUserId(userId);
                            if (appUserCarList != null && !appUserCarList.isEmpty()) {
                                Set<Long> carSetIds = new HashSet<>();
                                for (AppUserCar appUserCar : appUserCarList) {
                                    ResCar resCar = appUserCar.getCarId();
                                    if (resCar != null) {
                                        if (!carSetIds.contains(resCar.getId())) {
                                            myCarNumber.put(resCar.getId() + "", 1);
                                            myCarArray.add(resCar.getId() + "");
                                            carSetIds.add(resCar.getId());
                                        } else {
                                            int carNumber = myCarNumber.getInt(resCar.getId() + "");
                                            myCarNumber.put(resCar.getId() + "", carNumber + 1);
                                        }
                                    }
                                }
                            }


                            JSONArray myHouseArray = new JSONArray();
                            JSONObject myHouseNumber = new JSONObject();
                            List<AppUserHouse> appUserHouseList = appUserHouseStore.getByUserId(userId);
                            if (appUserHouseList != null && !appUserHouseList.isEmpty()) {
                                Set<Long> houseSetIds = new HashSet<>();
                                for (AppUserHouse appUserHouse : appUserHouseList) {
                                    ResHouse resHouse = appUserHouse.getHouseId();
                                    if (resHouse != null) {
                                        if (!houseSetIds.contains(resHouse.getId())) {
                                            myHouseNumber.put(resHouse.getId() + "", 1);
                                            myHouseArray.add(resHouse.getId() + "");
                                            houseSetIds.add(resHouse.getId());
                                        } else {
                                            int houseNumber = myHouseNumber.getInt(resHouse.getId() + "");
                                            myHouseNumber.put(resHouse.getId() + "", houseNumber + 1);
                                        }
                                    }
                                }
                            }

                            Integer day = appUserMan.getDays();
                            Integer jobLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "JOB");
                            Integer luckLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "LUCK");
                            Integer houseLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "HOUSE");
                            Integer carLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "CAR");
                            Integer coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "COUPLE");
                            Integer fundLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "FUND");
                            infoObj = JsonUtils.formIdEntity(appUserMan, 0);
                            if (infoObj != null) {
                                GameUtils.man(infoObj, jobLimit, fundLimit, luckLimit, houseLimit, carLimit, coupleLimit);
                                infoObj.put("myCarArray", myCarArray);
                                infoObj.put("myCarNumber", myCarNumber);
                                infoObj.put("myHouseArray", myHouseArray);
                                infoObj.put("myHouseNumber", myHouseNumber);
                                infoObj.put("currentDay", GameUtils.currentDay(day));
                            }
                        }
                    } else if (appUser.getGender() == 2) {
                        AppUserLady appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            JSONArray myClothesArray = new JSONArray();
                            JSONObject myClothesNumber = new JSONObject();
                            List<AppUserClothes> appUserClothesList = appUserClothesStore.getByUserId(userId);
                            if (appUserClothesList != null && !appUserClothesList.isEmpty()) {
                                Set<Long> clothesSetIds = new HashSet<>();
                                for (AppUserClothes appUserClothes : appUserClothesList) {
                                    ResClothes resClothes = appUserClothes.getClothesId();
                                    if (resClothes != null) {
                                        if (!clothesSetIds.contains(resClothes.getId())) {
                                            myClothesNumber.put(resClothes.getId() + "", 1);
                                            myClothesArray.add(resClothes.getId() + "");
                                            clothesSetIds.add(resClothes.getId());
                                        } else {
                                            int carNumber = myClothesNumber.getInt(resClothes.getId() + "");
                                            myClothesNumber.put(resClothes.getId() + "", carNumber + 1);
                                        }
                                    }
                                }
                            }


                            JSONArray myLuxuryArray = new JSONArray();
                            JSONObject myLuxuryNumber = new JSONObject();
                            List<AppUserLuxury> appUserLuxuryList = appUserLuxuryStore.getByUserId(userId);
                            if (appUserLuxuryList != null && !appUserLuxuryList.isEmpty()) {
                                Set<Long> luxurySetIds = new HashSet<>();
                                for (AppUserLuxury appUserLuxury : appUserLuxuryList) {
                                    ResLuxury resLuxury = appUserLuxury.getLuxuryId();
                                    if (resLuxury != null) {
                                        if (!luxurySetIds.contains(resLuxury.getId())) {
                                            myLuxuryNumber.put(resLuxury.getId() + "", 1);
                                            myLuxuryArray.add(resLuxury.getId() + "");
                                            luxurySetIds.add(resLuxury.getId());
                                        } else {
                                            int houseNumber = myLuxuryNumber.getInt(resLuxury.getId() + "");
                                            myLuxuryNumber.put(resLuxury.getId() + "", houseNumber + 1);
                                        }
                                    }
                                }
                            }

                            Integer day = appUserLady.getDays();
                            Integer jobLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "JOB");
                            Integer luckLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "LUCK");
                            Integer clothesLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "CLOTHES");
                            Integer luxuryLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "LUXURY");
                            Integer coupleLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "COUPLE");
                            Integer fundLimit = appUserLimitStore.getCountByUserIdDayAction(userId, day, "FUND");
                            infoObj = JsonUtils.formIdEntity(appUserLady, 0);
                            if (infoObj != null) {
                                GameUtils.lady(infoObj, jobLimit, fundLimit, luckLimit, clothesLimit, luxuryLimit, coupleLimit);
                                infoObj.put("myClothesArray", myClothesArray);
                                infoObj.put("myClothesNumber", myClothesNumber);
                                infoObj.put("myLuxuryArray", myLuxuryArray);
                                infoObj.put("myLuxuryNumber", myLuxuryNumber);
                                infoObj.put("currentDay", GameUtils.currentDay(day));
                            }
                        }
                    }
                    if (infoObj != null) {
                        infoObj.put("myFundArray", myFundArray);
                        infoObj.put("myFundDiff", myFundDiff);
                        infoObj.put("fund", GameUtils.formatGroupingUsed(fund.longValue()));
                        infoObj.put("myJobId", myJobId);
                        infoObj.put("myCoupleId", myCoupleId);
                        resultObj.put("userState", infoObj);
                    }
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
    }

    @Override
    public String refresh(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        loadUserData(resultObj, userId);
        return resultObj.toString();
    }

    @Override
    public String start(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            if (appUserStore != null && appUserManStore != null && appUserLadyStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    Integer days = 0;
                    boolean newGame = false;
                    if (appUser.getGender() == 1) {
                        AppUserMan appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan == null) {
                            appUserMan = new AppUserMan();
                            appUserMan.setId(DrdsIDUtils.getID(DrdsTable.APP));
                            appUserMan.setUserId(appUser);
                            appUserMan.setHealth(100);
                            appUserMan.setMoney(8000);
                            appUserMan.setAbility(100);
                            appUserMan.setExperience(100);
                            appUserMan.setHappy(100);
                            appUserMan.setPositive(100);
                            appUserMan.setConnections(100);
                            appUserMan.setDays(GameUtils.intDays);
                            appUserMan.setHours(GameUtils.intHours);
                            appUserMan.setScore(0);
                            appUserMan.setUseYn("Y");
                            bind(appUserMan, userId);
                            appUserManStore.save(appUserMan, Persistent.SAVE);
                            newGame = true;
                        }
                        days = appUserMan.getDays();
                    } else if (appUser.getGender() == 2) {
                        AppUserLady appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady == null) {
                            appUserLady = new AppUserLady();
                            appUserLady.setId(DrdsIDUtils.getID(DrdsTable.APP));
                            appUserLady.setUserId(appUser);
                            appUserLady.setHealth(100);
                            appUserLady.setMoney(8000);
                            appUserLady.setAbility(100);
                            appUserLady.setWisdom(100);
                            appUserLady.setHappy(100);
                            appUserLady.setBeauty(100);
                            appUserLady.setPopularity(100);
                            appUserLady.setDays(GameUtils.intDays);
                            appUserLady.setHours(GameUtils.intHours);
                            appUserLady.setScore(0);
                            appUserLady.setUseYn("Y");
                            bind(appUserLady, userId);
                            appUserLadyStore.save(appUserLady, Persistent.SAVE);
                            newGame = true;
                        }
                        days = appUserLady.getDays();
                    }
                    loadUserData(resultObj, userId);
                    String nightText = "第" + GameUtils.dayText(days) + "天";
                    resultObj.put("nightText", nightText);
                    if (newGame) {
                        JSONArray resultArray = new JSONArray();
                        GameUtils.addResultArray(resultArray, "北京是你的舞台，初到北京，给你8000启动资金。", null);
                        GameUtils.addResultArray(resultArray, "你可以先找份最初级工作，这样每天可以获得工资。安顿好后要多四处逛逛见见市面，提高你的个人成长能力。", null);
                        GameUtils.addResultArray(resultArray, "看" + GameUtils.gameDays + "天后你能混出什么样来", null);
                        resultObj.put("resultArray", resultArray);
                    }
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
    public String updateUserInfo(Long userId, String userNickName, String userAvatarUrl, String userGender, String userCity, String userProvince, String userCountry) throws BizException {
        return null;
    }

    @Override
    public String resData(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            ResJobStore resJobStore = hsfServiceFactory.consumer(ResJobStore.class);
            ResPlanStore resPlanStore = hsfServiceFactory.consumer(ResPlanStore.class);
            ResCarStore resCarStore = hsfServiceFactory.consumer(ResCarStore.class);
            ResHouseStore resHouseStore = hsfServiceFactory.consumer(ResHouseStore.class);
            ResClothesStore resClothesStore = hsfServiceFactory.consumer(ResClothesStore.class);
            ResLuxuryStore resLuxuryStore = hsfServiceFactory.consumer(ResLuxuryStore.class);
            ResCoupleStore resCoupleStore = hsfServiceFactory.consumer(ResCoupleStore.class);
            ResLuckStore resLuckStore = hsfServiceFactory.consumer(ResLuckStore.class);
            ResFundStore resFundStore = hsfServiceFactory.consumer(ResFundStore.class);
            ResTipStore resTipStore = hsfServiceFactory.consumer(ResTipStore.class);
            if (appUserStore != null && resJobStore != null && resPlanStore != null && resCarStore != null && resHouseStore != null && resClothesStore != null
                    && resLuxuryStore != null && resCoupleStore != null && resLuckStore != null && resTipStore != null && resFundStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    JSONArray jobArray = new JSONArray();
                    JSONArray planArray = new JSONArray();
                    JSONArray coupleArray = new JSONArray();
                    JSONArray carArray = new JSONArray();
                    JSONArray houseArray = new JSONArray();
                    JSONArray clothesArray = new JSONArray();
                    JSONArray luxuryArray = new JSONArray();
                    JSONArray luckArray = new JSONArray();
                    JSONArray fundArray = new JSONArray();
                    JSONArray tipArray = new JSONArray();
                    List<Selector> selectorList = new ArrayList<>();
                    selectorList.add(SelectorUtils.$eq("gender", appUser.getGender()));
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    selectorList.add(SelectorUtils.$order("price", true));
                    List<ResJob> jobList = resJobStore.getList(selectorList);
                    if (jobList != null && !jobList.isEmpty()) {
                        for (ResJob resJob : jobList) {
                            JSONObject jobObj = JsonUtils.formIdEntity(resJob, 0);
                            if (jobObj != null) {
                                GameUtils.minish(jobObj);
                                jobArray.add(jobObj);
                            }
                        }
                    }

                    selectorList.clear();
                    selectorList.add(SelectorUtils.$eq("gender", appUser.getGender()));
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    selectorList.add(SelectorUtils.$order("displayOrder", true));
                    List<ResPlan> planList = resPlanStore.getList(selectorList);
                    if (planList != null && !planList.isEmpty()) {
                        for (ResPlan resPlan : planList) {
                            JSONObject planObj = JsonUtils.formIdEntity(resPlan, 0);
                            if (planObj != null) {
                                GameUtils.minish(planObj);
                                planArray.add(planObj);
                            }
                        }
                    }

                    selectorList.clear();
                    selectorList.add(SelectorUtils.$eq("gender", appUser.getGender()));
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    List<ResCouple> coupleList = resCoupleStore.getList(selectorList);
                    if (coupleList != null && !coupleList.isEmpty()) {
                        for (ResCouple resCouple : coupleList) {
                            JSONObject coupleObj = JsonUtils.formIdEntity(resCouple, 0);
                            if (coupleObj != null) {
                                GameUtils.minish(coupleObj);
                                coupleArray.add(coupleObj);
                            }
                        }
                    }

                    selectorList.clear();
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    selectorList.add(SelectorUtils.$order("investPrice", true));
                    List<ResLuck> luckList = resLuckStore.getList(selectorList);
                    if (luckList != null && !luckList.isEmpty()) {
                        for (ResLuck resLuck : luckList) {
                            JSONObject luckObj = JsonUtils.formIdEntity(resLuck, 0);
                            if (luckObj != null) {
                                GameUtils.minish(luckObj);
                                luckArray.add(luckObj);
                            }
                        }
                    }

                    selectorList.clear();
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    selectorList.add(SelectorUtils.$order("probability", false));
                    List<ResFund> fundList = resFundStore.getList(selectorList);
                    if (fundList != null && !fundList.isEmpty()) {
                        for (ResFund resFund : fundList) {
                            JSONObject fundObj = JsonUtils.formIdEntity(resFund, 0);
                            if (fundObj != null) {
                                GameUtils.minish(fundObj);
                                fundArray.add(fundObj);
                            }
                        }
                    }

                    selectorList.clear();
                    selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                    List<ResTip> tipList = resTipStore.getList(selectorList);
                    if (tipList != null && !tipList.isEmpty()) {
                        for (ResTip resTip : tipList) {
                            JSONObject tipObj = JsonUtils.formIdEntity(resTip, 0);
                            if (tipObj != null) {
                                GameUtils.minish(tipObj);
                                tipArray.add(tipObj);
                            }
                        }
                    }

                    if (appUser.getGender() == 1) {
                        selectorList.clear();
                        selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                        selectorList.add(SelectorUtils.$order("buyPrice", true));
                        List<ResCar> carList = resCarStore.getList(selectorList);
                        if (carList != null && !carList.isEmpty()) {
                            for (ResCar resCar : carList) {
                                JSONObject carObj = JsonUtils.formIdEntity(resCar, 0);
                                if (carObj != null) {
                                    GameUtils.minish(carObj);
                                    carArray.add(carObj);
                                }
                            }
                        }

                        selectorList.clear();
                        selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                        selectorList.add(SelectorUtils.$order("buyPrice", true));
                        List<ResHouse> houseList = resHouseStore.getList(selectorList);
                        if (houseList != null && !houseList.isEmpty()) {
                            for (ResHouse resHouse : houseList) {
                                JSONObject houseObj = JsonUtils.formIdEntity(resHouse, 0);
                                if (houseObj != null) {
                                    GameUtils.minish(houseObj);
                                    houseArray.add(houseObj);
                                }
                            }
                        }

                    } else if (appUser.getGender() == 2) {
                        selectorList.clear();
                        selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                        selectorList.add(SelectorUtils.$order("buyPrice", true));
                        List<ResClothes> clothesList = resClothesStore.getList(selectorList);
                        if (clothesList != null && !clothesList.isEmpty()) {
                            for (ResClothes resClothes : clothesList) {
                                JSONObject clothesObj = JsonUtils.formIdEntity(resClothes, 0);
                                if (clothesObj != null) {
                                    GameUtils.minish(clothesObj);
                                    clothesArray.add(clothesObj);
                                }
                            }
                        }

                        selectorList.clear();
                        selectorList.add(SelectorUtils.$eq("useYn", "Y"));
                        selectorList.add(SelectorUtils.$order("buyPrice", true));
                        List<ResLuxury> luxuryList = resLuxuryStore.getList(selectorList);
                        if (luxuryList != null && !luxuryList.isEmpty()) {
                            for (ResLuxury resLuxury : luxuryList) {
                                JSONObject luxuryObj = JsonUtils.formIdEntity(resLuxury, 0);
                                if (luxuryObj != null) {
                                    GameUtils.minish(luxuryObj);
                                    luxuryArray.add(luxuryObj);
                                }
                            }
                        }
                    }
                    resultObj.put("tipArray", tipArray);
                    resultObj.put("jobArray", jobArray);
                    resultObj.put("planArray", planArray);
                    resultObj.put("coupleArray", coupleArray);
                    resultObj.put("carArray", carArray);
                    resultObj.put("houseArray", houseArray);
                    resultObj.put("clothesArray", clothesArray);
                    resultObj.put("luxuryArray", luxuryArray);
                    resultObj.put("luckArray", luckArray);
                    resultObj.put("fundArray", fundArray);
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
    public String nextDay(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserJobStore appUserJobStore = hsfServiceFactory.consumer(AppUserJobStore.class);
            ResJobEffectStore resJobEffectStore = hsfServiceFactory.consumer(ResJobEffectStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            ResCarEffectStore resCarEffectStore = hsfServiceFactory.consumer(ResCarEffectStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            ResHouseEffectStore resHouseEffectStore = hsfServiceFactory.consumer(ResHouseEffectStore.class);
            AppUserClothesStore appUserClothesStore = hsfServiceFactory.consumer(AppUserClothesStore.class);
            ResClothesEffectStore resClothesEffectStore = hsfServiceFactory.consumer(ResClothesEffectStore.class);
            AppUserLuxuryStore appUserLuxuryStore = hsfServiceFactory.consumer(AppUserLuxuryStore.class);
            ResLuxuryEffectStore resLuxuryEffectStore = hsfServiceFactory.consumer(ResLuxuryEffectStore.class);
            ResFundStore resFundStore = hsfServiceFactory.consumer(ResFundStore.class);
            AppUserFundStore appUserFundStore = hsfServiceFactory.consumer(AppUserFundStore.class);
            AppUserFundMarketStore appUserFundMarketStore = hsfServiceFactory.consumer(AppUserFundMarketStore.class);
            if (appUserStore != null && appUserManStore != null && appUserLadyStore != null && appUserJobStore != null
                    && appUserCarStore != null && appUserHouseStore != null && appUserClothesStore != null && appUserLuxuryStore != null && resJobEffectStore != null
                    && resCarEffectStore != null && resHouseEffectStore != null && resClothesEffectStore != null && resLuxuryEffectStore != null
                    && resFundStore != null && appUserFundStore != null && appUserFundMarketStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    AppUserJob appUserJob = appUserJobStore.getByUserId(userId);

                    JSONArray resultArray = new JSONArray();
                    JSONArray effectArray = null;
                    AppUserMan appUserMan = null;
                    AppUserLady appUserLady = null;
                    Integer days = 0;
                    Integer hours = 0;
                    if (appUser.getGender() == 1) {
                        appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            days = appUserMan.getDays();
                            hours = appUserMan.getHours();
                        }
                    } else if (appUser.getGender() == 2) {
                        appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            days = appUserLady.getDays();
                            hours = appUserLady.getHours();
                        }
                    }
                    List<AppUserFundDetail> appUserFundDetails = new ArrayList<>();
                    List<AppUserFundMarket> appUserFundMarkets = new ArrayList<>();
                    List<AppUserFund> appUserFundList = appUserFundStore.getByUserId(userId);
                    if (appUserFundList != null && !appUserFundList.isEmpty()) {
                        int marketDay = days - 1;
                        for (AppUserFund appUserFund : appUserFundList) {
                            ResFund resFund = appUserFund.getFundId();
                            if (resFund != null) {
                                List<Double> doubleList = new ArrayList<>();
                                doubleList.add(resFund.getProbability());
                                doubleList.add(NumberUtils.subtract(1.00, resFund.getProbability()));

                                AppUserFundMarket appUserFundMarket = appUserFundMarketStore.getByUserFundId(userId, resFund.getId());
                                if (appUserFundMarket != null) {
                                    String market = appUserFundMarket.getMarket();
                                    if (StringUtils.isNotBlank(market)) {
                                        JSONArray marketArray = JSONArray.fromObject(market);
                                        int d = GameUtils.gameDays - marketDay;
                                        int sum = 7 + d;
                                        if (marketArray.size() < sum) {
                                            int diff = sum - marketArray.size();
                                            for (int i = 1; i <= diff; i++) {
                                                marketArray.add(String.valueOf(GameUtils.fundMarket(doubleList, resFund.getMinNum(), resFund.getMaxNum())));
                                            }
                                            appUserFundMarket.setMarket(marketArray.toString());
                                            appUserFundMarket.setUseYn("Y");
                                            bind(appUserFundMarket, userId);
                                        }

                                        Double newMarket = marketArray.getDouble(marketArray.size() - 1);
                                        Integer beforeMoney = appUserFund.getMoney();
                                        Integer afterMoney = 0;
                                        if (newMarket > 0) {
                                            afterMoney = Double.valueOf(NumberUtils.add(appUserFund.getMoney(), NumberUtils.multiply(appUserFund.getMoney(), NumberUtils.divide(newMarket, 100, 2), 0))).intValue();
                                        } else if (newMarket < 0) {
                                            Double fundMarket = NumberUtils.subtract(100, NumberUtils.subtract(0, newMarket, 2), 2);
                                            afterMoney = Double.valueOf(NumberUtils.multiply(appUserFund.getMoney(), NumberUtils.divide(fundMarket, 100, 2), 0)).intValue();
                                        }

                                        appUserFund.setMarket(newMarket);
                                        appUserFund.setMoney(afterMoney);
                                        bind(appUserFund, userId);
                                        AppUserFundDetail appUserFundDetail = new AppUserFundDetail();
                                        appUserFundDetail.setId(DrdsIDUtils.getID(DrdsTable.APP));
                                        appUserFundDetail.setUserFundId(appUserFund);
                                        appUserFundDetail.setBeforeMoney(beforeMoney);
                                        appUserFundDetail.setAfterMoney(afterMoney);
                                        appUserFundDetail.setDay(marketDay);
                                        appUserFundDetail.setMarket(newMarket);
                                        bind(appUserFundDetail, userId);
                                        appUserFundDetail.setUseYn("Y");

                                        appUserFundDetails.add(appUserFundDetail);
                                        appUserFundMarkets.add(appUserFundMarket);
                                    }
                                }
                            }
                        }
                    }
                    if (appUser.getGender() == 1) {
                        if (appUserMan != null) {
                            if (days > 0 && hours == 0) {
                                appUserMan.setHours(GameUtils.intHours);
                                appUserMan.setDays(days - 1);
                                String resultText = "第" + GameUtils.dayText(appUserMan.getDays()) + "天,";
                                List<AppUserCar> appUserCarList = appUserCarStore.getByUserId(userId);
                                List<AppUserHouse> appUserHouseList = appUserHouseStore.getByUserId(userId);
                                if (appUserJob == null && appUserCarList.isEmpty() && appUserHouseList.isEmpty()) {
                                    resultText += "你又没工作，又没车，又没房，哎，仅仅做了个梦！";
                                    GameUtils.addResultArray(resultArray, resultText, null);
                                } else {
                                    resultText += "因为你";
                                    AppUserMan oldMan = (AppUserMan) appUserMan.clone();
                                    if (appUserJob != null) {
                                        List<ResJobEffect> jobEffectList = resJobEffectStore.getListByJobId(appUserJob.getJobId().getId());
                                        if (jobEffectList != null && !jobEffectList.isEmpty()) {
                                            resultText += "有一份工作,";
                                            GameUtils.useEffect(jobEffectList, appUserMan);
                                        }
                                    }
                                    if (appUserCarList != null && !appUserCarList.isEmpty()) {
                                        resultText += "有座驾,";
                                        for (AppUserCar appUserCar : appUserCarList) {
                                            List<ResCarEffect> carEffectList = resCarEffectStore.getListByCarId(appUserCar.getCarId().getId());
                                            if (carEffectList != null && !carEffectList.isEmpty()) {
                                                GameUtils.useEffect(carEffectList, appUserMan);
                                            }
                                        }
                                    }
                                    if (appUserHouseList != null && !appUserHouseList.isEmpty()) {
                                        resultText += "有房产";
                                        for (AppUserHouse appUserHouse : appUserHouseList) {
                                            List<ResHouseEffect> houseEffectList = resHouseEffectStore.getListByHouseId(appUserHouse.getHouseId().getId());
                                            if (houseEffectList != null && !houseEffectList.isEmpty()) {
                                                GameUtils.useEffect(houseEffectList, appUserMan);
                                            }
                                        }
                                    }
                                    effectArray = GameUtils.diffEffectMan(oldMan, appUserMan);
                                    GameUtils.addResultArray(resultArray, resultText, null);
                                    GameUtils.addResultArray(resultArray, "最终", effectArray);
                                }
                                appUserManStore.nextDay(appUserMan, Persistent.UPDATE, appUserFundList, appUserFundDetails, appUserFundMarkets);
                                resultObj.put("result", 0);
                                resultObj.put("resultArray", resultArray);
                            }
                        }
                    } else if (appUser.getGender() == 2) {
                        if (appUserLady != null) {
                            if (days > 0 && hours == 0) {
                                appUserLady.setHours(GameUtils.intHours);
                                appUserLady.setDays(days - 1);
                                String resultText = "第" + GameUtils.dayText(appUserLady.getDays()) + "天,";
                                List<AppUserClothes> appUserClothesList = appUserClothesStore.getByUserId(userId);
                                List<AppUserLuxury> appUserLuxuryList = appUserLuxuryStore.getByUserId(userId);
                                if (appUserJob == null && appUserClothesList.isEmpty() && appUserLuxuryList.isEmpty()) {
                                    resultText += "你又没工作，又没衣服，又没饰品，哎，仅仅做了个梦！";
                                    GameUtils.addResultArray(resultArray, resultText, null);
                                } else {
                                    resultText += "因为你";
                                    AppUserLady oldLady = (AppUserLady) appUserLady.clone();
                                    if (appUserJob != null) {
                                        List<ResJobEffect> jobEffectList = resJobEffectStore.getListByJobId(appUserJob.getJobId().getId());
                                        if (jobEffectList != null && !jobEffectList.isEmpty()) {
                                            resultText += "有一份工作,";
                                            GameUtils.useEffect(jobEffectList, appUserLady);
                                        }
                                    }
                                    if (appUserClothesList != null && !appUserClothesList.isEmpty()) {
                                        resultText += "有衣服,";
                                        for (AppUserClothes appUserClothes : appUserClothesList) {
                                            List<ResClothesEffect> clothesEffectList = resClothesEffectStore.getListByClothesId(appUserClothes.getClothesId().getId());
                                            if (clothesEffectList != null && !clothesEffectList.isEmpty()) {
                                                GameUtils.useEffect(clothesEffectList, appUserLady);
                                            }
                                        }
                                    }
                                    if (appUserLuxuryList != null && !appUserLuxuryList.isEmpty()) {
                                        resultText += "有饰品,";
                                        for (AppUserLuxury appUserLuxury : appUserLuxuryList) {
                                            List<ResLuxuryEffect> luxuryEffectList = resLuxuryEffectStore.getListByLuxuryId(appUserLuxury.getLuxuryId().getId());
                                            if (luxuryEffectList != null && !luxuryEffectList.isEmpty()) {
                                                GameUtils.useEffect(luxuryEffectList, appUserLady);
                                            }
                                        }
                                    }

                                    effectArray = GameUtils.diffEffectLady(oldLady, appUserLady);
                                    GameUtils.addResultArray(resultArray, resultText, null);
                                    GameUtils.addResultArray(resultArray, "最终", effectArray);
                                }
                                appUserLadyStore.nextDay(appUserLady, Persistent.UPDATE, appUserFundList, appUserFundDetails, appUserFundMarkets);
                                resultObj.put("result", 0);
                                resultObj.put("resultArray", resultArray);
                            }
                        }
                    }

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
    public String done(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserJobStore appUserJobStore = hsfServiceFactory.consumer(AppUserJobStore.class);
            ResJobRequireStore resJobRequireStore = hsfServiceFactory.consumer(ResJobRequireStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            AppUserClothesStore appUserClothesStore = hsfServiceFactory.consumer(AppUserClothesStore.class);
            AppUserLuxuryStore appUserLuxuryStore = hsfServiceFactory.consumer(AppUserLuxuryStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            ResCoupleRequireStore resCoupleRequireStore = hsfServiceFactory.consumer(ResCoupleRequireStore.class);
            AppUserFundStore appUserFundStore = hsfServiceFactory.consumer(AppUserFundStore.class);
            AppUserRankingsStore appUserRankingsStore = hsfServiceFactory.consumer(AppUserRankingsStore.class);
            ResCommentStore resCommentStore = hsfServiceFactory.consumer(ResCommentStore.class);
            if (appUserStore != null && appUserManStore != null && appUserLadyStore != null && appUserJobStore != null
                    && resJobRequireStore != null && appUserCarStore != null && appUserHouseStore != null && appUserClothesStore != null
                    && appUserLuxuryStore != null && appUserCoupleStore != null && resCoupleRequireStore != null && resCommentStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    AppUserMan appUserMan = null;
                    AppUserLady appUserLady = null;
                    Integer score = 0;
                    boolean flagRanking = false;
                    int day = 0;
                    int hour = 0;
                    if (appUser.getGender() == 1) {
                        appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            day = appUserMan.getDays();
                            hour = appUserMan.getHours();
                            if (day == 0 && hour == 0) {
                                score += GameUtils.getScoreAttr(appUserMan.getHealth());
                                score += GameUtils.getScoreAttr(appUserMan.getAbility());
                                score += GameUtils.getScoreAttr(appUserMan.getExperience());
                                score += GameUtils.getScoreAttr(appUserMan.getHappy());
                                score += GameUtils.getScoreAttr(appUserMan.getPositive());
                                score += GameUtils.getScoreAttr(appUserMan.getConnections());

                                Integer fundMoney = appUserFundStore.getSumByUserId(userId);
                                if (fundMoney == null||fundMoney<0) {
                                    fundMoney = 0;
                                }
                                score += GameUtils.getScoreMoney(appUserMan.getMoney());
                                score += GameUtils.getScoreMoney(fundMoney);

                                AppUserJob appUserJob = appUserJobStore.getByUserId(userId);
                                Integer maxJobLevel = 0;
                                Integer maxCarLevel = 0;
                                Integer maxHouseLevel = 0;
                                Integer maxCoupleLevel = 0;
                                if (appUserJob != null) {
                                    maxJobLevel = appUserJob.getJobId().getLevel();
                                    List<ResJobRequire> jobRequireList = resJobRequireStore.getListByJobId(appUserJob.getJobId().getId());
                                    if (jobRequireList != null && !jobRequireList.isEmpty()) {
                                        for (ResJobRequire resJobRequire : jobRequireList) {
                                            score += GameUtils.getScoreAttr(resJobRequire.getValue());
                                        }
                                    }
                                }
                                List<AppUserCar> appUserCarList = appUserCarStore.getByUserId(userId);
                                if (appUserCarList != null && !appUserCarList.isEmpty()) {
                                    for (AppUserCar appUserCar : appUserCarList) {
                                        if (appUserCar.getCarId().getLevel() > maxCarLevel) {
                                            maxCarLevel = appUserCar.getCarId().getLevel();
                                        }
                                        score += GameUtils.getScoreMoney(appUserCar.getCarId().getSellPrice());
                                    }
                                }

                                List<AppUserHouse> appUserHouseList = appUserHouseStore.getByUserId(userId);
                                if (appUserHouseList != null && !appUserHouseList.isEmpty()) {
                                    for (AppUserHouse appUserHouse : appUserHouseList) {
                                        if (appUserHouse.getHouseId().getLevel() > maxHouseLevel) {
                                            maxHouseLevel = appUserHouse.getHouseId().getLevel();
                                        }
                                        score += GameUtils.getScoreMoney(appUserHouse.getHouseId().getSellPrice());
                                    }
                                }
                                AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                                if (appUserCouple != null) {
                                    score += 200000;
                                    maxCoupleLevel = 1;
                                    List<ResCoupleRequire> coupleRequireList = resCoupleRequireStore.getListByCoupleId(appUserCouple.getCoupleId().getId());
                                    if (coupleRequireList != null && !coupleRequireList.isEmpty()) {
                                        for (ResCoupleRequire coupleRequire : coupleRequireList) {
                                            score += GameUtils.getScoreAttr(coupleRequire.getValue());
                                        }
                                    }
                                }
                                List<ResComment> resCommentList = resCommentStore.getList(appUser.getGender());
                                String comment = "qiong";
                                if (resCommentList != null && !resCommentList.isEmpty()) {
                                    ScriptEngineManager manager = new ScriptEngineManager();
                                    ScriptEngine engine = manager.getEngineByName("js");
                                    engine.put("jobLevel",maxJobLevel);
                                    engine.put("carLevel",maxCarLevel);
                                    engine.put("houseLevel",maxHouseLevel);
                                    engine.put("coupleLevel",maxCoupleLevel);
                                    engine.put("money",appUserMan.getMoney());
                                    engine.put("fundMoney",fundMoney);
                                    engine.put("health",appUserMan.getHealth());
                                    for (ResComment commentMatch : resCommentList) {
                                        Object result = engine.eval(commentMatch.getLogicMatch());
                                        if (result.toString().equals("true")) {
                                            comment = commentMatch.getComment();
                                            break;
                                        }
                                    }
                                }
                                appUserMan.setScore(score);
                                appUserMan.setComment(comment);
                                bind(appUserMan, userId);
                                appUserManStore.save(appUserMan, Persistent.UPDATE);
                            }
                        }
                    } else if (appUser.getGender() == 2) {
                        appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            day = appUserLady.getDays();
                            hour = appUserLady.getHours();
                            if (day == 0 && hour == 0) {
                                score += GameUtils.getScoreAttr(appUserLady.getHealth());
                                score += GameUtils.getScoreAttr(appUserLady.getAbility());
                                score += GameUtils.getScoreAttr(appUserLady.getWisdom());
                                score += GameUtils.getScoreAttr(appUserLady.getHappy());
                                score += GameUtils.getScoreAttr(appUserLady.getBeauty());
                                score += GameUtils.getScoreAttr(appUserLady.getPopularity());

                                Integer fundMoney = appUserFundStore.getSumByUserId(userId);
                                if (fundMoney == null) {
                                    fundMoney = 0;
                                }
                                score += GameUtils.getScoreMoney(appUserLady.getMoney());
                                score += GameUtils.getScoreMoney(fundMoney);

                                AppUserJob appUserJob = appUserJobStore.getByUserId(userId);
                                if (appUserJob != null) {
                                    List<ResJobRequire> jobRequireList = resJobRequireStore.getListByJobId(appUserJob.getJobId().getId());
                                    if (jobRequireList != null && !jobRequireList.isEmpty()) {
                                        for (ResJobRequire resJobRequire : jobRequireList) {
                                            score += GameUtils.getScoreAttr(resJobRequire.getValue());
                                        }
                                    }
                                }
                                List<AppUserClothes> appUserClothesList = appUserClothesStore.getByUserId(userId);
                                if (appUserClothesList != null && !appUserClothesList.isEmpty()) {
                                    for (AppUserClothes appUserClothes : appUserClothesList) {
                                        score += GameUtils.getScoreMoney(appUserClothes.getClothesId().getSellPrice());
                                    }
                                }

                                List<AppUserLuxury> appUserLuxuryList = appUserLuxuryStore.getByUserId(userId);
                                if (appUserLuxuryList != null && !appUserLuxuryList.isEmpty()) {
                                    for (AppUserLuxury appUserLuxury : appUserLuxuryList) {
                                        score += GameUtils.getScoreMoney(appUserLuxury.getLuxuryId().getSellPrice());
                                    }
                                }
                                AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                                if (appUserCouple != null) {
                                    List<ResCoupleRequire> coupleRequireList = resCoupleRequireStore.getListByCoupleId(appUserCouple.getCoupleId().getId());
                                    if (coupleRequireList != null && !coupleRequireList.isEmpty()) {
                                        for (ResCoupleRequire coupleRequire : coupleRequireList) {
                                            if (coupleRequire.getAttrKey().toUpperCase().equals("MONEY")) {
                                                score += 250000;
                                            } else {
                                                score += GameUtils.getScoreAttr(coupleRequire.getValue());
                                            }
                                        }
                                    }
                                }
                                appUserLady.setScore(score);
                                bind(appUserLady, userId);
                                appUserLadyStore.save(appUserLady, Persistent.UPDATE);
                            }
                        }
                    }

                    Integer count = appUserRankingsStore.getCount();

                    if (count >= 999) {
                        Integer minScore = appUserRankingsStore.getMin();
                        if (minScore < score) {
                            List<Selector> selectorList = new ArrayList<>();
                            selectorList.add(SelectorUtils.$order("score"));
                            Page<AppUserRankings> appUserRankingsPage = appUserRankingsStore.getPageList(0, 1, selectorList);
                            if (appUserRankingsPage != null) {
                                List<AppUserRankings> appUserRankings = appUserRankingsPage.getResultList();
                                if (appUserRankings != null && !appUserRankings.isEmpty()) {
                                    appUserRankingsStore.delete(appUserRankings);
                                }
                            }
                            flagRanking = true;
                        }
                    } else {
                        flagRanking = true;
                    }
                    if (flagRanking) {
                        AppUserRankings appUserRankings = new AppUserRankings();
                        appUserRankings.setId(DrdsIDUtils.getID(DrdsTable.APP));
                        appUserRankings.setScore(score);
                        appUserRankings.setUserId(appUser);
                        appUserRankings.setUseYn("Y");
                        bind(appUserRankings, userId);
                        appUserRankingsStore.save(appUserRankings, Persistent.SAVE);
                    }
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
    public String rankings(Long userId, Integer start, Integer limit) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserRankingsStore appUserRankingsStore = hsfServiceFactory.consumer(AppUserRankingsStore.class);
            if (appUserStore != null && appUserRankingsStore != null && appUserManStore != null && appUserLadyStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    List<Selector> selectorList = new ArrayList<>();
                    selectorList.add(SelectorUtils.$alias("userId", "userId"));
                    selectorList.add(SelectorUtils.$order("score", false));
                    Page<AppUserRankings> appUserRankingsPage = appUserRankingsStore.getPageList(start, limit, selectorList);
                    JSONArray rankingsArray = new JSONArray();
                    String mySeq = limit + "+";
                    if (appUserRankingsPage != null) {
                        List<AppUserRankings> appUserRankingsList = appUserRankingsPage.getResultList();
                        if (appUserRankingsList != null && !appUserRankingsList.isEmpty()) {
                            int seqIndex = 1;
                            for (AppUserRankings appUserRankings : appUserRankingsList) {
                                appUserRankings.setSeq(seqIndex);
                                AppUser rankingsUser = appUserRankings.getUserId();
                                if (rankingsUser != null) {
                                    if (rankingsUser.getId().equals(userId)) {
                                        mySeq = seqIndex + "";
                                    }
                                    JSONObject userObj = JsonUtils.formIdEntity(rankingsUser, 0);
                                    if (userObj != null) {
                                        // userObj.put("comment", GameUtils.getScoreComment(appUserRankings.getScore(), true));
                                        userObj.put("score", appUserRankings.getScore());
                                        userObj.put("seq", appUserRankings.getSeq());
                                        GameUtils.minish(userObj);
                                        rankingsArray.add(userObj);
                                    }
                                }
                                seqIndex++;

                            }
                        }
                    }
                    JSONObject userObj = JsonUtils.formIdEntity(appUser, 0);
                    if (userObj != null) {
                        GameUtils.minish(userObj);
                        AppUserRankings appUserRankings = appUserRankingsStore.getByUserId(userId);
                        if (appUserRankings != null) {
                            userObj.put("score", appUserRankings.getScore());
                            // userObj.put("comment", GameUtils.getScoreComment(appUserRankings.getScore(), true));
                            userObj.put("seq", mySeq);
                        } else {
                            userObj.put("seq", "999+");
                            AppUserMan appUserMan = null;
                            AppUserLady appUserLady = null;
                            if (appUser.getGender() == 1) {
                                appUserMan = appUserManStore.getByUserId(userId);
                                if (appUserMan != null) {
                                    userObj.put("score", appUserMan.getScore());
                                }
                            } else if (appUser.getGender() == 2) {
                                appUserLady = appUserLadyStore.getByUserId(userId);
                                if (appUserLady != null) {
                                    userObj.put("score", appUserLady.getScore());
                                }
                            }
                        }
                        resultObj.put("list", rankingsArray);
                        resultObj.put("myData", userObj);
                        resultObj.put("result", 0);
                    }
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
    public String report(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserJobStore appUserJobStore = hsfServiceFactory.consumer(AppUserJobStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            AppUserClothesStore appUserClothesStore = hsfServiceFactory.consumer(AppUserClothesStore.class);
            AppUserLuxuryStore appUserLuxuryStore = hsfServiceFactory.consumer(AppUserLuxuryStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            AppUserFundStore appUserFundStore = hsfServiceFactory.consumer(AppUserFundStore.class);
            if (appUserStore != null && appUserManStore != null && appUserLadyStore != null && appUserJobStore != null
                    && appUserCarStore != null && appUserHouseStore != null && appUserClothesStore != null
                    && appUserLuxuryStore != null && appUserCoupleStore != null) {
                AppUser appUser = appUserStore.getById(userId);
                if (appUser != null) {
                    AppUserMan appUserMan = null;
                    AppUserLady appUserLady = null;
                    JSONObject infoObj = null;
                    Integer fundMoney = appUserFundStore.getSumByUserId(userId);
                    if (fundMoney == null||fundMoney<0) {
                        fundMoney = 0;
                    }
                    AppUserJob appUserJob = appUserJobStore.getByUserId(userId);
                    AppUserCouple appUserCouple = appUserCoupleStore.getByUserId(userId);
                    if (appUser.getGender() == 1) {
                        appUserMan = appUserManStore.getByUserId(userId);
                        if (appUserMan != null) {
                            infoObj = JsonUtils.formIdEntity(appUserMan, 0);
                            if (infoObj != null) {
                                GameUtils.minish(infoObj);
                                infoObj.put("fundMoney", fundMoney.longValue());

                            }


                            if (appUserJob != null) {
                                infoObj.put("job", appUserJob.getJobId().getTitle());
                            }
                            List<AppUserCar> appUserCarList = appUserCarStore.getByUserId(userId);
                            if (appUserCarList != null && !appUserCarList.isEmpty()) {
                                for (AppUserCar appUserCar : appUserCarList) {
                                    if (appUserCarList.size() > 1) {
                                        infoObj.put("car", appUserCar.getCarId().getTitle() + "...");
                                        break;
                                    } else {
                                        infoObj.put("car", appUserCar.getCarId().getTitle());
                                    }
                                }
                            }

                            List<AppUserHouse> appUserHouseList = appUserHouseStore.getByUserId(userId);
                            if (appUserHouseList != null && !appUserHouseList.isEmpty()) {
                                for (AppUserHouse appUserHouse : appUserHouseList) {
                                    if (appUserHouseList.size() > 1) {
                                        infoObj.put("house", appUserHouse.getHouseId().getTitle() + "...");
                                        break;
                                    } else {
                                        infoObj.put("house", appUserHouse.getHouseId().getTitle());
                                    }
                                }
                            }

                            if (appUserCouple != null) {
                                infoObj.put("couple", appUserCouple.getCoupleId().getTitle());
                            }
                        }
                    } else if (appUser.getGender() == 2) {
                        appUserLady = appUserLadyStore.getByUserId(userId);
                        if (appUserLady != null) {
                            infoObj = JsonUtils.formIdEntity(appUserMan, 0);
                            if (infoObj != null) {
                                GameUtils.minish(infoObj);
                                infoObj.put("fundMoney", fundMoney.longValue());

                            }


                            if (appUserJob != null) {
                                infoObj.put("job", appUserJob.getJobId().getTitle());
                            }

                            List<AppUserClothes> appUserClothesList = appUserClothesStore.getByUserId(userId);
                            if (appUserClothesList != null && !appUserClothesList.isEmpty()) {
                                for (AppUserClothes appUserClothes : appUserClothesList) {
                                    if (appUserClothesList.size() > 1) {
                                        infoObj.put("clothes", appUserClothes.getClothesId().getTitle() + "...");
                                        break;
                                    } else {
                                        infoObj.put("clothes", appUserClothes.getClothesId().getTitle());
                                    }
                                }
                            }

                            List<AppUserLuxury> appUserLuxuryList = appUserLuxuryStore.getByUserId(userId);
                            if (appUserLuxuryList != null && !appUserLuxuryList.isEmpty()) {
                                for (AppUserLuxury appUserLuxury : appUserLuxuryList) {
                                    if (appUserLuxuryList.size() > 1) {
                                        infoObj.put("luxury", appUserLuxury.getLuxuryId().getTitle() + "...");
                                        break;
                                    } else {
                                        infoObj.put("luxury", appUserLuxury.getLuxuryId().getTitle());
                                    }
                                }
                            }

                            if (appUserCouple != null) {
                                infoObj.put("couple", appUserCouple.getCoupleId().getTitle());
                            }

                        }
                    }
                    JSONArray textArray=new JSONArray();
                    textArray.add("你手头有现金"+infoObj.getInt("money")+"，富得走路都在掉钱。");
                    textArray.add("你深知你不理财，财不理你，投资财富为"+infoObj.getInt("fundMoney")+"。");
                    textArray.add("你有一份不错得工作"+infoObj.getString("job")+"，生活充实，有了最基本得保障。");
                    textArray.add("你名下有车"+infoObj.getString("car")+"，再也不用在下雪或下雨路边等车了。");
                    textArray.add("你名下有房"+infoObj.getString("house")+"，不用隔三岔五得搬家租房了。");
                    textArray.add("你的能力才干已能笑看人生，金主都不再雇你，而是用钱和你谈合作。");
                    textArray.add("你的社会经验只能用'老司机'来形容，坑蒙拐骗的地痞都得绕着你走。");
                    textArray.add("生活充满希望，你每天都洋溢着微笑，没有什么困难能阻挠你。");
                    textArray.add("你遇事能躲就躲，是个对社会正能量毫无贡献的人。");
                    infoObj.put("text",textArray);
                    resultObj.put("data", infoObj);
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
    public String replay(Long userId) throws BizException {
        JSONObject resultObj = new JSONObject();
        resultObj.put("result", -1);
        try {
            AppUserStore appUserStore = hsfServiceFactory.consumer(AppUserStore.class);
            AppUserManStore appUserManStore = hsfServiceFactory.consumer(AppUserManStore.class);
            AppUserLadyStore appUserLadyStore = hsfServiceFactory.consumer(AppUserLadyStore.class);
            AppUserLimitStore appUserLimitStore = hsfServiceFactory.consumer(AppUserLimitStore.class);
            AppUserJobStore appUserJobStore = hsfServiceFactory.consumer(AppUserJobStore.class);
            AppUserCarStore appUserCarStore = hsfServiceFactory.consumer(AppUserCarStore.class);
            AppUserHouseStore appUserHouseStore = hsfServiceFactory.consumer(AppUserHouseStore.class);
            AppUserClothesStore appUserClothesStore = hsfServiceFactory.consumer(AppUserClothesStore.class);
            AppUserLuxuryStore appUserLuxuryStore = hsfServiceFactory.consumer(AppUserLuxuryStore.class);
            AppUserCoupleStore appUserCoupleStore = hsfServiceFactory.consumer(AppUserCoupleStore.class);
            AppUserFundStore appUserFundStore = hsfServiceFactory.consumer(AppUserFundStore.class);
            AppUserFundMarketStore appUserFundMarketStore = hsfServiceFactory.consumer(AppUserFundMarketStore.class);
            AppUserFundDetailStore appUserFundDetailStore = hsfServiceFactory.consumer(AppUserFundDetailStore.class);
            AppUserLuckStore appUserLuckStore = hsfServiceFactory.consumer(AppUserLuckStore.class);
            AppUserPlanStore appUserPlanStore = hsfServiceFactory.consumer(AppUserPlanStore.class);
            if (appUserStore != null) {
                AppUser appUser = null;
                if (userId != null) {
                    appUser = appUserStore.getById(userId);
                    if (appUser != null) {
                        if (appUser.getGender() == 1) {
                            AppUserMan appUserMan = appUserManStore.getByUserId(userId);
                            if (appUserMan != null) {
                                List<AppUserLimit> userLimitList = appUserLimitStore.getListByUserId(userId);
                                AppUserJob userJob = appUserJobStore.getByUserId(userId);
                                List<AppUserCar> userCarList = appUserCarStore.getByUserId(userId);
                                List<AppUserHouse> userHouseList = appUserHouseStore.getByUserId(userId);
                                AppUserCouple userCouple = appUserCoupleStore.getByUserId(userId);
                                List<AppUserFund> userFundList = appUserFundStore.getByUserId(userId);
                                List<AppUserFundMarket> userFundMarketList = appUserFundMarketStore.getByUserId(userId);
                                List<AppUserFundDetail> userFundDetailList = appUserFundDetailStore.getByUserId(userId);
                                List<AppUserLuck> userLuckList = appUserLuckStore.getByUserId(userId);
                                List<AppUserPlan> userPlanList = appUserPlanStore.getByUserId(userId);
                                appUserManStore.delete(appUserMan, userLimitList, userJob, userCarList
                                        , userHouseList, userCouple, userFundList, userFundMarketList, userFundDetailList, userLuckList, userPlanList);
                                resultObj.put("result", 0);
                            }
                        }
                    }
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
}
