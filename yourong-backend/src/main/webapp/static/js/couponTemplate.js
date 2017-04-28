jQuery(function($) {
	//表单验证初始化
	var couponTemplateForm = $("#couponTemplate_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	var smsForm=$("#couponTemplateSMS_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	})

	//印刷
	var printCouponTemplateForm = $('#print_couponTemplate_form').Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	// 获取操作按钮
	function getAllOperation(row) {
		var status = row.status;
		var sort=row.sortTime;
		var printList = "<button class='btn btn-xs btn-success couponTemplate-printIndex permission-" + config.permission.printAjax + "'  data-id='" + row.id + "' data-couponType ='" + row.couponType + "'><i class='icon-zoom-in bigger-130'>查看记录</i></button>"; // 详情
		var print = "<button class='btn btn-xs btn-light couponTemplate-print permission-" + config.permission.print + "'  data-id='" + row.id + "'><i class='icon-print bigger-120'>印刷</i></button>"; // 印刷
		var edit = "<button class='btn btn-xs btn-info couponTemplate-edit permission-" + config.permission.show + "'  data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
		var del = "<button class='btn btn-xs btn-danger couponTemplate-del permission-" + config.permission.del + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		var top="<button class='btn btn-xs btn-info couponTemplate-top' data-id='" + row.id + "'><i>置顶</i></button>";
		var canceltop="<button class='btn btn-xs btn-info couponTemplate-canceltop' data-id='" + row.id + "'><i>取消置顶</i></button>";
		var sms="<button class='btn btn-xs btn-yellow couponTemplate-sms' data-id='" + row.id + "'><i class='icon-zoom-in bigger-130'>短信提醒</i></button>";

		var btn='';
		switch (status) {
			case 0: // 未使用
				btn= print + edit + del;
				break;
			case 1: // 已使用
				btn= print + printList;
			default:
				btn= print + printList;
		}
		if (sort==null){
			btn=btn + top;
		}else {
			btn=btn + canceltop;
		}
		btn=btn+sms;
		return btn;
	}

	/**
	 * 现金券表
	 */
	var couponTemplateCashTable = $('#couponTemplateCash-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_couponType",
				"value": "1"
			});
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp': 'id'
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'amount',
			'bSortable': true,
			'mRender': function(data, type, row) {
				return Number(data).toFixed(2);
			}
		}, {
			'mDataProp': 'vaildCalcType',
			'bSortable': true,
			'mRender': function(data, type, row) {
				switch (data) {
					case 0:
						return "长期有效";
						break;
					case 1:
						return formatDate(row.startDate) + "到" + formatDate(row.endDate);
					default:
					case 2:
						return "领取后" + row.days + "天有效";
						break;
				}
			}
		}, {
			'mDataProp': 'webScope',
			'bSortable': false,
			'mRender':function(data, type, row){
				if((row.webScope=='1'||row.webScope==null)&&(row.wapScope=='1'||row.wapScope==null)&&(row.appScope=='1'||row.appScope==null)){
					return "通用";
				}else if((row.webScope=='1'||row.webScope==null)&&(row.wapScope=='1'||row.wapScope==null)){
					return "web、wap";
				}else if((row.webScope=='1'||row.webScope==null)&&(row.appScope=='1'||row.appScope==null)){
					return "web、app";
				}else if((row.wapScope=='1'||row.wapScope==null)&&(row.appScope=='1'||row.appScope==null)){
					return "wap、app";
				}else if(row.webScope=='1'||row.webScope==null){
					return "web";
				}else if(row.wapScope=='1'||row.wapScope==null){
					return "wap";
				}else if(row.appScope=='1'||row.appScope==null){
					return "app";
				}
			}
		},{
			'mDataProp': 'amountScope',
			'bSortable': false
		},{
			'mDataProp': 'daysScope',
			'bSortable': false
		},{
			'mDataProp': 'printNum',
			'bSortable': false
		}, {
			'mDataProp': 'totalAmount',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return Number(data).toFixed(2);
			}
		}, {
			'mDataProp': 'printTimes',
			'bSortable': false
		}, {
			'mDataProp': 'createTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp': 'receivedNum',
			'bSortable': false
		}, {
			'mDataProp': 'usedNum',
			'bSortable': false
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
	});

	/**
	 * 收益券表
	 */
	var couponTemplateIncomeTable = $('#couponTemplateIncome-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_couponType",
				"value": "2"
			});
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp': 'id'
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'amount',
			'bSortable': true
		}, {
			'mDataProp': 'vaildCalcType',
			'bSortable': true,
			'mRender': function(data, type, row) {
				switch (data) {
					case 0:
						return "长期有效";
						break;
					case 1:
						return formatDate(row.startDate) + "到" + formatDate(row.endDate);
					default:
					case 2:
						return "领取后" + row.days + "天有效";
						break;
				}
			}
		}, {
			'mDataProp': 'webScope',
			'bSortable': false
		},{
			'mDataProp': 'amountScope',
			'bSortable': false
		},{
			'mDataProp': 'daysScope',
			'bSortable': false
		}, {
			'mDataProp': 'printNum',
			'bSortable': false
		}, {
			'mDataProp': 'printTimes',
			'bSortable': false
		}, {
			'mDataProp': 'createTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp': 'receivedNum',
			'bSortable': false
		}, {
			'mDataProp': 'usedNum',
			'bSortable': false
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
	});

	/*$('#modal-table').on('change', "input[name='extraInterestDay']", function() {
		var extraDay = $(this).val();
		var daysScope = $('#form-field-daysScope').val();
		if(extraDay>daysScope){
			bootbox.alert("加息天数不能大于起投期限！");
		}
		
	});*/
	
	/*$('#modal-table').on('change', '#form-field-daysScope', function() {
		var daysScope = $(this).val();
		var extraDay = $('#form-field-extraInterestDay').val();
		if(extraDay>daysScope){
			bootbox.alert("加息天数不能大于起投期限！");
		}
		
	});*/
	

	/**
	 * 开始时间的验证
	 */
	$('.modal-body').on('change', "input[name='startDate']", function() {
		var startDate = new Date($(this).val());
		var nowDate = new Date(formatDate(new Date()));
		if (startDate < nowDate) {
			bootbox.alert("开始时间必须大于等于当前时间！");
			$(this).val("");
		}
	});

	/**
	 * 结束时间的验证
	 */
	$('.modal-body').on('change', "input[name='endDate']", function() {
		var endDate = $(this).val();
		var startDate = $("input[name='startDate']").val();
		if (!startDate) {
			bootbox.alert("请先填写开始时间！");
			$(this).val("");
		} else {
			var startEndDateDiff = new Date(endDate) - new Date(startDate);
			if (startEndDateDiff <= 0) {
				bootbox.alert("结束时间必须大于开始时间！");
				$(this).val("");
			}
		}
	});

	/**
	 * 验证年化收益数字合法性	（年化券最小值年化0.5%）
	 * 面值变化时验证
	 */
	$('.modal-body').on("change", '#form-field-amount', function() {
		var type = $('#coupon_type').val();
		if (type == 2) { //2表示收益券
			var amount = $(this).val();
			if (Number(amount) < 0.5) {
				bootbox.alert("收益券的最小值年化率不得小于0.5%");
				$(this).val("");
			}

		}
	});

	/**
	 * 验证年化收益数字合法性	（年化券最小值年化0.5%）
	 * 优惠券类型变化时验证
	 */
	$('.modal-body').on("change", '#coupon_type', function() {
		var type = $('#coupon_type').val();
		if (type == 2) { //2表示收益券
			var amount = $("#form-field-amount").val();
			if (!amount) return;
			if (Number(amount) < 0.5) {
				bootbox.alert("收益券的最小值年化率不得小于0.5%");
				$("#form-field-amount").val("");
			}
		}
	});

	//clone页面克隆
	var modalForm = $("#couponTemplate_form").clone();

	// 新增 dialog
	$('#new_couponTemplate').on('click', function() {
		$('#couponTemplate_form').replaceWith(modalForm); //use clone
		$("#couponTemplateTitle").text("新增优惠券模板");
		couponTemplateInit(); //内容初始化
		resetValidation(); //验证信息初始化
		$('#modal-table').modal('show');
		couponTemplateForm = $("#couponTemplate_form").Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});

	});

	// 新增 save
	$('#save_couponTemplate').on('click', function() {
		//checkbox的值设置
		var daysScope = $('#form-field-daysScope').val();
		var extraDay = $('#form-field-extraInterestDay').val();
		if(parseInt(extraDay)>0){
			if(parseInt(extraDay)>parseInt(daysScope)){
				bootbox.alert("加息天数不能大于起投期限！");
				//$('#modal-table').modal("show");
				return;
			}
		}
		removeHiddenVaildation(); //移除隐藏的的验证
		if (couponTemplateForm.check(false)) {
			$('#couponTemplate_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					if (!!data.resultCodeEum) {
						bootbox.alert(data.resultCodeEum[0].msg,function(){
						});
					} else {
						bootbox.alert("保存失败!",function(){
						});
					}
					
				} else {
					bootbox.alert("保存成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
						couponTemplateForm.resetForm(); //验证重置
					});
				}
			});
		}
	});
	
	$("#modal-table").on("change",".client-scope",function(){
		var currentCheckbox = $(this);
		var isChecked = currentCheckbox.attr("checked");
		if(isChecked){
			currentCheckbox.removeAttr("checked").val("").next(".client-scope-v").val(0);
			var allIsNotChecked = true;//表示所有.client-scope的checkbox都未选中
			$('.client-scope').each(function(){
				var checked = $(this).attr("checked");
				if(checked){
					allIsNotChecked = false;
				}
			})
			if(allIsNotChecked){
				bootbox.alert("必须支持一个客户端使用！");
				currentCheckbox.siblings(".client-scope:first").prop("checked",true).attr("checked","true").val("1");
				currentCheckbox.siblings(".client-scope:first").next(".client-scope-v").val(1);
			}
		}else{
			currentCheckbox.attr("checked","true").val("1");
			currentCheckbox.next(".client-scope-v").val(1);
		}
		
	});
	
	//取消 cancel
	$('#cancel_couponTemplate').on('click', function() {
		couponTemplateForm.resetForm(); //验证重置
	});

	//query
	$('#query_couponTemplateCash').on('click', function() {
		
		//获取当前选中tab
		/*var $tabs = $("#tabs").tabs();
		$tabIndex = $tabs.tabs('option', 'selected');active
*/		
		var selected= $("#myTab3 li[class$='active'] >a").attr("href");
		switch(selected){
			case "#tabs-1":
				couponTemplateCashTable.fnDraw();
				return;
			case "#tabs-2":
				couponTemplateIncomeTable.fnDraw();
				return;
		}
		
	});
	//重置隐藏时间区间和领取天数查询条件
	$('#reset_couponTemplateCash').on('click', function() {
		$("#end_date_start_search").css("display","none");
		$("#end_date_start_search1").css("display","none");
		$("#due_date_search").css("display","none");
		$("#due_date_search1").css("display","none");
		$("#search_startDate").val("");  
		$("#search_endDate").val("");  
		$("#search_days").val("");  
	});
	
	
	//删除 delet
	$("#couponTemplateCash-table,#couponTemplateIncome-table").on("click", '.couponTemplate-del', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.del + id, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
					});
				} else {
					bootbox.alert("删除失败！");
				}
			});
		});
	});

	//置顶
	$("#couponTemplateCash-table,#couponTemplateIncome-table").on("click", '.couponTemplate-top', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要置顶吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.top +'?couponTemplateId='+ id, function(data) {
				if (data.code>0) {
					bootbox.alert("置顶成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
					});
				} else {
					bootbox.alert("置顶失败！");
				}
			});
		});
	});

	//取消置顶
	$("#couponTemplateCash-table,#couponTemplateIncome-table").on("click", '.couponTemplate-canceltop', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要取消置顶吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.canceltop +'?couponTemplateId='+ id, function(data) {
				if (data.code>0) {
					bootbox.alert("取消置顶成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
					});
				} else {
					bootbox.alert("取消置顶失败！");
				}
			});
		});
	});

	//短信提醒
	$("#couponTemplateCash-table,#couponTemplateIncome-table").on("click", '.couponTemplate-sms', function() {
		smsForm.resetForm();
		var id = $(this).attr("data-id");
		$('#couponTemplate_form').replaceWith(modalForm); //use clone
		$("#couponTemplateTitle").text("短信提醒");
		//resetValidation(); //验证初始化
		$('#modal-table-sms').modal("show");

		$('#couponTemplateSMS_form').xform("load", config.urlMap.sms +'?templateid='+ id, function(data) {
			$('#templateId').val(id);
			$('#validDays').val('');
			$('#status').attr("checked",true);
			//couponTemplateInit();
			//checkBoxData(data);
			if (data.success){
				$('#smsid').val(data.result.id);
				if (data.result.status==1){
					$('#status').attr("checked",true);
				}else{
					$('#status').attr("checked",false);
				}
				$('#validDays').val(data.result.validDays);
			}
		});
	});

	// 保存短信提醒信息
	$('#save_couponTemplate_SMS').on('click', function() {
		//removeHiddenVaildation(); //移除隐藏的的验证
		//checkbox的值设置
		$('#modal-table-sms').modal("hide");
		if (smsForm.check(false)){
			$('#couponTemplateSMS_form').xform('post', config.urlMap.savesms, function(data) {
				if (data.code>0) {
					bootbox.alert("保存成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
						smsForm.resetForm(); //验证重置
					});
				} else {
					bootbox.alert("保存失败！");
				}
			});
		}
	});

	//取消 cancel
	$('#cancel_couponTemplate_SMS').on('click', function() {
		couponTemplateForm.resetForm(); //验证重置
	});

	$("#vaildCalcType").on("change",function(){
		var vaildCalcType = $(this).val();
		switch(vaildCalcType){
			case "0":
				$("#end_date_start_search").css("display","none");
				$("#end_date_start_search1").css("display","none");
				$("#due_date_search").css("display","none");
				$("#due_date_search1").css("display","none");
				$("#search_startDate").val("");  
				$("#search_endDate").val("");  
				$("#search_days").val("");  
				return;
			case "1":
				$("#end_date_start_search").css("display","");
				$("#end_date_start_search1").css("display","");
				$("#due_date_search").css("display","none");
				$("#due_date_search1").css("display","none");
				$("#search_startDate").val("");  
				$("#search_endDate").val("");  
				$("#search_days").val("");  
				return;
			case "2":
				$("#end_date_start_search").css("display","none");
				$("#end_date_start_search1").css("display","none");
				$("#due_date_search").css("display","");
				$("#due_date_search1").css("display","");
				$("#search_startDate").val("");  
				$("#search_endDate").val("");  
				$("#search_days").val("");  
				return;
			default:
				$("#end_date_start_search").css("display","none");
				$("#end_date_start_search1").css("display","none");
				$("#due_date_search").css("display","none");
				$("#due_date_search1").css("display","none");
				$("#search_startDate").val("");  
				$("#search_endDate").val("");  
				$("#search_days").val("");  
				return;
		}
	});
	
	//编辑 edit
	$('#couponTemplateCash-table,#couponTemplateIncome-table').on("click", ".couponTemplate-edit", function() {
		var id = $(this).attr("data-id");
		$('#couponTemplate_form').replaceWith(modalForm); //use clone
		$("#couponTemplateTitle").text("修改优惠券模板");
		resetValidation(); //验证初始化
		$('#modal-table').modal("show");
		couponTemplateForm = $("#couponTemplate_form").Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});

		$('#couponTemplate_form').xform("load", config.urlMap.show + id, function(data) {
			checkBoxData(data);
			$('#extra_type').val(data.extraInterestType);
			//alert(data.extraInterestType+"---");
			//alert($('#extra_type').val()+"=====");
			couponTemplateInit();
		});
	});

	function checkBoxData(data){
		$.each(data,function(n,v){
			if(n=='webScope'){
				if(v=='1'){
					$('#webScope').attr("checked","true").val("1").next(".client-scope-v").val(1);
				}else{
					$("#webScope").removeAttr("checked").next(".client-scope-v").val(0);
				}
			}
			if(n=='wapScope'){
				if(v=='1'){
					$("#wapScope").attr("checked","true").val("1").next(".client-scope-v").val(1);
				}else{
					$("#wapScope").removeAttr("checked").next(".client-scope-v").val(0);
				}
			}
			if(n=='appScope'){
				if(v=='1'){
					$('#appScope').attr("checked","true").val("1").next(".client-scope-v").val(1);
				}else{
					$('#appScope').removeAttr("checked").next(".client-scope-v").val(0);
				}
			}
		});
	}
	
	$('#print-table').on('change', '#form-field-printNum', function() {
		var printNum = $(this).val();
		if (printNum > 100000) {
			bootbox.alert("单次最多印刷100000张！");
		}
	});

	//clone
	var printDialog = $('#print_couponTemplate_form').clone();

	$("#cancel_print_couponTemplate").on('click', function() {
		$('#print_couponTemplate_form').replaceWith(printDialog);
		printCouponTemplateForm.resetForm();
	});

	//印刷 dialog
	$('#couponTemplateCash-table,#couponTemplateIncome-table').on("click", ".couponTemplate-print", function() {
		$('#print_couponTemplate_form').replaceWith(printDialog); //use clone
		var id = $(this).attr("data-id");
		$('#coupon_template_id').val(id);
		$('#print-table').modal("show");
		printCouponTemplateForm = $('#print_couponTemplate_form').Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});
	});

	//印刷 print
	$('#print_couponTemplate').on(ace.click_event, function() {
		if (printCouponTemplateForm.check(false)) {
			$('#print_couponTemplate_form').xform('post', config.urlMap.print, function(data) {
				if (!data.success) {
					bootbox.alert("<font color='red'>印刷失败！</font>");
				} else {
					bootbox.alert("印刷成功！", function() {
						couponTemplateIncomeTable.fnDraw();
						couponTemplateCashTable.fnDraw();
						printCouponTemplateForm.resetForm();
					});
				}
			});
		}
	});

	/**
	 * 印刷列表
	 */
	var couponTemplatePrintTable = $('#couponTemplatePrint-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getSearchValue("#couponTemplatePrint-form", aoData);
		},
		'sAjaxSource': config.urlMap.printAjax,
		'aoColumns': [{
			'mDataProp': 'printTime',
			'bSortable': true,
			'mRender': function(data) {
				return formatDate(data, 'yyyy-mm-dd HH:mm');
			}
		}, {
			'mDataProp': 'printNum',
			'bSortable': true
		}, {
			'mDataProp': 'totalAmount',
			'bSortable': false
		}, {
			'mDataProp': 'remarks',
			'bSortable': false,
			'mRender': function(data) {
				if (!data) {
					return "--";
				} else {
					return data;
				}
			}
		}]
	});

	//查看印刷记录 dialog
	$('#couponTemplateCash-table,#couponTemplateIncome-table').on("click", ".couponTemplate-printIndex", function() {
		var couponType = $(this).attr("data-couponType");
		var id = $(this).attr("data-id");
		$('#print_coupon_template_id').val(id);
		var amount = $('.total-amount');
		if (Number(couponType) == 1) {
			if (!amount) {
				$('#couponTemplatePrint-table th:eq(1)').after("<th class='total-amount'>合计金额（元）</th>")
			}
			couponTemplatePrintTable.fnSetColumnVis(2, true);
		}
		if (Number(couponType) == 2) {
			if (!!amount) {
				$('.total-amount').remove();
			}
			couponTemplatePrintTable.fnSetColumnVis(2, false);
		}
		couponTemplatePrintTable.fnDraw({
			"fnServerParams": function(aoData) {
				getSearchValue("#couponTemplatePrint-form", aoData);
			}
		});

		//设置分页显示格式
//		setPaginationFormat();

		$('#printIndex-table').modal("show");
	});

});



