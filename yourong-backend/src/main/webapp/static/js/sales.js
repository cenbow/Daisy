var startActivityDataTable,endActivityDataTable,memberDataTable,historyDetailDataTable;
$(function(){
	$("#salesPromotionForm").Validform({
		tiptype:3
	});
	$("#j-lingqu").html("");
	$("#j-lingqu").append($("#huiYuan").html());
	$("#j-lingqu").find("div").show();
	activityListDataTable();
	
	// 添加活动
	$("#add_sales").click(function(){
		clearFormData();//需要判断用户是否有保存，如果没保存则不清空数据
		$("#next_action").removeClass("find");
		$('#add-modal-table').modal('show');
		
	});
	
	$(".activityTab li").click(function(){
		var target = $(this).data("target");
		switch(target){
			case "start":
				if(startActivityDataTable == null){
					activityListDataTable();
				}else{
					startActivityDataTable.fnDraw();
				}
				break;
			case "end":
				if(endActivityDataTable == null){
					endActivityListDataTable();
				}else{
					startActivityDataTable.fnDraw();
				}
				break;
			case "member":
				if(memberDataTable == null){
					activityHistoryDataTable();
				}else{
					memberDataTable.fnDraw();
				}
				break;
		}
	})
	
	$(".activityType").change(function(){
		var type = $(this).val();
		togglePage(type);
		clearFormData(false);
	});
	
	$("#liPingList").on("click", ".removeGift", function () {
		var giftNum = $(".removeGift").length;
		if(giftNum <= 1){
			//显示没有数据
			$("#j-no-data-tips").show();
		}
	})
	
	$(".addGift").click(function(){
		var type = $(this).data("type");
		switch(type){
			case "xianJinQuan":
//				if(checkGiftIsAdd(type)){
//					bootbox.alert("礼品现金券已经存在，不能再添加。");
//					return;
//				}
				boxDialog("赠送现金券","xianJinQuan",function(){
					var num = $("#number").val();
					var id = $("#templateId").val();
					var obj = new Object();
					obj.templateId = id;
					obj.number = num;
					obj.type="xianJinQuan";
					addGift(obj);
				});
				break;
			case "shouYiQuan":
				boxDialog("赠送收益券","shouYiQuan",function(){
					var num = $("#number").val();
					var id = $("#templateId").val();
					var obj = new Object();
					obj.templateId = id;
					obj.number = num;
					obj.type="shouYiQuan";
					addGift(obj);
				});
				break;
			case "renQiZhi": 
				boxDialog("赠送人气值","renQiZhi",function(){
					var num = $("#number").val();
					var val = $("#value").val();
					var obj = new Object();
					obj.value = val;
					obj.number = num;
					obj.type="renQiZhi";
					addGift(obj);
				});
				break;
			case "qiTa":
				boxDialog("其它","qiTa",function(){
					var obj = new Object();
					var remarks = $("#remarks").val();
					obj.remarks = remarks;
					obj.type="qiTa";
					addGift(obj);
				});
				break;
			default:
				break;
		
		}
	})
	
	$("#up_action").click(function(){
		var page = $(this).data("page");
		switch(page){
			case "1":
				$("#num_2").hide();
				$("#num_1").show();
				$("#next_action").data("page",1);
				$("#up_action").hide();
				$("#next_action").text("下一步");
				break;
			case "2":
				$("#num_3").hide();
				$("#num_2").show();
				$("#next_action").data("page",2);
				$("#next_action").text("下一步");
				$("#up_action").data("page","1");
				break;
			default:
				break;
		}
	});
	
	$("#next_action").click(function(){
		var page = $(this).data("page");
		switch(page){
			case 1:
				$("#num_1").hide();
				$("#num_2").show();
				$(this).data("page",2);
				$("#up_action").data("page","1");
				$("#up_action").show();
				break;
			case 2:
				$("#num_2").hide();
				$("#num_3").show();
				$(this).data("page",3);
				if($("#next_action").hasClass("find")){
					$(this).text("关闭");
				}else{
					$(this).text("保存");
				}
				$("#up_action").data("page","2");
				break;
			default:
				break;
		}
		if(page >= 3){
			if($("#next_action").hasClass("find")){
				$('#add-modal-table').modal('hide');
			}else{
				submitActivity();
			}
//			$('#salesPromotionForm')[0].reset();
		}
		
	});
	
	
	bindInputBlurEvent();
	
});


