//倒计时
export const Countdown = class {
    /**
     * 项目预告倒计时
     * @param seconds {number}
     */
    static notice (seconds) {
        var hh = 0,
            mm = 0,
            ss = 0

        function setNoticeTime() {
            hh = numberAppendZero(Math.floor((seconds / 3600)))
            mm = numberAppendZero(Math.floor((seconds / 60) % 60))
            ss = numberAppendZero(Math.floor(seconds % 60))
            $("#j-project-notice-time").text(hh + ":" + mm + ":" + ss)
        }

        /**
         * 小于10在数字前追加0
         *
         * @param num
         * @returns {String}
         */
        function numberAppendZero(num) {
            if (num < 10) {
                num = "0" + num
            }
            return num
        }

        if (seconds > 0) {
            setNoticeTime()
        }
        var noticeProjectInterval = setInterval(function () {
            if (seconds > 0) {
                setNoticeTime()
            } else {
                clearInterval(noticeProjectInterval)
                var projectNotice = $("#j-project-notice")
                if (!projectNotice.hasClass("detail-notice")) {
                    projectNotice.text("立即投资")
                    projectNotice.removeClass("z-disabled")
                    projectNotice.addClass("z-actived")
                    projectNotice.attr("href", projectNotice.data("url"))
                } else {
                    projectNotice.removeClass("detail-notice")
                    $("#j-now-order").show()
                    projectNotice.hide()
                }
            }
            seconds--
        }, 1000)

        $(".j-index-notice").on("click", function () {
            var notice = $("#j-project-notice")
            if (!notice.hasClass("detail-notice") && notice.data("url") != "") {
                window.location.href = notice.data("url")
            }
        })
    }
}