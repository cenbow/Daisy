var projectTable;
var projectForm;
var investErrorMsg="";
var jcrop_api;
jQuery(function($){
	//表单验证
	 projectForm = $("#project_form").Validform({
		 tiptype : 4,
	     ajaxPost : true,
	     datatype:{
	    	 "price":/^[\d]*(\.\d{1,2})?$/
	     }
	 });
   
	//初始化事件绑定
	 initBindEvent();
	 
	 //项目列表数据
	 projectListDataTable();
	 
	 //债权号搜索提示
	 autocompleteDebtId();
	 
	 //默认调用一次，用于验证
	 $("#annualizedRateType").trigger("change");
	 
	 $(".j-modify-img").click(function(){
		 $("#upload-project-thumbnail").show(1000);
		 $("#project-thumbnail-pane").hide();
		 $("#upload-thumbnail-btn").show();
	 });
	 
	 //上传项目图片
	 updateProjectThumbnail();
	 //项目转让相关设置
	 selectTransferFlag();
	 
});//jQuery


//自绘缩略图
function jcropThumbnail(){
	 $("#upload-thumbnail").Jcrop({
			onChange:jcropThumbnailCallBack,
	        allowSelect:false,
	        bgOpacity: 0.6,
	        setSelect: [0, 0, 300, 300],
	        bgColor: 'white',
	        addClass: 'jcrop-light',
	        aspectRatio: 1,
	        minSize: [120, 120],
	        onSelect:jcropThumbnailCallBack
	    }, function() {
	        jcrop_api = this;
	    });
	 
	 function jcropThumbnailCallBack(c){
		 if (parseInt(c.w) > 0) {
			 var rx = 120 / c.w;
	         var ry = 120 / c.h;
	         $("#thumbnail-120").css({
                width: Math.round(rx * 300) + 'px',
                height: Math.round(ry * 300) + 'px',
                marginLeft: '-' + Math.round(rx * c.x) + 'px',
                marginTop: '-' + Math.round(ry * c.y) + 'px'
            });
		 }
		 $('#x').val(c.x);
	     $('#y').val(c.y);
	     $('#w').val(c.w);
	     $('#h').val(c.h);	
	 }
}



/**
 *初始化事件绑定 
 */