function bindInputBlurEvent(){
	$("input[type=text]").on("blur",function(){
		if($(this).hasClass("valid-other")){
			if($(this).val()=="" || $(this).val().length >=300){
				$(this).addClass("validation-failed");
			}else{
				if($(this).hasClass("validation-failed")){
					$(this).removeClass("validation-failed");
				}
			}
		}else{
			validation($(this));
		}
	})
}

function togglePage(type){
	if(type==3){
		$("#j-lingqu").html("");
		$("#j-lingqu").append($("#xianMu").html());
		$("#j-lingqu").find("div").show();
	}else{
		$("#j-lingqu").html("");
		$("#j-lingqu").append($("#huiYuan").html());
		$("#j-lingqu").find("div").show();
	}
	zhengDianTime();
	
	bindInputBlurEvent();
}

function submitActivity(){
	if(validationInput()){
		var activityId = $("#activityId").val();
		var actionUrl = config.urlMap.add;
		if(activityId != ""){
			actionUrl = config.urlMap.update;
		}
		var type = $("#type").val();
		var name = $("#name").val();
		var startTime = $("#startTime").val();
		var endTime = $("#endTime").val();
		var obtainConditionsJson = getObtainConditionsJson();
		var ruleParameterJson = getGiftParam();
		xShade();
		$.post(actionUrl,{id:activityId,type:type,name:name,startTime:startTime,endTime:endTime,obtainConditionsJson:obtainConditionsJson,ruleParameterJson:ruleParameterJson},
			function(data){
				xShade("hide");
				$('#add-modal-table').modal('hide');
				startActivityDataTable.fnDraw();
			}
		)
	}else{
		bootbox.alert("验证失败！<br/>&nbsp;&nbsp;1.请修改红色边框的值;<br/>&nbsp;&nbsp;2.必须指定至少一个条件;<br/>&nbsp;&nbsp;3.领取条件填写的值应该是数字且最大值不能大于1亿");
	}
	
}



/**
 * 获得礼品
 * @returns {String}
 */
function getGiftParam(){
	var giftParam ="[";
	var pLength = $(".giftParamData").length-1;
	$(".giftParamData").each(function(i,j){
		if(pLength>i){
			giftParam = giftParam+$(this).data("param")+",";
		}else{
			giftParam = giftParam+$(this).data("param");
		}
	})
	if(pLength>=0){
		giftParam+="]";
	}
	return giftParam;
}

/**
 * 获得领取条件
 * @returns
 */
function getObtainConditionsJson(){
	var data = $("#salesPromotionForm").serializeArray();
	var type = $("#type").val();
	var map = new Map();
	$(data).each(function(){
		var attrObj = $("#"+this.name);
		//某些字段不需要
		if(attrObj.hasClass("exclude") || attrObj.length < 1){
			return;
		}
		var refid = attrObj.data("refid");
		var method = attrObj.data("method");
		var rule = attrObj.data("rule");
		if(typeof(rule) !== "undefined"){
			var methodObj = new Object();
			methodObj.name=method;
			if(typeof refid !==  "undefined"){
				var refObj = $("#"+refid);
				var refid2 = refObj.data("refid");
				if(refObj.is("select") && refid2 != null){
					methodObj.rule=refObj.val();
					var refObj2 = $("#"+refid2);
					methodObj.value = refObj2.val();
				}else{
					methodObj.value=refObj.val();
				}							
			}
			if(map.containsKey(rule)){
				var obj = map.get(rule);							
				obj.push(methodObj);
			}else{
				var b = new Array();
				b.push(methodObj);
				map.put(rule,b);
			}
		}
	});
	if(type == 3){
		var baseData = JSON.parse(JSON.stringify(map));
		var projectIds = $("#xianMuId").val();
		if(projectIds != ""){
			var projectArray = new Array();
			var ids = projectIds.split(",");
			for(i=0;i<ids.length;i++){
				var project = new Object();
				project.id=ids[i];
				project.name="";
				projectArray.push(project);
			}
			baseData.projects = projectArray;
		}
		return JSON.stringify(baseData);
	}else{
		return JSON.stringify(map);
	}
}

