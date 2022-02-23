/* ============================================================
 * News Feat JavaScript
 * ============================================================ */
"use strict";

function FiTem(itemid, itemname, action, code) {
    $('#feataction').html(action + ' ' + itemname);
    $('#feat-btn').html(action);
    $('#featid').val(itemid);
    if (code == 1) {
        $('#feataction').addClass('text-success');
        $('#feataction').removeClass('text-warning');
        $('#feat-btn').addClass('btn-success');
        $('#feat-btn').removeClass('btn-warning');
    } else {
        $('#feataction').addClass('text-warning');
        $('#feataction').removeClass('text-success');
        $('#feat-btn').addClass('btn-warning');
        $('#feat-btn').removeClass('btn-success');
    }
    $('#featitem').modal('show');
}

$('#feat-btn').click(function (e) {
    e.preventDefault();
    var AjaxUrl = reqSource() + "seller/news";
    var progresselm = $('#featitem').find('.modal-body');
    var confbtn = $('#feat-btn');
    var action = 'action=feat&';
    var postData = action + 'item_id=' + $('#featid').val();
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
                $('#featitem').modal('hide');
                dtable.ajax.reload(null, false);
            } else {

            }
        }
    });
});

$('#featitem').on('hidden.bs.modal', function () {
    $('#feat-form').trigger("reset");
    $('#feataction').empty();
    $('#feat-btn').empty();
    $('#featid').val('');
});
(jQuery);