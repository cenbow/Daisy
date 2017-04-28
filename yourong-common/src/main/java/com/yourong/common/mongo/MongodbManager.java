package com.yourong.common.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.yourong.common.util.PropertiesUtil;
import java.util.*;

/**
 * Created by XR on 2016/10/31.
 */
public class MongodbManager {
    private static MongoClient client=null;

    private static MongoDatabase mongoDatabase=null;

    private static MongoClient authClient=null;

    private static MongoDatabase authMongoDatabase=null;

    /**
     * 无认证
     * @return
     */
    public static MongoDatabase getDatabase(){
        if (mongoDatabase==null){
            if (client==null){
                client=new MongoClient(PropertiesUtil.getProperties("mongo_ip"),Integer.parseInt(PropertiesUtil.getProperties("mongo_port")));
            }
            mongoDatabase=client.getDatabase(PropertiesUtil.getProperties("mongo_database"));
        }
        return mongoDatabase;
    }

    /**
     * 认证
     * @return
     */
    public static MongoDatabase getAuthDatabase(){
        if (authMongoDatabase==null){
            if (authClient==null){
                MongoCredential credential = MongoCredential.createCredential(PropertiesUtil.getProperties("mongo_username"), PropertiesUtil.getProperties("mongo_database"), PropertiesUtil.getProperties("mongo_password").toCharArray());
                authClient=new MongoClient(new ServerAddress(PropertiesUtil.getProperties("mongo_ip"),Integer.parseInt(PropertiesUtil.getProperties("mongo_port"))), Arrays.asList(credential));
            }
            authMongoDatabase=authClient.getDatabase(PropertiesUtil.getProperties("mongo_database"));
        }
        return authMongoDatabase;
    }

}