function boxDialog(title, type, callBackFun){
	bootbox.dialog({
		title: title,
		 message:  boxMessage(type),
		 buttons: {
			 success: {
				 label: "提交",
				 className: "btn-success",
				 callback: callBackFun
			 }
		 }
		
	})
}

function boxMessage(type){
	switch(type){
		case "xianJinQuan":
			return xianJinQuan();
			break;
		case "shouYiQuan":
			return shouYiQuan()
			break;
		case "renQiZhi":
			return renQiZhi()
			break;
		case "qiTa":
			return qiTa();
			break;
	}
}

/**
 * 礼券：现金券
 * @returns {String}
 */
function xianJinQuan(){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal' id='giftForm'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='templateId'>现金券模板编号：</label>"+
    "<div class='col-md-4'><input type='text' id='templateId' name='templateId'/></div></div>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='number'>参加人数：</label>"+
    "<div class='col-md-4'><input type='text' id='number' name='number' /><input type='hidden' name='type' id='type' value='xianJinQuan'/></div></div>";
    html+="</form></div></div>";
	return html;
}

/**
 * 礼券：收益券
 * @returns {String}
 */
function shouYiQuan(){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal' id='giftForm'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='templateId'>收益券模板编号：</label>"+
    "<div class='col-md-4'><input type='text' id='templateId' name='templateId'/></div></div>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='number'>参加人数：</label>"+
    "<div class='col-md-4'><input type='text' id='number' name='number'/><input type='hidden' name='type' id='type' value='shouYiQuan'/></div></div>";
    html+="</form></div></div>";
	return html;
}

/**
 * 礼券：人气值
 * @returns {String}
 */
function renQiZhi(){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal' id='giftForm'>"+
    "<div class='form-group'><label class='col-md-4 control-label' for='name'>赠送人气值：</label>"+
    "<div class='col-md-4'><input type='text' id='value' name='value'/><input type='hidden' name='type' id='type' value='renQiZhi'/></div></div>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>参加人数：</label>"+
    "<div class='col-md-4'><input type='text' id='number' name='number'/></div></div>";
    html+="</form></div></div>";
	return html;
}

/**
 * 礼券：其他
 * @returns {String}
 */
function qiTa(){
	var html = "<div class='row'><div class='col-md-12'><form class='form-horizontal' id='giftForm'>"+
	"<div class='form-group'><label class='col-md-4 control-label' for='name'>备注：</label>"+
    "<div class='col-md-4'><textarea id='remarks' name='remarks' placeholder='请说明赠送什么礼品，该礼品另由程序派送。' class='form-control input-md' style='width: 341px; height: 89px;'></textarea>" +
    "<input type='hidden' name='type' id='type' value='qiTa'/></div></div>";
    html+="</form></div></div>";
	return html;

}

/**
 * 检查礼品是否添加
 * @param type
 * @returns {Boolean}
 */
function checkGiftIsAdd(type){
	if($("."+type).length <= 0){
		return false;
	}
	return true;
}

/**
 * 创建礼品
 * @param type
 * @param giftData
 * @returns {String}
 */
