/* ============================================================
 * Profile Edit Jquery Function
 * ============================================================ */

$("#form-myprofile").validate({
    ignore: ":hidden"
});
$("#form-myprofile").submit(function (e) {
    e.preventDefault();
});
$(document).ready(function () {

    $('#submit-btn').click(function () {
        var FormUrl = reqSource() + "editprofile";
        var dataForm = $('#form-myprofile');
        var dataFormmu = dataForm[0];
        var submitBtn = dataForm.find("#submit-btn");
        var errorMsg = dataForm.find("#error");
        if (!errorMsg.hasClass('hidden')) {
            errorMsg.addClass('hidden');
        }
        if (!$('#loading-spin').hasClass('hidden')) {
            $('#loading-spin').addClass('hidden');
        }
        var formData = new FormData(dataFormmu);
        if (dataForm.valid()) {
            $.ajax(
                {
                    url: FormUrl,
                    type: "POST",
                    data: formData,
                    contentType: false,
                    processData: false,
                    beforeSend: function () {
                        submitBtn.attr("disabled", "true");
                        $('#loading-spin').removeClass('hidden');
                    }, success: function (data) {
                        submitBtn.removeAttr('disabled');
                        $('#loading-spin').addClass('hidden');
                        if (data.status === 200) {
                            if (data.avatar_code === 1) {
                                $('#profileavatar').html(data.avatar);
                                $('#useravatar').html(data.avatar);
                                dataForm.addClass("submitted");
                                $("#useravatar").css("background-image", "url(" + $('#useravatar').find("img").attr("src") + ")");
                                $("#profileavatar").css("background-image", "url(" + $('#profileavatar').find("img").attr("src") + ")");
                            } else if (data.avatar_code === 2) {
                                $('.useravatar').html(data.avatar);
                            }
                        } else if (data.status === 400) {
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