function initBindEvent(){
	//收益类型切换
   $("#annualizedRateType").on("change",function(){
	   if($(this).val() == 1){
		   $("#ladder").show();
		   $("#increment").hide();
		   projectForm.ignore(".incrementInput");
		   projectForm.unignore(".ladderInput");
	   }else{
		   $("#ladder").hide();
		   $("#increment").show();
		   projectForm.ignore(".ladderInput");
		   projectForm.unignore(".incrementInput");
	   }
   });
   
   //增加投资下线
   $(".addLadderRow").on("click",function(){
		var trNum = $(".ladderTable tr").length+1;
		var newRow = $(".ladderTable").find(".ladderRow").first().clone(true);
		newRow.append("<td><input type='button' value='删除' class='btn btn-danger btn-sm btn-primary deleteLadderRow' onclick='deleteLadderRow(this)'></td>");
		$(".ladderTable .symbol:last").text("<");
		var maxInvest = $(".ladderTable .maxInvest:last").val();
		newRow.find("input[type='text']").val("");
		newRow.find(".minInvest").val(maxInvest);
		newRow.find(".symbol").text("<=");
		newRow.find(".minInvest").attr("id","minInvest"+trNum);
		newRow.find(".maxInvest").attr("id","maxInvest"+trNum);
		newRow.find(".annualizedRate").attr("id","annualizedRate"+trNum);
		newRow.find("input[type='text']").first().focus();//把光标定位到第一个方便框
		$(".ladderTable").append(newRow);
	});
   
   //添加项目
   $("#add_project").on('click', function() {
	   if($(this).hasClass("going")){
		   return;
	   }
	   var thumbnail = $("#thumbnail").val();
	   //var transferFlag = $("#transferFlag").val();
	   //if(transferFlag==0){
		//  // bootbox.alert("转让项目必须添加形象图");
		//  // return;
		//   removeHiddenVaildation();
	   //}
	   if(thumbnail=="" || thumbnail.length<1){
		   bootbox.alert("请上传项目缩略图");
		   return;
	   }
	   if(projectForm.check(false)){
		  var cStatus =  checkInvestData();
		  if(cStatus){
			   getInterestData();
			   $(this).addClass("going");
			   $(this).text("请稍候...");
		       $('#project_form').xform("post", config.urlMap.save,function(data){
		    	   if(!data.success){
		     		 // $.each(data.messages,function(n,v){
		    		   bootbox.alert(data.resultCodeEum[0].msg);
		     		//  })
		     	  }else{
		     		  bootbox.alert("添加项目成功",function(){
		     			 window.top.closeActiveIframe("项目列表");
		     		  });
		     	  }
		    	   $("#add_project").removeClass("going");
		    	   $("#add_project").text("保存");
		       });
		  }
	   }
   });
   
   function removeHiddenVaildation() {
		$('#project_form div:hidden').each(function() {
			$(this).find('select,input').removeAttr("datatype");
		});
	}
   //修改项目
   $("#update_project").on('click', function() {
	   if($(this).hasClass("going")){
		   return;
	   }
	   var thumbnail = $("#thumbnail").val();
	   if(thumbnail=="" || thumbnail.length<1){
		   bootbox.alert("请上传项目缩略图");
		   return;
	   }
	   if(projectForm.check(true)){
		   var cStatus =  checkInvestData();
		   if(cStatus){
			   getInterestData();
			   $(this).addClass("going");
			   $(this).text("请稍候...");
			   $('#project_form').xform("post", config.urlMap.update,function(data){
		    	   if(!data.success){
		     		 // $.each(data.messages,function(n,v){
		     			 bootbox.alert(data.resultCodeEum[0].msg);
		     		//  })
		     	  }else{
		     		  bootbox.alert("项目修改成功",function(){
		     			 window.top.closeActiveIframe("项目列表");
		     		  });
		     	  }
		    	   $("#update_project").removeClass("going");
		    	   $("#update_project").text("修改");
		       });
		   }
	   }
   });
   
   
   //收益递增计算
   $(".incrementObject").blur(function(){
	   var rateType = $("#annualizedRateType").val();
	   if(rateType == 2){
			var minInvestAmount = $("#minInvestAmount").val();//起投金额
			var incrementAmount = $("#incrementAmount").val();//递增金额
			var incrementMaxInvest = $("#maxInvestAmount").val();//最大收益额
			var minAnnualizedRate = $("#minAnnualizedRate").val();//最小年化收益
			var maxAnnualizedRate = $("#maxAnnualizedRate").val();//最大年化收益		  	
			var a = valAmount(minInvestAmount);
			var b = valAmount(incrementAmount);
			var c = valAmount(incrementMaxInvest);
			var d = valAmount(minAnnualizedRate);
			var e = valAmount(maxAnnualizedRate);
			if(a&&b&&c&&d&&e){
				  maxAnnualizedRate = parseFloat(maxAnnualizedRate).toFixed(2);
				  minAnnualizedRate = parseFloat(minAnnualizedRate).toFixed(2);
				  incrementMaxInvest = parseFloat(incrementMaxInvest).toFixed(2);
				  minInvestAmount = parseFloat(minInvestAmount).toFixed(2);
				  incrementAmount = parseFloat(incrementAmount).toFixed(2);
				  //递增收益=(最大年化收益-最小年化收益)/((最大收益额-起投金额)/递增金额)
				  var v = (maxAnnualizedRate-minAnnualizedRate)/((incrementMaxInvest-minInvestAmount)/incrementAmount);
				  var rate = v.toFixed(2);
				  if(rate < 0.0001 || !valAmount(rate)){
					  $("#incrementAnnualizedRate").val("");
				  }else{
					  $("#incrementAnnualizedRate").val(rate);
				  }
			}
	   }
   });
   
   //关闭页面
   $(".closePage").click(function(){
	   window.top.closeActiveIframe("项目列表");
   })
   //起投金额自动赋值给递增金额
   $("#minInvestAmount").blur(function(){
	   $("#incrementAmount").val($(this).val());
   })
}

/**
 * 债权号搜索提示
 */
function autocompleteDebtId(){
   $("#debtSerialNumber").autocomplete({  
	    minLength: 3,
	    source:function(request,response) {
	    	$.ajax({
	    		url: config.urlMap.findSerialNumber,
	    		dataType: "json",
	    		data:{
	    			serialNumber: request.term
                },
	    		success: function(data) {
	    			 response($.map(data, function(item) {
	    		         return {
	    		        	value: item.serialNumber,
	    		        	memberTrueName: item.memberTrueName
	    		         }
	    		        })
	    		     );
	    		}
	    	});
	    },
	    select: function( event, ui ) {
	    	deleteDropzoneAllFile();
	    	//选中后，填充数据
	    	fillDebtData(ui.item.value);
	    },
   }) .autocomplete( "instance" )._renderItem = function( ul, item) {
	   return  $( "<li>" ).append(item.value).append("<span style='color:#1963aa;float:right;'>"+item.memberTrueName+"</span>").appendTo( ul );
   };
}

/**
 * 删除投资下线
 * @param currRow
 */
function deleteLadderRow(currRow){
	$(currRow).closest(".ladderRow").remove();
	$(".ladderTable .symbol:last").text("<=");
}

//获得阶梯收益数据

