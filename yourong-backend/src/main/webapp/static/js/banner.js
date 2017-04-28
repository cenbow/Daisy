jQuery(function($) {
	
	var appForm = $("#app_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
	  });
	
	var appForm = $("#app_title_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
	  })
	
	
	var bannerForm = $("#banner_form").Validform({
	        tiptype : 4,
	        btnReset : ".btnReset",
	        ajaxPost : false
    });
	  
	var oTable1 = $('#banner-table-2').dataTable({
		'bFilter': false,
		'bProcessing': true,
//		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
			aoData.push({
				"name": "search_type",
				"value": "0"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTable1 !== "undefined"){
				var dts = oTable1.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'areaSign',
			'bSortable': false,
			'mRender':function(data,type,row){
				return getDictLabel(config.areaSign, row.areaSign);
			}
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}, {
			'mDataProp': 'targetBlank',
			'bSortable': false,
			'mRender':function(data){
				if(data==1){
					return "是";
				}else{
					return "否";
				}
			}
		}
		]
	});
	
	var oTableApp = $('#app-banner-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "1"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableApp !== "undefined"){
				var dts = oTableApp.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'areaSign',
			'bSortable': false,
			'mRender':function(data,type,row){
				return getDictLabel(config.versionSign, row.areaSign);
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	var oTableM = $('#m-banner-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "2"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableM !== "undefined"){
				var dts = oTableM.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	/**
	 * app广告参数表
	 */
	var appTable = $('#app-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.getParam,
		'aoColumns' : [ {
			'mDataProp' : 'key',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				if(data=="link"){
					return "点击广告跳转链接(请输入带http或https前缀的网址)";
				}
				if(data=="appTitle"){
					return "链接网页标题";
				}
				return data;
				
			}
		}, {
			'mDataProp' : 'value', //https://oss-cn-hangzhou.aliyuncs.com
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				var img = "";
				if(data!==""){
					img = img + "<div    id='imglist'><li><a><img class=' imglist' alt='' src='https://oss-cn-hangzhou.aliyuncs.com"+ data +"'  height='50' width='50'/></a></li>" +
					"	</div> ";
				}
				if(row.key=="link" || row.key == "appTitle"){
					img = data;
				}
				return  img;
				
			}
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) { 
				var buttons="";
				if(row.value==""){//修改按钮
					buttons = buttons + "<button  class='btn  btn-sm btn-primary permission-"+config.permission.save+" add_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>添加</button>";
				}else{
					buttons = buttons + "<button  class='btn  btn-pink btn-sm btn-primary permission-"+config.permission.save+" edit_app'data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>修改</button>";
					buttons = buttons + "<button  class='btn  btn-sm btn-primary permission-"+config.permission.clear+" clear_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>置空</button>";
				}
				
				return   buttons;
			}
		} ]
	});
	
	//app首页广告
	var oTableAppIndexAd = $('#appIndexAd-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "6"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableAppIndexAd !== "undefined"){
				var dts = oTableAppIndexAd.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'areaSign',
			'bSortable': false,
			'mRender':function(data,type,row){
				return getDictLabel(config.versionSign, row.areaSign);
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	//app首页弹窗
	var oTableAppIndexPopup = $('#appIndexPopup-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "7"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableAppIndexPopup !== "undefined"){
				var dts = oTableAppIndexPopup.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	
	var oTableAppActivity = $('#app-activity-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "3"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableApp !== "undefined"){
				var dts = oTableApp.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	var oTableAppActivityFind = $('#app-activity-find-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "4"
			});
		},
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof oTableApp !== "undefined"){
				var dts = oTableApp.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
			'mDataProp': 'id',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		}, {
			'mDataProp': 'name',
			'bSortable': false
		}, {
			'mDataProp': 'startTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'endTime',
			'bSortable': false,
			'mRender':function(data,type,row){
				return data==null?"":formatDate(data,"yyyy-mm-dd HH:mm");
			}
		}, {
			'mDataProp': 'bannerStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.bannerStatus == 0) {
					return "待生效";
				}
				if(row.bannerStatus == 1) {
					return "已生效";
				}
				if(row.bannerStatus == -1) {
					return "已失效";
				}
			}
		}, {
			'mDataProp': 'href',
			'bSortable': false
		}
		]
	});
	
	
	/**
	 * app发现页面图标配置
	 */
	var appFindTable = $('#app-find-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource' : config.urlMap.getAppFindParam,
		'aoColumns' : [ {
			'mDataProp' : 'key',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				if(data=="link"){
					return "点击广告跳转链接(请输入带http或https前缀的网址)";
				}
				if(data=="appTitle"){
					return "链接网页标题";
				}
				return data;
				
			}
		}, {
			'mDataProp' : 'value', //https://oss-cn-hangzhou.aliyuncs.com
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				var img = "";
				if(data!==""){
					img = img + "<div    id='imglist'><li><a><img class=' imglist' alt='' src='https://oss-cn-hangzhou.aliyuncs.com"+ data +"'  height='50' width='50'/></a></li>" +
					"	</div> ";
				}
				if(row.key=="first_name"||row.key=="second_name"||row.key=="third_name"||row.key=="fourth_name"){
					img = data;
				}
				return  img;
				
			}
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) { 
				var buttons="";
				if(row.value==""){//修改按钮
					buttons = buttons + "<button  class='btn  btn-sm btn-primary permission-"+config.permission.save+" add_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>添加</button>";
				}else{
					buttons = buttons + "<button  class='btn  btn-pink btn-sm btn-primary permission-"+config.permission.save+" edit_app'data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>修改</button>";
					buttons = buttons + "<button  class='btn  btn-sm btn-primary permission-"+config.permission.clear+" clear_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>置空</button>";
				}
				
				return   buttons;
			}
		} ]
	});

	/**
	 * app红包页面table
	 */
	var appLuckyMoneyTable = $('#app-luckymoney-table').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams': function(aoData) {
			aoData.push({
				"name": "search_type",
				"value": "5"
			});
		},
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender': function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='bannerId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		},{
			'mDataProp' : 'name',
			'bSortable' : false
		}, {
			'mDataProp' : 'image', //https://oss-cn-hangzhou.aliyuncs.com
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				var img = "";
				if(data!==""){
					img = img + "<div    id='imglist'><li><a><img class=' imglist' alt='' src='https://oss-cn-hangzhou.aliyuncs.com"+ data +"'  height='50' width='50'/></a></li>" +
						"	</div> ";
				}
				return  img;

			}
		}, {
			'mDataProp' : 'href', //https://oss-cn-hangzhou.aliyuncs.com
			'bSortable' : false
		} ]
	});
	
	

	$("#app-table,#app-find-table").on("click", '.add_app', function() {
		var id = $(this).attr("data-id");
		var type = $(this).attr("data-type");
		var value =  $(this).attr("data-value");
		if(type=="link"||type=="appTitle"||type=="first_name"||type=="second_name"||type=="third_name"||type=="fourth_name")
		{	
			$("#title_type").val(type);
			$("#title_id").val(id);
			if(type=="link"){
				$("#title_type-lable").text("链接地址：");
			}
			if(type=="appTitle"){
				$("#title_type-lable").text("链接地址标题：");
			}
			$("#value").val("");
			
			$('#apptitle-modal-table').modal({
				'show': true
			});
		}else{
			$("#type").val(type);
			$("#id").val(id);
			$("img[name='image']").attr("src","");
			$(".j-img-box").css("display","none");
			$("#advert-lable").text("投放设备:"+type);
			$("#type-lable").text(type+":");
			if(type=="android"){
				$("#type-lable").text("通用(720*1280)");
			}
			$('#app-modal-table').modal({
				'show': true
			});
		}
	});
