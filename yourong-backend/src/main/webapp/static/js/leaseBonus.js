jQuery(function($) {
	/**
	 *	添加租赁记录的验证
	 */
	var leaseBonusForm = $("#leaseDetail_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	/**
	 *  分红结算的验证
	 */
	var closeBonusForm = $("#closeBonus_form").Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	
	/**
	 * 获取租赁分红操作按钮
	 */
	function getAllOperation(row) {
		var leaseStatus = row.leaseStatus,//租赁状态
			projectStatus = row.projectStatus;//项目状态
		var addLease = "<button class='btn btn-xs btn-success lease-addLeaseClose permission-" + config.permission.leaseDetailSave + "'data-id='" + row.id + "' data-projectid = '"+row.projectId+"'><i class=' bigger-120'>添加分红并结算</i></button>"; //添加租赁记录
		var leaseDetail = "<button class='btn btn-xs btn-info lease-leaseDetail permission-" + config.permission.leaseDetailAjax + "'data-id='" + row.id + "' data-bonusstatus = '"+row.bonusStatus+"'><i class=' bigger-120'>查看分红记录</i></button>"; //查看租赁记录
		var leaseBtn = "";
		if(projectStatus>=50 && projectStatus <= 70){
			if(leaseStatus == 0){//待租
				leaseBtn = addLease;
			}else if(leaseStatus == 1){//已租
				leaseBtn = addLease +" "+ leaseDetail;
			}
		}else if(projectStatus > 70 && leaseStatus == 1){
			leaseBtn = leaseDetail;
		}
		return leaseBtn;
	}
	
	/**
	 * 获取租赁记录操作按钮
	 */
	function getLeaseDetailOperation(row) {
		var bonusStatus = $("#detail_bonus_status").val();
		console.info(bonusStatus);
		var edit = "<button class='btn btn-xs btn-success lease-edit permission-" + config.permission.showLeaseDetail + "'data-id='" + row.id + "'><i class=' bigger-120'>修改</i></button>"; //添加租赁记录
		var del = "<button class='btn btn-xs btn-danger lease-delete permission-" + config.permission.delet + "'data-id='" + row.id + "' data-unitrental='" + row.totalrental + "' ><i class=' bigger-120'>删除</i></button>"; //删除
		if(bonusStatus<2){
			return edit +" "+ del;
		}else{
			return "";
		}
	}

	/**
	 * 租赁分红列表
	 */
	var oTable1 = $('#leaseBonus-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [  {
			'mDataProp' : 'id',
			'bSortable' : false
		}, {
			'mDataProp' : 'projectName',
			'bSortable' : false
		}, {
			'mDataProp' : 'leaseStatus',
			'bSortable' : false,
			'mRender':function(data){
				return getDictLabel(config.leaseStatus, data); 
			}
		}, {
			'mDataProp' : 'periods',
			'bSortable' : false,
			'mRender':function(data){
				return data==null?"-":data;
			}
		},{
			'mDataProp' : 'operator',
			'bSortable' : false,
			'mRender':function(data,type,row){
				return getAllOperation(row);
			}
		}]
	});
	
	/**
	 * 租赁记录列表
	 */
	var leaseDetailTable = $('#lease-detail-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getSearchValue("#lease-detail-form",aoData);
		},
		'fnRowCallback':function(nRow, aData,iDataIndex){
			var startIndex = iDataIndex;
			if(typeof leaseDetailTable !== "undefined"){
				var dts = leaseDetailTable.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(0)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.leaseDetailAjax,
		'aoColumns': [  {
			'mDataProp' : 'id'
		}, {
			'mDataProp' : 'timeZones',
			'bSortable' : false,
			'mRender':function(data,type,row){
				return formatDate(row.startDate)+"~"+formatDate(row.endDate);
			}
		}, {
			'mDataProp' : 'leaseDays',
			'bSortable' : false
		}, {
			'mDataProp' : 'rental',
			'bSortable' : false,
			'mRender':function(data){
				return "￥"+data;
			}
		}, {
			'mDataProp' : 'totalRental',
			'bSortable' : false,
			'mRender':function(data){
				return "￥"+data;
			}
		}, {
			'mDataProp' : 'userBonus',
			'bSortable' : false,
			'mRender':function(data){
				return data + "%";
			}
		}, {
			'mDataProp' : 'bonusStatus',
			'bSortable' : false,
			'mRender':function(data){
				return getDictLabel(config.bonusStatus,data);
			}
		}, {
			'mDataProp' : 'bonusDate',
			'bSortable' : false,
			'mRender':function(data){
				return data==null?"":formatDate(data);
			}
		}]
	});
	
	/**
	 * 查询租赁分红
	 */
	$('#query_leaseBonus').on('click', function() {
		oTable1.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	
	/**
	 * 结算分红 closeBonus
	 */
	$("#leaseBonus-table-2").on("click",".lease-addLeaseClose", function() {
//		closeBonusForm.resetForm();
		var id = $(this).data("id"),
			projectId = $(this).data("projectid");
		$("#lease_bonus_id").val(id);
		$("#project_id").val(projectId);
		$('#leaseClose-table').modal('show');
	});
	
	/**
	 * 保存结算分红
	 */
	$("#save_closeBonus").on("click",function(){
		//判断不同用户的分红百分比和是否等于100%
		if(!leaseBonusForm.check(false)){
			return false;
		}
		var borrowerBonus = $("#form-field-borrowerBonus").val(),
			lenderBonus = $("#form-field-lenderBonus").val(),
			lessorBonus = $("#form-field-lessorBonus").val(),
			userBonus = $("#form-field-userBonus").val();
		var totalBonus = Number(borrowerBonus) + Number(lenderBonus) + Number(lessorBonus) + Number(userBonus);
		if(totalBonus!=100){
			bootbox.alert("分红比例和必须为100%！");
			return false;
		}
		//设置保存按钮为不可用
		$(this).attr("disabled","disabled");
		$('#leaseDetail_form').xform('post', config.urlMap.toLeaseBonus, function(data) {
			if (data.success) {
				bootbox.alert("结算执行成功,稍后请查看分红状态！",function(){
					$("#save_closeBonus").removeAttr("disabled");
					$('#leaseClose-table').modal('hide');
					oTable1.fnDraw();
					leaseBonusForm.resetForm();
				});
			} else {
				bootbox.alert(data.resultCodeEum[0].msg,function(){
					$("#save_closeBonus").removeAttr("disabled");
				});
			}
		});
	});
	
	/**
	 * 添加租赁记录
	 */
	$("#leaseBonus-table-2").on("click",".lease-addLease",function(){
		var id = $(this).data("id")
		$("#leaseDetail_form").find("input[name='leaseBonusId']").val(id);
		$('#lease-table').modal("show");
	});
	
	/**
	 * 保存租赁记录
	 */
	$("#save_leaseDetail").on('click',function(){
		if (leaseBonusForm.check(false)) {
			$('#leaseDetail_form').xform('post', config.urlMap.leaseDetailSave, function(data) {
				if (data.success) {
					bootbox.alert("保存成功！",function(){
						$('#lease-table').modal('hide');
						oTable1.fnDraw();
						leaseDetailTable.fnDraw();
						leaseBonusForm.resetForm();
						$("#leaseDetail_form").find(".unitRental").html("");
						$("#leaseDetail_form").find("input[name='days']").html("");
					});
				} else {
					bootbox.alert(data.resultCodeEum[0].msg);
				}
			});
		}
	});	
	
	/**
	 * 查看租赁记录
	 */
	$("#leaseBonus-table-2").on("click",".lease-leaseDetail",function(){
		var id = $(this).data("id"),
		bonusStatus = $(this).data("bonusstatus")
		$("input[name='leaseBonusId']").val(id);
		$("#detail_bonus_status").val(bonusStatus);
		localStorage.leaseBonusId = id;
		$('#leaseDetailIndex-table').modal("show");
		leaseDetailTable.fnDraw();
	});
	
	
	
	//add 租赁记录自动计算租赁总额
	$(".totalRental").on("change",function(){
		autoRental();
	});
	
	//租赁记录自动计算租赁天数
//	$(".end-date").on("click",function(){
//		console.info("000");
//		var e_date = $(this).val();
//		var s_date = $(".start-date").val();
//		if(!s_date){
//			bootbox.alert("请先填写开始时间！",function(){
//				$("#leaseDetail_form").find("input[name='endDate']").val("");
//				return;
//			});
//		}
//		if(!e_date){
//			$("#leaseDetail_form").find("span[name='days']").html("");
//			return;
//		}
//		
//	    var dayNum = findleaseDay(s_date,e_date);
//		$("#leaseDetail_form").find("input[name='leaseDays']").val(dayNum);
//		autoRental();//自动计算
//	});
});