//获得递增投资数据
function getInterestData(){
	var _jsonArray = "";
	var trLength = $(".ladderTable tr").length - 1;
	$(".ladderTable tr").each(function(index){
		var minInvest = $(this).find(".minInvest").val();
		var maxInvest = $(this).find(".maxInvest").val();
		var annualizedRate = $(this).find(".annualizedRate").val();
		_jsonArray += "{minInvest:'"+minInvest+"',maxInvest:'"+maxInvest+"',annualizedRate:'"+annualizedRate+"'}";
		if(index < trLength){
			_jsonArray += ",";
		}
	})
	if(_jsonArray != ""){
		$("#interestData").val("["+_jsonArray+"]");
	}else{
		$("#interestData").val("");
	}
}

/**填充数据
 * @param serialNumber
 */
function fillDebtData(serialNumber){
	$.post(config.urlMap.getDebtBySerialNumber+serialNumber,null,function(data){
		if(data == null || data == ""){
			bootbox.alert("没有找到债权编号："+serialNumber+"数据，可能是该债权已经被使用！");
		}else{
			$.each(data,function(name,value){
				if(name == "borrowMemberBaseBiz"){//借款人信息 
					$("#borrower_trueName").text(value.member.trueName);
				}else if(name == "lenderMember"){//出借人
					$("#lender_trueName").text(value.trueName);
				}else if(name == "debtType"){
					var debtType = getDictLabel(config.debtType,value);
					$("#debtType").text(debtType);
					$("#debtTypeText").val(debtType+"物估值");
					if(value == "collateral"){
						var debtCollateral = data.debtCollateral;
						if(debtCollateral != null && debtCollateral.collateralValuation != null){
							$("#valuation").text(debtCollateral.collateralValuation+"万");
						}
					}else if(value == "pledge"){
						var debtPledge = data.debtPledge;
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
					var day = data.interestFrom;
					var date = new Date(formatDate(value));
					if(day > 0){
						date = new Date((date.getTime()-(day*86400000)));
					}
					$("#saleEndTime").val(formatDate(value)+" 23:59");
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
							if(module=="debt_sign"){
								disableDropzone();
							}
						});
					}
					$(".dz-input-data").attr('disabled', 'disabled');
				}else if(name == "guarantyType"){
					var guarantyType = getGuarantyTypeName(config.guarantyType,data.instalment,value);
					$("#guarantyType").text(guarantyType);
					if(value=="car" &&  data.instalment==0){
						$(".j-join-lease").css("display","block");
					}else{
						$(".j-join-lease").css("display","none");
					}
					if(value=="equity"){
						UM.getEditor('form-field-description').setContent(equityHtml, false);
					}else if(value=="houseRecord"){
						UM.getEditor('form-field-description').setContent(creditHtml, false);
					}else if(value=="newCar"){
						UM.getEditor('form-field-description').setContent(newCarHtml, false);
					}else if(value=="carPayIn"){
						UM.getEditor('form-field-description').setContent(carPayInHtml, false);
					}else if(value=="carBusiness"){
						UM.getEditor('form-field-description').setContent(carBusinessInHtml, false);
					}else if(value=="car" && data.instalment==1){
						UM.getEditor('form-field-description').setContent(buyCarHtml, false);
					}else if(value=="runCompany"){
						UM.getEditor('form-field-description').setContent(runCompanyHtml, false);
					}else{
						UM.getEditor('form-field-description').setContent(initHtml, false);
					}
				}else{
					var attr = $("#"+name);
					if(attr.length > 0){//检查属性是否存在
						attr.text(value);
					}
				}
			});			
			$("#_serialNumber").val(serialNumber);
		}
	})
}

/***
 * 导出按钮的权限设置
 */
var exportButton = [];
var excelButton = { 
        "sExtends": "xls", 
        "sButtonText": "导出Excel" ,
        "sFileName": "项目列表.xls",
        "mColumns":[ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 ,13,14,15]
};

if(config.permission.projectExcel){
	exportButton.push(excelButton);
}
/**
 *项目列表数据 
 */
