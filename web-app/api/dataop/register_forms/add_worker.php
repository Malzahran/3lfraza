<?php
if (isset($_POST['u_name']) && isset($_POST['u_uname']) && isset($_POST['u_pass']) && isset($_POST['u_phone']) && isset($_POST['u_email'])) {
    $usersObj = new \iCms\Users();
    $usersObj->setAuth('form');
    $usersObj->setUsertype('worker');
    $usersObj->setName($_POST['u_name']);
    $usersObj->setEmail($_POST['u_email']);
    $usersObj->setActive(0);
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
    $usersObj->setStore(1);
    $usersObj->setUsername($_POST['u_uname']);
    $usersObj->setPassword($_POST['u_pass']);
    $register = $usersObj->register();
    if ($register) {
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
        $ntfObj = new \iCms\Notifications();
        $notarray = array('stid' => 1, 'type' => 6);
        if ($ntfObj->registerNotif($notarray)) {
            $response['message'] = Lang('account_registration_msg');
            $response['success_action'] = 3;
            $response['error'] = false;
        }
    }
}