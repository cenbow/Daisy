var enterpeiseTable,enterprise_form,memberInfo_form,org_form,orgTable;
jQuery(function($) {
	  /***
     * 导出按钮的权限设置
     */
    var exportButton = [];
    var excelButton = {
        "sExtends": "xls",
        "sButtonText": "导出Excel",
        "mColumns": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,12,13],
        "fnCellRender": function (sValue, iColumn, nTr, iDataIndex) {
            sValue = nTr.childNodes[iColumn].innerText;
            if (iColumn === 1) {
                if (sValue != "") {
                    return "\"" + sValue.substring(0,sValue.length-7) + "\"";
                }
            }
            
            if (sValue != "") {
                return "\"" + sValue + "\"";
            } else {
                return sValue;
            }
        },
        "sFileName": "会员列表.xls"
    };
    if(config.permission.memberExcel){
        exportButton.push(excelButton);
    }
    
	var memberTable = $('#member-table').dataTable({
		"tableTools": {//excel导出
    		"aButtons": exportButton,
    		"sSwfPath": config.swfUrl
    	},
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
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
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return row.id + "<button class='btn btn-xs btn-success permission-" + 
					 config.permission.synThirdPayEaring + " 'style='float: right' onclick='showMemberBalance("+row.mobile+")'>查询存钱罐余额</a>";
				}
			}, {
				'mDataProp' : 'username',
				'bSortable' : false,
			}, {
				'mDataProp' : 'sex',
				'bSortable' : true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.sex, row.sex);
				}
				,'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'memberType',
				'bSortable' : true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.memberType, row.memberType);
				}
				,'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'vipLevel',
				'bSortable' : true
				,'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'score',
				'bSortable' : true
				,'bVisible':config.permission.searchMember
			}
			, {
				'mDataProp' : 'trueName',
				'bSortable' : false
				,'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'mobile',
				'bSortable' : false,
				'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'email',
				'bSortable' : false,
				'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'status',
				'bSortable' : true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.status, row.status);
				}
				,'bVisible':config.permission.searchMember
			}, {
				'mDataProp' : 'registerTraceSource',
				'bSortable' : true
			}, {
				'mDataProp' : 'registerTraceNo',
				'bSortable' : true
			}, {
				'mDataProp' : 'registerTime',
				'bSortable' : true,
				'mRender'   : function(data, type, row) {
					return formatDate(data,'yyyy-mm-dd HH:mm:ss');
				}
			},{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					var buttons = "";
					/*if(config.permission.show){
						buttons = buttons + "<button class='btn btn-info btn-xs btn-primary showMemeber' data-value='"+row.id+"'>查看详情</button> ";
					}*/
					//新功能
					if(config.permission.show){
						buttons = buttons + "<button class='btn btn-info btn-xs btn-primary showDetail' data-value='"+row.id+"'>查看详情</button> ";
					}
					if(row.trueName  !=  null  && config.permission.changeMobile ){
						buttons = buttons + " <button class='btn btn-warn btn-xs btn-primary changeMobile' data-id='"+row.id+"'>更换手机</button> ";
					}
					/*if( row.trueName  !=  null  && config.permission.saveUcEnterprise ){
						buttons = buttons + " <button class='btn btn-warn btn-xs  saveUcEnterprise' data-trueName='"+row.trueName+"'  data-identityNumber='"+row.identityNumber+"'  data-id='"+row.id+"'>完善企业信息</button> ";
					}*/
					if(row.status == 2 && config.permission.cancel) {
						buttons = buttons + " <button class='btn btn-warn btn-xs  cancelMember' data-status=0 data-id='"+row.id+"'>注销</button> ";
					}
					if(row.status == 2 && (row.trueName != null && row.trueName.length > 0) && (row.identityNumber != null && row.identityNumber.length > 0) && 
							(row.referral == null || row.referral.length == 0) && config.permission.addReferral) {
						buttons = buttons + " <button class='btn btn-purple btn-xs  addReferral' data-status=0 data-id='"+row.id+"'>添加推荐人</button> ";
					}
					if(row.trueName  !=  null){
						buttons = buttons + " <button class='btn btn-warn btn-primary btn-xs  updateMemberInfo'  data-id='"+row.id+"'>修改会员信息</button> ";
					}
					if(row.trueName  !=  null&& row.trueName.length > 0&& config.permission.forzenBalance){
						buttons = buttons + " <button class='btn btn-danger btn-primary btn-xs  forzenBalance'  data-id='"+row.id+"'>冻结</button> ";
					}
					return   buttons;
				}
			}]

	});//dataTable
	//表单验证初始化
	var changerMobile_form = $("#changerMobile_form").Validform({
		tiptype: 4,
		//btnReset: ".btnReset",
		ajaxPost: false
	});

	
	// 更换手机号码 显示model
	$("#member-table").on("click", '.changeMobile', function() {
		var id = $(this).attr("data-id");
		//document.getElementById("changerMobile_form").reset()
		changerMobile_form.resetForm();
		$("#changerMobile_form_id").val(id);
		$("#btn_mobile_send_code").removeAttr("disabled");
		$("#btn_mobile_send_code").removeClass("disabled");
		$("#btn_mobile_send_code").text("获取验证码");
		clearInterval(timer);
		$('#modal-table-changeMobile').modal({
			'show': true,
			backdrop: 'static'
		});
	});
	
	//注销会员
	$("#member-table").on("click", '.cancelMember', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要注销吗?", function(result) {
			if (result) {
				$.post(
					config.urlMap.cancelMember,{id:id},function(data){
						 if(data == 1) {
							 bootbox.alert("注销成功！");
							 memberTable.fnDraw();
						 }
					}
				);
			}
		});
	});
	
	//会员详情
	$("#member-table").on("click", '.showDetail', function() {
		var id = $(this).attr("data-value");
		window.top.setIframeBox("showDetail-" + id, config.urlMap.showDetail + id , "会员详情");
	});
	//查看详情
	$("#member-table").on("click", '.showMemeber', function() {
		var id = $(this).attr("data-value");
		$.post(config.urlMap.show+id,null,function(data){
			for(var i=0; i<data.length;i++){
				$.each(data[i],function(name,value){
					var attr = $("#"+name);
					if(attr.length > 0){//检查属性是否存在
						var at = attr.attr("dict");
						if(typeof(at) != "undefined"){//检查是否字典属性
							attr.text(getDictLabelByNameAndValue(at,value));
						}else{
							if(name == "birthday" || name == "registerTime" || name == "updateTime") {
								attr.text(formatDate(value,'yyyy-mm-dd'));
							} else {
								attr.text(value);
							}
						}
					}
				});
			}
			memberBankCard(id);
		});
		$('#modal-table').modal('show');
	})
	
		
	//更改手机，发送后台
	$("#btn_change_mobile").on("click",function(){
		$(this).addClass("disabled");
		var that = $(this);
		var id = $("#changerMobile_form_id").val();
		var newMobile= $("#form-field-group-newMobile").val();
		var token = $("#form-field-group-token").val();
		console.log(id);
		if (changerMobile_form.check(false)) {
			$.post(
				config.urlMap.changeMobile,{id:id,newMobile:newMobile,token:token},function(data){
					 that.removeClass("disabled");
							if(data.success){
								$('#modal-table-changeMobile').modal('hide');
								memberTable.fnDraw();
							}else{
								showErrorMessage(data);
								$("#changerMobile_form_id").val(id);
								console.log(id);
							}
				}
			);
		//$("#changerMobile_form").xform("post", config.urlMap.changeMobile,
		//	function(data) {
		//		that.removeClass("disabled");
		//		if(data.success){
		//			$('#modal-table-changeMobile').modal('hide');
		//			memberTable.fnDraw();
		//		}else{
		//			showErrorMessage(data);
		//			$("#changerMobile_form_id").val(id);
		//			console.log(id);
		//		}
		//	}
		//);
		}
	});
	//发送验证码
	var timer
	var count = 30;
	$("#btn_mobile_send_code").on("click",function(){
		if(changerMobile_form.check(false)) {
			var newMobile  = $("#form-field-group-newMobile").val();
			$("#btn_mobile_send_code").attr('disabled',"true");
			$.ajax({
				type: "GET",
				url:  config.urlMap.sendMobileCode,
				data: {newMobile:newMobile},
				dataType: "json",
				success: function(data){
					//showErrorMessage(data);
				}
			});
			timer = setInterval(function () {
				if (count !== 0) {
					count -= 1;
					$("#btn_mobile_send_code").addClass("disabled");
					$("#btn_mobile_send_code").text("已发送，" + count + "秒后可重获！");
				} else {
					count = 30;
					clearInterval(timer);
					$("#btn_mobile_send_code").removeClass("disabled");
					$("#btn_mobile_send_code").removeAttr("disabled");
					$("#btn_mobile_send_code").text("重新获取验证码");
				}
			}, 1000);
		}
	});

	$("#caInfo").on("click",function(){
		var id=$("#id").val();
		$.ajax({
			url:config.urlMap.caEnterprise+"?id="+id,
			type:"post", 
			dataType:'json',
			success:function(data) {
				if(data.success){
					 $("#ca").find("em[name='fullName']").html(data.result.fullName);
					 var isAuth="未申请";
					 if(data.result.isAuth==1){
						 isAuth="申请成功";
					 }
					 $("#ca").find("em[name='isAuth']").html(isAuth);
					 $("#ca").find("em[name='caNo']").html(data.result.caNo);
					 renderAttachmentDetail(data.result.bscAttachment);
				}else{
					//bootbox.alert("2");
				}
			}
		});
	});
	ca_form = $("#ca_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	//CA申请
	$("#caApply").on("click", function() {
		var id=$("#id").val();
		ca_form.resetForm();
		$("#ca_form").xform("load", config.urlMap.caEnterprise+"?id="+id,function(data){
			$("#enterpriseId").val(data.result.id);
			
			$("#form-field-legalId-ca").val(data.result.legalId);
			$("#form-field-identity-ca").val(data.result.identity);
			$("#form-field-fullName-ca").val(data.result.fullName);
			$("#form-field-legalName-ca").val(data.result.legalName);
			$("#form-field-identityNumber-ca").val(data.result.identity);
			$("#form-field-regisNo-ca").val(data.result.regisNo);
			$("#form-field-organizNo-ca").val(data.result.organizNo);
			$("#form-field-taxNo-ca").val(data.result.taxNo);
			$("#form-field-province-ca").val(data.result.province);
			$("#form-field-city-ca").val(data.result.city);
			$("#form-field-address-ca").val(data.result.address);
			$("#form-field-mobile-ca").val(data.result.telephone);
		});
		$('#modal-table-ca-form').modal('show');
	});
	
	
	
	$("#btn_ca_apply").on("click", function() {
		
		var id=$("#enterpriseId").val();
		$("#ca_form").xform("load", config.urlMap.caApply+"?enterpriseId="+id,function(data){
			if (data.success) {
				 $("#ca").find("em[name='fullName']").html(data.result.fullName);
				 var isAuth="未申请";
				 if(data.result.isAuth==1){
					 isAuth="申请成功";
				 }
				 $("#ca").find("em[name='isAuth']").html(isAuth);
				 $("#ca").find("em[name='caNo']").html(data.result.caNo);
				bootbox.alert("企业ca认证成功");
			}else{
				bootbox.alert("企业ca认证失败");
			}
			$('#modal-table-ca-form').modal('hide');
			enterpeiseTable.fnDraw();
		});
		
	});
	
	//图片上传
	$("#uploadCaImage").on("click", function() {
		
		
		/*var ca = $("em[name='isAuth']").html();
		if(ca!='申请成功'){
			bootbox.alert("请先进行ca认证！");
			return;
		}*/
		var id=$("#enterpriseId").val();
		$('#ca_image_form').xform('post', config.urlMap.saveImage+"?enterpriseId="+id, function(data) {
			if (data.success) {
				bootbox.alert("上传企业公章成功");
				renderAttachmentDetail(data.result);
				$('#modal-table-ca-image').modal('hide');
			}else{
				bootbox.alert("上传企业公章失败");
			}
		});
	});
	
	
	//choose
	$('table th input:checkbox').on('click',function() {
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox').each(function() {
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});

	//delete member
	$('#delete_member').on('click', function() {
		var ids = [];
		$('table tr > td input:checked').each(function() {
			ids.push($(this).val());
		});
		if (ids.length === 0) {
			bootbox.alert("请选择数据！");
			return;
		}
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (result) {
				ids = ids.join(",");
				$("#member_form").xform("post", config.urlMap.delet+"?ids="+ids);
				memberTable.fnDraw();
			}
		});
	});//delete_member

	$('#query_member').on('click', function() {
		memberTable.fnDraw();
	});//query_member.

	$('#new_member').on('click',function(){
		$('#new-member-table').modal('show');
	});
	//表单验证初始化
	var member_balance_form = $("#member_balance_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	$("#query_memberID_balance").on('click',function(){
		if (member_balance_form.check(false)) {
			var mobile = $("#memberId").val();
			$.ajax({
				url:config.urlMap.queryBalance+"?memberId="+mobile,
				type:"post", 
				dataType:'json',
				success:function(data) {
					if(data.success){
						bootbox.alert("余额:" + data.module.balance + ",可用余额:"
								+ data.module.availableBalance);
					}else{
						bootbox.alert("同步余额失败！");
					}
				}
			});
		}
	});
	$("#query_memberID_profit").on('click',function(){
		if (member_balance_form.check(false)) {
			var mobile = $("#memberId").val();
			$.ajax({
				url:config.urlMap.queryProfit+"?memberId="+mobile,
				type:"post", 
				dataType:'json',
				success:function(data) {
					if(data.success){
						bootbox.alert("同步收益成功！");
					}else{
						bootbox.alert("同步收益失败！");
					}
				}
			});
		}
	});
	//表单验证初始化
	var regidist_member_form = $("#regidist_member_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
		
	$("#j-regedit-member").on('click', function () {
		if (regidist_member_form.check(false)) {
			if(!checkAreaSelected("option")) {
				bootbox.confirm("户籍地信息选择不完整，点击【OK】继续保存用户信息但不保存户籍地信息，否则点击【Cancel】取消本次操作！", function(result) {
					if (result) {
						$("#censusRegisterName").val('');
						$("#censusRegisterId").val('');
						var that = $(this);
						$(this).addClass("disabled");
						$("#regidist_member_form").xform("post", config.urlMap.regedistMember,
								function (data) {
							if(data.success){
								alert("添加会员成功");
								that.removeClass("disabled");
							}else{
								showErrorMessage(data);
								that.removeClass("disabled");
							}
						}
						);
					}
				});
			} else {
				if($("#area_prov").val() == '' && $("#area_city").val() == '' && $("#area_dict").val() == '') {
					$("#censusRegisterName").val('');
					$("#censusRegisterId").val('');
				}
				var that = $(this);
				$(this).addClass("disabled");
				$("#regidist_member_form").xform("post", config.urlMap.regedistMember,
						function (data) {
					if(data.success){
						alert("添加会员成功");
						that.removeClass("disabled");
					}else{
						showErrorMessage(data);
						that.removeClass("disabled");
					}
				}
				);
			}
		}
	});
	enterprise_form = $("#enterprise_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	memberInfo_form = $("#memberInfo_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	org_form = $("#org_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
   //完善企业信息
	$("#member-table").on("click", '.saveUcEnterprise', function() {
		enterprise_form.resetForm();
		$("#org_form").find("input[name='id']").val("");
		var id = $(this).attr("data-id");
		$("#org_form").xform("load", config.urlMap.getEnterprise+"?memberID="+id,function(data){
			if(data.regeditDate!=null){
				var date = formatDate(data.regeditDate,"yyyy-mm");
				$("#form-field-regeditDate").val(date);
				if(data.creditAmount == 0){
					$("#form-field-creditAmount").val("");
				}
			}
		});

		$('#modal-table_other_form').modal('show');
	});
	//新增企业
	$("#addEnterprise").on("click",function() { 
		enterprise_form.resetForm();
		//$("#enterprise_form").find("input[name='id']").val("");
		var id = $("#id").val();
		$("#enterprise_form").xform("load", config.urlMap.addEnterprise+"?memberID="+id,function(data){
			if(data.regeditDate!=null){
				var date = formatDate(data.regeditDate,"yyyy-mm");
				$("#form-field-regeditDate").val(date);
				if(data.creditAmount == 0){
					$("#form-field-creditAmount").val("");
				}
			}
		});

		$('#modal-table_enterprise_form').modal('show');
	});
	
	//新增其他组织
	$("#addOther").on("click",function() { 
		org_form.resetForm();
		var id = $("#id").val();
		$("#org_form").xform("load", config.urlMap.addEnterprise+"?memberID="+id,function(data){
			$("#form-field-orgName").val(data.legalName);
			$("#form-field-legalId-org").val(data.legalId);
			$("#form-field-identity-org").val(data.identity);
			if(data.regeditDate!=null){
				var date = formatDate(data.regeditDate,"yyyy-mm");
				$("#form-field-regeditDate").val(date);
				if(data.creditAmount == 0){
					$("#form-field-creditAmount").val("");
				}
			}
		});

		$('#modal-table_other_form').modal('show');
	});
	
	 //修改会员信息
	$("#member-table").on("click", '.updateMemberInfo', function() {
		memberInfo_form.resetForm();
		$("#memberInfo_form").find("input[name='id']").val("");
		var id = $(this).attr("data-id");
		$("#memberInfo_form").xform("load", config.urlMap.showMemberInfo+id,function(data){
			if(data!=null){
				var html="<label for='marriage-1'><input type='radio' name='marriage' id='marriage-1' value='-1' >保密 </label>"+
				"<label for='marriage-1'><input type='radio' name='marriage' id='marriage-1' value='0' >已婚 </label>"+
				"<label for='marriage-1'><input type='radio' name='marriage' id='marriage-1' value='1' >未婚 </label>";
				$("#marriage_temp").html(html);
				$("input[name=marriage]").each(function(k,v){
					if(this.value==data.marriage){
						$("input[name=marriage]:eq("+k+")").attr("checked","checked");
					}
				});
				if(!!data.censusRegisterId){
					getSysAreasByParentId(data.censusRegisterId,"1");
				}else{
					resetSelect("area_city", "选择市");
					resetSelect("area_dict", "选择区");
				}
				if(!!data.city){
					getSysAreasByParentId(data.city,"2");
				}else{
					resetSelect("area_city_now", "选择市");
					resetSelect("area_dict_now", "选择区");
				}
				if(!!data.registerType){
					$("#registerType").find("option[value='"+data.registerType+"']").attr("selected",true);
				}else{
					$("#registerType").find("option[value='']").attr("selected",true);
				}
				if(!!data.income){
					$("#income").find("option[value='"+data.income+"']").attr("selected",true);
				}else{
					$("#incomeType").find("option[value='']").attr("selected",true);
				}
			}
		});

		$('#modal-table_member_form').modal('show');
	});
	//保存会员信息
	$("#btn_save_member").on('click', function () {
		//alert($('#area_prov_now  option:selected').text());
		$("#areaFullName").val($('#area_prov_now  option:selected').text() + $('#area_city_now  option:selected').text() + $('#area_dict_now option:selected').text());
		$("#censusRegisterName").val($('#area_prov  option:selected').text() + $('#area_city  option:selected').text() + $('#area_dict option:selected').text());
		$(this).addClass("disabled");
		//$("#area_dict").val($("#censusRegisterId").find("option:selected").val());
		//$("#area_dict_now").val($("#city").find("option:selected").val());
		$("#registerType").val($('#registerType  option:selected').val());
		$("#income").val($('#income  option:selected').val());
		var that = $(this);
		 if (memberInfo_form.check(false)) {
			$.ajax({
				url:config.urlMap.saveUcMemberInfo,
				data:$("#memberInfo_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						$('#modal-table_member_form').modal('hide');
						memberTable.fnDraw();
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								return;
							});
						}else{
							return bootbox.alert("修改会员信息失败！");
						}
					}
				}
			});
		 }
		that.removeClass("disabled");
	});
	
	$("#btn_save_enterprise").on('click', function () {
		$(this).addClass("disabled");
		var that = $(this);
		 if (enterprise_form.check(false)) {
			/*$('#enterprise_form').xform("post", config.urlMap.saveUcEnterprise,function(data){
				if(data.success){
					$('#modal-table_enterprise_form').modal('hide');
					memberTable.fnDraw();
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							return;
						});
					}else{
						return bootbox.alert("完善企业信息失败！");
					}
				}
				}
			);*/
			$.ajax({
				url:config.urlMap.saveUcEnterprise,
				data:$("#enterprise_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						$('#modal-table_enterprise_form').modal('hide');
						enterpeiseTable.fnDraw();
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								return;
							});
						}else{
							return bootbox.alert("完善企业信息失败！");
						}
					}
				}
			});
		 }
		//enterpriseTable($("#id").val());
		that.removeClass("disabled");
	});

	$("#btn_save_org").on('click', function () {
		$(this).addClass("disabled");
		var that = $(this);
		 if (org_form.check(false)) {
			$.ajax({
				url:config.urlMap.saveUcEnterprise,
				data:$("#org_form").serialize(),
				type:"post",
				dataType:"json",
				success:function(data){
					if(data.success){
						$('#modal-table_enterprise_form').modal('hide');
						$('#modal-table_other_form').modal('hide');
						orgTable.fnDraw();
					}else{
						if(!!data.resultCodeEum){
							return bootbox.alert(data.resultCodeEum[0].msg,function(){
								return;
							});
						}else{
							return bootbox.alert("完善企业信息失败！");
						}
					}
				}
			});
		 }
		that.removeClass("disabled");
	});
	//取消新浪存钱罐认证
	$("#j-cancleMobile-button").on("click",function(){
		var uid  = $("#cancleMobile_form_id").val();
		$.ajax({
			type: "GET",
			url:  config.urlMap.cansoleMobile,
			data: {uid:uid},
			dataType: "json",
			success: function(data){
				bootbox.alert("清除认证");
			}
		});
	});

	// 添加推荐人
	var addReferral_form = $("#addReferral_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	
	$("#member-table").on("click", '.addReferral', function() {
		var id = $(this).attr("data-id");
		addReferral_form.resetForm();
		$("#referredId").val(id);
		$("#referralMobile").val('');
		$('#modal-table-addReferral').modal({
			'show': true,
			backdrop: 'static'
		});
	});

	$("#btn_save_referral").on("click",function(){
		if (addReferral_form.check(false)) {
			var id = $("#referredId").val();
			var mobile = $("#referralMobile").val();
			$.ajax({
				type: "GET",
				url:  config.urlMap.addReferral,
				data: {referredId:id, referralMobile:mobile},
				dataType: "json",
				success: function(data){
					if(data.success){
						bootbox.alert("添加成功！", function() {
							$('#modal-table-addReferral').modal('hide');
							memberTable.fnDraw();
						});
					}else{
						if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
							bootbox.alert(data.resultCodeEum[0].msg);
						}else{
							bootbox.alert("添加失败！");
						}
					}
				}
			});
		}
	});
	
	
	$("#otherOrgInfo").on("click",function(){
		var id=$("#id").val();
		orgInfoTable(id);
	});
	
	function orgInfoTable(id){
		orgTable = $('#other-table').dataTable({
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : false,
			"bRetrieve": true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource' : config.urlMap.findMemberEnterprise+"?memberId="+id+"&type="+2,
			'aoColumns' : [
				{
					'mDataProp' : 'id',
					'bSortable' : false,
					'mRender' : function(data, type, row) {
						return "<input type='checkbox' value=" + row.id + ">";
					}
				}, {
					'mDataProp' : 'id'
				}, {
					'mDataProp' : 'legalName',
					'bSortable' : false
				}, {
					'mDataProp' : 'fullName',
					'bSortable' : false
				}, {
					'mDataProp' : 'regeditDate',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						return formatDate(data,'yyyy-mm');
					}
				}, {
					'mDataProp' : 'telephone',
					'bSortable' : false
				}, {
					'mDataProp' : 'income',
					'bSortable' : false
				}, {
					'mDataProp' : 'creditAmount',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						if(data==0){
							return "-";
						}else{
							return data;
						}
					}
				}, {
					'mDataProp' : 'organizationCode',
					'bSortable' : false
				}, {
					'mDataProp': 'operation',
					'bSortable': false,
					'mRender': function(data, type, row) {
						return getAllOperationOrg(row);
					}
				}
			]
		});
		orgTable.fnDraw();
		
	}
	function getAllOperationOrg(row){
		//var editorg = "<button onclick='editOrgInfo("+ row.id+");' class='btn btn-xs btn-info perfectEnterprise permission-" + config.permission.getEnterpriseDetail + "'  data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
		var editorg = "<button class='btn btn-xs btn-info org-edit permission-" + config.permission.show + "' data-id='" + row.id + "' data-id ='" + row.id + "'>" +
		"<i class='icon-edit bigger-120'>编辑</i></button>"; //编辑
		
		var del =  "<button onclick='delEnterprise("+ row.id+");' class='btn btn-xs btn-danger enterprise-del permission-" + config.permission.delEnterprise + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		var caApply =  "<button onclick='caApply("+ row.id+");' class='btn btn-xs btn-info  permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>申请CA证书</i></button>";
		var uploadCaImage =  "<button onclick='uploadCaImage("+ row.id+");' class='btn btn-xs btn-info permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>添加企业公章</i></button>";
		var caInit =  "<button onclick='caInit("+ row.id+");' class='btn btn-xs btn-info  permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>查看CA证书</i></button>";
		if(row.isAuth==1){
			return editorg+" "+del+" "+caInit + " " + uploadCaImage;
		}else{
			return editorg+" "+del+" "+caApply + " " + uploadCaImage;
		}
		
		
	}
	
	$('#other-table').on('click', '.org-edit', function() {
		var id = $(this).attr("data-id");
		editOrgInfo(id);
	});
	//编辑其他组织
	function editOrgInfo(id){
		org_form.resetForm();
		$("#org_form").xform("load", config.urlMap.getEnterpriseDetail+"?enterpriseID="+id,function(data){
			if(data.regeditDate!=null){
				var date = formatDate(data.regeditDate,"yyyy-mm");
				$("#form-field-regeditDate-org").val(date);
				if(data.creditAmount == 0){
					$("#form-field-creditAmount").val("");
				}
			}
		});
		$('#modal-table_other_form').modal('show');
	}
	
	$("#enterpriseInfo").on("click",function(){
		var id=$("#id").val();
		enterpriseTable(id);
	});
	
	function enterpriseTable(id){
		enterpeiseTable = $('#enterprise-table').dataTable({
			'bFilter' : false,
			'bProcessing' : true,
			'bSort' : false,
			"bRetrieve": true,
			'aaSorting':[[1,"desc"]],
			'bServerSide' : true,
			'fnServerParams' : function(aoData) {
				getAllSearchValue(aoData);
			},
			'sAjaxSource' : config.urlMap.findMemberEnterprise+"?memberId="+id+"&type="+1,
			'aoColumns' : [
				{
					'mDataProp' : 'id',
					'bSortable' : false,
					'mRender' : function(data, type, row) {
						return "<input type='checkbox' value=" + row.id + ">";
					}
				}, {
					'mDataProp' : 'id'
				}, {
					'mDataProp' : 'legalName',
					'bSortable' : false
				}, {
					'mDataProp' : 'fullName',
					'bSortable' : false
				}, {
					'mDataProp' : 'regeditDate',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						return formatDate(data,'yyyy-mm');
					}
				}, {
					'mDataProp' : 'telephone',
					'bSortable' : false
				}, {
					'mDataProp' : 'income',
					'bSortable' : false
				}, {
					'mDataProp' : 'creditAmount',
					'bSortable' : false,
					'mRender'   : function(data, type, row) {
						if(data==0){
							return "-";
						}else{
							return data;
						}
					}
				}, {
					'mDataProp' : 'organizationCode',
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
		enterpeiseTable.fnDraw();
		
	}
	
	
	

});

//完善企业信息
/*$("#enterprise-table").on("click", '.perfectEnterprise', function() {
	alert($(this).attr("data-id"));
	 alert($(this).find("input[type=checkbox]:checked").val());
	$("#enterprise_form").xform("load", config.urlMap.getEnterprise+"?memberID="+id,function(data){
		if(data.regeditDate!=null){
			var date = formatDate(data.regeditDate,"yyyy-mm");
			$("#form-field-regeditDate").val(date);
			if(data.creditAmount == 0){
				$("#form-field-creditAmount").val("");
			}
		}
	});

	$('#modal-table_enterprise_form').modal('show');
});*/
function getAllOperation(row){
	var edit = "<button onclick='editEnterprise("+ row.id+");' class='btn btn-xs btn-info perfectEnterprise permission-" + config.permission.getEnterpriseDetail + "'  data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
	var del =  "<button onclick='delEnterprise("+ row.id+");' class='btn btn-xs btn-danger enterprise-del permission-" + config.permission.delEnterprise + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
	var caApply =  "<button onclick='caApply("+ row.id+");' class='btn btn-xs btn-info  permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>申请CA证书</i></button>";
	var uploadCaImage =  "<button onclick='uploadCaImage("+ row.id+");' class='btn btn-xs btn-info permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>添加企业公章</i></button>";
	var caInit =  "<button onclick='caInit("+ row.id+");' class='btn btn-xs btn-info  permission-" + config.permission.caApply + "' data-id='" + row.id + "'><i class='icon-edit bigger-120'>查看CA证书</i></button>";
	if(row.isAuth==1){
		return edit+" "+del+" "+caInit + " " + uploadCaImage;
	}else{
		return edit+" "+del+" "+caApply + " " + uploadCaImage;
	}
	
	
}
//编辑
function editEnterprise(id){
	enterprise_form.resetForm();
	$("#enterprise_form").xform("load", config.urlMap.getEnterpriseDetail+"?enterpriseID="+id,function(data){
		if(data.regeditDate!=null){
			var date = formatDate(data.regeditDate,"yyyy-mm");
			$("#form-field-regeditDate").val(date);
			if(data.creditAmount == 0){
				$("#form-field-creditAmount").val("");
			}
		}
	});
	$('#modal-table_enterprise_form').modal('show');
}
//删除
function delEnterprise(id){
	bootbox.confirm("你确定要删除吗?", function(result) {
		if (!result) return;
		$.post(config.urlMap.delEnterprise +"?enterpriseID="+id, function(data) {
			if (data.success) {
				bootbox.alert("删除成功！", function() {
					enterpeiseTable.fnDraw();
				});
			} else {
				if(!!data.resultCodeEum){
					return bootbox.alert(data.resultCodeEum[0].msg,function(){
						return;
					});
				}else{
					return bootbox.alert("删除失败！");
				}
			}
		});
	});
}
function caApply(id){
	 $("#is_auth").css("display","none");
	 $("#ca_no").css("display","none");
	ca_form.resetForm();
	$("#ca_form").xform("load", config.urlMap.caEnterprise+"?id="+id,function(data){
		$("#enterpriseId").val(data.result.id);
		
		$("#form-field-legalId-ca").val(data.result.legalId);
		$("#form-field-identity-ca").val(data.result.identity);
		$("#form-field-fullName-ca").val(data.result.fullName);
		$("#form-field-legalName-ca").val(data.result.legalName);
		$("#form-field-identityNumber-ca").val(data.result.identity);
		$("#form-field-regisNo-ca").val(data.result.regisNo);
		$("#form-field-organizNo-ca").val(data.result.organizNo);
		$("#form-field-taxNo-ca").val(data.result.taxNo);
		$("#form-field-province-ca").val(data.result.province);
		$("#form-field-city-ca").val(data.result.city);
		$("#form-field-address-ca").val(data.result.address);
		$("#form-field-mobile-ca").val(data.result.telephone);
	});
	$("#ca_apply").css("display","block");
	$('#modal-table-ca-form').modal('show');
}


function caInit(id){
	 $("#is_auth").css("display","none");
	 $("#ca_no").css("display","none");
	$("#ca_form").xform("load", config.urlMap.caEnterprise+"?id="+id,function(data){
		$("#enterpriseId").val(data.result.id);
		
		$("#form-field-legalId-ca").val(data.result.legalId);
		$("#form-field-identity-ca").val(data.result.identity);
		$("#form-field-fullName-ca").val(data.result.fullName);
		$("#form-field-legalName-ca").val(data.result.legalName);
		$("#form-field-identityNumber-ca").val(data.result.identity);
		$("#form-field-regisNo-ca").val(data.result.regisNo);
		$("#form-field-organizNo-ca").val(data.result.organizNo);
		$("#form-field-taxNo-ca").val(data.result.taxNo);
		$("#form-field-province-ca").val(data.result.province);
		$("#form-field-city-ca").val(data.result.city);
		$("#form-field-address-ca").val(data.result.address);
		$("#form-field-mobile-ca").val(data.result.telephone);
		
		 if(data.result.isAuth==1){
			 $("#form-field-isAuth-ca").val("申请通过");
			 $("#is_auth").css("display","block");
			 $("#form-field-caNo-ca").val(data.result.caNo);
			 $("#ca_no").css("display","block");
		 }
	});
	$("#ca_apply").css("display","none");
	$('#modal-table-ca-form').modal('show');
}

function uploadCaImage(id){
	deleteDropzoneAllimage("dropzone_stamp");
	
	$("#enterpriseId").val(id);
	
	$.ajax({
		url:config.urlMap.caEnterprise+"?id="+id,
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				 renderAttachmentDetail(data.result.bscAttachment);
			}else{
				//bootbox.alert("2");
			}
		}
	});
	
	$('#modal-table-ca-image').modal('show');
}



