var feedbackTable;


$(function(){
	feedbackListDataTable();		
})


$('#query_feedback').on('click', function() {
	feedbackTable.fnDraw();
})

function feedbackListDataTable(){
	feedbackTable = $('#feedback-table').dataTable({
		'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
        	getSearchValue("#feedbackSearchForm",aoData);
        },
        'fnInitComplete':function(){
		},
        'sAjaxSource' : config.urlMap.ajax,
        'aoColumns' : [ 
        {
            'mDataProp' : 'id',
            'bSortable' : false,
            'mRender' : function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        }, {
            'mDataProp' : 'id',
            'bSortable' : true
        },{
            'mDataProp' : 'type',
            'bSortable' : true,
			'mRender'   : function(data, type, row) {
				return getDictLabel(config.feedbackType, row.type);
			}
        },{
			'mDataProp' : 'content',
			'bSortable' : false
		},{
			'mDataProp' : 'source',
			'bSortable' : true,
			'mRender'   : function(source, type, row) {
            	if(source == 1){
            		return "Android";
            	}else if(source == 2){
            		return "IOS";
            	}else if(source == 0){
            		return "PC";
            	}
            	return "--";
            }
		}, {
            'mDataProp' : 'trueName',
            'bSortable' : false
        },{
			'mDataProp' : 'sex',
			'bSortable' : false,
			'mRender'   : function(sex, type, row) {
            	if(sex == 0){
            		return "女";
            	}else if(sex == 1){
            		return "男";
            	}
            	return "未知";
            }
		}, {
            'mDataProp' : 'mobile',
            'bSortable' : false
        },{
			'mDataProp' : 'createTime',
			'bSortable' : true,
			'mRender'   : function(data, type, row) {
            	return formatDate(data,'yyyy-mm-dd HH:mm');
            }
		}, {
            'mDataProp' : 'replyStatus',
            'bSortable' : false,
			'mRender'   : function(replyStatus, type, row) {
            	if(replyStatus == 0){
            		return "未处理";
            	}else if(replyStatus == 1){
            		return "已处理";
            	}else if(replyStatus == 2){
            		return "已关闭";
            	}
            	return "--";
            }
        },{
			'mDataProp' : 'id',
			'bSortable' : false,
			'mRender' : function(data, type, row) {
				return  action(row);
			}
		}]
		
	});//dataTable
}

/**
 * 操作按钮
 * @param 
 * @returns {String}
 */
function action(row){
	var replyStatus = row.replyStatus;
	var reply = "",review="";//"+permission-config.permission.find+"
	reply = "<button class='btn btn-primary reply_feedback  permission-"+config.permission.replyFeedback+"' data-value='"+row.id+"')>回复</button>";
	review = "<button class='btn btn-primary review_feedback  permission-"+config.permission.replyFeedback+"' data-value='"+row.id+"')>查看回复</button>";
	if(replyStatus == 0){//未处理
		return reply;
	}else if(replyStatus == 1){//已处理
		return review;
	}else{
		return"";
	}
}


//查看回复
$("#feedback-table").on("click", '.review_feedback', function() {
	var id = $(this).attr("data-value");
	
	$("#feedback_form").xform("load", config.urlMap.queryFeedback+"?id="+id,function(data){
		$("#form-field-reply").val(data.reply);
		
	});
	$("#feedback_button").css("display","none");
	$('#modal-table-reply').modal('show');
});

//回复
$("#feedback-table").on("click", '.reply_feedback', function() {
	
	var id = $(this).attr("data-value");
	$("#feed_back_id").val(id);
	$("#form-field-reply").val("");
	$("#feedback_button").css("display","block");
	$('#modal-table-reply').modal('show');
	
});

$("#btn_ca_reply").on("click", function() {
	
	var id=$("#feed_back_id").val();
	
	var reply = $("#form-field-reply").val()
	if(!reply){
		bootbox.alert("回复内容不能为空");
		return;
	}
	
	$("#feedback_form").xform("post", config.urlMap.replyFeedback,function(data){
		if (data.success) {
			bootbox.alert("回复保存成功");
		}else{
			bootbox.alert("回复保存失败");
		}
		$('#modal-table-reply').modal('hide');
		feedbackTable.fnDraw();
	});
	
	
});


