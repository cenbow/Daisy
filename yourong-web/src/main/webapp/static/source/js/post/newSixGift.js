(function(){

    /* 调整6个任务的位置用的 默认排列顺序为：实名认证》投资》完善》绑定》体验》关注*/
    var $taskList = $('#j-task-list'),
        taskList = ['trueName', 'project', 'info', 'email', 'app', 'weixin'],
        ActiveList = [],
        NotActiveList = [],
        $thisItem;

    taskList.forEach(function(item){
        $thisItem = $taskList.find('[data-href='+ item +']');

        if($thisItem.hasClass('z-active')){
            ActiveList.push($thisItem.prop('outerHTML'))
        } else{
            NotActiveList.push($thisItem.prop('outerHTML'))
        }
    });

    // 与后台接轨
    var domain = env.globalPath,
        urls = {
            init: domain + '/post/newSixGift/init', // 新手项目列表
            userList: domain + '/activity/newerMission/init', // 用户列表
            profile: domain + '/member/profile' //实名认证30元现金券
        };

    /* 新手项目 */
    $.xPost({
        url: urls.init,
        callback: function(data){
            if(data.success){
                renderNewerMission(data)
            } else {
                // 不会出现这种情况
                console.log('新手项目数据没有加载成功')
            }
        }
    });

    function renderNewerMission(data){
        var result = data.result;

        $('#j-newerMission').html(template('j-newerMission-tpl', result));

        // part3的按钮
        if($taskList.find('li[data-href="project"]').hasClass('z-active')){
            $('#j-part3-btn1').text('人气值已获得').addClass('z-active').removeAttr('href')
        } else {
            $('#j-part3-btn1').text('我要10点人气值').attr({
                'href': $('#newC_aC').attr('href'),
                'target': '_blank'
            })
        }

        // 新手任务圆环，兼容IE9
        var $pstat = $('.u-pl-stat');
        if ($pstat.length > 0) {
            $("body").append("<canvas id='j-canvas-demo'></canvas>");

            try {
                $("canvas")[0].getContext("2d");
                $pstat.circliful();
            } catch (err) {}

            var circle = $('#j-pl-stat2');
            circle.data('percent', data.result.round);
            var round=$('#newC_round'),
                oldImg=round.css('background-image'),
                newImg=oldImg.replace(/_finish/g, data.result.round);
            round.css('background-image',newImg);
            $("#j-canvas-demo").remove();
        }
    }


    /* part5用户列表 */
    $.xPost({
        url: urls.userList,
        callback: function(data){
            if(data.success){
                renderResult(data)
            } else {
                //没取到数据的时候，不会出现这种情况
                console.log('注册用户列表数据没有加载成功')
            }
        }
    });

    function renderResult(data){
        var resultList = data.resultList;

        resolveResultList(resultList);

        if(resultList && resultList.length>0) {
            $('#j-heavyInvestment-list').html(template('j-heavyInvestment-list-tpl', data));
            /* part5 滚动 */
            var scrollList=$("#j-heavyInvestment-list"),
                scrollSize=scrollList.find("li").length;
            if(scrollSize>8){
                setInterval(function(){
                    var scrollItems=scrollList.find("li:visible");
                    scrollList.animate({marginTop:-57},700,function(){
                        scrollItems.eq(0).appendTo(scrollList);
                        scrollItems.eq(1).appendTo(scrollList);
                        $(this).css("margin-top",0);
                    });
                },5000);
            }
        } else {
            $('#j-heavyInvestment-list').html('暂时没用数据哦');
        }
    }

    function numberAppendZero(num) {
        if (num < 10) {
            num = "0" + num;
        }
        return num;
    }

    // 处理返回来的resultList中的 activityName,只在这里处理，其他地方都用不到的
    function resolveResultList(data){
        var createTime,
            createDay,
            createClock,
            tempStr,
            strHead,
            strTail;

        $.each(data, function(index, value){
            createTime = new Date(value.createTime);

            // 由于时间需要 24小时制，这里特别处理一下
            createClock = numberAppendZero(createTime.getHours()) + ':'
                + numberAppendZero(createTime.getMinutes()) + ':'
                + numberAppendZero(createTime.getSeconds());

            createTime = createTime.toLocaleString().replace(/年|月|\//g, "-").replace(/日|上午|下午/g, " ");
            createDay = createTime.substring(0, createTime.lastIndexOf(' '));

            tempStr = value.activityName.substr(5);
            strHead = tempStr.substring(0, tempStr.indexOf('送')+1);
            strTail = tempStr.substring(tempStr.indexOf('送')+1);

            tempStr = value.activityName.substr(5);
            strHead = tempStr.substring(0, tempStr.indexOf('送')+1);
            strTail = tempStr.substring(tempStr.indexOf('送')+1);

            value.createTime = createTime;
            value.strHead = strHead;
            value.strTail = strTail;
            value.createDay = createDay;
            value.createClock = createClock;
        });
    }

    var taskListHtml = NotActiveList.concat(ActiveList);
    $taskList.html(taskListHtml.join(''));

    // 已完成的任务放到最后
    var $htmlBody = $('html, body');
    $taskList.find('li').removeClass('z-hide').on('click', function(){
        $htmlBody.animate({
            scrollTop: $('.' + $(this).attr('data-href')).offset().top - 80
        }, 500)
    });

    // part3的问号
    $('#j-question').hover(function(){
        $('.u-answer').fadeToggle()
    });

    // 登录之后处理, part4的两个按钮,和印章
    if($taskList.find('li[data-href="info"]').hasClass('z-active')){
        $('#j-part4-btn1a').text('人气值已获得').addClass('z-active').removeAttr('href')
    } else{
        $('#j-part4-btn1a').text('我要10点人气值')
    }

    if($taskList.find('li[data-href="email"]').hasClass('z-active')){
        $('#j-part4-btn2a').text('人气值已获得').addClass('z-active').removeAttr('href')
    }else{
        $('#j-part4-btn2a').text('我要10点人气值')
    }

    if($taskList.find('li[data-href="app"]').hasClass('z-active')){
        $('#j-qrcode-app').addClass('z-active')
    }
    if($taskList.find('li[data-href="weixin"]').hasClass('z-active')){
        $('#j-qrcode-weixin').addClass('z-active')
    }

    // 未登录时登录跳转，这里纯粹只为了登录之后会跳转回原来页面而作，不取数据
    $('#j-steps-btn, #j-part3-btn2, #j-part4-btn1b, #j-part4-btn2b').on('click', function(){
        $.xPost({ url: urls.profile })
    });
})();