/**
 * 优惠券模板类型
 */
$('.modal-dialog').on('change', '#coupon_type', function() {
	var type = $(this).val();
	couponTypeUnitShow(type);
});

/**
 * 有效期类型选择
 */
$('.modal-dialog').on('change', '#vaild_calc_type', function() {
	var type = $(this).val();
	vaildCalcTypeDetailShow(type);
});

$('.modal-dialog').on('change', '#extra_type', function() {
	var type = $(this).val();
	couponTypeExtraShow(type);
});
function couponTypeExtraShow(type) {
	if (type == 0) {
		$('#extra_type_is').css("display", "none");
	}
	if (type == 1) {
		$('#extra_type_is').css("display", "block");
	}
}
/**
 * 根据优惠券类别显示不同的单位
 * @param type
 */
function couponTypeUnitShow(type) {
	$('#unit').html('');
	if (type == 1) {
		$('#unit').html('&nbsp;元');
		$('#extra_type_coupon').css("display", "none");
	}
	if (type == 2) {
		$('#unit').html('&nbsp;%');
		$('#extra_type_coupon').css("display", "block");
	}
}

/**
 * 有效期类型选择时内容的显示
 * @param type
 */
function vaildCalcTypeDetailShow(type) {
	switch (Number(type)) {
		case 1:
			$('#type_time_zone').css("display", "block");
			$('#type_days').css("display", "none");
			break;
		case 2:
			$('#type_days').css("display", "block");
			$('#type_time_zone').css("display", "none");
			break;
		default:
			$('#type_days').css("display", "none");
			$('#type_time_zone').css("display", "none");
	}
}

