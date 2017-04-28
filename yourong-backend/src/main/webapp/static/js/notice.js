var projectNoticeTable;
jQuery(function($){
	
	noticeDataTable();
	
	$("#notice-table tbody").sortable({
		helper: fixHelper,  
		cursor:'move', 
		opacity:'0.6', 
		revert:true,
		update:function(e,u){
			var sort = ($("#notice-table tbody tr").index(u.item)+1);
			var id = ($(u.item).find(".projectNoticeIdValue").val());
			updateSort(id, sort);
		}
	}).disableSelection();
	
	autoLoadProject();
	
})//jQuery


/**
 * 列表数据
 */
function noticeDataTable(){
	projectNoticeTable = $('#notice-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : false,
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
        	getSearchValue("#noticeSearchForm",aoData);
        },
        'fnInitComplete':function(){
		},
		'fnRowCallback':function(nRow, aData,iDataIndex){
			var startIndex = iDataIndex;
			if(typeof projectNoticeTable !== "undefined"){
				var dts = projectNoticeTable.dataTableSettings[0];
				startIndex = dts._iDisplayStart+iDataIndex;
			}
        	$('td:eq(1)', nRow).html(startIndex+1);
        },
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'id',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + " class='projectNoticeIdValue'>";
            }
        }, {
            'mDataProp' : 'id',
            'bSortable' : false
        }, {
            'mDataProp' : 'projectName',
            'bSortable' : false
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
        }/*, {
            'mDataProp' : 'sort',
            'bSortable' : true
        }*/, {
            'mDataProp' : 'status',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.noticeStatus, row.status)
            }
        }, {
            'mDataProp' : 'createTime',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
        },{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return getProjectNoticeButton(row);
			}
		}]
		
	});//dataTable
}

/**
 * 查询
 */
$('#query_notice').on('click', function() {
	projectNoticeTable.fnDraw();
});

/**
 * 打开新增预告窗口
 */
$('#new_notice').on('click', function() {
	$('#modal-table').modal({
		'show' : true
	});
	$("#projectName").removeAttr("disabled");
});

/**
 * 添加预告
 */
$('#save_notice').on('click', function() {
	var projectId = $("#projectId").val();
	var projectName = $("#projectName").val();
	var startTime = $("#startTime").val();
	if(projectId == "" || projectName == ""){
		 bootbox.alert("请选择有效的项目，备注（必须通过下拉选择，而不是直接粘贴）");
		 return;
	}
	if(startTime == ""){
		bootbox.alert("项目预告开始时间不能为空！");
		 return;
	}

	 $('#newNoticeForm').xform("post", config.urlMap.save,function(data){
		 if(data.success){
			 bootbox.alert("操作成功",function(){
				 projectNoticeTable.fnDraw();
			  });
			}else{
				if(!!data.resultCodeEum){
					return bootbox.alert(data.resultCodeEum[0].msg,function(){
						return;
					});
				}else{
					return bootbox.alert("添加/修改预告失败，预告结束时间必须跟项目上线时间一致且项目状态必须是待发布。");
				}
			}
	 });
	 
});

/**
 * 操作按钮
 */
function getProjectNoticeButton(notice){
	var btn="", find="", del="", edit="",start="",stop="", recommend="";
	find = "<button class='btn btn-primary permission-"+config.permission.find+"' onclick=showNotice('"+notice.id+"','"+encodeURIComponent(notice.projectName)+"')>查看</button>";
	del = "<button class='btn btn-inverse  permission-"+config.permission.delet+"' onclick=deleteNotice('"+notice.id+"')>删除</button>";//删除
	/***
	stop ="<button class='btn  btn-warning permission-"+config.permission.stop+"' onclick=stopNotice('"+notice.id+"')>暂停</button>";//已暂停
	start = "<button class='btn btn-success permission-"+config.permission.start+"' onclick=startNotice('"+notice.id+"')>恢复</button>";//恢复
	if(notice.indexShow > 0){
		recommend = "<button class='btn btn-purple  permission-"+config.permission.recommend+"' onclick=cancelRecommendNotice('"+notice.id+"','"+encodeURIComponent(notice.projectName)+"')>取消推荐</button>";
	}else{
		recommend = "<button class='btn btn-purple  permission-"+config.permission.recommend+"' onclick=recommendNotice('"+notice.id+"','"+encodeURIComponent(notice.projectName)+"')>推荐首页</button>";
	}****/
	btn = find+ del;
	return btn;
}

