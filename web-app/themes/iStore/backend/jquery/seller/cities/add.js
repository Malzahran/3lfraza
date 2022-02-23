/* ============================================================
 * Cities Add JavaScript
 * ============================================================ */
$("#additm").validate({ignore: ":hidden"});

function PostForm() {
    var dataForm = $('#additm');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/cities";
    var formbtn = $('#add-btn');
    var formData = new FormData(dataFormmu);
    formData.append('action', 'add');
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
                    $('#addnew').modal('hide');
                    dtable.ajax.reload();
                    getAreas();
                } else {

                }
            }
        });
    } else {
        return false;
    }
}

$('#addnew').on('hidden.bs.modal', function () {
    $('#additm').trigger("reset");
});

$('#add-new').click(function (e) {
    e.preventDefault();
    $('#addnew').modal('show');
});

$('#add-btn').click(function (e) {
    e.preventDefault();
    PostForm();
});