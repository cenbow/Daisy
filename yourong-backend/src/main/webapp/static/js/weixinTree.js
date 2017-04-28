 var form;
 var menudata = [];
 var tree;
 var data = [];
 		$(function (){
                $("#layout1").ligerLayout({ leftWidth: 200, height: 100 });
	            //liger.get('pid').setDisabled();
	            //liger.get('id').setDisabled();
	            //liger.get('key').setDisabled();
 		});

	function init(){
		$.ajax({
			url:config.urlMap.getWeixin,
			success:function(data){
				if(data!=null){
					menudata.push(data);
					tree = $("#treeWeixin").ligerTree({
						data:data,
					    idFieldName :'id',
					    parentIDFieldName :'pid',
					    onClick: onClick
					    
						 
					});
				}
				
			}
		});
	
	}
		$("#form").ligerForm({
			space: 40,
			validate : true,
		    fields: [
		        { type: 'hidden', label: '父级节点', name: 'pid'},
		        { type: 'hidden', label: '节点id', name: 'id'},
		        { type: 'text', label: '菜单名称', name: 'name',width:'200',height:30,newline: true},
		        { type: 'textarea', label: '链接', name: 'url',width:'400' ,newline: true},
		        { type: 'text', label: 'key', name: 'key',width:'400',newline: true },
		        { type: 'hidden', label: '类型', name: 'type',width:'400' }
		    ], buttons: [
		        { text: '保存', width: 70,height:20, click: f_save }
		    ]
		});

 
		$("#treeWeixin").ligerTree({  
             url:config.urlMap.getWeixin,
             idFieldName :'id',
             parentIDFieldName :'pid',
             onClick: onClick
             }
        );
		
          function onClick(note){
                 var id=note.data.id;
                 if(note.data.type="view"){
                	  liger.get("form").setData({
         					name: "",
         					//url: "",
         					key: "",
         					//type: "",
         					pid:"",
         					id:""
                        }); 
                 }
                 var type="";
                 liger.get("form").setData({
   					name: "",
   					//url: "",
   					//key: "",
   					//type: "",
   					pid:"",
   					id:""
                  });
                 if(note.data.pid=="WDZH"){
                	 type=note.data.type;
                 }else{
                	 type="view";
                 }
                 liger.get("form").setData({
  					name: note.data.text,
  					url: note.data.url,
  					key: note.data.key,
  					type: type,
  					pid:note.data.pid,
  					id:note.data.id
                 });
                 if(note.data.pid=="PID"){
                	 $("#pid").hide();
                 }
                
             }
             
             
             var menu;
             var actionNodeID;
             var pId;
             var kId;
             var key;
             var type;
              function itemclick(item, i){
            	  var type="";
            	  if(pId=="WDZH"){
                  	 type="click";
                   }else{
                  	 type="view";
                   }
            	  if(pId=="PID"){
            		  pId=kId;
            	  }
            	 
            	  //alert(pId+"=="+kId);
            		 liger.get("form").setData({
      					pid: pId,
      					id:"",
      					name: "",
      					url:"",
      					key:key,
      					type:type
      					
                     });
             }
              function del(item, i){
            	  if(pId=="PID"){
            		  $.ligerDialog.alert('父节点不能删除', '提示', "error");
            		  return false;
            	  }
            	  jQuery.ligerDialog.confirm('确定删除吗?', function (confirm) {
                      if (confirm)
                    	  $.ajax({
                    		  type: 'POST',
                    		  url: config.urlMap.del,
                    		  dataType: "json",
              	    		  data:{
              	    			id: kId
                              },
                    		  success : function(data) {
                    			  if(data!=null){
                    				  var manager = $("#treeWeixin").ligerGetTreeManager();
                        			  manager.reload(); 
                        			  $.ligerDialog.success('删除成功');
                    			  }else{
                    				  $.ligerDialog.error('删除失败');
                    			  }
                    			
                    		  }
                    		});
                  });
            	 
              }
              
             $(function (){
                 menu = $.ligerMenu({ top: 100, left: 100, width: 120, items:
                 [
                 { text: '增加', click: itemclick, icon: 'add' },
                 { line: true },
                 { text: '删除', click: del, icon:'del' }
                 ]
                 });
       
                 $("#treeWeixin").ligerTree({ 
                	 onContextmenu: function (node, e){ 
                     actionNodeID = node.data.text;
                     pId=node.data.pid;
                     kId=node.data.id;
                     key=node.data.key;
                     type=node.data.type;
                     menu.show({ top: e.pageY, left: e.pageX });
                     return false;
                	 }
                 });
             });
             
             function f_save(){
             	form = liger.get("form");
             	var manager = $("#treeWeixin").ligerGetTreeManager();
             	var data = form.getData();
             	if(data.name==""){
             		$.ligerDialog.warn('菜单名称不能为空');
             		return;
             		
             	}
             	$.ajax({
             		  type: 'POST',
             		  url: config.urlMap.saveWeixin,
             		  data: data,
             		  success : function(data) {
             			  if(data!=null){
             				  $.ligerDialog.success('保存成功');
             				  manager.reload();
             			  }else{
             				  $.ligerDialog.error('保存失败');
             			  }
             		  }
             		});
                     
             }
            
            $("#Button1").bind('click', function (){
            	form = liger.get("form");
            	var manager = $("#treeWeixin").ligerGetTreeManager();
            	var data = form.getData();
            	if(data.name==""){
            		$.ligerDialog.warn('菜单名称不能为空');
            		return;
            		
            	}
            	$.ajax({
            		  type: 'POST',
            		  url: config.urlMap.saveWeixin,
            		  data: data,
            		  success : function(data) {
            			  if(data!=null){
            				  $.ligerDialog.success('保存成功');
            				  manager.reload();
            			  }else{
            				  $.ligerDialog.error('保存失败');
            			  }
            		  }
            		});
                    });
           
           $("#push").bind('click', function (){
           	var manager = $("#treeWeixin").ligerGetTreeManager();
           	var data=manager.getData();
           	var jsonStr=liger.toJSON(data);
           	$.ajax({
           		  type: 'POST',
           		  dataType: "json",
	    		  data:{
	    			  jsonStr: jsonStr
                  },
           		  url: config.urlMap.push,
           		  success : function(data) {
           			if(data.success){
           			 $.ligerDialog.success('发布成功');
           			}else{
           				$.ligerDialog.error('发布失败');
           			}
           		  }
           		});
            });
           
           
           function getTypeData() {
               return [
               { Name: '事件处理', Code: 'click' },
               { Name: '页面跳转', Code: 'view' }
               ];
           }