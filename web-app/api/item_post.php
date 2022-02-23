<?php
$response['error'] = true;
$response['message'] = Lang('error_try_again');
$type = isset($_POST['type']) ? $escapeObj->stringEscape($_POST['type']) : '';
$posttype = isset($_POST['posttype']) ? $escapeObj->stringEscape($_POST['posttype']) : '';
if (!empty($type) && !empty($posttype)) include('api/dataop/' . $type . '/' . $posttype . '.php');