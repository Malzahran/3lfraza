/* ============================================================
 * Orders Delivery JavaScript
 * ============================================================ */
"use strict";
$('#assigndelvbtn').click(function (e) {
    var AjaxUrl = reqSource() + "orders/set_delv";
    var progresselm = $('#assigndelvform');
    var orderdID = $('#aordid').val();
    var delvID = $('#dlvasel').val();
    if (delvID != 0)
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID + '&delv_id=' + delvID,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $('#assigndelvmodal').modal('hide');
                    dtable.ajax.reload();
                }
            }
        });
});

function Adelv(orderdID) {
    var AjaxUrl = reqSource() + "orders/get_delv";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                $("#dlvasel").html(data.html);
                $('#assigndelvmodal').modal('show');
                $('#aordid').val(orderdID);
            }
        }
    });
}

$('#assigndelvmodal').on('hidden.bs.modal', function () {
    $('#assigndelvform').trigger("reset");
    $("#dlvasel").empty();
    $('#aordid').val('');
});

function Edelv(orderdID) {
    var AjaxUrl = reqSource() + "orders/get_delv";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                $("#dlvcsel").html(data.html);
                $('#changedelvmodal').modal('show');
                $('#eordid').val(orderdID);
            }
        }
    });
}

$('#changedelvbtn').click(function (e) {
    var AjaxUrl = reqSource() + "orders/set_delv";
    var progresselm = $('#changedelvform');
    var orderdID = $('#eordid').val();
    var delvID = $('#dlvcsel').val();
    if (delvID != 0)
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID + '&delv_id=' + delvID,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $('#changedelvmodal').modal('hide');
                    dtable.ajax.reload();
                }
            }
        });
});

$('#changedelvmodal').on('hidden.bs.modal', function () {
    $('#changedelvform').trigger("reset");
    $('#dlvcsel').empty();
    $('#eordid').val('');
});

(jQuery);