/*	$("#app_title_save_banner").on('click', function() {
		alert("1")
		$('#app_title_form').submit();
		appTable.fnDraw();
	});*/
	
	
	$("#app_title_save_banner").on('click', function() {
		
		var title_type = $("#title_type").val();
		var value = $("#value").val();
		if(title_type=="link"&&value.indexOf("http")!=0){
			bootbox.alert("请输入带http或https前缀的网址");
			return;
		}
		
		var options = {
			beforeSubmit : checkform,
			url : config.urlMap.saveTitle,
			type : "post",
			resetForm : true,
			success : function(data) {
				$('#apptitle-modal-table').modal('toggle');
				appTable.fnDraw();
				appFindTable.fnDraw();
			}
		};
		$('#app_title_form').ajaxSubmit(options);
	});
	
	$("#app_save_banner").on('click', function() {
		var options = {
				beforeSubmit : checkform,
				url : config.urlMap.saveAdvert,
				type : "post",
				resetForm : true,
				success : function(data) {
					$('#app-modal-table').modal('toggle');
					appTable.fnDraw();
					appFindTable.fnDraw();
				}
			};
			$('#app_form').ajaxSubmit(options);
		
		
		//$('#app_form').submit();
		//appTable.fnDraw();
	});
	
	$("#app-table,#app-find-table").on("click", '.edit_app', function() {
		
		var id = $(this).attr("data-id");
		var type = $(this).attr("data-type");
		var value =  $(this).attr("data-value");
		if(type=="link"||type=="appTitle"||type=="first_name"||type=="second_name"||type=="third_name"||type=="fourth_name")
		{	
			$("#title_type").val(type);
			$("#title_id").val(id);
			if(type=="link"){
				$("#title_type-lable").text("链接地址：");
			}
			if(type=="appTitle"){
				$("#title_type-lable").text("链接地址标题：");
				
			}
			$("#value").val(value);
			$('#apptitle-modal-table').modal({
				'show': true
			});
			
		}else{
			$("#advert-lable").text("投放设备:"+type);
			$("#type-lable").text(type+":");
			if(type=="android"){
				$("#type-lable").text("通用(720*1280)");
			}
			$("#type").val(type);
			$("#id").val(id);
			
			$(".j-img-box").css("display","block");
			$("img[name='image']").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+value);
			
			$('#app-modal-table').modal('show');
		}
	});
	$("#app-table,#app-find-table").on("click", '.clear_app', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("您确定删除么？", function(result) {
			if (result) {
				$.post(config.urlMap.clear, {
					"id": id.toString()
				}, function(data) {
					appTable.fnDraw();
					appFindTable.fnDraw();
				});
			}
		});
	});
	
	$("#banner-table-2 tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#banner-table-2 tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
			console.info(position);
		}
	}).disableSelection();
	
	$("#app-banner-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#app-banner-table tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	
	$("#m-banner-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#m-banner-table tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	
	$("#appIndexAd-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#appIndexAd-table tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	
	$("#appIndexPopup-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#appIndexPopup-table tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	

	$("#app-activity-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#app-activity-table tbody tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	
	$("#app-activity-find-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#app-activity-find-table tr").index(u.item);
			var bannerId = ($(u.item).find("[iname='bannerId']").val());
			updateBannerWeight(bannerId, position);
		}
	}).disableSelection();
	
	
	/**
	 * 更新公告权重
	 * @param projectID
	 * @param weight
	 */
	function updateBannerWeight(bannerId, position){
		$.post(config.urlMap.update,{bannerId:bannerId, position:position},function(data){
			oTable1.fnDraw();
			oTableApp.fnDraw();
			oTableM.fnDraw();
			oTableAppIndexAd.fnDraw();
			oTableAppIndexPopup.fnDraw();
			oTableAppActivity.fnDraw();
			oTableAppActivityFind.fnDraw();
			appFindTable.fnDraw();
			
		})
	}
	
	$('table th input:checkbox').on('click', function() {
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
			.each(function() {
				this.checked = that.checked;
				$(this).closest('tr').toggleClass('selected');
			});
	});
	$('#query_banner').on('click', function() {
		oTable1.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	$('#new_banner').on('click', function() {
		$("select[name='areaSign']")[0].selectedIndex = areaSignIndex;
		$(".j-img-box").css("display","none");
		$(".j-imgBg-box").css("display","none");
		$("#form-field-image").attr("datatype","*");
		$("#new-windows-open").css("display","block");
		$("#d-areaSign").css("display","block");
		$("#d-versionSign").css("display","none");
		$("#d-activity").css("display","block");
		bannerForm.resetForm();
		$("select[name='areaSign']").find("option[value='']").attr("selected", true);
		$("select[name='activityId']").find("option[value='']").attr("selected", true);
		
		$("#d-share-title").css("display","none");
		$("#d-share-word").css("display","none");
		$("#d-share-flag").css("display","none");
		
		$('#modal-table').modal({
			'show': true
		});
	});
	$('#edit_banner').on('click', function() {
		var id = $('table tr > td input:checked').first().val();
		if(typeof id === 'undefined'){
			bootbox.alert("请先选择数据!");
			return false;
		}
		bannerForm.resetForm();
		$("#banner_form").xform("load", config.urlMap.show + id, function(data) {
			$(".j-img-box").css("display","block");
			$("#d-versionSign").css("display","none");
			
			$('#banner_form').form("load", data);
			if(data.startTime!=null){
				var startformat = formatDate(data.startTime,"yyyy-mm-dd HH:mm");
			}
			$("#form-field-startTime").val(startformat);
			if(data.endTime!=null){
				var endformat = formatDate(data.endTime,"yyyy-mm-dd HH:mm");
			}
			if(data.image!=null){
				$("img[name='image']").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+data.image);
			}
			if(data.imageBg!=null){
				$(".j-imgBg-box").css("display","block");
				$("img[name='imageBg']").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+data.imageBg);
			}else{
				$(".j-imgBg-box").css("display","none");
			}
			if(data.areaSign!=null&& data.type!=1){
				$("select[name='areaSign']").find("option[value='"+data.areaSign+"']").attr("selected",true);
			}
			if(data.areaSign!=null&&data.type==1){
				$("#d-versionSign").css("display","block");
				$("select[name='versionSign']").find("option[value='"+data.areaSign+"']").attr("selected",true);
			}
			if(data.activityId!=null){
				$("select[name='activityId']").find("option[value='"+data.activityId+"']").attr("selected",true);
			}
			resetForm(data.type);
			$("#form-field-endTime").val(endformat);
			$("#form-field-endTime").val(endformat);
			$("#form-field-image").removeAttr("datatype");
			$("#banner_type").val(data.type);
			$('#modal-table').modal('show');
		});
	});
	
	function resetForm(type){
		if(type==1 || type==6  ){
			$("#new-windows-open").css("display","none");
			$("#target_blank").val(0);
			$("#d-areaSign").css("display","none");
			$("#d-activity").css("display","none");
			areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
			$("select[name='areaSign']").find("option[value='1']").attr("selected", true);
			
			$("#d-share-flag").css("display","block");
		}else if(type==2){
			$("#new-windows-open").css("display","none");
			$("#target_blank").val(0);
			$("#d-areaSign").css("display","none");
			$("#d-activity").css("display","none");
			areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
			$("select[name='areaSign']").find("option[value='2']").attr("selected", true);	
			
			$("#d-share-title").css("display","none");
			$("#d-share-word").css("display","none");
			$("#d-share-flag").css("display","none");

			
		}else if(type==3||type==4){
			$("select[name='areaSign']")[0].selectedIndex = areaSignIndex;
			$("#new-windows-open").css("display","block");
			$("#d-areaSign").css("display","none");
			$("#d-activity").css("display","block");
			$("#d-versionSign").css("display","none");
			areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
			$("select[name='areaSign']").find("option[value='3']").attr("selected", true);	
			
			$("#d-share-flag").css("display","block");
			
		}else if (type==5){
			$("#new-windows-open").css("display","none");
			$("#target_blank").val(0);
			$("#d-areaSign").css("display","none");
			$("#d-versionSign").css("display","block");
			$("#d-activity").css("display","none");
			$("#d-versionSign").css("display","none");
			areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
			$("select[name='areaSign']").find("option[value='1']").attr("selected", true);

			$("#d-share-flag").css("display","none");
		}else if(type==7){
			$("#new-windows-open").css("display","none");
			$("#target_blank").val(0);
			$("#d-areaSign").css("display","none");
			$("#d-versionSign").css("display","block");
			$("#d-activity").css("display","none");
			$("#d-versionSign").css("display","none");
			areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
			$("select[name='areaSign']").find("option[value='1']").attr("selected", true);

			$("#d-share-flag").css("display","none");
		}else{
			$("select[name='areaSign']")[0].selectedIndex = areaSignIndex;
			$("#new-windows-open").css("display","block");
			$("#d-areaSign").css("display","block");
			$("#d-activity").css("display","block");
			
			$("#d-share-title").css("display","none");
			$("#d-share-word").css("display","none");
			$("#d-share-flag").css("display","none");
		}
	}
	
	
	
//	$("input[name='file']").on("change",function(){
////		$("input[name='image']").val($(this).val());
////		$(this).on('click',function(){
////			alert("1");
////		});
//	});
	
	$('#delete_banner').on('click', function() {
		var ids = [];
		$('table tr > td input:checked').each(function() {
			ids.push($(this).val());
		});
		if (ids.length == 0) {
			bootbox.alert("请选择数据！");
			return;
		}
		bootbox.confirm("您确定删除么？删除后banner将不再展示。", function(result) {
			if (result) {
				$.post(config.urlMap.delet, {
					"id": ids.toString()
				}, function(data) {
					console.log(data);
					oTable1.fnDraw();
					oTableM.fnDraw();
					oTableApp.fnDraw();
					oTableAppActivity.fnDraw();
					oTableAppActivityFind.fnDraw();
					oTableAppIndexAd.fnDraw();
					oTableAppIndexPopup.fnDraw();
					appLuckyMoneyTable.fnDraw();
				});
			}
		});
	});

	function checkform(formData, jqForm, options) {
		return $("#banner_form").valid();
	}
	$("#save_banner").on('click', function() {
		//验证框架
		if(!bannerForm.check(false)){
			return false;
		}
		if($("#form-field-name").val() == "") {
			bootbox.alert("请输入banner标题！");
			return false;
		}
		//banner图片格式校验
		if(!checkFileFormat("#form-field-image")){
			return false;
		}
		//底图格式校验
		if(!checkFileFormat("#form-field-imageBg")){
			return false;
		}
		//
		
		//将json字符串转化为json格式对象
//			var a =$("#j-json-image").val();
//			var a1 = a.substring(1,a.length-1);
//				var imgObj = jQuery.parseJSON(a1);
//				console.info(imgObj.fileUrl);
		//判断结束时间必须大于开始时间
		var startTime = $("#form-field-startTime").val();
		var endTime = $("#form-field-endTime").val();
		if(compareTwoDate(startTime,endTime)<=0){
			bootbox.alert("结束时间必须大于开始时间！");
			return false;
		}
		//判断链接地址必须为http开头
		var href = $("#form-field-href").val();
		if(typeof(href) != "undefined" && href.length > 0 && href.length < 4){
			bootbox.alert("链接长度必须大于4");
			return false;
		}
		if($("#banner_type").val() == 0 && $("select[name='areaSign']").val() == "") {
			bootbox.alert("请选择banner显示位置！");
			return false;
		}
		if($("#banner_type").val() == 3 ) {
			var name = $("#form-field-name").val();
			if(typeof(name) == "undefined"||name=="" ){
				bootbox.alert("请填写banner标题！");
				return false;
			}
			var href = $("#form-field-href").val();
			if(typeof(href) == "undefined"||href=="" ){
				bootbox.alert("请填写banner链接！");
				return false;
			}
			if($("#activityId").val() == "") {
				bootbox.alert("请选择关联活动！");
				return false;
			}
		}
		
		if($("select[name='areaSign']").val() == '3' || $("select[name='areaSign']").val() == '8') {
			if($("#activityId").val() == "") {
				bootbox.alert("请选择关联活动！");
				return false;
			}
		}
		if($("#share_flag").val() == '1' ) {
			if($("#form-field-shareWord").val() == "" ||$("#form-field-shareTitle").val() == "") {
				bootbox.alert("分享标题和分享文案不能为空");
				return false;
			}
		}
		
		$('#banner_form').submit();
		oTable1.fnDraw();
	});
	
	$("select[name='search_areaSign']").on("change",function(){
		oTable1.fnDraw();
	});
});

