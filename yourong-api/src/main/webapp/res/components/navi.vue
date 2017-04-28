<template>
    <ul class="ui-grid-trisect u-navi">
        <li :class="{'z-current ui-border-b':current==='index'}">
            <a v-link="{path:'/'}">首页</a>
        </li>
        <li :class="{'z-current ui-border-b':current==='projects'}">
            <a v-link="{path:'/projects/list'}">理财项目</a>
        </li>
        <li :class="{'z-current ui-border-b':current==='member'}" >
            <a v-link="{path:'/member/home'}">我的账户</a>
        </li>
    </ul>
</template>
<script>
    export default {
        data: function () {
            let currentNavi = this.$root.currentNavi||'index'
            return {
                current:currentNavi
            }
        },
        events:{
            'currentNavi': function (name) {
                this.$set('current',name)
            }
        }
    }
</script>