jQuery(function($) {
	// 获取操作按钮
	function getAllOperation(row) {
		var detail = "<button class='btn btn-xs btn-success coupon-detail permission-"+config.permission.detail+"'  data-id='" + row.id + "'><i class='icon-zoom-in bigger-130'>查看</i></button>";
		var unlock= "<button  class='btn btn-xs btn-success btn-pink coupon_unlock permission-"+config.permission.unlocked+"' data-value='"+row.couponCode+"'><i class='icon-zoom-in bigger-130'>解锁</i></button>";
		if(row.status == 5){
			return detail + unlock;
		}else{
			return detail;
		}
	}

	/**
	 * 优惠券表
	 */
	var coupontable = $('#coupon-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
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
			'mDataProp': 'couponCode',
			'bSortable': false
		}, {
			'mDataProp': 'couponType',
			'bSortable': true,
			'mRender': function(data, type, row) {
				return getDictLabel(config.type, data.toString());
			}
		},{
			'mDataProp': 'couponTemplateName',
			'bSortable': true
		}, {
			'mDataProp': 'amount',
			'bSortable': true,
			'mRender':function(data, type, row){
				
				if(row.couponType=='1'){
					return Number(data).toFixed(2)+"元";
				}else{
					return Number(data).toFixed(2)+"%";
				}					
				
				
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
			'mDataProp': 'status',
			'bSortable': false,
			'mRender':function(data,type,row){
				return getDictLabel(config.status, data.toString());
			}	
		}, {
			'mDataProp': 'couponCode',
			'bSortable': false,
			'mRender':function(data,type,row){
				return row.holderId;
			}	
		},{
			'mDataProp': 'member',//<th>持有人姓名</th>	
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(data!=null){
					return data.trueName;
				}
				return null;
			}
		},{
			'mDataProp': 'member',//<th>持有人手机号</th>	
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(data!=null){
					return data.mobile;
				}
				return null;
			}
		},{
			'mDataProp': 'projectId',
			'bSortable': false
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
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
	
	//query
	$('#query_coupon').on('click', function() {
		coupontable.fnDraw();
	});
	//重置隐藏时间区间和领取天数查询条件
	$('#reset_coupon').on('click', function() {
		$("#end_date_start_search").css("display","none");
		$("#end_date_start_search1").css("display","none");
		$("#due_date_search").css("display","none");
		$("#due_date_search1").css("display","none");
		$("#search_startDate").val("");  
		$("#search_endDate").val("");  
		$("#search_days").val("");  
	});
	
	
	
	$('#coupon-table').on('click','.coupon-detail',function(){
		$('#coupon-table-detail').modal("show");
		$.ajax({
			url:config.urlMap.detail,
			data:{
				'id':$(this).attr('data-id')
				},
			type:'post',
			dataType:'json',
			success:function(data){
				eachCoupon(data);
			}
		});
		
	});
	
	function eachCoupon(data){
		$.each(data,function(n,v){
			$('#coupon-table-detail').find("td[name='"+n+"']").html(v);
		});
		
		var receiveTime = data.receiveTime;
		if(!!receiveTime){
			receiveTime = formatDate(receiveTime,"yyyy-mm-dd HH:mm:ss");
			$("#coupon-table-detail").find("td[name='receiveTime']").html(receiveTime);
		}
		
		var usedTime = data.usedTime;
		if(!!usedTime){
			usedTime = formatDate(usedTime,"yyyy-mm-dd HH:mm:ss");
			$("#coupon-table-detail").find("td[name='usedTime']").html(usedTime);
		}
		
		$('#coupon-table-detail td[name]').each(function(i){
			var val = $(this).text();
			if(!val){
				$(this).html("--");
			}
		})	
		
		var status = data.status;
		
		$("#coupon-table-detail").find("td[name='status']").html(getDictLabel(config.status, status.toString()));
		
		/*switch(Number(status)){
		case 0:
			$("#coupon-table-detail").find("td[name='status']").html("未领用");
			break;
		case 1:
			$("#coupon-table-detail").find("td[name='status']").html("已领取，未使用");
			break;
		case 2:
			$("#coupon-table-detail").find("td[name='status']").html("已使用");
			break;
		case 3:
			$("#coupon-table-detail").find("td[name='status']").html("未领取，已过期");
			break;
		case 4:
			$("#coupon-table-detail").find("td[name='status']").html("已领取，已过期");
			break;
		case 5:
			$("#coupon-table-detail").find("td[name='status']").html("使用中");
			break;
		default:
			$("#coupon-table-detail").find("td[name='status']").html("");
		}*/
		var vaildCalcType = data.vaildCalcType;
		switch(Number(vaildCalcType)){
		case 0:
			$("#coupon-table-detail").find("td[name='vaildCalcType']").html("长期有效");
			$("#coupon-table-detail").find("td[name='vaildCalcTypeContent']").html("长期有效");
			break;
		case 1:
			$("#coupon-table-detail").find("td[name='vaildCalcType']").html("按时间");
			$("#coupon-table-detail").find("td[name='vaildCalcTypeContent']").html(formatDate(data.startDate)+"——"+formatDate(data.endDate));
			break;
		case 2:
			$("#coupon-table-detail").find("td[name='vaildCalcType']").html("按领取");
			$("#coupon-table-detail").find("td[name='vaildCalcTypeContent']").html("领取后"+data.days+"有效");
			break;
		default:
			$("#coupon-table-detail").find("td[name='vaildCalcType']").html("");
			$("#coupon-table-detail").find("td[name='vaildCalcTypeContent']").html("");
		}
		var couponType = data.couponType;
		switch(Number(couponType)){
		case 1:
			$('#detal_coupon_amount').html("面值（元）");
			var amount = $("#coupon-table-detail").find("td[name='amount']").text();
			$("#coupon-table-detail").find("td[name='amount']").html(Number(amount).toFixed(2))
			break;
		case 2:
			$('#detal_coupon_amount').html("面值（%）");
			break;
		default:
			$('#detal_coupon_amount').html("");
		}
	}
	
	var couponGiveForm = $("#coupon_give_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	
	/**赠送优惠券*/
	$("#btn_give").on("click",function(){
		if (couponGiveForm.check(false)) {
			$("#btn_give").addClass("disabled");
			var couponData = $("#coupon_give_form").serialize();
			$.ajax({
				url:config.urlMap.toGiveCoupon,
				data:couponData,
				dataType:'json',
				success:function(data){
					var str = "";
					$.each(data,function(v,n){
						if(n){
							n = "成功！";
						}else{
							n = "失败！";
						}
						if(str != ""){
							str = str + "\x0a\x0d" + v+":"+n ;
						}else{
							str = v+":"+n;
						}
					});
					console.info(str)
					bootbox.alert(str,function(){
						$("#btn_give").removeClass("disabled");
					});
				}
			});
		}
	});
	
	var popularityForm = $("#popularity_give_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	/**赠送优惠券*/
	$("#btn_givePopularity").on("click",function(){
		if (popularityForm.check(false)) {
			$("#btn_givePopularity").addClass("disabled");
			var couponData = $("#popularity_give_form").serialize();
			$.ajax({
				url:config.urlMap.toGivePopularity,
				data:couponData,
				dataType:'json',
				success:function(data){
					console.info(data);
					if(data){
						data = "赠送成功！";
					}else{
						data = "赠送失败！";
					}
					bootbox.alert(data,function(){
						$("#btn_givePopularity").removeClass("disabled");
					});
				}
			});
		}
	});
	

	/**
	 * 监控收益劵参数表
	 */
	var oTable1 = $('#monitor-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.getParam,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp' : 'label',
			'bSortable' : true
		}, {
			'mDataProp' : 'key',
			'bSortable' : true
		}, {
			'mDataProp' : 'value',
			'bSortable' : true
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				var buttons = "";
				if(config.permission.edit){//修改按钮
					buttons = buttons + "<button  class='btn  btn-pink btn-sm btn-primary edit_monitor' data-value='"+row.id+"'>修改</button>";
				}
				
				return   buttons;
			}
		} ]
	});
	$("#save_monitor").on('click', function() {
		var options = {
			beforeSubmit : checkform,
			url : config.urlMap.saveMoParam,
			type : "post",
			resetForm : true,
			success : function(data) {
				$('#modal-table').modal('toggle');
				oTable1.fnDraw();
			}
		};
		$('#monitor_form').ajaxSubmit(options);
	});
	function checkform(formData, jqForm, options) {
		return $("#monitor_form").valid();
	}
	
	
	$('#monitor-table').on('click','.edit_monitor', function() {
		$(".modalFormTitle").text("编辑参数");
		var id = $(this).attr("data-value");
        $("#monitor_form").xform("load", (config.urlMap.showMoParam + id));
        $('#modal-table').modal('show');
      
	});
	
	$('#query_unlock').on('click', function() {
		
		
		var $mobile = $("#search_mobile");
		var $truename = $("#search_trueName");
		var $id = $("#search_id");

		if(!$mobile.val() &&!$truename.val()&&!$id.val()){
		}else{
			$("#unlockcoupon2-table").show();
			showDataTable();
		}
	});
	
	function showDataTable() {
		/**
		 * 优惠券解锁表
		 */
		var unlockcouponTable = $('#unlockcoupon2-table').dataTable({
			'bFilter': false,
			"bRetrieve": true,
			'bProcessing': true,
			'bSort': true,
			'bServerSide': true,
			'fnServerParams': function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource': config.urlMap.unlockQuery,
			'aoColumns': [{
				'mDataProp': 'id',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp': 'couponCode'
			}, {
				'mDataProp': 'couponTemplateId',
				'bSortable': true
			},  {
				'mDataProp': 'name',
				'bSortable': true
			}, {
				'mDataProp': 'couponType',
				'bSortable': true,
				'mRender':function(data,type,row){
					return getDictLabel(config.type, data.toString());
				}	
			},{
				'mDataProp': 'amount',
				'bSortable': false,
				'mRender':function(data,type,row){
					if(row.couponType=="1"){
						return row.amount +"元";
					}else if(row.couponType=="2"){
						return row.amount +"%";
					}
					
				}	
			}, {
				'mDataProp': 'activityId',
				'bSortable': false
			},{
				'mDataProp': 'activityName',
				'bSortable': false
			},{
				'mDataProp': 'usedTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if(row.usedTime!=null){
						return formatDate(data, "yyyy-mm-dd HH:mm:ss");
					}
					return null;
				}
			},{
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
				'mDataProp': 'status',
				'bSortable': false,
				'mRender':function(data,type,row){
					return getDictLabel(config.status, data.toString());
				}	
			}, {
				'mDataProp': 'senderId',
				'bSortable': false,
				'mRender': function(data, type, row) {
					switch (data) {
					case -1:
						return "系统赠送";
						break;
					default:
						return data;
						break;
				}
				}
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					var buttons = "";
					if(row.status=="5"){
						buttons = buttons + "<button  id='coupon_unlock' class='btn  btn-pink btn-sm btn-primary coupon_unlock' data-value='"+row.couponCode+"'>解锁</button>";
					}
					
					return   buttons;
				}
			}]
		});
		
		unlockcouponTable.fnDraw();
	}
	$('#coupon-table').on('click','.coupon_unlock', function() {

		var couponNo = $(this).attr("data-value");
		bootbox.confirm("你确定要解锁吗?", function(result) {
			if (result) {
				$.post(
					config.urlMap.unlockedCoupon,{couponNo:couponNo},function(data){
						if(data.success) {
							bootbox.alert("优惠券解锁成功！");
							showDataTable();
						} else {
							var status = data.result;
							switch(status) {
								case 0:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取");
									break;
								case 1:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，未使用");
									break;
								case 2:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已使用");
									break;
								case 3:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取，已过期");
									break;
								case 4:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，已过期");
									break;
								case 5:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：使用中");
									break;
								case 10:
									bootbox.alert("优惠券解锁失败！使用该优惠券的订单状态为：待支付");
									break;
								case 11:
									bootbox.alert("优惠券解锁失败！使用该优惠券的订单状态为：处理中");
									break;
								case 13:
									bootbox.alert("优惠券解锁成功！优惠券状态置为已使用,使用该优惠券的订单状态为：已支付，投资成功。");
									break;
							}
						}
						coupontable.fnDraw();
					}
				);
			}
		});
	});
	
	$('#unlockcoupon2-table').on('click','.coupon_unlock', function() {

		var couponNo = $(this).attr("data-value");
		bootbox.confirm("你确定要解锁吗?", function(result) {
			if (result) {
				$.post(
					config.urlMap.unlockedCoupon,{couponNo:couponNo},function(data){
						if(data.success) {
							bootbox.alert("优惠券解锁成功！");
							showDataTable();
						} else {
							var status = data.result;
							switch(status) {
								case 0:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取");
									break;
								case 1:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，未使用");
									break;
								case 2:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已使用");
									break;
								case 3:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：未领取，已过期");
									break;
								case 4:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：已领取，已过期");
									break;
								case 5:
									bootbox.alert("优惠券解锁失败！当前优惠券状态为：使用中");
									break;
								case 10:
									bootbox.alert("优惠券解锁失败！使用该优惠券的订单状态为：待支付");
									break;
								case 11:
									bootbox.alert("优惠券解锁失败！使用该优惠券的订单状态为：处理中");
									break;
								case 13:
									bootbox.alert("优惠券解锁成功！优惠券状态置为已使用,使用该优惠券的订单状态为：已支付，投资成功。");
									break;
							}
							showDataTable();
						}
					}
				);
			}
		});
	});


	var couponSendForm = $("#coupon_send_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	/**优惠券发放*/
	$("#btn_send").on("click",function(){
		if (couponSendForm.check(false)) {
			$("#btn_send").addClass("disabled");
			$('#coupon_send_form').submit();
		}
	});
	
	
	
	
	
});