function createGiftContainer(type, giftData){
	var alertClass ="";
	var giftType="";
	switch(type){
		case "xianJinQuan":
			giftType="现金券";
			alertClass="xianJinQuan alert-danger";
			break;
		case "shouYiQuan":
			giftType="收益券";
			alertClass="shouYiQuan alert-success";
			break;
		case "renQiZhi":
			giftType="人气值";
			alertClass="renQiZhi alert-info";
			break;
		case "qiTa":
			giftType="其它";
			alertClass="qiTa alert-qiTa";
			break;
	}
	var html="<div class='alert "+alertClass+" giftParamData'>"+
				"<button data-dismiss='alert' class='close removeGift' type='button'>"+
					"<i class='icon-remove'></i>"+
				"</button>"+
				"<strong id='giftType'>"+giftType+"</strong>"+
				"<div id='giftData'>"+giftData+"</div>"+
			"</div>";
	return html;
}


/**
 * 活动表格
 * @param id
 * @returns
 */
function activityDataTable(id){
	var dataTable = $('#'+id).dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'bRetrieve' : true,
        'fnServerParams' : function(aoData) {
        	if(id=="end-sales-table"){
        		 aoData.push({
                     "name": "search_selectType",
                     "value": 6
                 });
        	}
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
        },{
			'mDataProp' : 'activityName',
			'bSortable' : false
		}, {
            'mDataProp' : 'type',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.activityType, row.type);
            }
        }, {
            'mDataProp' : 'startTime',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
        }, {
            'mDataProp' : 'endTime',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
        }, {
            'mDataProp' : 'activityStatus',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.activityStatus, row.activityStatus);
            }
        }, {
            'mDataProp' : 'createId',
            'bSortable' : true
        },{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  getAction(row);
			}
		}]
		
	});//dataTable
	return dataTable;
}

function activityHistoryDataTable(){
	memberDataTable = $('#sales-member-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'bServerSide' : true,
        'bRetrieve' : true,
        'fnServerParams' : function(aoData) {
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.history,
        'aoColumns' : [ 
        {
            'mDataProp' : 'activityId',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.activityId + ">";
            }
        },{
			'mDataProp' : 'activityName',
			'bSortable' : false
		}, {
            'mDataProp' : 'type',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.activityType, row.type);
            }
        },{
            'mDataProp' : 'total',
            'bSortable' : true
        },{
			'mDataProp' : 'activityId',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return activityHistoryAction(row);
			}
		}]
		
	});//dataTable
}

function activityHistoryDetailTable(){
	historyDetailDataTable = $('#history-detail-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'bServerSide' : true,
        'bRetrieve' : true,
        'fnServerParams' : function(aoData) {
        	var activityId = $("#activityHistoryId").val();
        	aoData.push({
                "name": "search_activityId",
                "value": activityId
            });
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.historyDetail,
        'aoColumns' : [ 
        {
            'mDataProp' : 'memberId',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.memberId + ">";
            }
        },{
			'mDataProp' : 'memberId',
			'bSortable' : false
		},{
			'mDataProp' : 'trueName',
			'bSortable' : false
		}, {
            'mDataProp' : 'mobile',
            'bSortable' : true
        },{
			'mDataProp' : 'createTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
				return formatDate(data,'yyyy-mm-dd HH:mm:ss');
			}
		}]
		
	});//dataTable
}

function activityHistoryAction(row){
	var find = "<button class='btn btn-primary permission-"+2+"' onclick=showActivityHistory('"+row.activityId+"')>查看</button>";//查看
	return find;
}

function showActivityHistory(activityId){
	//$("#activityHistoryData").html("");
	$("#activityHistoryId").val(activityId);
	$("#history-modal-table").modal('show');
	if(typeof historyDetailDataTable !== "undefined"){
		historyDetailDataTable.fnDraw();
	}else{
		activityHistoryDetailTable();
	}
}

