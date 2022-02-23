<?php
$items = array();
$search = urlencode($escapeObj->stringEscape($data->misc->searchq));
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, "https://api.tomtom.com/search/2/search/$search.json?countrySet=EG&idxSet=STR&key=n6vDMFoF1CAEDUmGteZtjWEaYAzmBffV");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$output = curl_exec($ch);
$output = json_decode($output, true);
curl_close($ch);
if (!isset($output['httpStatusCode']))
    if (!empty($output['results'])) {
        foreach ($output['results'] as $place) {
            $items[] = array('name' => $place['address']['streetName'], 'address' => $place['address']['municipalitySubdivision'], 'lat' => $place['position']['lat'], 'lon' => $place['position']['lon']);
        }
    }
$response["result"] = "success";
$response["places"] = $items;