function projectListDataTable(){
	projectTable = $('#project-table').dataTable({
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
        	getSearchValue("#projectSearchForm",aoData);
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
            'bSortable' : false
        },{
			'mDataProp' : 'originalDebtNumber',
			'bSortable' : false,
			'mRender':function(data,type,row){
				return row.originalDebtNumber==null?"":row.originalDebtNumber;
			}
		}, {
            'mDataProp' : 'serialNumber',
            'bSortable' : true,
            'mRender':function(data,type,row){
            	return "<a href='#' onclick='toDebt(" + row.id + ")' >" + data + "</a>";
            }
        }, {
            'mDataProp' : 'name',
            'bSortable' : false
        }, {
            'mDataProp' : 'debtType',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.debtType, row.debtType);
            }
        }, {
            'mDataProp' : 'projectType',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.project_type, row.projectType);
            }
        },{
            'mDataProp' : 'totalAmount',
            'bSortable' : true
        }, {
            'mDataProp' : 'onlineTime',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
        }, {
            'mDataProp' : 'endDate',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd');
            }
        }, {
            'mDataProp' : 'incomeDays',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	//var dayNum = (new Date(formatDate(row.endDate)) - new Date(formatDate(row.startDate))) / (24 * 3600 * 1000) + 1 -row.interestFrom;
            	if(row.status ==20){//待发布状态下显示
            		if(data<0){
                		return 0;
                	}else{
                		return data;
                	}
            	}
            	return null;           	
            }
        }, {
            'mDataProp' : 'status',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.projectStatus, row.status);
            }
        },{
            'mDataProp' : 'lenderName',
            'bSortable' : false
        }, {
            'mDataProp' : 'auditName',
            'bSortable' : false
        }, {
            'mDataProp' : 'activitySign',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.activitySign, row.activitySign);
            }
        },{
			'mDataProp': 'remarks',
			'bSortable': false,
			'mRender': function(data, type, row) {
				if(data == null) 
					return "";
				if(row.endFlag > 0)
					return "<font color='red'>" + data + "</font>";
				else
					return data;
			}
		},{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  projectAction(row);
			}
		}]
		
	});//dataTable
}
$('#query_project').on('click', function() {
	projectTable.fnSettings()._iDisplayStart=0;
	projectTable.fnDraw({
		"fnServerParams": function(aoData) {
			getAllSearchValue(aoData);
		}
	});
	return false;

})
function toDebt(projectId){
	$.ajax({
		url:config.urlMap.getSerialNumber,
		data:{'projectId':projectId},
		dataType:"json",
		success:function(data){
			if(data.success){
				var id=data.result.id;
				var serial=data.result.serialNumber;
				window.top.setIframeBox("debt-" + "detail-" + id, config.urlMap.detail + id, serial + "债权详情");
			}else{
				bootbox.alert("");
			}
			
		}
	});
	//var url=config.urlMap.toDebt;
	//window.top.aaTT("债权列表",url);
	//window.top.setIframeBox("debt-edit-" + id, config.urlMap.toDebt + id, "债权列表");
	//window.top.closeActiveIframe("债权详情");
}
$("#project-table").on("click", '.delProjectRedis', function() {
	var projectId = $(this).attr("data-value");
	$.ajax({
		url:config.urlMap.delProjectRedis+"?projectId="+projectId,
		type:"post", 
		dataType:'json',
		success:function(data) {
			if(data.success){
				bootbox.alert("清除项目缓存成功！");
			}else{
				bootbox.alert("清除项目缓存失败！");
			}
		}
	});
});

/**
 * 查看详情
 * @param id
 * @param name
 */
function showProject(id,name){
	window.top.setIframeBox("show-"+id,config.urlMap.show+id,decodeURIComponent(name));
}

function editProject(id,name){
	window.top.setIframeBox("edit-"+id,config.urlMap.edit+id,decodeURIComponent(name));
}
/**
 * 紧急修改
 * @param id
 * @param name
 */
function emergencyProject(id,name){
	window.top.setIframeBox("emergency-"+id,config.urlMap.emergency+id,decodeURIComponent(name));
}

/**
 * 操作按钮
 * @param project 项目对象
 * @returns {String}
 */
