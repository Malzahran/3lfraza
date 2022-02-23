<?php
if (!empty($_POST['itemid'])) {
    $itemid = (int)$_POST['itemid'];
    $uinfo = $userDObj->getById($itemid);
    $themeData['user_edit_id'] = $uinfo['id'];
    $themeData['user_edit_username'] = $uinfo['username'];
    $themeData['user_edit_name'] = $uinfo['name'];
    $themeData['user_edit_email'] = $uinfo['email'];
    $themeData['user_edit_phone'] = $uinfo['phone'];
    $themeData['user_edit_nid'] = $uinfo['nid'];

    $themeData['user_edit_img'] = $uinfo['avatar_url'];
    $themeData['user_edit_perm_head'] = 'hidden';
    $themeData['user_edit_password'] = 'hidden';
    if ($uinfo['authmethod'] == "web" || $uinfo['authmethod'] == "form") {
        $themeData['user_edit_password'] = null;
    }
    if ($uinfo['type'] == "moderator") {
        $themeData['user_edit_perm_head'] = null;
        $perm = $userDObj->isSysAdmin($uinfo['id']);
        if ($perm) {
            foreach ($perm as $key => $value) {
                if (is_array($value)) {
                    foreach ($value as $key2 => $value2) {
                        if (is_array($value)) {
                            $themeData['perm_' . $key . '_' . $key2] = 'checked';
                        }
                    }
                } else {
                    $themeData['perm_' . $key] = 'checked';
                }
            }
        }
        $themeData['edit_user_perm_tab'] = \iCms\UI::view('backend/users/permissionsedit');
    }
    $themeData['user_active'] = $uinfo['active'] == 1 ? 'checked' : '';

    if ($uinfo['city'] != null) {
        $city = $lang[$uinfo['city']];
    } else {
        $city = $lang['no_main'];
    }
    $themeData['cityname'] = $city;
    if ($uinfo['type'] == 'delivery') {
        $html = \iCms\UI::view('backend/delivery/edit-content');
    } else if ($uinfo['type'] == 'worker') {
        $html = \iCms\UI::view('backend/worker/edit-content');
    } else {
        $html = $sellerLogged ? \iCms\UI::view('backend/seller/users/edit-content') : \iCms\UI::view('backend/users/edit-content');
    }
    $data = array(
        'status' => 200,
        'html' => $html
    );
}