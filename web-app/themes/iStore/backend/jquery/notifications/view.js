/* ============================================================
 * View Notifications Report
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var table = $('#notftbl');

var AjaxURL = "datatable/fcmreport";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "desc", "orderable": false},
    {"data": "msgtitle", "orderable": false},
    {"data": "msg", "orderable": false},
    {"data": "pros"},
    {"data": "state", "orderable": false},
    {"data": "time"}
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
    "dom":
        "<'table-responsive't><p i l>"
    ,
    "ajax": {
        "url": reqSource() + AjaxURL,
        "type": "POST",
        "data": function (d) {
            d.normal = $('input:checkbox[id="normal"]:checked').val();
            d.msg = $('input:checkbox[id="msg"]:checked').val();
            d.diropen = $('input:checkbox[id="diropen"]:checked').val();
            d.linkclk = $('input:checkbox[id="linkclk"]:checked').val();
        }
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[6, 'desc']]
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
// change accounting state
$('input:checkbox[name="type[]"]').change(function () {
    setTimeout(function () {
        dtable.ajax.reload();
    }, 100);
});