function showDetail(id){
	$.post(config.urlMap.show+id,null,function(data){
		for(var i=0; i<data.length;i++){
			$.each(data[i],function(name,value){
				var attr = $("#"+name);
				if(attr.length > 0){//检查属性是否存在
					var at = attr.attr("dict");
					if(typeof(at) != "undefined"){//检查是否字典属性
						attr.text(getDictLabelByNameAndValue(at,value));
					}else{
						if(name == "birthday" || name == "registerTime" || name == "updateTime") {
							attr.text(formatDate(value,'yyyy-mm-dd'));
						} else {
							attr.text(value);
						} 
					}
				}
			});
		}
		memberBankCardList(id);
	});
}




function memberBankCardList(id){
	var memberBankCardTable = $('#bankCard-table-list').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : false,
		"bRetrieve": true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.findMemberBankCard+"?memberId="+id,
		'aoColumns' : [
			{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'id'
			}, {
				'mDataProp' : 'cardNumber',
				'bSortable' : false
			}, {
				'mDataProp' : 'cardHolder',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCode',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankProvince',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCity',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankDistrict',
				'bSortable' : false
			}
		]
	});
	memberBankCardTable.fnDraw();
	
}
function showErrorMessage(data) {
	if (data.resultCodeEum != null && data.resultCodeEum != "") {
		var message = "";
		for (x in data.resultCodeEum) {
			message = data.resultCodeEum[x].msg + message;
		}
		bootbox.alert(message);
	} else {
		bootbox.alert(data.result);
	}
}

