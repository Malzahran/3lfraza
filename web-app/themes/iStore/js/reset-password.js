/* ============================================================
 * Reset Password Jquery Function
 * ============================================================ */

$("#form-reset-pass").validate({
    ignore: ""
});
$("#form-reset-pass").submit(function (e) {
    e.preventDefault();
});
$(document).ready(function () {
    $('#submit-btn').click(function () {
        var FormUrl = reqSource() + "reset_password";
        var dataForm = $('#form-reset-pass');
        var submitBtn = dataForm.find("#submit-btn");
        var formMsg = dataForm.find("#error");
        if (!formMsg.hasClass('hidden')) {
            formMsg.addClass('hidden');
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
							submitBtn.attr("disabled", "true");
                            formMsg.html(data.message);
                            if (formMsg.hasClass('hidden')) {
                                formMsg.removeClass('hidden');
                            }
                            setTimeout(window.location = data.redirect_url, 5000);
                        } else {
                            formMsg.html(data.error_message);
                            if (formMsg.hasClass('hidden')) {
                                formMsg.removeClass('hidden');
                            }
                        }
                    }
                });
        } else {
            return false;
        }
    });
});