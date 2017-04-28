jQuery(function($) {
    /* form handler */
     //表单验证初始化
     var userForm = $("#sysUser_form").Validform({
         tiptype : 4,
         btnReset : ".btnReset",
         ajaxPost : true
     });
    // dateTable init
    var userTable = $('#sysUser-table-2').dataTable({
        'bFilter' : false,
        'bProcessing' : true,
        'bSort' : true,
        'aaSorting':[[1,"desc"]],
        'bServerSide' : true,
        'fnServerParams' : function(aoData) {
            getAllSearchValue(aoData);
        },
        "initComplete": function () {
        	initCallBack();
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
            'mDataProp' : 'id'
        }, {
            'mDataProp' : 'loginName',
            'bSortable' : true
        }, {
            'mDataProp' : 'name',
            'bSortable' : true
        }, {
            'mDataProp' : 'no',
            'bSortable' : true
        }, {
            'mDataProp' : 'email',
            'bSortable' : true
        }, {
            'mDataProp' : 'mobile',
            'bSortable' : true
        }, {
            'mDataProp' : 'status',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.userStatus, row.status)
            }
        }, {
            'mDataProp' : 'userType',
            'bSortable' : true,
            'mRender'   : function(data, type, row) {
            	return getDictLabel(config.userType, row.userType);
            }
        }, {
            'mDataProp' : 'loginIp',
            'bSortable' : true
        }, {
            'mDataProp' : 'loginTime',
            'bSortable' : true,
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        },{
            'mDataProp' : 'createTime',
            'bSortable' : true,
            'mRender': function(data, type, row) {
            	if(data == null) {
            		return '';
            	}
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
    $('#query_sysUser').on('click', function() {
        userTable.fnDraw();
    });
    // 添加用户
    $('#new_sysUser').on('click', function() {
    	$(".modalFormTitle").text("添加用户");
        $("#j-newpassword").hide();
        $('#modal-table').modal({
            'show' : true
        });
    });
    // 编辑用户
    $('#edit_sysUser').on('click', function() {
    	$(".modalFormTitle").text("编辑用户");
        var id = 0, checked = $('table tr > td input:checked');
        if (checked.length) {
            id = checked.first().val();
            $("#j-old-password").hide();
            $("#sysUser_form").find("input[type='checkbox']").attr("checked",false);
            $("#sysUser_form").xform("load", (config.urlMap.show + id));
            $('#modal-table').modal('show');
        } else {
            bootbox.alert("请选择数据！");
        }
    });
    // 删除用户
    $('#delete_sysUser').on('click', function() {
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
               // ids=ids.join(",");
                $("#sysUser_form").xform("post", config.urlMap.delet+"?ids="+ids);
                userTable.fnDraw();
            }
        });
    });
    //保存用户
    $("#save_sysUser").on('click', function() {
        //if (userForm.check(false)) {暂时不验证
            $('#sysUser_form').xform("post", config.urlMap.save);
            userTable.fnDraw();
        //}
    });
});
