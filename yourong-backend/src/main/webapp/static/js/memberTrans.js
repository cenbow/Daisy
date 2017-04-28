jQuery(function($) {
	
	//表单验证初始化
	var memberTrans_form = $("#memberTrans_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});

	$('#query_memberTrans').on('click', function() {
		var that = $(this);
		that.addClass("disabled");
		var id = $("#search_id").val();
		var mobile= $("#search_mobile").val();
		if(id == '' && mobile == '') {
			innerText = '';
			$("#memeberTransInfo").html('<font color=red>请先输入用户ID或者手机号！</font>');
			$('#memberTrans-table-2').dataTable().fnDestroy();
			$("#memberTrans-table-2").hide();
			that.removeClass("disabled");
			return;
		} else {
			if(id != '') {
				if(!isPInt(id)) {
					$("#memeberTransInfo").html('<font color=red>用户ID格式错误！</font>');
					$('#memberTrans-table-2').dataTable().fnDestroy();
					$("#memberTrans-table-2").hide();
					that.removeClass("disabled");
					return;
				}
			}
			if(mobile != '') {
				if(!isMobile(mobile)) {
					$("#memeberTransInfo").html('<font color=red>手机号格式错误！</font>');
					$('#memberTrans-table-2').dataTable().fnDestroy(); 
					$("#memberTrans-table-2").hide();
					that.removeClass("disabled");
					return;
				}
			}
		}
		$.post(
			config.urlMap.memberInfo,{id:id,mobile:mobile},function(data){
				that.removeClass("disabled");
				if(data.selectCode == '1') {
					$("#memeberTransInfo").html('<font color=black>'+data.selectMessage+'</font>');
					showDataTable(data.id);
					$("#memberTrans-table-2").show();
				} else {
					$("#memeberTransInfo").html('<font color=red>'+data.selectMessage+'</font>');
					$('#memberTrans-table-2').dataTable().fnDestroy();
					$("#memberTrans-table-2").hide();
				}
			}
		);
	});
	
	
	
	$('#memberTrans-table-2').on('click','.showProject', function() {
		
		var projectId = $(this).attr("data-projectId");
		var projectName = $(this).attr('data-projectName');
		window.top.setIframeBox("show-"+projectId,config.urlMap.show+projectId,decodeURIComponent(projectName));
	});

});



function showDataTable(id) {
	$('#memberTrans-table-2').dataTable().fnDestroy()
	var memberTransTable = $('#memberTrans-table-2').dataTable({
		'bAutoWidth' : false,
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
		'sAjaxSource' : config.urlMap.query + id,
		'aoColumns' : [
			{
				'mDataProp' : 'id',
				'bSortable' : false,
				'mRender' : function(data, type, row) {
					return "<input type='checkbox' value=" + row.id + ">";
				}
			}, {
				'mDataProp' : 'projectName',
				'bSortable' : false,
				'mRender'   : function(data, type, row) {
					return "<a class='showProject'  data-projectId='" + row.projectId + "' data-projectName ='" + encodeURIComponent(row.projectName) + "')>"+data+"</a>";
				}
			}, {
				'mDataProp' : 'investAmountStr',
				'bSortable' : false,
				'mRender'   : function(data, type, row) {
					return '￥' + data;
				}
			}, {
				'mDataProp' : 'transactionTime',
				'bSortable' : false,
				'mRender'   : function(data, type, row) {
					if(data == null) {
	            		return '';
	            	}
	                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
				}
			}]

	});
	memberTransTable.fnDraw()
}

/**
 * 查看项目详情
 * @param projectId
 */
function findProjectById(projectId){
	$.post(config.urlMap.find,{id:projectId},function(data){
		eachDebtBiz(data.debtBiz);
		eachProject(data);
		var thumbnail = data.thumbnail;
		$("#upload-project-thumbnail").hide();
		$("#upload-thumbnail-btn").hide();
		$("#project-thumbnail-pane").show();
		$(".j-modify-img").hide();
		if(thumbnail !="" && thumbnail != null){
			$("#thumbnail-120").attr("src",config.picURLHead+thumbnail);
			$("#project-thumbnail").attr("src",config.picURLHead+thumbnail);
		}
		loadUmeditor(data.description);
		$("#debtSerialNumber").attr("readonly","readonly");
	})
}

function eachDebtBiz(debtBiz){
	$.each(debtBiz,function(name,value){
		if(name == "borrowMemberBaseBiz"){//借款人信息 
			$("#borrower_trueName").text(value.member.trueName);
		}else if(name == "lenderMember"){//出借人
			$("#lender_trueName").text(value.trueName);
		}else if(name == "debtType"){
			var debtType = getDictLabel(config.debtType,value);
			$("#debtType").text(debtType);
			$("#debtTypeText").text(debtType+"物估值");
			if(value == "collateral"){
				var debtCollateral = debtBiz.debtCollateral;
				if(debtCollateral != null && debtCollateral.collateralValuation != null){
					$("#valuation").text(debtCollateral.collateralValuation+"万");
				}
			}else if(value == "pledge"){
				var debtPledge = debtBiz.debtPledge;
				if(debtPledge != null && debtPledge.pledgeValuation != null){
					$("#valuation").text(debtPledge.pledgeValuation+"万");
				}
			}
		}else if(name == "interestFrom"){
			var interestFrom = getDictLabel(config.interestFrom,value);
			$("#interestFrom").text(interestFrom);
			$("#j-interest").val(value);
		}else if(name == "returnType"){
			var returnType = getDictLabel(config.returnType,value);
			$("#returnType").text(returnType);
		}else if(name == "status"){
			var status = getDictLabel(config.debtStatus,value);
			$("#status").text(status);
		}else if(name == "annualizedRate"){
			$("#annualizedRate").text(value+"%");
			$("#maxAnnualizedRate").val(value);
			$("#_maxAnnualizedRate").val(value);
		}else if(name == "startDate"){
			$("#startDate").text(formatDate(value));
			$("#j-start-date").val(formatDate(value)+" 00:00:00");
		}else if(name == "endDate"){
			$("#endDate").text(formatDate(value));
			var day = debtBiz.interestFrom;
			var date = new Date(formatDate(value));
			if(day > 0){
				date = new Date((date.getTime()-(day*86400000)));
			}
			$("#j-end-date").val(formatDate(date)+" 23:59:59");
		}else if(name =="bscAttachments"){
			//删除之前的记录
			$("#sortableDebtFile li").remove();
			if(value != "" && value != null){
				$.each(value,function(n,v){
					var module = v.module;
					var dropID = "";
					var customDropzone;
					if(dropID != module){
						customDropzone = Dropzone.forElement("#dropzone_"+module);
						dropID = module;
					}
					addImageToDropzone(customDropzone, v);
					
					customColorBox(module);
				});
			}
			if($(".editStatus").length<1){
				disableDropzone();
			}
			$(".dz-input-data").attr('disabled', 'disabled');
		}else if(name == "guarantyType"){
			var guarantyType = getGuarantyTypeName(config.guarantyType,debtBiz.instalment,value);
			$("#guarantyType").text(guarantyType);
		}else{
			var attr = $("#"+name);
			if(attr.length > 0){//检查属性是否存在
				attr.text(value);
			}
		}
	});			
	$("#_serialNumber").val(debtBiz.serialNumber);
	$("#debtSerialNumber").val(debtBiz.serialNumber);
}

function eachProject(project, isEdit){
	if(!(typeof isEdit !== "undefined" && isEdit)){//非编辑状态下，屏蔽按钮
		$(".addLadderRow").hide();
		if(project.projectType != 'car' || (project.projectType == 'car' && project.debtBiz !=null && project.debtBiz.instalment == 1) ) {
			$('#joinLeaseSel').hide();
			$('#joinLeaseTip').hide();
			$('#joinLeaseMsg').hide();
		} 
		$('#project_form').find('input,select,textarea,button').attr('disabled', 'disabled');
	} else if(isEdit == 'edit') {//编辑
		//不是有车融的不允许参与租赁分红
		if(project.projectType != 'car' || (project.projectType == 'car' && project.debtBiz !=null && project.debtBiz.instalment == 1) ) {
			$('#joinLeaseSel').hide();
			$('#joinLeaseTip').show();
			$('#joinLeaseMsg').hide();
		} 
	} else if(isEdit == 'emergency') {//紧急编辑
		$(".addLadderRow").hide();
		$(".ladderRow").find('input,select,textarea,button').attr('disabled', 'disabled');
		//编辑状态下判断是否显示租赁分红下拉框
		if(project.joinLease == '1') {			
			//已经参与租赁分红
			$('#joinLeaseSel').hide();
			$('#joinLeaseTip').hide();
			$('#joinLeaseMsg').html("该项目已经参与租赁分红！");
		} else {
			//不是有车融的不允许参与租赁分红
			if(project.projectType != 'car' || (project.projectType == 'car' && project.debtBiz !=null && project.debtBiz.instalment == 1) ) {
				$('#joinLeaseSel').hide();
				$('#joinLeaseTip').hide();
				$('#joinLeaseMsg').hide();
			} 
		}
	}
	$.each(project,function(name, value){
		if(name == "projectInterestList" && project.annualizedRateType == 1){
			var j = 0;
			var size = value.length;
			$.each(value,function(n,v){//
				if(j == 0){
					$("#minInvest1").val(v.minInvest);
					$("#maxInvest1").val(v.maxInvest);
					$("#annualizedRate1").val(v.annualizedRate);
					if(size > 1){
						$(".ladderTable .symbol").html("<");
					}
				}else{
					var trNum = $(".ladderTable tr").length+1;
					var newRow = $(".ladderTable").find(".ladderRow").first().clone(true);
					if(typeof isEdit !== "undefined" && isEdit == 'edit'){
						newRow.append("<td><input type='button' value='删除' class='btn btn-danger btn-sm btn-primary deleteLadderRow' onclick='deleteLadderRow(this)'></td>");
					}
					newRow.find("input[type='text']").val("");
					newRow.find(".symbol").text("<=");
					var minInvest = newRow.find(".minInvest");
						minInvest.attr("id","minInvest"+trNum);
						minInvest.val(v.minInvest);
					var maxInvest = newRow.find(".maxInvest");
						maxInvest.attr("id","maxInvest"+trNum);
						maxInvest.val(v.maxInvest);
					var annualizedRate = newRow.find(".annualizedRate");
						annualizedRate.attr("id","annualizedRate"+trNum);
						annualizedRate.val(v.annualizedRate);
					$(".ladderTable").append(newRow);
				}
				j++;
			})
		}else{
			//选择是否参与租赁分红开关
			var attrObj = $("#"+name);
			if(attrObj.length > 0){//检查属性是否存在
				if(name == "isNovice"){
					$("#isNovice").find("option[value='"+value+"']").attr("selected",true);
				}else{
					var dateFormat = attrObj.attr("dateFormat");
					if(typeof (dateFormat) !== "undefined"){
						attrObj.val(formatDate(value,dateFormat));
					}else{
						attrObj.val(value);
					}
					
				}
			}
		}
	});
	
	$("#annualizedRateType").find("option[value='"+project.annualizedRateType+"']").attr("selected",true);
	$("#annualizedRateType").trigger("change");
}



//正整数
function isPInt(str) {
    var g = /^[1-9]*[1-9][0-9]*$/;
    return g.test(str);
}
//手机格式
function isMobile(str) {
	var reg = /^0?(13[0-9]|15[0123456789]|18[0-9]|14[57]|17[0678])[0-9]{8}$/;
	return reg.test(str);
}