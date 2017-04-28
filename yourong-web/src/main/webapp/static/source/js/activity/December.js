/**
 * Created by Administrator on 2016/12/6.
 */
(function () {
  $("#j-december").snowfall({
    image: '/static/img/activity/December/snow-new.png',
    flakeCount: 20,
    minSize: 30,
    maxSize: 60
  });

  // var base = require('base'),
  var logined = $('#j-total').data('logined'),
    path = environment.globalPath
  Vue.config.devtools = true

  window.vm = new Vue({
    el: '#j-december',
    data: {
      // 后端接口地址
      urls: {
        init: path + '/activity/double/init',
        receiveReward: path + '/activity/double/receive'
      },
      list: [],
      list1: [],
      list2: [],
      startTime: 0,
      endTime: 0,
      success: 0,
      myInvestAmount: 0,
      number: [4, 5, 6, 7, 8, 9, 10],
      activityStatus: 0,
      logined: logined,
      couponAmountnum: '',
      date: new Date().getTime(),
      dialogTips: '',
      dayTime: 0,
      nextday: 0,
      ten: 0,
      fifTeen: 0,
      hours: '',
      minutes: '',
      seconds: '',
      tenMinuteStatus: false,
      startGrabCash: false,
      startCountDown: false,
      nextfifteen: false,
      newdate: '',
      startDateA: '',
      startDateM: '',
      curTime: '',
      startTimeA: '',
      startTimeM: '',
      can: 0,
      click: true,
      startactive: false,
      endactive: false,
      get: 0,
      redRemind: 0
    },

    ready: function () {
      var self = this
      console.log(self.logined);
      $.xPost({
        url: self.urls.init,
        callback: function (data) {
          self.success = data.success
          console.log(data);
          if (data.success) {
            var result = data.result
            self.list = result.firstInvestAmount || []
            self.list1 = result.everyDayFirstInvestAmount || []
            self.list2 = result.countFirstAmountNumber || []
            self.endTime = result.endTime
            self.startTime = result.startTime
            self.activityStatus = result.status
            self.myInvestAmount = result.myInvestAmount || 0
            self.ten = result.ten
            self.fifTeen = result.fifTeen
            self.totalRed = result.totalRed
            self.redRemind = result.redRemind
            if (result.everyDayFirstInvestAmount.length > 0) {
              self.nextday = result.everyDayFirstInvestAmount[result.everyDayFirstInvestAmount.length - 1].dayTime + 24 * 60 * 60 * 1000
            }
            self.newdate = env.serverDate;
            self.startDateA = self.formatTime(self.newdate, 'yyyy-M-d') + " " + self.fifTeen + ":00:00";
            self.startDateM = self.formatTime(self.newdate, 'yyyy-M-d') + " " + self.ten + ":00:00";
            self.curTime = self.getSeconds(new Date(self.newdate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
            self.startTimeA = self.getSeconds(new Date(self.startDateA).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
            self.startTimeM = self.getSeconds(new Date(self.startDateM).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
            if ((self.curTime < self.startTimeM) || (self.activityStatus == 2)) {
              self.can = 1
            }
            if (self.formatTime(self.date, 'd') > 3){
              self.date = 1483372800000
            }
            // if (self.curTime > self.startTimeM && self.curTime < self.endTime) {
            //   self.click = false;
            // }
          }
        }
      })
    },
    // created: function () {
    //   var self = this;
    //
    // },
    computed: {
      champion: function () {
        var self = this;
        return self.splitContributionList(self.list1)
      },
      top: function () {
        var self = this;
        return self.splitContributionList(self.list)
      },
      // 抢红包倒计时
      TimeCount: function () {
        var activetip = "";
        var self = this;
        var seconds = 0;
        var flag = false;
        // var newdate = env.serverDate;
        // var startDateM = self.formatTime(newdate, 'yyyy-M-d') + " " + self.ten + ":00:00";
        // var startDateA = self.formatTime(newdate, 'yyyy-M-d') + " " + self.fifTeen + ":00:00";
        // var curTime = self.getSeconds(new Date(newdate).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        // var startTimeM = self.getSeconds(new Date(startDateM).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        // var startTimeA = self.getSeconds(new Date(startDateA).toJSON().split(/[TZ]/)[1]) + 3600 * 8;
        // console.log('ten:'+self.ten)

        //活动未开始
        if (self.activityStatus == 2) {
          //activetip = "活动即将开始"
          self.startactive = true;
        } else if (self.activityStatus == 6) {
          //activetip = "活动已结束"
          self.endactive = true;
          self.click = true;
        } else {
          self.startactive = false;
          self.endactive = false;
          if (self.formatTime(self.newdate, 'h') >= 0 && self.formatTime(self.newdate, 'h') < 8){
            self.startCountDown = true;
          }else {
            //早上活动开始
            if (self.curTime < self.startTimeM && self.startTimeM - self.curTime > 600) {
              //activetip = "每日10点、15点整点开抢"
              // self.can = 1
              self.startCountDown = true;
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeM - self.curTime - extraTime;
              console.log(extraSeconds)
              flag = false

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.countdown(parseInt(seconds))
                }
              }, 1000)

            } else if (self.curTime < self.startTimeM && self.startTimeM - self.curTime < 600) {
              seconds = self.startTimeM - self.curTime;
              self.countdown(parseInt(seconds));
              self.tenMinuteStatus = true;
            }
            if (self.curTime > self.startTimeM && self.startTimeA - self.curTime > 600 && self.redRemind > 0) {
              self.startGrabCash = true;
              self.get = 1;
              self.click = false;
              //activetip = "点我赢红包大礼"
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeA - self.curTime - extraTime;
              flag = false;

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.startGrabCash = false;
                  self.get = 0;
                  self.countdown(parseInt(seconds))
                  self.click = true;
                }
              }, 1000)
            } else if (self.curTime > self.startTimeM && self.startTimeA - self.curTime > 600 && self.redRemind == 0) {
              self.nextfifteen = true;
              //activetip = "下一场：15点开抢"
              var extraTime = 10 * 60;
              var extraSeconds = self.startTimeA - self.curTime - extraTime;
              flag = false;

              var exTimer = setInterval(function () {
                if (extraSeconds > 0) {
                  extraSeconds--
                } else {
                  clearInterval(exTimer);
                  seconds = 600;
                  self.startCountDown = false;
                  self.tenMinuteStatus = true;
                  self.nextfifteen = false;
                  self.countdown(parseInt(seconds))
                  self.click = true;
                }
              }, 1000)
            } else if (self.curTime < self.startTimeA && self.startTimeA - self.curTime < 600) {
              seconds = self.startTimeA - self.curTime;
              self.countdown(parseInt(seconds));
              self.tenMinuteStatus = true;
              self.click = true;
              //activetip = "开抢倒计时：09:08:25"
            } else if (self.curTime > self.startTimeA && self.redRemind > 0) {
              self.startGrabCash = true;
              self.get = 1;
              self.click = false;
              //activetip = "点我赢红包大礼"
            } else if (self.curTime > self.startTimeA && self.redRemind == 0) {
              self.startCountDown = true;
              //activetip = "每日10点、15点整点开抢"
            }
          }
        }
        return true
      }
    },
    watch: {
      "list": function () {
        var self = this
        self.scrollUserList({
          size: 5,
          height: -47,
          length: 1
        });
      }
    },
    methods: {
      scrollUserList: function (config) {
        $("#j-usersRankList").each(function () {
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
      },
      //倒计时
      fixZero: function (num) {
        return +num < 10 ? '0' + num : num
      },
      countdown: function (seconds) {
        var sec = seconds - 1,
          self = this

        count()

        var timer = setInterval(function () {

          if (sec > 0) {
            count()
          } else {
            clearInterval(timer);
            self.startGrabCash = true;
            self.tenMinuteStatus = false;
            self.click = false;
            self.get = 1;
          }
        }, 1000)

        function count() {
          var hours = parseInt(sec / 3600),
            minutes = parseInt((sec - hours * 3600) / 60)
          self.$set('hours', self.fixZero(hours))
          self.minutes = self.fixZero(minutes)
          self.seconds = self.fixZero(sec - hours * 3600 - minutes * 60)
          sec--
        }
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
      getSeconds: function (timeString) {
        var list = timeString.split(':')
        return list[0] * 3600 + list[1] * 60 + Math.ceil(list[2] * 1)
      },
      //去登录
      goLoginPage: function () {
        $.xPost({url: this.urls.receiveReward})
      },
      rule: function () {
        $(".u-rule-content").fadeIn();
      },
      hide: function () {
        $(".u-rule-content").fadeOut();
        $(".m-december-tip").fadeOut();
      },
      // 最后的列表
      splitContributionList: function (arr) {
        if (arr.length == 0) {
          return []
        } else {
          if (arr.length < 12) {
            var arrLength = arr.length
            for (var i = 0; i < 12 - arrLength; i++) {
              var day = arr[arrLength - 1].dayTime + 24 * 60 * 60 * 1000 * (i + 1);
              arr.push({
                dayTime: day,
                avatars: '',
                username: "虚位以待",
                totalInvest: ''
              })
            }
          }
          // return (_.chunk(arr, 5))[pos-1]
          return arr;
        }
      },
      grabred: function (event) {
        var self = this;
        if ((!self.logined) && self.click == false) {
          self.goLoginPage()
        } else {
          if (self.click || (self.activityStatus == 2) || (self.activityStatus == 6)) {
            return false
          } else {
            $.xPost({
              url: self.urls.receiveReward,
              // data: {
              //   invokeMethod: 'doubleReceiveCoupon'
              // },
              callback: function (data) {
                if (data.success) {
                  if (data.result.redRemind > 0) {
                    self.couponAmountnum = data.result.couponAmount;
                    self.dialogTips = "恭喜您获得" + self.couponAmountnum + "元现金券";
                  } else if (data.result.redRemind == 0 && (self.curTime < self.startTimeA)) {
                    self.couponAmountnum = data.result.couponAmount;
                    self.dialogTips = "恭喜您获得" + self.couponAmountnum + "元现金券";
                    self.nextfifteen = true;
                    self.startGrabCash = false;
                    self.get = 0;
                    self.click = true;
                  } else if (data.result.redRemind == 0 && (self.curTime > self.startTimeA)) {
                    self.couponAmountnum = data.result.couponAmount;
                    self.dialogTips = "恭喜您获得" + self.couponAmountnum + "元现金券";
                    self.startCountDown = true;
                    self.startGrabCash = false;
                    self.get = 0;
                    self.click = true;
                  }
                } else {
                  if (data.resultCodeEum[0].code == 94005) {
                    self.dialogTips = "每日限抢一次哦";
                  } else if (data.resultCodeEum[0].code == 94003 && (self.curTime < self.startTimeA)) {
                    self.nextfifteen = true;
                    self.startGrabCash = false;
                    self.get = 0;
                    self.click = true;
                    self.dialogTips = "已抢完";
                  } else if (data.resultCodeEum[0].code == 94003 && (self.curTime > self.startTimeA)) {
                    self.startCountDown = true;
                    self.startGrabCash = false;
                    self.get = 0;
                    self.click = true;
                    self.dialogTips = "已抢完";
                  } else {
                    self.dialogTips = "网络拥挤，过会再来哦";
                  }
                }
                $("#u-success").fadeIn();
              }
            })
          }
        }
      }
    }
  })
})()