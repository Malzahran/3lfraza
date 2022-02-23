/* ============================================================
 * Reset Password By Email Jquery Function
 * ============================================================ */

$("#form-reset-email").validate({
    ignore: ""
});
$("#form-reset-email").submit(function (e) {
    e.preventDefault();
});
$(document).ready(function () {
    $('#submit-btn').click(function () {
        var FormUrl = reqSource() + "reset_mail";
        var dataForm = $('#form-reset-email');
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