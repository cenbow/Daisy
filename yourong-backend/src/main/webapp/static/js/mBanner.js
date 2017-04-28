jQuery(function($) {
	
	
	var bannerForm = $("#banner_form").Validform({
	        tiptype : 4,
	        btnReset : ".btnReset",
	        ajaxPost : false
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
	
	
	
	/**
	 * 更新公告权重
	 * @param projectID
	 * @param weight
	 */
	function updateBannerWeight(bannerId, position){
		$.post(config.urlMap.update,{bannerId:bannerId, position:position},function(data){
			oTableM.fnDraw();
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
		oTableM.fnDraw({
			"fnServerParams": function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	$('#new_banner').on('click', function() {
		$(".j-img-box").css("display","none");
		$(".j-imgBg-box").css("display","none");
		$("#form-field-image").attr("datatype","*");
		$("#new-windows-open").css("display","block");
		bannerForm.resetForm();
		$("select[name='areaSign']").find("option[value='']").attr("selected", true);
		$("select[name='activityId']").find("option[value='']").attr("selected", true);
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
			if(data.areaSign!=null){
				$("select[name='areaSign']").find("option[value='"+data.areaSign+"']").attr("selected",true);
			}
			if(data.activityId!=null){
				$("select[name='activityId']").find("option[value='"+data.activityId+"']").attr("selected",true);
			}
			$("#form-field-endTime").val(endformat);
			
			$("#form-field-name").val(data.type);
			
			$("#form-field-image").removeAttr("datatype");
			$("#new-windows-open").css("display","block");
			$('#modal-table').modal('show');
		});
	});
	
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
		bootbox.confirm("您确定删除么？删除后banner将不在M站首页展示。", function(result) {
			if (result) {
				$.post(config.urlMap.delet, {
					"id": ids.toString()
				}, function(data) {
					console.log(data);
					oTableM.fnDraw();
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
		if($("select[name='areaSign']").val() == '3' || $("select[name='areaSign']").val() == '8') {
			if($("#activityId").val() == "") {
				bootbox.alert("请选择关联活动！");
				return false;
			}
		}
		$('#banner_form').submit();
		oTableM.fnDraw();
	});
	
	$("select[name='search_areaSign']").on("change",function(){
		oTableM.fnDraw();
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

