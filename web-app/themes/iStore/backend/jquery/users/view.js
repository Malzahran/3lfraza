/* ============================================================
 * View Users
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var table = $('#userstbl');

var AjaxURL = "datatable/users";
var cols = [
    {"data": "id", "orderable": false},
    {"data": "name"},
    {"data": "regfrom"},
    {"data": "date"},
    {"data": "area", "orderable": false},
    {"data": "active", "orderable": false},
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
            d.web = $('input:checkbox[id="formweb"]:checked').val();
            d.app = $('input:checkbox[id="formmob"]:checked').val();
            d.facebook = $('input:checkbox[id="facebook"]:checked').val();
            d.google = $('input:checkbox[id="google"]:checked').val();
        }
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[3, 'desc']]
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
                $('.rating').each(function () {
                    $(this).val() > 0 ? $(this).next(".label").show().text($(this).val() || ' ') : $(this).next(".label").hide();
                });
                $('#viewitem').modal('show');
            } else {

            }
        }
    });
}