//根据GroupName和值获得对应的Label
function getDictLabelByNameAndValue(name,value){
	var v = value;
	switch(name){
		case "memberType":
			v = getDictLabel(config.memberType, value);
			break;
		case "sex":
			v = getDictLabel(config.sex, value);
			break;
		case "status":
			v = getDictLabel(config.status, value);
			break;
		case "registerMethod":
			v = getDictLabel(config.registerMethod, value);
			break;
		case "withholdAuthorityFlag":
			if(value==0){
				v="否";
			}else{
				v="是";
			}
			break;
		default:
			v;
	}
	return v;
}


function memberBankCard(id){
	var memberBankCardTable = $('#bankCard-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : false,
		"bRetrieve": true,
		'aaSorting':[[1,"desc"]],
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.findMemberBankCard+"?memberId="+id,
		'aoColumns' : [
			{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'id'
			}, {
				'mDataProp' : 'cardNumber',
				'bSortable' : false
			}, {
				'mDataProp' : 'cardHolder',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCode',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankProvince',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankCity',
				'bSortable' : false
			}, {
				'mDataProp' : 'bankDistrict',
				'bSortable' : false
			}
		]
	});
	memberBankCardTable.fnDraw();
	
}

function synThirdPayEaring(memberId){
	$.ajax({
		url:config.urlMap.synThirdPayEaring+"?memberId="+memberId,
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				bootbox.alert("同步余额成功！");
			}else{
				bootbox.alert("同步余额失败！"+ memberId);
			}
		}
	});
}

