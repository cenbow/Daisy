/**
 * Created by XR on 2017/4/12.
 */
(function () {

  var path = environment.globalPath

  var vm = new Vue({
    el: '#j-labor',
    data: {
      urls: {
        init: path + '/activity/labor/init',                  //页面初始化接口
        receive: path + '/activity/labor/receiveLabor',                  //页面初始化接口
      },

      init: {},
      logined: logined,
      whiteCoverShow: false,
      isShowTips: false,
      tipsList:['','活动即将开始','','领 取','明天再来','活动已结束'],
      tipsIndex: 0,

      coverShow: false,          //黑色蒙层
      showDialog: false,         //是否显示弹框

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

      $.xPost({
        url: self.urls.init,
        callback: function(data){
          if(data.success){
            self.initData(data.result);
          }else {
            console.log('页面初始化错误',data)
          }
        }
      });
    },
    methods: {
      // 非模态提示框
      openTipsFrame: function() {
        var self = this
        self.isShowTips = true
        setTimeout(function(){
          self.isShowTips = false
        },2000)
      },
      // 初始化数据
      initData: function (result) {
        var self = this
        self.init = {
          newDate: env.serverDate,
          startTime: result.startTime || '',   //活动开始时间
          endTime: result.endTime || '',       //活动结束时间
          invest: result.invest || 0,       //今日投资额
          signDate: result.signDate || [],       //签到日期
          giftsDate: result.giftsDate || [],       //领取日期
          receiveWorker: result.receiveWorker,      //劳模奖章
          receivePioneer: result.receivePioneer,      //先锋奖章
          receiveDedicated: result.receiveDedicated,      //敬业奖章
          workInvest: result.workInvest,               //领取礼包阈值
          workRight: result.workRight                 //是否能领取礼包
        };
          var start = self.init.startTime
          self.arr= [start,start + self.n,start + 2*self.n,start + 3*self.n,start + 4*self.n,start + 5*self.n,start + 6*self.n,start + 7*self.n]
          // self.init.signDate= [1490889600000,1490889600000 + 2*self.n,1490889600000 + 3*self.n]
          // self.init.giftsDate= [1490889600000,1490889600000 + 2*self.n,1490889600000 + 3*self.n]
          self.curTime = self.formatTime(self.init.newDate, 'yyyy/M/d')
          self.today = new Date(self.curTime).getTime();
          self.tipsList = ['','活动即将开始','累投≥'+self.init.workInvest+'元可领取礼包','领 取','明天再来','活动已结束']
          // self.today = 1490889600000 + 4*self.n;

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

        if (self.arr[7] < self.today){
          self.checkSign = 8
          self.giftSign = 8
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
      },
      goget: function () {
        var self = this;
        if (self.tipsIndex == 3){
          self.whiteCoverShow = true
          $.xPost({
            url: self.urls.receive,
            callback: function (data) {
              if (data.success) {
                  self.showDialog = true
                  self.coverShow = true
                  self.whiteCoverShow = false
                  self.tipsIndex = 4
                  self.canget = false
              }else {
                switch (data.resultCodeEum[0].code) {
                  case "90744":
                    self.openTipsFrame();
                    break;
                  default:
                    break;
                }
              }
            }
          })
        }
      },
      // 去登陆
      goLoginPage: function () {
        $.xPost({url: this.urls.receive})
      },
      close: function () {
        var self = this;
        self.showDialog = false;
        self.coverShow = false;
      },
      formatTime: function (date, format) {
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
    }
  })

  var newtext = $(".m-nav-btn");
  /*导航选中高亮*/
  var rightSlideActive = function rightSlideActive($index) {
    // newtext.removeClass('active').eq($index).addClass('active');
    $(".m-nav-btn").removeClass('active').eq($index).addClass('active');
  }

  /*悬浮导航点击*/
  $(".m-nav-btn").on('click', function () {
    var $index = $(this).index();
    $("html,body").animate({
      scrollTop: $('.m-body').eq($index).offset().top
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
    var menu = $("#j-labor");            //定义变量，抓取#menu
    var items = menu.find(".m-body");          //定义变量，查找.item
    var curId = "";                              //定义变量，当前所在的楼层item #id

    items.each(function (index) {
      var item = $(this);
      var itemTop = item.offset().top;        //定义变量，获取当前的top偏移量
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
    if (navTop < 440) {
      topNav.css({
        display: "none"
      })
    }else {
      topNav.css({
        display: "block"
      })
    }
    updateNavItem();
  });
  $(window).ready(function () {
    var navTop = $(this).scrollTop();

    if (navTop < 440) {
      topNav.css({
        display: "none"
      })
    }else {
      topNav.css({
        display: "block"
      })
    }
  });

})();