function projectAction(project){
	var status = project.status;
	var btn = "",find="",waitReview="",review="",release="",stop="",start="",del="",remarks="",delProjectRedis="",setSave="";
	find = "<button class='btn btn-primary permission-"+config.permission.find+"' onclick=showProject('"+project.id+"','"+encodeURIComponent(project.name)+"')>查看</button>";
	edit = "<button class='btn btn-pink permission-"+config.permission.edit+"' onclick=editProject('"+project.id+"','"+encodeURIComponent(project.name)+"')>编辑</button>";//编辑
	waitReview = "<button class='btn permission-"+config.permission.waitReview+"' onclick=waitProject('"+project.id+"')>提交审核</button>";//提交审核 进入待审核
	review = "<button class='btn btn-danger permission-"+config.permission.review+"' onclick=reviewProject('"+project.id+"')>审核</button>";//审核
	uotTime = "<button class='btn btn-purple permission-"+config.permission.uotTime+"' onclick=modifyOnlineTimeProject('"+project.id+"','"+project.onlineTime+"','"+project.saleEndTime+"','"+project.startDate+"')>修改时间</button>";//待发布，修改开始结束时间
	stop ="<button class='btn  btn-warning permission-"+config.permission.stop+"' onclick=stopProject('"+project.id+"')>暂停</button>";//已暂停
	uEndDate ="<button class='btn  btn-purple permission-"+config.permission.uEndDate+"' onclick=modifyEndDateProject('"+project.id+"','"+project.saleEndTime+"')>修改时间</button>";//修改结束时间
	start = "<button class='btn btn-success permission-"+config.permission.start+"' onclick=startProject('"+project.id+"')>恢复</button>";//已还款
	del = "<button class='btn btn-inverse deleteProject permission-"+config.permission.delet+"' onclick=deleteProject('"+project.id+"')>删除</button>";//删除
	emergency = "<button class='btn btn-danger permission-"+config.permission.emergency+"' onclick=emergencyProject('"+project.id+"','"+encodeURIComponent('紧急修改'+project.name)+"')>紧急修改</button>";//编辑
	remarks = "<button class='btn btn-primary permission-"+config.permission.addRemarks+" addRemarks' data-value='"+project.id+"' data-controlRemarks='" + project.remarks +"'>添加备注</button> ";//添加备注
	delProjectRedis= "<button class='btn btn-primary permission-"+config.permission.delProjectRedis+" delProjectRedis' data-value='"+project.id+"' >清除项目缓存</button>";//清除缓存
	setSave= "<button class='btn btn-danger permission-"+config.permission.setSave+"' onclick=setSave('"+project.id+"')>置为存盘</button>";//置为存盘
	if(status == 1){//存盘
		return edit+waitReview+find+del+emergency+remarks+delProjectRedis;
	}else if(status == 10){//待审核
		return review+find+emergency+remarks+delProjectRedis;
	}else if(status == 20){//待发布
		return uotTime+find+emergency+remarks+delProjectRedis+setSave;
	}else if(status == 30){//投资中
		return stop+uEndDate+find+emergency+remarks+delProjectRedis;
	}else if(status == 40){//已暂停
		return start+find+emergency+remarks+delProjectRedis;
	}else if(status == 50 || status == 60 || status == 70){//已满额 已截止 已还款
		return find+emergency+remarks+delProjectRedis;
	}
}

