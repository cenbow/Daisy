jQuery(function($) {
	
	var appForm = $("#app_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
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
				'bSortable' : false
			}, {
				'mDataProp' : 'value', //https://oss-cn-hangzhou.aliyuncs.com
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
				'mDataProp': 'operation',
				'bSortable': false,
				'mRender': function(data, type, row) { 
					var buttons="";
					if(row.value==""){//修改按钮
						buttons = buttons + "<button  class='btn  btn-sm btn-primary add_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>添加</button>";
					}else{
						buttons = buttons + "<button  class='btn  btn-pink btn-sm btn-primary edit_app'data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>修改</button>";
						buttons = buttons + "<button  class='btn  btn-sm btn-primary clear_app' data-id='"+row.id+"' data-type='"+row.key+"' data-value='"+row.value+"'>置空</button>";
					}
					
					return   buttons;
				}
			} ]
		});
		
		
		$("#app-table").on("click", '.add_app', function() {

			$("img[name='image']").attr("src","");
			$(".j-img-box").css("display","none");
			var id = $(this).attr("data-id");
			var type = $(this).attr("data-type");
			var value =  $(this).attr("data-value");
			$("#type-lable").text(type+":");
			$("#type").val(type);
			$("#id").val(id);
			$("#advert-lable").text("投放设备:"+type);
			$("#type-lable").text("");
			if(type=="android"){
				$("#type-lable").text("通用(720*1280)");
			}
			
			
			$('#modal-table').modal({
				'show': true
			});
		});
	$("#save_banner").on('click', function() {
		$('#app_form').submit();
		appTable.fnDraw();
	});
	
	$("#app-table").on("click", '.edit_app', function() {
		
		var id = $(this).attr("data-id");
		var type = $(this).attr("data-type");
		var value =  $(this).attr("data-value");
		
		$("#advert-lable").text("投放设备:"+type);
		$("#type-lable").text("");
		if(type=="android"){
			$("#type-lable").text("通用(720*1280)");
		}
		$("#type").val(type);
		$("#id").val(id);
		
		$(".j-img-box").css("display","block");
		$("img[name='image']").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+value);
		
		$('#modal-table').modal('show');
		
	});
	$("#app-table").on("click", '.clear_app', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("您确定删除么？", function(result) {
			if (result) {
				$.post(config.urlMap.clear, {
					"id": id.toString()
				}, function(data) {
					appTable.fnDraw();
				});
			}
		});
		
		
	});
		
});


