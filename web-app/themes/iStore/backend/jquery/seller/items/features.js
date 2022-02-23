/* ============================================================
 * Items Add JavaScript
 * ============================================================ */
$("#itemID").validate({ignore: ""});

function PostFeature() {
    var dataForm = $('#addfeature');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/items";
    var formbtn = $('#addFtBtn');
    var formData = new FormData(dataFormmu);
    formData.append('action', 'features');
    formData.append('method', 'add');
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
                    $('#Features').append(data.html);
                    $('#addfeature').trigger("reset");
                } else {

                }
            }
        });
    }
}

$('#itemFeatures').on('hidden.bs.modal', function () {
    $('#itemID').val('');
    $('#itemName').empty();
    $('#Features').empty();
    $('#addfeature').trigger("reset");
});

function FTiTem(itemID, itemName) {
    var FormUrl = reqSource() + "seller/items";
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'action=features&method=get&item_id=' + itemID,
        beforeSend: function () {
            table.addClass('csspinner load1');
        },
        success: function (data) {
            table.removeClass('csspinner load1');
            if (data.status === 200) {
                $('#itemID').val(itemID);
                $('#itemName').html(itemName);
                $('#Features').html(data.html);
                $('#itemFeatures').modal('show');
            } else {

            }
        }
    });
}

function eFt(ftID) {
    var dataForm = $('#featureForm_' + ftID);
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/items";
    var formData = new FormData(dataFormmu);
    formData.append('action', 'features');
    formData.append('method', 'edit');
    if (dataForm.valid()) {
        $.ajax({
            url: FormUrl, data: formData, type: 'POST', contentType: false, processData: false,
            beforeSend: function () {
                dataForm.addClass('csspinner load1');
            },
            success: function (data) {
                dataForm.removeClass('csspinner load1');
            }
        });
    }
}

function dFt(ftID) {
    var div = $('#feature_' + ftID);
    var AjaxUrl = reqSource() + "seller/items";
    var postData = 'action=features&method=delete&ft_id=' + ftID;
    $.ajax({
        url: AjaxUrl, data: postData, type: 'POST',
        beforeSend: function () {
            div.addClass('csspinner load1');
        },
        success: function (data) {
            div.removeClass('csspinner load1');
            if (data.status === 200) {
                div.remove();
            }
        }
    });
}

$('#addFtBtn').click(function (e) {
    e.preventDefault();
    PostFeature();
});