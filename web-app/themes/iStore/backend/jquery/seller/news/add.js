/* ============================================================
 * News Add JavaScript
 * ============================================================ */
$("#addItem").validate({ignore: ""});

function PostForm() {
    var dataForm = $('#addItem');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/news";
    var formbtn = $('#addnewbtn');
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
                } else {

                }
            }
        });
    }
}

$('#addnew').on('hidden.bs.modal', function () {
    $('#addItem').trigger("reset");
    $('.tabbable a[href=#addItem_tab_genral]').tab('show');
});

$('#add-new').click(function (e) {
    $('#addnew').modal('show');
});

$('#addnewbtn').click(function (e) {
    e.preventDefault();
    PostForm();
});