/**
 * 进行中的活动
 */
function activityListDataTable(){
	startActivityDataTable = activityDataTable("start-sales-table");
}

/**
 * 结束的活动
 */
function endActivityListDataTable(){
	endActivityDataTable = activityDataTable("end-sales-table");
}

/**
 * 操作按钮
 * @param status
 * @param id
 * @returns {String}
 */
function getAction(activity){
	var find = "<button class='btn btn-primary  permission-"+2+"' onclick=findActivity('"+activity.id+"')>查看</button>";//查看
	var update = "<button class='btn btn-pink  permission-"+2+"' onclick=updateActivity('"+activity.id+"')>修改</button>";//修改
	var submittedForReview = "<button class='btn btn-purple  permission-"+2+"' onclick=submittedForReview('"+activity.id+"','"+encodeURIComponent(activity.activityName)+"')>提交审核</button>";//修改
	var review = "<button class='btn btn-danger  permission-"+2+"' onclick=reviewActivity('"+activity.id+"','"+encodeURIComponent(activity.activityName)+"')>审核</button>";//审核
	var del = "<button class='btn btn-inverse  permission-"+2+"' onclick=deleteActivity('"+activity.id+"','"+encodeURIComponent(activity.activityName)+"')>删除</button>";//删除
	if(activity.activityStatus == 0){//存盘
		return find+update+submittedForReview+del;
	}else if(activity.activityStatus == 1){//待发布
		return find+review;
	}else{
		return find;
	}
}

function findActivity(activityId){
	$("#next_action").addClass("find");
	getActivityData(activityId);
}

function updateActivity(activityId){
	$("#next_action").removeClass("find");
	getActivityData(activityId);
}

/**
 * 根据ID获得活动数据
 * @param activityId
 */
function getActivityData(activityId){
	clearFormData();
	$.post(config.urlMap.select,{activityId:activityId},function(data){
		$('#add-modal-table').modal('show');
		$.each(data,function(name,value){
			if(name == "activityStatus"){
				if(value != 0){
					$("#next_action").addClass("find");
				}
			}
			if(name=="name"){
				$("#name").val(value);
			}else if(name=="id"){
				$("#activityId").val(value);
			}else if(name=="type"){
				//这里不使用jquery实现，是因为来回切换有问题
				var typeSelect = document.getElementById("type");
				for (var i = 0; i < typeSelect.options.length; i++) {
					if (typeSelect.options[i].value == value) {
						typeSelect.options[i].selected = true;
						break;
					} 
				}
				togglePage(value);
			}else if(name=="startTime"){
				$("#startTime").val(formatDate(value,'yyyy-mm-dd HH:mm'));
			}else if(name=="endTime"){
				$("#endTime").val(formatDate(value,'yyyy-mm-dd HH:mm'));
			}else if(name=="obtainConditionsJson"){
				var obj = JSON.parse(value);
				var ele = obj.elements;
				for(i=0;i<ele.length;i++){
					var values = ele[i].value;
					for(j=0;j<values.length;j++){
						var mobj = eval(values[j]);
						var cbox = $("#"+mobj.name);
						if(cbox.length>0){
							cbox.attr("checked", true);
							var refid = cbox.data("refid");
							if(typeof refid !==  "undefined"){
								var refObj = $("#"+refid);
								var refid2 = refObj.data("refid");
								if(refObj.is("select")){
									var refSelect = document.getElementById(refid);
									for (var i = 0; i < refSelect.options.length; i++) {
										if (refSelect.options[i].value == mobj.value) {
											refSelect.options[i].selected = true;
											break;
										} 
									}
									if(refid2 != null){
										$("#"+refid2).val(mobj.value);
									}
								}else{
									refObj.val(mobj.value);
								}							
							}
						}
					}
				}
				//项目活动
				var projects = obj.projects;
				var pids = "";
				if(typeof(projects) !== "undefined"){
					for(j=0;j<projects.length;j++){
						pids +=projects[j].id+",";
					}
					$("#xianMuId").val(pids.substring(0,pids.length-1));
				}
				
			}else if(name=="ruleParameterJson"){
				var obj = JSON.parse(value);
				if(obj.length>0){
					$("#j-no-data-tips").hide();
				}
				for(i=0;i<obj.length;i++){
					$("#liPingList").append(addGift(obj[i]));
				}
			}
		});
	})
}


