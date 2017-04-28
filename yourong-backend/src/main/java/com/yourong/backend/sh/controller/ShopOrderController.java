package com.yourong.backend.sh.controller;

import com.yourong.backend.BaseController;
import com.yourong.backend.sh.service.ShopOrderService;
import com.yourong.common.baidu.yun.utility.MessageDigestUtility;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.http.common.HttpRequest;
import com.yourong.common.http.common.HttpResponse;
import com.yourong.common.http.common.Method;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.PropertiesUtil;
import com.yourong.core.sh.model.biz.OrderBiz;
import com.yourong.core.sh.model.biz.OrderRechargeBiz;
import com.yourong.core.sh.model.query.ShopOrderQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XR on 2016/10/21.
 */
@Controller
@RequestMapping(value = "/shoporder")
public class ShopOrderController extends BaseController {
    @Autowired
    private ShopOrderService shopOrderService;
    @RequestMapping(value = "/index")
    public String index(){
        return "/sh/order/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    public Object ajax(HttpServletRequest req){
        String truename=req.getParameter("truename");
        String mobile=req.getParameter("mobile");
        String goodstype=req.getParameter("goodstype");
        String orderstatus=req.getParameter("orderstatus");
        String starttime= req.getParameter("starttime");
        String endtime= req.getParameter("endtime");
        String goodname= req.getParameter("goodname");
        String takemobile= req.getParameter("takemobile");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        ShopOrderQuery shopOrderQuery=new ShopOrderQuery();
        if (StringUtils.isEmpty(goodstype)||"0".equals(goodstype)){
            shopOrderQuery.setGoodsType(null);
        }else {
            shopOrderQuery.setGoodsType(Integer.parseInt(goodstype));
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringUtils.isEmpty(starttime)){
            try {
                Date st=sdf.parse(starttime);
                shopOrderQuery.setStartTime(st);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringUtils.isEmpty(endtime)){
            try {
                Date st=sdf.parse(endtime);
                shopOrderQuery.setEndTime(st);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isEmpty(orderstatus)||"0".equals(orderstatus)){
            shopOrderQuery.setStatus(null);
        }else {
            shopOrderQuery.setStatus(Integer.parseInt(orderstatus));
        }
        shopOrderQuery.setTrueName(truename);
        shopOrderQuery.setMobile(mobile);
        shopOrderQuery.setGoodName(goodname);
        shopOrderQuery.setTakeMobile(takemobile);
        Page<OrderBiz> page=new Page<>();
        shopOrderQuery.setPageSize(Integer.parseInt(pagesize));
        shopOrderQuery.setStartRow(Integer.parseInt(start));
        page=shopOrderService.queryPageOrderInfo(shopOrderQuery);
        return page;
    }

    /**
     * 添加发送信息
     * @param id
     * @param remark
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/sendremark")
    @RequiresPermissions("shoporder:sendRemark")
    public Object sendremark(@RequestParam(value = "orderid")Long id,@RequestParam(value = "remark")String remark){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            shopOrderService.send(id,remark);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (ManagerException e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/queryremark")
    @RequiresPermissions("shoporder:Remark")
    public Object queryremark(@RequestParam(value = "orderid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            String remark= shopOrderService.queryRemarkById(id);
            resultDO.setSuccess(true);
            resultDO.setResult(remark);
        } catch (Exception e) {
            resultDO.setSuccess(false);
        }
        return resultDO;
    }

    /**
     * 添加备注
     * @param id
     * @param remark
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remark")
    @RequiresPermissions("shoporder:Remark")
    public Object remark(@RequestParam(value = "orderid")Long id,@RequestParam(value = "remark")String remark){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            if (shopOrderService.updateRemarkById(id,remark)){
                resultDO.setSuccess(true);
                return resultDO;
            }
            resultDO.setSuccess(false);
        } catch (Exception e) {
            resultDO.setSuccess(false);
        }
        return resultDO;
    }

    /**
     * 更新发货信息
     * @param id
     * @param remark
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updatesendremark")
    @RequiresPermissions("shoporder:updateSendremark")
    public Object updateSendremark(@RequestParam(value = "orderid")Long id,@RequestParam(value = "remark")String remark){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            if (shopOrderService.updateSendRemarkById(id,remark)){
                resultDO.setSuccess(true);
                return resultDO;
            }
            resultDO.setSuccess(false);
        } catch (Exception e) {
            resultDO.setSuccess(false);
        }
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/recharge")
    @RequiresPermissions("shoporder:recharge")
    public Object recharge(@RequestParam(value = "orderid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        OrderRechargeBiz orderRechargeBiz= shopOrderService.queryOrderRechargeInfoByOrderId(id);
        if (orderRechargeBiz!=null&&orderRechargeBiz.getStatus()==1&&orderRechargeBiz.getRechargeType()==1){
            try {
                String url=PropertiesUtil.getProperties("duobei.url")+"common/buy.do";
                Map<String,String> map=new HashMap<>();

                String merchant=PropertiesUtil.getProperties("duobei.merchant");
                String sign=PropertiesUtil.getProperties("duobei.sign");
                map.put("serialnumber",orderRechargeBiz.getOrderId().toString());
                map.put("bizcode","101");
                map.put("useraccount",orderRechargeBiz.getRechargeCard());
                map.put("merchant", merchant);
                map.put("amount","1");
                map.put("money",orderRechargeBiz.getRechargeAmount().toString());
                String md5str="1"+"|"+"101"+"|"+merchant+"|"+orderRechargeBiz.getRechargeAmount().toString()+"|"+"|"+
                        orderRechargeBiz.getOrderId().toString()+"|"+orderRechargeBiz.getRechargeCard()+"|"+sign;
                map.put("sign",MessageDigestUtility.toMD5Hex(md5str));
                HttpResponse res= requestDuobei(url,map);
                String str= tostring(res.getInputStream());
                String resultcode= getNodeValue(str,"resultcode");
                if (resultcode.equals("97")){
                    shopOrderService.rechargeOrder(id);
                    resultDO.setSuccess(true);
                    return resultDO;
                }
                shopOrderService.rechargeResult(id,5);
                resultDO.setSuccess(false);
                resultDO.setResult("充值失败");
                return resultDO;
            } catch (Exception e) {
                resultDO.setSuccess(false);
                resultDO.setResult("系统参数错误");
                return resultDO;
            }
        }
        resultDO.setSuccess(false);
        resultDO.setResult("充值参数有误");
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/rechargeresult")
    @RequiresPermissions("shoporder:rechargeResult")
    public Object rechargeResult(@RequestParam(value = "orderid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        OrderRechargeBiz orderRechargeBiz= shopOrderService.queryOrderRechargeInfoByOrderId(id);
        if (orderRechargeBiz!=null&&orderRechargeBiz.getRechargeType()==1){
            String url=PropertiesUtil.getProperties("duobei.url")+"common/queryorder.do";
            Map<String,String> map=new HashMap<>();

            String merchant=PropertiesUtil.getProperties("duobei.merchant");
            String sign=PropertiesUtil.getProperties("duobei.sign");
            map.put("serialnumber",orderRechargeBiz.getOrderId().toString());
            map.put("merchant",merchant);
            String md5str=merchant+"|"+orderRechargeBiz.getOrderId()+"|"+sign;
            map.put("sign",MessageDigestUtility.toMD5Hex(md5str));
            HttpResponse res= requestDuobei(url,map);
            String str= tostring(res.getInputStream());
            String resultcode= getNodeValue(str,"resultcode");
            resultDO.setSuccess(true);
            resultDO.setResult(resultcode);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/balance")
    @RequiresPermissions("shoporder:balance")
    public Object balance(HttpServletResponse response){
        ResultDO<Object> resultDO=new ResultDO<>();
        String merchant=PropertiesUtil.getProperties("duobei.merchant");
        String sign=PropertiesUtil.getProperties("duobei.sign");
        Map<String,String> map=new HashMap<>();
        map.put("merchant",merchant);
        String md5str=merchant+"|"+sign;
        map.put("sign",MessageDigestUtility.toMD5Hex(md5str));
        String url=PropertiesUtil.getProperties("duobei.url")+"common/querybalance.do";
        HttpResponse res= requestDuobei(url,map);
        String str= tostring(res.getInputStream());
        String balance= getNodeValue(str,"balance");
        resultDO.setResult(balance);
        return resultDO;
    }

    /**
     * 请求多贝
     * @param map
     * @return
     */
    private HttpResponse requestDuobei(String url,Map<String, String> map){
        HttpRequest httpRequest=new HttpRequest(url);
        if (!map.isEmpty()){
            for (String key:map.keySet()) {
                httpRequest.addParam(key,map.get(key));
            }
        }
        httpRequest.setMethod(Method.GET);
        HttpResponse httpResponse=null;
        try {
            httpResponse= httpRequest.exeute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpResponse;
    }

    /**
     * 解析InputStream
     * @param stream
     * @return
     */
    private String tostring(InputStream stream){
        StringBuffer bs = new StringBuffer();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(stream,"utf-8"));
            String l = null;
            while((l=buffer.readLine())!=null){
                bs.append(l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bs.toString();
    }

    /**
     * 多贝xml解析获取节点数据
     * @param xmlstr
     * @param tagName
     * @return
     */
    private String getNodeValue(String xmlstr,String tagName){
        String value="";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlstr.getBytes("utf-8")));
            Element element= doc.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName(tagName);
            Node node=nodeList.item(0);
            Node s= node.getFirstChild();
            value= s.getNodeValue();
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (SAXException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return value;
    }
}
