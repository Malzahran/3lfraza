/* ============================================================
 * View Orders
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
"use strict";
var table = $('#ordtbl');
$(document).ready(function () {
    setTimeout(update, 10000);
    $("#preprationform").validate({ignore: ":hidden"});
});
var AjaxURL = "datatable/orders";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "number", "orderable": false},
    {"data": "cost"},
    {"data": "discount"},
    {"data": "dlv_cost"},
    {"data": "detailes", "orderable": false},
    {"data": "agent"},
    {"data": "worker"},
    {"data": "time"},
    {"data": "area"},
    {"data": "state"},
    {"data": "more", "orderable": false}
];

var settings = {
    "columnDefs": [{
        "searchable": false,
        "orderable": false,
        "targets": 0
    }
    ],
    "search": {
        "smart": true
    },
    "language": {
        "processing": "<span class='glyphicon glyphicon-refresh glyphicon-refresh-animate'></span>",
        "lengthMenu": "_MENU_ ",
        "search": "_INPUT_",
        "searchPlaceholder": "search for"
    },
    "processing": true,
    "serverSide": true,
    "responsive": true,
    "dom": "<'table-responsive't><p i l>"
    ,
    "ajax": {
        "url": reqSource() + AjaxURL,
        "type": "POST"
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[8, 'desc']]
};
var dtable = table.DataTable(settings);
if ($('#search-table').val().length > 1) {
    dtable.search($('#search-table').val()).draw();
}
// search box for table
$('#search-table').keyup(function () {
    setTimeout(function () {
        dtable.search($('#search-table').val()).draw();
    }, 200);
});

function update() {
    dtable.ajax.reload(null, false);
    var firstload = $('#first_get').val();
    var oldCount = $('#orders_total').val();
    var rowCount = dtable.page.info().recordsTotal;
    if (firstload == 0) {
        $('#first_get').val(1);
    } else {
        if (oldCount != rowCount) {
            $.playSound(themesource() + '/sound/what');
        }
    }
    $('#orders_total').val(rowCount);
    setTimeout(update, 10000);
}

function ViTem(itemid) {
    var FormUrl = reqSource() + "orders/view-modal";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'item_id=' + itemid,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                $("#viewcontent").html(data.html);
                $('#viewitem').modal('show');
            } else {

            }
        }
    });
}

function Cord(itemid) {
    var FormUrl = reqSource() + "orders/confirm";
    var progresselm = $('#ordtbl');
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'item_id=' + itemid,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status == 200) {
                dtable.ajax.reload();
            } else {

            }
        }
    });
}

$('#rOrderBtn').click(function (e) {
    var AjaxUrl = reqSource() + "orders/refuse";
    var dataForm = $('#refuseform');
    var postData = dataForm.serializeArray();
    if (dataForm.valid()) {
        $.ajax({
            url: AjaxUrl, type: "POST", data: postData,
            beforeSend: function () {
                dataForm.addClass('csspinner load1');
            },
            success: function (data) {
                dataForm.removeClass('csspinner load1');
                if (data.status == 200) {
                    $('#refusemodal').modal('hide');
                    dtable.ajax.reload();
                }
            }
        });
    } else {
        return false;
    }
});

function ROrder(orderdID) {
    $('#rordid').val(orderdID);
    $('#refusemodal').modal('show');
}

$('#refusemodal').on('hidden.bs.modal', function () {
    $('#refuseform').trigger("reset");
    $('#rordid').val('');
});
(jQuery);