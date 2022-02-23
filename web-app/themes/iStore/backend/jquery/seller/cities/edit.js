/* ============================================================
 * Cities Edit JavaScript
 * ============================================================ */
$("#edititm").validate({ignore: ":hidden"});

function Eitem(itemid) {
    var AjaxUrl = reqSource() + "seller/cities";
    var progresselm = $('#itmstbl');
    $.ajax({
        url: AjaxUrl, data: 'action=edit-modal&item_id=' + itemid, type: 'POST',
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
    var dataForm = $('#edititm');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/cities";
    var formbtn = $('#edit-btn');
    var formData = new FormData(dataFormmu);
    formData.append('action', 'edit');
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
                    getAreas();
                } else {

                }
            }
        });
    } else {
        return false;
    }
}

$('#edititem').on('hidden.bs.modal', function () {
    $('#editcontent').empty();
});