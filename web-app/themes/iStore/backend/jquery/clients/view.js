/* ============================================================
 * View Clients
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
"use strict";
var table = $('#datatbl');
var AjaxURL = "datatable/clients";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "title", "orderable": false},
    {"data": "area", "orderable": false},
    {"data": "state", "orderable": false},
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
            d.users = 1;
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

function ViTem(itemid) {
    var FormUrl = reqSource() + "users/view-modal";
    var progresselm = $('#userstbl');
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'itemid=' + itemid,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $("#viewcontent").html(data.html);
                $('#viewitem').modal('show');
            } else {

            }
        }
    });
}
(jQuery);