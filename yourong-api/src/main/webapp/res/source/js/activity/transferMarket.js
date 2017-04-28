
/**
 * Created by XR on 2017/1/3.
 */
define(['base'], function (require, exports, module) {
  // 'use strict'
  var base = require('base');

    var $transferPop =
      $('.j_TransferPop');
    $('.m-question').on('click', function () {
      $transferPop.show();
      window.scrollTo(0, 320)
      base.cover.show();
    });
    $('.j_transferClose').on('click', function () {
      $transferPop.hide();
      base.cover.hide();
    });
})