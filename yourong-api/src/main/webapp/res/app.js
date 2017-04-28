Vue.config.devtools = true

import Header from './components/header.vue'
import Navi from './components/navi.vue'

let log = console.log.bind(console)

if(env.debug){
    __webpack_public_path__ = 'http://localhost:8080/build/'
}else{
    __webpack_public_path__ = env.siteurl+'/res/build/'
}


//路由器根组件
var App = Vue.extend({
    components: {
        'app-header': Header,
        'app-navi': Navi
    },
    props: ['logined', 'siteurl', 'openIdisBind'],
    watch:{
        'logined': function (val) {
            this.$broadcast('logined',val)
        }
    }
})

//模块组件
var Projects = Vue.extend({
    template: '<router-view></router-view>'
})
var Member = Vue.extend({
    template: '<router-view></router-view>'
})
var User = Vue.extend({
    template: '<router-view></router-view>'
})

//路由器实例
var router = new VueRouter({
    root:env.siteurl+'/'
})

// 路由规则
router.map({
    '/': {
        name: 'index',
        component: function (resolve) {
            require(['components/index.vue'], resolve)
        }
    },
    '/projects': {
        name: 'projects',
        component: Projects,
        subRoutes: {
            '/list': {
                component: function (resolve) {
                    require(['components/projects/list.vue'], resolve)
                }
            }
        }
    },
    '/member': {
        name: 'member',
        component: Member,
        subRoutes: {
            '/home': {
                component: function (resolve) {
                    require(['components/member/home.vue'], resolve)
                }
            },
            '/security': {
                component: function (resolve) {
                    require(['components/member/security.vue'], resolve)
                }
            },

            '/password': {
                component: function (resolve) {
                    require(['components/member/security/password.vue'], resolve)
                }
            },
            '/personal': {
                component: function (resolve) {
                    require(['components/member/security/personal.vue'], resolve)
                }
            },
            '/detail': {
                component: function (resolve) {
                    require(['components/member/security/detail.vue'], resolve)
                }
            }

        }
    },
    '/user':{
        name:'user',
        component:User,
        subRoutes:{
            '/login':{
                     component:function(resolve){
                             require(['components/user/login.vue'],resolve)
                     }
            },
            '/register':{
                component:function(resolve){
                    require(['components/user/register.vue'],resolve)
                }
            }
        }
    }
})

//路由预处理
router.beforeEach(function (transition) {
    let name = transition.to.name

    //设置全局导航条高亮
    if (name) {
        router.app.$broadcast('currentNavi', name)
    }

    //未登录强制跳转(应跳转到登录页,并可以跳回)
    if (name === 'member' && !router.app.logined) {
        transition.abort()
        router.go('/user/login')
        router.app.$broadcast('currentNavi', 'user')
    } else if(name === 'user' && router.app.logined){
        transition.abort()
        router.go('/member/home')
        router.app.$broadcast('currentNavi', 'member')
    } else{
        transition.next()
    }
})

router.start(App, '#app')