/**
 * 清理数据
 */
function clearFormData(isClear){
	$("#num_2,#num_3").hide();
	$("#num_1").show(); 
	$("#up_action").data("page",1);
	$("#next_action").data("page",1);
	$("#next_action").text("下一步");
	$("#up_action").hide();
	$("#liPingList").html("");
	$("#j-no-data-tips").show(); 
	if(isClear === undefined || isClear== true){
		$("#activityId").val("");
		$('#salesPromotionForm')[0].reset();
	}
	zhengDianTime();
}

function addGift(giftObj){
	var giftData = "";
	switch(giftObj.type){
		case "xianJinQuan":
			giftData="现金券模板："+giftObj.templateId+",参加人数："+giftObj.number;
			break;
		case "shouYiQuan":
			giftData="收益券模板："+giftObj.templateId+",参加人数："+giftObj.number;
			break;
		case "renQiZhi":
			giftData="人气值："+giftObj.value+",参加人数："+giftObj.number;
			break;
		case "qiTa":
			giftData=giftObj.remarks;
			break;
	}
	$("#liPingList").append(createGiftContainer(giftObj.type, giftData));
	$("."+giftObj.type).data("param",JSON.stringify(giftObj));
	$("#j-no-data-tips").hide();
}


function deleteActivity(id,name){
	bootbox.confirm("确定要删除活动："+decodeURIComponent(name)+"吗？",function(result){
		if(result){
			$.post(config.urlMap.del,{activityId:id},function(data){
				if(data.success){
					bootbox.alert("成功删除活动",function(){
						startActivityDataTable.fnDraw();
					})
				}else{
					bootbox.alert("活动删除失败，请稍后重试！");
				}
			})
		}
	})
}

function reviewActivity(id,name){
	bootbox.confirm("确定活动【"+decodeURIComponent(name)+"】审核通过吗？",function(result){
		if(result){
			$.post(config.urlMap.review,{activityId:id},function(data){
				if(data.success){
					bootbox.alert("活动审核通过",function(){
						startActivityDataTable.fnDraw();
					})
				}else{
					bootbox.alert("活动审核失败，请稍后重试！");
				}
			})
		}
	})
}

function submittedForReview(id,name){
	bootbox.confirm("确定把活动【"+decodeURIComponent(name)+"】提交审核吗？",function(result){
		if(result){
			$.post(config.urlMap.submittedForReview,{activityId:id},function(data){
				if(data.success){
					bootbox.alert("提交审核成功",function(){
						startActivityDataTable.fnDraw();
					})
				}else{
					bootbox.alert("提交审核失败，请稍后重试！");
				}
			})
		}
	})
}

function zhengDianTime(){
	$(".zhendianTime").empty();
	for(var i=0;i<=23;i++){
		var time ="";
		if(i<10){
			time = 0+""+i+":00"
		}else{ 
			time = i+":00"
		};
		var option ="<option value='"+time+"'>"+time+"</option>";
		$("#zhengDianTouZiSJ").append(option);
		$("#xiangMuZDTZSJ").append(option);
	}
}

