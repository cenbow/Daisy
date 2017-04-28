jQuery(function($) {
	
	setReturnType(1);
	
	//表单验证初始化
	var debtForm = $("#debt_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	//获取操作按钮
	function getAllOperation(row) {
		var status = row.status,project_status = row.projectStatus;
		var detail = "<button class='btn btn-xs btn-success debt-detail permission-" + config.permission.detail + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'><i class='icon-zoom-in bigger-130'>详情</i></button>"; //详情
		var edit = "<button class='btn btn-xs btn-info debt-edit permission-" + config.permission.show + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'>" +
			"<i class='icon-edit bigger-120'>编辑</i></button>"; //编辑
		var del = "<button class='btn btn-xs btn-danger debt-del permission-" + config.permission.del + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		var emergencyEdit = "<button class='btn btn-xs btn-danger debt-emergency permission-" + config.permission.emergency + "' data-id='" + row.id + "' data-serialNumber ='" + row.serialNumber + "'>" +
		"<i class='icon-edit bigger-120'>紧急修改</i></button>"; //编辑
		if (status==0) { //存盘
				return detail + " " + edit + " " + del;
		}else if (status==10 || status==20){ //10:已使用 或者 20:已还款
				return detail + " " + emergencyEdit;;
		}
	}


	var debtTable = $('#debt-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'aaSorting':[[0,"desc"]],
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
			}
			, {
				'mDataProp': 'serialNumber',
				'bSortable': true
			}, {
				'mDataProp' : 'originalDebtNumber',
				'bSortable' : false,
				'mRender':function(data,type,row){
					return row.originalDebtNumber==null?"":row.originalDebtNumber;
				}
			}, {
				'mDataProp': 'amount',
				'bSortable': true
			}, {
				'mDataProp': 'debtType',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getDictLabel(config.debtType, row.debtType);
				}
			}, {
				'mDataProp': 'dateRange',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return formatDate(row.startDate) + "至" + formatDate(row.endDate);
				}
			}, {
				'mDataProp': 'annualizedRate',
				'bSortable': false,
				'mRender': function(data) {
					return data + '%';
				}
			}, {
				'mDataProp': 'borrowerName',
				'bSortable': false,
				'mRender':function(data,type,row){
					 if(row.enterpriseName!=null){
						 return row.enterpriseName;
					 }else{
						 return data;
					 }
				}
			}, {
				'mDataProp': 'lenderName',
				'bSortable': false
			}, {
				'mDataProp': 'createdTime',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return formatDate(data, "yyyy-mm-dd HH:mm:ss");
				}
			}, {
				'mDataProp': 'projectName',
				'bSortable': false,
				'mRender': function(data, type, row) {
					if (!data) {
						return '未关联';
					} else {
						return data;
					}
				}
			}, {
				'mDataProp': 'publishName',
				'bSortable': false
			}, {
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) {
					return getAllOperation(row);
				}
			}
		]
	});

	//查询
	$('#query_debt').on('click', function() {
		debtTable.fnDraw();
	});

	//重置
	$('#reset_debt').on('click', function() {
		$('#debt_form')[0].reset();
	});

	//编辑show
	$("#debt-table").on("click", '.debt-edit', function() {
		var target = $(this);
		var id = $(this).attr("data-id");
		$.ajax({
			url:config.urlMap.debtStatus,
			data:{'id':id},
			dataType:"json",
			success:function(data){
				if(data!=null){
					if(data.status==0){
						var serial = target.attr('data-serialNumber');
						window.top.setIframeBox("debt-edit-" + id, config.urlMap.show + id, "修改债权" + serial);
					}else {
						bootbox.alert("当前债权非存盘状态，不能编辑！");
					}
				}
				
			}
		});
	});
	
	//编辑emergency
	$("#debt-table").on("click", '.debt-emergency', function() {
		var id = $(this).attr("data-id");
		var serial = $(this).attr('data-serialNumber');
		window.top.setIframeBox("debt-emergency-" + id, config.urlMap.emergency + id, "紧急修改债权" + serial);
	});

	//新增
	$('#new_debt').on('click', function() {
		window.top.setIframeBox("debt-add", config.urlMap.add, "新增债权");
	});

	//详情detail
	$('#debt-table').on('click', '.debt-detail', function() {
		var id = $(this).attr("data-id");
		var serial = $(this).attr('data-serialNumber');
		window.top.setIframeBox("debt-" + "detail-" + id, config.urlMap.detail + id, serial + "债权详情");
	});

	//删除delete
	$('#debt-table').on('click', '.debt-del', function() {
		var id = $(this).data("id");
		$.ajax({
			url:config.urlMap.debtStatus,
			data:{'id':id},
			dataType:"json",
			success:function(data){
				if(data!=null){
					if(data.status!=0){
						bootbox.alert("当前债权非存盘状态，不能删除！");
					}else{
						bootbox.confirm("你确定要删除吗?", function(result) {
							if(!result)return;
							$.post(config.urlMap.del + id, function(data) {
								if (data.success) {
									bootbox.alert("删除成功！",function(){
										debtTable.fnDraw();
									});
								} else {
									boobbox.alert("删除失败！");
								}
							});
						});
					}
				}
				
			}
		});
		
	});


	/**
	 * 债权类型选择
	 */
	$("#form-field-debtType").change(
		function() {
			// 债权类型 (质押物、抵押物)
			var type = $(this).find("option:selected").val();
			collateralDetailVisible(type);
	});
	
	/**
	 * 是否分期选择
	 */
	$("#form-field-instalment").click(
		function() {
			// 分期、非分期
			if($(this).prop("checked")){
				$("#buyCar_detail").css("display","block");
				$("#car_detail").css("display", "none");
				$("#d_monthly_rate").css("display", "block");
				$("#d_annualized_rate").css("display", "none");
				$("#form-field-returnType").val("avg_principal");//等本等息
	        }else{
	        	$("#buyCar_detail").css("display","none");
				$("#car_detail").css("display", "block");
				$("#d_monthly_rate").css("display", "none");
				$("#d_annualized_rate").css("display", "block");
				$("#form-field-returnType").val("");//请选择
	        }
			var interestFrom = $("#interest_from").val();
			var startData = new Date($('#form-field-startDate').val());
			if($("#form-field-returnType").val()=="avg_principal"){
				$('#interest_s_date_1').val(formatDate(startData.getTime()));
			}else{
				$('#interest_s_date_1').val(formatDate(startData.getTime() + (interestFrom * 24 * 60 * 60 * 1000)));
			}
	});

	/**
	 * 月利率和年利率值改变时，年利率隐藏表单值随之改变
	 */
	$(".j-rate").change(function(){
		var rate = $(this).val();
		if($(this).hasClass("j-mon")){//年利率=月利率*12
			rate = ($(this).val()*12).toFixed(2);
		}
		$("input[name='annualizedRate']").val(rate);
		$('#auto_rate').html(rate);
	});
	
	/**
	 * 担保方式选择详情显示
	 */
	function collateralDetailVisible(type) {
		switch (type) {
			case "collateral":
				$("#collateral_type").css("display", "block");
				$("#pledge_type").css("display", "none");
				$("#credit_type").css("display", "none");
				var collateralType = $('#collateral_type').find(
					"option:selected").val();
				switch (collateralType) {
					case "car":
//						$("#car_instalment").css("display", "block");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");//基础信息（目前是车商融）
						var instalment = $("#form-field-instalment").prop("checked");
//						if(instalment){
//							$("#buyCar_detail").css("display","block");
//							$("#car_detail").css("display", "none");
//							$("#d_monthly_rate").css("display", "block")
//							$("#d_annualized_rate").css("display", "none")
//				        }else{
//				        	$("#buyCar_detail").css("display","none");
//							$("#car_detail").css("display", "block");
//							$("#d_monthly_rate").css("display", "none")
//							$("#d_annualized_rate").css("display", "block")
//				        }
						getShowDebtTypeDetail('#collateral_car',instalment)
						break;
					case "house":
//						$("#house_detail").css("display", "block");
//						$("#car_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none")
//						$("#d_annualized_rate").css("display", "block")
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail('#house_detail');
						break;
					default:
//						$("house_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none")
//						$("#d_annualized_rate").css("display", "block")
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail();
				}
				break;
			case "pledge":
				$("#pledge_type").css("display", "block");
				$("#collateral_type").css("display", "none");
				$("#credit_type").css("display", "none");
				var pledgeType = $('#pledge_type').find("option:selected")
					.val();
				switch (pledgeType) {
					case "car":
//						$("#car_detail").css("display", "block");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail("#car_detail");
						break;
					case "newCar":
//						$("#newCar_detail").css("display", "block");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail("#newCar_detail");
						break;
					case "equity"://股权有融和新车融一样处理
//						$("#newCar_detail").css("display", "block");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail("#newCar_detail");
						break;
					default:
//						$("#newCar_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#carPayIn_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail();
				}
				break;
			case "credit":
				$("#pledge_type").css("display", "none");
				$("#collateral_type").css("display", "none");
				$("#credit_type").css("display", "block");
				var creditType = $('#credit_type').find("option:selected").val();
				switch (creditType) {
					case "houseRecord":
//						$("#houseRecord_detail").css("display", "block");
//						$("#carPayIn_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail('#houseRecord_detail');
						break;
					case "carPayIn":
//						$("#carPayIn_detail").css("display", "block");
//						$("#houseRecord_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail("#carPayIn_detail");
						break;
					case "carBusiness":
//						$("#base_detail").css("display", "block");
//						$("#carPayIn_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");//汽车是否分期
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail("#base_detail");
						break;
					case "carBusiness":
						getShowDebtTypeDetail("#runCompany_detail");
						break;
					default:
//						$("#carPayIn_detail").css("display", "none");
//						$("#houseRecord_detail").css("display", "none");
//						$("#car_detail").css("display", "none");
//						$("#house_detail").css("display", "none");
//						$("#newCar_detail").css("display", "none");
//						$("#base_detail").css("display", "none");
//						$("#car_instalment").css("display", "none");//汽车是否分期
//						$("#d_monthly_rate").css("display", "none");
//						$("#d_annualized_rate").css("display", "block");
//						$("#buyCar_detail").css("display","none");
						getShowDebtTypeDetail();
				}
				break;
			default:
				$("#pledge_type").css("display", "none");
				$("#collateral_type").css("display", "none");
				$("#credit_type").css("display", "none");

		}
	}

	/**
	 * 设置担保类型的详情显示
	 */
	function getShowDebtTypeDetail(showDetailId,isInstalment){
		var detailsObject = [
			"#carPayIn_detail",
			"#houseRecord_detail",
			"#house_detail",
			"#newCar_detail",
			"#base_detail",
			"#car_instalment",
			"#buyCar_detail",
			"#car_detail",
			"#d_monthly_rate",
			"#runCompany_detail"
 		]; 
		if(showDetailId=='#collateral_car'){
			var otherNoneDetails = [
		            "#carPayIn_detail",
		            "#houseRecord_detail",
		            "#house_detail",
		            "#newCar_detail",
		            "#base_detail",
		            "#runCompany_detail"
            ]; 
			if(typeof isInstalment != "undefined" && isInstalment){//当类型
				$("#buyCar_detail").css("display","block");
				$("#car_detail").css("display", "none");
				$("#d_monthly_rate").css("display", "block");
				$("#d_annualized_rate").css("display", "none");
			}else {
				$("#buyCar_detail").css("display","none");
				$("#car_detail").css("display", "block");
				$("#d_monthly_rate").css("display", "none");
				$("#d_annualized_rate").css("display", "block");
			}
			$("#car_instalment").css("display", "block");
			$.each(otherNoneDetails,function(n,value){
				$(value).css("display", "none");
			});
		}else{
			$("#d_annualized_rate").css("display", "block");
			$.each(detailsObject,function(n,value){
				if(typeof value != "undefined" && value == showDetailId){
					$(value).css("display", "block");
				}else{
					$(value).css("display", "none");
				}
			});
		}
	}

	/**
	 * 担保内容的选择
	 */
	$(".thingType").change(function() {
		var thingType = $(this).find("option:selected").val();
		var debtType = $("#form-field-debtType").find("option:selected").val();
		switch (thingType) {
			case "car":
				getShowDetail("#car_detail",debtType);
				break;
			case "house":
				getShowDetail("#house_detail");
				break;
			case "newCar":
				getShowDetail("#newCar_detail");
				break;
			case "equity"://股权融和新车融一样处理
				getShowDetail("#newCar_detail");
				break;
			case "houseRecord"://房屋备案
				getShowDetail("#houseRecord_detail");
				break;
			case "carPayIn"://车贷垫资
				getShowDetail("#carPayIn_detail");
				break;
			case "carBusiness"://车商融
				getShowDetail("#base_detail");
				break;
			case "runCompany"://车商融
				getShowDetail("#runCompany_detail");
				break;
			default:
				getShowDetail();
		}
	});

	/**
	 * 设置抵押或抵押不同类型的详情显示
	 */
	function getShowDetail(showDetailId,debtType){
		var detailsObject = [
                "#carPayIn_detail",
				"#car_detail",
				"#house_detail",
				"#newCar_detail",
				"#houseRecord_detail",
				"#base_detail",
				"#buyCar_detail",
				"#runCompany_detail"
		]; 
		$.each(detailsObject,function(n,value){
			if(typeof debtType != "undefined" && debtType == 'collateral'){
				$("#car_instalment").css("display", "block");//汽车是否分期
			}else{
				$("#car_instalment").css("display", "none");
			}
			if(typeof value != "undefined" && value == showDetailId){
				$(value).css("display", "block");
			}else{
				$(value).css("display", "none");
			}
        });
	}
	
	/**
	 * 债权人类型选择
	 */
	$("#l_member_type").change(function() {
		var memberType = $(this).find("option:selected").val();
		lmemberVisible(memberType);
	});

	//不同类型债权人的信息显示和隐藏
	function lmemberVisible(type) {
		switch (type) {
			case "1":
				$('#l_personal_info').css("display", "block");
				$('#l_company_info').css("display", "none");
				$('.lenderCompanyDetail').remove();
				$('#lender_company_name').val("");
				break;
			case "2":
				$('#l_personal_info').css("display", "none");
				$('#l_company_info').css("display", "block");
				$('.lenderMemberDetail').remove();
				$('#lender_member_name').val("");
				break;	
			default:
				$('#l_personal_info').css("display", "none");
				$('#l_company_info').css("display", "none");
				$('#lender_member_id').val("");
				$(".lenderMemberDetail").remove();
				$('.lenderCompanyDetail').remove();
				$('#lender_member_name').val("");
				$('#lender_company_name').val("");
		}
	}

	/**
	 * 借款人类型选择
	 */
	$("#b_member_type").change(function() {
		var memberType = $(this).find("option:selected").val();
		bmemberVisible(memberType);
	});

	//不同类型的借款人的显示和隐藏
	function bmemberVisible(type) {
		switch (type) {
			case "1":
				$('#b_company_info').css("display", "none");
				$('#b_personal_info').css("display", "block");
				$('.borrowerCompanyDetail').remove();
				break;
			case "2":
				$('#b_company_info').css("display", "block");
				$('#b_personal_info').css("display", "none");
				$('.borrowMemberDetail').remove();
				break;
			default:
				$('#b_company_info').css("display", "none");
				$('#b_personal_info').css("display", "none");
				$('#borrow_member_id').val("");
				$('.borrowMemberDetail').remove();
				$('.borrowerCompanyDetail').remove();
		}
	}

	/**
	 * 借款人和出借人个人信息的根据姓名的自动补全
	 */
	$("#lender_member_name,#borrow_member_name").autocomplete({
		multiple:true,
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchMember,
				dataType: "json",
				type: 'post',
				data: {
					searchDbInforItem: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							mobile: item.mobile, // 手机
							memberInfo: item.memberInfo, // 详细信息
							address: item.address, // 联系地址
							sex: item.sex, // 性别
							marriage: item.marriage, // 婚姻
							highEdu: item.highEdu, // 学历
							identityNumber: item.identityNumber, // 身份证
							memberId: item.id,
							value: item.trueName + " " + item.identityNumber
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			if ($(this).hasClass("autoc-lender-name")) {
				$('.lenderMemberDetail').remove();
				showLenderMemberDetail(); //显示债权人个人信息
				$("#lender_member_id").val(ui.item.memberId);
				$(".lenderMinfo").html("");
				$('#lender_mobile').html(ui.item.mobile);
				$('#lender_identityNumber')
					.html(ui.item.identityNumber);
				ui.item.sex = getDictLabel(config.sex,ui.item.sex);
				$('#lender_sex').html(ui.item.sex);
				if (ui.item.memberInfo != "undefined" && ui.item.memberInfo != null) {
					$('#lender_address').html(
						ui.item.memberInfo.address);
					ui.item.memberInfo.marriage = getDictLabel(config.marriage,ui.item.memberInfo.marriage);
					$('#lender_marriage').html(
						ui.item.memberInfo.marriage);
					ui.item.memberInfo.highEdu = getDictLabel(config.education,ui.item.memberInfo.highEdu);
					$('#lender_highEdu').html(
						ui.item.memberInfo.highEdu);
				}
				return;
			}
			if ($(this).hasClass("autoc-borrow-name")) {
				$('.borrowerCompanyDetail').remove();
				$('.borrowMemberDetail').remove();
				showBorrowerMemberDetail(); //显示借款人个人信息
				$(".borrowMinfo").html("");
				$("#borrow_member_id").val(ui.item.memberId);
				$('#borrow_mobile').html(ui.item.mobile);
				$('#borrow_identityNumber')
					.html(ui.item.identityNumber);
				ui.item.sex = getDictLabel(config.sex,ui.item.sex);
				$('#borrow_sex').html(ui.item.sex);
				if (ui.item.memberInfo != "undefined" && ui.item.memberInfo != null) {
					$('#borrow_address').html(
						ui.item.memberInfo.address);
					ui.item.memberInfo.marriage = getDictLabel(config.marriage,ui.item.memberInfo.marriage);
					$('#borrow_marriage').html(
						ui.item.memberInfo.marriage);
					ui.item.memberInfo.highEdu = getDictLabel(config.education,ui.item.memberInfo.highEdu);
					$('#borrow_highEdu').html(
						ui.item.memberInfo.highEdu);
				}
				return;
			}
		}
	});

	/**
	 * 企业借款人信息根据企业名称的自动补齐
	 */
	$("#b_company_name").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchEnterprise,
				dataType: "json",
				type: 'post',
				data: {
					enterpriseName: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							income: item.income, // 注册资本
							region: item.region, // 地区
							identityNumber: item.identity, //法人身份证
							id: item.legalId,
							enterpriseId:item.id,
							value: item.name +" "+ item.legalName
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			var guarantyType = $("#credit_type_sel").val()
			if(guarantyType=="runCompany"){
				var data={
					id:ui.item.enterpriseId,
					guarantyType:guarantyType
				}
			}else{
				var data={
					id:ui.item.enterpriseId,
					guarantyType:guarantyType
				}
			}
			$.ajax({
				url: config.urlMap.searchMemberInfo,
				dataType: "json",
				type: 'post',
				data:  data,
				success: function(data) {
					if (!!data.member) {
						$('.borrowerCompanyDetail').remove();
						showBorrowerCompanyDetail();
						//法人姓名*  member 
						$("#borrow_member_id").val(data.member.id);
					}
					if (data.enterprises.length > 0) {
						$.each(data.enterprises[0],function(n,v){
							$("input[id='b_company_"+n+"']").val(v);
							$("span[id='b_company_"+n+"']").html(v);
						});
						$('#b_company_regeditDate').html(formatDate(data.enterprises[0].regeditDate));
						$('#b_company_address').html(data.enterprises[0].region+data.enterprises[0].address);
					}
					if(guarantyType=="runCompany"){
						
					}
				}
			});
		}
	});

	/**
	 * 企业出借人信息根据法人名称的自动补齐
	 */
	$("#l_company_legalName").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: config.urlMap.searchEnterpriseLegal,
				dataType: "json",
				type: 'post',
				data: {
					legalname: request.term
				},
				success: function(data) {
					response($.map(data, function(item) {
						return {
							income: item.income, // 注册资本
							region: item.region, // 地区
							identityNumber: item.identity, //法人身份证
							id: item.legalId,
							lenderEnterpriseId:item.id,
							value: item.name +" "+ item.legalName
						}
					}));
				}
			});
		},
		minLength: 1,
		select: function(event, ui) {
			var guarantyType = $("#credit_type_sel").val()
			if(guarantyType=="runCompany"){
				var data={
					id:ui.item.lenderEnterpriseId,
					guarantyType:guarantyType
				}
			}else{
				var data={
					id:ui.item.lenderEnterpriseId,
					guarantyType:guarantyType
				}
			}
			$.ajax({
				url: config.urlMap.searchMemberInfo,
				dataType: "json",
				type: 'post',
				data:  data,
				success: function(data) {
					if (!!data.member) {
						$('.lenderCompanyDetail').remove();
						showLenderCompanyDetail();
						$("#lender_member_id").val(data.member.id);
					}
					if (data.enterprises.length > 0) {
						$.each(data.enterprises[0],function(n,v){
							$("input[id='l_company_"+n+"']").val(v);
							$("span[id='l_company_"+n+"']").html(v);
						});
						$('#l_company_regeditDate').html(formatDate(data.enterprises[0].regeditDate));
						$('#l_company_address').html(data.enterprises[0].region+data.enterprises[0].address);
					}
					
				}
			});
		}
	});

	/*出借人姓名清空，其详细信息也需要清空*/
	$("#lender_member_name").on("keyup",function(){
		var name = $(this).val();
		if(typeof name =="undefined" || name ==""){
			removeLenderMemberDetail();
		}
	});
	
	/*借款人姓名清空，其他详细信息也需要清空*/
	$("#borrow_member_name").on("keyup",function(){
		var name = $(this).val();
		if(typeof name =="undefined" || name ==""){
			removeBorrowerMemberDetail();
		}
	});

	/**
	 * 新增保存
	 */
	$('#save_debt').on('click', function() {
		$("input[name='originalDebtNumber']").removeAttr("ignore");
		$("#form-field-debtType").removeAttr("ignore");
		getGuarantyType();
		getCollateralDetail();
		getPledgeDetail();
		var creditType = $('#credit_type_sel').find("option:selected").val();
		if(creditType=='houseRecord' || creditType=='carPayIn'  || creditType=='carBusiness' || creditType=='runCompany'){
			getCreditDetail();//信用详细信息
		}
		removeHiddenVaildation();
		if (!checkInterestEndDate()) {
			return false;
		};
		if(!preCheckForDebtAdd()){
			return;
		}
		if (debtForm.check(false)) {
			$(this).attr("disabled","disabled");
			$('#debt_form').xform('post', config.urlMap.save, function(data) {
				if (!data.success) {
					if (!!data.resultCodeEum) {
						bootbox.alert(data.resultCodeEum[0].msg,function(){
							$('#save_debt').removeAttr("disabled");
						});
					} else {
						bootbox.alert("添加债权失败!",function(){
							$('#save_debt').removeAttr("disabled");
						});
					}
					
				} else {
					bootbox.alert("添加债权成功!", function() {
						debtTable.fnDraw();
						window.top.closeActiveIframe();
						$('#save_debt').removeAttr("disabled");
					});
				}
			});
		}
	});

	/**
	 * 编辑
	 */
	$("#edit_debt").click(function() {
		getGuarantyType();
		getCollateralDetail();
		getPledgeDetail();
		var debtType= $("#form-field-debtType").find("option:selected").val();
		var creditType = $('#credit_type_sel').find("option:selected").val();
		if(debtType=='credit'){
			if(creditType=='houseRecord'||creditType=='carPayIn' || creditType=='carBusiness' || creditType=='runCompany'){
				getCreditDetail();//信用详细信息
			}
		}
		removeHiddenVaildation();
		if (!checkInterestEndDate()) {
			return;
		}
		if(!preCheckForDebtAdd()){
			return;
		}
		if (debtForm.check(false)) {
			$(this).attr("disabled","disabled");
			$('#debt_form').xform('post', config.urlMap.update, function(data) {
				if (!data.success) {
					var flag = "债权更新失败!";
					if(!!data.resultCodeEum){
						flag = data.resultCodeEum[0].msg;
					}
					bootbox.alert(flag,function(){
						$('#edit_debt').removeAttr("disabled");
					});
				} else {
					bootbox.alert("债权更新成功!", function() {
						window.top.closeActiveIframe();
						$('#edit_debt').removeAttr("disabled");
					});
					
				}
			});

		}
	});

});


