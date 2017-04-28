var projectRecommendTable, projectAppRecommendTable;

jQuery(function($){
	
	recommendDataTable();
	
	recommendAppDataTable();
	
	$("#pc-recommend-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var weight = ($("#pc-recommend-table tbody tr").index(u.item)+1);
			var projectId = ($(u.item).find(".projectIdValue").val());
			updateRecommendWeight(projectId, weight ,1);
		}
	}).disableSelection();
	
	$("#app-recommend-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var weight = ($("#app-recommend-table tbody tr").index(u.item)+1);
			var projectId = ($(u.item).find(".projectIdValue").val());
			updateRecommendWeight(projectId, weight,2);
		}
	}).disableSelection();
});


function recommendDataTable(){
	projectRecommendTable = $('#pc-recommend-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
        	aoData.push({
                "name": "search_recommendType",
                "value": 1
            });
        	getAllSearchValue(aoData);
        },
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	$('td:eq(1)', nRow).html(iDataIndex+1);
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'id',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + " class='projectIdValue'>";
            }
        }, {
            'mDataProp' : 'recommendWeight',
            'bSortable' : false
        }, {
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'name',
            'bSortable' : false
        },{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  recommendAction(row, 1);
			}
		}]
		
	});//dataTable
}

function recommendAppDataTable(){
	projectAppRecommendTable = $('#app-recommend-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
        	aoData.push({
                "name": "search_recommendType",
                "value": 2
            });
        	getAllSearchValue(aoData);
        },
        'fnRowCallback':function(nRow, aData,iDataIndex){
        	$('td:eq(1)', nRow).html(iDataIndex+1);
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'id',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + " class='projectIdValue'>";
            }
        }, {
            'mDataProp' : 'recommendWeight',
            'bSortable' : false
        }, {
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'name',
            'bSortable' : false
        },{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  recommendAction(row, 2);
			}
		}]
		
	});//dataTable
}


/**
 * 更新推荐权重
 * @param projectID
 * @param weight
 */
function updateRecommendWeight(projectId, weight,recommendType){
	$.post(config.urlMap.update,{projectId:projectId, weight:weight, recommendType:recommendType},function(data){
		if(recommendType ==1){
			projectRecommendTable.fnDraw();
		}else{
			projectAppRecommendTable.fnDraw();
		}
	})
}


/**
 * 打开新增预告窗口
 */
$('#new_recommend').on('click', function() {
	var _length = $("#recommend-table tbody tr").length;
//	if(_length < 8){
		$('#modal-table').modal({
			'show' : true
		});
//	}else{
//		bootbox.alert("只有8个推荐位哦。请先删除部分推荐再添加！");
//	}
	
});

/**
 * 添加项目推荐
 */
$('#save_recommend').on('click', function() {
	 $('#newRecommendForm').xform("post", config.urlMap.save,function(data){
		 if(data != null){
			 if(data.success){
				bootbox.alert("操作成功",function(){
					 projectRecommendTable.fnDraw();
					 console.log(projectAppRecommendTable);
					 if(projectAppRecommendTable !== undefined && projectAppRecommendTable!==null ){
						 projectAppRecommendTable.fnDraw();
					 }
				})
			}else{
				bootbox.alert("推荐添加失败，请稍后重试！");
			}
		 }
	 });
});

function recommendAction(obj, recommendType){
	return "<button class='btn btn-primary permission-"+config.permission.delet+"' onclick=delRecommend('"+obj.id+"','"+encodeURIComponent(obj.name)+"','"+recommendType+"')>取消推荐</button>";
}

/**
 * 取消推荐
 * @param id
 */
function delRecommend(id,name, recommendType){
	bootbox.confirm("确定要取消推荐："+decodeURIComponent(name)+"吗？",function(result){
		if(result){
			$.post(config.urlMap.delet,{id:id,recommendType:recommendType},function(data){
				if(data.success){
					bootbox.alert("成功删除推荐",function(){
						if(recommendType ==1){
							projectRecommendTable.fnDraw();
						}else{
							projectAppRecommendTable.fnDraw();
						}
					})
				}else{
					bootbox.alert("删除失败，请稍后重试！");
				}
			})
		}
	})
}
