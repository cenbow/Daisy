package com.yourong.backend.sh.service.impl;

import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.backend.sh.dto.SaveGoodsDto;
import com.yourong.backend.sh.service.GoodsService;
import com.yourong.common.enums.AttachmentEnum;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.BeanCopyUtil;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.manager.BscAttachmentManager;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.GoodsAttachmentHandle;
import com.yourong.core.sh.manager.GoodsManager;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XR on 2016/10/20.
 */
@Service
public class GoodsServiceImpl implements GoodsService {
    private static Logger logger = LoggerFactory
            .getLogger(GoodsServiceImpl.class);
    @Autowired
    private GoodsManager goodsManager;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private BscAttachmentManager bscAttachmentManager;
    @Autowired
    private GoodsAttachmentHandle goodsAttachmentHandle;

    @Override
    public SaveGoodsDto queryGoodsById(Long id) {
        Goods goods=goodsManager.queryGoodsById(id);
        SaveGoodsDto goodsDto=new SaveGoodsDto();
        if (goods!=null){
            goodsDto=BeanCopyUtil.map(goods,SaveGoodsDto.class);
        }
        return goodsDto;
    }

    @Override
    public Page<Goods> queryPageGoods(GoodsQuery goodsQuery) {
        return goodsManager.queryPageGoods(goodsQuery);
    }

    @Override
    public List<BscAttachment> queryOrderAttachment(String goodsid) {
        List<BscAttachment> list=new ArrayList<>();
        try {
            list=bscAttachmentManager.findAttachmentsByKeyIdAndModule(goodsid, AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode(),-1);
        } catch (ManagerException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateSortById(List<GoodsDto> list) {
        if (Collections3.isNotEmpty(list)){
            for (GoodsDto dto:list) {
                goodsManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

    @Override
    public boolean updateStatusById(Long id, Integer status) {
        return goodsManager.updateStatusById(id,status,new Date());
    }

    @Override
    public void saveGoods(SaveGoodsDto saveGoodsDto,String appPath) throws ManagerException {
        try {
            Goods goods=new Goods();
            goods=BeanCopyUtil.map(saveGoodsDto,Goods.class);

            if (goods.getGoodsType()!=1&&goods.getGoodsType()!=4){
                goods.setSourceId(null);
            }
            if (goods.getTags()==null){
                goods.setTags(0);
            }
            goods.setSort(0);
            goods.setStatus(1);
            goods.setStatusTime(new Date());
            goods.setCreateTime(new Date());
            goods.setDelFlag(1);
            AttachmentInfo attachmentInfo=new AttachmentInfo();
            if (goods.getId()!=null&&goods.getId()>0){       //编辑
                goods.setUpdateTime(new Date());
                attachmentInfo.setOperation(AttachmentInfo.UPDATE);
                goodsManager.updateGoodsInfo(goods);
            }else {
                attachmentInfo.setOperation(AttachmentInfo.SAVE);
                goods.setFreezeInventor(0);
                goods= goodsManager.insertGoods(goods);
            }
            if (Collections3.isNotEmpty(saveGoodsDto.getBscAttachments())){
                for (BscAttachment b:saveGoodsDto.getBscAttachments()) {
                    b.setModule(AttachmentEnum.ATTACHMENT_MODULE_SHOP_GOODS.getCode());
                }
            }
            attachmentInfo.setBscAttachments(saveGoodsDto.getBscAttachments());
            attachmentInfo.setAppPath(appPath);
            attachmentInfo.setKeyId(goods.getId().toString());
            attachmentInfo.setAttachMentType(AttachmentInfo.AttachMentType.GOODS);
            taskExecutor.execute(new AttachmentThread(goodsAttachmentHandle,attachmentInfo));
        } catch (Exception e) {
            logger.error("插入所有债券信息失败,goods="+saveGoodsDto, e);
            throw new ManagerException(e);
        }
    }

	@Override
	public boolean deleteById(Long id) {
		return goodsManager.deleteById(id);
	}
}
