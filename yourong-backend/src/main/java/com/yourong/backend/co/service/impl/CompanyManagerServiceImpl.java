package com.yourong.backend.co.service.impl;

import com.yourong.backend.co.service.CompanyManagerService;
import com.yourong.common.domain.ResultDO;
import com.yourong.common.pageable.Page;
import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.co.manager.CompanyManagerManager;
import com.yourong.core.co.model.CompanyManage;
import com.yourong.core.co.model.query.CompanyManageQuery;
import com.yourong.core.handle.AttachmentInfo;
import com.yourong.core.handle.AttachmentThread;
import com.yourong.core.handle.CompanyManageAttachmentHandle;
import org.apache.commons.lang.StringUtils;
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
 * Created by alan.zheng on 2017/4/1.
 */
@Service
public class CompanyManagerServiceImpl implements CompanyManagerService {
    private static Logger logger = LoggerFactory.getLogger(CompanyManagerServiceImpl.class);
    @Autowired
    private CompanyManagerManager companyManagerManager;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private CompanyManageAttachmentHandle companyManageAttachmentHandle;

    @Override
    public CompanyManage queryById(Long id) {
        try {
            CompanyManage companyManage = companyManagerManager.queryById(id);
            List<BscAttachment> bsclist=null;
            if (companyManage!=null){
                if (!StringUtils.isEmpty(companyManage.getManageHref())){
                    BscAttachment bsc=new BscAttachment();
                    bsc.setId(1L);
                    bsc.setFileUrl(companyManage.getManageHref());
                    bsc.setModule("article_common");
                    bsclist=new ArrayList<>();
                    bsclist.add(bsc);
                    companyManage.setAttachments(bsclist);
                }
            }
            return companyManage;
        } catch (Exception e) {
            logger.error("查询公司管理层异常", e);
        }
        return null;
    }

    @Override
    public Page<CompanyManage> queryPageCompanyManage(CompanyManageQuery query) {
        try {
            return companyManagerManager.queryPageCompanyManage(query);
        } catch (Exception e) {
            logger.error("分页查询公司管理层异常", e);
        }
        return null;
    }

    @Override
    public void updateSortById(List<CompanyManage> list) {
        if (Collections3.isNotEmpty(list)){
            for (CompanyManage dto:list) {
                companyManagerManager.updateSortById(dto.getId(),dto.getSort(),new Date());
            }
        }
    }

    @Override
    public boolean saveCompanyManage(CompanyManage companyManage,String appPath,List<BscAttachment> bscAttachments) {
        try {
            AttachmentInfo info = new AttachmentInfo();
            if (companyManage.getId()!=null&&companyManage.getId()>0){
                companyManage.setUpdateTime(new Date());
                if (companyManagerManager.updateCompanyManage(companyManage)){
                    info.setKeyId(companyManage.getId().toString());
                    info.setBscAttachments(bscAttachments);
                    info.setAppPath(appPath);
                    info.setAttachMentType(AttachmentInfo.AttachMentType.COMPANYMANAGE);
                    info.setOperation(AttachmentInfo.UPDATE);
                    taskExecutor.execute(new AttachmentThread(companyManageAttachmentHandle, info));
                    return true;
                }
                return false;
            }else {
                companyManage.setStatus(1);
                companyManage.setSort(0);
                companyManage.setDelFlag(1);
                companyManage.setCreateTime(new Date());
                CompanyManage insert = companyManagerManager.insertCompanyManage(companyManage);
                if (insert!=null&&insert.getId()!=null&&insert.getId()>0){
                    info.setKeyId(insert.getId().toString());
                    info.setBscAttachments(bscAttachments);
                    info.setAppPath(appPath);
                    info.setAttachMentType(AttachmentInfo.AttachMentType.COMPANYMANAGE);
                    info.setOperation(AttachmentInfo.SAVE);
                    taskExecutor.execute(new AttachmentThread(companyManageAttachmentHandle, info));
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            logger.error("保存公司管理层异常", e);
        }
        return false;
    }

    @Override
    public boolean deleteManager(Long managerid) {
        try {
            return companyManagerManager.deleteCompanyManage(managerid);
        } catch (Exception e) {
            logger.error("删除公司管理层异常", e);
        }
        return false;
    }

    @Override
    public boolean updateStatus(Long managerid, Integer status) {
        try {
            return companyManagerManager.updateStatus(managerid,status,new Date());
        } catch (Exception e) {
            logger.error("更新公司管理层状态异常", e);
        }
        return false;
    }

    @Override
    public ResultDO<String> releaseAll() {
        ResultDO<String> resultDO=new ResultDO<>();
        try {
            if (companyManagerManager.queryUnReleaseCount()<1){
                resultDO.setResult("已无未发布公司管理层");
                return resultDO;
            }
            resultDO.setSuccess(companyManagerManager.releaseAll(new Date()));
        } catch (Exception e) {
            resultDO.setSuccess(false);
            logger.error("公司管理层一键发布异常", e);
        }
        return resultDO;
    }
}
