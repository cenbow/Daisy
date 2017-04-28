/**
 * Created by lyl on 2016/1/12.
 */
define(function (require, exports, module) {
    var $fund=$('#j-fund');
    $fund.text(splitAmount($fund.data('fund')));
    var num=$fund.data('fund');
   //进度条
    progressBarPosition();
    function progressBarPosition(){
        var $progressBar=$('.f-progressBar-yel '),
            $textPs=$('.f-text-ps'),
            Width=(1000000-num)/10000,
            right=Width*0.6,
            Width2=100-Width;

        if(Width2>0&&Width2<=7){
            $progressBar.css('width','7%');
            $textPs.css('right','58%')
        }
        else if(Width2===0){
            $progressBar.css('width',0);
            $textPs.css('right','66%')
        }
        else{
            $progressBar.css('width',Width2+'%');
            $textPs.css('right',right+'%')
        }
    }

    /**
     *金额格式化
     * @param amount{Number} 需要格式化的金额
     * @param format{String} 格式化类型，split=逗号分割的整数
     */
    function splitAmount(amount) {
        if (amount >= 1000) {

            var amountStr = amount.toString(),
                size = parseInt(amountStr.length / 3),
                amountArray = amountStr.split('').reverse();

            for (var i = 1; i <= size; i++) {
                var j = i * 3 - 1;
                if (j !== amountArray.length - 1) {
                    amountArray[j] = ',' + amountArray[j];
                }
            }

            return amountArray.reverse().join('');

        } else {
            return amount;
        }
    }
});
