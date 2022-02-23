/* ============================================================
 * View Items
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var table = $('#itemsTbl');

var AjaxURL = "seller/datatable";
var allowed_inventory = $('#aINV').val();
var allowed_category = $('#aCAT').val();
var sort = 4;
var cols = [
    {"data": "id", "orderable": false},
    {"data": "code"},
    {"data": "title"},
    {"data": "price"},
    {"data": "date"},
    {"data": "active"},
    {"data": "more", "orderable": false}
];
if (allowed_inventory == 1 && allowed_category == 1) {
    cols = [
        {"data": "id", "orderable": false},
        {"data": "code"},
        {"data": "title"},
        {"data": "cat"},
        {"data": "inventory"},
        {"data": "price"},
        {"data": "date"},
        {"data": "active"},
        {"data": "more", "orderable": false}
    ];
    sort = 6;
} else if (allowed_inventory == 1 && allowed_category != 1) {
    cols = [
        {"data": "id", "orderable": false},
        {"data": "code"},
        {"data": "title"},
        {"data": "inventory"},
        {"data": "price"},
        {"data": "date"},
        {"data": "active"},
        {"data": "more", "orderable": false}
    ];
    sort = 5;
} else if (allowed_inventory != 1 && allowed_category == 1) {
    cols = [
        {"data": "id", "orderable": false},
        {"data": "code"},
        {"data": "title"},
        {"data": "cat"},
        {"data": "price"},
        {"data": "date"},
        {"data": "active"},
        {"data": "more", "orderable": false}
    ];
    sort = 5;
}


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
            d.active = $('input:checkbox[id="active"]:checked').val();
            d.unactive = $('input:checkbox[id="unactive"]:checked').val();
            d.t_type = "items";
        }
    },
    "lengthMenu": [[10, 50, -1], [10, 50, "all"]],
    "columns": cols,
    "order": [[sort, 'desc']]
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
$('input:checkbox[name="active[]"]').change(function () {
    setTimeout(function () {
        dtable.ajax.reload();
    }, 100);
});


function ViTem(itemid) {
    var FormUrl = reqSource() + "seller/items";
    $.ajax({
        url: FormUrl,
        type: "POST",
        data: 'action=view-modal&item_id=' + itemid,
        beforeSend: function () {
            table.addClass('csspinner load1');
        },
        success: function (data) {
            table.removeClass('csspinner load1');
            if (data.status === 200) {
                $("#viewcontent").html(data.html);
                $('#viewitem').modal('show');
            } else {

            }
        }
    });
}