/*
 *周年庆微信分享
 */
(function () {
    //console
    window.log = window.console ? console.log.bind(console) : function () {
    };

    //common method script
    $.extend({
        // Ajax Post Method
        xPost: function (config) {
            var type = "POST",
                datatype = "json",
                async = true;
            if (typeof config === "object") {
                type = config.type ? config.type : type;
                datatype = config.datatype ? config.datatype : datatype;
                async = config.async ? config.async : async;
                var xToken = $("#xToken").val();
                if (typeof xToken !== "undefined" && xToken !== "") {
                    if (config.hasOwnProperty("data")) {
                        config.data.xToken = xToken;
                    } else {
                        config.data = {
                            xToken: xToken
                        };
                    }
                }
            } else {
                throw new Error("xPost: config参数为空或不是对象");
            }
            $.ajax({
                type: type,
                url: config.url,
                data: config.data,
                dataType: datatype,
                async: async,
                headers: {
                    'X-Requested-Accept': 'json',
                    'Accept-Version': config.version ? config.version : '1.0.0'
                },
                success: config.callback,
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    // console.log(XMLHttpRequest);
                    // console.log(textStatus);
                    // console.log(errorThrown);
                },
                statusCode: {
                    403: function () {
                        alert('请刷新页面');
                    },
                    500: function () {
                        window.location.href = environment.globalPath + "/500";
                    }
                }

            });
        },
        /*
         * 本地存储封装,所有键值对存在localStorage.xStorage下
         * @param name {string} key name
         * @param value {string｜number} key value
         * @return get => {string|number}, set|del => {object},没有参数 => undefined
         */
        xStorage: function (name, value) {
            var sessionData = {},
                xStorageValue = localStorage.getItem('xStorage');
            if (xStorageValue) {
                sessionData = $.parseJSON(xStorageValue);
            } else {
                localStorage.setItem('xStorage', '{}');
            }

            var action = typeof(arguments[1]) === 'undefined' ? 'get' : (arguments[1] !== null ? 'set' : 'del');
            switch (action) {
                case 'get':
                    return $.type(name) === 'string' ? sessionData[name] : undefined;
                    break;

                case 'set':
                    sessionData[name] = value;
                    localStorage.setItem('xStorage', JSON.stringify(sessionData));
                    break;

                case 'del':
                    delete sessionData[name];
                    name ? localStorage.setItem('xStorage', JSON.stringify(sessionData)) : throwError('name');
                    break;

                default:
                    return sessionData;
            }

            function throwError(name) {
                throw new ReferenceError('xStorage: ' + name + ' is not defined');
            }

            return sessionData;
        }
    });


    var $btn = $('#j-receiveBtn'),
        $cpn = $('#j-cpn'),
        $showArea = $('#j-wx-show'),
        $inputArea = $('#j-wx-input'),
        phoneReg = /^13[0-9]{9}$|14[5,7]{1}[0-9]{8}$|15[0-9]{9}$|17[0,1,3,6,7,8]{1}[0-9]{8}$|18[0-9]{9}$/;

    var urlCode = location.href.substr(location.href.indexOf('?code=') + 6).split('&')[0];
    //a.提示输入手机号码
    $btn.on('click', function () {
        var cpn = $cpn.val();
        if (phoneReg.test(cpn)) {
            getPoint(cpn);
        } else {
            alert('请输入正确的手机号码');
        }
    });
    //b.读取缓存
    var anniversaryCache = $.xStorage('anniversaryCache'),
        keycode = anniversaryCache ? anniversaryCache.keycode : [];

    //c.保存后和后端交互
    function getPoint(cpn) {
        if (anniversaryCache && $.inArray(cpn + '=>' + urlCode, keycode) !== -1) {
            renderResultArea(anniversaryCache.data, cpn, true);
        }
        else {
            if (!$.xpostisloading) {
                $.xPost({
                    url: env.path + '/activity/anniversary/getRed',
                    data: {code: urlCode, mobile: cpn},
                    version: '1.0.0',
                    callback: function (data) {

                        if (data.success) {

                            $.xStorage('anniversaryCache', {data: data, keycode: keycode});
                            renderResultArea(data, cpn);

                        } else {
                            alert(data.resultCodes[0].msg);
                        }
                        $.xpostisloading = false;

                    }
                });
                $.xpostisloading = true;
            } else {
                //重复提交
            }
        }
    }

    function renderResultArea(data, cpn, cached) {
        var result = data.result;

        if (result) {

            var $point = $('#j-show-point');

            if (filter(result, '点人气值')) {

                var point = result.replace('点人气值', '');
                if (cached && point - 0) {
                    $point.parent().addClass('f-fs40').text('已抢过');
                } else {
                    $point.text(point);
                    keycode.push(cpn + '=>' + urlCode);
                }
            }
            else if (filter(result, '已抢过')) {

                $point.parent().addClass('f-fs40').text(result);

            }
            else if (filter(result, '您今日已领')) {

                $point.parent().addClass('f-fs16').text(result);

            }
            $('#j-show-cpn').text(cpn);
            $showArea.show();
            $inputArea.hide();
        }

        function filter(result, value) {
            return result.indexOf(value) !== -1;
        }
    }
})();
