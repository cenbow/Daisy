(function () {

    Vue.config.devtools = true

    var path = environment.globalPath

    window.vm = new Vue({
        el: '#j-modifyMobile',
        data: {
            step: window.initStep || 1,
            url: {
                sendCode: path + '/security/sendMobileCode',
                checkCode: path + '/security/checkMobileCode',
                checkMobile: path + '/security/checkMember',
                checkMemberInfo: path + '/member/validateMemberInfo',
                modifyByCaptcha: path + '/member/modifyMobileByCaptcha',
                modifyByIdentity: path + '/member/modifyMobileByIdentity',
                resetPage: path + '/security/resetPassword?step=3&type=',
                resetPassword: path + '/security/' +( window.validateType === 2 ? 'resetPasswordByIdentity' : 'resetPasswordByCaptcha')
            },
            validate: {
                type: 1,
                codeCorrect: 0,
                mobileCorrect: 0,
                passwordCorrect: 0,
                confirmPasswordCorrect: 0,
                realnameCorrect: 0,
                idcardCorrect: 0,
                loginPasswordCorrect: 0,
                realnameReg: /^[\u4E00-\u9FA5]{2,4}$/,
                pwdReg: /^(?![^a-zA-Z]+$)(?!\D+$).{6,16}$/,
                mobileReg: /^13[0-9]{9}$|14[57]{1}[0-9]{8}$|15[0-9]{9}$|17[013678]{1}[0-9]{8}$|18[0-9]{9}$/
            },
            tips: {
                errorCodeTips: '验证码错误',
                errorMobileTips: '请输入正确的手机号码',
                errorMobileNum: '请输入正确的手机号码',
                errorMobileExist: '手机号码已被注册',
                errorPassword: '密码错误',
                errorConfirm: '您两次输入的密码不一致',
                errorConfirmPassword: '您两次输入的密码不一致',
                getMobileCodeAgain: '重新获取验证码',
                errorRealname: '请输入正确的姓名',
                errorIdcard: '请填写正确的身份证'
            },
            codeBtn: {
                text: '获取验证码',
                status: 0
            },
            codeBtn2: {
                text: '获取验证码',
                status: 0
            },
            hasResetPassword: false
        },
        methods: {
            /**
             * 选择验证方式
             * @param type
             */
            selectValidateType: function (type) {
                this.validate.type = type
            },

            /**
             * 校验验证码位数
             * @param code
             */
            verifyMobileCode: function (code) {
                this.validate.codeCorrect = code.length != 4 ? -1 : 1
            },

            /**
             * 获取验证码
             * @param type
             * @returns {boolean}
             */
            getMobileCode: function (type) {
                var self = this,
                    btnName = type ? 'codeBtn2' : 'codeBtn',
                    status = self.$get(btnName + '.status')

                if (status < 0) {
                    return false
                }

                if (type && self.validate.mobileCorrect < 1) {
                    return false
                }

                var url = self.url.sendCode


                var data = {
                    type: 1,
                    isCheckMobile: type ? 3 : 4
                }

                if (type) {
                    data.mobile = this.validate.newMobile
                }

                $.xPost({
                    url: url,
                    data: data,
                    callback: function (data) {

                        var error = data.resultCodeEum
                        if (error && error[0].code === '3') {
                            $.xDialog({content: '今日发送短信超过次数'})
                        }else{
                            self.$set(btnName+'.status',-1)
                            self.timeCountdown(btnName, 60, self.tips.getMobileCodeAgain, 's')
                        }
                    }
                })
            },
            //修改手机号-手机号验证方式
            checkMobile: function () {
                var self = this,
                    isCorrect = this.validate.codeCorrect,
                    url = self.url.checkCode,
                    vercode = self.validate.vercode

                if (isCorrect > 0) {
                    $.xPost({
                        url: url,
                        data: {
                            type: 1,
                            code: vercode,
                            checkType: 1
                        },
                        callback: function (data) {
                            self.validate.codeCorrect = data.success ? 1 : -1
                            if (data.success) {
                                self.step = 2
                                self.codeBtn.text = '获取验证码'
                                self.codeBtn.status = 0
                            }else{
                                self.validate.vercode=''
                            }
                        }
                    })
                } else if (isCorrect === 0) {
                    this.validate.codeCorrect = -1
                }
            },

            /**
             * 修改手机号-身份信息验证
             * @param name 真实姓名
             * @param idcard 身份证号码
             * @param password 登录密码
             */
            checkIdentity: function (name, idcard, password) {
                var self = this,
                    v = self.validate
                if (v.realnameCorrect && v.idcardCorrect && v.loginPasswordCorrect) {
                    $.xPost({
                        url: self.url.checkMemberInfo,
                        data: {
                            trueName: self.validate.realname,
                            identityNumber: self.validate.idcard,
                            password: self.validate.loginPassword
                        },
                        callback: function (data) {
                            if (data.success) {
                                self.step = 2
                            } else {
                                $.xDialog({content: data.resultCodeEum[0].msg})
                            }
                        }
                    })
                }
            },

            modifyMobileByIdentity: function () {
                var self = this,
                    newMobile = self.validate.newMobile,
                    captcha = self.validate.newVercode

                if (self.validate.codeCorrect && self.validate.mobileCorrect) {
                    $.xPost({
                        url: self.url.modifyByIdentity,
                        data: {
                            newMobile: newMobile,
                            captcha: captcha,
                            trueName: self.validate.realname,
                            identityNumber: self.validate.idcard
                        },
                        callback: function (data) {
                            self.validate.codeCorrect = data.success ? 1 : -1
                            if (data.success) {
                                self.gotoResetPage()
                            }
                        }
                    })
                }
            },

            modifyMobileByCaptcha: function () {
                var self = this,
                    newMobile = self.validate.newMobile,
                    captcha = self.validate.newVercode

                if (self.validate.codeCorrect && self.validate.mobileCorrect) {
                    $.xPost({
                        url: self.url.modifyByCaptcha,
                        data: {
                            newMobile: newMobile,
                            captcha: captcha
                        },
                        callback: function (data) {
                            self.validate.codeCorrect = data.success ? 1 : -1
                            if (data.success) {
                                self.gotoResetPage()
                            }
                        }
                    })
                }
            },

            gotoResetPage: function () {
                var self= this
                self.step = 3
                window.location.href = self.url.resetPage + self.validate.type || 1
            },

            /**
             * 校验新手机号码
             * @param mobile
             */
            verifyNewMobile: function (mobile) {
                var self = this,
                    mobileCorrect = this.validate.mobileCorrect =
                        this.validate.mobileReg.test(mobile) ? 1 : -1


                if (mobileCorrect > 0) {
                    $.xPost({
                        url: self.url.checkMobile,
                        data: {
                            type: 1,
                            code: mobile
                        },
                        callback: function (data) {
                            var correct = 0,
                                code = data.resultCodeEum[0].code

                            switch (code) {
                                case '90002':
                                    correct = 1
                                    break
                                case '90018':
                                    correct = -1
                                    self.tips.errorMobileTips = self.tips.errorMobileExist
                                    break
                            }

                            self.validate.mobileCorrect = correct
                        }
                    })
                } else {
                    self.tips.errorMobileTips = self.tips.errorMobileNum
                }
            },

            /**
             * 校验密码格式
             * @param pwd
             * @param type
             */
            verifyPassword: function (pwd, type) {
                var pwdCorrect = this.validate.pwdReg.test(pwd),
                    password = this.validate.password

                if (!type) {
                    this.validate.passwordCorrect = pwdCorrect ? 1 : -1
                    var confimPassword = this.validate.confirmPassword

                    if (pwdCorrect && (password ===
                        this.validate.confirmPassword || !confimPassword)) {
                        this.validate.confirmPasswordCorrect = 1
                    } else {
                        if (confimPassword) {
                            if (password === this.validate.confirmPassword) {
                                this.tips.errorConfirm = this.tips.errorPassword
                            } else {
                                this.tips.errorConfirm = this.tips.errorConfirmPassword
                            }

                            this.validate.confirmPasswordCorrect = -1
                        }
                    }
                } else {
                    if (pwdCorrect && pwd === password) {
                        this.validate.confirmPasswordCorrect = 1
                    } else {
                        if (pwdCorrect) {
                            this.tips.errorConfirm = this.tips.errorConfirmPassword
                        } else if (pwd === password) {
                            this.tips.errorConfirm = this.tips.errorPassword
                        }
                        this.validate.confirmPasswordCorrect = -1
                    }
                }
            },

            /**
             * 重置密码
             * @param pwd
             * @param confirmPwd
             * @returns {boolean}
             */
            resetPassword: function (pwd, confirmPwd) {
                var self = this,
                    v = self.validate

                //拦截密码错误
                if (!pwd || !v.passwordCorrect) {

                    if (!pwd) {
                        v.passwordCorrect = -1
                    }
                    return false
                }

                //拦截重复密码错误
                if (!confirmPwd || !v.confirmPasswordCorrect) {

                    if (!confirmPwd) {
                        v.confirmPasswordCorrect = -1
                        self.tips.errorConfirm = self.tips.errorPassword
                    }
                    return false
                }

                if (!self.hasResetPassword) {
                    self.hasResetPassword = true
                } else {
                    return false
                }

                $.xPost({
                    url: self.url.resetPassword,
                    data: {
                        password: pwd,
                        repassword: confirmPwd
                    },
                    callback: function (data) {
                        if (data.success) {
                            vm.step = 4
                            v.password = v.confirmPassword = ''
                        } else {
                            $.xDialog({content: data.resultCodeEum[0].msg})
                        }
                        setTimeout(function () {
                            self.hasResetPassword = false
                        }, 2000)
                    }
                })
            },

            /**
             * 校验身份信息
             * @param val
             * @param type
             */
            verifyIdentity: function (val, type) {
                var self = this,
                    v = self.validate
                switch (type) {
                    case 1:
                        v.realnameCorrect = v.realnameReg.test(val) ? 1 : -1
                        break
                    case 2:
                        v.idcardCorrect = self.verifyIdcard(val) ? 1 : -1
                        break
                    case 3:
                        v.loginPasswordCorrect = v.pwdReg.test(val) ? 1 : -1
                        break
                }
            },

            /**
             * 验证合格的身份证号码
             * @param num 身份证号码
             * @return boolean
             */
            verifyIdcard: function (num) {
                var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1]; // 加权因子;
                var ValideCode = [1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2]; // 身份证验证位值，10代表X;

                if (num.length == 15) {
                    return isValidityBrithBy15IdCard(num)
                } else if (num.length == 18) {
                    var a_idCard = num.split("") // 得到身份证数组

                    return isValidityBrithBy18IdCard(num) && isTrueValidateCodeBy18IdCard(a_idCard)
                }
                return false

                function isTrueValidateCodeBy18IdCard(a_idCard) {
                    var sum = 0; // 声明加权求和变量
                    if (a_idCard[17].toLowerCase() == 'x') {
                        a_idCard[17] = 10; // 将最后位为x的验证码替换为10方便后续操作
                    }
                    for (var i = 0; i < 17; i++) {
                        sum += Wi[i] * a_idCard[i] // 加权求和
                    }
                    var valCodePosition = sum % 11; // 得到验证码所位置

                    return a_idCard[17] == ValideCode[valCodePosition]
                }

                function isValidityBrithBy18IdCard(idCard18) {
                    var year = idCard18.substring(6, 10)
                    var month = idCard18.substring(10, 12)
                    var day = idCard18.substring(12, 14)
                    var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day));
                    // 这里用getFullYear()获取年份，避免千年虫问题
                    return !(temp_date.getFullYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day))
                }

                function isValidityBrithBy15IdCard(idCard15) {
                    var year = idCard15.substring(6, 8)
                    var month = idCard15.substring(8, 10)
                    var day = idCard15.substring(10, 12)
                    var temp_date = new Date(year, parseFloat(month) - 1, parseFloat(day))
                    // 对于老身份证中的你年龄则不需考虑千年虫问题而使用getYear()方法
                    return !(temp_date.getYear() != parseFloat(year) || temp_date.getMonth() != parseFloat(month) - 1 || temp_date.getDate() != parseFloat(day))
                }
            },

            /**
             * 倒计时
             * @param name
             * @param seconds
             * @param text
             * @param symbol
             */
            timeCountdown: function (name, seconds, text, symbol) {
                var self = this,
                    sec = seconds - 1,
                    str = symbol || ''

                var timer = setInterval(function () {
                    var status = self.$get(name + '.status')

                    if (sec > 0) {
                        if(status !== 0){
                            self.$set(name + '.text', sec + str)
                            sec--
                        }else{
                            clearInterval(timer)
                            self.$set(name + '.status', 0)
                            self.$set(name + '.text', '获取验证码')
                        }
                    } else {
                        clearInterval(timer)
                        if (status !== 0) {
                            self.$set(name + '.status', 1)
                            if (text) {
                                self.$set(name + '.text', text)
                            }
                        }
                    }

                }, 1000)
            }
        }
    })

})()