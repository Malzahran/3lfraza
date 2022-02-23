<?php
if (isset($_POST['u_name']) && isset($_POST['u_uname']) && isset($_POST['u_pass']) && isset($_POST['u_phone'])) {
    $email = !empty($_POST['u_email']) ? $_POST['u_email'] : $_POST['u_uname'] . '@' . $site_domain;
    $usersObj = new \iCms\Users();
    $usersObj->setAuth('form');
    $usersObj->setUsertype('worker');
    $usersObj->setName($_POST['u_name']);
    $usersObj->setEmail($email);
    if (isset($_POST['latitude']) && isset($_POST['longitude'])) {
        $usersObj->setLatitude($_POST['latitude']);
        $usersObj->setLongitude($_POST['longitude']);
    }
    $usersObj->setPhone($_POST['u_phone']);
    $usersObj->setLatitude($_POST['user_lat']);
    $usersObj->setLongitude($_POST['user_lon']);
    $usersObj->setStreet($_POST['u_street']);
    $usersObj->setBuilding($_POST['u_building']);
    $usersObj->setFloor($_POST['u_floor']);
    $usersObj->setApartment($_POST['u_apartment']);
    $usersObj->setAdditional($_POST['u_additional']);
    if (isset($_POST['u_birth']) && !empty($_POST['u_birth'])) {
        $birth = explode('/', $_POST['u_birth']);
        $usersObj->setBirthday($birth);
    }
    if (isset($_POST['u_gender']) && !empty($_POST['u_gender'])) 
        $usersObj->setGender($_POST['u_gender'] == 1 ? 'male' : 'female');
    if (isset($_POST['user_city']))
        $usersObj->setCity($_POST['user_city']);
    $usersObj->setQualification($_POST['u_qualification']);
    $usersObj->setNationalID($_POST['u_nid']);
    $usersObj->setStore($user['store_id']);
    $usersObj->setUsername($_POST['u_uname']);
    $usersObj->setPassword($_POST['u_pass']);
    if ($register = $usersObj->register()) {
        $userID = $register;
        if (isset($_FILES['photos']['name'])) {
            $photos = $_FILES['photos'];
            $count = count($photos['name']);
            for ($i = 0; $i < $count; $i++) {
                $params = array(
                    'tmp_name' => $photos['tmp_name'][$i],
                    'name' => $photos['name'][$i],
                    'size' => $photos['size'][$i]
                );
                $media = registerMedia($params);
                if (isset($media['id'])) $query = $conn->query("INSERT INTO " . DB_USER_FILES . " (user_id,file_id) values(" . $userID . ", " . $media['id'] . ")");
            }

        }
        $response['message'] = Lang('success_done_msg');
        $response['error'] = false;
    }
}