function autoIntervalDays(){
	var e_date = $("input[name='endDate']").val();
	var s_date = $(".start-date").val();
	if(!s_date){
		bootbox.alert("请先填写开始时间！",function(){
			$("#leaseDetail_form").find("input[name='endDate']").val("");
			return;
		});
	}
	if(!e_date){
		$("#leaseDetail_form").find("span[name='days']").html("");
		return;
	}
    var dayNum = findleaseDay(s_date,e_date);
    if(dayNum<=0){
    	bootbox.alert("开始时间必须小于等于结束时间！",function(){
    		$("#leaseDetail_form").find("input[name='startDate']").val("");
    		return;
    	});
    }
	$("#leaseDetail_form").find("input[name='leaseDays']").val(dayNum);
	autoRental();//自动计算
}

$("#form-field-leaseDays").on("change",function(){
	autoRental()
});

/**
 * 自动计算租赁天数
 */
function findleaseDay(s,e){
	return (new Date(e) - new Date(s)) / (24 * 60 * 60 * 1000) + 1;
}

/**
 * add 自动结算租金
 */
function autoRental(){
	var dayNum = $("#leaseDetail_form").find("input[name='leaseDays']").val();
	var totalRental = $("#leaseDetail_form").find("input[name='totalRental']").val();
	var unitRental = Number(totalRental/dayNum).toFixed(2);
	$(".unitRental").html(unitRental);
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