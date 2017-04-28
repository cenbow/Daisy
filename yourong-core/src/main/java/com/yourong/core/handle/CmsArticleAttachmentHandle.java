package com.yourong.core.handle;

import com.yourong.common.util.Collections3;
import com.yourong.core.bsc.model.BscAttachment;
import com.yourong.core.cms.manager.CmsArticleManager;
import com.yourong.core.upload.model.ImageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by alan.zheng on 2017/3/27.
 */
@Component
public class CmsArticleAttachmentHandle extends BaseAttachmentHandle {
    private static Logger logger = LoggerFactory.getLogger(CmsArticleAttachmentHandle.class);
    @Autowired
    private CmsArticleManager cmsArticleManager;

    @Autowired
    private Map<String, List<ImageConfig>> imagesConfig;

    @Override
    public Map<String, List<ImageConfig>> getImagesConfig() {
        return imagesConfig;
    }

    @Override
    public void attachmentsHandle(AttachmentInfo info) {
        try {
            String image="";
            String chosenimage="";
            //上传附件至阿里云
            uploadFileToAliyun(info.getKeyId(), info.getBscAttachments(), info.getAppPath(),info.getAttachmentType());
            if (Collections3.isNotEmpty(info.getBscAttachments())){
                for (BscAttachment bsc:info.getBscAttachments()) {
                    if ("article_common".equals(bsc.getModule())){
                        image=bsc.getFileUrl();
                    }
                    if ("article_chosen".equals(bsc.getModule())){
                        chosenimage=bsc.getFileUrl();
                    }
                }
            }
            cmsArticleManager.updateCmsArticleAttachments(Long.parseLong(info.getKeyId()),image,chosenimage);
        } catch (Exception e) {
            logger.error("附件处理失败，KeyID:"+info.getKeyId(), e);
        }
    }
}
