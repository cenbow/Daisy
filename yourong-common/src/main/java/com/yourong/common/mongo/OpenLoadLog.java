package com.yourong.common.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.yourong.common.mongo.logDto.OpenDownLoadDto;
import com.yourong.common.mongo.query.OpenDownLoadQuery;
import com.yourong.common.util.DateUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XR on 2016/11/7.
 */
public class OpenLoadLog {
    private static Logger logger = LoggerFactory.getLogger(OpenLoadLog.class);

    public static void log(OpenDownLoadDto openDownLoadDto){
        try {
            String collectionname="openload"+DateUtils.getStrFromDate(new Date(),"yyyyMMdd");
            MongoCollection collection= MongodbManager.getAuthDatabase().getCollection(collectionname);
            Document document=new Document();
            document.append("openId",openDownLoadDto.getOpenId());
            document.append("loadUrl",openDownLoadDto.getLoadUrl());
            document.append("msg",openDownLoadDto.getMsg());
            Date date=new Date();//取时间
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
            document.append("createTime",calendar.getTime());
            collection.insertOne(document);
        } catch (Exception e) {
            logger.error("mongo记录日志出现异常。",e);
        }
    }

    public static List<OpenDownLoadDto> queryLog(OpenDownLoadQuery query){
        String collectionname="openload"+DateUtils.getStrFromDate(query.getCreateTime(),"yyyyMMdd");
        MongoCollection collection= MongodbManager.getAuthDatabase().getCollection(collectionname);
        List<OpenDownLoadDto> list=new ArrayList<>();
        BasicDBObject basicDBObject=new BasicDBObject();
        if (query.getOpenId()!=null&&query.getOpenId()>0){
            basicDBObject.put("openId",query.getOpenId());
        }
        BasicDBObject timebasesic=new BasicDBObject();
        Calendar calendar  =  new GregorianCalendar();
        if (query.getStartTime()!=null){
            calendar.setTime(query.getStartTime());
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
            timebasesic.append("$gte",calendar.getTime());
        }
        if (query.getEndTime()!=null){
            calendar.setTime(query.getEndTime());
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
            timebasesic.append("$lte",calendar.getTime());
        }
        if (timebasesic.size()>0){
            basicDBObject.put("createTime",timebasesic);
        }
        MongoCursor mongoCursor = collection.find(basicDBObject).sort(new BasicDBObject("createTime", -1)).skip(query.getStartRow()).limit(query.getPageSize()).iterator();
        while (mongoCursor.hasNext()){
            Document document=(Document) mongoCursor.next();
            OpenDownLoadDto openDownLoadDto= new OpenDownLoadDto();
            openDownLoadDto.setOpenId(Long.parseLong(document.get("openId").toString()));
            openDownLoadDto.setMsg(document.get("msg").toString());
            openDownLoadDto.setLoadUrl((String) document.get("loadUrl"));
            calendar.setTime((Date) document.get("createTime"));
            calendar.add(Calendar.HOUR,-8);//时区关系加8小时
            openDownLoadDto.setCreateTime(calendar.getTime());
            list.add(openDownLoadDto);
        }
        return list;
    }

    public static int queryLogCount(OpenDownLoadQuery query){
        String collectionname="openload"+DateUtils.getStrFromDate(query.getCreateTime(),"yyyyMMdd");
        MongoCollection collection= MongodbManager.getAuthDatabase().getCollection(collectionname);
        int i=0;
        BasicDBObject basicDBObject=new BasicDBObject();
        if (query.getOpenId()!=null&&query.getOpenId()>0){
            basicDBObject.put("openId",query.getOpenId());
        }
        BasicDBObject timebasesic=new BasicDBObject();
        Calendar calendar   =   new GregorianCalendar();
        if (query.getStartTime()!=null){
            calendar.setTime(query.getStartTime());
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
            timebasesic.append("$gte",calendar.getTime());
        }
        if (query.getEndTime()!=null){
            calendar.setTime(query.getEndTime());
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
            timebasesic.append("$lte",calendar.getTime());
        }
        if (timebasesic.size()>0){
            basicDBObject.put("createTime",timebasesic);
        }

        MongoCursor mongoCursor = collection.find(basicDBObject).sort(new BasicDBObject("createTime", -1)).iterator();
        while (mongoCursor.hasNext()){
            i++;
            mongoCursor.next();
        }
        return i;
    }
}
