/* ============================================================
 * View Orders Report
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
"use strict";
var table = $('#ordtbl');
$(document).ready(function () {
    $(".date").datepicker({
        format: 'dd-mm-yyyy',
        endDate: '+0d', autoclose: true, todayBtn: true, todayHighlight: true
    });

});
var AjaxURL = "datatable/rep_ord";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "title", "orderable": false},
    {"data": "norders", "orderable": false},
    {"data": "total", "orderable": false},
    {"data": "discount", "orderable": false},
    {"data": "delivery", "orderable": false},
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
        "type": "POST",
        "data": function (d) {
            d.stores = $('input:radio[id="storetype"]:checked').val();
            d.delivery = $('input:radio[id="delvtype"]:checked').val();
            d.worker = $('input:radio[id="wrktype"]:checked').val();
            d.users = $('input:radio[id="userstype"]:checked').val();
            d.from_date = $('input:text[name="from_date"]').val();
            d.to_date = $('input:text[name="to_date"]').val();
        }
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[0, 'asc']]
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

function VRep(itemid, type, ordtype) {
    var FormUrl = reqSource() + "report/order-details";
    var progresselm = $('#ordtbl');
    var fromD = $('input:text[name="from_date"]').val();
    var toD = $('input:text[name="to_date"]').val();
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'item_id=' + itemid + '&type=' + type + '&ordtype=' + ordtype + '&from=' + fromD + '&to=' + toD,
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

// search date range
$('.date').change(function () {
    setTimeout(function () {
        dtable.ajax.reload();
    }, 300);
});
// change orders report type
$('input:radio[name="type[]"]').change(function () {
    if ($('#delvtype').is(':checked')) {
        $('#ordersCat').addClass('hidden');
    } else {
        $('#ordersCat').removeClass('hidden');
    }
    setTimeout(function () {
        dtable.ajax.reload();
    }, 300);
});

// change orders type
$('input:checkbox[name="cats[]"]').change(function () {
    setTimeout(function () {
        dtable.ajax.reload();
    }, 100);
});

$('#viewitem').on('hidden.bs.modal', function () {
    $('#viewcontent').empty();
    $("#payablechk").prop('checked', true)
    $("#detailschk").prop('checked', true)
    $("#totalchk").prop('checked', true)
});
// change orders view payable
$('#payablechk').change(function () {
    if ($('#payable_data').hasClass('hidden')) {
        $('#payable_data').removeClass('hidden');
    } else {
        $('#payable_data').addClass('hidden');
    }
});
// change orders view details
$('#detailschk').change(function () {
    if ($('#orders_data').hasClass('hidden')) {
        $('#orders_data').removeClass('hidden');
    } else {
        $('#orders_data').addClass('hidden');
    }
});
// change orders view total
$('#totalchk').change(function () {
    if ($('#total_data').hasClass('hidden')) {
        $('#total_data').removeClass('hidden');
    } else {
        $('#total_data').addClass('hidden');
    }
});
(jQuery);