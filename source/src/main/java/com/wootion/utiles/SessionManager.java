package com.wootion.utiles;

import com.wootion.config.shiro.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * session管理,
 * 约定:
 *  SessionBean : 所保存的用户所有的session内容,
 *  sessionEntity : 所保存的用户某个实体
 * 用户登录后会以Authorization为key,创建一个SessionBean放入sessionStorage中
 * 可以根据业务需要在sessionBean中增删改查sessionEntity
 *
 */
@Component
public class SessionManager implements Runnable{


    private final Logger logger = LoggerFactory.getLogger(getClass());


    public final static String LAST_USE_TIME = "lastUseTime";

    public final static String USER_BEAN = "userBean";
    public final static String ROBOT = "Robot";
    public final static String SITE = "site";
    private final static ConcurrentHashMap<String, Map<String,Object>> sessionStorage = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Map<String,Object>> getSessionStorage() {
        return sessionStorage;
    }

    /**
     * 通过Authorization获取该用户sessionBean
     * @param token
     * @return
     */
    public static Map<String,Object> getSessionBean(String token){
        if(token==null){
            return null;
        }
        Map<String,Object> sessionBean = sessionStorage.get(token);
        if (sessionBean != null) {
            sessionBean.put(LAST_USE_TIME,new Date());
        }
        return sessionBean;
    }

    public static Map<String,Object> getSessionBean(String token,boolean updateTime){
        if(token==null){
            return null;
        }
        Map<String,Object> sessionBean = sessionStorage.get(token);
        if (sessionBean != null && updateTime) {
            sessionBean.put(LAST_USE_TIME,new Date());
        }
        return sessionBean;
    }

    /**
     * 添加sessionBean,一般只有在用户登录的时候需要用到.
     * @param token
     * @param sessionBean
     * @return
     */
    private static boolean addSessionBean(String token, Map<String,Object> sessionBean) {
        sessionBean.put(LAST_USE_TIME,new Date());
        sessionStorage.put(token,sessionBean);
        return true;
    }

    public static Map<String,Object> removeSessionBean (String token ) {
        return sessionStorage.remove(token);
    }
    /**
     * 通过Authorization和session的key获取sessionEntity
     * @param token : Authorization
     * @param entityKey : sessionEntity 的 key
     * @return
     */
    public static Object getSessionEntity(String token, String entityKey) {
        Map<String,Object> sessionBean = getSessionBean(token,false);
        if (sessionBean != null) {
            return sessionBean.get(entityKey);
        } else {
            return null;
        }
    }

    /**
     * 添加或者更新Session实体
     * @param token Authorization
     * @param entityKey sessionEntity 的key
     * @param entity 变更结果
     * @return 更新后的结果
     */
    public static Object addOrUpdateSessionEntity(String token,String entityKey, Object entity) {
        if (sessionStorage.containsKey(token)) {
            Map<String,Object> sessionBean = sessionStorage.get(token);
            return sessionBean.put(entityKey,entity);
        } else {
            Map<String,Object> sessionBean = new Hashtable<>();
            sessionBean.put(entityKey,entity);
            addSessionBean(token,sessionBean);
            return entity;
        }
    }

    /**
     * 通过Authorization,和查询的entity的key删除session中的实体
     * @param token
     * @param entityKey
     * @return
     */
    public static Object removeSessionEntity(String token,String entityKey) {
        Map<String,Object> sessionBean = getSessionBean(token);
        if (sessionBean != null) {
            return sessionBean.remove(entityKey);
        }else {
            return null;
        }
    }


    public static boolean timeOut(Date date1 ,Date date2){
        if(date1==null || date2==null){
            return false;
        }
        long timeOut = DataCache.getSysParamInt("ros.sessionMaxAge");
        long idleTime = date2.getTime()/1000 - (date1).getTime()/1000;
        if(idleTime > timeOut){
            return true;
        }
        return false;
    }


    @Override
    public void run() {


        while (true) {
            Map<String,Map<String,Object>> data = getSessionStorage();
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                boolean flag=timeOut((Date)data.get(key).get(LAST_USE_TIME),new Date());
                if (flag) {
                    removeSessionBean(key);
                    logger.info("{} have been out of time", JWTUtil.getLoginname(key));
                }
            }
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
