jQuery(function($) {
	
	$('#questionCategory').hide();
	$('#questionType').change(function(){
		var questionType = $("#questionType option:selected").val();
		if(questionType == 0){
			$('#questionCategory').show();
		}else{
			$('#questionCategory').hide();
		}
	});
	

	/**
	 * 问题列表
	 */
	var questionTable = $('#question-table').dataTable({
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
			'mDataProp': 'questionOrder',
			'bSortable': false
		},{
			'mDataProp': 'terminal',
			'bSortable': false,
			'mRender': function(data, type, row) {
				switch (data) {
					case 0:
						return "PC";
						break;
					case 1:
						return "移动端";
						break;

				}
			}
		},{
			'mDataProp': 'labelName',
			'bSortable': false
		},{
			'mDataProp': 'content',
			'bSortable': false
		},{
			'mDataProp': 'createTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		},  {
			'mDataProp': 'updateTime',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return formatDate(data, "yyyy-mm-dd HH:mm:ss");
			}
		}, {
			'mDataProp': 'operation',
			'bSortable': false,
			'mRender': function(data, type, row) {
				return getAllOperation(row);
			}
		}]
	});
	
	
	//query
	$('#query_questionDetail').on('click', function() {
		
		questionTable.fnDraw();
	});
	
	// 添加问题
	$("#add_question").click(function(){
		$("#labelId").find("option").remove();
		$('#HelpCenterQuestionTitle').text("添加问题");
		$('#questionOrder').val("");
	    $('#id').val(null);
	    $('#terminal').val($("#terminalType").val());
	    var type = $("#questionType").val();
		if(type == 0){
			$("#addLabel-form").show();
			$("#isHot").val(0);
			$("#isHotShow").val("常见问题");
	    	var category = $("#questionCategory").val();
	    	$("#questionForm").xform("load", (config.urlMap.showLabel + category),function(data) {
	    		$.each(data,function(n,value) {   
	    			$("#labelId").append("<option value="+value.id+">"+value.labelName+"</option>"); 
		           	});   
	    	});
		}else if(type == 1){
			$("#isHot").val(1);
			$("#isHotShow").val("热门问题");
			$("#labelId").val(null);
			$("#addLabel-form").hide();
		}else{
			$("#isHot").val(2);
			$("#isHotShow").val("新手引导");
			$("#labelId").val(null);
			$("#addLabel-form").hide();
		}
	    $('#sort').val(0);
	    $('#content').val("");
	    $('#form-field-content').val("");
        if (typeof postEditor.id === 'undefined') {
        	loadUeditor();
        } else {
            postEditor.execCommand('cleardoc');
        };
	    $('#add-modal-table').modal('show');
	});

	
	// 获取操作按钮
	function getAllOperation(row) {
		var edit = "<button class='btn btn-xs btn-info HelpCenterQuestion-edit permission-" + config.permission.show + "'  data-id='" + row.id + "'><i class='icon-edit bigger-120'>编辑</i></button>"; // 编辑
		var del = "<button class='btn btn-xs btn-danger HelpCenterQuestion-del permission-" + config.permission.del + "' data-id='" + row.id + "'><i class='icon-trash bigger-120'>删除</i></button>";
		var btn='';
		btn = edit + del;
		return btn;
	}
	
	//删除 delete
	$("#question-table").on("click", '.HelpCenterQuestion-del', function() {
		var id = $(this).attr("data-id");
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.del + id, function(data) {
				if (data.success) {
					bootbox.alert("删除成功！", function() {
						questionTable.fnDraw();
					});
				} else {
					bootbox.alert("删除失败！");
				}
			});
		});
	});
	
	//编辑 edit
	$('#question-table').on("click", ".HelpCenterQuestion-edit", function() {
		var id = $(this).attr("data-id");
		$("#HelpCenterQuestionTitle").text("修改问题");
		var type = $("#questionType").val();
		if(type == 0){
			$("#addLabel-form").show();
			$("#isHotShow").val("常见问题");
	    	var category = $("#questionCategory").val();
	    	$("#questionForm").xform("load", (config.urlMap.showLabel + category),function(data) {
	    		$.each(data,function(n,value) {   
	    			$("#labelId").append("<option value="+value.id+">"+value.labelName+"</option>"); 
		           	});
	    	});
		}else{
			$("#isHotShow").val("热门问题");
			$("#labelId").val(null);
			$("#addLabel-form").hide();
		}
		$('#add-modal-table').modal("show");
		$("#questionForm").xform("load", (config.urlMap.show + id), function(data) {
			$("#content").val(data.content);
			//$("#form-field-content").val(data.answer);
			var answer = data.answer;
    		if (typeof postEditor.id === 'undefined') {                    
    			loadUeditor(answer);
    		}else{
    			postEditor.setContent(answer);
    		}
		});
	});

	// 添加问题
	$("#submit_question").click(function(){
		var contentVal = $.trim($('#content').val());
		if(contentVal == null || contentVal == ""){
			bootbox.alert("请输入问题!");
			return;
		}

		$("textarea[name='answer']").val(postEditor.getContent());
	    $('#questionForm').xform('post', config.urlMap.save, function(data) {
	        if (!data.success) {
	            bootbox.alert("保存问题失败!",function(){
	                $('#submit_question').removeAttr("disabled");
	            });
	        } else {
	            bootbox.alert("保存问题成功!", function() {
	                $('#add-modal-table').modal('hide');
	                $('#submit_question').removeAttr("disabled");
	                questionTable.fnDraw();
	            });
	        }
	    });
	});
	
	// 排序
	$("#question_sort").click(function(){
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
	                questionTable.fnDraw();
	                bootbox.alert("保存成功");
	            }else{
	                bootbox.alert("保存失败");
	            }
	        }
	    });
	    $(this).hide();
	    $('#question_sort').show();
	});
	
	//刷新到缓存
	$("#flush_question").click(function(){
		bootbox.confirm("你确定要更新吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.flush);
		});
	});
	
    //加载umeditor编辑器
    var postEditor = {};

    function loadUeditor(answer) {
        var url = config.urlMap.ueditor;
        if(typeof answer==='undefined'){
        	answer="";
        };
        $.getScript(url + 'ueditor.config.js');
        $.getScript(url + 'ueditor.all.min.js', function() {
            //实例化编辑器
            postEditor =UE.getEditor('form-field-content', {
                initialFrameWidth: 765,
                initialFrameHeight: 350,
                initialContent:answer
            });
        });
    }
    
    
});	
