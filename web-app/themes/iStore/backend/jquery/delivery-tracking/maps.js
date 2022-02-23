/* ============================================================
 * Delivery Tracker
 * Generate advanced tables with sorting, export options using
 * jQuery DataTables plugin
 * ============================================================ */
var map;
var autoZoom = true;
var markersArray = [];
var infoWindow = new google.maps.InfoWindow({
    content: ""
});

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: new google.maps.LatLng(0, 0),
        zoom: 0,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    getmarkers();
}

function getmarkers() {
    var gmarkers = $.post(reqSource() + "delivery-tracking", '',
        '');
    gmarkers.success(function (data) {
        if (data.status == 200) {
            gmarkers = data.locations;
            addMarkers(gmarkers);
        }

    });
    setTimeout(getmarkers, 4000);
}

function addMarkers(gmarkers) {
    deleteOverlays();
    for (var i = 0; i < gmarkers.length; i++) {
        latlng = new google.maps.LatLng(gmarkers[i].lat, gmarkers[i].longt);
        var icon = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
        if (gmarkers[i].color == 1) {
            icon = 'https://maps.google.com/mapfiles/ms/icons/green-dot.png';
        }
        mmap = new google.maps.Marker({
            position: latlng,
            map: map,
            icon: icon,
            title: gmarkers[i].title
        });
        //Create infowindow
        var infowindow = new google.maps.InfoWindow({
            content: gmarkers[i].snippet
        });
        bindInfoWindow(mmap, map, infoWindow, "<h3>" + gmarkers[i].title + "</h3><p>" + gmarkers[i].snippet + "</p>");
        markersArray.push(mmap);
    }
    if (autoZoom) {
        var latlngbounds = new google.maps.LatLngBounds();
        for (var s = 0; s < gmarkers.length; s++) {
            latlng = new google.maps.LatLng(gmarkers[s].lat, gmarkers[s].longt);
            latlngbounds.extend(latlng);
        }
        map.fitBounds(latlngbounds);
    }
}

function bindInfoWindow(marker, map, infowindow, html) {
    google.maps.event.addListener(marker, 'click', function () {
        infowindow.setContent(html);
        infowindow.open(map, marker);
    });
}

// Deletes all markers in the array by removing references to them
function deleteOverlays() {
    if (markersArray) {
        for (i in markersArray) {
            markersArray[i].setMap(null);
        }
        markersArray.length = 0;
    }
}

// change auto zoom
$('#autozoom').change(function () {
    if ($('#autozoom').is(':checked')) {
        autoZoom = true;
    } else {
        autoZoom = false;
    }
});
google.maps.event.addDomListener(window, 'load', initMap);