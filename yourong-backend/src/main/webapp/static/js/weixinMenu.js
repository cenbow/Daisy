var zTree, rMenu,treeNode,level_id,id,tem_id,type,oTable1,oTable2,weixinKeyForm,weixinAttenForm;
jQuery(function($) {
	initTable1();
	weixinKeyForm = $("#weixin_key_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	weixinAttenForm = $("#weixin_atten_from").Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});
	
	 
	oTable2 = $('#weixin-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'aaSorting':[[0,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.ajaxAtten,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp' : 'id'
		}, {
			'mDataProp' : 'title',
			'bSortable' : false
		}, {
			'mDataProp' : 'templateType',
			'bSortable' : false,
			'mRender':function(data, type, row){
				if(row.templateType=="1"){
					return "文字";
				}else if(row.templateType=="2"){
					return "图文";
				}else if(row.templateType=="3"){
					return "视频";
				}
			}
		},{
			'mDataProp' : 'url',
			'bSortable' : false
		},{
			'mDataProp' : 'textDescribe',
			'bSortable' : false
		}, {
			'mDataProp' : 'status',
			'bSortable' : false,
			'mRender':function(data, type, row){
				if(row.status=="1"){
					return "正常";
				}else if(row.status=="0"){
					return "停用";
				}
			}
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation_atten(row);
			}
		}
		]
	});
});
//关键字回复操作
function getAllOperation(row) {
	var edit = "<button class='btn btn-xs btn-info weixin-show permission-" + config.permission.show +"' data-id='" + row.id + "' data-templateType ='" + row.templateType + "'>" +
		"<i class='icon-edit bigger-120'>编辑</i></button>"; //编辑
	var del = "<button class='btn btn-xs btn-danger debt-del permission-" + config.permission.del +"' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
			return  edit + " " + del;
	}
    function  initTable1(){
    	oTable1 = $('#weixin-table-1').dataTable({
    		'bFilter' : false,
    		'bProcessing' : true,
    		'bSort' : true,
    		'aaSorting':[[1,"asc"]],
    		'bServerSide' : true,
    		'fnServerParams' : function(aoData) {
    			getAllSearchValue(aoData);
    		},
    		'sAjaxSource' : config.urlMap.ajax,
    		'aoColumns' : [ {
    			'mDataProp' : 'id',
    			'bSortable' : true,
    			'mRender' : function(data, type, row) {
    				return "<input type='checkbox' value=" + row.id + ">";
    			}
    		}, {
    			'mDataProp' : 'id'
    		}, {
    			'mDataProp' : 'name',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'templateType',
    			'bSortable' : false,
    			'mRender':function(data, type, row){
    				if(row.templateType=="1"){
    					return "文字";
    				}else if(row.templateType=="2"){
    					return "图文";
    				}
    			}
    		}, {
    			'mDataProp' : 'title',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'url',
    			'bSortable' : false,
    			'mRender':function(data, type, row){
    				if(row.url.length>20){
    					return row.url.substring(0,19);
    				}else{
    					return row.url;
    				}
    			}
    		}, {
    			'mDataProp' : 'keyword1',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'keyword2',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'keyword3',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'keyword4',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'keyword5',
    			'bSortable' : false
    		},{
    			'mDataProp' : 'textDescribe',
    			'bSortable' : false
    		}, {
    			'mDataProp' : 'remarks',
    			'bSortable' : false
    		}, {
    			'mDataProp': 'operation',
    			'bSortable': false,
    			'mRender': function(data, type, row) {
    				return getAllOperation(row);
    			}
    		}
    		]
    	});
    }
	function getAllOperation_atten(row) {
		//var detail = "<button class='btn btn-xs btn-success debt-detail permission-" + config.permission.detail + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'><i class='icon-zoom-in bigger-130'>详情</i></button>"; //详情
		var edit = "<button class='btn btn-xs btn-info weixin-show permission-" + config.permission.show +"' data-id='" + row.id+ "' data-templateType ='" + row.templateType + "'>" +
			"<i class='icon-edit bigger-120'>编辑</i></button>"; //编辑
		
		var open = "<button class='btn btn-xs btn-danger weixin-open permission-" + config.permission.ajaxOpen +"' data-id='" + row.id + "'>" +
		"<i class=' bigger-120'>启用</i></button>"; //开启
		
		var close = "<button class='btn btn-xs btn-info weixin-ajaxClose btn-warning permission-" + config.permission.ajaxClose +"' data-id='" + row.id + "'>" +
		"<i class=' bigger-120'>停用</i></button>"; //关闭
		
		var del = "<button class='btn btn-xs btn-danger debt-del permission-" + config.permission.del +"' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
				return  edit + " " +open+" "+close+" " + del;
		}
