jQuery(function($) {
    $('#sysArea_form').validate({
        rules: {},
        messages: {}
    });
    var oTable1 = $('#sysArea-table-2').dataTable({
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
        'aaSorting':[[1,"desc"]],
        'bServerSide': true,
        'fnServerParams': function(aoData) {
            getAllSearchValue(aoData);
        },
        'sAjaxSource': config.urlMap.ajax,
        'aoColumns': [{
            'mDataProp': 'id',
            'bSortable': false,
            'mRender': function(data, type, row) {
                return "<input type='checkbox' value=" + row.id + ">";
            }
        }, {
            'mDataProp': 'id'
        }, {
            'mDataProp': 'parentId',
            'bSortable': true
        }, {
            'mDataProp': 'code',
            'bSortable': true
        }, {
            'mDataProp': 'name',
            'bSortable': true
        }, {
            'mDataProp': 'type',
            'bSortable': true
        }, {
            'mDataProp': 'createTimeStr',
            'bSortable': true
        }, {
            'mDataProp': 'updateTimeStr',
            'bSortable': true
        }, {
            'mDataProp': 'remarks',
            'bSortable': true
        }, {
            'mDataProp': 'delFlag',
            'bSortable': true
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
    $('#query_sysArea').on('click', function() {
        oTable1.fnDraw();
    });
    $('#new_sysArea').on('click', function() {
        $(".modalFormTitle").text("添加地区");
        $('#modal-table').modal({
            'show': true
        });
    });
    $('#edit_sysArea').on('click', function() {
        $(".modalFormTitle").text("编辑地区");
        var id = $('table tr > td input:checked').first().val();
        $.post(config.urlMap.show + id, {}, function(data) {
            // console.log(data);
            $('#sysArea_form').form("load", data);
            $('#modal-table').modal('show');
        });
    });
    $('#delete_sysArea').on('click', function() {
        var ids = [];
        $('table tr > td input:checked').each(function() {
            ids.push($(this).val());
        });
        if (ids.length == 0) {
            bootbox.alert("请选择数据！");
            return;
        }
        // console.log(ids);
        bootbox.confirm("你确定要删除吗?", function(result) {
            if (result) {
                $.post(config.urlMap.delet, {
                    "id": ids
                }, function(data) {
                    console.log(data);
                    oTable1.fnDraw();
                });
            }
        });
    });
    function checkform(formData, jqForm, options) {
        return $("#sysArea_form").valid();
    }
    $("#save_sysArea").on('click', function() {
        var options = {
            beforeSubmit: checkform,
            url: config.urlMap.save,
            type: "post",
            resetForm: true,
            success: function(data) {
                $('#modal-table').modal('toggle');
                oTable1.fnDraw();
            }
        };
        $('#sysArea_form').ajaxSubmit(options);
    });
});
