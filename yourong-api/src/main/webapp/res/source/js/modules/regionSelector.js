/*global define,require,$,environment,alert,env,globalPath,Vue,log*/
/* jshint undef: true, asi: true, boss: true */
define(['base'], function (require, exports, module) {
    "use strict"
    var base = require('base')

    var regionSelector = function ($selector, areaCode) {
        this.$selector = $selector
        this.areaCode = areaCode
    }

    regionSelector.prototype = {

        /**
         * 初始化组件
         * @param callback
         */
        init: function (callback) {
            var self = this,
                code = !self.areaCode ? 1 : self.areaCode

            //初次加载
            this.getRegionList(self.$selector,code)

            //数据变化处理
            self.$selector.on('click', 'div', function (event) {
                event.stopPropagation()
                self.regionChangeHandle($(this),callback)
            })
        },

        /**
         * 地区变化处理
         * @param $target
         * @param callback
         */
        regionChangeHandle: function ($target,callback) {
            var areaData=$target.data(),
                areaCode=areaData.code,
                self=this

            if(areaCode%1E4!==0){
                var parentName=areaCode%100?'fullname':'name'
                $target.data('fullname',
                    $target.parent().data(parentName)+$target.data('name')
                )
            }

            if(areaCode%100===0){
                var childNodes=$target.find('div')
                if(childNodes.length){
                    if($target.data('expanded')){
                        childNodes.hide()
                        $target.data('expanded',false).removeClass('z-expanded')
                    }else{
                        childNodes.show()
                        $target.data('expanded',true).addClass('z-expanded')
                    }
                }else{
                    self.getRegionList($target,areaCode)
                    $target.data('expanded',true).addClass('z-expanded')
                }
            }else{
                //数据处理
                callback(areaCode,$target.data('fullname'))
            }
        },

        /**
         * 渲染地区列表
         * @param $target
         * @param data
         * @param code
         */
        renderRegionList: function ($target, data, code) {
            var type = this.getRegionLevelByCode(code),
                html = [],
                dataList = data

            if (!code) {
                return false
            }

            if (type !== 'province') {

                data.forEach(function (item) {

                    if (item.code === code) {
                        dataList = item.list
                    }
                })
            }

            dataList.forEach(function (item) {
                html.push('<div data-code="' + item.code +
                    '" data-name="'+item.name+'" class="u-region-'+type+'">' +
                    item.name + '</div>')
            })

            $target.append(html.join(''))
        },

        /**
         * 异步获取地区列表
         * @param $target
         * @param areaCode
         * @returns {boolean}
         */
        getRegionList: function ($target,areaCode) {

            var storagedList = this.getStoragedRegionList(areaCode),
                self = this

            if (!areaCode) {
                return false
            }

            if (storagedList) {
                var isStoraged = false
                storagedList.forEach(function (item) {
                    if (item.code === areaCode) {

                        self.renderRegionList($target, storagedList, areaCode)
                        isStoraged = true
                    }
                })
                if (isStoraged) {
                    return false
                }
            }
            $.ajax({
                type: 'GET',
                url: environment.globalPath + "/area/getAreaList",
                data: {code: areaCode},
                dataType: 'json',
                success: function (data) {
                    if (data.success) {
                        self.storageRegionList(data.resultList, areaCode)

                        self.renderRegionList($target, data.resultList, areaCode)
                    } else {
                        log('省市区数据加载失败')
                    }
                }
            })
        },

        /**
         * 根据地区代码获取地区级别
         * @param code
         * @returns {string}
         */
        getRegionLevelByCode: function (code) {
            var type = 'province'
            if (+code !== 1) {
                if (code % 1E4) {
                    type = 'area'
                } else {
                    type = 'city'
                }
            }
            return type
        },

        /**
         * 缓存地区列表
         * @param data
         * @param code
         * @returns {{}}
         */
        storageRegionList: function (data, code) {
            var regionList = {},
                list = [],
                storage = {},
                type = this.getRegionLevelByCode(code)

            data.forEach(function (item) {
                list.push({code: item.code - 0, name: item.name})
            })

            var storageList = base.storage('regionList'),
                pushList = {"code": code, "list": list}

            if (storageList) {

                if (type !== 'province') {

                    if (storageList[type]) {
                        var flag = false

                        storageList[type].forEach(function (item) {
                            if (item.code === code) {
                                flag = true
                            }
                        })

                        if (!flag) {
                            storageList[type].push(pushList);
                        }

                    } else {
                        storageList[type] = []
                        storageList[type].push(pushList)
                    }

                } else {
                    storageList[type] = list
                }

                base.storage('regionList', storageList)

                storage = storageList

            } else {

                if (type === 'province') {
                    regionList[type] = list
                } else {
                    regionList[type] = []
                    regionList[type].push(pushList)
                }

                base.storage('regionList', regionList)

                storage = regionList
            }
            return storage
        },

        /**
         * 获取缓存的地区列表
         * @param code
         * @returns {*}
         */
        getStoragedRegionList: function (code) {
            var type = this.getRegionLevelByCode(code),
                storageList = base.storage('regionList'),
                regionList = null

            if (storageList) {
                if (type === 'province') {

                    if (storageList[type]) {
                        var index = -1
                        storageList[type].forEach(function (item, i) {
                            if (item.code === code) {
                                index = i
                            }
                        })
                        if (index >= 0) {
                            regionList = storageList[type][index]
                        }
                    }
                } else {
                    regionList = storageList[type]
                }
            }

            return regionList
        }
    }

    module.exports = regionSelector
})