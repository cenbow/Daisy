jQuery(function($) {
    //表单验证初始化
    var articleForm = $("#cmsArticle_form").Validform({
        tiptype : 4,
        btnReset : ".btnReset",
        ajaxPost : false
    });
    var oTable1 = $('#cmsArticle-table-2').dataTable({
        'order':[[0,"desc"]],
        'bFilter': false,
        'bProcessing': true,
        'bSort': true,
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
            'mDataProp': 'categoryId',
            'bSortable': true,
            'mRender': function(data, type, row) {
                return row.categoryName;
            }
        }, {
            'mDataProp': 'title',
            'bSortable': true

        }, {
            'mDataProp': 'weight',
            'bSortable': true
        }, {
            'mDataProp': 'weightTime',
            'bSortable': true,
            'mRender': function(data, type, row) {
                if(data!=null){
                    return formatDate(data);
                }else{
                    return data="";
                }
            }
        }, {
            'mDataProp': 'hits',
            'bSortable': false
        }, {
            'mDataProp': 'copyfrom',
            'bSortable': false
        }, {
            'mDataProp': 'relation',
            'bSortable': false
        }, {
            'mDataProp': 'allowComment',
            'bSortable': false,
            'mRender': function(data, type, row) {
                if (Number(data) === 0) {
                    return "否";
                } else {
                    return "是";
                }
            }
        }, {
            'mDataProp': 'createBy',
            'bSortable': false
        }, {
            'mDataProp': 'createTime',
            'bSortable': true,
            'mRender': function(data, type, row) {
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp': 'updateBy',
            'bSortable': false
        }, {
            'mDataProp': 'updateTime',
            'bSortable': true,
            'mRender': function(data, type, row) {
                return formatDate(data,"yyyy-mm-dd HH:mm:ss");
            }
        }, {
            'mDataProp': 'onlineTime',
            'bSortable': true,
            'mRender': function(data, type, row) {
                if(data!=null){
                    return formatDate(data,"yyyy-mm-dd HH:mm:ss");
                }
                return null;
            }
        }]
    });
    //表单选择
    $('table th input:checkbox').on('click',function() {
        var that = this;
        $(this).closest('table').find('tr > td:first-child input:checkbox').each(function() {
            this.checked = that.checked;
            $(this).closest('tr').toggleClass('selected');
        });
    });

    $('#query_cmsArticle').on('click', function() {
        oTable1.fnDraw({
            "fnServerParams": function(aoData) {
                getAllSearchValue(aoData);
            }
        });
        oTable1.fnDraw();
    });
    //加载umeditor编辑器
    var postEditor = {};

    function loadUeditor(content) {
        var url = config.urlMap.ueditor;
        if(typeof content==='undefined'){
            content="";
        };
        $.getScript(url + 'ueditor.config.js');
        $.getScript(url + 'ueditor.all.min.js', function() {
            //实例化编辑器
            postEditor =UE.getEditor('articleContent', {
                initialFrameWidth: 765,
                initialFrameHeight: 350,
                initialContent:content
            });
        });
    }
    //文章新增
    $('#new_cmsArticle').on('click', function() {
        $(".modalFormTitle").text("添加文章");
        if (typeof postEditor.id === 'undefined') {
            loadUeditor();
        } else {
            postEditor.execCommand('cleardoc');
        };
        $('#categoryId').val(1);
        $('#genre_group').show();
        $('#articlechosen_group').hide();
        deleteDropzoneAllimage('');
        $('#modal-table').modal({
            'show': true
        });

    });
    //文章编辑
    $('#edit_cmsArticle').on('click', function() {
        $(".modalFormTitle").text("修改文章");
        var id = 0,
            checked = $('table tr > td input:checked');
        if (checked.length>1) {
            bootbox.alert("请选择一条数据！");
            return;
        }
        if (checked.length) {
            id = checked.first().val();
            $("#cmsArticle_form").xform("load", (config.urlMap.show + id), function(data){
                if(data!=null && data.content!=null){
                    if(data.onlineTime!=null){
                        $("#onlineTime").val(formatDate(data.onlineTime, "yyyy-mm-dd HH:mm"));
                    }
                    if (data.categoryId==1){
                        $('#genre_group').show();
                    }else {
                        $('#genre_group').hide();
                    }

                    if (data.categoryId==2){
                        $('#articlechosen_group').show();
                    }else {
                        $('#articlechosen_group').hide();
                    }
                    var content = data.contentHtml;
                    if (typeof postEditor.id === 'undefined') {
                        loadUeditor(content);
                    }else{
                        postEditor.setContent(content);
                    }
                    deleteDropzoneAllimage('');
                    if (data.commonAttachments){
                        eachAttachements(data.commonAttachments,"dropzone_article_common");
                    }
                    if (data.chosenAttachments){
                        eachAttachements(data.chosenAttachments,"dropzone_article_chosen");
                    }
                }
                $('#modal-table').modal('show');
            });
        } else {
            bootbox.alert("请选择数据！");
        }
    });
    //文章删除
    $('#delete_cmsArticle').on('click', function() {
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
                $("#cmsArticle_form").xform("post", config.urlMap.delet + "?ids=" + ids,
                    function(data) {
                        if(data.success){
                            bootbox.alert("删除成功！",function(){
                                oTable1.fnDraw();
                            });
                        }else{
                            bootbox.alert("删除失败！");
                        }
                    });
            }
        });
    });
    $("#save_cmsArticle").on('click', function() {
        //if(checkFileFormat("#form-field-image")){
        //
        //}
        if (articleForm.check(false)) {
            $("textarea[name='articleContent']").val(postEditor.getContent());
            $('#cmsArticle_form').submit();
            oTable1.fnDraw();
        }
    });

    $("#categoryId").on('change', function(){
        if($(this).val()==1){
            $('#genre_group').show();
        }else {
            $('#genre_group').hide();
        }
        if($(this).val()==2){
            $('#articlechosen_group').show();
        }else {
            $('#articlechosen_group').hide();
        }
    })

});

