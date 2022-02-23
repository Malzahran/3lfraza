/* ============================================================
 * Categories Add JavaScript
 * ============================================================ */
$("#addcat").validate({ignore: ":hidden"});

function PostForm() {
    var dataForm = $('#addcat');
    var dataFormmu = dataForm[0];
    var FormUrl = reqSource() + "seller/categories";
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
                } else {

                }
            }
        });
    } else {
        return false;
    }
}

$('#addnew').on('hidden.bs.modal', function () {
    $('#addcat').trigger("reset");
    if (!$('#subsel').hasClass('hidden')) {
        $('#subsel').addClass('hidden');
    }
});

$('#add-new').click(function (e) {
    var AjaxUrl = reqSource() + "seller/categories";
    var progresselm = $('#catstbl');
    $.ajax({
        url: AjaxUrl, type: "POST", data: 'action=getcats&type=1&catType=1',
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $("#catadd").html(data.html);
                $('#addnew').modal('show');
            }
        }
    });
});

$('#catType').on('change', function () {
    $("#catadd").empty();
    if (!$('#subsel').hasClass('hidden')) {
        $('#subsel').addClass('hidden');
        $('#subcatadd').empty();
    }
    var catType = $('#catType').val();
    if (catType == 1) {
        if ($('#mainsel').hasClass('hidden')) {
            $('#mainsel').removeClass('hidden');
        }
        var AjaxUrl = reqSource() + "seller/categories";
        var progresselm = $('#addcat');
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'action=getcats&type=1',
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $("#catadd").html(data.html);
                }
            }
        });
    } else {
        if (!$('#mainsel').hasClass('hidden')) {
            $('#mainsel').addClass('hidden');
        }
    }
});

$('#catadd').on('change', function () {
    if ($('#catadd').val() == -1 || $('#catadd').val() == -2) {
        if (!$('#subsel').hasClass('hidden')) {
            $('#subsel').addClass('hidden');
        }
    } else {
        $('#subsel').removeClass('hidden');
        var AjaxUrl = reqSource() + "seller/categories";
        var progresselm = $('#addcat');
        var catid = $('#catadd').val();
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'action=getcats&type=2&pcatid=' + catid,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status === 200) {
                    $("#subcatadd").html(data.html);
                }
            }
        });
    }
});

$('#add-btn').click(function (e) {
    e.preventDefault();
    PostForm();
});