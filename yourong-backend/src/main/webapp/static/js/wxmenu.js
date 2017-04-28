roleTreeInit();
var zSetting = {
			view: {
				dblClickExpand: false
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				onRightClick: OnRightClick
			}
		};

$('#getTree').on('click', function() {
	roleTreeInit();
});
$("#type").change(function(){
	var type = $(this).val();
	if(type=="view"){
		$('#key').hide();
		$('#url').show();
		$('#weixin_type').show();
	}else{
		$('#url').hide();
		$('#key').show();
		$('#weixin_type').show();
	}
});
function OnRightClick(event, treeId, treeNode) {
	if (!treeNode && event.target.tagName.toLowerCase() != "button" && $(event.target).parents("a").length == 0) {
		zTree.cancelSelectedNode();
		showRMenu("root", event.clientX, event.clientY);
	} else if (treeNode && !treeNode.noR) {
		zTree.selectNode(treeNode);
		showRMenu("node", event.clientX, event.clientY);
	}
}
function showRMenu(type, x, y) {
	$("#rMenu ul").show();
	if (type=="root") {
		$("#m_del").hide();
		$("#m_check").hide();
		$("#m_unCheck").hide();
	} else {
		$("#m_del").show();
		$("#m_check").show();
		$("#m_unCheck").show();
	}
	rMenu.css({"top":y+"px", "left":x+"px", "visibility":"visible"});

	$("body").bind("mousedown", onBodyMouseDown);
}
function hideRMenu() {
	if (rMenu) rMenu.css({"visibility": "hidden"});
	$("body").unbind("mousedown", onBodyMouseDown);
}
function onBodyMouseDown(event){
	if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length>0)) {
		rMenu.css({"visibility" : "hidden"});
	}
}



var zTree,rMenu;
rMenu = $("#rMenu");
function roleTreeInit() {
	$.ajax({
		url : config.urlMap.ajaxTree,
		type : "GET",
		success : function(data) {
			if(data.length>0){
				$("#getTree").hide();
				$("#publish").show();
			}else{
				$("#getTree").show();
				$("#publish").hide();
			}
			var zNodes = new Array();
			$(data).each(function(index) {
				var menu = new Object();
				if (data[index].pid == 0) {
					menu.open = true;
				}
				menu.id = data[index].id;
				menu.pId = data[index].pid;
				menu.name = data[index].name;
				menu.status=data[index].status;
				zNodes[index] = menu;
			});				
			console.log(zNodes);
			zTree=$.fn.zTree.init($("#roleTree"), zSetting, zNodes); 
		
		},
		error : function(e) {
			bootbox.alert("出错了！");
		}
	});
}
    //增加
	$('#wx_add').on('click', function() {
		$(".modalFormTitle").text("增加菜单");
		var id=zTree.getSelectedNodes()[0].id;
		var wxForm = $('#weixin_form').Validform({
			tiptype: 4,
			btnReset: ".btnReset",
			ajaxPost: false
		});
		$("#status").val("2");
		$("#id").val(id);
		var type = $("#type").find("option:selected").val();
		if(type=="view"){
    		$('#key').hide();
    		$('#url').show();
    		$('#weixin_type').show();
    	}else if(type=="click"){
    		$('#url').hide();
    		$('#key').show();
    		$('#weixin_type').show();
    	}else{
    		$('#url').hide();
    		$('#key').hide();
    		$('#weixin_type').hide();
    	}
		$('#modal-table').modal({
			'show' : true
		});
	});   
	//编辑
	$('#wx_edit').on('click', function() {
		var id=zTree.getSelectedNodes()[0].id;
		$(".modalFormTitle").text("编辑菜单");
            $('#modal-table').modal('show');
            $('#weixin_form').xform("load", config.urlMap.show + id, function(data) {
            	$("#type").find("option[value='"+data.type+"']").attr("selected",true);
            	if(data.type=="view"){
            		$('#key').hide();
            		$('#sort').show();
            		$('#url').show();
            		$('#weixin_type').show();
            	}else if(data.type=="click"){
            		$('#url').hide();
            		$('#key').show();
            		$('#sort').show();
            		$('#weixin_type').show();
            	}else{
            		$('#url').hide();
            		$('#key').hide();
            		$('#sort').hide();
            		$('#weixin_type').hide();
            	}
    		});
	});
	var weixinForm = $('#weixin_form').Validform({
		tiptype: 4,
		btnReset: ".btnReset",
		ajaxPost: false
	});
	//保存
	$("#save_weixin").on('click', function() {
		var id=zTree.getSelectedNodes()[0].id;
		if (weixinForm.check(false)) {
			$('#weixin_form').xform('post', config.urlMap.save, function(data) {
				if(data.success){
					bootbox.alert("保存成功！");
					roleTreeInit();
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("保存失败！");
					}
				}
			});
		}
	});
	//删除 delet
	$("#wx_del").on("click", function() {
		var id=zTree.getSelectedNodes()[0].id;
		var status=zTree.getSelectedNodes()[0].status;
		if(status!="undefined"&&status=="2"){
			bootbox.alert("父节点不能删除！");
			return;
		}
		bootbox.confirm("你确定要删除吗?", function(result) {
			if (!result) return;
			$.post(config.urlMap.del + id, function(data) {
				if (data==1) {
					bootbox.alert("删除成功！", function() {
						roleTreeInit();
					});
				} else {
					bootbox.alert("删除失败！");
				}
			});
		});
	});
	 //发布
	$('#publish').on('click', function() {
		$.ajax({
			url : config.urlMap.ajaxPublish,
			type : "GET",
			success : function(data) {
				if(data.success){
					bootbox.alert("发布成功！");
				}else{
					if(typeof data.resultCodeEum!="undefined" && data.resultCodeEum!=null){
						bootbox.alert(data.resultCodeEum[0].msg);
					}else{
						bootbox.alert("发布失败！");
					}
				}
			
			},
			error : function(e) {
				bootbox.alert("出错了！");
			}
		});
});   


