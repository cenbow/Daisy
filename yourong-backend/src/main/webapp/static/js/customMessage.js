var customMessageTable,customMessageForm;


$(function(){
//	if($("customMsg-table").length>0){
		customMessageListDataTable();
//	}
		
	customMessageForm = $("#custom-message-form").Validform({
		 tiptype : 4,
	     ajaxPost : true
	 });
	
	shortMessageForm = $("#short-message-form").Validform({
		 tiptype : 4,
	     ajaxPost : true
	 });
	
	customMessageForm.ignore("#custom_project");
	customMessageForm.ignore("#custom_user");
	customMessageForm.ignore("#register_end_time");
	customMessageForm.ignore("#register_days");
	
	shortMessageForm.ignore("#custom_project");
	shortMessageForm.ignore("#custom_user");
	shortMessageForm.ignore("#register_end_time");
	shortMessageForm.ignore("#register_days");

	
})

$("#js-updateOrSave").click(function(){
	if($(this).hasClass("btn-primary")){
		if(customMessageForm.check(false)){
			$(this).removeClass("btn-primary");
			$(this).text("请稍候...");
			//提交时，编辑框置为提示框显示层之下
			$("#form-field-content").css("position","relative");
			$("#form-field-content").css("z-index","0");
			
			var action = $(this).data("action");
			var url = config.urlMap.save;
			if(action == "edit"){
				url = config.urlMap.update;
			}
			var userType = $("#userType").val();
			if(userType == 3){
				$("#customAttr").val($("#custom_project").val());
			}else if(userType == 4){
				$("#customAttr").val($("#custom_user").val());
			}
			$('#custom-message-form').xform("post", url,function(data){
		 	   if(!data.success){
		 		   bootbox.alert(data.resultCodeEum[0].msg);
		  	  	}else{
		  		  bootbox.alert("保存消息成功",function(){
		  			window.top.closeActiveIframe("信息列表");
		  		  });
		  	  	}
		 	   	
		    });
		}
	}
})

$("#js-shortMsgSave").click(function(){
	if($(this).hasClass("btn-primary")){
		if(shortMessageForm.check(false)){
			var that = $(this);
			$(this).removeClass("btn-primary");
			$(this).text("请稍候...");
			var action = $(this).data("action");
			
			var action = $(this).data("action");
			var url = config.urlMap.saveShortMsg;
			if(action == "edit"){
				url = config.urlMap.update;
			}
			var userType = $("#userType").val();
			if(userType == 3){
				$("#customAttr").val($("#custom_project").val());
			}else if(userType == 4){
				$("#customAttr").val($("#custom_user").val());
			}
			$('#short-message-form').xform("post", url,function(data){
		 	   if(!data.success){
		 		   bootbox.alert(data.resultCodeEum[0].msg);
		  	  	}else{
		  		  bootbox.alert("保存短信成功",function(){
		  			that.addClass("btn-primary");
		  			that.text("保存");
		  			window.top.closeActiveIframe("信息列表");
		  		  });
		  	  	}
		    });
		}
	}
})



$("#js-appPushSave").click(function(){
	if($(this).hasClass("btn-primary")){
		if(shortMessageForm.check(false)){
			var that = $(this);
			$(this).removeClass("btn-primary");
			$(this).text("请稍候...");
			var action = $(this).data("action");
			
			var action = $(this).data("action");
			var url = config.urlMap.saveAppPush;
			if(action == "edit"){
				url = config.urlMap.update;
			}
			var userType = $("#userType").val();
			if(userType == 3){
				$("#customAttr").val($("#custom_project").val());
			}else if(userType == 4){
				$("#customAttr").val($("#custom_user").val());
			}
			$('#short-message-form').xform("post", url,function(data){
		 	   if(!data.success){
		 		   bootbox.alert(data.resultCodeEum[0].msg);
		  	  	}else{
		  		  bootbox.alert("保存app推送成功",function(){
		  			that.addClass("btn-primary");
		  			that.text("保存");
		  			window.top.closeActiveIframe("信息列表");
		  		  });
		  	  	}
		    });
		}
	}
})

$("#js-appMsgSave").click(function(){
	if($(this).hasClass("btn-primary")){
		if(shortMessageForm.check(false)){
			var that = $(this);
			$(this).removeClass("btn-primary");
			$(this).text("请稍候...");
			var action = $(this).data("action");
			var url = config.urlMap.saveAppMsg;
			if(action == "edit"){
				url = config.urlMap.update;
			}
			var userType = $("#userType").val();
			if(userType == 3){
				$("#customAttr").val($("#custom_project").val());
			}else if(userType == 4){
				$("#customAttr").val($("#custom_user").val());
			}
			$('#short-message-form').xform("post", url,function(data){
		 	   if(!data.success){
		 		   bootbox.alert(data.resultCodeEum[0].msg);
		  	  	}else{
		  		  bootbox.alert("保存app消息成功",function(){
		  			that.addClass("btn-primary");
		  			that.text("保存");
		  			window.top.closeActiveIframe("信息列表");
		  		  });
		  	  	}
		    });
		}
	}
})

