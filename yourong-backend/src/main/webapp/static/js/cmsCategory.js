jQuery(function($) {
	//表单验证初始化
    var categoryForm = $("#cmsCategory_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
    });
	var categoryTable = $('#cmsCategory-table-2').dataTable({
		'order':[[0,"desc"]],
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
			'mDataProp' : 'parentName',
			'bSortable' : false
		}, {
			'mDataProp' : 'module',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
	            	return getDictLabel(config.module, row.module)
            }
		}, {
			'mDataProp' : 'name',
			'bSortable' : true
		}, {
			'mDataProp' : 'href',
			'bSortable' : false
		}, {
			'mDataProp' : 'target',
			'bSortable' : false
		}, {
			'mDataProp' : 'sort',
			'bSortable' : true
		}, {
			'mDataProp' : 'inMenu',
			'bSortable' : false,
			'mRender': function(data,type,row){
				if(Number(data)===0){
					return "否";
				}else{
					return "是";
				}
			}
		}, {
			'mDataProp' : 'allowComment',
			'bSortable' : false,
			'mRender': function(data,type,row){
				if(Number(data)===0){
					return "否";
				}else{
					return "是";
				}
			}
		}, {
			'mDataProp' : 'isAudit',
			'bSortable' : false,
			'mRender': function(data,type,row){
				if(Number(data)===0){
					return "否";
				}else{
					return "是";
				}
			}
		}, {
			'mDataProp' : 'createTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
		}, {
			'mDataProp' : 'updateTime',
			'bSortable' : false,
			'mRender'   : function(data, type, row) {
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
			}
		}]
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
	$('#query_cmsCategory').on('click', function() {
		categoryTable.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	//添加栏目
	$('#new_cmsCategory').on('click', function() {
	    $(".modalFormTitle").text("添加栏目");
		$('#modal-table').modal({
			'show' : true
		});
	});
	//修改栏目
	$('#edit_cmsCategory').on('click', function() {
	    $(".modalFormTitle").text("修改栏目");
        var id = 0, checked = $('table tr > td input:checked');
        if (checked.length) {
            id = checked.first().val();
            $("#cmsCategory_form").xform("load", (config.urlMap.show + id));
            $('#modal-table').modal('show');
        } else {
            bootbox.alert("请选择数据！");
        }
	});
	//删除栏目
	$('#delete_cmsCategory').on('click', function() {
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
                $("#cmsCategory_form").xform("post", config.urlMap.delet+"?ids="+ids,
                		function(data){
                		if(Number(data.status)===1){
                			categoryTable.fnDraw();
                			bootbox.alert("删除成功！");
                		}else{
                			bootbox.alert(data.messages[0]);
                		}
                });
            }
        });
	});
	//保存栏目
	$("#save_cmsCategory").on('click', function() {
		if (categoryForm.check(false)) {
			$('#cmsCategory_form').submit();
			categoryTable.fnDraw();
		}
	});
    //datatable单选
    $(".table").delegate("input[type=checkbox]","click",function(e){
        $(this).parents('.table').find('input[type=checkbox]').not($(this)).removeAttr('checked');
    });
});
// 设置栏目
$("#form-field-parentId").setCategory(config.urlMap.tree);
