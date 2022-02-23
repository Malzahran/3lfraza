/* ============================================================
 * Clients Suspend JavaScript
 * ============================================================ */
"use strict";

function AiTem(itemid, itemname, action, code) {
    $('#actaction').html(action + ' ' + itemname);
    $('#activate-btn').html(action);
    $('#activeid').val(itemid);
    if (code === 1) {
        $('#actaction').addClass('text-success');
        $('#actaction').removeClass('text-warning');
        $('#activate-btn').addClass('btn-success');
        $('#activate-btn').removeClass('btn-warning');
    } else {
        $('#actaction').addClass('text-warning');
        $('#actaction').removeClass('text-success');
        $('#activate-btn').addClass('btn-warning');
        $('#activate-btn').removeClass('btn-success');
    }
    $('#activateitem').modal('show');
}

$('#activate-btn').click(function (e) {
    e.preventDefault();
    var AjaxUrl = reqSource() + "users/activate";
    var progresselm = $('#activateitem').find('.modal-body');
    var confbtn = $('#activate-btn');
    var dataForm = $('#activate-form');
    var postData = dataForm.serializeArray();
    $.ajax({
        url: AjaxUrl, type: "POST", data: postData,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
            confbtn.attr("disabled", "true");
        },
        success: function (data) {
            confbtn.removeAttr('disabled');
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $('#activateitem').modal('hide');
                dtable.ajax.reload(null, false);
            } else {

            }
        }
    });
});

$('#activateitem').on('hidden.bs.modal', function () {
    $('#activate-form').trigger("reset");
    $('#actaction').empty();
    $('#activate-btn').empty();
    $('#activeid').val('');
});
(jQuery);