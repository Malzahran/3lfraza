/* ============================================================
 * Terms JavaScript
 * ============================================================ */
"use strict";

$('#save-btn').click(function (e) {
    e.preventDefault();
    Edit();
});

function Edit() {
    for (var instance in CKEDITOR.instances) {
        CKEDITOR.instances[instance].updateElement();
    }
    var dataForm = $('#inputForm');
    var dataFormmu = $('#inputForm')[0];
    var AjaxUrl = reqSource() + "misc/terms";
    var confbtn = $('#save-btn');
    var postData = new FormData(dataFormmu);
    $.ajax({
        url: AjaxUrl,
        data: postData,
        type: 'POST',
        contentType: false,
        processData: false,
        beforeSend: function () {
            confbtn.attr("disabled", "true");
            dataForm.addClass('csspinner load1');
        },
        success: function (data) {
            dataForm.removeClass('csspinner load1');
            confbtn.removeAttr('disabled');
            if (data.status == 200) {
                alert('saved');
            } else {
                var msg = lang["globalErrorTryAgain"];
                if (data.message != null) {
                    msg = data.message;
                    alert(msg);
                }
            }
        }
    });
}

(jQuery);