package com.yourong.common.mongo;

import java.util.*;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import com.yourong.common.mongo.logDto.OpenDownLoadDto;
import com.yourong.common.mongo.logDto.OpenServiceInputDto;
import com.yourong.common.mongo.query.OpenDownLoadQuery;
import com.yourong.common.mongo.query.OpenServiceInputQuery;
import com.yourong.common.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;

/**
 * Created by XR on 2016/10/31.
 */
public class OpenServiceLog {

	private static Logger logger = LoggerFactory.getLogger(OpenServiceLog.class);

	public static void openServicelog(String serviceName, String channelKey, String outBizNo, String inputJson) {
        try {
			String collectionname="openServiceInput"+ DateUtils.getStrFromDate(new Date(),"yyyyMMdd");
			MongoCollection collection = MongodbManager.getAuthDatabase().getCollection(collectionname);
			Document document = new Document();
			document.append("outBizNo", outBizNo);
			document.append("channelKey", channelKey);
			document.append("serviceName", serviceName);
			document.append("inputJson", inputJson);
            Date date=new Date();//取时间
			Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR,8);//时区关系加8小时
			document.append("createTime", calendar.getTime());
			collection.insertOne(document);
        } catch (Exception e) {
			logger.error("mongo记录日志出现异常。", e);
        }
    }

	public static List<OpenServiceInputDto> queryLog(OpenServiceInputQuery query){
		String collectionname="openServiceInput"+DateUtils.getStrFromDate(query.getCreateTime(),"yyyyMMdd");
		MongoCollection collection= MongodbManager.getAuthDatabase().getCollection(collectionname);
		List<OpenServiceInputDto> list=new ArrayList<>();
		BasicDBObject basicDBObject=new BasicDBObject();
		if (!StringUtils.isEmpty(query.getOutBizNo())){
			basicDBObject.put("outBizNo",query.getOutBizNo());
		}
		if (!StringUtils.isEmpty(query.getChannelKey())){
			basicDBObject.put("channelKey",query.getChannelKey());
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
			OpenServiceInputDto openServiceInputDto= new OpenServiceInputDto();
			openServiceInputDto.setOutBizNo((String)document.get("outBizNo"));
			openServiceInputDto.setChannelKey((String) document.get("channelKey"));
			openServiceInputDto.setServiceName((String) document.get("serviceName"));
			openServiceInputDto.setInputJson((String) document.get("inputJson"));
			calendar.setTime((Date) document.get("createTime"));
			calendar.add(Calendar.HOUR,-8);//时区关系加8小时
			openServiceInputDto.setCreateTime(calendar.getTime());
			list.add(openServiceInputDto);
		}
		return list;
	}

	public static int queryLogCount(OpenServiceInputQuery query){
		String collectionname="openServiceInput"+DateUtils.getStrFromDate(query.getCreateTime(),"yyyyMMdd");
		MongoCollection collection= MongodbManager.getAuthDatabase().getCollection(collectionname);
		int i=0;
		BasicDBObject basicDBObject=new BasicDBObject();
		if (!StringUtils.isEmpty(query.getOutBizNo())){
			basicDBObject.put("outBizNo",query.getOutBizNo());
		}
		if (!StringUtils.isEmpty(query.getChannelKey())){
			basicDBObject.put("channelKey",query.getChannelKey());
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
