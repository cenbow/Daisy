/**
 * Created by XR on 2017/3/23.
 */
define(['base'], function (require, exports, module) {
  'use strict'
  /* 除掉移动端默认的点击图片出现全图浏览 */
// $('img').on('click', function (e) {e.preventDefault()})


  var path = environment.globalPath,
    currentTime = +environment.serverDate,
    base = require('base'),
    hook = new AppHook(os)

  Vue.config.devtools = true

  var vm = new Vue({
    el: '#j-goodSpring',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/springComing/init',
        receiveCoupon: path + '/activity/dynamicInvoke'
      },
      logined: os > 2 ? logined : hook.logined,
      loginUrl: path + '/mstation/login?from=' + location.href,
      init: {},
      door: false,
      whiteCoverShow: false,

      //提示框
      showDialog: false,                    //提示信息
      isClick: true,
      rule: false,
      tipsList:['','活动即将开始','活动已结束','您已经领取过了哦'],
      tipsIndex: 0,
      rewardIndex: 0,
      isShowTips: false,
      appIndex: 0,
      nocheck: false
    },
    created: function () {
      var self = this;

      window.onhashchange = function () {
        if (location.hash == '') {
          window.scrollTo(0, 0)
          self.record = false
        }
      }

      self.init = {
        newDate: +environment.serverDate,
        startDate: receiveData.result.startDate || '',   //活动开始时间
        endDate: receiveData.result.endDate || '',       //活动结束时间
        status: receiveData.result.status || 2,          //活动状态
        couponList: receiveData.result.couponList || [],     //券
        template: receiveData.result.template || 0,          //领过的券ID
        totalAmount: receiveData.result.totalAmount || 0,    //投资额
        pacNum: receiveData.result.pacNum || 0         //如意包
      };

      window.hookCallback = function (data,eventName) {
        self.Callback(data)
      }
    },
    methods:{
      // 非模态提示框
      openTipsFrame: function(num) {
        var self = this
        self.tipsIndex = num
        self.isShowTips = true
        setTimeout(function(){
          self.isShowTips = false
        },2000)
      },
      //格式化时间
      formatTime: function(date, format){
        date = new Date(date);

        var map = {
          "M": date.getMonth() + 1, //月份
          "d": date.getDate(), //日
          "h": date.getHours(), //小时
          "m": date.getMinutes(), //分
          "s": date.getSeconds(), //秒
          "q": Math.floor((date.getMonth() + 3) / 3), //季度
          "S": date.getMilliseconds() //毫秒
        };
        format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
          var v = map[t];
          if (v !== undefined) {
            if (all.length > 1) {
              v = '0' + v;
              v = v.substr(v.length - 2);
            }
            return v;
          }
          else if (t === 'y') {
            return (date.getFullYear() + '').substr(4 - all.length);
          }
          return all;
        });
        return format;
      },

      close: function () {
        var self = this;
        self.door = false;
        self.rule = false;
        self.showDialog = false;
        self.nocheck = false;
      },
      openrule: function () {
        var self = this;
        self.door = true;
        self.rule = true
      },
      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl;
        return false
      },
      // 领红包
      getmoney:function (num,$event) {
        var self=this
        if (self.init.status == 2){
          self.openTipsFrame(1)
        }else if (self.init.status == 6){
          self.openTipsFrame(2)
        }else {
          if (!self.logined && os > 2) {
            self.goLoginPage();
            // self.isClick = true;
          }else {
            if(self.init.template){
              return false;
            }else {
              self.appIndex = num;
              self.whiteCoverShow = true;
              if (os !== 3) {
                hook.getEvent('springComingActivity'
                  + '&isNeedRealName=0'
                  + '&args_templateId_1_integer_'+ self.init.couponList[num].id, $($event.currentTarget));
                self.whiteCoverShow = false;
              }else {
                base.getAPI({
                  url: self.urls.receiveCoupon,
                  data: {
                    invokeMethod: 'springComingActivity',
                    invokeParameters: '&args_templateId_1_integer_'+ self.init.couponList[num].id
                  },
                  callback: function(data) {
                    self.Callback(data)
                  }
                })
              }
            }
          }
        }
      },
      Callback: function (data) {
        var self = this;
        if(data.success){
          self.door = true;
          self.showDialog = true;
          self.rewardIndex = self.appIndex;
          self.init.template = self.init.couponList[self.appIndex].id
        }else {
          switch (data.resultCodes[0].code) {
            case "90703":
              self.openTipsFrame(3);
              break;
            case "94016":
              self.door = true;
              self.nocheck = true;
              break;
            default:
              break;
          }
        }
        self.whiteCoverShow = false;
      }
    }
  })

  var btn = $(".m-nav-btn");

  var rightSlideActive = function rightSlideActive($index) {
    btn.removeClass('active').eq($index).addClass('active');
    $(".m-nav-btn").removeClass('active').eq($index).addClass('active');
  }

  /*悬浮导航点击*/
  $(".m-nav-btn").on('click', function () {
    var $index = $(this).index();
    $("html,body").animate({
      scrollTop: $('.m-fu-title').eq($index).offset().top
    }, 500, function () {
      rightSlideActive($index);
      // updateNavItem();
    });
  })

  var updateNavItem = function updateNavItem() {
    //获取滚动条的滑动距离
    var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
    var menu = $("#j-goodSpring");            //定义变量，抓取#menu
    var items = menu.find(".m-fu-title");          //定义变量，查找.item
    var curId = "";                              //定义变量，当前所在的楼层item #id

    items.each(function (index) {
      var item = $(this);
      var itemTop = item.offset().top - 180;        //定义变量，获取当前的top偏移量
      $(this).attr("data-id", index);
      if (top >= itemTop) {
        curId = item.attr("data-id");
      } else {
        return false;
      }
    });

    //根据取得的id设置相应属性
    if (curId && !btn.eq(curId).hasClass("active")) {
      rightSlideActive(curId);
    }
  }

  $(window).scroll(function () {
    updateNavItem();
  });
})