function showLenderMemberDetail() {
	var detailBase = $("<div class='lenderMemberDetail'><div class='col-sm-3'><span>身份证：</span><span class='lenderMinfo' id='lender_identityNumber'></span></div><div class='col-sm-3'><span>联系电话：</span><span class='lenderMinfo' id='lender_mobile'></span></div></div>");
	$('#lender-member-detail').append(detailBase);
	var detail = $("<div class='form-group clearfix lenderMemberDetail'><div class='col-sm-3'><span>联系地址：</span><span class='lenderMinfo' id='lender_address'></span></div><div class='col-sm-2'><span>性别：</span><span class='lenderMinfo' id='lender_sex'></span></div><div class='col-sm-2'><span>婚姻：</span><span class='enderMinfo' id='lender_marriage'></span></div><div class='col-sm-2'><span>学历：</span><span class='lenderMinfo' id='lender_highEdu'></span></div></div>")
	$('#lender-member-detail').after(detail);
}

function showLenderCompanyDetail() {
	var detailPart1 = $("<div class='lenderCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>企业名称 *：</span>" +
			"<span id='l_company_name' ></span><!-- name -->" +
			"</div>" +
			"<div>" +
			"<span>注册时间 *：</span>" +
			"<span id='l_company_regeditDate'></span><!-- regedit_date --></div></div>");
	$('#lender-company-detail').append(detailPart1);
	var detailPart3 = $("<div class='form-group clearfix lenderCompanyDetail'>" +
			"<div class='col-sm-6'>" +
			"<span>联系地址 *：</span>" +
			"<span id='l_company_address'></span><!-- regin + address --></div>" );
	$('#lender-company-detail').after(detailPart3);
	var detailPart2 = $("<div class='form-group clearfix lenderCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>公司所在地*：</span>" +
			"<span id='l_company_region'></span><!-- region -->" +
			"</div><div class='col-sm-3'>" +
			"<span>联系电话*：</span>" +
			"<span id='l_company_telephone'></span><!-- region -->" +
			"</div><div class='col-sm-2'>" +
			"<span>注册资本*：</span>" +
			"<span id='l_company_income' ></span>万<!-- income --></div></div>");
	$('#lender-company-detail').after(detailPart2);
}


