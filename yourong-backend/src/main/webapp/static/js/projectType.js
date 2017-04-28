jQuery(function($) {
	$('#projectType_form').validate({
		rules : {},
		messages : {}
	});
	
	var oTable1 = $('#projectType-table-2').dataTable({
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
			'mDataProp' : 'projectTypeName',
			'bSortable' : true
		}, {
			'mDataProp' : 'projectTypeCode',
			'bSortable' : true
		}, {
			'mDataProp' : 'guarantyType',
			'bSortable' : true,
			'mRender': function(data, type, row) {
				return getDictLabel(config.guarantyType, data);
			}
		}, {
			'mDataProp' : 'guarantyThingType',
			'bSortable' : true
		}, {
			'mDataProp' : 'instalment',
			'bSortable' : true,
			'mRender':function(data,type,row){
				return data==1?"是":"否";
			}
		} ]
	});
	$('table th input:checkbox').on(
			'click',
			function() {
				var that = this;
				$(this).closest('table').find(
						'tr > td:first-child input:checkbox').each(function() {
					this.checked = that.checked;
					$(this).closest('tr').toggleClass('selected');
				});
			});
	$('#query_projectType').on('click', function() {
		oTable1.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	$('#new_projectType').on('click', function() {
		resetModel();
		$('#modal-table').modal({
			'show' : true
		});
	});
	$('#edit_projectType').on('click', function() {
		var id = $('table tr > td input:checked').first().val();
			$('#projectType_form').xform("load", config.urlMap.show + id,function(data) {
				showGuarantyInfo(data);
				renderSecurityDetail(data);
				$('#modal-table').modal('show');
			});
	});
	$('#delete_projectType').on('click', function() {
		var ids = [];
		$('table tr > td input:checked').each(function() {
			ids.push($(this).val());
		});
		if (ids.length == 0) {
			bootbox.alert("请选择数据！");
			return;
		}
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (result) {
				$.post(config.urlMap.delet, {
					"id" : ids
				}, function(data) {
					console.log(data);
					oTable1.fnDraw();
				});
			}
		});
	});
	function checkform(formData, jqForm, options) {
		return $("#projectType_form").valid();
	}
	
	$("#close_projectType").on('click',function(){
		resetModel();
	});
	$("#save_projectType").on('click', function() {
		//获取guaranty_info的值
		getGuarantyInfo();
		var options = {
			beforeSubmit : checkform,
			url : config.urlMap.save,
			type : "post",
			resetForm : true,
			success : function(data) {
				$('#modal-table').modal('toggle');
				oTable1.fnDraw();
				resetModel();
			}
		};
		$('#projectType_form').ajaxSubmit(options);
	});
	
	/**
	 * 获取项目类型信息的json串
	 */
	function getGuarantyInfo(){
		var infoArray = new Array();
		$("#guaranty_info_table tbody tr").each(function(i){
			var inputObj = {};
			$(this).find("input").each(function(i, domEle){
				var name =$(domEle).attr('iname');
				var val = $(domEle).val();
				inputObj[name] = val;
			});
			infoArray.push(JSON.stringify(inputObj));
		});
		var infoJson ='';
		if(infoArray.length >0 ){
			infoJson = "[" + infoArray.toString() + "]";
		}
		$("input[name='guarantyInfo']").val(infoJson);
	}
	
	/**
	 * 显示担保物信息
	 */
	function showGuarantyInfo(data){
		if(!!data && !!data.guarantyInfo){
			var guarantyInfo = JSON.parse(data.guarantyInfo)
			$.each(guarantyInfo,function(i,item){
				if(i>0){
					addGuarantyInfoRow();
				}
				var trObj = $('#guaranty_info_table tbody tr')[i];
				$.each(item,function(n,v){
					$(trObj).find("input[iname='"+n+"']").val(v);
				});
			});
		}
	}
	
	/**
	 * 动态添加行方法
	 */
	function addGuarantyInfoRow(){
		var infoHtml = "<tr>" +
				"<td><input size='14' type='text' value='' iname='info_name' >" +
				"</td><td>" +
				"<input size='14' type='text' value='' iname='info_name_code' >" +
				"</td><td >" +
				"<input size='14' type='text' value='' iname='info_name_type' >" +
				"</td><td >" +
				"<input size='14' type='text' value='' iname='is_not_null' >" +
				"</td><td >" +
				"<input size='14' type='text' value='' iname='valid_rule' >" +
				"</td><td >" +
				"<input size='14' type='text' value='' iname='valid_nullmsg' >" +
				"</td><td >" +
				"<input size='14' type='text' value='' iname='valid_errormsg' ></td><td >" +
				"<a  class='add_guaranty_info' href='javascript:void(0);'>+</a>" +
				"<a  class='del_guaranty_info' href='javascript:void(0);'>-</a></td>" +
				"</tr>";
		$("#guaranty_info_table tbody").append(infoHtml);
	}
	
	/**
	 * 移除行事件
	 */
	$("#guaranty_info_table").on("click", '.del_guaranty_info', function() {
		$(this).parent().parent().remove();
	});
	
	/**
	 * 添加行事件 
	 */
	$("#guaranty_info_table").on("click", '.add_guaranty_info', function() {
		addGuarantyInfoRow();
	});
	
	/**
	 * 重置模态对话框（主要是删除动态添加的行）
	 */
	function resetModel(){
		//动态添加行删除
		var trObjs = $("#guaranty_info_table tbody tr");
		$.each($(trObjs),function(i,ele){
			if(i>0){
				$(ele).remove();
			}
		});
		//设置securityType重置
		$("#debt_type").val("");
		showGuarantyTypeByDebtType("");
		//设置是否分期值
		$("#instalment").val(0);
	}
	
	$("#show_projectType").on("click",function(){
		$('#parse-project-type-form').modal({
			'show' : true
		});
	});
	

	$('#parse_projectType_form').validate({
		rules : {},
		messages : {}
	});
	
	
	$("#query_guaranty_thing").on("click",function(){
		var paramData = {
				guarantyType:$("#parse-guarantyType").val(),
				guarantyThingType:$("#parse-guarantyThingType").val(),
				instalment:$("#parse-instalment").val()
		}
		
		$.ajax({
			url:config.urlMap.search,
			dateType:"json",
			data:paramData,
			type:"post",
			success:function(data){
				if(!!data && !!data.guarantyInfo){
					var guarantyInfoJsonObj = JSON.parse(data.guarantyInfo);
					$.each(guarantyInfoJsonObj,function(i,item){
						var html = "<div  class='form-group clearfix'>" +
								"<label class='col-sm-1 control-label no-padding-right' for='form-field-type'>"+item.info_name+
								"</label><div class='col-sm-2'>";
								var inputHtml = '';
						var type = 'text';
						if(item.info_name_type == 'string'){
							type = 'text';
						}else if(item.info_name_type == 'date'){
							type = 'date';
						}
						var datatype = "";
						if(item.is_not_null == "1"){
							datatype= '*';
						}else{
							datatype = '';
						}
						
						var inputHtml = "<input type='"+type+"' iname='"+item.info_name_code+"' datatype='"+datatype+"' nullmsg='"+item.valid_msg+"'>";
						html  = html + inputHtml +"</div></div>";
						$("#guaranty_thing_detail").append(html);
						$("#guaranty_thing_detail").css("display","block");
					});
				}
			}
		});
	});
	
	$("#direct_type").on("change", function() {
		var type = $(this).find("option:selected").val();
		//担保物类型
		showGuarantyTypeByDebtType(type);
	});
	
	/*
	 * 根据担保类型显示不同的担保物
	 */
	function showGuarantyTypeByDebtType(directType, projectType) {
		var allGuaranty = ["credit_type", "guarantee_type"];
		$.each(allGuaranty, function(i, n) {
			if (n == directType + "_type") {
				$("#" + n).show();
				if (typeof projectType != 'undefined') {
					$("#" + n).find("select").val(projectType);
				}
			} else {
				$("#" + n).hide();
			}
		});
	}
	
	$("#credit_direct_type,#guarantee_type").on("change", function() {
		var type = $(this).find("option:selected").val();
		//设置担保物类型
		setGurantyThingType(type);
	});
	
	/**
	 * 设置担保物类型
	 * @param type
	 */
	function setGurantyThingType(type) {
		$("#input_collateralType").val(type);
	}
	
	function renderSecurityDetail(data) {
		showGuarantyTypeByDebtType(data.guarantyType, data.guarantyThingType);
	}
});
