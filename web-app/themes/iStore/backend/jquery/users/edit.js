/* ============================================================
 * Users Edit JavaScript
 * ============================================================ */
"use strict";
$("#edituser").validate({ignore: ""});

function Eitem(itemid) {
    var AjaxUrl = reqSource() + "users/edit-modal";
    var progresselm = $('#userstbl');
    $.ajax({
        url: AjaxUrl, data: 'itemid=' + itemid, type: 'POST',
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $('#editcontent').html(data.html);
                $('#edititem').modal('show');
            } else {

            }
        }
    });
}

function updateitem() {
    var dataForm = $('#edituser');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "users/edit";
    var formbtn = $('#edit-btn');
    var formData = new FormData(dataFormmu);
    if (dataForm.valid()) {
        $.ajax({
            url: FormUrl, data: formData, type: 'POST', contentType: false, processData: false,
            beforeSend: function () {
                formbtn.attr("disabled", "true");
                dataForm.addClass('csspinner load1');
            },
            success: function (data) {
                formbtn.removeAttr('disabled');
                dataForm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $('#edititem').modal('hide');
                    dtable.ajax.reload(null, false);
                } else {

                }
            }
        });
    } else {
        return false;
    }
}

function epasseye() {
    $("#passseymb").addClass('hidden');
    $("#showpasse").removeClass('hidden');
}

$('#edititem').on('hidden.bs.modal', function () {
    $('#editcontent').empty();
});

function Viewepass() {
    if ($('#epassword').attr('type') === 'password') {
        $('#epassword').attr('type', 'text');
        $('.passeyee').attr('data-original-title', 'Hide Password');
    } else {
        $('#epassword').attr('type', 'password');
        $('.passeyee').attr('data-original-title', 'Show Password');
    }
}

function checkeEmail(e, s, t, o) {
    var AjaxUrl = reqSource() + "usercheck/checkemail";
    var target_html = $(t);
    $.ajax({
        url: AjaxUrl, data: 'userid=' + s + '&query=' + e, type: 'POST',
        success: function (data) {
            $("#copypass").attr('disabled');
            if (data.status == 200) {
                target_html.html('<span style="color: #94ce8c;"><i class="fa fa-check"></i></span>');
            } else {
                target_html.html('<span style="color: #ee2a33;"><i class="fa fa-times"></i></span>');
            }
        }
    });
}

(jQuery);