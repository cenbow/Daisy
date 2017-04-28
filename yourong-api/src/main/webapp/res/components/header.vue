<template>
    <h2 class="u-pageTitle f-pos-r">
        <a v-link="{path:'/'}">{{title}}</a>
        <span class="u-login-link f-pos-a" v-if="logined">
            <a @click="logout">退出</a>
        </span>
        <span class="u-login-link f-pos-a" v-else>
            <a v-if="from" v-link="{path:'/user/login',query:{from:from}}">登录</a>
            <a v-else v-link="{path:'/user/login'}">登录</a>
            <a v-link="{path:'/user/register'}">注册</a>
        </span>
    </h2>
</template>
<script>
    export default {
        data () {
            let self = this
            return {
                siteurl: this.$root.siteurl + '/?spa',
                title: '有融网',
                logined: this.$root.logined,
                from:''
            }
        },
        watch:{
            'from': function (n) {
                location.reload()
            }
        },
        methods: {
            logout: function () {
                let self = this
                $.get(self.$root.siteurl + '/mCenter/logout', function (data) {
                    self.$root.logined = ''
                })
            }
        },
        events:{
            'logined': function (val) {
                this.logined = val
                location.reload()
            }
        }
    }
</script>