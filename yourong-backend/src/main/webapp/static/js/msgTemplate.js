var messageListTemplateTable;
$(function(){
	//取消core.js绑定的事件
	$(".table").off("click","tbody>tr");
	$(".table").off("click","input[type=checkbox]");
	
	messageListTemplateDataTable();
	
	//表单验证初始化
	var mailTemplate = $("#msg-mailTemplate-form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	$("#save_mailTemplate").on("click",function(){
		if(mailTemplate.check(false)) {
			if($(this).hasClass("btn-primary")){
				$(this).removeClass("btn-primary");
				$(this).text("请稍候...");
				$("#msg-mailTemplate-form").xform("post", config.urlMap.saveMailTemplate,function(data){
					if(!data.success){
						bootbox.alert("保存邮件模板失败");
					}else{
						bootbox.alert("保存邮件模板成功",function(){
							window.top.closeActiveIframe("消息模板配置");
						});
					}
					$("#save_mailTemplate").addClass("btn-primary");
					$("#save_mailTemplate").text("保存");
				});
			}
		}
	})
})
/**
 * 查看详情
 * @param id
 * @param name
 */
function showTemplate(id,name){
	window.top.setIframeBox("show-template-"+id,config.urlMap.find+"?id="+id, name);
}

$("#msg-template-table").on("click",".edit-msg-template",function(){
	var templateId = $(this).data("id");
	var name = $(this).data("name");
	showTemplate(templateId, name);
})

$("#msg-template-table").on("click",".cancel-msg-template",function(){
	var isCheck = $(this).is(":checked");
	var checkBoxObj = $(this);
	if(isCheck){
		bootbox.confirm("确定需要启用消息模板吗？",function(result){
			if(result){
				 $.post(config.urlMap.updateStatus,{id:checkBoxObj.data("id"),status:1},function(data){
					if(!data.success){
						bootbox.alert("操作失败，请稍后重试！");
					}
				})
			}else{
				document.getElementById("tpl-"+checkBoxObj.data("id")).checked = false;
			}
		})
	}else{
		bootbox.confirm("确定需要取消消息模板吗",function(result){
			if(result){
				 $.post(config.urlMap.updateStatus,{id:checkBoxObj.data("id"),status:0},function(data){
					if(!data.success){
						bootbox.alert("操作失败，请稍后重试！");
					}
				})
			}else{
				document.getElementById("tpl-"+checkBoxObj.data("id")).checked = true;
			}
		})
	}
})

$("#update_message").click(function(){
	if($(this).hasClass("btn-primary")){
		$(this).removeClass("btn-primary");
		$(this).text("请稍候...");
		 $('#msg-template-form').xform("post", config.urlMap.update,function(data){
	    	   if(!data.success){
	    		   bootbox.alert(data.resultCodeEum[0].msg);
	     	  }else{
	     		  bootbox.alert("保存消息成功",function(){
	     		  });
	     	  }
	    	   $("#update_message").addClass("btn-primary");
	    	   $("#update_message").text("保存");
	       });
	}
})


function messageListTemplateDataTable(){
	messageListTemplateTable = $('#msg-template-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
//        	getSearchValue("#customMsgSearchForm",aoData);
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'templateName',
            'bSortable' : true
        },{
			'mDataProp' : 'mobileMsgId',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return getT(row.mobileMsgId,'mobile',row);
            }
		},{
			'mDataProp' : 'emailMsgId',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return getT(row.emailMsgId,'email',row);
            }
		},{
			'mDataProp' : 'msgStatus',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return getT(row.msgId,'msg',row);
            }
		}]
		
	});//dataTable
}


function getT(id,type,row){
	if(id > 0){
		var isCheck="";
		var x = "";
		if(type=="mobile"){
			if(row.mobileMsgStatus == 1)
				isCheck = "checked = checked";
			 x="<input type='checkbox' class='cancel-msg-template' id='tpl-"+id+"' data-id='"+id+"' name='mobileMsg-"+id+"' "+isCheck+">";
				x+="<span class='edit-msg-template' data-id='"+id+"' data-name='"+row.templateName+"-手机短信'> 编辑模板</span>";
		}else if(type=="email"){
			if(row.emailMsgStatus == 1)
				isCheck = "checked = checked";
			 x="<input type='checkbox' class='cancel-msg-template' id='tpl-"+id+"' data-id='"+id+"' name='emailMsg-"+id+"' "+isCheck+">";
			if(row.templateType == 1 || row.templateType == 0) {
				x+="<span class='edit-mail-template' data-id='"+id+"' data-name='"+row.templateName+"-电子邮件'> 编辑模板</span>";
			} else {
				x+="<span class='edit-msg-template' data-id='"+id+"' data-name='"+row.templateName+"-电子邮件'> 编辑模板</span>";
			}
				x+="<span class='del-mail-template' data-id='"+id+"' data-name='"+row.templateName+"-电子邮件'> 删除模板</span>";
		}else if(type="msg"){
			if(row.msgStatus == 1)
				isCheck = "checked = checked";
			 x="<input type='checkbox' class='cancel-msg-template' id='tpl-"+id+"' data-id='"+id+"' name='msgMsg-"+id+"' "+isCheck+"/>";
				x+="<span class='edit-msg-template' data-id='"+id+"' data-name='"+row.templateName+"-站内短信'> 编辑模板</span>";
		}
		return x;
	}else{
		return "编辑模板";
	}
}

$("#new_emailTemplate").on("click",function(){
	window.top.setIframeBox("show-template-999",config.urlMap.eidtEmailTemplate, "新增邮件模板");
})

$("#msg-template-table").on("click",".edit-mail-template",function(){
	var templateId = $(this).data("id");
	var name = $(this).data("name");
	window.top.setIframeBox("show-template-"+templateId,config.urlMap.eidtEmailTemplate+"?id="+templateId, name+"-邮件模板");
})

$("#msg-template-table").on("click",".del-mail-template",function(){
	var templateId = $(this).data("id");
	bootbox.confirm("你确定要删除吗?", function(result) {
		if(!result)return;
		$.post(config.urlMap.del, {id:templateId}, function(data) {
			if (data.success) {
				bootbox.alert("删除成功！",function(){
					$('#msg-template-table').dataTable().fnDraw();
				});
			} else {
				boobbox.alert("删除失败！");
			}
		});
	});
})
