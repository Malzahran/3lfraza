/* ============================================================
 * Orders Worker JavaScript
 * ============================================================ */
"use strict";
$('#assignwrkbtn').click(function (e) {
    var AjaxUrl = reqSource() + "orders/set_wrk";
    var progresselm = $('#assignwrkform');
    var orderdID = $('#aordid').val();
    var wrkID = $('#wrkasel').val();
    if (wrkID != 0)
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID + '&wrk_id=' + wrkID,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $('#assignwrkmodal').modal('hide');
                    dtable.ajax.reload();
                }
            }
        });
});

function Aworker(orderdID) {
    var AjaxUrl = reqSource() + "orders/get_wrk";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                $("#wrkasel").html(data.html);
                $('#assignwrkmodal').modal('show');
                $('#aordid').val(orderdID);
            }
        }
    });
}

$('#assignwrkmodal').on('hidden.bs.modal', function () {
    $('#assignwrkform').trigger("reset");
    $("#wrkasel").empty();
    $('#aordid').val('');
});

function Eworker(orderdID) {
    var AjaxUrl = reqSource() + "orders/get_wrk";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                $("#wrkcsel").html(data.html);
                $('#changewrkmodal').modal('show');
                $('#eordid').val(orderdID);
            }
        }
    });
}

$('#changewrkbtn').click(function (e) {
    var AjaxUrl = reqSource() + "orders/set_wrk";
    var progresselm = $('#changewrkform');
    var orderdID = $('#eordid').val();
    var wrkID = $('#wrkcsel').val();
    if (wrkID != 0)
        $.ajax({
            url: AjaxUrl, type: "POST", data: 'item_id=' + orderdID + '&wrk_id=' + wrkID,
            beforeSend: function () {
                progresselm.addClass('csspinner load1');
            },
            success: function (data) {
                progresselm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $('#changewrkmodal').modal('hide');
                    dtable.ajax.reload();
                }
            }
        });
});

$('#changewrkmodal').on('hidden.bs.modal', function () {
    $('#changewrkform').trigger("reset");
    $('#wrkcsel').empty();
    $('#eordid').val('');
});

(jQuery);