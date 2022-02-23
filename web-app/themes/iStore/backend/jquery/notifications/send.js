/* ============================================================
 * Notification Send JavaScript
 * ============================================================ */
"use strict";
$("#sendsnotf").validate({ignore: ":hidden"});
$("#sendgnotf").validate({ignore: ":hidden"});

function SendSNOTF(userid, uname) {
    $('#notfuname').html(uname);
    $('#nuserid').val(userid);
    $('#sendsnotfm').modal('show');
}

$('#send-snotf-btn').click(function (e) {
    e.preventDefault();
    e.stopPropagation();
    var dataForm = $('#sendsnotf');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "fcmnotf/sendsingle";
    var formbtn = $('#send-snotf-btn');
    var formData = new FormData(dataFormmu);
    if (dataForm.valid()) {
        $.ajax({
            url: FormUrl,
            data: formData,
            type: 'POST',
            contentType: false,
            processData: false,
            beforeSend: function () {
                formbtn.attr("disabled", "true");
                dataForm.addClass('csspinner load1');
            }, success: function (data) {
                formbtn.removeAttr('disabled');
                dataForm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $('#sendsnotfm').modal('hide');
                } else {

                }
            }
        });
    } else {
        return false;
    }
});

$('#send-gnotf-btn').click(function (e) {
    e.preventDefault();
    e.stopPropagation();
    var dataForm = $('#sendgnotf');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "fcmnotf/sendbulk";
    var formbtn = $('#send-gnotf-btn');
    var formData = new FormData(dataFormmu);
    if (dataForm.valid()) {
        $.ajax({
            url: FormUrl,
            data: formData,
            type: 'POST',
            contentType: false,
            processData: false,
            beforeSend: function () {
                formbtn.attr("disabled", "true");
                dataForm.addClass('csspinner load1');
            }, success: function (data) {
                formbtn.removeAttr('disabled');
                dataForm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $('#sendgnotfm').modal('hide');
                    dtable.ajax.reload();
                } else {

                }
            }
        });
    } else {
        return false;
    }
});

$('#sendsnotfm').on('hidden.bs.modal', function () {
    $('#sendsnotf').trigger("reset");
    if (!$('#notfaction').hasClass('hidden')) {
        $('#notfaction').addClass('hidden');
    }
    $('#nuserid').val('');
});

$('#sendgnotfm').on('hidden.bs.modal', function () {
    $('#sendgnotf').trigger("reset");
    $("#notfgroup").empty();
    if (!$('#notfaction').hasClass('hidden')) {
        $('#notfaction').addClass('hidden');
    }
});

$('#send-g-notf').click(function (e) {
    var AjaxUrl = reqSource() + "fcmnotf/filtergroup";
    var progresselm = $('#notftbl');
    $.ajax({
        url: AjaxUrl, type: "POST",
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $("#notfgroup").html(data.html);
                $('#sendgnotfm').modal('show');
            }
        }
    });
});

$('#notftype').on('change', function () {
    var notftype = $('#notftype').val();
    if (notftype == 1 || notftype == 2) {
        if (!$('#notfaction').hasClass('hidden')) {
            $('#notfaction').addClass('hidden');
        }
    } else {
        if ($('#notfaction').hasClass('hidden')) {
            $('#notfaction').removeClass('hidden');
        }
    }
});
(jQuery);