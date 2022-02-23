/* ============================================================
 * Users Add JavaScript
 * ============================================================ */
"use strict";
$("#adduser").validate({
    ignore: ""
});
$(document).ready(function () {
    $("#emailinput").change(function () {
        $("#emailinput").trigger('keyup');
    });
});

function generateUsername(start, n) {
    if (!n) {
        n = 5;
    }
    var text = '';
    var possible = '0123456789';
    text += start;
    for (var i = 0; i < n; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    $("#username").val(text || '');
    checkUsername(text, 0, '.check-username-result', false);
}

function passeye(e) {
    $("#passsymb").addClass('hidden');
    $("#showpass").removeClass('hidden');
}

function copyPassword() {
    $("#password").val($("#username").val() || '');
    passeye();
}

$('.passeye').click(function () {
    if ($('#password').attr('type') === 'password') {
        $('#password').attr('type', 'text');
        $('.passeye').attr('data-original-title', 'Hide Password');
    } else {
        $('#password').attr('type', 'password');
        $('.passeye').attr('data-original-title', 'Show Password');
    }
});

function checkUsername(e, s, t, o) {
    var AjaxUrl = reqSource() + "usercheck/checkusername";
    var target_html = $(t);
    $.ajax({
        url: AjaxUrl,
        data: 'userid=' + s + '&query=' + e,
        type: 'POST',
        beforeSend: function () {
        },
        success: function (data) {
            $("#copypass").attr('disabled');
            if (data.status === 200) {
                target_html.html('<span style="color: #94ce8c;"><i class="fa fa-check"></i></span>');
                $("#copypass").removeAttr('disabled');
            } else {
                target_html.html('<span style="color: #ee2a33;"><i class="fa fa-times"></i></span>');
            }
        }
    });
}

function checkEmail(e, s, t, o) {
    var AjaxUrl = reqSource() + "usercheck/checkemail";
    var target_html = $(t);
    $.ajax({
        url: AjaxUrl,
        data: 'userid=' + s + '&query=' + e,
        type: 'POST',
        beforeSend: function () {
        },
        success: function (data) {
            $("#copypass").attr('disabled');
            if (data.status === 200) {
                target_html.html('<span style="color: #94ce8c;"><i class="fa fa-check"></i></span>');
                $("#copypass").removeAttr('disabled');
            } else {
                target_html.html('<span style="color: #ee2a33;"><i class="fa fa-times"></i></span>');
            }
        }
    });
}

function PostForm() {
    var dataForm = $('#adduser');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "users/add";
    var formbtn = $('#addnewbtn');
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
            },
            success: function (data) {
                formbtn.removeAttr('disabled');
                dataForm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $('#addnew').modal('hide');
                    dtable.ajax.reload();
                } else {

                }
            }
        });
    } else {
        return false;
    }
}

$('#typesel').on('change', function () {
    var acounttype = $('#typesel').val();
    if (acounttype == 1) {
        $('#perm').removeClass('hidden');
        var AjaxUrl = reqSource() + "users/getperm";
        var progresselm = $('#adduser');
        $.ajax({
            url: AjaxUrl,
            type: "POST",
            data: 'type=' + acounttype,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $("#addusr_tab_perm").html(data.perm);
                }
            }
        });
    } else {
        if (!$('#perm').hasClass('hidden')) {
            $('#perm').addClass('hidden');
            $("#addusr_tab_perm").empty();
        }
    }
});

$('#addnew').on('hidden.bs.modal', function () {
    $('#adduser').trigger("reset");
    if (!$('#perm').hasClass('hidden')) {
        $('#perm').addClass('hidden');
        $("#addusr_tab_perm").empty();
    }
    $('.tabbable a[href=#addusr_tab_genral]').tab('show');
});
$('#add-new').click(function (e) {
    $('#addnew').modal('show');
});
$('#addnewbtn').click(function (e) {
    e.preventDefault();
    PostForm();
});
(jQuery);