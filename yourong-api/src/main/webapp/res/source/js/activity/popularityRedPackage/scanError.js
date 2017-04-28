/**
 * cc add 这里春节在初期获取下红包码而已，之后可以删除的 // TODO
 */

define(['base'],function (require, exports, module) {
    console.log('jo')


    var base=require('base')

    base.getAPI({
        url: environment.globalPath + '/security/activity/redBag/shareUrl',
        callback: function(data){
            console.log(data)
        }
    })

})