$("#form-field-categoryId").setCategory(config.urlMap.tree);

function checkFileFormat(imageId){
    var fileName = $(imageId).val(),
        extIndex=fileName.lastIndexOf('.'),
        ext=fileName.substr(extIndex).toLowerCase();
    if(ext!=null && ext!=""){
        if(ext!=".jpg"&&ext!=".png"&&ext!=".bmp"&&ext!=".gif"&&ext!=".jpeg"){
            bootbox.alert("只能上传jpg,png,bmp,gif,jpeg格式的图片");
            $(imageId).val("");
            return false;
        }else{
            return true;
        }
    }else{
        return true;
    }
}

//加载分类
function setCategory(){
    $.post(
        config.urlMap.tree,function(data){
            $.each(data,function(i,v){
                $('#categoryId').append('<option value="'+v.id+'">'+ v.name +'</option>');
            });
        }
    );
}

//一键删除
$('.delImage').on('click', function() {
    var dropzoneID =$(this).attr("id");
    var imgID="";
    if(dropzoneID.indexOf("del-")!=-1){
        imgID=dropzoneID.substring(4,dropzoneID.length-1);
    }
    bootbox.confirm("你确定要删除吗?", function(result) {
        if (result) {
            deleteDropzoneAllimage(imgID);
            checkId(dropzoneID);
        }
    });
});

function checkId(dropzoneID){
    if(dropzoneID.indexOf("dropzone_goods")>=0){
        $("#j-json-dropzone_goods").val("");
    }
}

function deleteDropzoneAllimage(imgID){
    $(".dropzoneImage").each(function(){
        var dropzoneID =$(this).attr("id");
        var customDropzone = Dropzone.forElement("#"+dropzoneID);
        var dropZoneFiles = customDropzone.files;
        if(dropzoneID.indexOf(imgID)==0){
            if((dropzoneID.indexOf("mosaic")>=0)==(imgID.indexOf("mosaic")>=0)){
                if(dropZoneFiles.length > 0){
                    for(i=0; i<dropZoneFiles.length;i++){
                        customDropzone.removeFile(dropZoneFiles[i]);
                    }
                }
            }
        }

    });
}

//edit Contract
function findContractById(id) {
    $.post(config.urlMap.findattachment +"?orderid="+ id, function(data) {
        if (!!data.attachments) {
            eachAttachements(data.attachments, "dropzone_goods");
        }
    });
}

//edit:附件
function eachAttachements(attachments, fileId) {
    var customDropzone = Dropzone.forElement("#"+fileId);
    $.each(attachments, function(n, v) {
        addImageToDropzone(customDropzone, v);
        customColorBox(v.module);
    });
}