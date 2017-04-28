
__webpack_public_path__ = env.path + '/res/build/entry/'

Vue.config.devtools = true

import Swiper from '../../../assets/lib/swiper-min.js'

//路由器根组件
var App = Vue.extend({
    data:{
        stopHistory:false
    },
    components: {
        'mySwiper': Swiper
    },
    props: ['platform', 'siteurl', 'resurl','alipath']
})

//路由器实例
var router = new VueRouter({
    root:location.href
})

// 路由规则
router.map({
    '/':{
        name:'home',
        component:(resolve)=>{
            require.ensure([],()=>{
             resolve(require('../../../components/popularityPark/home.vue'))
            },'pPark.home')
        }
    },
    // '/memberCenter':{
    //     name:'memberCenter',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/memberCenter.vue'))
    //         },'pPark.memberCenter')
    //     }
    // },
    '/popularGameNine':{
        name:'popularGameNine',
        component:(resolve)=>{
            require.ensure([],()=>{
                resolve(require('../../../components/popularityPark/popularGameNine.vue'))
            },'pPark.popularGameNine')
        }
    },
    // '/allProductList':{
    //     name:'allProductList',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/allProductList.vue'))
    //         },'pPark.allProductList')
    //     }
    // },
    // '/entityProductList': {
    //     name: 'entityProductList',
    //     component: (resolve)=> {
    //         require.ensure([], ()=> {
    //             resolve(require('../../../components/popularityPark/entityProductList.vue'))
    //         }, 'pPark.entityProductList')
    //     }
    // },
    // '/fictitiousProductList': {
    //     name: 'fictitiousProductList',
    //     component: (resolve)=> {
    //         require.ensure([], ()=> {
    //             resolve(require('../../../components/popularityPark/fictitiousProductList.vue'))
    //         }, 'pPark.fictitiousProductList')
    //     }
    // },
    // '/productDetail':{
    //     name:'productDetail',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/productDetail.vue'))
    //         },'pPark.productDetail')
    //     }
    // },
    // '/birthdayGift':{
    //     name:'birthdayGift',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/birthdayGift.vue'))
    //         },'pPark.birthdayGift')
    //     }
    // },
    // '/upgradePacks':{
    //     name:'upgradePacks',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/upgradePacks.vue'))
    //         },'pPark.upgradePacks')
    //     }
    // },
    // '/membershipPrivileges':{
    //     name:'membershipPrivileges',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/membershipPrivileges.vue'))
    //         },'pPark.membershipPrivileges')
    //     }
    // },
    // '/growthSystem':{
    //     name:'growthSystem',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/growthSystem.vue'))
    //         },'pPark.growthSystem')
    //     }
    // },
    // '/exchangeRecord':{
    // name:'exchangeRecord',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/exchangeRecord.vue'))
    //         },'pPark.exchangeRecord')
    //     }
    // },
    // '/exchangeOrderInfo':{
    //     name:'exchangeOrderInfo',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/exchangeOrderInfo.vue'))
    //         },'pPark.exchangeOrderInfo')
    //     }
    // },
    // '/eputationRecord':{
    //     name:'eputationRecord',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/eputationRecord.vue'))
    //         },'pPark.eputationRecord')
    //     }
    // },
    // '/growthRecode':{
    //     name:'eputationRecord',
    //     component:(resolve)=>{
    //         require.ensure([],()=>{
    //             resolve(require('../../../components/popularityPark/growthRecode.vue'))
    //         },'pPark.growthRecode')
    //     }
    // }
    // '/preferentialProject': {
    //     name: 'preferentialProject',
    //     component: (resolve)=> {
    //         require.ensure([], ()=> {
    //             resolve(require('../../../components/popularityPark/preferentialProject.vue'))
    //         }, 'pPark.preferentialProject')
    //     }
    // }
})
window.hook = new AppHook(os)
window.hookCallback=function (data,method) {
    console.log('globalCallback:',data,'method:',method)
    router.app.$broadcast('methodCallback',{data:data,method:method})
}
router.start(App, '#app')