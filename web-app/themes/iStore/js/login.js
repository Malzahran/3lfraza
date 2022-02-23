/* ============================================================
 * Login Jquery Function
 * ============================================================ */

$("#form-login").validate({
    ignore: ""
});
$("#form-login").submit(function (e) {
    e.preventDefault();
});
$(document).ready(function () {

    $('#submit-btn').click(function () {
        var FormUrl = reqSource() + "login";
        var dataForm = $('#form-login');
        var submitBtn = dataForm.find("#submit-btn");
        var errorMsg = dataForm.find("#error");
        if (!errorMsg.hasClass('hidden')) {
            errorMsg.addClass('hidden');
        }
        var postData = dataForm.serializeArray();
        if (dataForm.valid()) {
            $.ajax(
                {
                    url: FormUrl,
                    type: "POST",
                    data: postData,
                    beforeSend: function () {
                        submitBtn.attr("disabled", "true");
                    }, success: function (data) {
                        submitBtn.removeAttr('disabled');

                        if (data.status === 200) {
                            window.location = data.redirect_url;
                        } else {
                            errorMsg.html(data.error_message);
                            if (errorMsg.hasClass('hidden')) {
                                errorMsg.removeClass('hidden');
                            }
                        }
                    }
                });
        } else {
            return false;
        }
    });
});