function removeLenderMemberDetail(){
	$(".lenderMemberDetail").remove();
}

function showBorrowerMemberDetail() {
	var detailBase = $("<div class='borrowMemberDetail'><div class='col-sm-3'><span>身份证：</span><span class='borrowminfo' id='borrow_identityNumber'></span></div><div class='col-sm-3'><span>联系电话：</span><span class='borrowminfo' id='borrow_mobile'></span></div></div>");
	$('#borrower-member-detail').append(detailBase);
	var detail = $("<div class='form-group clearfix borrowMemberDetail' ><div class='col-sm-3'><span>联系地址：</span><span class='borrowMinfo' id='borrow_address'></span></div><div class='col-sm-2'><span>性别：</span><span class='borrowMinfo' id='borrow_sex'></span></div><div class='col-sm-2'><span>婚姻：</span><span class='borrowMinfo' id='borrow_marriage'></span></div><div class='col-sm-2'><span>学历：</span><span class='borrowMinfo' id='borrow_highEdu'></span></div></div>")
	$('#borrower-member-detail').after(detail);
}

function removeBorrowerMemberDetail(){
	$(".borrowMemberDetail").remove();
}

function showBorrowerCompanyDetail() {
	var detailPart1 = $("<div class='borrowerCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>法人姓名 *：</span>" +
			"<span id='b_company_legalName' ></span><!-- name -->" +
			"</div>" +
			"<div>" +
			"<span>注册时间 *：</span>" +
			"<span id='b_company_regeditDate'></span><!-- regedit_date --></div></div>");
	$('#borrower-company-detail').append(detailPart1);
	var detailPart3 = $("<div class='form-group clearfix borrowerCompanyDetail'>" +
			"<div class='col-sm-6'>" +
			"<span>联系地址 *：</span>" +
			"<span id='b_company_address'></span><!-- regin + address --></div>" +
			"<div class='col-sm-4'><span >可用授信额度 *：</span>" +
			"<span id='b_company_availableCreditAmount'></span><!-- availableCreditAmount --></div>" +
			"</div>");
	$('#borrower-company-detail').after(detailPart3);
	var detailPart2 = $("<div class='form-group clearfix borrowerCompanyDetail'>" +
			"<div class='col-sm-3'>" +
			"<span>公司所在地*：</span>" +
			"<span id='b_company_region'></span><!-- region -->" +
			"</div><div class='col-sm-3'>" +
			"<span>联系电话*：</span>" +
			"<span id='b_company_telephone'></span><!-- region -->" +
			"</div><div class='col-sm-2'>" +
			"<span>注册资本*：</span>" +
			"<span id='b_company_income' ></span>万<!-- income --></div></div>");
	$('#borrower-company-detail').after(detailPart2);
}