$("#project-table").on("click", '.addRemarks', function() {
	var id = $(this).attr("data-value");
	var controlRemarks =  $(this).attr("data-controlRemarks");
	$('#controlRemarksId').val(id);
	if(controlRemarks == 'null') {
		$('#newControlRemarks').val('');
	} else {
		$('#newControlRemarks').val(controlRemarks);
	}
	$('#modal-controlRemarks').modal('show');
});
//添加备注，发送后台
$("#btn_add_remarks").on("click",function(){
	$(this).addClass("disabled");
	var that = $(this);
	var id = $("#controlRemarksId").val();
	var newControlRemarks= $("#newControlRemarks").val();
	$.post(
		config.urlMap.addRemarks,{id:id,newControlRemarks:newControlRemarks},function(data){
			that.removeClass("disabled");
			if(data.success){
				$('#modal-controlRemarks').modal('hide');
				projectTable.fnDraw('bStateSave', true);
			}else{
				showErrorMessage(data);
			}
		}
	);
});


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
				var customDropzone;
				var dropID = "";
				$.each(value,function(n,v){
					var module = v.module;
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
		if(project.activitySign > 0) {
			$("select[name='activitySign']").find("option[value='"+project.activitySign+"']").attr("selected",true);			
		}
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
	
	
	showTransferFlag(project.transferFlag);
}

/**
 * 查看项目详情
 * @param projectId
 */
function findProjectById(projectId){
	$.post(config.urlMap.find,{id:projectId},function(data){
		if (data.debtBiz){
			eachDebtBiz(data.debtBiz);
		}
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

/**
 * 编辑项目
 * @param projectId
 */
function editProjectById(projectId, method){
	$.post(config.urlMap.find,{id:projectId},function(data){
		var thumbnail = data.thumbnail;
		if(thumbnail !="" && thumbnail != null){
			$("#upload-project-thumbnail").hide();
			$("#upload-thumbnail-btn").hide();
			$("#thumbnail-120").attr("src",config.picURLHead+thumbnail);
			$("#project-thumbnail").attr("src",config.picURLHead+thumbnail);
			$("#project-thumbnail-pane").show();
		}
		eachDebtBiz(data.debtBiz);
		eachProject(data,method);
		loadUmeditor(data.description);
	})
}

/**
 * 删除项目
 * @param id
 */
function deleteProject(id){
	bootbox.confirm("确定要删除项目："+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.delet,{id:id},function(data){
				if(data.success){
					bootbox.alert("成功删除项目",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("项目删除失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 提交待审
 * @param id
 */
function waitProject(id){
	bootbox.confirm("确定把项目："+id+"提交审核吗？",function(result){
		if(result){
			$.post(config.urlMap.waitReview,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
		}
	})
}


/**
 * 项目审核
 * @param id
 */
function reviewProject(id){
	boxDialog("项目审核",true,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(config.urlMap.review,{id:id,message:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					if(data.resultCodeEum.length>0){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("操作失败，请稍后重试！");
					}
				}
			})
	})
}

/**
 * 修改项目发布
 * @param id
 */
function modifyOnlineTimeProject(id,onlineTime, saleEndTime,startDate){
	boxDialog2("修改时间",formatDate(onlineTime,'yyyy-mm-dd HH:mm'), formatDate(saleEndTime,'yyyy-mm-dd HH:mm'),function(){
		 var message = $('#message').val();
		 var onlineTime = $('#onlineTime').val();
		 var saleEndTime = $('#saleEndTime').val();
		 if(onlineTime == "" || saleEndTime == ""){
			 bootbox.alert("数据不能为空哦");
			 return;
		 }
		ediff = compareTwoDate(new Date(formatDate(startDate)),onlineTime); 
		//计息开始日期(取项目开始时间，不考虑T+因素)与上线时间
		if (ediff < 0) {
			 bootbox.alert("发布时间必须大于起息日");
			 return;
		 }
		 $.post(config.urlMap.uotTime,{id:id,onlineTime:onlineTime,saleEndTime:saleEndTime},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
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
/**
 * 修改截止时间
 * @param id
 * @param endDate
 */
function modifyEndDateProject(id, saleEndTime){
	boxDialog3("修改时间",formatDate(saleEndTime,'yyyy-mm-dd HH:mm'),function(){
		 var message = $('#message').val();		 
		 var saleEndTime = $('#saleEndTime').val();
		 if(saleEndTime == ""){
			 bootbox.alert("数据不能为空哦");
			 return;
		 }
		 $.post(config.urlMap.uEndDate,{id:id,saleEndTime:saleEndTime},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}

/**
 * 暂停项目
 * @param id
 */
function stopProject(id){
	boxDialog("项目暂停",false,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(config.urlMap.stop,{id:id,message:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}
/**
 * 置为存盘
 * @param id
 */
function setSave(id){
	boxDialogSet("置为存盘",false,function(){
		 var message = $('#message').val();
		 if(message == ""){
			 bootbox.alert("原因不能为空哦");
			 return;
		 }
		 $.post(config.urlMap.setSave,{id:id,message:message},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					if(!!data.resultCodeEum){
						return bootbox.alert(data.resultCodeEum[0].msg,function(){
							return;
						});
					}else{
						return bootbox.alert("操作失败！");
					}
				}
			})
	})
}
function boxDialogSet(title, isRadio, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessageSet(isRadio),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}
function boxMessageSet(isRadio){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>请填写置为存盘原因：</label>"+
    "<div class='col-md-4'><textarea id='message' name='message' type='text' placeholder='请填写置为存盘原因' class='form-control input-md' style='width: 341px; height: 89px;'></textarea></div></div>";
    html+="</form></div></div>";
	return html;
}
/**
 * 恢复项目状态到投资中
 * @param id
 */
function startProject(id){
	boxDialog("恢复项目状态到投资中",false,function(){
		 var message = $('#message').val();
		 var radioStatus = $("input[name='radioStatus']:checked").val();
		 $.post(config.urlMap.start,{id:id,message:message,radioStatus:radioStatus},function(data){
				if(data.success){
					bootbox.alert("操作成功。",function(){
						projectTable.fnDraw();
					})
				}else{
					bootbox.alert("操作失败，请稍后重试！");
				}
			})
	})
}

function boxDialog(title, isRadio, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage(isRadio),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxDialog2(title,onlineTime, saleEndTime, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage2(onlineTime, saleEndTime),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxDialog3(title, saleEndTime, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage3(saleEndTime),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxMessage(isRadio){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>请填写操作原因：</label>"+
    "<div class='col-md-4'><textarea id='message' name='message' type='text' placeholder='请填写操作原因' class='form-control input-md' style='width: 341px; height: 89px;'></textarea></div></div>";
    if(typeof isRadio !== "undefined" && isRadio){
    	 html+="<div class='form-group'><label class='col-md-4 control-label' for='radioStatus'>动作</label> ";
    	 html+="<div class='col-md-4'><div class='radio'><label for='radioStatus-1'><input type='radio' name='radioStatus' id='radioStatus-1' value='1' checked='checked'>通过 </label></div>";
    	 html+="<div class='radio'><label for='radioStatus-0'><input type='radio' name='radioStatus' id='radioStatus-0' value='0'> 退回 </label></div></div></div>";
    }
    html+="</form></div></div>";
	return html;
}

function boxMessage2(onlineTime, saleEndTime){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name' >上线时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+onlineTime+"' id='onlineTime' onclick='getDatePicker()'></div></div>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>销售截止时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+saleEndTime+"' id='saleEndTime' onclick='getDatePicker()'/></div></div>";
	 html+="</form></div></div>";
	return html;
}

function boxMessage3(saleEndTime){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>销售截止时间：</label>"+
    "<div class='col-md-4'><input type='text' value='"+saleEndTime+"' id='saleEndTime' onclick='getDatePicker()'/></div></div>";
	 html+="</form></div></div>";
	return html;
}



function getDatePicker(){
	WdatePicker(
		{
			isShowClear:true,
			readOnly:true,
			dateFmt:'yyyy-MM-dd HH:mm'
		}
	);
}

function validationInvest(){
	var minInvestAmount = parseFloat($("#minInvestAmount").val());//起投金额
	var incrementAmount = parseFloat($("#incrementAmount").val());//递增金额
	var totalAmount = parseFloat($("#amount").text());//总金额
	var maxRate = parseFloat($("#_maxAnnualizedRate").val());//年化利率
	
	if(minInvestAmount > totalAmount){
		investErrorMsg = "起投金额不能大于债权总额";
		return false;
	}
	if(incrementAmount > totalAmount){
		investErrorMsg = "递增金额不能大于债权总额";
		return false;
	}
	if(valAmount(minInvestAmount) && valAmount(incrementAmount) && valAmount(totalAmount) && valAmount(maxRate)){
		var checkStatus = checkInvest(minInvestAmount, incrementAmount, totalAmount, maxRate);
		if(!checkStatus){
			return false;
		}
		return true;
	}
	return false;
}

var rePrice = new RegExp("^[\\d]*(\\.\\d{1,2})?$");
function valAmount(val){
	if(val == null || val.length <= 0){
		return false;
	}
	return testRegExp(rePrice, val);
}

function testRegExp(re, str) {
	return re.test(str);
}

/**
 * 检查投资是否符号要求
 */
function checkInvest(minInvestAmount, incrementAmount, totalAmount, maxRate){
	var investAmount = minInvestAmount;//起投金额
	var rate = maxRate;
	var size = $(".ladderRow").length;
	var checkStatus = false;
	$(".ladderRow").each(function(index){
		var ind = index + 1;
		var minInvest = parseFloat($(this).find(".minInvest").val());
		var maxInvest = parseFloat($(this).find(".maxInvest").val());
		var annualizedRate = parseFloat($(this).find(".annualizedRate").val());
		checkStatus = false;
		//利率必须小于等于最大利率
		if(annualizedRate > maxRate){
			investErrorMsg = "第"+ind+"行，年化利率必须小于等于"+maxRate;
			return false;
		}
		//最小金额必须是递增金额倍数
		if(minInvest % incrementAmount !=0 ){
			investErrorMsg = "第"+ind+"行，投资下线最小金额必须是递增金额倍数";
			return false;
		}
		//最小金额必须是递增金额倍数
		if(maxInvest % incrementAmount != 0){
			investErrorMsg = "第"+ind+"行，投资下线最大金额必须是递增金额倍数";
			return false;
		}
		//投资下线金额必须大于等于起投金额、总金额
		if(minInvest < investAmount){
			investErrorMsg = "第"+ind+"行，投资下线最小金额必须大于或等于"+investAmount;
			return false;
		}
		
		if(maxInvest < investAmount){
			investErrorMsg = "第"+ind+"行，投资下线最大金额必须大于或等于"+investAmount;
			return false;
		}
		//minInvest < investAmount || maxInvest < investAmount || maxInvest > totalAmount
		if(maxInvest > totalAmount){
			investErrorMsg = "第"+ind+"行，投资下线最大金额必须小于等于"+totalAmount;
			return false;
		}
		
		//如果配置项大于1并且不是最后一项，上线必须大于下线
		if(size != 1 && size != ind && index > 0){
			if(maxInvest < investAmount){
				investErrorMsg = "第"+ind+"行，投资下线最大金额必须大于或等于最小金额";
				return false;
			}
		}
		//如果投资下线有多项，第一项最小和最大投资额度不能相等
		if(size > 1 && index == 0){
			if(minInvest >= maxInvest){
				investErrorMsg = "第"+ind+"行，投资下线最大金额必须是大于小最金额";
				return false;
			}
		}
		if(index > 0){
			if(annualizedRate <= rate){
				investErrorMsg = "第"+ind+"行，年化利率必须必须大于"+rate;
				return false;
			}
			if(minInvest != investAmount){//如果起投不等于上一项最大值，不允许通过!
				investErrorMsg = "第"+ind+"行，投资下线最小金额必须等于"+investAmount;
				return false;
			}
		}
		investAmount = maxInvest;
		rate = annualizedRate;
		checkStatus = true;
	});
	if(checkStatus){
		if(investAmount != totalAmount){
			investErrorMsg = "第"+size+"行，投资下线最大金额必须等于"+totalAmount;
			checkStatus = false;
		}
	}
	return checkStatus;
}


function checkInvestData(){
	var rateType = $("#annualizedRateType").val();
   if(rateType == 1){//阶递
	   if(!validationInvest()){
		   bootbox.alert(investErrorMsg);
		   return false;
	   }
   }
   if(rateType == 2){//递增
	  var mia = parseFloat($("#maxInvestAmount").val());
	  var totalAmount = parseFloat($("#amount").text());//总金额
	  var minInvestAmount = parseFloat($("#minInvestAmount").val());//起投金额
	  var incrementAmount = parseFloat($("#incrementAmount").val());//递增金额
	  var maxAnnualizedRate = parseFloat($("#maxAnnualizedRate").val());//最大年化率
	  var _maxAnnualizedRate = parseFloat($("#_maxAnnualizedRate").val());//最大年化率
	  if(maxAnnualizedRate > _maxAnnualizedRate){
		  bootbox.alert("年化收益区间最大值不能大于"+_maxAnnualizedRate);
		  return false;
	  }
	  if(minInvestAmount > totalAmount){
		  bootbox.alert("起投金额不能大于债权总额");
		  return false;
	  }
	  if(mia > (totalAmount)){
		  bootbox.alert("收益封顶不能大于"+(totalAmount));
		  return false;
	  }
	  if(incrementAmount > totalAmount){
		  bootbox.alert("递增金额不能大于债权总额");
		  return false;
	  }
	  if(totalAmount % incrementAmount !=0){
		  bootbox.alert("债权总额必须是递增金额的倍数");
		  return false;
	  }
   }
   return true;
}

/**
 * 上传项目图片
 */
function updateProjectThumbnail(){
	var previewNode = document.querySelector("#template");
    var previewTemplate;
    if(typeof previewNode !== "undefined" && previewNode != null)
    previewTemplate = previewNode.parentNode.innerHTML;
    $("#upload-thumbnail-btn").dropzone({
		url:config.urlMap.uploadURL,
		thumbnailWidth: null,
		thumbnailHeight: null,
		acceptedFiles:".jpg,.jpeg,.png",
		params:{category:'project-thumbnail'},
		maxFilesize:1,
		maxFiles:1,
		previewTemplate: previewTemplate,
		previewsContainer: "#previews",
		clickable: ".fileinput-button",
		dictDefaultMessage: "Drop files here to upload",
		dictFallbackMessage: "上传控件暂不支持您使用的浏览器。请使用Firefox4+、Chrome7+、IE10+、Opear12+、Safari6+版本以上的浏览器",
		dictFallbackText: "",
		dictFileTooBig: "101",// 文件大小超限
		dictInvalidFileType: "102",// 不支持文件类型
		dictResponseError: "103",// 服务器异常
		dictCancelUpload: "104",// 取消上传
		dictCancelUploadConfirmation: "确定要取消上传吗？",
		dictRemoveFile: "删除文件",
		dictMaxFilesExceeded: "105",// 文件上传数量超限了，不能再上传更多文件
		init: function() {
		      this.on("addedfile", function(file) {
		    	  $(".dz-processing").hide();
		      });
		      this.on("success",function(file,data){
		    	  if(data.uploadStatus != 1){
			      		alert("文件上传失败");
			      }else{
					var previewNode = document.querySelector("#template");
					var previewTemplate;
					if(typeof previewNode !== "undefined" && previewNode != null){
						previewTemplate = previewNode.parentNode.innerHTML;
						previewNode.parentNode.removeChild(previewNode);
					}
					var dropZoneFiles = this.files;
					this.removeFile(dropZoneFiles[0]);
					var fileInfo = data.fileInfo[0];
					var url =config.baseURL+"/"+ fileInfo.filePath;
					var mockFile = {
						name: fileInfo.name,
						size: fileInfo.fileSize,
						type: 'image/jpeg',
						status: Dropzone.SUCCESS,
						url   : url
					};
					$("#thumbnail").val(fileInfo.filePath);
					this.emit("addedfile", mockFile);
					this.emit("thumbnail", mockFile, url);
					$("#thumbnail-120").attr("src",url);
					jcropThumbnail();
			      }
		      });
		      this.on("error",function(file,data){
		    	  dropzoneError("upload-thumbnail-btn", file, data);
		      });
		      this.on("processing",function(file,data){
		    	  $(".dz-processing").hide();
		      })
    	}
	});
}

/**
 *项目是否可以转让
 */
function selectTransferFlag() {
	$("#transferFlag").on("change", function() {
		var type = $(this).val();
		showTransferFlag(type);
	});
}

function showTransferFlag(type) {
		if(type==1){
			$("#transfer_flag_detail").show();
			$("#transfer_flag_image").show();
		}else{
			$("#transfer_flag_detail").hide();
			$("#transfer_flag_image").hide();
		}
}
