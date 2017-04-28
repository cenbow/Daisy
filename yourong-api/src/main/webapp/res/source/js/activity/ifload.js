define(['base'], function (require, exports, module) {
  'use strict'
  var base = require('base'),
    hook = new AppHook(os),
    logined = os > 2 ? $('#j-special').data('logined') : hook.logined,
    path = environment.globalPath,
    encryptionId = base.getUrlParam('encryptionId') || ''

  window.onload = function () {
    var oSpan = document.querySelectorAll("#value,#value1,#value2");
    var oName = document.getElementById("name");
    var oPer = document.getElementById("percent");
    var oLoad = document.getElementById("m-load");
    var oStart = document.getElementById("start");
    var oDown = document.querySelector(".m-down");
    var oEl = document.querySelectorAll("#f-show");
    var oLast = document.querySelectorAll("#f-last");
    var oTime = document.querySelectorAll(".f-time");
    var array = ["投资小鲜肉", "赚钱小达人", "掘金小能手", "理财老司机", "理财界神壕", "理财界神壕"];
    var percent = ["80%", "86%", "90%", "96%", "98%", "98%"];

    //下落
    down(oDown);
    function down(a_th) {
      a_th.style.animation = "Down 1s forwards ease-in-out";
    }

    //首页动画效果
    load(oLoad, oStart);
    function load(a_th, b_th) {
      a_th.style.animation = "rubberBand 1.5s 1s forwards ease-in-out";
      b_th.style.animation = "swing 0.5s 1s forwards ease-in-out";
    }

    oTime[0].innerHTML = new Date(Special.initData.startTime).getFullYear();
    if (logined) {
      //滑动翻页
      var mySwiper = new Swiper('.swiper-container', {

        loop: false,
        //翻页3D效果
        effect: 'coverflow',
        coverflow: {
          rotate: 30,
          stretch: 10,
          depth: 60,
          modifier: 2,
          slideShadows: true
        },
        //页面切换时callback
        onSlideChangeEnd: function (swiper) {
          if (swiper.activeIndex == 1) {
            member.start();
          }
          if (!Special.initData.myFirstInvestDate) {
            switch (swiper.activeIndex) {
              case 2:
                text(oEl[0], oEl[1], oEl[2], oEl[3], oEl[4], oEl[5], oEl[6]);
                break;
              case 3:
                last(oLast[0], oLast[1], oLast[2], oLast[3], oLast[4]);
                break;
              default:
                break;
            }
          } else {
            switch (swiper.activeIndex) {
              case 2:
                money.start();
                break;
              case 3:
                allmoney.start();
                break;
              case 4:
                active(oName);
                break;
              case 5:
                text(oEl[0], oEl[1], oEl[2], oEl[3], oEl[4], oEl[5], oEl[6]);
                break;
              case 6:
                last(oLast[0], oLast[1], oLast[2], oLast[3], oLast[4]);
                break;
              default:
                break;
            }
          }

        },
        //滑动结束时callback
        onTouchEnd: function (swiper) {
          if (swiper.activeIndex == 1) {
            member.start();
          }
          if (!Special.initData.myFirstInvestDate) {
            switch (swiper.activeIndex) {
              case 2:
                text(oEl[0], oEl[1], oEl[2], oEl[3], oEl[4], oEl[5], oEl[6]);
                break;
              case 3:
                last(oLast[0], oLast[1], oLast[2], oLast[3], oLast[4]);
                break;
              default:
                break;
            }
          } else {
            switch (swiper.activeIndex) {
              case 2:
                money.start();
                break;
              case 3:
                allmoney.start();
                break;
              case 4:
                active(oName);
                break;
              case 5:
                text(oEl[0], oEl[1], oEl[2], oEl[3], oEl[4], oEl[5], oEl[6]);
                break;
              case 6:
                last(oLast[0], oLast[1], oLast[2], oLast[3], oLast[4]);
                break;
              default:
                break;
            }
          }
        }
      })
    } else {
      return false
    }


    //数字跳动
    var options = {
      useEasing: true,
      easingFn: null,
      useGrouping: true,
      separator: '',
      decimal: '.'
    }
    var member = new CountUp("value", 0, Special.initData.myRank, 0, 2, options);
    var money = new CountUp("value1", 0, Special.initData.myTotalInvest, 2, 2, options);
    var allmoney = new CountUp("value2", 0, Special.initData.myTotalInvestInterest, 2, 2, options);

    //称号的变大变小
    function active(a_th) {
      a_th.style.animation = "jump 2s ease forwards";
    }

    //字逐行显示
    function text(a_th, b_th, c_th, d_th, e_th, f_th, g_th) {
      a_th.style.animation = "show 0.5s 0s forwards ease-in-out";
      b_th.style.animation = "show 0.5s 0.5s forwards ease-in-out";
      c_th.style.animation = "show 0.5s 1s forwards ease-in-out";
      d_th.style.animation = "show 0.5s 1.5s forwards ease-in-out";
      e_th.style.animation = "show 0.5s 2s forwards ease-in-out";
      f_th.style.animation = "show 0.5s 2.5s forwards ease-in-out";
      g_th.style.animation = "show 0.5s 3s forwards ease-in-out";
    }

    function last(a_th, b_th, c_th, d_th, e_th) {
      a_th.style.animation = "show 0.5s 0s forwards ease-in-out";
      b_th.style.animation = "show 0.5s 0.5s forwards ease-in-out";
      c_th.style.animation = "show 0.5s 1s forwards ease-in-out";
      d_th.style.animation = "show 0.5s 1.5s forwards ease-in-out";
      e_th.style.animation = "show 0.5s 2s forwards ease-in-out";
    }

    oName.innerHTML = array[Special.initData.myVipLevel];
    oPer.innerHTML = percent[Special.initData.myVipLevel];
  };

  /* 除掉移动端默认的点击图片出现全图浏览 */
  // $('img').on('click', function (e) {e.preventDefault()})


  var Special = new Vue({
    el: '#j-special',
    data: {
      loginUrl: path + '/mstation/login?from=' + location.href,
      logined: os > 2 ? logined : hook.logined,
      show: false,
      initData: {}
    },

    created: function () {
      var self = this
      if (self.logined) {
        if (receiveData.success) {

          //self.initData = receiveData.result
          init(receiveData.result)
        }
      }

      function init(result) {
        //alert(result.myTotalInvestInterest);
        self.initData = {
          startTime: result.registerDate || '',
          myRank: result.rank || '',
          myTotalInvest: result.totalInvest || '',
          myTotalInvestInterest: result.totalInvestInterest || '',
          myFirstInvestDate: result.firstInvestDate || '',
          myProjectName: result.projectName || '',
          myTotalDays: result.totalDays || '',
          myTransactionCount: result.transactionCount || '',
          myNumber: result.number || '0',
          myVipLevel: result.vipLevel || '0'
        }
      }
    },

    props: ['os'],

    methods: {
      start: function () {
        var self = this;
        //console.log(self.logined)
        if (!self.logined) {
          if (os < 3) {
            hook.login($(event.currentTarget))
          } else {
            self.goLoginPage()
          }
        } else if (self.logined) {
          var mySwiper = new Swiper('.swiper-container', {
            loop: false,
            //翻页3D效果
            effect: 'coverflow',
            coverflow: {
              rotate: 30,
              stretch: 10,
              depth: 60,
              modifier: 2,
              slideShadows: true
            }
          });
          mySwiper.slideNext();
          self.getTotalData();
        }
      },

      getTotalData: function () {
        var self = this;
        var options = {
          useEasing: true,
          easingFn: null,
          useGrouping: true,
          separator: '',
          decimal: '.'
        };
        var member = new CountUp("value", 0, self.initData.myRank, 0, 2, options);
        member.start()
      },

      // 去登陆
      goLoginPage: function () {
        location.href = this.loginUrl
        return false
      }
    }
  })
})

