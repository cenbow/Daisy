define(['base'], function (require, exports, module) {
  'use strict'
  var base = require('base'),
    logined = $('#public').data('logined'),
    path = environment.globalPath,
    invoke = path + '/activity/dynamicInvoke',
    hook = new AppHook(os),
    encryptionId = base.getUrlParam('encryptionId') || ''
  window.vm = new Vue({
    el: '#public',
    data: {
      // 后端接口地址
      urls: {
        support: invoke
      },
      loginUrl: path + '/mstation/login?from=' + location.href + '#abc',
      logined: os > 2 ? logined : hook.logined,
      encryptionId: encryptionId,
      show: 0,
      current: 1,
      memberList: {},
      initData: {},
      clicked: false,
      point: ''
    },

    created: function () {
      var self = this
      window.scrollTo(0, 0)
      if (receiveData.success) {
        init(receiveData.result)
      }

      function init(result) {
        self.initData = {
          startTime: result.startTime || '',
          endTime: result.endTime || '',
          received: result.received || '',
          memberList: result.memberList || ''
        }
        self.point = result.point || '0'
      }

      if (self.initData.received) {
        self.clicked = true
      } else {
        self.clicked = false
      }
      window.hookCallback = function (data, eventName) {
        //console.log('data:', data)
        self.Callback(data)
      }
    },
    ready: function () {
      var self = this
      this.memberList = this.initData.memberList;
      window.onhashchange = function () {
        if (location.hash == '') {
          window.scrollTo(0, 0)
          $('#g-activity').hide()
          $('#g-look').show()
        } else {
          return false
        }

      }

    },
    props: ['os'],
    watch: {
      "memberList": function () {
        var self = this
        self.scrollUserList({
          size: 5,
          height: -40,
          length: 1
        });
      }
    },
    methods: {
      timeAgo: function (timestring) {
        var time = (environment.serverDate - timestring) / 1E3,
          ago = 0
        switch (true) {
          case time / 3600 > 1:
            ago = parseInt(time / 3600) + '小时'
            break
          case time / 3600 < 1 && time / 60 >= 1:
            ago = parseInt(time / 60) + '分钟'
            break
          case time / 60 < 1:
            // ago = time + '秒'
            ago = '1分钟'
            break
        }
        return ago
      },
      start: function (event) {
        console.log(new Date().getTime())
        var self = this;
        window.scrollTo(0, 0)
        if (!self.logined) {
          if (os < 3) {
            hook.login($(event.currentTarget))
          } else {
            self.goLoginPage()
          }

        } else {
          if (new Date().getTime() > self.initData.endTime) {
            $('#g-look,#g-wait,#g-sup').hide();
            $('.g-activity-wait,#g-stop').show();
          } else if (new Date().getTime() > self.initData.startTime) {
            $('#g-look,#g-wait,#g-stop').hide();
            $('.g-activity-wait,#g-sup').show();
          } else if (new Date().getTime() < self.initData.startTime) {
            $('#g-look,#g-stop,#g-sup').hide();
            $('.g-activity-wait,#g-wait').show();
          }
        }
      },

      know: function () {
        // 禁止冒泡，使底部页面不可以滚动
        $('html,body').css({'overflow': 'hidden', 'height': '100%'});
        $(".m-know-apple").show();
        base.cover.show();
      },

      close: function () {
        // 放开控制，可以滚动
        $('html,body').css({'overflow': 'visible', 'height': '100%'});
        $(".m-know-apple").hide();
        base.cover.hide();
      },

      hide: function () {
        $(".m-alert-d,.m-alert-s").hide();
        base.cover.hide();
      },

      hidel: function () {
        $(".m-alert-l").hide();
        base.cover.hide();
        location.reload();
        $('#g-look,#g-wait,#g-sup').hide();
        $('.g-activity-wait,#g-stop').show();
      },

      support: function (event) {
        var self = this,
          receive = self.initData.received;
        if (os != 3) {
          hook.getEvent('supportWelfare' + '&isNeedRealName=0&' + '', $(event.currentTarget))
        } else {
          base.getAPI({
            url: self.urls.support,
            data: {
              invokeMethod: 'supportWelfare'
            },
            callback: function (data) {
              self.Callback(data)
              }
          })
        }
      },

      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl
        return false
      },
      traggleBtn: function (type) {
        var self = this
        if (this.current == type) {
          self.current = 0
          self.show = 1
        } else {
          self.current = type
          self.show = 0
        }
      },
      scrollUserList: function (config) {
        $("#j-winnerList").each(function () {
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
      Callback: function (data) {
        var self = this
        if (data.success) {
            self.point = data.result.point;
            self.clicked = true
            $(".m-thank").show();
            $(".m-support").hide();
            $(".m-alert-s").show();
            base.cover.show();
        } else {
          self.clicked = false
          if (data.resultCodes[0].code == 90702) {
            $(".m-alert-l").show();
          } else {
            $(".m-alert-d").show();
          }
          base.cover.show();
        }
      }
    }
  })

})
