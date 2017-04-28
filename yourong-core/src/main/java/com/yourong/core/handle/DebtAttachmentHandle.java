package com.yourong.core.handle;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourong.core.upload.model.ImageConfig;

@Component
public class DebtAttachmentHandle extends BaseAttachmentHandle {
	
	@Autowired
	private Map<String, List<ImageConfig>> imagesConfig;
	
	@Override
	public Map<String, List<ImageConfig>> getImagesConfig() {
		return imagesConfig;
	}

}