/**
 * 暂停预告
 */
function stopNotice(id){
	bootbox.confirm("确定要暂停项目预告："+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.stop,{id:id},function(data){
				if(data.success){
					bootbox.alert("成功暂停项目预告",function(){
						projectNoticeTable.fnDraw();
					})
				}else{
					bootbox.alert("项目暂停暂停失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 恢复预告
 */
function startNotice(id){
	bootbox.confirm("确定要恢复项目预告："+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.start,{id:id},function(data){
				if(data.success){
					bootbox.alert("成功恢复项目预告",function(){
						projectNoticeTable.fnDraw();
					})
				}else{
					bootbox.alert("项目预告恢复失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 删除预告
 */
function deleteNotice(id){
	bootbox.confirm("确定要删除项目预告："+id+"吗？",function(result){
		if(result){
			$.post(config.urlMap.delet,{id:id},function(data){
				if(data.success){
					bootbox.alert("成功删除项目预告",function(){
						projectNoticeTable.fnDraw();
					})
				}else{
					bootbox.alert("项目预告删除失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 查看预告
 * @param id
 * @param name
 */
function showNotice(id, name){
	$(".modalFormTitle").text("查看"+decodeURIComponent(name)+"预告");
    $("#newNoticeForm").xform("load", (config.urlMap.show + id));
    $('#modal-table').modal('show');
    $("#projectName").attr("disabled","disabled");
}

function recommendNotice(id,name){
	bootbox.confirm("确定要推荐"+decodeURIComponent(name)+"吗？",function(result){
		if(result){
			$.post(config.urlMap.recommend,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						projectNoticeTable.fnDraw();
					})
				}else{
					bootbox.alert("项目预告推荐失败，请稍后重试！");
				}
			})
		}
	})
}

function cancelRecommendNotice(id,name){
	bootbox.confirm("确定要取消推荐："+decodeURIComponent(name)+"吗？",function(result){
		if(result){
			$.post(config.urlMap.cancelRecommend,{id:id},function(data){
				if(data.success){
					bootbox.alert("操作成功",function(){
						projectNoticeTable.fnDraw();
					})
				}else{
					bootbox.alert("取消推荐失败，请稍后重试！");
				}
			})
		}
	})
}

/**
 * 更新排序
 * @param id
 * @param sort
 */
function updateSort(id, sort){
	$.post(config.urlMap.updateSort,{id:id, sort:sort},function(data){
		projectNoticeTable.fnDraw();
	})
}


function autoLoadProject(){
   $("#projectName").autocomplete({  
	    minLength: 1,
	    source:function(request,response) {
	    	$.ajax({
	    		url: config.urlMap.queryProjectFromNotice,
	    		dataType: "json",
	    		data:{
	    			projectName: request.term
                },
	    		success: function(data) {
	    			 response($.map(data, function(item) {
	    		         return {
	    		        	value: item.name,
	    		        	projectId: item.id,
	    		        	onlineTime: item.onlineTime
	    		         }
	    		        })
	    		     );
	    		}
	    	});
	    },
	    select: function( event, ui ) {
	    	$("#projectId").val(ui.item.projectId);
	    	$("#endTime").val(formatDate(Number(ui.item.onlineTime),"yyyy-mm-dd HH:mm"));
	    },
   }) .autocomplete( "instance" )._renderItem = function( ul, item) {
	   return  $( "<li>" ).append("<span style='color:#1963aa;'>"+item.value+"</span>").appendTo( ul );
   };
}