function checkFileFormat(imageId){
	var fileName = $(imageId).val(),
        extIndex=fileName.lastIndexOf('.'),
        ext=fileName.substr(extIndex).toLowerCase();
	if(ext!=null && ext!=""){
		if(ext!=".jpg"&&ext!=".png"&&ext!=".jpeg"){
			bootbox.alert("只能上传jpg,png,jpeg格式的图片");
			$(imageId).val("");
			return false;
		}else{
			return true;
		}
	}else{
		return true;
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
		return 1;
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
$("#form-field-shareFlag").on("change",function(){
	var type = $("#share_flag").val();
	if(type==1){//开
		$("#d-share-title").css("display","block");
		$("#d-share-word").css("display","block");
	}else{//关
		$("#d-share-title").css("display","none");
		$("#d-share-word").css("display","none");
	}
	
	
});
var areaSignIndex = "";
$("#banner_type").on("change",function(){
	$("select[name='activityId']").find("option[value='']").attr("selected", true);
	var type = $(this).val();
	if(type==1 || type==6 ){//app
		$("#new-windows-open").css("display","none");
		$("#target_blank").val(0);
		$("#d-areaSign").css("display","none");
		$("#d-versionSign").css("display","block");
		$("#d-activity").css("display","none");
		areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
		$("select[name='areaSign']").find("option[value='1']").attr("selected", true);
		
		$("#d-share-flag").css("display","block");
		
	}else if(type==2){//m
		$("#new-windows-open").css("display","none");
		$("#target_blank").val(0);
		$("#d-areaSign").css("display","none");
		$("#d-versionSign").css("display","none");
		$("#d-activity").css("display","none");
		areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
		$("select[name='areaSign']").find("option[value='2']").attr("selected", true);	
		
		$("#d-share-title").css("display","none");
		$("#d-share-word").css("display","none");
		$("#d-share-flag").css("display","none");

		
	}else if(type==3||type==4){ // 3 活动页  4 发现页
		$("select[name='areaSign']")[0].selectedIndex = areaSignIndex;
		$("#new-windows-open").css("display","block");
		$("#d-areaSign").css("display","none");
		$("#d-activity").css("display","block");
		$("#d-versionSign").css("display","none");
		
		$("#d-share-flag").css("display","block");

		
	}else if(type==5){
		$("#new-windows-open").css("display","none");
		$("#target_blank").val(0);
		$("#d-areaSign").css("display","none");
		$("#d-versionSign").css("display","block");
		$("#d-activity").css("display","none");
		$("#d-versionSign").css("display","none");
		areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
		$("select[name='areaSign']").find("option[value='1']").attr("selected", true);

		$("#d-share-flag").css("display","none");
	}else if(type==7){
		$("#new-windows-open").css("display","none");
		$("#target_blank").val(0);
		$("#d-areaSign").css("display","none");
		$("#d-versionSign").css("display","block");
		$("#d-activity").css("display","none");
		$("#d-versionSign").css("display","none");
		areaSignIndex = $("select[name='areaSign']")[0].selectedIndex;
		$("select[name='areaSign']").find("option[value='1']").attr("selected", true);

		$("#d-share-flag").css("display","none");
	}else{
		$("select[name='areaSign']")[0].selectedIndex = areaSignIndex;
		$("#new-windows-open").css("display","block");
		$("#d-areaSign").css("display","block");
		$("#d-activity").css("display","block");
		$("#d-versionSign").css("display","none");
		
		$("#d-share-title").css("display","none");
		$("#d-share-word").css("display","none");
		$("#d-share-flag").css("display","none");

	}
	
});