// 显示存钱罐余额
function showMemberBalance(mobile) {
	$.ajax({
		url:config.urlMap.queryBalance+"?memberId="+mobile,
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				bootbox.alert("存钱罐余额：" + data.module.balance + "元！");
				/*bootbox.alert("余额:" + data.module.balance + ",可用余额:"
						+ data.module.availableBalance);*/
			}else{
				bootbox.alert("查询余额失败！");
			}
		}
	});
}

$("#area_prov_now").on('change', function() {
	if($(this).val() != '') {
		loadAreaByParentId($(this).val(), "area_city_now", "选择市");
		resetSelect("area_dict_now", "选择区");
	} else {
		resetSelect("area_city_now", "选择市");
		resetSelect("area_dict_now", "选择区");
	}
});

$("#area_city_now").on('change', function() {
	if($(this).val() != '') {
		loadAreaByParentId($(this).val(), "area_dict_now", "选择区");
	} else {
		resetSelect("area_dict_now", "选择区");
	}
});

$("#area_dict_now").on('change', function() {
	$("#city").val($(this).val());
	if($(this).val() != '') {s
		$("#areaFullName").val($('#area_prov_now  option:selected').text() + $('#area_city_now  option:selected').text() + $('#area_dict_now option:selected').text());
	} else {
		$("#areaFullName").val('');
	}
});


