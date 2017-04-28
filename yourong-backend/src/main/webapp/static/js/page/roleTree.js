//zTree config
var zSetting = {
		check : {
			enable : true,
			chkStyle : "checkbox",
			chkboxType : {
				"Y" : "ps",
				"N" : "ps"
			}
		},
    data : {
		simpleData : {
			enable : true
		}
	},
    view: {
        showIcon: false
    }
};
//点击编辑权限
function roleTreeInit(id) {
    $("#getCheckedNodes").addClass("block").removeClass("hidden").attr("data-value",id);   
    	$.ajax({
			url : config.urlMap.ajaxTree+id,
			type : "GET",
			success : function(data) {
				var zNodes = new Array();
				$(data).each(function(index) {
					var menu = new Object();
					if (data[index].parentId == 0) {
						menu.open = true;
					}
					menu.id = data[index].id;
					menu.pId = data[index].parentId;
					menu.name = data[index].name;
					// alert(data[index].checked);
//					if (data[index].type == '0') {
//						menu.chkDisabled = true;
//					}
					if (data[index].checked) {
						menu.checked = data[index].checked;
					} else {
						menu.checked = false;
					}
					zNodes[index] = menu;
				});				
				console.log(zNodes);
				$.fn.zTree.init($("#roleTree"), zSetting, zNodes); //getRoleData(url,oid);
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