/**
 * 获取抵押物详细信息
 */
function getCollateralDetail() {
	var collType = $('#collateral_type_sel').find("option:selected").val();
	switch (collType) {
		case 'car':
			var instal = $("#instalment").val();
			var inputObj = {};
			if(instal==1){
				$('#buyCar_detail textarea').each(function() {
					var name = $(this).attr('iname'),
					val = $(this).val();
					inputObj[name] = val;
				});
			}else{
				$('#car_detail input').each(function() {
					var name = $(this).attr('iname'),
					val = $(this).val();
					inputObj[name] = val;
				});
			}
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#coll_value').val($('#car_scgz').val());
			$("#coll_collateralType").val(collType);
			$('#credit_debt_type_value').val("collateral");
			break;
		case 'house':
			var inputObj = {};
			$('#house_detail input').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#coll_value').val($('#house_scgz').val());
			$("#coll_collateralType").val(collType);
			$('#credit_debt_type_value').val("collateral");
			break;
		default:
			break;
	}
}

/**
 * 获取质押物详细信息
 */
function getPledgeDetail() {
	var pledType = $('#pledge_type_sel').find("option:selected").val();
	switch (pledType) {
		case 'car':
			var inputObj = {};
			$('#car_detail input').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#pled_detail').val(JSON.stringify(inputObj));
			$('#pled_value').val($('#car_scgz').val());
			break;
		case 'newCar':
			var inputObj = {};
			$('#newCar_detail textarea').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#pled_detail').val(JSON.stringify(inputObj));
			$('#pled_value').val("");
			break;
		case 'equity'://股权融和新车融一样处理
			var inputObj = {};
			$('#newCar_detail textarea').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#pled_detail').val(JSON.stringify(inputObj));
			$('#pled_value').val("");
			break;
		default:
			var inputObj = {};
			$('#pled_detail').val("");
			$('#pled_value').val("");
			$('#pled_id').val("");
	}
}

/**
 * 获取信用详细信息
 */
function getCreditDetail() {
	var creditType = $('#credit_type_sel').find("option:selected").val();
	switch (creditType) {
		case 'houseRecord':
			var inputObj = {};
			$('#houseRecord_detail textarea').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#credit_debt_type_value').val("credit");
			$("#coll_collateralType").val(creditType);
			break;
		case 'carPayIn':
			var inputObj = {};
			$('#carPayIn_detail textarea').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#credit_debt_type_value').val("credit");
			$("#coll_collateralType").val(creditType);
			break;
		case 'carBusiness':
			var inputObj = {};
			$('#base_detail textarea').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#credit_debt_type_value').val("credit");
			$("#coll_collateralType").val(creditType);
			break;
		case 'runCompany':
			var inputObj = {};
			$('#runCompany_detail textarea,#runCompany_detail input').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#coll_detail').val(JSON.stringify(inputObj));
			$('#credit_debt_type_value').val("credit");
			$("#coll_collateralType").val(creditType);
			break;
		default:
			var inputObj = {};
			$('#coll_detail').val("");
			$('#credit_debt_type_value').val("collateral");
			$("#coll_collateralType").val("");
	}
}

/**
 * 获取担保物具体的类型
 */
function getGuarantyType() {
	var debtType = $('#form-field-debtType').val();
	var guarantyType = "";
	if (debtType == 'collateral') {
		guarantyType = $('#collateral_type_sel').find("option:selected").val();
	}
	if (debtType == 'pledge') {
		guarantyType = $('#pledge_type_sel').find("option:selected").val();
	}
	if(debtType=='credit'){
		guarantyType = $("#credit_type_sel").find("option:selected").val();
	}
	$('#guaranty_type').val(guarantyType);
}

/**
 * 验证最后一条本息数据的结束时间是否小于借款周期的结束时间
 */
function checkInterestEndDate() {
	var interestEndDate = new Date($('#sample-table-1 tBody tr:last .i_e_date:eq(0)').val());
	var debtEndDate = new Date($('#form-field-endDate').val());
	if (interestEndDate.getDate() > debtEndDate.getDate()) {
		bootbox.alert("最后一期本息结束时间需小于借款周期的结束时间！");
		return false;
	}
	return true;
}
/**
 * 填写"债权信息内容"自动填充"债权本息信息"
 */
$(".autoFill").on("change",function() {
	if ($(this).hasClass("autoAmount")) {
		$('#auto_amount').html($(this).val());
		return;
	}

	var pled_type = $('#pledge_type').find('option:selected').val();
	switch (pled_type) {
		case 'car':
			var inputObj = {};
			$('#car_detail input').each(function() {
				var name = $(this).attr('iname'),
					val = $(this).val();
				inputObj[name] = val;
			});
			$('#pled_detail').val(JSON.stringify(inputObj));
			$('#pled_value').val($('#car_scgz').val());
			break;
		default:
			var inputObj = {};
			$('#pled_detail').val(JSON.stringify(inputObj));
	}
});

/*开始时间自动设置*/
function autoStartDate(){
	var sDate = $("#form-field-startDate").val(),//开始时间
		eDate = $("#form-field-endDate").val(),
		startData = new Date(sDate),
		interestFrom = $('#interest_from').val();//起息日
	//本息表头的开始时间
	$('#start_date').html(sDate);
	//债权本息表第一个日期的计算
	if($("#form-field-returnType").val()=="avg_principal"){
		$('#interest_s_date_1').val(formatDate(startData.getTime()));
	}else{
		$('#interest_s_date_1').val(formatDate(startData.getTime() + (interestFrom * 24 * 60 * 60 * 1000)));
	}
}
/*结束时间自动设置，判断是否大于开始时间*/
function autoEndDate(){
	var sDate = $('#form-field-startDate').val(),//开始时间
		eDate = $("#form-field-endDate").val();//结束时间
	//判断结束时间是否大于开始时间
	var num = compareTwoDate(sDate,eDate);
	if (num <= 0) {
		bootbox.alert("结束时间必须大于开始时间！",function(){
			$("#form-field-endDate").val("");
		});
		return false;
	}
	//本息表头的结束时间
	$('#end_date').html("—" + eDate);
}