function loadAreaByParentId(code, callbackObj, nullName){
	//加载数据，返回数组。让回调函数处理	
	$.post(config.urlMap.getAreaList+"?code=" + code,null,function(data){
		if(data.success) {
			$("#" + callbackObj).empty();
			$("#" + callbackObj).append("<option value=''>"+nullName+"</option>");
			$.each(data.resultList,function(name,value){
				$("#"+callbackObj).append("<option value='"+value.code+"'>"+value.name+"</option>");
			});
		}
	});
}

function resetSelect(objName, nullName) {
	$("#" + objName).empty();
	$("#" + objName).append("<option value=''>"+nullName+"</option>");
}

function initArea(callbackObj,code,id){
	$.post(config.urlMap.getAreaList+"?code=" + code,null,function(data){
		if(data.success) {
			$("#" + callbackObj).empty();
			$.each(data.resultList,function(name,value){
				$("#"+callbackObj).append("<option value='"+value.code+"'>"+value.name+"</option>");
				/*if(code==value.code){
					  $(this).attr("selected",true);
				}*/
				
			});
			var all_options = document.getElementById(""+callbackObj+"").options;
			for(var i=0;i<all_options.length;i++){
				if(id==all_options[i].value){
					all_options[i].selected = true;
				}
			}
		}
	});
}
function getSysAreasByParentId(code,type){
	var princeId="";
	$.post(config.urlMap.getSysAreasByParentId,{code:code},function(data){
		if(data.success) {
			princeId=data.resultList[1];
			//省
			if(type==1){
				initPrince("area_prov","1",princeId)
			}else{
				initPrince("area_prov_now","1",princeId)
			}
			//市
			if(type==1){
				initArea("area_city",princeId,data.resultList[2]);
			}else{
				initArea("area_city_now",princeId,data.resultList[2]);
			}
			//区
			if(type==1){
				initArea("area_dict",data.resultList[2],data.resultList[3]);
			}else{
				initArea("area_dict_now",data.resultList[2],data.resultList[3]);
			}
			
			
		}
	});
}