/**
 * 优惠券模板内容的初始化
 */
function couponTemplateInit() {
	//单位
	couponType = $('#coupon_type').val();
	couponTypeUnitShow(couponType);
	//alert($('#extra_type').val()+"+++");
	couponTypeExtraShow($('#extra_type').val());
	//计算方式
	vaildCalcType = $('#vaild_calc_type').val();
	vaildCalcTypeDetailShow(vaildCalcType);
	//checkbox值初始化
	$("input[type='checkbox']").each(function(){
		$(this).attr("checked","true").val("1");
	});
	
}

/**
 * 删除隐藏域的验证
 */
function removeHiddenVaildation() {
	$('#couponTemplate_form div:hidden').each(function() {
		$(this).find('select,input').removeAttr("datatype");
	});
}

/**
 * 查看印刷记录的分页格式化 printIndex
 */
function setPaginationFormat() {
	var tableInfo = $('#couponTemplatePrint-table_info');
	var paginate = $('#couponTemplatePrint-table_paginate');
	var parentDiv = $('#couponTemplatePrint-table_info').parent().parent();

	$('#couponTemplatePrint-table_info').parent().remove();
	$('#couponTemplatePrint-table_paginate').parent().remove();
	var newTableInfo = tableInfo.wrap("<div class='col-xs-12'></div>").parent();
	var newPaginate = paginate.wrap("<div class='col-xs-12'></div>").parent();
	parentDiv.append(newTableInfo).append(newPaginate);
}

/**
 * 重置验证
 */
function resetValidation() {
	$('#couponTemplate_form').find("input[name='startDate']").attr('datatype', '*');
	$('#couponTemplate_form').find("input[name='endDate']").attr('datatype', '*');
	$('#couponTemplate_form').find("input[name='days']").attr('datatype', '/^[0-9]*[1-9][0-9]*$/').attr('errormsg', '请输入正整数！');
}