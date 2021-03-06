package com.diary.apis.app;

import com.diary.common.BaseAPI;
import com.diary.providers.biz.app.*;
import net.sf.json.JSONObject;
import org.guiceside.commons.lang.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;

/**
 * Created by gbcp on 16/8/8.
 */
@Path("/user")
public class UserAPI extends BaseAPI {


    @Path("/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response login(@FormParam("code") String code,
                          @FormParam("userId") Long userId,
                          @FormParam("nickName") String nickName, @FormParam("avatarUrl") String avatarUrl,
                          @FormParam("gender") Integer gender,
                          @FormParam("city") String city, @FormParam("province") String province,
                          @FormParam("country") String country) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (StringUtils.isBlank(code)) {
            errorBuilder.append("code was null.");
        }


        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.login(code, userId, nickName, avatarUrl, gender, city, province, country);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/start")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response start(@FormParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.start(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/resData/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response resData(@PathParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.resData(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/refresh/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response refresh(@PathParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.refresh(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/applyJob")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response applyJob(@FormParam("userId") Long userId,
                             @FormParam("jobId") Long jobId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (jobId == null) {
            errorBuilder.append("jobId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserJobBiz userJobBiz = hsfServiceFactory.consumer(UserJobBiz.class);
                if (userJobBiz != null) {
                    bizResult = userJobBiz.applyJob(userId, jobId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/applyPlan")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response applyPlan(@FormParam("userId") Long userId,
                              @FormParam("planId") Long planId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (planId == null) {
            errorBuilder.append("planId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserPlanBiz userPlanBiz = hsfServiceFactory.consumer(UserPlanBiz.class);
                if (userPlanBiz != null) {
                    bizResult = userPlanBiz.applyPlan(userId, planId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/done")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response done(@FormParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.done(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/nextDay")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response nextDay(@FormParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.nextDay(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/buyCar")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response buyCar(@FormParam("userId") Long userId,
                           @FormParam("carId") Long carId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (carId == null) {
            errorBuilder.append("carId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserCarBiz userCarBiz = hsfServiceFactory.consumer(UserCarBiz.class);
                if (userCarBiz != null) {
                    bizResult = userCarBiz.buyCar(userId, carId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/sellCar")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response sellCar(@FormParam("userId") Long userId,
                            @FormParam("carId") Long carId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (carId == null) {
            errorBuilder.append("carId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserCarBiz userCarBiz = hsfServiceFactory.consumer(UserCarBiz.class);
                if (userCarBiz != null) {
                    bizResult = userCarBiz.sellCar(userId, carId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/buyHouse")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response buyHouse(@FormParam("userId") Long userId,
                             @FormParam("houseId") Long houseId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (houseId == null) {
            errorBuilder.append("houseId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserHouseBiz userHouseBiz = hsfServiceFactory.consumer(UserHouseBiz.class);
                if (userHouseBiz != null) {
                    bizResult = userHouseBiz.buyHouse(userId, houseId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/sellHouse")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response sellHouse(@FormParam("userId") Long userId,
                              @FormParam("houseId") Long houseId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (houseId == null) {
            errorBuilder.append("houseId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserHouseBiz userHouseBiz = hsfServiceFactory.consumer(UserHouseBiz.class);
                if (userHouseBiz != null) {
                    bizResult = userHouseBiz.sellHouse(userId, houseId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/buyClothes")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response buyClothes(@FormParam("userId") Long userId,
                               @FormParam("clothesId") Long clothesId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (clothesId == null) {
            errorBuilder.append("clothesId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserClothesBiz userClothesBiz = hsfServiceFactory.consumer(UserClothesBiz.class);
                if (userClothesBiz != null) {
                    bizResult = userClothesBiz.buyClothes(userId, clothesId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/sellClothes")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response sellClothes(@FormParam("userId") Long userId,
                                @FormParam("clothesId") Long clothesId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (clothesId == null) {
            errorBuilder.append("clothesId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserClothesBiz userClothesBiz = hsfServiceFactory.consumer(UserClothesBiz.class);
                if (userClothesBiz != null) {
                    bizResult = userClothesBiz.sellClothes(userId, clothesId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/buyLuxury")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response buyLuxury(@FormParam("userId") Long userId,
                              @FormParam("luxuryId") Long luxuryId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (luxuryId == null) {
            errorBuilder.append("luxuryId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserLuxuryBiz userLuxuryBiz = hsfServiceFactory.consumer(UserLuxuryBiz.class);
                if (userLuxuryBiz != null) {
                    bizResult = userLuxuryBiz.buyLuxury(userId, luxuryId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/sellLuxury")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response sellLuxury(@FormParam("userId") Long userId,
                               @FormParam("luxuryId") Long luxuryId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (luxuryId == null) {
            errorBuilder.append("luxuryId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserLuxuryBiz userLuxuryBiz = hsfServiceFactory.consumer(UserLuxuryBiz.class);
                if (userLuxuryBiz != null) {
                    bizResult = userLuxuryBiz.sellLuxury(userId, luxuryId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/applyLuck")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response applyLuck(@FormParam("userId") Long userId,
                              @FormParam("luckId") Long luckId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (luckId == null) {
            errorBuilder.append("luckId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserLuckBiz userLuckBiz = hsfServiceFactory.consumer(UserLuckBiz.class);
                if (userLuckBiz != null) {
                    bizResult = userLuckBiz.applyLuck(userId, luckId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/relationship")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response relationship(@FormParam("userId") Long userId,
                                 @FormParam("coupleId") Long coupleId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (coupleId == null) {
            errorBuilder.append("coupleId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserCoupleBiz userCoupleBiz = hsfServiceFactory.consumer(UserCoupleBiz.class);
                if (userCoupleBiz != null) {
                    bizResult = userCoupleBiz.relationship(userId, coupleId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/breakUp")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response breakUp(@FormParam("userId") Long userId,
                            @FormParam("coupleId") Long coupleId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (coupleId == null) {
            errorBuilder.append("coupleId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserCoupleBiz userCoupleBiz = hsfServiceFactory.consumer(UserCoupleBiz.class);
                if (userCoupleBiz != null) {
                    bizResult = userCoupleBiz.breakUp(userId, coupleId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/plan/findEvent")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response findEventPlan(@QueryParam("userId") Long userId,
                                  @QueryParam("findEventId") Long findEventId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (findEventId == null) {
            errorBuilder.append("findEventId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserPlanBiz userPlanBiz = hsfServiceFactory.consumer(UserPlanBiz.class);
                if (userPlanBiz != null) {
                    bizResult = userPlanBiz.findEvent(userId, findEventId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/fund/market")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response market(@QueryParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserFundBiz userFundBiz = hsfServiceFactory.consumer(UserFundBiz.class);
                if (userFundBiz != null) {
                    bizResult = userFundBiz.market(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/fund/trade")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response trade(@QueryParam("userId") Long userId,
                          @QueryParam("fundId") Long fundId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (fundId == null) {
            errorBuilder.append("fundId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserFundBiz userFundBiz = hsfServiceFactory.consumer(UserFundBiz.class);
                if (userFundBiz != null) {
                    bizResult = userFundBiz.trade(userId, fundId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/fund/buy")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response buyFund(@FormParam("userId") Long userId,
                            @FormParam("fundId") Long fundId,
                            @FormParam("money") Integer money) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (fundId == null) {
            errorBuilder.append("fundId was null.");
        }
        if (money == null) {
            errorBuilder.append("money was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserFundBiz userFundBiz = hsfServiceFactory.consumer(UserFundBiz.class);
                if (userFundBiz != null) {
                    bizResult = userFundBiz.buyFund(userId, fundId, money);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/fund/sell")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response sellFund(@FormParam("userId") Long userId,
                             @FormParam("fundId") Long fundId,
                             @FormParam("money") Integer money) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (fundId == null) {
            errorBuilder.append("fundId was null.");
        }
        if (money == null) {
            errorBuilder.append("money was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserFundBiz userFundBiz = hsfServiceFactory.consumer(UserFundBiz.class);
                if (userFundBiz != null) {
                    bizResult = userFundBiz.sellFund(userId, fundId, money);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/myReport/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response myReport(@PathParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.myReport(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/report/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response report(@PathParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.report(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/coupleRankings")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response coupleRankings() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.coupleRankings();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/rankings/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response rankings(@PathParam("userId") Long userId,@QueryParam("orderType") String orderType, @QueryParam("gender") Integer gender) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if(orderType==null){
            orderType="score";
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.rankings(userId, 0, 100, orderType,gender);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/replay")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response replay(@FormParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.replay(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response info(@QueryParam("userId") Long userId, @QueryParam("code") String code) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (StringUtils.isBlank(code) && userId == null) {
            errorBuilder.append("code was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.info(code, userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/submit")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response submit(@FormParam("userId") Long userId, @FormParam("formId") String formId,
                           @FormParam("action") String action) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (StringUtils.isBlank(formId)) {
            errorBuilder.append("formId was null.");
        }
        if (StringUtils.isBlank(action)) {
            errorBuilder.append("action was null.");
        }

        if (errorBuilder.length() == 0) {
            try {
                UserFormBiz userFormBiz = hsfServiceFactory.consumer(UserFormBiz.class);
                if (userFormBiz != null) {
                    bizResult = userFormBiz.submit(userId, formId,action);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/QRCode/{userId}")
    @GET
    @Produces("image/png")
    public Response userQRCode(@PathParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    byte[] b= userBiz.QRCode(userId);
                    if (b!=null) {
                        return Response.ok(b).build();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/accessToken")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response accessToken(@FormParam("userId") Long userId) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (userId == null) {
            errorBuilder.append("userId was null.");
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.accessToken(userId);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }


    @Path("/help")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response help() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.help();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/share/{gender}")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response share(@PathParam("gender") Integer gender) {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();
        if (gender == null) {
            gender=1;
        }
        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.share(gender);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/refreshRankings")
    @GET
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response refreshRankings() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.refreshRankings();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/pushRankings")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response accessToken() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.pushRankings();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/pushNoGameMan")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response pushNoGameMan() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.pushNoGameMan();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }

    @Path("/pushNoGameLady")
    @POST
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Consumes("application/x-www-form-urlencoded")
    public Response pushNoGameLady() {
        JSONObject result = new JSONObject();
        String bizResult = null;
        StringBuilder errorBuilder = new StringBuilder();

        if (errorBuilder.length() == 0) {
            try {
                UserBiz userBiz = hsfServiceFactory.consumer(UserBiz.class);
                if (userBiz != null) {
                    bizResult = userBiz.pushNoGameLady();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        result = buildResult(result, errorBuilder, bizResult);
        return Response.ok().entity(result.toString()).build();
    }
}