/*债权本息表当中的时间选择*/
$('#sample-table-1 tbody').on("click",".i_e_date,.i_s_date",function(){
	WdatePicker({
		isShowClear:true,
		readOnly:true,
		dateFmt:'yyyy-MM-dd',
		onpicked:vailInterestEndDate
	});
});

/*本息时间合法性校验*/
function vailInterestEndDate(){
	var edateObj = $(this),
		e_date = edateObj.val(),//结束时间
		s_date = $(this).parent().find(".i_s_date").val(),//开始时间
		debtEndDate = $('.autoEndDate').val();//债权结束时间
		ediff = compareTwoDate(e_date,debtEndDate); 
	//债权周期结束时间与每期结束时间比较
	if (ediff < 0) {
		bootbox.alert("债权本息的结束时间必须小于等于债权周期的结束时间！",function(){
			edateObj.val("");
		});
		return false;
	}
	//每一期的开始结束时间相差天数
	var startEndDiff =compareTwoDate(s_date,e_date); 
	if (startEndDiff < 0) {
		bootbox.alert("结束时间必须大于等于开始时间！",function(){
			edateObj.val("");
		})
		return false;
	}
}

/*比较两个时间的大小
 * 1:end>start
 * 0:end==start
 * -1:start>end
 * */
function compareTwoDate(start,end){
	if(start==""){
		return 1;
	}else if(end==""){
		return -1;
	}else if(start=="" && end ==""){
		return 0;
	}else{
		var dayNum = new Date(end) - new Date(start);
		if(dayNum>0){
			return 1;
		}else if(dayNum<0){
			return -1;
		}else{
			return 0;
		}
	}
}

$('#interest_from').on('change', function() {
	var interestFrom = $(this).val();
	var startData = new Date($('#form-field-startDate').val());
	if($("#form-field-returnType").val()=="avg_principal"){
		$('#interest_s_date_1').val(formatDate(startData.getTime()));
	}else{
		$('#interest_s_date_1').val(formatDate(startData.getTime() + (interestFrom * 24 * 60 * 60 * 1000)));
	}
});

/**
 * 添加期次
 */
$('#add_interestIssue').click(function() {
	if ($('#sample-table-1 tBody tr').length >= 1) {
		var preEndDate = formatDate(new Date($('#sample-table-1 tBody tr:last .i_e_date:eq(0)').val()).getTime() + (1 * 24 * 60 * 60 * 1000));
		var debtEndDate = $('.autoEndDate').val();

		if (new Date(preEndDate) - new Date(debtEndDate) > 0) {
			bootbox.alert('无需再添加期次！');
			return false;
		}
		$("tbody").append("<tr><td>" + ($('tbody tr').length + 1) + "</td><td><input type='text' name='debtInterests[" + ($('tbody tr').length) + "].startDate' value='" + preEndDate + "' class='i_s_date' /> 至  <input type='text' name='debtInterests[" + ($('tbody tr').length) + "].endDate' class='i_e_date' /></td><td></td><td></td><td></td><td></td><td></td><td><select name='debtInterests[" + ($('tbody tr').length) + "].status'><option value='0'>待支付</option><option value='1'>已支付</option></select></td></tr>")
	}
	return false;
});

/**
 * 期次
 */
$('#del_interestIssue').click(function() {
	if ($('#sample-table-1 tBody tr').length > 1) {
		$('#sample-table-1 tBody tr:last').remove();
	} else {
		bootbox.alert('默认期次，不允许删除！');
	}
	return false;
});

//自动分期
$("#auto_interest").click(function() {
	$("input[name='originalDebtNumber']").attr("ignore","ignore");
	$("select[name='debtType']").attr("ignore","ignore");
	var returnType = $('#form-field-returnType').val();
	if(typeof returnType =="undefined" || returnType ==""){
		bootbox.alert("请选择还款方式！");
		return false;
	}
	var interestFrom = $('#interest_from').val();
	if(typeof interestFrom =="undefined" || interestFrom ==""){
		bootbox.alert("请选择起息方式！");
		return false;
	}
	var sDate = $("#form-field-startDate").val(),//开始时间
	eDate = $("#form-field-endDate").val();
	if(typeof sDate =="undefined" || sDate ==""){
		bootbox.alert("请选择借款周期开始时间！");
		return false;
	}
	if(typeof eDate =="undefined" || eDate ==""){
		bootbox.alert("请选择借款周期结束时间！");
		return false;
	}
	//autoEndDate();
	$.ajax({
		url:config.urlMap.autoInterest,
		data:{
				'returnType':returnType,
				'interestFrom':interestFrom,
				'sDate':sDate,
				'eDate':eDate
				},
		dataType:"json",
		success:function(data){
			if(data.success){
				var index=$('#sample-table-1 tBody tr').length;
				if(index>1){
					$("#sample-table-1 tBody tr:gt(0)").remove();
					/* for(i=0;i<index;i++){
		                        table.removeChild(tbodies[i]);
		                }*/
				}
				/*if ($('#sample-table-1 tBody tr').length > 1) {
					$('#sample-table-1 tBody tr:last').remove();
				}
				*/
				eachDebtInterest(data.resultList);
			}
			
		}
	});
});
//计算每期次的本息
$("#cal_interest").click(function() {
	$("input[name='originalDebtNumber']").removeAttr("ignore");
	$("#form-field-debtType").removeAttr("ignore");
	// 还款方式
	var returnType = $('#form-field-returnType').val();
	if (returnType != "monthly_paid" && returnType != "once_paid" && returnType != "avg_principal" ) {
		bootbox.alert("目前只支持“按日计息，按月付息，到期还本、一次性还本付息和等本等息”,请重新选择还款方式！");
		return;
	}
	
	if(returnType == "monthly_paid" || returnType == "once_paid"){
		$('#sample-table-1 tbody tr').each(function(i) {
			var endDateOb = $(this).find(".i_e_date");
			endDateOb.parent().nextAll().eq(0).html("");
			endDateOb.parent().nextAll().eq(1).html("");
			endDateOb.parent().nextAll().eq(2).html("");
			endDateOb.parent().nextAll().eq(3).html("");
			endDateOb.parent().nextAll().eq(4).html("");
			//日息 ＝ 年利率*总额/360 并四舍五入保留2位小数
			var dayRate = ($('#auto_rate').text() * $("#auto_amount").text() / 36000).toFixed(2);
			//天数
			var s_date = endDateOb.prevAll("input:not(.i_e_date)").val();
			var e_date = endDateOb.val();
			if (!!e_date) {
				endDateOb.parent().nextAll().eq(0).html(dayRate);
				var debtEndDate = $('.autoEndDate').val();
				var ediff = new Date(e_date) - new Date(debtEndDate)
				if (ediff > 0) {
					bootbox.alert("债权本息的结束时间必须小于债权周期的结束时间！")
					return;
				}
				var dayNum = (new Date(e_date) - new Date(s_date)) / (24 * 60 * 60 * 1000) + 1; //每一期的开始结束时间相差天数
				if (dayNum <= 0) {
					bootbox.alert("结束时间必须大于开始时间！")
					return;
				}
				
				//应付利息 四舍五入，保留2位小数
				var receInterest = (dayRate * dayNum).toFixed(2);
				endDateOb.parent().nextAll().eq(1).html(dayNum);
				endDateOb.parent().nextAll().eq(2).html(endDateOb.val());
				endDateOb.parent().nextAll().eq(3).html(receInterest);
				if (debtEndDate == e_date) {
					var amount = $('#form-field-amount').val();
					endDateOb.parent().nextAll().eq(4).html(Number(amount).toFixed(2));
				} else {
					endDateOb.parent().nextAll().eq(4).html("0.00");
				}
			}
		});
	}else if(returnType == "avg_principal"){//等本等息
		$('#sample-table-1 tbody tr').each(function(i) {
			//清空债券本息表计算值
			var endDateOb = $(this).find(".i_e_date");
			endDateOb.parent().nextAll().eq(0).html("");
			endDateOb.parent().nextAll().eq(1).html("");
			endDateOb.parent().nextAll().eq(2).html("");
			endDateOb.parent().nextAll().eq(3).html("");
			endDateOb.parent().nextAll().eq(4).html("");
			
			var debtStartDate = $('.autoStartDate').val(),
			dayRate,//日息
			s_date = endDateOb.prevAll("input:not(.i_e_date)").val(),//开始时间
			e_date = endDateOb.val(),//结束时间
			totalAmount = $("#auto_amount").text(),//项目总额
			period = $('#sample-table-1 tbody tr').length;//分期数
			if (!!e_date) {
				endDateOb.parent().nextAll().eq(0).html(dayRate);
				var debtEndDate = $('.autoEndDate').val();
				var ediff = new Date(e_date) - new Date(debtEndDate)
				if (ediff > 0) {
					bootbox.alert("债权本息的结束时间必须小于债权周期的结束时间！")
					return;
				}
				var dayNum = (new Date(e_date) - new Date(s_date)) / (24 * 60 * 60 * 1000) + 1; //每一期的开始结束时间相差天数
				if (dayNum <= 0) {
					bootbox.alert("结束时间必须大于开始时间！")
					return;
				}
				//第一期自然月天数
				//应付利息 四舍五入，保留2位小数
				var rate = $("#form-field-monthlyRate").val();
				var aunRate = $("#form-field-annualizedRate").val();
				
				if(!rate){
					rate = aunRate/12;
				}
				var receInterest = ( totalAmount * rate/100).toFixed(2);
				
				dayRate = (receInterest / dayNum).toFixed(2);
				endDateOb.parent().nextAll().eq(0).html(dayRate);
				endDateOb.parent().nextAll().eq(1).html(dayNum);
				endDateOb.parent().nextAll().eq(2).html(endDateOb.val());
				
				endDateOb.parent().nextAll().eq(3).html(receInterest);
				if(i==period-1){
					var proAmount = (totalAmount/period).toFixed(2)*(period-1);
					endDateOb.parent().nextAll().eq(4).html((totalAmount-proAmount).toFixed(2));
				}else{
					endDateOb.parent().nextAll().eq(4).html((totalAmount/period).toFixed(2));
				}
			}
			});
		}
	return false;
});