//开启
$('#weixin-table-2').on('click', '.weixin-open', function() {
	var id = $(this).attr("data-id");
	$.ajax({
	url : config.urlMap.ajaxOpen,
	data:{'id':id},
	dataType:"json",
	success : function(data) {
		if(data.success){
			bootbox.alert("启用成功！");
			oTable2.fnDraw();
		}else{
			bootbox.alert("启用失败！");
		}
	},
	error : function(e) {
		bootbox.alert("出错了！");
	}
});})
$('#weixin-table-2').on('click', '.weixin-ajaxClose', function() {
	var id = $(this).attr("data-id");
	$.ajax({
	url : config.urlMap.ajaxClose,
	data:{'id':id},
	dataType:"json",
	success : function(data) {
		if(data.success){
			bootbox.alert("停用成功！");
			oTable2.fnDraw();
		}else{
			bootbox.alert("停用失败！");
		}
	},
	error : function(e) {
		bootbox.alert("出错了！");
	}
});})
//修改关键字回复模板
$('#weixin-table-1').on('click', '.weixin-show', function() {
	var id = $(this).attr("data-id");
	var templateType = $(this).attr("data-templateType");
	if (templateType == 1) {
		$('#ifshow').hide();
	}else{
		$('#hf_content').hide();
	}
	window.top.setIframeBox("show-" + id, config.urlMap.show + id , "修改");
});
//修改首次关注模板
$('#weixin-table-2').on('click', '.weixin-show', function() {
	var id = $(this).attr("data-id");
	var templateType = $(this).attr("data-templateType");
	if (templateType == 1) {
		$('#ifhide').hide();
	}else{
		$('#s_text').hide();;
	}
	window.top.setIframeBox("show-" + id, config.urlMap.showAtten + id , "修改");
});
//删除关键字回复模板
$('#weixin-table-1').on('click', '.debt-del', function() {
	var id = $(this).data("id");
	bootbox.confirm("你确定要删除吗?", function(result) {
		if(!result)return;
		$.post(config.urlMap.delTemplete + id, function(data) {
			if (data.success) {
				bootbox.alert("删除成功！",function(){
					oTable1.fnDraw();
				});
			} else {
				boobbox.alert("删除失败！");
			}
		});
	});
	
});
//删除首次关注模板信息
$('#weixin-table-2').on('click', '.debt-del', function() {
	var id = $(this).data("id");
	bootbox.confirm("你确定要删除吗?", function(result) {
		if(!result)return;
		$.post(config.urlMap.delTemplete + id, function(data) {
			if (data.success) {
				bootbox.alert("删除成功！",function(){
					oTable2.fnDraw();
				});
			} else {
				boobbox.alert("删除失败！");
			}
		});
	});
	
})
function findDebtById(id) {
	$.post(config.urlMap.find + id, function(data) {
		if (!!data.winxinMenu) {
			eachDebtField(data.winxinMenu);
			$("select[name='templateType']").attr("disabled","disabled");
		}
		if (!!data.lenderAttachments) {
			eachAttachements(data.lenderAttachments, "dropzone-weixin-lender");
		}
	});
}
//保存关键字回复模板信息
$("#save_weixin_key").on('click', function() {
	$('#weixin_key_form div:hidden').each(function() {
		$(this).find('input,textarea').removeAttr("datatype");
	});
	 var templateType = $("#templateType").val();
	 //alert(templateType);
	/* if(templateType==1){
		 $("#form-field-title").removeAttr("datatype");
		 $("#form-field-url").removeAttr("datatype");
	 }*/
	 if(templateType==2){
		 var img = $("#j-json-dropzone-weixin-lender").val();
		   if(img=="" || img.length<1){
			   bootbox.alert("请上传图片");
			   return;
		   }
	 }
	if (weixinKeyForm.check(false)) {
		//alert();
	var flag="1";
		var options = {
			//beforeSubmit : checkform,
			url : config.urlMap.save+flag,
			type : "post",
			resetForm : true,
			success : function(data) {
				if (!data.success) {
					var flag = "保存失败!";
					bootbox.alert(flag,function(){
					});
				} else {
					bootbox.alert("保存成功!", function() {
						oTable1.fnDraw();
						//window.top.closeActiveIframe()
						window.top.closeActiveIframe("微信管理");
						//weixinKeyForm.resetForm();
						//weixinAttenForm.resetForm();
					});
					
				}
			}
		};
		$('#weixin_key_form').ajaxSubmit(options);
	}
	});

