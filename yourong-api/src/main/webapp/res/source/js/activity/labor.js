/**
 * Created by XR on 2017/4/20.
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
    el: '#j-labor',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/labor/index',
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
      tipsList:['','活动即将开始','','领 取','明天再来','活动已结束'],
      tipsIndex: 0,
      rewardIndex: 0,
      isShowTips: false,
      appIndex: 0,
      nocheck: false,

      arr: [],
      n: 86400000,
      signIn: [0,0,0,0,0,0,0,0],
      giftIn: [0,0,0,0,0,0,0,0],
      index: 0,
      giftindex: 0,
      today: '',
      newDate: '',
      curTime: '',
      checkSign: -1,
      giftSign: -1,
      canget: true
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
        startTime: receiveData.result.startTime || '',   //活动开始时间
        endTime: receiveData.result.endTime || '',       //活动结束时间
        status: receiveData.result.status || 2,          //活动状态
        signDate: receiveData.result.signDate || [],       //签到日期
        giftsDate: receiveData.result.giftsDate || [],       //领取日期
        receiveWorker: receiveData.result.receiveWorker,      //劳模奖章
        receivePioneer: receiveData.result.receivePioneer,      //先锋奖章
        receiveDedicated: receiveData.result.receiveDedicated,      //敬业奖章
        workInvest: receiveData.result.workInvest,               //领取礼包阈值
        workRight: receiveData.result.workRight,                 //是否能领取礼包
        invest: receiveData.result.invest || 0,       //今日投资额
      };
      var start = self.init.startTime
      self.arr= [start,start + self.n,start + 2*self.n,start + 3*self.n,start + 4*self.n,start + 5*self.n,start + 6*self.n,start + 7*self.n]
      self.init.signDate= [start,start + 2*self.n,start + 3*self.n]
      self.init.giftsDate= [start,start + 2*self.n,start + 3*self.n]
      self.curTime = self.formatTime(self.init.newDate, 'yyyy-M-d')
      self.today = new Date(self.curTime).getTime();
      self.tipsList = ['','活动即将开始','累投≥'+self.init.workInvest+'元可领取礼包','领 取','明天再来','活动已结束']
      self.today = start + 5*self.n;

      //遍历出哪些天是签到过的
      var signDate = self.init.signDate;
      for (var i=0;i < signDate.length;i++){
        var li = $('.m-checkbox-date').find('li')
        var sign = signDate[i]
        for (var j=0;j < self.arr.length;j++){
          if (self.arr[j] == sign && self.arr[j] <= self.today){
            self.index = (sign-start) / (self.n)
            self.signIn.$set(self.index,1)
          }
        }
      }
      for (var j=0;j < self.arr.length;j++){
        if (self.arr[j] == self.today){
          self.checkSign = j
        }
      }

      //遍历出哪些天是过的领取
      var giftsDate = self.init.giftsDate;
      for (var i=0;i < giftsDate.length;i++){
        var li = $('.m-body-getbox').find('li')
        var gift = giftsDate[i]
        if (self.today == gift){
          self.canget = false
        }
        for (var j=0;j < self.arr.length;j++){
          if (self.arr[j] == gift && self.arr[j] <= self.today){
            self.giftindex = (gift-start) / (self.n)
            self.giftIn.$set(self.giftindex,1)
          }
        }
      }
      for (var j=0;j < self.arr.length;j++){
        if (self.arr[j] == self.today){
          self.giftSign = j
        }
      }

      //领取框文案显示判断
      if (self.init.newDate < self.init.startTime){
        self.tipsIndex = 1
      }else if (self.init.newDate > self.init.endTime){
        self.tipsIndex = 5
      }else if (!self.init.workRight){
        self.tipsIndex = 2
      }else if (self.init.workRight && self.canget){
        self.tipsIndex = 3
      }else if (self.init.workRight && !self.canget){
        self.tipsIndex = 4
      }
      window.hookCallback = function (data,eventName) {
        self.Callback(data)
      }
    },
    methods:{
      // 非模态提示框
      openTipsFrame: function() {
        var self = this
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
        self.rule = true
      },
      // 关闭弹框
      closeDialog: function (callback) {
        var self = this
        self.rule = false
        if(callback) {
          window.location.reload()
        }
      },
      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl;
        return false
      },
      goget: function ($event) {
        var self = this;
        if (self.tipsIndex == 3){
          self.whiteCoverShow = true
          if (os !== 3) {
            hook.getEvent('receiveLabor'
              + '&isNeedRealName=0', $($event.currentTarget));
            self.whiteCoverShow = false;
          }else {
            base.getAPI({
              url: self.urls.receiveCoupon,
              data: {
                invokeMethod: 'receiveLabor'
              },
              callback: function(data) {
                self.Callback(data)
              }
            })
          }
        }
      },
      Callback: function (data) {
        var self = this;
        if(data.success){
            self.showDialog = true
            self.door = true
            self.whiteCoverShow = false
            self.tipsIndex = 4
            self.canget = false
        }else {
          switch (data.resultCodes[0].code) {
            case "90744":
              self.openTipsFrame();
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
    var menu = $("#j-labor");            //定义变量，抓取#menu
    var items = menu.find(".m-fu-title");          //定义变量，查找.item
    var curId = "";                              //定义变量，当前所在的楼层item #id

    items.each(function (index) {
      var item = $(this);
      var itemTop = item.offset().top - 80;        //定义变量，获取当前的top偏移量
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