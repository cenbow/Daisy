var selectMemberTable;
$(function(){
//	selectMemberDataTable();
})



function selectMemberDataTable(){
	selectMemberTable = $('#select-member-table').dataTable({
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
		'sAjaxSource' : config.urlMap.selectMember,
		'aoColumns' : [
			{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'id',
			}, {
				'mDataProp' : 'trueName',
				'bSortable' : false
			}, {
				'mDataProp' : 'sex',
				'bSortable' : true,
				'mRender'   : function(data, type, row) {
					return getDictLabel(config.sex, row.sex);
				}
			}, {
				'mDataProp' : 'mobile',
				'bSortable' : false
			}, {
				'mDataProp' : 'identityNumber',
				'bSortable' : false
			},{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					var buttons = '';
					if(config.permission.findMemberInfo ){
						buttons = "<button class='btn btn-info btn-sm btn-primary showMemeber' data-value='"+row.id+"' onclick=selectMemberInfo('"+row.id+"')>查看详情</button>";
					}
					if( row.trueName  !=  null  && config.permission.selectMemberSaveUcEnterprise ){
						buttons = buttons + " <button class='btn btn-warn btn-sm  saveUcEnterprise' data-trueName='"+row.trueName+"'  data-identityNumber='"+row.identityNumber+"'  data-id='"+row.id+"'>完善企业信息</button> ";
					}
					if( row.trueName  !=  null  && config.permission.selectMemberChangeMobile ){
						buttons = buttons + " <button class='btn btn-warn btn-sm btn-primary changeMobile' data-id='"+row.id+"'>更换手机</button> ";
					}
					return buttons;
				}
			}]

	});//dataTable
	
	//完善企业信息
	$("#select-member-table").on("click", '.saveUcEnterprise', function() {
		$("#enterprise_form").Validform().resetForm();
		var id = $(this).attr("data-id");
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
	});
	
	//表单验证初始化
	var changerMobile_form = $("#changerMobile_form").Validform({
		tiptype: 4,
		//btnReset: ".btnReset",
		ajaxPost: false
	});
	
	var enterprise_form = $("#enterprise_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	// 更换手机号码 显示model
	$("#select-member-table").on("click", '.changeMobile', function() {
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
								selectMemberTable.fnDraw();
							}else{
								showErrorMessage(data);
								$("#changerMobile_form_id").val(id);
								console.log(id);
							}
				}
			);
		}
	});
	
	//完善企业信息
	$("#member-table").on("click", '.saveUcEnterprise', function() {
		enterprise_form.resetForm();
		var id = $(this).attr("data-id");
		$("#enterprise_form").xform("load", config.urlMap.getEnterprise+"?memberID="+id,function(data){
			if(data.regeditDate!=null){
				var date = formatDate(data.regeditDate,"yyyy-mm");
				$("#form-field-regeditDate").val(date);
				
			}
		});

		$('#modal-table_enterprise_form').modal('show');
	});


	$("#btn_save_enterprise").on('click', function () {
		$(this).addClass("disabled");
		var that = $(this);
		 if (enterprise_form.check(false)) {
			$('#enterprise_form').xform("post", config.urlMap.saveUcEnterprise,function(data){
						$('#modal-table_enterprise_form').modal('hide');
						selectMemberTable.fnDraw();
				}
			);
		 }
		that.removeClass("disabled");
	});
}

$('#select_member').on('click', function() {
	var name = $("#search_trueName").val(),mobile=$("#search_mobile").val(),id=$("#search_identityNumber").val(), memberId=$("#search_id").val();
	if(name=="" && mobile=="" && id=="" && memberId==""){
		bootbox.alert("请输入查询条件搜索");
		return;
	}
	if(typeof selectMemberTable === "undefined"){
		selectMemberDataTable();
	}else{
		selectMemberTable.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	}
});


function selectMemberInfo(id){
	$("#select-member-modal-table").modal('show');
	$.get(config.urlMap.findMemberInfo,{memberId:id},function(data){
		if(data.success){
			var result = data.result;
			if(result != null){
				var member = data.result.member;
				$("#member_trueName").text(member.trueName);
				$("#member_sex").text(getDictLabel(config.sex, member.sex));
				$("#member_identityNumber").text(member.identityNumber);
				$("#member_mobile").text(member.mobile);
				$("#member_type").text(getDictLabel(config.memberType, member.memberType));
				$("#member_registerTime").text(formatDate(member.registerTime,'yyyy-mm-dd HH:mm:ss'));
				var memberInfo = data.result.memberInfo;
				if(memberInfo !=null){
					$("#member_occupation").text(getDictLabel(config.occupation, memberInfo.occupation));
					$("#member_marriage").text(getDictLabel(config.marriage, memberInfo.marriage));
					$("#member_censusRegisterName").text(memberInfo.censusRegisterName);
					$("#member_address").text(memberInfo.areaFullName+memberInfo.address);
				}
			}
		}
	})
	
}