var updateweixinAttenForm = $("#update_weixin_key_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
//更新关键字回复模板信息
$("#update_weixin_key").on('click', function() {
	$('#update_weixin_key_form div:hidden').each(function() {
		$(this).find('input,textarea').removeAttr("datatype");
	});
	 var templateType = $("#templateType").find("option:selected").val();
	 if(templateType==2){
		 var img = $("#j-json-dropzone-weixin-lender").val();
		   if(img=="" || img.length<1){
			   bootbox.alert("请上传图片");
			   return;
		   }
	 }
	if(updateweixinAttenForm.check(false)){
		var flag="1";
		var options = {
			//beforeSubmit : checkform,
			url : config.urlMap.save+flag,
			type : "post",
			resetForm : true,
			success : function(data) {
				if (data.success) {
					bootbox.alert("更新成功!", function() {
						//initTable1();
						oTable1.fnDraw();
						window.top.closeActiveIframe("微信管理");
					});
				} else {
					var flag = "更新失败!";
					bootbox.alert(flag,function(){
					});
				
				}
			}
		};
		$('#update_weixin_key_form').ajaxSubmit(options);
	}
	
	});
function removeHiddenVaildation() {
	$('#weixin_atten_from div:hidden').each(function() {
		$(this).find('input,textarea').removeAttr("datatype");
	});
}
//保存首次关注模板信息 
$("#save_weixin_atten").on('click', function() {
	 var templateType = $("#templateType_atten").val();
	 if(templateType==2||templateType==3){
		 var img = $("#j-json-dropzone-weixin-lender").val();
		   if(img=="" || img.length<1){
			   bootbox.alert("请上传图片");
			   return;
		   }
	 }
	removeHiddenVaildation();
	if (weixinAttenForm.check(false)) {
	var flag="2";
	var options = {
		beforeSubmit : checkform2,
		url : config.urlMap.save+flag,
		type : "post",
		resetForm : true,
		success : function(data) {
			if (!data.success) {
				var flag = "更新失败!";
				bootbox.alert(flag,function(){
				});
			} else {
				bootbox.alert("保存成功!", function() {
					window.top.closeActiveIframe("微信管理");
					oTable2.fnDraw();
				});
				
			}
		}
	};
	$('#weixin_atten_from').ajaxSubmit(options);
	}
});
var updateForm = $("#update_weixin_atten_from").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
//更新首次关注模板信息
$("#update_weixin_atten").on('click', function() {
	var templateType = $("#templateType").find("option:selected").val();
	 if(templateType==2){
		 var img = $("#j-json-dropzone-weixin-lender").val();
		   if(img=="" || img.length<1){
			   bootbox.alert("请上传图片");
			   return;
		   }
	 }
	var flag="2";
	$('#update_weixin_atten_from div:hidden').each(function() {
		$(this).find('input,textarea').removeAttr("datatype");
	});
	if(updateForm.check(false)){
	var options = {
		//beforeSubmit : checkform2,
		url : config.urlMap.save+flag,
		type : "post",
		resetForm : true,
		success : function(data) {
			if (!data.success) {
				var flag = "更新失败!";
				bootbox.alert(flag,function(){
				});
			} else {
				bootbox.alert("更新成功!", function() {
					window.top.closeActiveIframe("微信管理");
					oTable2.fnDraw();
				});
				
			}
		}
	};
	$('#update_weixin_atten_from').ajaxSubmit(options);
	}
});
function eachAttachements(attachments, fileId) {
	var customDropzone = Dropzone.forElement("#"+fileId);
	$.each(attachments, function(n, v) {
		addImageToDropzone(customDropzone, v);
	});
}
function eachDebtField(winxinMenu) {
	$.each(winxinMenu, function(name, value) {
		if (name == "keyword1") {
			$("#form-field-keyword1").val(value)
		} else if (name == "title") {
			$("#form-field-title").val(value);
		} else if (name == "startDate") {
			$("#form-field-startDate").val(formatDate(value));
			$('#start_date').html(formatDate(value));
		} else if (name == "endDate") {
			$("#form-field-endDate").val(formatDate(value));
			$('#end_date').html("—"+formatDate(value));
		} else if (name == "url") {
			$("#form-field-url").val(value);
		} else if (name == "name") {
			$("#form-field-name").val(value);
		}else if (name == "keyword2") {
			$("#form-field-keyword2").val(value)
		}else if (name == "keyword3") {
			$("#form-field-keyword3").val(value)
		}else if (name == "keyword4") {
			$("#form-field-keyword4").val(value)
		}else if (name == "keyword5") {
			$("#form-field-keyword5").val(value)
		} else if (name == "textDescribe") {
			$("#form-field-textDescribe").val(value);
		} else if (name == "templateType") {
			if(value==1){
				$('#detail_weixin').hide();
				$('#detail_weixin_atten').hide();
				$('#ifshow').hide();
				$('#ifhide').hide();
			}else if(value==2){
				$('#s_text').hide();
				$('#hf_content').hide();
			}
			$("#templateType").find("option[value='"+value+"']").attr("selected",true);
		}else if (name == "status") {
			$("#status").find("option[value='"+value+"']").attr("selected",true);
		}  
	});
}
var zSetting = {
		view: {
			addHoverDom: addHoverDom,
			removeHoverDom:removeHoverDom,
			selectedMulti: false
	    },edit: {
			enable: true,
			showRemoveBtn: showRemoveBtn
		},
		check : {
			enable : true,
			chkboxType : {
				"Y" : "ps",
				"N" : "ps"
			}
	},data : {
		simpleData : {
			enable : true
		}
	},callback: {
		onRightClick: OnRightClick,
		beforeEditName: showRenameBtn,
		beforeRemove: beforeRemove


	}
};
function removeHoverDom(treeId, treeNode) {
	$("#addBtn_"+treeNode.tId).unbind().remove();
};
function showRemoveBtn(treeId, treeNode) {
	return !treeNode.isFirstNode;
}
function showRenameBtn(){
	$(".modalFormTitle").text("编辑微信菜单");
    $("#weixinMenu_form").xform("load", (config.urlMap.show + id));
    $('#modal-table').modal('show');
    return false;
}
function addHoverDom(treeId, treeNode) {
	level=treeNode.level;
	id=treeNode.id;
	tem_id=treeNode.pId;
	level_id=level+id;
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		+ "' title='add node' onfocus='this.blur();'></span>";
	var editStr = "<button type='button' class='button add' id='addBtn_" +treeNode.tId+ "' title='"+treeNode.name+"' onfocus='this.blur();'></button>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.tId);
	if (btn){
		btn.bind("click", function(){
			$(".modalFormTitle").text("添加微信菜单");
			$('#modal-table').modal({
				'show' : true
			});
		});
	} 
};

$('#GetMenu').on('click', function() {
	$.ajax({
		url : config.urlMap.ajaxTree,
		type : "GET",
		success : function(data) {
			$("#remarks").val(data.remarks);
		},
		error : function(e) {
			bootbox.alert("出错了！");
		}
	});
});

/*function getWeixinMenu(){
	$.ajax({
		url : config.urlMap.ajaxTree,
		type : "GET",
		success : function(data) {
			$("#remarks").val(data.remarks);
		},
		error : function(e) {
			bootbox.alert("出错了！");
		}
	});
}*/

$('#publishMenu').on('click', function() {
	var options = {
			url : config.urlMap.ajaxPublish,
			type : "post",
			resetForm : true,
			success : function(data) {
				if(data.success){
					bootbox.alert("发布成功！");
				}else{
					bootbox.alert("发布失败！");
				}
			}
		};
		$('#weixin_form').ajaxSubmit(options);
});
function publish(){
	var options = {
			beforeSubmit : checkform,
			url : config.urlMap.ajaxPublish,
			type : "post",
			resetForm : true,
			success : function(data) {
				if(data.success){
					bootbox.alert("发布成功！");
				}else{
					bootbox.alert("发布失败！");
				}
			}
		};
		$('#weixin_form').ajaxSubmit(options);
}

function checkform(formData, jqForm, options) {
	return $("#update_weixin_key_form").valid();
}
function checkform2(formData, jqForm, options) {
	return $("#weixin_atten_from").valid();
}
		function OnRightClick(event, treeId, treeNode) {
			level=treeNode.level;
			id=treeNode.id;
			tem_id=treeNode.pId;
			level_id=level+id;
			if(level==1){
				$("#m_add_next").hide();
			}
			if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
				zTree.cancelSelectedNode();
				showRMenu("root", event.clientX, event.clientY);
			} else if (treeNode && !treeNode.noR) {
				zTree.selectNode(treeNode);
				showRMenu("node", event.clientX, event.clientY);
			}
		}

		function showRMenu(type, x, y) {
			$("#rMenu ul").show();
			if (type=="root") {
				$("#m_del").hide();
				$("#m_check").hide();
				$("#m_unCheck").hide();
			} else {
				$("#m_del").show();
				$("#m_check").show();
				$("#m_unCheck").show();
			}
			rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});

			$("body").bind("mousedown", onBodyMouseDown);
		}
		function hideRMenu() {
			if (rMenu) rMenu.css({"visibility": "hidden"});
			$("body").unbind("mousedown", onBodyMouseDown);
		}
		function onBodyMouseDown(event){
			if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
				rMenu.css({"visibility" : "hidden"});
			}
		}
		function addTreeNode(tem) {
			type=tem;
			hideRMenu();
			$(".modalFormTitle").text("添加微信菜单");
			$('#modal-table').modal({
				'show' : true
			});
		}
		//关键字回复增加模板
		$('#addWeixin').on('click', function() {
			var type=2;
			var templateType=1;
			showkeyTemplate(templateType);
			window.top.setIframeBox("show-" + id, config.urlMap.add+type , "添加模板");
		//function addTemplate(){
			/*var templateType = $('#templateType').val();
			templateType=1;
			weixinTemplateTypeShowAtten(templateType);
			weixinKeyForm.resetForm();
			$('#modal-table').modal({
				'show' : true
			});*/
		});
		function showContent(){
			$('#ifshow').hide();
			$('#template_img').hide();
		}
		function showAttenContent(){
			$('#detail_weixin_atten').hide();
			$('#ifhide').hide();
			$('#s_text').show();
		}
		$("#templateType_atten").change(function(){
			var type = $(this).val();
			if(type==2){
				$('#detail_weixin_atten').show();
				$('#ifhide').show();
				$('#s_text').hide();
			}
			if(type==1){
				showAttenContent();
			}
			if(type==3){
				$('#detail_weixin_atten').show();
				$('#ifhide').show();
				$('#s_text').hide();
			}
		});
		//关键字回复
		$("#templateType").change(function(){
			var type = $(this).val();
			if(type==2){
				$('#template_img').show();
				$('#ifshow').show();
				$('#hf_content').hide();
			}
			if(type==3){
				$('#weixin_img').show();
				$('#hideTitle').show();
				$('#hideUrl').show();
			}
			if(type==1){
				showContent();
				$('#hf_content').show();
			}
		});
		//关键字回复
		function showkeyTemplate(templateType) {
			if (templateType == 1) {
				$('#ifshow').hide();
				
				//$('#showTitle').hide();
				//$('#showUrl').hide();
				//$('#showText').show();
			}
		}
		//}
		//首次关注增加模板
		$('#addWeixinAtten').on('click', function() {
			var type=1;
			var templateType=1;
			weixinTemplateTypeShow(templateType);
			window.top.setIframeBox("show-" + id, config.urlMap.add+type , "添加模板");
		//function addTemplate_s(){
			/*var templateType = $('#templateType_atten').val();
			templateType=1;
			weixinTemplateTypeShow(templateType);
			weixinAttenForm.resetForm();
			$('#modal-table_2').modal({
				'show' : true
			});*/
		//}
		});
		//首次关注
		function weixinTemplateTypeShow(type) {
			if (type == 1) {
				$('#weixin_img_atten').hide();
				$('#hideTitle').hide();
				$('#hideUrl').hide();
			}
		}
		//首次关注
		/*$("#templateType_atten").change(function(){
			var type = $(this).val();
			if(type==2){
				$('#weixin_img_atten').show();
				$('#hideTitle').show();
				$('#hideUrl').show();
				$('#h_content').hide();
				//$("#form-field-title").attr("datatype","*");
				//$("#form-field-url").attr("datatype","*");
			}
			if(type==1){
				$('#weixin_img_atten').hide();
				//$("#form-field-title").removeAttr("datatype");
				//$("#form-field-url").removeAttr("datatype");
				$('#hideTitle').hide();
				$('#hideUrl').hide();
				$('#h_content').show();
			}
			if(type==3){
				$('#weixin_img_atten').show();
				$('#hideTitle').show();
				$('#hideUrl').show();
				//$("#form-field-title").attr("datatype","*");
				//$("#form-field-url").attr("datatype","*");
			}
		});*/
		function editTreeNode(){
				$(".modalFormTitle").text("编辑微信菜单");
		            $("#weixinMenu_form").xform("load", (config.urlMap.show + id));
		            $('#modal-table').modal('show');
		        }
		function beforeRemove(treeId, treeNode){
			if(treeNode.isParent){
				var id=treeNode.id;
				var ids = [];
				var children=treeNode.children;
				ids.push(treeNode.id);
				$.each(children,function(k,v){
					ids.push(v.id);
				})
				bootbox.confirm("你确定要删除吗?", function(result) {
			if (result) {
				$.post(config.urlMap.delet, {
					"id" : ids
				}, function(data) {
					console.log(data);
				});
			}
		});
						$.post(config.urlMap.delet, {
							"id" : ids
						}, function(data) {
							console.log(data);
						});
			}else{
				return confirm("确认要删除吗？",function(result){
					if (result) {
						$.post(config.urlMap.delet, {
							"id" : id
						});
					}
				});  
			
			}
		}
		function removeTreeNode() {
			hideRMenu();
			var nodes = zTree.getSelectedNodes();
			alert(nodes.length);
			if (nodes && nodes.length>0) {
				if (nodes[0].children && nodes[0].children.length > 0) {
					var msg = "要删除的节点是父节点，如果删除将连同子节点一起删掉。\n\n请确认！";
					if (confirm(msg)==true){
						zTree.removeNode(nodes[0]);
					}
				} else {
					zTree.removeNode(nodes[0]);
				}
			}
		}
		
		$('#delete_sysOffice').on('click', function() {
			var ids = [];
			$('table tr > td input:checked').each(function() {
				ids.push($(this).val());
			});
			if (ids.length == 0) {
				bootbox.alert("请选择数据！");
				return;
			}
			bootbox.confirm("你确定要删除吗?", function(result) {
				if (result) {
					$.post(config.urlMap.delet, {
						"id" : ids
					}, function(data) {
						console.log(data);
						oTable1.fnDraw();
					});
				}
			});
		});
		
		function checkTreeNode(checked) {
			var nodes = zTree.getSelectedNodes();
			if (nodes && nodes.length>0) {
				zTree.checkNode(nodes[0], checked, true);
			}
			hideRMenu();
		}
		function resetTree() {
			hideRMenu();
			$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		}