function pulish(){
	$.ajax({
		url : config.urlMap.ajaxPublish,
		type : "GET",
		success : function(data) {
		
		},
		error : function(e) {
			bootbox.alert("出错了！");
		}
	});
}
//get changed roleId
$("#getCheckedNodes").click(function () {
    var zTree = $.fn.zTree.getZTreeObj("roleTree");
    var checkedObj = zTree.getCheckedNodes(true);   
    var v = [];  
    for (var i = 0; i < checkedObj.length; i++) {		
		v.push(checkedObj[i].id);

	}
    
    
    var id = $(this).attr("data-value");
    var data = "id="+id+"&menus="+v;    
    $.ajax({
        type: "post",
        url: config.urlMap.updateRoleTree,
        dataType: "json",
        data:data,
        success: function (data) {
        	  	     bootbox.alert("保存成功");
                },
        error: function (e) {
        	  bootbox.alert("出错了！");
        }
     });
        
    
    console.log(data);
});

var newCount = 1;
function addHove1rDom(treeId, treeNode) {
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		+ "' title='add_node' onfocus='this.blur();'></span>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.tId);
	if (btn) btn.bind("click", function(){
		var zTree = $.fn.zTree.getZTreeObj("treeDemo");
		zTree.addNodes(treeNode, {id:(100 + newCount), pId:treeNode.id, name:"new node" + (newCount++)});
		return false;
	});
};
function addHoverDom1(treeId, treeNode) {
	level=treeNode.level;
	id=treeNode.id;
	tem_id=treeNode.pId;
	level_id=level+id;
	var sObj = $("#" + treeNode.tId + "_span");
	if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
	var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
		+ "' title='add node' onfocus='this.blur();'></span>";
	var editStr = "<button type='button' class='button add' id='addBtn_" +treeNode.tId+ "' title='"+treeNode.name+"' onfocus='this.blur();'></button>";
	sObj.after(addStr);
	var btn = $("#addBtn_"+treeNode.tId);
	if (btn){
		btn.bind("click", function(){
			$(".modalFormTitle").text("添加微信菜单");
			$('#modal-table').modal({
				'show' : true
			});
		});
	} 
};