//edit
function findDebtById(id) {
	$.post(config.urlMap.find + id, function(data) {
		if (!!data.debtBiz) {
			eachDebtField(data.debtBiz);
			eachDebtInterest(data.debtBiz.debtInterests);
		}
		if (!!data.lenderMember && !!data.debtBiz) {
			lmBizField(data.debtBiz.lenderType,data.lenderMember);
		}
		if (!!data.borrowerMember && !!data.debtBiz) {
			bmBizField(data.debtBiz.borrowerType,data.borrowerMember);
		}
		if (!!data.lenderAttachments) {
			eachAttachements(data.lenderAttachments, "dropzone-debt-lender");
		}
		if (!!data.borrowerAttachments) {
			eachAttachements(data.borrowerAttachments, "dropzone-debt-borrower");
		}
		if (!!data.collateralAttachments) {
			eachAttachements(data.collateralAttachments, "dropzone-debt-collateral");
		}
		if (!!data.contractAttachments) {
			eachAttachements(data.contractAttachments, "dropzone-debt-contract");
		}
		if (!!data.legalAttachments) {
			eachAttachements(data.legalAttachments, "dropzone-debt-legal");
		}
		if (!!data.baseAttachments) {
			eachAttachements(data.baseAttachments, "dropzone-debt-base");
		}
		if (!!data.signAttachments) {
			eachAttachements(data.signAttachments, "dropzone-debt-sign");
		}
	});
}

//emergency
function findDebtByIdForEmergencyEdit(id) {
	$.post(config.urlMap.find + id, function(data) {
		if (!!data.debtBiz) {
			eachDebtField(data.debtBiz);
			eachDebtInterest(data.debtBiz.debtInterests);
			//不可紧急修改字段
			$("input[name='startDate']").attr("disabled","disabled");
			$("input[name='endDate']").attr("disabled","disabled");
			$("select[name='interestFrom']").attr("disabled","disabled");
			$("select[name='returnType']").attr("disabled","disabled");
			$("input[name='amount']").attr("disabled","disabled");
			$("input[name='annualizedRate']").attr("disabled","disabled");
			//债权本息表不可修改
			$("#sample-table-1 tbody tr").find(".i_s_date,.i_e_date").attr("disabled","disabled");
			$("#add_interestIssue,#del_interestIssue").attr("disabled","disabled");
			$("#cal_interest").attr("disabled","disbaled");
			$("#form-field-monthlyRate").attr("disabled","disbaled");//购车分期具有的属性
			$("#form-field-instalment").attr("disabled","disbaled");//购车分期具有的属性
		}
		if (!!data.lenderMember && !!data.debtBiz) {
			lmBizField(data.debtBiz.lenderType,data.lenderMember);
		}
		if (!!data.borrowerMember && !!data.debtBiz) {
			bmBizField(data.debtBiz.borrowerType,data.borrowerMember);
		}
		if (!!data.lenderAttachments) {
			eachAttachements(data.lenderAttachments, "dropzone-debt-lender");
		}
		if (!!data.borrowerAttachments) {
			eachAttachements(data.borrowerAttachments, "dropzone-debt-borrower");
		}
		if (!!data.collateralAttachments) {
			eachAttachements(data.collateralAttachments, "dropzone-debt-collateral");
		}
		if (!!data.contractAttachments) {
			eachAttachements(data.contractAttachments, "dropzone-debt-contract");
		}
		if (!!data.legalAttachments) {
			eachAttachements(data.legalAttachments, "dropzone-debt-legal");
		}
		if (!!data.baseAttachments) {
			eachAttachements(data.baseAttachments, "dropzone-debt-base");
		}
		if (!!data.signAttachments) {
			eachAttachements(data.signAttachments, "dropzone-debt-sign");
		}
	});
}


//detail
function detailDebtById(id) {
	$.post(config.urlMap.find + id, function(data) {
		if (!!data.debtBiz) {
			eachDebtField(data.debtBiz);
			eachDebtInterest(data.debtBiz.debtInterests);
		}
		if (!!data.lenderMember && !!data.debtBiz) {
			lmBizField(data.debtBiz.lenderType,data.lenderMember);
		}
		if (!!data.borrowerMember && !!data.debtBiz) {
			bmBizField(data.debtBiz.borrowerType,data.borrowerMember);
		}
		if (!!data.lenderAttachments) {
			detailEachAttachements(data.lenderAttachments, "dropzone_debt_lender");
		}
		if (!!data.borrowerAttachments) {
			detailEachAttachements(data.borrowerAttachments, "dropzone_debt_borrower");
		}
		if (!!data.collateralAttachments) {
			detailEachAttachements(data.collateralAttachments, "dropzone_debt_collateral");
		}
		if (!!data.contractAttachments) {
			detailEachAttachements(data.contractAttachments, "dropzone_debt_contract");
		}
		if (!!data.legalAttachments) {
			detailEachAttachements(data.legalAttachments, "dropzone_debt_legal");
		}
		if (!!data.baseAttachments) {
			detailEachAttachements(data.baseAttachments, "dropzone_debt_base");
		}
		if (!!data.signAttachments) {
			detailEachAttachements(data.signAttachments, "dropzone_debt_sign");
		}
		$('#debt_form').find('input,select,textarea,button').attr('disabled', 'disabled');
	});
}

//edit:债权基本信息
function eachDebtField(debtBiz) {
	$.each(debtBiz, function(name, value) {
		if (name == "originalDebtNumber") {
			$("#form-field-originalDebtNumber").val(value);
		} else if (name == "debtType") {
			$("#form-field-debtType").val(value);
			var guaranteeThing = "",
				thingDetail = "{}",
				thingId = "";
			if (value == "collateral"||value == "credit") {
				if (!!debtBiz.debtCollateral) {
					guaranteeThing = debtBiz.debtCollateral.collateralType;
					thingDetail = debtBiz.debtCollateral.collateralDetails;
					thingId = debtBiz.debtCollateral.id;
					instal = debtBiz.instalment;
				}
				$("#credit_debt_type_value").val(value);
			}
			if (value == "pledge") {
				if (!!debtBiz.debtPledge) {
					guaranteeThing = debtBiz.debtPledge.pledgeType;
					thingDetail = debtBiz.debtPledge.pledgeDetails;
					thingId = debtBiz.debtPledge.id;
					instal = debtBiz.instalment;
				}
			}
			var thingObj = jQuery.parseJSON(thingDetail);
			collateralSelVisiable(value, guaranteeThing, thingObj, thingId,instal);
		} else if (name == "amount") {
			$("#form-field-amount").val(value)
			$('#auto_amount').html(value);
		} else if (name == "returnType") {
			$("#form-field-returnType").val(value);
		} else if (name == "startDate") {
			$("#form-field-startDate").val(formatDate(value));
			$('#start_date').html(formatDate(value));
		} else if (name == "endDate") {
			$("#form-field-endDate").val(formatDate(value));
			$('#end_date').html("—"+formatDate(value));
		} else if (name == "annualizedRate") {
			$("#j-annualizedRate").val(value.toFixed(2));
			$('#auto_rate').html(value.toFixed(2));
		} else if (name == "offlineAnnualizedRate") {
			$("#form-field-offlineAnnualizedRate").val(value);
		}else if (name == "interestFrom") {
			$("#interest_from").val(value);
		} else if (name == "loanUse") {
			$("#form-field-loanUse").val(value);
		} else if (name == "lenderMember") {
			if (!!value) {
				$("#l_member_type").val(debtBiz.lenderType);
			}
		} else if (name == "borrowMemberBaseBiz") {
			$("#b_member_type").val(debtBiz.borrowerType);
		} else if (name=="collateralType"){
			$("#coll_collateralType").val(collateralType);
		} else if (name =="instalment"){
			var switchBox=$("#instalment");
			if(value==0){
				$("#form-field-annualizedRate").val(debtBiz.annualizedRate);
				switchBox.val(0).siblings(".ace-switch").removeAttr("checked");
			}else{
				switchBox.val(1);
				switchBox.siblings(".ace-switch").click();
				$("#form-field-monthlyRate").val((debtBiz.annualizedRate/12).toFixed(2));
			}
		}
	});
}

