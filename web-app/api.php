<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $response["result"] = "failure";
    $operation = $parenttype = $subtype = $username = $password = '';
    $user = array('id' => 0);
    $opr = $itemPOST = false;
    $L_CODE = 'ar';
    if (isset($_POST['item_post'])) {
        $itemPOST = (int)$_POST['item_post'];
        if ($itemPOST == 1111) {
            $itemPOST = true;
            if (isset($_POST['username']) && isset($_POST['password'])) {
                $username = $_POST['username'];
                $password = $_POST['password'];
            }
            if (isset($_POST['langcode'])) $L_CODE = $_POST['langcode'];
        }
    } else {
        $data = json_decode(file_get_contents("php://input"));
        if (isset($data->user) && !empty($data->user)) {
            $userpost = $data->user;
            $username = isset($userpost->username) ? $userpost->username : '';
            $password = isset($userpost->password) ? $userpost->password : '';
            if (isset($userpost->langcode)) $L_CODE = $userpost->langcode;
        }
        if (isset($data->operation)) $opr = true;
    }
    require_once('assets/includes/core-api.php');
    if ($opr) {
        if (isset($data->operation)) $operation = $escapeObj->stringEscape($data->operation);
        if (isset($data->parenttype)) $parenttype = $escapeObj->stringEscape($data->parenttype);
        if (isset($data->subtype)) $subtype = $escapeObj->stringEscape($data->subtype);
        include('api/' . $operation . '.php');
    } else if ($itemPOST) include('api/item_post.php');
    if ($isLogged && isset($loginObj)) {
        $uplogin = false;
        $loginObj->setId($user['id']);
        if ($opr) {
            if ($deliveryLogged || $workerLogged || ($parenttype !== 'notifications' && $subtype !== 'pushservice')) $uplogin = true;
        } else if ($itemPOST) $uplogin = true;
        if ($uplogin) $loginObj->UpdateLastLogin();
        if (isset($data->user->city) && isset($data->user->city_group) && isset($data->user->latitude) && isset($data->user->longitude)) {
            $ct = $data->user->city;
            $cgr = $data->user->city_group;
            $lat = $data->user->latitude;
            $lon = $data->user->longitude;
            if ($user['current_city'] != $ct || $user['city_group'] != $cgr || "" . $user['lat'] . "" !== "" . $lat . "" || "" . $user['lon'] . "" !== "" . $lon . "") {
                $loginObj->setCityid($ct);
                $loginObj->setCityGroup($cgr);
                $loginObj->setLat($lat);
                $loginObj->setLon($lon);
                $loginObj->UpdateUserGeo();
            }
        }
    }
    header("Content-type: application/json; charset=utf-8");
    echo json_encode($response);
    $conn->close();
    $db = null;
    exit();
}