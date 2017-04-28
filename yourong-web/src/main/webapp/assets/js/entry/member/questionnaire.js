/**
 * @entry 风险评估
 */

import {Util} from 'common/util'
import {Dialog} from 'module/cube'

const $question = $('#j-questionnaire-question')


//第一屏滑动到一定高度添加动画
//滚动条事件
$(window).on('scroll', function () {
    //获取滚动条的滑动距离
    var scroH = $(this).scrollTop();
    if (scroH > 60) {
        $('#j-first-li').addClass('z-current')
    }
})

//联动选中
$('.j-qn-chart').on('click', 'span', function () {
    const i = $(this).index()
    $(this).parents('.u-questionnaire-question').find('label').eq(i).click()
})

$('.j-qn-text').on('click', function () {
    $(this).siblings('label').click()
})


$question.on('change','input[type=radio]',function () {

    if ($(this).is(':checked')) {
        $(this).parent().addClass('z-current')
            .siblings().removeClass('z-current')
            .end().parent().addClass('z-current')
            .siblings().removeClass('z-current')
    }

    $(this).closest('.u-questionnaire-question')
        .siblings().find('input[type=radio]').each(function () {

        if ($(this).is(':checked')) {

            if ($(this).closest('li').data('page') != 4) {
                var i = $(this).closest('li').data('page')

                $('.u-questionnaire-page').find('span').eq(i).addClass('z-current')
                    .siblings().removeClass('z-current')

                $(this).closest('li').next().addClass('f-db z-current')
                    .siblings().removeClass('f-db z-current')
            } else {
                $('#j-submit-gray').addClass('f-dn')
                $('#j-submit-red').removeClass('f-dn')
            }
        }
    })
})

let $finishBtn = $('#j-submit-red'),resultCount = 0
$finishBtn.on('click', function () {
    countAndLook()
    saveResult(resultCount)
    $('#j-qn-list').addClass('f-dn').next('div').removeClass('f-dn')
})

let saved = false
const saveResult =(count)=> {

    if(saved){
        return false
    }

    Util.post({
        url:'/member/saveEvaluation',
        data:{evaluationScore:count},
        callback: function (data) {

            if(data.success){
                log({content:'saveEvaluation success!'})
                saved = true
            }else{
                Dialog.show(data.resultCodeEum[0].msg)
            }
        }
    })
}

//评测结果
let countAndLook = ()=> {
    let radio = 'box_',
        count = 0

    for (let i = 0; i <= 9; i++) {

        let radios = document.getElementsByName(radio + i)

        for (var j = 0; j < radios.length; j++) {

            if (radios[j].checked) {
                count += parseInt(radios[j].value)
                break
            }
        }
    }

    let $resulth5 = $('#j-result-h5'),
        $resultText = $('#j-result-text'),
        type = '',
        suggest = ''

    if (count > 7 && count < 13) {
        type = '保守型'
        suggest = '您的投资风险偏好属于保守型。您投资时首要考虑保本，其次才考虑追求收益。'
    } else if (count > 12 && count < 18) {

        type = '稳健型'
        suggest = '追求平稳是您的理财标签，您对风险的关注更甚于对收益的关心。'
    } else if (count > 17 && count < 25) {

        type = '平衡型'
        suggest = '您的投资风险偏好属于理性型，不排斥也不追求风险。'
    } else if (count > 24 && count < 29) {

        type = '进取型'
        suggest = '您的投资风险偏好较高，愿意承受一定的风险，追求较高的投资收益。'
    } else if (count > 28 && count < 34) {

        type = '积极型'
        suggest = '您的投资风格非常大胆，愿意追求更高的收益和资产的快速增值，对投资的损失也有很强的承受能力。'
    }

    if(type&&suggest){
        $resulth5.text(type)
        $resultText.text(suggest)
    }

    resultCount = count
}