//edit:债权本息
function eachDebtInterest(debtInterests) {
	if (!!debtInterests) {
		for (var i = 0; i < debtInterests.length; i++) {
			if (i > 0) {
				addRow();
			}
			var trObj = $('#sample-table-1 tbody tr')[i];
			$(trObj).find("input[name='debtInterests[" + i + "].id']").val(debtInterests[i].id); //本息id
			$(trObj).find("input[name='debtInterests[" + i + "].startDate']").val(formatDate(debtInterests[i].startDate)); //开始时间
			$(trObj).find("input[name='debtInterests[" + i + "].endDate']").val(formatDate(debtInterests[i].endDate)); //结束时间
			$(trObj).find("select[name='debtInterests[" + i + "].status']").val(debtInterests[i].status); //状态
			var dayNum = (new Date(formatDate(debtInterests[i].endDate)) - new Date(formatDate(debtInterests[i].startDate))) / (24 * 3600 * 1000) + 1;
			var tds = $(trObj).find("td");
			tds.eq(2).html(Number(debtInterests[i].unitInterest).toFixed(2)); //日息
			tds.eq(3).html(dayNum); //天数
			tds.eq(4).html(formatDate(debtInterests[i].endDate)); //付息日
			tds.eq(5).html(Number(debtInterests[i].payableInterest).toFixed(2)); //应付利息	
			tds.eq(6).html(Number(debtInterests[i].payablePrincipal).toFixed(2)); //应付本金
		}
	}
}

//edit:附件
function eachAttachements(attachments, fileId) {
	var customDropzone = Dropzone.forElement("#"+fileId);
	$.each(attachments, function(n, v) {
		addImageToDropzone(customDropzone, v);
		customColorBox(v.module);
	});
}

//detail
function detailEachAttachements(lenderAttachments, fileId) {
	var customDropzone = Dropzone.forElement("#"+fileId);
	$.each(lenderAttachments, function(n, v) {
		addImageToDropzone(customDropzone, v);
		customColorBox(v.module);
	});
	disableDropzone();
}

function lmBizField(lenderType,lmBiz) {
	if (!!lmBiz.member) {
		editLMemberVisible(lenderType, lmBiz);
	}
}

function bmBizField(borrowerType,bmBiz) {
	if (!!bmBiz.member) {
		editBMemberVisible(borrowerType, bmBiz);
	}
}

/**
 * 设置质押物或抵押物类型显示 及 具体质押物抵押物内容显示
 */
function collateralSelVisiable(type, guaranteeThing, thingObj, thingId,instal) {
	switch (type) {
		case "collateral":
			$("#collateral_type").css("display", "block");
			$("#pledge_type").css("display", "none");
			$("#credit_type").css("display", "none");
			$("#coll_id").val(thingId);
			colThingVisiable(guaranteeThing, thingObj,instal);
			break;
		case "pledge":
			$("#pledge_type").css("display", "block");
			$("#collateral_type").css("display", "none");
			$("#credit_type").css("display", "none");
			$("#pled_id").val(thingId);
			pledThingVisiable(guaranteeThing, thingObj,instal);
			break;
		case "credit":
			$("#credit_type").css("display", "block");
			$("#pledge_type").css("display", "none");
			$("#collateral_type").css("display", "none");
			$("#coll_id").val(thingId);
			creditThingVisiable(guaranteeThing, thingObj,instal);
			break;
		default:
			$("#pledge_type").css("display", "none");
			$("#collateral_type").css("display", "none");
			$("#credit_type").css("display", "none");
	}
}

//抵押物详情显示
function colThingVisiable(type, thingObj,instal) {
	$('#collateral_type_sel').val(type);
	switch (type) {
		case "car"://汽车
			$("#car_instalment").css("display", "block")
			$("#house_detail").css("display", "none");
			if(instal == 1){
				$("#car_detail").css("display", "none");
				$("#buyCar_detail").css("display","block")
				$("#d_annualized_rate").css("display", "none");
				$("#d_monthly_rate").css("display", "block");
				//设置checkbox
				if (!!thingObj) {
					baseDetail("#buyCar_detail",thingObj);
				}
			}else{
				$("#car_detail").css("display", "block");
				$("#buyCar_detail").css("display","none")
				$("#d_annualized_rate").css("display", "block");
				$("#d_monthly_rate").css("display", "none");
				if (!!thingObj) {
					carDetail(thingObj);
				}
			}
			break;
		case "house":
			$("#house_detail").css("display", "block");
			$("#car_detail").css("display", "none");
			if (!!thingObj) {
				houseDetail(thingObj);
			}
			break;
		default:
			$("#house_detail").css("display", "none");
			$("#car_detail").css("display", "none");
	}
}

//质押物详情显示
function pledThingVisiable(type, thingObj) {
	$('#pledge_type_sel').val(type);
	switch (type) {
		case "car":
			$("#car_instalment").css("display", "none")
			$("#car_detail").css("display", "block");
			$("#house_detail").css("display", "none");
			$("#newCar_detail").css("display", "none");
			if (!!thingObj) {
				carDetail(thingObj);
			}
			break;
		case "newCar":
			$("#car_instalment").css("display", "none")
			$("#car_detail").css("display", "none");
			$("#house_detail").css("display", "none");
			$("#newCar_detail").css("display", "block");
			if (!!thingObj) {
				newCarDetail(thingObj);
			}
			break;
		case 'equity'://股权融和新车融一样处理
			$("#car_instalment").css("display", "none")
			$("#car_detail").css("display", "none");
			$("#house_detail").css("display", "none");
			$("#newCar_detail").css("display", "block");
			if (!!thingObj) {
				newCarDetail(thingObj);
			}
			break;
		default:
			$("#car_instalment").css("display", "none")
			$("#house_detail").css("display", "none");
			$("#car_detail").css("display", "none");
			$("#newCar_detail").css("display", "none");
	}
}
//信用详情显示
function creditThingVisiable(type, thingObj) {
	$('#credit_type_sel').val(type);
	switch (type) {
		case "houseRecord":
			$("#houseRecord_detail").css("display", "block");
			if (!!thingObj) {
				houseRecordDetail(thingObj);
			}
			break;
		case "carPayIn":
			$("#carPayIn_detail").css("display", "block");
			if (!!thingObj) {
				carPayInDetail(thingObj);
			}
			break;
		case "carBusiness":
			$("#base_detail").css("display", "block");
			if (!!thingObj) {
				baseDetail("#base_detail",thingObj);
			}
			break;
		case "runCompany":
			$("#runCompany_detail").css("display", "block");
			if (!!thingObj) {
				runCompanyDetail("#runCompany_detail",thingObj);
			}
			break;
		default:
			$("#houseRecord_detail").css("display", "none");
			$("#carPayIn_detail").css("display", "none");
			$("#base_detail").css("display", "none");
			$("#runCompany_detail").css("display", "none");
	}
}

//设置房产详情
function houseDetail(house) {
	$.each(house, function(name, val) {
		$("input[iname='"+name+"']").val(val);
	});
}

//设置汽车详情
function carDetail(car) {
	$.each(car, function(name, val) {
		switch (name) {
			case "car_cx":
				$("input[iname='car_cx']").val(val);
				break;
			case "car_djrq":
				$("input[iname='car_djrq']").val(val);
				break;
			case "car_xsgl":
				$("input[iname='car_xsgl']").val(val);
				break;
			case "car_gz":
				$("input[iname='car_gz']").val(val);
				break;
			case "car_jg":
				$("input[iname='car_jg']").val(val);
				break;
			default:
		}

	});
}

//设置汽车合格证详情
function newCarDetail(newCar){
	$.each(newCar, function(name, val) {
		switch (name) {
			case "car_ms":
				$("textarea[iname='car_ms']").html(val);
				break;
			default:
		}
	});
}

//设置房屋备注详情
function houseRecordDetail(houseRecord){
	$.each(houseRecord, function(name, val) {
		switch (name) {
			case "houseRecord_info":
				$("textarea[iname='houseRecord_info']").html(val);
				break;
			default:
		}
	});
}

//设置房屋备注详情
function carPayInDetail(carPayIn){
	$.each(carPayIn, function(name, val) {
		switch (name) {
			case "carPayIn_info":
				$("textarea[iname='carPayIn_info']").html(val);
				break;
			default:
		}
	});
}

