/* ============================================================
 * Coverage Map
 * ============================================================ */
var map;
var markersArray = [];
var circles = [];
var infoWindow = new google.maps.InfoWindow({
    content: ""
});

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: new google.maps.LatLng(0, 0),
        zoom: 0,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });
    getAreas();
}

function getAreas() {
    var gMarkers = $.post(reqSource() + "coverage-cities", '',
        '');
    gMarkers.success(function (data) {
        if (data.status == 200) {
            removeAllcircles();
            gMarkers = data.locations;
            addMarkers(gMarkers);
        }
    });
}

function addMarkers(gmarkers) {
    deleteOverlays();
    for (var i = 0; i < gmarkers.length; i++) {
        latlng = new google.maps.LatLng(gmarkers[i].lat, gmarkers[i].lon);
        var icon = 'https://maps.google.com/mapfiles/ms/icons/red-dot.png';
        mmap = new google.maps.Marker({
            position: latlng,
            map: map,
            icon: icon,
            title: gmarkers[i].title
        });


        var sunCircle = {
            strokeColor: "#D1351F",
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: "#ffd1ca",
            fillOpacity: 0.35,
            map: map,
            center: latlng,
            radius: (gmarkers[i].radius) * 1000 // in meters
        };
        cityCircle = new google.maps.Circle(sunCircle);
        cityCircle.bindTo('center', mmap, 'position');
        // push the circle object to the array
        circles.push(cityCircle);

        //Create infowindow
        var infowindow = new google.maps.InfoWindow({
            content: gmarkers[i].snippet
        });
        bindInfoWindow(mmap, map, infoWindow, "<h3>" + gmarkers[i].title + "</h3><p dir='ltr'>" + gmarkers[i].snippet + "</p>");
        markersArray.push(mmap);
    }
    var latlngbounds = new google.maps.LatLngBounds();
    for (var s = 0; s < gmarkers.length; s++) {
        latlng = new google.maps.LatLng(gmarkers[s].lat, gmarkers[s].lon);
        latlngbounds.extend(latlng);
    }
    map.fitBounds(latlngbounds);
}

// remove All circles
function removeAllcircles() {
    for (var i in circles) {
        circles[i].setMap(null);
    }
    circles = []; // this is if you really want to remove them, so you reset the variable.
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

google.maps.event.addDomListener(window, 'load', initMap);