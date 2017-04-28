package com.yourong.core.handle;

/**
 * 附件处理线程
 *
 */
public class AttachmentThread implements Runnable {

	private BaseAttachmentHandle attachmentHandle;

	private AttachmentInfo  info;
	
	@Override
	public void run() {
		attachmentHandle.attachmentsHandle(info);
	}
	
	public AttachmentThread(BaseAttachmentHandle attachmentHandle, AttachmentInfo info){
		this.attachmentHandle = attachmentHandle;
		this.info = info;
	}
}
