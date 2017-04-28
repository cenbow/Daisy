jQuery(function($) {
	
	
	
	
	var earlyRepaymentTable = $('#earlyRepayment-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp' : 'id'
		}, {
			'mDataProp' : 'name',
			'bSortable' : true
		}, {
			'mDataProp' : 'borrowerName',
			'bSortable' : true
		}, {
			'mDataProp' : 'borrowerMobile',
			'bSortable' : true
		}, {
			'mDataProp' : 'startDate',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				return formatDate(row.startDate) + "至" + formatDate(row.endDate);
			}
		}, {
			'mDataProp' : 'remainingTotalPrincipal',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.status==70){
					return "0" ;
				}
				return data;
			}
		},{
			'mDataProp' : 'remainingTotalInterest',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.status==70){
					return "0" ;
				}
				return data;
			}
		},{
			'mDataProp' : 'remainingTotalInterestByBorrower',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.status==70){
					return "0" ;
				}
				return data;
			}
		},{
			'mDataProp' : 'remainingExtraInterest',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.status==70){
					return "0" ;
				}
				return data;
			}
		},{
			'mDataProp' : 'status',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				var status=""
					if(row.status==52){
						status="履约中";
					}else if(row.status==70){
						status="已还款";
					}
					return status;
			}
		},{
			'mDataProp' : 'prepayment',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				var prepayment=""
				if(row.prepayment==1){
					prepayment="是";
				}else{
					prepayment="否";
				}
				return prepayment;
			}
		},{
			'mDataProp' : 'prepaymentTime',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.prepaymentTime!=null){
					return formatDate(row.prepaymentTime) ;
				}
				return "-" ;
			}
		},{
			'mDataProp' : 'prepaymentDay',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.status==70){
					return "-" ;
				}
				if(row.prepaymentDay!=null){
					return row.prepaymentDay ;;
				}
				return "-" ;
			}
		},{
			'mDataProp' : 'operation',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		},{
			'mDataProp' : 'prepaymentDay',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.prepayment==1){
					var a = "<label >"+row.operateName+"</label><label>"+ formatDate(row.operateDate)+"</label>"
					return  a;
				}
				return "-";
			}
		},{
			'mDataProp' : 'prepaymentDay',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				if(row.prepayment==1){
					return row.operateRemarks;
				}
				return "-";
			}
		} ]
	});
	
	//获取操作方法
	function getAllOperation(row) {
		var prepayment = "<button class='btn btn-xs btn-info j-repayment' data-id='"+row.id+"' data-date='"+row.endDate+"'>提前还款</button>",
			operation = "";
		if( row.status==52 && row.prepayment==0 ){
			operation = prepayment;
		}
		return operation;
	}
	
	//查询
	$('#query_earlyRepayment').on('click', function() {
		earlyRepaymentTable.fnDraw();
	});
	
	//重置
	$('#reset_earlyRepayment').on('click', function() {
		$('#earlyRepayment_form')[0].reset();
	});
	
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
	
	
	
	
	$("#earlyRepayment-table").on("click",".j-repayment",function(){
		$("#repayment_form").resetForm();
		var projectId = $(this).data('id');
		$('#projectId').val(projectId);
		var endDate = $(this).data('date');
		$('#endDate').val(endDate);
		$('#modal-repayment').modal('show');
	});
	
	earlyRepaymentForm = $("#earlyRepayment_form").Validform({
		tiptype: 3,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	
	
	$("#btn_early_repayment").on('click', function () {
		var prepaymentTime = $('#prepaymentTime').val();
		 var prepaymentRemarkSys = $('#prepaymentRemarkSys').val();
		 var prepaymentRemarkFront = $('#prepaymentRemarkFront').val();
		 var projectId = $('#projectId').val();
		 var endDate = $('#endDate').val();
		if(prepaymentTime==""||prepaymentRemarkSys==""||prepaymentRemarkFront==""){
			 bootbox.alert("数据不能为空哦");
			 return;
		}
		
		var num = compareTwoDate(prepaymentTime,formatDate(endDate));
		if(num<1){
			 bootbox.alert("提前还款时间要小于项目正常还款时间呦");
			 return;
		}
		var nowDate = new Date();
		var num = compareTwoDate(formatDate(nowDate,"yyyy-mm-dd"),prepaymentTime);
		if(num<0){
			 bootbox.alert("提前还款时间要大于等于当前时间");
			 return;
		}
		$.post(config.urlMap.repay, {
			prepaymentTime:prepaymentTime, 
			prepaymentRemarkSys:prepaymentRemarkSys,
			prepaymentRemarkFront:prepaymentRemarkFront,
			projectId:projectId
		}, function(data) {
			$('#modal-repayment').modal('toggle');
			earlyRepaymentTable.fnDraw();
		});
		 
	});
	
});

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