//设置基本信息详情
function baseDetail(detailDivId,baseInfo){
	$.each(baseInfo, function(name, val) {
		switch (name) {
		case "base_info":
			$(detailDivId+" textarea[iname='base_info']").html(val);
			break;
		default:
		}
	});
}

//设置信用类经营融项目的性情
function runCompanyDetail(detailDivId,baseInfo){
	$.each(baseInfo, function(name, val) {
		switch (name) {
		case "base_info":
			$(detailDivId+" textarea[iname='base_info']").html(val);
			break;
		case "db_company":
			$(detailDivId+" input[iname='db_company']").val(val);
			break;
		default:
		}
	});
}


//edit：债权人的信息显示
function editLMemberVisible(type, memberBiz) {
	switch (type) {
		case 1:
			$('#l_personal_info').css("display", "block");
			$('#l_company_info').css("display", "none");
			showLenderMemberDetail();
			lpMemberDetail(memberBiz);
			break;
		case 2:
			$('#l_personal_info').css("display", "none");
			$('#l_company_info').css("display", "block");
			showLenderCompanyDetail();
			lCompanyDetail(memberBiz);
			break;
		default:
			$('#l_personal_info').css("display", "none");
			$('#l_company_info').css("display", "none");
	}
}
//edit：借款人的信息显示
function editBMemberVisible(type, memberBiz) {
	switch (type) {
		case 1:
			$('#b_company_info').css("display", "none");
			$('#b_personal_info').css("display", "block");
			showBorrowerMemberDetail();
			bpMemberDetail(memberBiz);
			break;
		case 2:
			$('#b_company_info').css("display", "block");
			$('#b_personal_info').css("display", "none");
			showBorrowerCompanyDetail();
			cMemberDetail(memberBiz);
			break;
		default:
			$('#b_company_info').css("display", "none");
			$('#b_personal_info').css("display", "none");
	}
}
//edit：个人基础信息显示
function lpMemberDetail(memberBiz) {
	if (!!memberBiz.member) {
		$.each(memberBiz.member, function(name, value) {
			switch (name) {
				case "id":
					$('#lender_member_id').val(value);
					break;
				case "trueName":
					$("#lender_member_name").val(value);
					break;
				case "identityNumber":
					$('#lender_identityNumber').html(value);
					break;
				case "mobile":
					$('#lender_mobile').html(value);
					break;
				case "sex":
					var sex = getDictLabel(config.sex, value);
					$('#lender_sex').html(sex);
					break;
				default:
			}
		});
	}
	if (!!memberBiz.memberInfo) {
		$.each(memberBiz.memberInfo, function(name, value) {
			switch (name) {
				case "address":
					$('#lender_address').html(value);
					break;
				case "marriage":
					var marriage = getDictLabel(config.marriage, value);
					$('#lender_marriage').html(marriage);
					break;
				case "highEdu":
					var education = getDictLabel(config.education, value);
					$('#lender_highEdu').html(education);
					break;
				default:
			}
		});
	}
}

//edit：个人基础信息显示
function bpMemberDetail(memberBiz) {
	if (!!memberBiz.member) {
		$.each(memberBiz.member, function(name, value) {
			switch (name) {
				case "id":
					$('#borrow_member_id').val(value);
					break;
				case "trueName":
					$("#borrow_member_name").val(value);
					break;
				case "identityNumber":
					$('#borrow_identityNumber').html(value);
					break;
				case "mobile":
					$('#borrow_mobile').html(value);
					break;
				case "sex":
					var sex = getDictLabel(config.sex, value);
					$('#borrow_sex').html(sex);
					break;
				default:
			}
		});
	}
	if (!!memberBiz.memberInfo) {
		$.each(memberBiz.memberInfo, function(name, value) {
			switch (name) {
				case "address":
					$('#borrow_address').html(value);
					break;
				case "marriage":
					var marriage = getDictLabel(config.marriage, value);
					$('#borrow_marriage').html(marriage);
					break;
				case "highEdu":
					var education = getDictLabel(config.education, value);
					$('#borrow_highEdu').html(education);
					break;
				default:
			}
		});
	}
}

//edit：企业信息显示
function cMemberDetail(memberBiz) {
	if (!!memberBiz.member) {
		$.each(memberBiz.member, function(name, value) {
			switch (name) {
				case "id":
					$("#borrow_member_id").val(value);
					break;
				default:
			}
		});
	}
	if (!!memberBiz.enterprises[0]) {
		$.each(memberBiz.enterprises[0], function(name, value) {
			$("input[id='b_company_"+name+"']").val(value);
			$("span[id='b_company_"+name+"']").html(value);
		});
		$('#b_company_regeditDate').html(formatDate(memberBiz.enterprises[0].regeditDate));
		$('#b_company_address').html(memberBiz.enterprises[0].region+memberBiz.enterprises[0].address);
	}
}

//edit：出借人企业信息显示
function lCompanyDetail(memberBiz) {
	if (!!memberBiz.member) {
		$.each(memberBiz.member, function(name, value) {
			switch (name) {
				case "id":
					$("#lender_member_id").val(value);
					break;
				default:
			}
		});
	}
	if (!!memberBiz.enterprises[0]) {
		$.each(memberBiz.enterprises[0], function(name, value) {
			$("input[id='l_company_"+name+"']").val(value);
			$("span[id='l_company_"+name+"']").html(value);
		});
		$('#l_company_regeditDate').html(formatDate(memberBiz.enterprises[0].regeditDate));
		$('#l_company_address').html(memberBiz.enterprises[0].region+memberBiz.enterprises[0].address);
	}
}

function addRow() {
	$("tbody").append(
		"<tr><td>" + ($('tbody tr').length + 1) + "<input type='hidden' name='debtInterests[" + ($('tbody tr').length) + "].id'></td><td><input type='text' name='debtInterests[" + ($('tbody tr').length) + "].startDate' class='i_s_date'/> 至  <input type='text' name='debtInterests[" + ($('tbody tr').length) + "].endDate' class='i_e_date'/></td><td></td><td></td><td></td><td></td><td></td><td><select name='debtInterests[" + ($('tbody tr').length) + "].status'><option value='0'>待支付</option><option value='1'>已支付</option></select></td></tr>")
}

function removeHiddenVaildation() {
	$('#debt_form div:hidden').each(function() {
		$(this).find('select,input').removeAttr("datatype");
	});
}

function preCheckForDebtAdd() {
	var instalment = $("#form-field-instalment").prop("checked"),
		collateralType = $('#collateral_type').find("option:selected").val(),
		returnType = $("#form-field-returnType").val();
	if(instalment && collateralType=="car" && returnType!="avg_principal" ){
		bootbox.alert("购车融项目，请选择等本等息的还款方式！");
		return false;
	}
	return true;
}
$('.delImage').on('click', function() {
	var dropzoneID =$(this).attr("id");
	var imgID="";
	if(dropzoneID.indexOf("del-")!=-1){
		imgID=dropzoneID.substring(4,dropzoneID.length-1);
	}
	bootbox.confirm("你确定要删除吗?", function(result) {
		if (result) {
			deleteDropzoneAllimage(imgID);
			checkId(dropzoneID);
		}
	});
});
function checkId(dropzoneID){
	if(dropzoneID.indexOf("dropzone-debt-lender")>=0){
		$("#j-json-dropzone-debt-lender").val("");
	}else if(dropzoneID.indexOf("dropzone-debt-borrower")>=0){
		$("#j-json-dropzone-debt-borrower").val("");
	}else if(dropzoneID.indexOf("dropzone-debt-collateral")>=0){
		$("#j-json-dropzone-debt-collateral").val("");
	}else if(dropzoneID.indexOf("dropzone-debt-contract")>=0){
		$("#j-json-dropzone-debt-contract").val("");
	}else if(dropzoneID.indexOf("dropzone-debt-legal")>=0){
		$("#j-json-dropzone-debt-legal").val("");
	}else if(dropzoneID.indexOf("dropzone-debt-base")>=0){
		$("#j-json-dropzone-debt-base").val("");
	}
	
}
$("#endDateSelect").on("change",function(){
	var endDateSelect = $(this).val();
	switch(endDateSelect){
		case "0":
			$("#end_date_search").css("display","block");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","none");
			return;
		case "1":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","block");
			$("#due_date_search").css("display","none");
			return;
		case "2":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","block");
			return;
		case "":
			$("#end_date_search").css("display","none");
			$("#end_date_start_search").css("display","none");
			$("#due_date_search").css("display","none");
			return;
		default:
	}
});
//重置时时间查询条件隐藏
$(".resetButton").on("click",function(){
	$("#end_date_search").css("display","none");
	$("#end_date_start_search").css("display","none");
	$("#due_date_search").css("display","none");
});

// 设置显示的还款方式
function setReturnType (projectType) {
	if (projectType == 1) {
		$("#form-field-returnType option[value='avg_principal_week']").remove();
		$("#form-field-returnType option[value='season_paid']").remove();
	}
}
