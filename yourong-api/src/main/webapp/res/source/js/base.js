/**
 * YourongMobile Base Library
 * Created by adeweb on 15/6/3.
 */
/*global define,$,environment,console,fz*/

define(function (require, exports, module) {
    'use strict';
    //页面标识
    exports.isPage= function (pagetag) {
        var $pagetag=$('#j-pagetag');
        return $pagetag.length?$pagetag.data('pagetag')===pagetag:false;
    };

    //环境常量
    window.env = {
        path:environment.globalPath,
        time:+environment.serverDate,
        ali:environment.aliyunPath,
        debug:environment.debug
    };

    //空函数
    $.extend($,{noop:function(){}});

    //cache data
    exports.cache={};

    //阴影蒙层
    exports.cover={
        $el:(function () {
            var $cover = $('.j-cover'),
                $el;
            if($cover.length){
                $el = $cover;
            }else{
                $('body').append('<div class="u-cover j-cover"></div>');
                $el = $('.j-cover');
            }
            return $el;
        })(),
        show: function (target,closable) {
            var self = this,
                $target = $(target)

            this.$el.show()

            if(typeof(target)==='undefined'){
                return false
            }

            if(target && $target.length){
                $target.show()
            }

            if(closable){
                this.$el.on('click', function () {
                    self.hide(target)
                })
            }else{
                this.$el.off('click')
            }
        },
        hide: function (target) {
            this.$el.hide()
            if(target && $(target).length){
                $(target).hide()
            }
        }
    };

    //获取用于显示的倒计时时间
    exports.getCountTime = function (endTime) {
        var currentTime = new Date().getTime(),
            seconds = Math.round((endTime - currentTime) / 1000);
        if (endTime - currentTime < 0) {
            return false;
        }
        var timeArray = [Math.floor(seconds / 3600) || '00', Math.floor((seconds / 3600) % 1 * 60) || '00', seconds % 60];
        for (var i = 0; i < timeArray.length; i++) {
            if (timeArray[i] < 10 && timeArray[i] !== '00') {
                timeArray[i] = '0' + timeArray[i];
            }
        }
        var timeString = timeArray.join(":");
        if (timeString === '00:00:00') {
            return false;
        }
        return timeString;
    };

    exports.showEmptyData = function (targetId) {
        var html = '<div class="u-data-empty"><img src="'+
            environment.globalPath+'/res/img/logoGray.png"/>' +
            '<br>您还没有相关记录</div>';
        $(targetId).html(html);
    };

    //阻止uc、QQ等手机浏览器侧滑翻页
    exports.stopBrowserSlide=function(){
        var control = navigator.control || {};
        if (control.gesture) {
            control.gesture(false);
        }
    };

    //API request handler
    exports.getAPI = function (options) {
        if (typeof(options) === 'object') {
            var xToken=$('#xToken').val(),
                data={};
            if(options.data){
                if($.isArray(options.data)){
                    $.each(options.data, function (index, item) {
                        data[item.name]=item.value;
                    });
                }else{
                    data=options.data;
                }
            }
            data.xToken=xToken;
          var loadTime=setTimeout(function(){
                 loading.start()
             },10);
            $.ajax({
                url: options.url,
                data:data,
                type: options.type||'POST',         
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': options.version ||  '1.0.0'
                },
                dataType: options.dataType|| 'json',
                success: function(data){
                    clearTimeout(loadTime)
                      loading.end()
                   if(typeof(options.ids)==='object'){
                       options.callback(data,options.ids);
                   }else{
                       options.callback(data);
                   }
                }
            });
        } else {
            throw new Error('options must be an object');
        }
    };
    exports.memberBehavior = function (anchor, htmlToken, sourceId, remarks) {
        $.ajax({
            url: environment.globalPath + '/yrwHtml/memberBehavior',
            data: {
                sourceId: sourceId || '',//业务ID
                deviceParam: '',//设备参数
                remarks: remarks || '',//备注
                device: '',//设备
                anchor: anchor,//锚点
                htmlToken: htmlToken
            },
            type: 'POST',
            headers: {
                'X-Requested-Accept': 'json',
                'Accept-Version': '1.0.0'
            },
            dataType: 'json'
        })
    }
    //信息提示
    exports.xTips=function(config){
        var time=1500,
            type='info',
            content='',
            callback=function(){};
        if (typeof config === "object"){
            time=config.time||time;
            type=config.type||type;
            content=config.content||'';
            callback=config.callback||function(){};
            var tipsHtml='<div class="u-poptips z-'+type+'">'+
                '<div class="u-poptips-cnt"><i></i>'+content+'</div>'+
                '</div>',
                $tips=$('.u-poptips');
            if(!$tips.length){
                $('body').append(tipsHtml).find('.u-poptips').animate({
                    opacity:0.6
                },100,function(){
                    setTimeout(function () {
                        $('body').find('.u-poptips').animate({
                            opacity:0
                        },100,function(){
                            $('.u-poptips').remove();
                        });
                    },time);
                });
                if(config.delay&&/^[1-9]\d*$/.test(config.delay)){
                    setTimeout(function () {
                        callback();
                    },config.delay);
                }else{
                    callback();
                }
            }
        }else{
            throw new Error("xTips: config参数为空或不是对象");
        }
    };

    //输入校验
    exports.validate= function (formId, eventType) {
        $(formId).on(eventType,'.j-validate', function () {
            var val = $(this).val(),
                type=$(this).data('type'),
                content = $(this).data('errtips') || '';
            var regList={
                    'bankcard':/^\d{16,19}$/,
                    'mobile':/^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/,
                    'smscode':/^\d{6}$/
                },
                reg = regList[type]||null;

            if(reg){
                if(!reg.test(val)){
                    exports.xTips({content: content});
                    $(this).data('verified',false);
                }else{
                    $(this).data('verified',true);
                }
            }else{
                if(val&&val.length){
                    $(this).data('verified',true);
                }else{
                    module.xTips({content: content});
                    $(this).data('verified',false);
                }
            }
        });
    };

    //loading图标的加载和结束
    var loading=exports.loading={
       // loading
       render: function () {
           $('body').append('<div class="u-loading-box j-loading-box">' +
               '<div class="u-progress-bar">' +
               '<img src="' + environment.globalPath +
               '/res/img/xloading.gif" alt="请稍候..."/></div></div>' +
               '<div class="u-loadingCover j-loadingCover"></div>');
       },

       'start':  function (){
           this.end();
           this.render();
        },

      'end':function (){
          $('.j-loading-box, .j-loadingCover').remove();
      }
    };

    //请求数据后的处理数据
    exports.dataHanding=function(config){
        
    }
    /**
     * 获取URL参数
     * @param {string} name
     * @param {string} url //如果不是浏览器环境,此项必填
     * @return {string}
     *
     */
    exports.getUrlParam= function(name, url) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"),
            localUrl = typeof(window) !== 'undefined' ? window.location.search : url.substr(url.indexOf('?')),
            params = localUrl.substr(1).match(reg); // 匹配目标参数
        return params ? decodeURI(params[2]) : ''; // 返回参数值
    };

    /**
     * 本地存储封装
     * 所有键值对存在localStorage.xStorage下
     * @param name {string} key name
     * @param value {string|null} key value
     * @return get => {string|number}, set|del => {object},没有参数 => undefined
     */
    exports.storage=function(name, value) {
        var sessionData = {},
            xStorageValue = localStorage.getItem('xStorage');

        if (xStorageValue) {
            sessionData = $.parseJSON(xStorageValue);
        } else {
            localStorage.setItem('xStorage', '{}');
        }

        var action = typeof(value) === 'undefined' ?
            'get' : (value !== null ? 'set' : 'del');

        switch (action) {
            case 'get':
                return $.type(name) === 'string' ? sessionData[name] : undefined;

            case 'set':
                sessionData[name] = value;
                localStorage.setItem('xStorage', JSON.stringify(sessionData));
                break;

            case 'del':
                delete sessionData[name];

                if(name){
                    localStorage.setItem('xStorage', JSON.stringify(sessionData))
                }else{
                    throwError('name');
                }
                break;

            default:
                return sessionData;
        }

        function throwError(name) {
            throw new ReferenceError('xStorage: ' + name + ' is not defined');
        }

        return sessionData;
    };
});