/* ============================================================
 * Users Delete JavaScript
 * ============================================================ */
"use strict";

function Ditem(itemid, itemname) {
    $('#delname').html(itemname);
    $('#delid').val(itemid);
    $('#deleteitem').modal('show');
}

$('#del-btn').click(function (e) {
    e.preventDefault();
    var AjaxUrl = reqSource() + "users/delete";
    var progresselm = $('#deleteitem').find('.modal-body');
    var confbtn = $('#del-btn');
    var dataForm = $('#delete-form');
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
                $('#deleteitem').modal('hide');
                dtable.ajax.reload(null, false);
            } else {

            }
        }
    });
});

$('#deleteitem').on('hidden.bs.modal', function () {
    $('#delete-form').trigger("reset");
    $('#delname').empty();
    $('#delid').val('');
});

(jQuery);