function validationInput(){
	var checkFlag = true;
	$("#j-lingqu input[type=checkbox]:checked").each(function(){
		var refId = $(this).data("refid");
		if(typeof refId !== "undefined"){
			var refObj = $("#"+refId);
			var refid2 = refObj.data("refid");
			if(refObj.is("select")){
				if(typeof refid2 !== "undefined"){//是select 并且有指定refId
					//判断refObj有没有再关联的input,验证该input的值
					var refObj2 = $("#"+refid2);
					var flag = validation(refObj2);
					if(checkFlag){
						checkFlag = flag;
					}
				}
			}else{
				//验证refObj的值
				var flag = validation(refObj);
				if(checkFlag){
					checkFlag = flag;
				}
			}							
		}
	});
	if(checkFlag){//如果为true,需要检查有没有设置领取规则
		var num = $("#j-lingqu input[type=checkbox]:checked").size();
		if(num < 1){
			checkFlag = false;
		}
	}
	return checkFlag;
}


function validation(inputObj){
	var flag = true;
	if(!checkInputValue(inputObj.val())){
		inputObj.addClass("validation-failed");
		flag = false;
	}else{
		if(inputObj.hasClass("validation-failed")){
			inputObj.removeClass("validation-failed");
			flag = false;
		}
	}
	return flag;
}

function checkInputValue(value){
	if(value == "" || isNaN(value) || parseInt(value) >= 100000000 || parseInt(value) <= 0){
		return false;
	}
	return true;
}


/**
 * map
 */
function Map() {
	this.elements = new Array();
	// 获取MAP元素个数
	this.size = function() {
	    return this.elements.length;
	};
	// 判断MAP是否为空
	this.isEmpty = function() {
	    return (this.elements.length < 1);
	};
	// 删除MAP所有元素
	this.clear = function() {
	    this.elements = new Array();
	};
	// 向MAP中增加元素（key, value)
	this.put = function(_key, _value) {
	    this.elements.push( {
	        key : _key,
	        value : _value
	    });
	};
	// 删除指定KEY的元素，成功返回True，失败返回False
	this.remove = function(_key) {
	    var bln = false;
	    try {
	        for (i = 0; i < this.elements.length; i++) {
	            if (this.elements[i].key == _key) {
	                this.elements.splice(i, 1);
	                return true;
	            }
	        }
	    } catch (e) {
	        bln = false;
	    }
	    return bln;
	};
	// 获取指定KEY的元素值VALUE，失败返回NULL
	this.get = function(_key) {
	    try {
	        for (i = 0; i < this.elements.length; i++) {
	            if (this.elements[i].key == _key) {
	                return this.elements[i].value;
	            }
	        }
	    } catch (e) {
	        return null;
	    }
	};
	// 获取指定索引的元素（使用element.key，element.value获取KEY和VALUE），失败返回NULL
	this.element = function(_index) {
	    if (_index < 0 || _index >= this.elements.length) {
	        return null;
	    }
	    return this.elements[_index];
	};
	// 判断MAP中是否含有指定KEY的元素
	this.containsKey = function(_key) {
	    var bln = false;
	    try {
	        for (i = 0; i < this.elements.length; i++) {
	            if (this.elements[i].key == _key) {
	                bln = true;
	            }
	        }
	    } catch (e) {
	        bln = false;
	    }
	    return bln;
	};
	// 判断MAP中是否含有指定VALUE的元素
	this.containsValue = function(_value) {
	    var bln = false;
	    try {
	        for (i = 0; i < this.elements.length; i++) {
	            if (this.elements[i].value == _value) {
	                bln = true;
	            }
	        }
	    } catch (e) {
	        bln = false;
	    }
	    return bln;
	};
	// 获取MAP中所有VALUE的数组（ARRAY）
	this.values = function() {
	    var arr = new Array();
	    for (i = 0; i < this.elements.length; i++) {
	        arr.push(this.elements[i].value);
	    }
	    return arr;
	};
	// 获取MAP中所有KEY的数组（ARRAY）
	this.keys = function() {
	    var arr = new Array();
	    for (i = 0; i < this.elements.length; i++) {
	        arr.push(this.elements[i].key);
	    }
	    return arr;
	};
}