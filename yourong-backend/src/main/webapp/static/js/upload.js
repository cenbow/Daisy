var overrideOnSelectError = function(file, errorCode, errorMsg) {
	var msgText = "上传失败：\n";
	var settings = this.settings;
	switch (errorCode) {
	case SWFUpload.QUEUE_ERROR.QUEUE_LIMIT_EXCEEDED:
		msgText += "\t每次最多上传 " + settings.queueSizeLimit + "个文件";
		break;
	case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
		msgText += "\t文件大小超过限制" + settings.fileSizeLimit + "";
		break;
	case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
		msgText += "\t文件大小必须大于0";
		break;
	case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
		msgText += "\t文件格式不正确，仅限 " + settings.fileTypeExts;
		break;
	default:
		msgText += "\t错误代码：" + errorCode + "\n" + errorMsg;
	}
	alert(msgText);
}

var overrideOnDialogClose = function() {
}

var overrideOnUploadError = function(file, errorCode, errorMsg, errorString) {
	if (errorCode == SWFUpload.UPLOAD_ERROR.FILE_CANCELLED
			|| errorCode == SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
		return;
	}
	var msgText = "上传失败\n";
	switch (errorCode) {
	case SWFUpload.UPLOAD_ERROR.HTTP_ERROR:
		msgText += "HTTP 错误\n" + errorMsg;
		break;
	case SWFUpload.UPLOAD_ERROR.MISSING_UPLOAD_URL:
		msgText += "上传文件丢失，请重新上传";
		break;
	case SWFUpload.UPLOAD_ERROR.IO_ERROR:
		msgText += "IO错误";
		break;
	case SWFUpload.UPLOAD_ERROR.SECURITY_ERROR:
		msgText += "安全性错误\n" + errorMsg;
		break;
	case SWFUpload.UPLOAD_ERROR.UPLOAD_LIMIT_EXCEEDED:
		msgText += "每次最多上传 " + this.settings.uploadLimit + "个";
		break;
	case SWFUpload.UPLOAD_ERROR.UPLOAD_FAILED:
		msgText += errorMsg;
		break;
	case SWFUpload.UPLOAD_ERROR.SPECIFIED_FILE_ID_NOT_FOUND:
		msgText += "找不到指定文件，请重新操作";
		break;
	case SWFUpload.UPLOAD_ERROR.FILE_VALIDATION_FAILED:
		msgText += "参数错误";
		break;
	default:
		msgText = "";
	}
	if (msgText != "") {
		alert(msgText);
	}
}