jQuery(function($) {
	var roleTable = $('#sysRole-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
//		'fnInitComplete':function(){
//			$(".roleEditBtn").on("click",function(){
//				var id = $(this).attr("data-value");				
//				roleTreeInit(id);
//			})
//		},
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + ">";
			}
		}, {
			'mDataProp' : 'id'
		}/*, {
			'mDataProp' : 'officeId',
			'bSortable' : true,
			"bVisible" : false
		}*/, {
			'mDataProp' : 'name',
			'bSortable' : true
		}/*, {
			'mDataProp' : 'dataScope',
			'bSortable' : true,
			"bVisible" : false
		}*/, {
			'mDataProp' : 'createBy',
			'bSortable' : true,
			"bVisible" : false
		}, {
			'mDataProp' : 'createDate',
			'bSortable' : true
		}/*, {
			'mDataProp' : 'updateBy',
			'bSortable' : true,
			"bVisible" : false
		}*/, {
			'mDataProp' : 'updateDate',
			'bSortable' : true
		}, {
			'mDataProp' : 'remarks',
			'bSortable' : false
		}/*, {
			'mDataProp' : 'delFlag',
			'bSortable' : true,
			"bVisible" : false
		}*/,{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  "<button class='btn btn-xs btn-info roleEditBtn' data-value='"+row.id+"'><i class='icon-check bigger-120'></i></button>  ";
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
	$("#sysRole-table-2").on("click", '.roleEditBtn', function() {
		var id = $(this).attr("data-value");				
		roleTreeInit(id);
	});
	
	
	$('#query_sysRole').on('click', function() {
		roleTable.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	
	
	$('#new_sysRole').on('click', function() {
		$(".modalFormTitle").text("添加角色权限");
		$('#modal-table').modal({
			'show' : true
		});
	});
	
	
	$('#edit_sysRole').on('click', function() {
		$(".modalFormTitle").text("编辑角色权限");
		var id = 0, checked = $('table tr > td input:checked');
        if (checked.length) {
            id = checked.first().val();
            $("#sysRole_form").xform("load", (config.urlMap.show + id));
            $('#modal-table').modal('show');
        } else {
            bootbox.alert("请选择数据！");
        }
	});
	
	
	$('#delete_sysRole').on('click', function() {
		 var ids = [];
        $('table tr > td input:checked').each(function() {
            ids.push($(this).val());
        });
        if (ids.length === 0) {
            bootbox.alert("请选择数据！");
            return;
        }
        bootbox.confirm("你确定要删除吗?", function(result) {
            if (result) {
            	ids = ids.join(",");
                $("#sysRole_form").xform("post", config.urlMap.delet+"?ids="+ids);
                roleTable.fnDraw();
            }
        });
	});
	
	function checkform(formData, jqForm, options) {
		return $("#sysRole_form").valid();
	}
	
	$("#save_sysRole").on('click', function() {
		 $('#sysRole_form').xform("post", config.urlMap.save);
		 roleTable.fnDraw();
	});
});
