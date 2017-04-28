jQuery(function($) {
	

	//表单验证初始化
	var paymentPlatformFrom = $("#paymentPlatform_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	var addformFrom = $("#add_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false,
		dragonfly:true
	});

	var maintenanceForm = $("#maintenance_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	//初始化银行列表
	if($("#bankId option").length == 1) {
		$.post(
			config.urlMap.getBankList,{},function(data){
				if(typeof(data) != 'undefined'){
					for(var i=0; i<data.length; i++) {
						$("#bankId").append("<option value="+data[i].id+">"+data[i].simpleName+" "+data[i].code+"</option>");
					}
				}
			}
		);			
	}
	
	var paymentPlatformTable = $('#paymentPlatform-table-2').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'aaSorting':[[1,"desc"]],
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
				'mDataProp': 'id',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return "<input type='checkbox' name='bankSel' value=" + row.id + ">";
				}
			}
			, {
				'mDataProp': 'id',
				'bSortable': true
			}, {
				'mDataProp': 'platformType',
				'bSortable': true,
				'mRender': function(data) {
					if(data == 1) {return "新浪";}
					else {return "";}
				}
			}, {
				'mDataProp': 'simpleName',
				'bSortable': true
			}, {
				'mDataProp': 'code',
				'bSortable': true
			}, {
				'mDataProp': 'typeLimit',
				'bSortable': true,
				'mRender': function(data) {
					if(data == 1) {return "网银";}
					else if(data == 2) {return "快捷";}
					else {return "";}
				}
			}, {
				'mDataProp': 'singleLimit',
				'bSortable': true
			}, {
				'mDataProp': 'dailyLimit',
				'bSortable': true
			}, {
				'mDataProp': 'minLimit',
				'bSortable': true
			}, {
				'mDataProp': 'serviceStatus',
				'bSortable': true,
				'mRender': function(data) {
					if(data == 1) {return "可用";}
					else if(data == 0) {return "不可用";}
					else {return "";}
				}
			}, {
				'mDataProp': 'remark',
				'bSortable': false
			}, {
				'mDataProp': 'createTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'updateTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(data == "") {
						return "";
					}
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = "<button class='btn btn-xs btn-pink btn-primary editPayment permission-"+config.permission.edit+"' data-id='"+row.id+"'" +
							" data-bankId='"+row.bankId+"' data-bankId='"+row.bankId+"'" +
							" data-platformType='"+row.platformType+"'" +
							//" data-serviceStatus='"+row.serviceStatus+"'" +
							" data-typeLimit='"+row.typeLimit+"'" +
							" data-dailyLimit='"+row.dailyLimit+"'" +
							" data-singleLimit='"+row.singleLimit+"'" +
							" data-minLimit='"+row.minLimit+"'" + 
							" data-remark='"+row.remark+"'>编辑</button> ";
					buttons = buttons + " <button class='btn btn-xs btn-danger btn-primary delPayment permission-"+config.permission.del+"' data-id='"+row.id+"' >删除</button> ";
					if(row.maintenanceContent == '' || row.maintenanceContent == null) {
						buttons = buttons + "<button class='btn btn-xs btn-purple btn-primary addMaint permission-"+config.permission.addMaint+"' data-id='"+row.id+"' >新增维护</button> ";
					} else {
						buttons = buttons + "<button class='btn btn-xs btn-grey btn-primary showMaint permission-"+config.permission.showMaint+"' data-id='"+row.id+"' data-startTimeStr='"+row.startTimeStr+"' data-endTimeStr='"+row.endTimeStr+"' data-maintenanceContent='"+row.maintenanceContent+"' >查看维护</button> ";
						buttons = buttons + "<button class='btn btn-xs btn-info btn-primary editMaint permission-"+config.permission.editMaint+"' data-id='"+row.id+"' data-startTimeStr='"+row.startTimeStr+"' data-endTimeStr='"+row.endTimeStr+"' data-maintenanceContent='"+row.maintenanceContent+"' >编辑维护</button> ";
						buttons = buttons + "<button class='btn btn-xs btn-inverse btn-primary delMaint permission-"+config.permission.delMaint+"' data-id='"+row.id+"' >结束维护</button> ";
					}
					return buttons;
				}
			}
		]
	});
	
	$('table th input:checkbox').on('click', function() {
		var that = this;
		$(this).closest('table').find(
				'tr > td:first-child input:checkbox').each(function() {
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});

	//查询
	$('#query_paymentPlatform').on('click', function() {
		paymentPlatformTable.fnDraw();
	});

	//新增
	$('#new_paymentPlatform').on('click', function() {
		addformFrom.resetForm();
		$(".modalFormTitle").text("新增");
		$("div[act='add']").each(function () {
            $(this).show();
        });
		$("input[label='info']").each(function () {
            $(this).val('');
        });
		$("select[label=info]").find("option[value='-1']").attr("selected",true);
		$('#modal-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//修改
	$("#paymentPlatform-table-2").on("click", '.editPayment', function() {
		addformFrom.resetForm();
		$(".modalFormTitle").text("编辑");
		$("div[act='add']").each(function () {
            $(this).hide();
        });
		var id = $(this).attr("data-id");
		var bankId = $(this).attr("data-bankId");
		var platformType = $(this).attr("data-platformType");
		//var serviceStatus = $(this).attr("data-serviceStatus");
		var typeLimit = $(this).attr("data-typeLimit");
		var dailyLimit = $(this).attr("data-dailyLimit")=="null"?"":$(this).attr("data-dailyLimit");
		var singleLimit = $(this).attr("data-singleLimit")=="null"?"":$(this).attr("data-singleLimit");
		var minLimit = $(this).attr("data-minLimit")=="null"?"":$(this).attr("data-minLimit");
		var remark = $(this).attr("data-remark")=="null"?"":$(this).attr("data-remark");
		$("#id").val(id);
		$("#platformType").find("option[value='"+platformType+"']").attr("selected",true);
		$("#bankId").find("option[value='"+bankId+"']").attr("selected",true);
		//$("#serviceStatus").find("option[value='"+serviceStatus+"']").attr("selected",true);
		$("#typeLimit").find("option[value='"+typeLimit+"']").attr("selected",true);
		$("#dailyLimit").val(dailyLimit);
		$("#minLimit").val(minLimit);
		$("#singleLimit").val(singleLimit);
		$("#remark").val(remark);
		$('#modal-table').modal({
			'show' : true,
			backdrop : true
			
		});
	});
	
	//保存
	$('#save_paymentPlatform').on('click', function() {
		
		if (addformFrom.check(false)) {
			var noticeItem = "";
			var singleLimit = $("#singleLimit").val();
			var dailyLimit = $("#dailyLimit").val();
			var minLimit = $("#minLimit").val();
			var reg=/^([1-9][\d]{0,9}|0)(\.[\d]{1,2})?$/;  
			if(singleLimit.length > 0) {
		        if(!reg.test(singleLimit)){  
		        	noticeItem += "单笔限额 "; 
		        }    
			}
			if(dailyLimit.length > 0) {
		        if(!reg.test(dailyLimit)){  
		        	noticeItem += "每日限额 ";   
		        }    
			}
			if(minLimit.length > 0) { 
		        if(!reg.test(minLimit)){  
		        	noticeItem += "最低支付额度 ";
		        }    
			}
			if(noticeItem != "") {
				bootbox.alert(noticeItem + "金额格式不对!");	
				return;
			}
			$(this).addClass("disabled");
			var that = $(this);
			$('#add_form').xform("post", config.urlMap.save,function(data){
				that.removeClass("disabled");
				if(!data.success){
					bootbox.alert("保存失败",function(){});
				}else{
					$('#modal-table').modal('hide');
					paymentPlatformTable.fnDraw();
				}
	       });
		}
	});
	
	//删除
	$("#paymentPlatform-table-2").on("click", '.delPayment', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if(!result)return;
			$.post(config.urlMap.del, {id:id}, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！",function(){
						paymentPlatformTable.fnDraw();
					});
				} else {
					boobbox.alert("删除失败！");
				}
			});
		});
	});
	
	
	//新增维护公告
	loadUmeditor('', 'maintenanceContent');
	$("#paymentPlatform-table-2").on("click", '.addMaint', function() {
		var id = $(this).attr("data-id");
		maintenanceForm.resetForm();
		var edit = UE.getEditor('maintenanceContent');
		edit.setContent('');
		$(".modalFormTitle").text("新增维护公告");
		$("#maintenanceId").val(id);
		$('#maintenance-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//保存维护公告
	$('#save_maintenance').on('click', function() {
		if (maintenanceForm.check(false)) {
			var edit = UE.getEditor('maintenanceContent');
			//console.log(edit.getContent());
			if(!(edit.getContent().length > 0)) {
				bootbox.alert("维护公告为空！");
				return;
			}
			var content = $("#maintenanceContent").val();
			$('#maintenance_form').xform("post", config.urlMap.saveMaint,function(data){
		 		if(!data.success){
		 			bootbox.alert("保存失败！");
		  	  	}else{
		  			bootbox.alert("保存成功",function(){
		  				paymentPlatformTable.fnDraw();
		  			});
		  	  	}
		    });
			$('#maintenance-table').modal('toggle');
		} 
	});
	
	//查看维护
	$("#paymentPlatform-table-2").on("click", '.showMaint', function() {
		var id = $(this).attr("data-id");
		var startTime = $(this).attr("data-startTimeStr");
		var endTime = $(this).attr("data-endTimeStr");
		var maintenanceContent = $(this).attr("data-maintenanceContent");
		maintenanceForm.resetForm();
		$(".modalFormTitle").text("查看维护公告");
		$("#show_startTime").text(startTime == ''?'':startTime);
		$("#show_endTime").text(endTime == ''?'':endTime);
		$("#show_maintenanceContent").text(removeHTMLTag(maintenanceContent));
		$('#show-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//编辑维护
	$("#paymentPlatform-table-2").on("click", '.editMaint', function() {
		var id = $(this).attr("data-id");
		var startTime = $(this).attr("data-startTimeStr");
		var endTime = $(this).attr("data-endTimeStr");
		var maintenanceContent = $(this).attr("data-maintenanceContent");
		maintenanceForm.resetForm();
		$(".modalFormTitle").text("查看维护公告");
		$("#maintenanceId").val(id);
		$("#startTimeStr").val(startTime == ''?'':startTime);
		$("#endTimeStr").val(endTime == ''?'':endTime);
		$("#maintenanceContent").val(maintenanceContent == ''?'':maintenanceContent);
		var edit = UE.getEditor('maintenanceContent');
		edit.setContent(maintenanceContent);
		$('#maintenance-table').modal({
			'show' : true,
			backdrop : true
		});
	});
	
	//结束维护
	$("#paymentPlatform-table-2").on("click", '.delMaint', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要结束维护吗?", function(result) {
			if(!result)return;
			$.post(config.urlMap.delMaint, {id:id}, function(data) {
				if (data.success) {
					bootbox.alert("操作成功！",function(){
						paymentPlatformTable.fnDraw();
					});
				} else {
					boobbox.alert("操作失败！");
				}
			});
		});
	});
	
	function resetSelect() {
		$("select[label=info]").each(function () {
			if($(this)[0].id != "serviceStatus") {
				var val = $("#"+$(this)[0].id+" option:selected").val();
				var text = $("#"+$(this)[0].id+" option:selected").text();
				$(this).empty();
				$("#"+$(this)[0].id).append("<option value=-1>请选择</option>");
				$("#"+$(this)[0].id).append("<option value="+val+">"+text+"</option>");
				$("#"+$(this)[0].id).find("option[value='"+val+"']").attr("selected",true);
			}
		});
	}
	
	function loadUmeditor(content, obj) {
        var url = config.urlMap.ueditor;
        $.getScript(url + 'ueditor.config.js');
        $.getScript(url + 'ueditor.all.min.js', function() {
            //实例化编辑器
            postEditor =UE.getEditor(obj, {
                initialFrameWidth: 518,
                initialFrameHeight: 350,
                initialContent:content,
				toolbars: [
     					['fullscreen', 'source', 'undo', 'link', 'unlink','forecolor', 'italic', 'bold', 'underline' //字体颜色

				]]
            });
        });
	}
        
});

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
