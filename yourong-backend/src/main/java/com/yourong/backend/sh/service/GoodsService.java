package com.yourong.backend.sh.service;

import com.yourong.backend.sh.dto.GoodsDto;
import com.yourong.backend.sh.dto.SaveGoodsDto;
import com.yourong.common.exception.ManagerException;
import com.yourong.common.pageable.Page;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.sh.model.Goods;
import com.yourong.core.sh.model.query.GoodsQuery;

import java.util.List;

/**
 * Created by XR on 2016/10/20.
 */
public interface GoodsService {
    SaveGoodsDto queryGoodsById(Long id);
    Page<Goods> queryPageGoods(GoodsQuery goodsQuery);
    List<BscAttachment> queryOrderAttachment(String goodsid);
    void updateSortById(List<GoodsDto> list);
    boolean updateStatusById(Long id,Integer status);
    void saveGoods(SaveGoodsDto saveGoodsDto,String appPath) throws ManagerException;
    boolean deleteById(Long id);
}