function initPrince(callbackObj,code,id){
	$.post(config.urlMap.getAreaList+"?code=" + code,null,function(data){
		if(data.success) {
			$.each(data.resultList,function(name,value){
				$("#"+callbackObj).append("<option value='"+value.code+"'>"+value.name+"</option>");
			});
			var all_options = document.getElementById(""+callbackObj+"").options;
			for(var i=0;i<all_options.length;i++){
				if(id==all_options[i].value){
					all_options[i].selected = true;
				}
			}
		}
	});
}

function renderAttachmentDetail(bscAttachment) {
	if (bscAttachment != "" && bscAttachment != null) {
		var dropID = "";
		var customDropzone;
			var module = bscAttachment.module;
			if (dropID != module) {
				customDropzone = Dropzone.forElement("#dropzone_" + module);
				dropID = module;
			}
			addImageToDropzone(customDropzone, bscAttachment);
			customColorBox(module);
	}
}

$("#area_prov").on('change', function() {
	if($(this).val() != '') {
		loadAreaByParentId($(this).val(), "area_city", "选择市");
		resetSelect("area_dict", "选择区");
	} else {
		resetSelect("area_city", "选择市");
		resetSelect("area_dict", "选择区");
	}
});

$("#area_city").on('change', function() {
	if($(this).val() != '') {
		loadAreaByParentId($(this).val(), "area_dict", "选择区");
	} else {
		resetSelect("area_dict", "选择区");
	}
});

