jQuery(function($) {
	//表单验证初始化
    var categoryForm = $("#cmsIcon_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
    });
	var iconTable = $('#cmsIcon-table-2').dataTable({
		'bFilter' : false,
		'bProcessing' : true,
		//'bSort' : true,
		'bServerSide' : true,
		'fnServerParams' : function(aoData) {
			getAllSearchValue(aoData);
		},
		'fnRowCallback':function(nRow, aData,iDataIndex){
        	var startIndex = iDataIndex;
			if(typeof iconTable !== "undefined"){
				var dts = iconTable.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
		'sAjaxSource' : config.urlMap.ajax,
		'aoColumns' : [ {
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return "<input type='checkbox' value=" + row.id + " iname='iconId' ><input type='hidden' value=" + row.weight + " iname='weight' >";
			}
		}, {
			'mDataProp': 'id',
			'mSortable':false
		},{
			'mDataProp' : 'name',
			'bSortable' : false
		}, {
			'mDataProp' : 'href',
			'bSortable' : false
		}, {
			'mDataProp' : 'remark',
			'bSortable' : false
		}, {
			'mDataProp': 'iconStatus',
			'bSortable': false,
			'mRender':function(data,type,row){
				if(row.iconStatus == 0) {
					return "待生效";
				}
				if(row.iconStatus == 1) {
					return "已生效";
				}
				if(row.iconStatus == -1) {
					return "已失效";
				}
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
		iconTable.fnDraw({
			"fnServerParams" : function(aoData) {
				getAllSearchValue(aoData);
			}
		});
	});
	//添加Icon
	$('#new_cmsIcon').on('click', function() {
	    $(".modalFormTitle").text("添加Icon");
	    $(".j-img-box").css("display","none");
		$(".j-imgBg-box").css("display","none");
	    $("#form-field-image").attr("datatype","*");
	    categoryForm.resetForm();
		$('#modal-table').modal({
			'show' : true
		});
	});
	//修改Icon
	$('#edit_cmsIcon').on('click', function() {
	    $(".modalFormTitle").text("修改Icon");
        var id = 0, checked = $('table tr > td input:checked');
        if (checked.length) {
            id = checked.first().val();
            $("#cmsIcon_form").xform("load", (config.urlMap.show + id),function(data){
            	$(".j-img-box").css("display","block");
            	if(data.image!=null){
            		$(".j-imgBg-box").css("display","block");
            		$("img[name='image']").attr("src","https://oss-cn-hangzhou.aliyuncs.com"+data.image);
    			}else{
    				$(".j-imgBg-box").css("display","none");
    			}
            });
            $("#form-field-image").removeAttr("datatype");
            $('#modal-table').modal('show');
        } else {
            bootbox.alert("请选择数据！");
        }
	});
	//删除栏目
	$('#delete_cmsIcon').on('click', function() {
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
                $("#cmsIcon_form").xform("post", config.urlMap.delet+"?ids="+ids,
                		function(data){
                		if(data.success==true){
                			iconTable.fnDraw();
                			bootbox.alert("删除成功！");
                		}else{
                			bootbox.alert("删除失败！");
                		}
                });
            }
        });
	});
	//保存Icon
	$("#save_cmsIcon").on('click', function() {
		if (categoryForm.check(false)) {
			$('#cmsIcon_form').submit();
			iconTable.fnDraw();
		}
	});
    //datatable单选
    $(".table").delegate("input[type=checkbox]","click",function(e){
        $(this).parents('.table').find('input[type=checkbox]').not($(this)).removeAttr('checked');
    });
    
    //
    $("#cmsIcon-table-2 tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var position = $("#cmsIcon-table-2 tbody tr").index(u.item);
			var iconId = ($(u.item).find("[iname='iconId']").val());
			updateIconWeight(iconId, position);
			console.info(position);
		}
	}).disableSelection();
    
    
    /**
	 * 更新公告权重
	 * @param projectID
	 * @param weight
	 */
	function updateIconWeight(iconId, position){
		$.post(config.urlMap.update,{iconId:iconId, position:position},function(data){
			iconTable.fnDraw();
		})
	}
    
});

