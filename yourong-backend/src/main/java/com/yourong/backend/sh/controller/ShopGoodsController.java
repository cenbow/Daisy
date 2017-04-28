package com.yourong.backend.sh.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yourong.backend.BaseController;
import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.backend.sh.dto.SaveGoodsDto;
import com.yourong.backend.sh.service.GoodsService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XR on 2016/10/21.
 */
@Controller
@RequestMapping(value = "/shopgoods")
public class ShopGoodsController extends BaseController {
    @Autowired
    private GoodsService goodsService;

    @RequestMapping(value = "/index")
    @RequiresPermissions("shgoods:index")
    public String index(){
        return "/sh/goods/index";
    }

    @ResponseBody
    @RequestMapping(value = "/ajax")
    public Object ajax(HttpServletRequest req){
        String goodstype=req.getParameter("goodstype");
        String pagesize= req.getParameter("iDisplayLength");
        String start= req.getParameter("iDisplayStart");
        if (StringUtils.isEmpty(goodstype)){
            goodstype="0";
        }
        int type=1;
        try {
            type=Integer.parseInt(goodstype);
        } catch (NumberFormatException e) {
            type=1;
        }
        Page<Goods> page=new Page<>();
        GoodsQuery goodsQuery=new GoodsQuery();
        goodsQuery.setGoodsType(type);
        goodsQuery.setPageSize(Integer.parseInt(pagesize));
        goodsQuery.setStartRow(Integer.parseInt(start));
        page=goodsService.queryPageGoods(goodsQuery);
        return page;
    }

    @ResponseBody
    @RequestMapping(value = "/savegoods")
    @RequiresPermissions("shopgoods:saveGoods")
    public Object saveGoods(HttpServletRequest req, SaveGoodsDto saveGoodsDto, @RequestParam(value = "goodsAttachmentsData")String goodsAttachmentsData){
        ResultDO<Object> resultDO=new ResultDO<>();
        List<BscAttachment> attachments= new ArrayList<>();
        if (!StringUtils.isEmpty(goodsAttachmentsData)){
            attachments= JSON.parseArray(goodsAttachmentsData,BscAttachment.class);
        }
        if (saveGoodsDto.getRechargeAmount()!=null&&saveGoodsDto.getRechargeAmount()==0){
            saveGoodsDto.setRechargeAmount(null);
        }
        if (saveGoodsDto.getLevelNeed()!=null&&saveGoodsDto.getLevelNeed()==0){
            saveGoodsDto.setLevelNeed(null);
        }
        saveGoodsDto.setBscAttachments(attachments);
        saveGoodsDto.setLevelNeed(saveGoodsDto.getLevelNeed());
        if (saveGoodsDto.getDiscount()==null){
            saveGoodsDto.setDiscount(10f);
        }
        if (saveGoodsDto.getAutoInventory()==null||saveGoodsDto.getAutoInventory()!=1){
            saveGoodsDto.setAutoInventory(-1);
            saveGoodsDto.setAutoInventoryCount(null);
        }
        String appPath = req.getSession().getServletContext().getRealPath("/");
        try {
            goodsService.saveGoods(saveGoodsDto,appPath);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/upper")
    @RequiresPermissions("shopgoods:upper")
    public Object upper(@RequestParam(value = "goodsid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        if (goodsService.updateStatusById(id,2)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/lower")
    @RequiresPermissions("shopgoods:lower")
    public Object lower(@RequestParam(value = "goodsid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        if (goodsService.updateStatusById(id,1)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }

    @ResponseBody
    @RequestMapping(value = "/savesort")
    @RequiresPermissions("shopgoods:saveSort")
    public Object saveSort(@RequestParam(value = "sortstr")String sortstr){
        ResultDO<Object> resultDO=new ResultDO<>();
        try {
            List<GoodsDto> sorts= JSON.parseObject(sortstr,new TypeReference<ArrayList<GoodsDto>>(){});
            goodsService.updateSortById(sorts);
            resultDO.setSuccess(true);
            return resultDO;
        } catch (Exception e) {
            resultDO.setSuccess(false);
            return resultDO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/edit")
    @RequiresPermissions("shopgoods:edit")
    public Object edit(@RequestParam(value = "goodsid")Long goodsid){
        ResultDO<Object> resultDO=new ResultDO<>();
        SaveGoodsDto saveGoodsDto=new SaveGoodsDto();
        saveGoodsDto= goodsService.queryGoodsById(goodsid);
        List<BscAttachment> attachments=goodsService.queryOrderAttachment(goodsid.toString());
        saveGoodsDto.setBscAttachments(attachments);
        resultDO.setSuccess(true);
        resultDO.setResult(saveGoodsDto);
        return resultDO;
    }

    /**
     * 查询商品附件
     * @param orderid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/findattachment")
    @RequiresPermissions("shopgoods:findAttachment")
    public Object findAttachment(@RequestParam(value = "orderid" ,defaultValue = "0")Long orderid){
        Map map=new HashMap();
        List<BscAttachment> attachments=goodsService.queryOrderAttachment(orderid.toString());
        map.put("attachments",attachments);
        return map;
    }
    
    @ResponseBody
    @RequestMapping(value = "/deleted")
    @RequiresPermissions("shopgoods:deleted")
    public Object delete(@RequestParam(value = "goodsid")Long id){
        ResultDO<Object> resultDO=new ResultDO<>();
        if (goodsService.deleteById(id)){
            resultDO.setSuccess(true);
            return resultDO;
        }
        resultDO.setSuccess(false);
        return resultDO;
    }
}
