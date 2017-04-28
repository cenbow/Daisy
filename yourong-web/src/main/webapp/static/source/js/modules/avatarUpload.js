var jcrop_api;
jQuery(function($) {
    $("#avatarUpload").uploadify({
        height: 31,
        width: 120,
        method: 'post',
        swf: environment.globalPath + '/static/lib/js/uploadify/uploadify.swf',
        uploader: environment.globalPath + '/avatarUpload/upload',
        fileTypeExts: '*.png;*.jpg;*.jpeg',
        fileObjName: 'uploadFile',
        buttonImage: environment.globalPath + '/static/img/member/upload-file-btn.png',
        fileSizeLimit: '1MB',
        overrideEvents: ['onSelectError', 'onDialogClose', 'onUploadError', 'onUploadSuccess'],
        onSelectError: overrideOnSelectError,
        onDialogClose: overrideOnDialogClose,
        onUploadError: overrideOnUploadError,
        onUploadSuccess: overrideOnUploadSuccess,
        multi: false
    });

});



var overrideOnUploadSuccess = function(file, data, response) {
    var info = eval("(" + data + ")");
    if (info.uploadStatus >= 1) {
        var fileInfo = info.fileInfo[0];
        var path = fileInfo.ossPicUrl;
        if(path.indexOf("oss") <=0){
        	path = environment.globalPath + "/" + path;
        }
        $(".avatar").attr("src", path);
        $(".avatar").attr("id", "avatar");
        $("#s100").attr("src", path);
        $("#s50").attr("src", path);
        $(".avatar").css({
            "width": fileInfo.width,
            "height": fileInfo.height
        });
        $("#avatars").val(fileInfo.filePath);
        $("#ossPicUrl").val(path);
        $("#saveAvatar").show();
        if (jcrop_api != null) {
            jcrop_api.destroy();
        }
        jcropAvatar();
    } else {
        $.xDialog({
            content: "头像上传失败"
        });
    }
}



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
    $.xDialog({
        content: msgText
    });
}

var overrideOnDialogClose = function() {}

var overrideOnUploadError = function(file, errorCode, errorMsg, errorString) {
    if (errorCode == SWFUpload.UPLOAD_ERROR.FILE_CANCELLED || errorCode == SWFUpload.UPLOAD_ERROR.UPLOAD_STOPPED) {
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
        $.xDialog({
            content: msgText
        });
    }
}


function jcropAvatar() {
    var boundx = $('.avatar').width(),
        boundy = $('.avatar').height(),
        $s100 = $('.preview-avatar-s100 img'),
        $s50 = $('.preview-avatar-s50 img'),
        xsize = $('.preview-avatar-s100').width(),
        ysize = $('.preview-avatar-s100').height();
    $('#avatar').Jcrop({
        allowSelect:false,
        bgOpacity: 0.6,
        bgColor: 'white',
        addClass: 'jcrop-light',
        onChange: updatePreview,
        setSelect: [90,80,210,200],
        onSelect: updatePreview,
        aspectRatio: 1,
        minSize: [70, 70]
    }, function() {
        jcrop_api = this;
    });

    function updatePreview(c) {
        if (parseInt(c.w) > 0) {
            var rx = xsize / c.w;
            var ry = ysize / c.h;
            $s100.css({
                width: Math.round(rx * boundx) + 'px',
                height: Math.round(ry * boundy) + 'px',
                marginLeft: '-' + Math.round(rx * c.x) + 'px',
                marginTop: '-' + Math.round(ry * c.y) + 'px'
            });

            var rx2 = $('.preview-avatar-s50').width() / c.w;
            var ry2 = $('.preview-avatar-s50').height() / c.h;
            $s50.css({
                width: Math.round(rx2 * boundx) + 'px',
                height: Math.round(ry2 * boundy) + 'px',
                marginLeft: '-' + Math.round(rx2 * c.x) + 'px',
                marginTop: '-' + Math.round(ry2 * c.y) + 'px'
            });

            $('#x').val(c.x);
            $('#y').val(c.y);
            $('#w').val(c.w);
            $('#h').val(c.h);
        }
    };
}


/**
 * 保存用户头像
 */
$("#saveAvatar").on("click", function() {
    var data = $("#avatarUploadForm").serializeArray();
    var config = {
        url: environment.globalPath + "/member/saveAvatar",
        data: data,
        callback: saveAvatarCallback
    }
    $.xPost(config);
})

/**
 * 保存用户头像回调函数
 */
function saveAvatarCallback(data) {
    if (data != "") {
        if (data.success) {
        	avatarMessageDialog();
        } else {
            $.xDialog({
                content: "头像上传失败"
            });
        }
    }
}

/**
 * 编辑&显示头像切换
 */
$("#j-avatar-show").on("click", function() {
    $("#avatar-show").hide();
    $("#avatar-edit").show(1000);
})

/**
 * 上传头像保存成功提示框
 */
function avatarMessageDialog(){
	 var html = "<div class='u-dialog-wrap'><i class='u-icon-success2 u-icon f-icon-37'></i><span style='max-width:400px;'>" +
	    "头像正在保存中，新头像将在<span id='j-timing' style='display:inline;padding:0;'>3</span>秒后呈现!" +
	    "</span></div>";
	   var avatarDialog = dialog({
		    id:"upload-avatar-dialog",
	        content: html,
	        skin: "u-dialog-box",
	        cancel: false,
	        width: 360,
	        height: 70
	    });
	   avatarDialog.show();
	   var timingInterval = setInterval(function() {
		  var time = $("#j-timing").text();
		  var v = parseInt(time)-1;
		  if(v > 0){
			  $("#j-timing").text(v);
		  }else{
			 clearInterval(timingInterval);
			 avatarDialog.close().remove(); 
			 window.location.href = top.location.href;
		  }
	   },1000)
}