$("#userType").change(function(){
	var userType = $(this).val();
	if(userType == 3){
		$("#user_type_3").show();
		$("#user_type_4").hide();
		$("#user_type_7").hide();
		customMessageForm.ignore("#custom_user");
		customMessageForm.unignore("#custom_project");
		shortMessageForm.ignore("#custom_user");
		shortMessageForm.unignore("#custom_project");

		shortMessageForm.ignore("#register_end_time");
		shortMessageForm.ignore("#register_days");
		
		customMessageForm.ignore("#register_end_time");
		customMessageForm.ignore("#register_days");

	}else if(userType == 4){
		$("#user_type_3").hide();
		$("#user_type_7").hide();
		$("#user_type_4").show();
		customMessageForm.ignore("#custom_project");
		customMessageForm.unignore("#custom_user");
		shortMessageForm.ignore("#custom_project");
		shortMessageForm.unignore("#custom_user");
		
		shortMessageForm.ignore("#register_end_time");
		shortMessageForm.ignore("#register_days");
		
		customMessageForm.ignore("#register_end_time");
		customMessageForm.ignore("#register_days");
	} else if(userType == 7) {
		// 新注册用户
		$("#user_type_3").hide();
		$("#user_type_4").hide();
		$("#user_type_7").show();
		
		customMessageForm.unignore("#register_end_time");
		customMessageForm.unignore("#register_days");
		
		shortMessageForm.unignore("#register_end_time");
		shortMessageForm.unignore("#register_days");
	} else{
		$("#user_type_3").hide();
		$("#user_type_4").hide();
		$("#user_type_7").hide();
		customMessageForm.ignore("#custom_project");
		customMessageForm.ignore("#custom_user");
		shortMessageForm.ignore("#custom_project");
		shortMessageForm.ignore("#custom_user");
		
		shortMessageForm.ignore("#register_end_time");
		shortMessageForm.ignore("#register_days");
		
		customMessageForm.ignore("#register_end_time");
		customMessageForm.ignore("#register_days");
	}
})

$('#query_customMsg').on('click', function() {
	customMessageTable.fnDraw();
})

function customMessageListDataTable(){
	customMessageTable = $('#customMsg-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
        	getSearchValue("#customMsgSearchForm",aoData);
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'id',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        }, {
            'mDataProp' : 'id',
            'bSortable' : true
        },{
			'mDataProp' : 'msgType',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				if(data == 1) {
					return "短信";
				} else if(data == 2) {
					return "邮件";
				} else if(data == 3) {
					return "站内信";
				} else if(data == 4) {
					return "百度推送";
				} else if(data == 5) {
					return "app消息";
				}
				return "";
            }
        },{
			'mDataProp' : 'msgName',
			'bSortable' : false
		},{
			'mDataProp' : 'content',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return formatContent(row.content);
            }
		}, {
            'mDataProp' : 'userType',
            'bSortable' : true,
			'mRender'   : function(data, type, row) {
				return getDictLabel(config.userType, row.userType);
            }
        },{
			'mDataProp' : 'creatorName',
			'bSortable' : false
		}, {
            'mDataProp' : 'status',
            'bSortable' : true,
			'mRender'   : function(data, type, row) {
				return getDictLabel(config.msgStatus, row.status);
            }
        },{
			'mDataProp' : 'sendDate',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
		},{
			'mDataProp' : 'invalidDate',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				if(data == null){
					return "永久有效";
				}
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
		},{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  msgAction(row);
			}
		}]
		
	});//dataTable
}


