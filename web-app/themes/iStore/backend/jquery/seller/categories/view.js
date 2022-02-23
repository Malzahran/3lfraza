/* ============================================================
 * View Categories
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var table = $('#catstbl');

var AjaxURL = "seller/datatable";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "title"},
    {"data": "maincat"},
    {"data": "subcat"},
    {"data": "catorder"},
    {"data": "type"},
    {"data": "photo", "orderable": false},
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
    "dom":
        "<'table-responsive't><p i l>"
    ,
    "ajax": {
        "url": reqSource() + AjaxURL,
        "type": "POST",
        "data": function (d) {
            d.t_type = "categories";
            d.parcat = $('input:radio[id="parentcat"]:checked').val();
            d.maincat = $('input:radio[id="maincat"]:checked').val();
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
// change accounting state
$('input:radio[name="type[]"]').change(function () {
    setTimeout(function () {
        dtable.ajax.reload();
    }, 100);
});


function ViTem(itemid) {
    var AjaxUrl = reqSource() + "seller/categories";
    var progresselm = $('#catstbl');
    $.ajax({
        url: AjaxUrl,
        type: "POST",
        data: 'action=view-modal&item_id=' + itemid,
        beforeSend: function () {
            progresselm.addClass('csspinner load1');
        },
        success: function (data) {
            progresselm.removeClass('csspinner load1');
            if (data.status === 200) {
                $("#viewcontent").html(data.html);
                $('#viewitem').modal('show');
            }
        }
    });
}