$(function(){
	Dropzone.autoDiscover = false;
	$(".dropzoneImage").each(function(){
		 var dropzoneID =$(this).attr("id");
		 var clickableClass = "."+dropzoneID;
		 var category = $(this).data("category");
		 var maxfiles = $(this).data("maxfiles");
		 if(typeof maxfiles === "undefined" || maxfiles === ""){
			 maxfiles = 50;
		 }
		 $("#"+dropzoneID).dropzone({
			url:config.urlMap.uploadURL,
			thumbnailWidth: null,
			thumbnailHeight: null,
			acceptedFiles:".jpg,.jpeg,.png",
			maxFilesize:1,
			maxFiles:maxfiles,
			params:{category:category},
			clickable: clickableClass,
			dictDefaultMessage: "Drop files here to upload",
			dictFallbackMessage: "上传控件暂不支持您使用的浏览器。请使用Firefox4+、Chrome7+、IE10+、Opear12+、Safari6+版本以上的浏览器",
			dictFallbackText: "",
			dictFileTooBig: "101",// 文件大小超限
			dictInvalidFileType: "102",// 不支持文件类型
			dictResponseError: "103",// 服务器异常
			dictCancelUpload: "104",// 取消上传
			dictCancelUploadConfirmation: "确定要取消上传吗？",
			dictRemoveFile: "删除文件",
			dictMaxFilesExceeded: "105",// 文件上传数量超限了，不能再上传更多文件
			init: function() {
		      this.on("addedfile", function(file) {
		    	  $("#"+dropzoneID+" .dz-preview").addClass("ui-sortable-handle");
		      });
		      this.on("success",function(file,data){
			      	if(data.uploadStatus != 1){
			      		alert("文件上传失败");
			      		var dropzone = Dropzone.forElement("#"+dropzoneID);
			      		dropzone.removeFile(file);
			      	}else{
			      		var fileInfo = data.fileInfo[0];
			      		var uniqueId = "";
			      		// 此处需要特殊处理下，主要是添加和编辑时
			      		if(typeof fileInfo.unique !== "undefined"){
			      			uniqueId = fileInfo.unique+"-"+dropzoneID;
			      		}else{
			      			uniqueId = fileInfo.name;//来自于后台生成的文件名称，不含中文、特殊字符，具有唯一性
			      		}
			      		customFileNameText(this,file,uniqueId,dropzoneID);
			      		
			      		customRemoveButton(this,file,dropzoneID);
			      		
						var input = $("#"+uniqueId);
							input.val(fileInfo.originalFilename);
							input.data("category", fileInfo.category);
							input.data("suffix", fileInfo.suffix);
							input.data("filePath", fileInfo.filePath);
							input.data("fileName", fileInfo.name);
							input.data("fileSize", fileInfo.fileSize);
							input.data("originalFilename", fileInfo.originalFilename);
							if(typeof fileInfo.attachmentId !== "undefined"){
								input.data("attachmentId", fileInfo.attachmentId);
							}else{
								input.data("attachmentId", "");
							}
						sortFileItem(dropzoneID);
			      	}
		      });
		      this.on("error",function(file,data){
		    	  dropzoneError(dropzoneID, file, data);
		      })
	    	}
		})
	})
	
	//支持拖动
	$(".dropzoneSort").sortable({
		cursor:'move',
		opacity:'0.6',
		revert:true, 
		update:function(e,u){
			sortFileItem($(this).attr("id"));
		}
	});
})


//上传错误 处理函数
function dropzoneError(dropzoneID, file, data){
	var dropzone = Dropzone.forElement("#"+dropzoneID);
    dropzone.removeFile(file);
	var statusCode = 0;
	try{
		// 错误提示文本，可见的重写成了'代码'且http状态码一般都在三位数内。
		if(data !="" && data.length <= 3){
			statusCode = parseInt(data);
		}
	}catch(e){
		statusCode = 0;
	}
    switch(statusCode){
    	case 101:
    		alert("图片太大了不允许上传，最大支持："+dropzone.options.maxFilesize+"MB");
    		break;
    	case 102:
    		alert("选中的图片不支持上传，支持的图片类型："+dropzone.options.acceptedFiles);
    		break;
    	case 103:
    		alert("图片上传出现异常，请重试.如果多次尝试失败，请检查您的登录状态!");
    		break;
    	case 105:
    		alert("图片上传数量超限了，不能再上传更多图片!");
    		break;
    	case 404:
    		alert("上传地址失效了，请联系管理员！");
    		break;
    	default:
    		alert("图片上传出现错误，请刷新页面重试。");
    		break;
    }
}


//自定义文件名输入框
function customFileNameText(tis, file,uniqueId,dropzoneID){
	var input = Dropzone.createElement("<input type='text' id='"+uniqueId+"' class='col-xs-12 col-sm-12 dz-input-data'/>");		
    var _this = tis;		
    input.addEventListener("blur", function(e) {
	  e.preventDefault();
	  e.stopPropagation();
	  var v = $(this).val();
	  if(v.length > 30){
	  	$(this).val($(this).data("originalFilename"));
	  }
	  sortFileItem(dropzoneID);
    });
    file.previewElement.appendChild(input);
    var colorbox = $("#"+uniqueId).siblings(".dz-details");
    colorbox.attr("href",file.url);
    colorbox.attr("title",file.name);
    colorbox.addClass(dropzoneID+"_colorbox");
}

