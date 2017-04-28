define(['base', 'vue','zepto'], function (require, exports, module) {
    'use strict'
    var base = require('base');
    new Vue({
        el:'#j-setEsignature',
        data:{
            isSigned:1,
            signWay:'',
            name:''
        },
        created:function () {
            var _this=this;
            base.getAPI({
                url:environment.globalPath+'/security/evalua/getSignInfo',
                version:'1.6.0',
                callback:function (data) {
                    if(data.success){
                        _this.signWay=data.result.signWay
                        _this.name=data.result.name
                    }else{
                        base.xTips({content:data.resultCodes[0].msg})
                    }
                }

            })
        },
        methods:{
            setSign:function (     ) {
                var _this=this;
                var setSignWay=_this.signWay;
                if(setSignWay==0){

                    setSignWay=1

                }else {

                    setSignWay=0

                }
              base.getAPI({
                  url:environment.globalPath+'/security/member/saveSignWay',
                  version:'1.6.0',
                  data:{signWay:setSignWay},
                  callback:function (data) {
                      if(data.success){
                          _this.signWay=setSignWay
                      }else{
                          base.xTips({
                              content:data.resultCodes[0].msg
                          })
                      }
                  }
              })
            }
        }
    })
})
