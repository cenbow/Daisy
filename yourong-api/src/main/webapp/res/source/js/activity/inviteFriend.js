/**
 * Created by XR on 2017/3/14.
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
    el: '#j-inviteFriend',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/depository/index',
        receiveCoupon: path + '/activity/dynamicInvoke'
      },
      logined: os > 2 ? logined : hook.logined,
      loginUrl: path + '/mstation/login?from=' + location.href,
      initData: {},
      selectTab: 1,
      door: false,
      pages:{
        pageNo: 0,
        totalPageCount: 0
      },
      isLoad: false,

      //提示框
      dialogTips: '',                    //提示信息
      isClick: true,
      rule: false,
      tipsList:['','活动即将开始','活动已结束'],
      tipsIndex: 0,
      isShowTips: false,
      record: false,
      friend: false,
      arrLength: 8,
      lotteryRecord: []
    },
    created: function () {
      var self = this;

      window.onhashchange = function () {
        if (location.hash == '') {
          window.scrollTo(0, 0)
          self.record = false
        }
      }

      self.initData = {
        newDate: +environment.serverDate,
        startDate: receiveData.result.startDate,
        endDate: receiveData.result.endDate,
        inviteFriendLists: receiveData.result.inviteFriendLists || [],  //英雄榜
        inviteCount: receiveData.result.inviteCount || 0,  //邀请人数
        invest: receiveData.result.invest || 0,  //累计投资额
        reward: receiveData.result.reward || 0,  //奖金
      };
      //英雄榜列表小于10条时用虚位以待填充
      self.initData.inviteFriendLists = self.fillLuckyMemberList(self.initData.inviteFriendLists)

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
      invite: function () {
        var self = this
        if (self.initData.newDate < self.initData.startDate){
          self.openTipsFrame(1)
        }else if (self.initData.newDate > self.initData.endDate){
          self.openTipsFrame(2)
        }else {
          if (!self.logined) {
            if (os > 2) {
              self.goLoginPage();
            }
          }else {
            self.friend = true
          }
        }
      },
      inviteapp: function ($event) {
        var self = this
        if (self.initData.newDate < self.initData.startDate){
          self.openTipsFrame(1)
        }else if (self.initData.newDate > self.initData.endDate){
          self.openTipsFrame(2)
        }else {
          if (!self.logined) {
            if (os > 2) {
              self.goLoginPage();
            } else {
              hook.login($($event.currentTarget));
            }
          }
        }
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
      // 用虚位以待填充
      fillLuckyMemberList: function (arr) {
        var self = this;
        if(arr.length > 0 && arr.length < 8){
          self.arrLength = arr.length
          for (var i = 0; i < 8 - self.arrLength; i++) {
            arr.push({
              avatars : '',
              username : "邀友风云榜，等你来上榜",
              rewardInfo: '',
              totalInvestAmount: ''
            })
          }
        }
        return arr
      },

      close: function () {
        var self = this;
        self.door = false;
        self.friend = false;
        self.rule = false
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
      // 中奖纪录弹框增加登陆判断
      showrecord: function ($event) {
        var self = this
        if (!self.logined && os > 2) {
            self.goLoginPage();
        } else {
          window.location.hash = '#bothRecord'
          self.record = true;
          self.pages.pageNo = 0
          self.loadMoreData($event)
        }
      },
      // 关闭弹框
      closeDialog: function (callback) {
        var self = this
        this.sign = ''
        self.record = false
        if(callback) {
          window.location.reload()
        }
      },
      // 我的中奖记录
      loadMoreData:function ($event) {
        var self=this
        self.pages.pageNo++
        self.isLoad = true

        if (os !== 3) {
          hook.getEvent('inviteFriendDetail'
            + '&isNeedRealName=0'
            + '&args_pageNo_1_integer_'+ self.pages.pageNo, $($event.currentTarget))
        }else {
          base.getAPI({
            url: self.urls.receiveCoupon,
            data: {
              invokeMethod: 'inviteFriendDetail',
              invokeParameters: '&args_pageNo_1_integer_'+ self.pages.pageNo
            },
            callback: function(data) {
              self.Callback(data)
            }
          })
        }
      },
      Callback: function (data) {
        var self = this;
        if(data.success){
          self.lotteryRecord = []
          var res = data.result
          self.isLoad = false
          //列表
          var resList = res.data

          resList.forEach(function(obj){
            self.lotteryRecord.push(obj)
          })

          //分页
          self.pages = {
            pageNo: res.pageNo,
            totalPageCount: res.totalPageCount
          }
        }
      }
    }
  })

  //列表滚动
  $.fn.scrollUserList = function (config) {
    this.each(function () {
      var _this = $(this),
        scrollSize = _this.find("li").length;
      if (scrollSize > config.size) {
        setInterval(function () {
          var scrollItems = _this.find("li:visible");
          _this.animate({marginTop: config.height}, 700, function () {
            scrollItems.eq(0).appendTo(_this);
            _this.css("margin-top", 0);
          });
        }, 3000);
      }
    })
  };

  // 邀请链接复制
  var clipboard = new Clipboard('#j-copy-invite')
  clipboard.on('success', function () {
    // $.xDialog({
    //   type: 'success',
    //   content: '复制成功'
    // })
    $('.u-vanish-get').removeClass('z-hidden',setTimeout(function(){
      $('.u-vanish-get').addClass('z-hidden')
    },2000));
  })

  if (vm.arrLength > 3){
    $('.j-usersRankList').scrollUserList({
      size: 5,
      height: -33,
      length: 1
    })
  }


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

  $(".m-jump").on('click',function () {
    $("html,body").animate({
      scrollTop: $('#m-move-flag').offset().top
    }, 500);
  })

  var updateNavItem = function updateNavItem() {
    //获取滚动条的滑动距离
    var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
    var menu = $("#j-inviteFriend");            //定义变量，抓取#menu
    var items = menu.find(".m-fu-title");          //定义变量，查找.item
    var curId = "";                              //定义变量，当前所在的楼层item #id

    items.each(function (index) {
      var item = $(this);
      var itemTop = item.offset().top - 10;        //定义变量，获取当前的top偏移量
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