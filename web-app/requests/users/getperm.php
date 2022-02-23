<?php
$data = array();
if (!empty($_POST['type']) && is_numeric($_POST['type']) && $_POST['type'] == 1) {
    if ($admin['system'] == true || $admin['users']['add']) {

        $permdata = \iCms\UI::view('backend/users/permissions');

        if (!empty($permdata)) {
            $data = array(
                'status' => 200,
                'perm' => $permdata
            );
        }

    }
}