function msgAction(row){
	
	var status = row.status;
	var name = "站内信";
	if(row.msgType==1){
		name ="短信";	
	}
	if(row.msgType==4){
		name="百度推送";
	}
	if(row.msgType==5){
		name="app消息";
	}
	var btn = "",find="",waitReview="",review="",release="",edit="",del="";
	find = "<button class='btn btn-xs btn-primary permission-"+config.permission.find+"' onclick=findMsg('"+row.id+"','"+row.msgType+"','"+encodeURIComponent("查看"+name)+"')>查看</button>";
	edit = "<button class='btn btn-xs btn-pink permission-"+config.permission.edit+"' onclick=editMsg('"+row.id+"','"+row.msgType+"','"+encodeURIComponent("编辑"+name)+"')>编辑</button>";
	waitReview = "<button class='btn btn-xs btn-purple permission-"+config.permission.waitReview+"' onclick=submitReview('"+row.id+"')>提交审核</button>";
	review = "<button class='btn btn-xs btn-danger permission-"+config.permission.review+"'  onclick=review('"+row.id+"')>审核</button>";
	del = "<button class='btn btn-xs btn-inverse permission-"+config.permission.del+"'  onclick=delMsg('"+row.id+"')>删除</button>";
	cancel="<button class='btn btn-xs btn-inverse permission-"+config.permission.cancel+"'  onclick=cancelMsg('"+row.id+"')>取消发布</button>";
	var userType = row.userType;
	if(status == 1){//存盘
		return edit+ " " +waitReview+ " "+find+ " "+del;
	}else if(status == 2){//待审核
		if (userType == 7) {
			return edit+ " " +review+ " "+find;
		}
		return review+ " "+find;
	}else if(status == 3){//待发布
		if (userType == 7) {
			return edit+ " " +find+" "+cancel;
		}
		return find+" "+cancel;
	}else if(status == 4){//已发布
		if (userType == 7) {
			return edit+ " " +find;
		}
		return find;
	}
	return find;
}

/**
 * 查看消息
 * @param id
 * @param name
 */
function findMsg(id, type,name){
	window.top.setIframeBox("find-msg-"+id,config.urlMap.find+"?id="+id+"&type="+type,decodeURIComponent(name));
}

/**
 * 编辑消息
 * @param id
 * @param name
 */
function editMsg(id, type,name){
	window.top.setIframeBox("edit-msg-"+id,config.urlMap.edit+"?id="+id+"&type="+type,decodeURIComponent(name));
}

//关闭页面
   $(".closePage").click(function(){
	   window.top.closeActiveIframe("信息列表");
   })

/**
 * 提交审核
 * @param id
 */
function submitReview(id){
	bootbox.confirm("确定需要把编号"+id+"提交审核吗？",function(result){
		if(result){
			$.post(config.urlMap.submitReview,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						customMessageTable.fnDraw();
					})
				}else{
					bootbox.alert("站内信提交审核失败，消息状态可能已经变更，只有已保存状态才可以提交审核。",function(){
						customMessageTable.fnDraw();
					});
				}
			})
		}
	})
}

function review(id){
	boxDialog("站内信审核",function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(config.urlMap.review,{id:id,message:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						customMessageTable.fnDraw();
					})
				}else{
					bootbox.alert("站内信审核失败，消息状态可能已经变更，只有待审核状态才可以审核。",function(){
						customMessageTable.fnDraw();
					});
				}
			})
	})
}

function cancelMsg(id){
	bootbox.confirm("确定需要取消发布站内信编号"+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.cancel,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						customMessageTable.fnDraw();
					})
				}else{
					bootbox.alert("站内信取消发布失败，消息状态可能已经变更，只有待发布状态才可以取消。",function(){
						customMessageTable.fnDraw();
					});
				}
			})
		}
	})
}

$("#msgType").on("change",function(){
		var msgType = $(this).val();
		 var s = document.getElementById("notifyType");
		switch(msgType){
			case "1":
				  $("#notifyType").val("");
	              s.disabled = true;
				return;
			case "3":
				  $("#notifyType").val("");
			 	  s.disabled = false;
				return;
			default:
				  $("#notifyType").val("");
				  s.disabled = false
				return;
		}
	});


function delMsg(id){
	bootbox.confirm("确定需要删除编号"+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.del,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						customMessageTable.fnDraw();
					})
				}else{
					bootbox.alert("删除失败，消息状态可能已经变更，只有在已保存状态才可以删除",function(){
						customMessageTable.fnDraw();
					});
				}
			})
		}
	})
}


function boxDialog(title, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage(),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxMessage(isRadio){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>请填写操作原因：</label>"+
    "<div class='col-md-4'><textarea id='message' name='message' type='text' placeholder='请填写操作原因' class='form-control input-md' style='width: 341px; height: 89px;'></textarea></div></div>";
	 html+="<div class='form-group'><label class='col-md-4 control-label' for='radioStatus'>动作</label> ";
	 html+="<div class='col-md-4'><div class='radio'><label for='radioStatus-1'><input type='radio' name='radioStatus' id='radioStatus-1' value='1' checked='checked'>通过 </label></div>";
	 html+="<div class='radio'><label for='radioStatus-0'><input type='radio' name='radioStatus' id='radioStatus-0' value='0'> 退回 </label></div></div></div>";
    html+="</form></div></div>";
	return html;
}


/**
 * 格式化内容
 * @param str
 * @returns {String}
 */
function formatContent(str){
	var s = removeHTMLTag(str);
	if(s.length > 30){
		s = s.substring(0,30)+"...";
	}
	return s;
}

/**
 * 删除HTML标签
 * @param str
 * @returns
 */
function removeHTMLTag(str) {
    str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
    str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
    str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
    str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
    return str;
}
