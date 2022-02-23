/* ============================================================
 * View Cities
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var table = $('#itmstbl');

var AjaxURL = "seller/datatable";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "title"},
    {"data": "group"},
    {"data": "lat"},
    {"data": "lon"},
    {"data": "radius"},
    {"data": "shipping"},
    {"data": "date"},
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
            d.t_type = "cities";
        }
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[7, 'desc']]
};
var dtable = table.DataTable(settings);
// search box for table
$('#search-table').keyup(function () {
    setTimeout(function () {
        dtable.search($('#search-table').val()).draw();
    }, 200);
});