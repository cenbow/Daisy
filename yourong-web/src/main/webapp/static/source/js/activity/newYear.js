/**
 * Created by XR on 2016/12/30.
 */
(function () {
  $("#j-newyear").snowfall({
    image: '/static/img/activity/newYear/snow.png',
    flakeCount: 100,
    minSize: 5,
    maxSize: 10
  });
  var path = environment.globalPath,
    logined = $('#j-top').data('logined')


  Vue.config.devtools = true
  window.newyear = new Vue({
    el: '#j-newyear',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/newyear/init',
        receiveCoupon: path + '/activity/newyear/restore',
        receiveReward: path + '/activity/newyear/luckymoney'
      },
      logined: logined,
      initData: {},
      newMan: false,
      backMan: false,
      finishMan: false,
      defeatMan: false,
      show: true,
      dialog: false,
      dialog1: false,
      dialog2: false,
      gray: false,
      gray2: false,
      happen: false,
      text1: true,
      text2: true,
      text3: true,
      text4: true,
      friend: false,
      activityOver: false,

      //提示框
      dialogTips: '',                    //提示信息
      dialogTips2: '',
      targetUrls: '',                    //跳转路径
      dialogOther: '',                   //链接文字
      door: false,                       //蒙层


      value: [188, 100, 50, 188, 100, 50, 188, 100, 50],

    },
    created: function () {
      var self = this;

      $.xPost({
        url: self.urls.init,
        callback: function (data) {
          console.log(data);
          init(data)
        }
      })

      function init(data) {
        self.initData = {
          status: data.status,  //活动状态
          startTime: data.startTime,
          endTime: data.endTime,
          // activityForNewYear: data.activityForNewYear,
          // activityLotteryCount: data.activityLotteryCount || 0,  //图标数量
          registerTime: data.registerTime,  //注册时间
          transactionCount: data.transactionCount,  //十月后投资数
          referralCount: data.referralCount,  //邀请用户数
          referralTransactionCount: data.referralTransactionCount,  //邀请用户并投资数
          newDate: env.serverDate,
          luckyMoney28: data.luckyMoneyTemplateIds.split(',')[0],
          luckyMoney88: data.luckyMoneyTemplateIds.split(',')[1],
          luckyMoney888: data.luckyMoneyTemplateIds.split(',')[2],
          luckyMoney2017: data.luckyMoneyTemplateIds.split(',')[3]
        };
        if (self.initData.status == 6) {
          self.gray = true;
          self.gray2 = true;
          self.activityOver = true;
        }
        // console.log(self.initData.luckyMoney28,self.initData.luckyMoney88,self.initData.luckyMoney888,self.initData.luckyMoney2017)
        // if (self.initData.newDate > self.initData.luckyMoneyEndTime) {
        //   self.gray2 = true;
        // }
        // if (self.initData.received){
        //   self.happen = true;
        // }
      }
    },
    methods: {
      //查看图标
      look: function (event) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true
          self.dialogTips = '活动未开始'
          self.door = true
        } else {
          self.goLoginPage();
        }
      },
      //关闭弹框
      close: function () {
        var self = this;
        self.dialog = false;
        self.dialog1 = false;
        self.dialog2 = false;
        self.door = false;
        self.friend = false;
      },
      //我要特权
      getpower: function () {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true
          self.dialogTips = '活动未开始'
          self.door = true
        }else if (self.initData.status == 6) {
          return false;
        } else {
          if (!self.logined) {
            self.goLoginPage();
          } else {
            self.show = false;
            if (self.initData.registerTime > self.initData.startTime) {
              self.newMan = true
            } else if (self.initData.registerTime < self.initData.startTime && self.initData.transactionCount == 0) {
              self.backMan = true
            } else if (self.initData.transactionCount > 0 && self.initData.referralTransactionCount < 5) {
              self.defeatMan = true
            } else if (self.initData.transactionCount > 0 && self.initData.referralTransactionCount >= 5) {
              self.finishMan = true
            }
          }
        }
      },
      //邀请好友
      invite: function () {
        var self = this;
        self.friend = true
        self.door = true
      },
      // 去登陆
      goLoginPage: function () {
        $.xPost({url: this.urls.receiveReward})
      },
      //兑换方法
      gochange: function (id) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true
          self.dialogTips = '活动未开始'
          self.door = true
        } else if (self.initData.status == 6) {
          return false;
        } else {
          if (!self.logined) {
            self.goLoginPage();
          } else {
            $.xPost({
              url: this.urls.receiveCoupon,
              data: {
                templateid: id
              },
              callback: function (data) {
                if (data.success) {
                  self.dialog1 = true
                  self.dialogTips = data.result.couponAmount;
                  self.door = true
                  self.initData.activityLotteryCount = self.initData.activityLotteryCount - data.result.consume
                } else {
                  self.dialog = true
                  // console.log(data.resultCodeEump.msg)
                  self.dialogTips = '图标数量不足，快去投资获得更多图标吧！'
                  self.door = true
                }
              }
            })
          }
        }
      },
      //领券
      getmoney: function (id) {
        var self = this;
        if (self.initData.status == 2) {
          self.dialog = true
          self.door = true
          self.dialogTips = '活动未开始'
        } else if (self.initData.status == 6) {
          return false;
        } else {
          if (!self.logined) {
            self.goLoginPage();
          } else {
            $.xPost({
              url: this.urls.receiveReward,
              data: {
                templateid: id
              },
              callback: function (data) {
                if (data.success) {
                  self.dialog2 = true;
                  self.dialogTips = data.result.luckyMoneyAmount;
                  self.door = true
                } else {
                  self.dialog = true;
                  self.door = true
                  if (data.resultCodeEum[0].code == 90709){
                    self.dialogTips = '您需要使用掉该面额红包才能再次领取哦'
                  }else {
                    self.dialogTips = data.resultCodeEum[0].msg;
                  }
                }
              }
            })
          }
        }
      }
    }
  })

  var newtext = $(".m-new-text");
  /*导航选中高亮*/
  var rightSlideActive = function rightSlideActive($index) {
    newtext.removeClass('active').eq($index).addClass('active');
    $(".m-nav-btn").removeClass('active').eq($index).addClass('active');
  }

  /*悬浮导航点击*/
  $(".m-nav-btn").on('click', function () {
    var $index = $(this).index();
    $("html,body").animate({
      scrollTop: $('.m-fu-title').eq($index).offset().top - 80
    }, 500, function () {
      rightSlideActive($index);
      // updateNavItem();
    });
  })

  $(".m-nav-back").on('click',function () {
    $("html,body").animate({
      scrollTop: 0
    }, 500);
  });

  var updateNavItem = function updateNavItem() {
    //获取滚动条的滑动距离
    var top = $(document).scrollTop();          //定义变量，获取滚动条的高度
    var menu = $("#j-newyear");            //定义变量，抓取#menu
    var items = menu.find(".j-main-wrapper");          //定义变量，查找.item
    var curId = "";                              //定义变量，当前所在的楼层item #id

    items.each(function (index) {
      var item = $(this);
      var itemTop = item.offset().top - 118;        //定义变量，获取当前的top偏移量
      $(this).attr("data-id", index);
      if (top >= itemTop) {
        curId = item.attr("data-id");
      } else {
        return false;
      }
    });

    //根据取得的id设置相应属性
    if (curId && !newtext.eq(curId).hasClass("active")) {
      rightSlideActive(curId);
    }
  }

  /*滚动到相应模块，右侧导航响应*/
  var topNav = $("#j-nav-toTop");
  // var top = $(document).scrollTop();
  $(window).scroll(function () {
    var navTop = $(this).scrollTop();
    if (navTop > 120) {
      topNav.css({
        position: "fixed",
        top: "0"
      })
    } else {
      topNav.css({
        position: "absolute",
        top: "115px"
      })
    }
    updateNavItem();
  });
  $(window).ready(function () {
    var navTop = $(this).scrollTop();

    if (navTop > 120) {
      topNav.css({
        position: "fixed",
        top: "0"
      })
    } else {
      topNav.css({
        position: "absolute",
        top: "115px"
      })
    }
  });

  // 邀请好友按钮，点击展示浮层
  $('#j-btn-invite').on('click', function () {
    $('#j-qrcode-wrap').removeClass('z-hide')
    $.shade('show')
  })

  // 邀请链接复制
  var clipboard = new Clipboard('#j-copy-invite')
  clipboard.on('success', function () {
    $.xDialog({
      type: 'success',
      content: '复制成功'
    })
  })
})()