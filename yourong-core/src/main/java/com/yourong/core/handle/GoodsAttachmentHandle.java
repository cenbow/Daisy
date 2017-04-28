package com.yourong.core.handle;

import com.yourong.core.upload.model.ImageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by XR on 2016/10/20.
 */
@Component
public class GoodsAttachmentHandle extends BaseAttachmentHandle {
    @Autowired
    private Map<String, List<ImageConfig>> imagesConfig;

    @Override
    public Map<String, List<ImageConfig>> getImagesConfig() {
        return imagesConfig;
    }
}
