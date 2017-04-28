jQuery(function($) {
	/**
	 * 标签列表
	 */
	var labelTable = $('#label-table').dataTable({
		'bFilter': false,
		'bProcessing': true,
		'bSort': true,
		'bServerSide': true,
		'fnServerParams': function(aoData) {
			getAllSearchValue(aoData);
		},
		'sAjaxSource': config.urlMap.ajax,
		'aoColumns': [{
            'mDataProp' : 'sort',
            'bSortable' : false,
            'mRender'   : function(data, type, row) {
                return "<input type='text' style='width: 60px' name='sort' data-id='"+row.id+"' readonly='readonly' value=" + row.sort + ">";
            }
        },{
			'mDataProp': 'labelName',
			'bSortable': false
		},{
			'mDataProp': 'category',
			'bSortable': false
		}, {
			'mDataProp': 'createTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		},{
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
	});
	
	
	//query
	$('#query_labelDetail').on('click', function() {
			labelTable.fnDraw();
		});
	
	// 添加标签
	$("#add_label").click(function(){
		$('#HelpCenterLabelTitle').text("添加标签");
	    $('#id').val(null);
	    $('#category').val("资金类");
	    $('#sort').text("");
	    $('#labelName').val("");
	    $('#add-modal-table').modal('show');
	});

	
	// 获取操作按钮
	function getAllOperation(row) {
		var edit = "<button class='btn btn-xs btn-info HelpCenterLabel-edit permission-" + config.permission.show + "'  data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
		var del = "<button class='btn btn-xs btn-danger HelpCenterLabel-del permission-" + config.permission.del + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		var btn='';
		btn = edit + del;
		return btn;
	}
	
	//删除 delet
	$("#label-table").on("click", '.HelpCenterLabel-del', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.del + id, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！", function() {
						labelTable.fnDraw();
					});
				} else {
					bootbox.alert("删除失败！");
				}
			});
		});
	});
	
	//编辑 edit
	$('#label-table').on("click", ".HelpCenterLabel-edit", function() {
		var id = $(this).attr("data-id");
		//resetValidation(); //验证初始化
		$("#HelpCenterLabelTitle").text("修改标签");
		$('#add-modal-table').modal("show");
		$("#labelForm").xform("load", (config.urlMap.show + id));
		
	});

	// 添加标签
	$("#submit_label").click(function(){
		var labelNameVal = $.trim($('#labelName').val());
		if(labelNameVal == null || labelNameVal == ""){
			bootbox.alert("请输入标签名!");
			return;
		}
	    $('#labelForm').xform('post', config.urlMap.save, function(data) {
	        if (!data.success) {
	            bootbox.alert("保存标签失败!",function(){
	                $('#submit_label').removeAttr("disabled");
	            });
	        } else {
	            bootbox.alert("保存标签成功!", function() {
	                $('#add-modal-table').modal('hide');
	                $('#submit_label').removeAttr("disabled");
	                labelTable.fnDraw();
	            });
	        }
	    });
	});
	
	// 排序
	$("#label_sort").click(function(){
	    $("input[name='sort']").each(function(e){
	        $(this).attr("readonly",false);
	    })
	    $(this).hide();
	    $('#submit_sort').show();
	});

	// 保存排序
	$("#submit_sort").click(function(){
	    var sorts="[";
	    $("input[name='sort']").each(function(){
	    	if($(this).data("id") != null){
	    		sorts+="{\"id\":\""+$(this).data("id")+"\",\"sort\":\""+$(this).val()+"\"}";
		        $(this).attr("readonly",true);
	    	}
	    })
	    sorts+="]";
	    $.ajax({
	        url:config.urlMap.savesort,
	        data:{
	            'sortstr':sorts
	        },
	        type:'post',
	        dataType:'json',
	        success:function(data){
	            if(data.success){
	            	labelTable.fnDraw();
	                bootbox.alert("保存成功");
	            }else{
	                bootbox.alert("保存失败");
	            }
	        }
	    });
	    $(this).hide();
	    $('#label_sort').show();
	});
	
});	