$("#area_dict").on('change', function() {
	$("#censusRegisterId").val($(this).val());
	if($(this).val() != '') {
		$("#censusRegisterName").val($('#area_prov  option:selected').text() + $('#area_city  option:selected').text() + $('#area_dict option:selected').text());
	} else {
		$("#censusRegisterName").val('');
	}
});

var forzen_form = $("#forzen_form").Validform({
	tiptype: 4,
	btnReset: ".btnReset",
	ajaxPost: false
});
//冻结详情
$("#member-table").on("click", '.forzenBalance', function() {
	forzen_form.resetForm();
	$(this).removeClass("disabled");
	var id = $(this).attr("data-id");
	$("#forzen_form").xform("load", config.urlMap.getMemberInfo+id,function(data){
		if(data!=null){
			$("#forzen_trueName").html(data.trueName);
			$("#forzen_mobile").html(data.mobile);
			if(!!data.availableBalance){
				$("#forzen_balance").val(data.availableBalance);
				$("#forzen_balance_x").html(data.availableBalance);
			}else{
				$("#forzen_balance_x").html("0");
			}
		}
	});

	$('#modal-table_forzen_form').modal('show');
});

//冻结余额
$("#btn_save_forzen").on('click', function () {
	var that = $(this);
	if(Number($("#forzen_balance").val())==0){
		 bootbox.alert("存钱罐余额不足！"); 
		 return;
	}
	 if (forzen_form.check(false)) {
			if(Number($("#forzen_balance").val())<Number($("#form-field-amount").val())){
				 bootbox.alert("冻结金额大于存钱罐可以用余额！"); 
				 return;
			}
			$(this).addClass("disabled");
		$.ajax({
			url:config.urlMap.forzen,
			data:$("#forzen_form").serialize(),
			type:"post",
			dataType:"json",
			success:function(data){
				if(data.success){
					$('#modal-table_forzen_form').modal('hide');
					bootbox.alert("冻结存钱罐余额成功！");
					memberTable.fnDraw();
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							return;
						});
					}else{
						return bootbox.alert("冻结存钱罐余额失败！");
					}
				}
			}
		});
	 }
	//that.removeClass("disabled");
});

$("#form-field-amount").change(function(){
	var rate = $(this).val();
	if(Number($("#forzen_balance").val())<Number(rate)){
		 bootbox.alert("冻结金额不能大于存钱罐可用余额！"); 
		 return;
	}
});

/**
 * 根据会员手机查询会员ID
 */
$("#select_mobileMember").on('click', function () {
	var mobile = $("#search_mobile").val();
	if(mobile != null && mobile != ""){
		$.post(config.urlMap.showMemberId+mobile,null,function(data){
			$("#search_id").val(data);
		});
	}else{
		bootbox.alert("请输入用户手机号！"); 
		 return;
	}
	
});