//自定义删除按钮,不使用插件自带的remove，主要是需要在考虑在删除按钮前要显示文本框
function customRemoveButton(tis, file, dropzoneID){
	var removeButton = Dropzone.createElement("<a data-dz-remove='' href='javascript:undefined;' class='dz-remove dz-remove-img'>删除图片</a>");		
    var _this = tis;		
    removeButton.addEventListener("click", function(e) {
	  e.preventDefault();
	  e.stopPropagation();
	  if(confirm("确定要删除文件吗？")){
		  _this.removeFile(file);
		  sortFileItem(dropzoneID);
	  }
    });		
    file.previewElement.appendChild(removeButton);
}

//排序文件项
function sortFileItem(targetId){
	var aid = $("#attachmentIndexData").length;
	if(aid > 0){
		fillAttachmentIdItem();
	}else{
		fillAttachmentItem(targetId);
	}
}

//附件完整信息
function fillAttachmentItem(targetId){
	var inputDataLength = $("#"+targetId+" .dz-input-data").size() - 1;
	var _jsonArray = "";
	$("#"+targetId+" .dz-input-data").each(function(index,obj){
		_jsonArray += "{id:'"+$(this).data("attachmentId")+"',fileName:'"+$(this).val()+"',fileUrl:'"+$(this).data("filePath")+"',fileExt:'"+$(this).data("suffix")+"',module:'"+$(this).data("category")+"',fileSize:'"+$(this).data("fileSize")+"'}";
		if(index < inputDataLength){
			_jsonArray += ",";
		}
	});
	if(_jsonArray != ""){
		$("#j-json-"+targetId).val("["+_jsonArray+"]");
	}else{
		$("#j-json-"+targetId).val("");
	}
}

//附件ID
function fillAttachmentIdItem(){
	var _jsonArray = "";
	var inputDataLength = $(".dz-input-data").size() - 1;
	$(".dz-input-data").each(function(index,obj){
		_jsonArray += "{attachmentId:'"+$(this).data("attachmentId")+"'}";
		if(index < inputDataLength){
			_jsonArray += ",";
		}
	})
	if(_jsonArray != ""){		
		$("#attachmentIndexData").val("["+_jsonArray+"]");
	}else{
		$("#attachmentIndexData").val("");
	}
}


//把文件增加到控件
function addImageToDropzone(customDropzone, attachment){
	var dropZoneFiles = customDropzone.files;
	var index = dropZoneFiles.length;
	var unique = "dz-img-"+index;
	var url;
	if (attachment.fileUrl.indexOf("open/upload")<0){
		url = config.picURLHead+attachment.fileUrl;
		if(attachment.module.indexOf("direct_project_borrower")>=0){
			url = url.substring(0,url.length-4)+"_425.jpg";
		}
	}else {
		url = config.baseURL+"/"+attachment.fileUrl;
	}
	var mockFile = {
		name: attachment.fileName,
		size: attachment.fileSize,
		type: 'image/jpeg',
		status: Dropzone.SUCCESS,
		url   : url
	};
	customDropzone.emit("addedfile", mockFile);
	customDropzone.emit("thumbnail", mockFile, url);
	dropZoneFiles.push(mockFile);
	var attachmentId="";
	if (attachment.id){
		attachmentId=attachment.id
	}
	var data = {"fileInfo":[
		{
			category: attachment.module,
			filePath: attachment.fileUrl,
			fileSize: attachment.fileSize,
			name    : attachment.fileName,
			originalFilename :attachment.fileName,
			suffix: attachment.fileExt,
			attachmentId:attachmentId,
			unique: unique
		}
	],"uploadStatus":1};
	customDropzone.emit("success", dropZoneFiles[index], data);
}

//禁用控件的可编辑属性
function disableDropzone(){
	$(".dz-upload-btn").hide();
	$(".dz-remove-img").hide();
	//文件名不可编辑
	$(".dz-input-data").attr('disabled', 'disabled');
	//禁用排序
	$(".dropzoneSort").sortable('disable')
}

//删除所有文件
function deleteDropzoneAllFile(){
	$(".dropzoneImage").each(function(){
		var dropzoneID =$(this).attr("id");
		var customDropzone = Dropzone.forElement("#"+dropzoneID);
		var dropZoneFiles = customDropzone.files;
		if(dropZoneFiles.length > 0){
			for(i=0; i<dropZoneFiles.length;i++){
				customDropzone.removeFile(dropZoneFiles[i]);
			}
		}
	});
}

//colorbox
function customColorBox(module){
	var colorbox = 'dropzone_'+module+'_colorbox';
	$('.'+colorbox).colorbox({rel:colorbox,current: "图片 {current} / {total}"});
}


//禁用控件的可编辑属性
function disableDropzoneById(id){
	$(id).next("div").find(".dz-upload-btn").hide();
	$(id).next("div").find(".dz-remove-img").hide();
	//文件名不可编辑
	$(id).next("div").find(".dz-input-data").attr('disabled', 'disabled');
	//禁用排序
	$(id).next("div").find(".dropzoneSort").sortable('disable')
}

function deleteDropzoneAllimage(imgID){
	$(".dropzoneImage").each(function(){
		var dropzoneID =$(this).attr("id");
			var customDropzone = Dropzone.forElement("#"+dropzoneID);
			var dropZoneFiles = customDropzone.files;
			if(dropzoneID.indexOf(imgID)==0){
				if((dropzoneID.indexOf("mosaic")>=0)==(imgID.indexOf("mosaic")>=0)){
					if(dropZoneFiles.length > 0){
						for(i=0; i<dropZoneFiles.length;i++){
							customDropzone.removeFile(dropZoneFiles[i]);
						}
					}
				}
			}
			
	});
}
