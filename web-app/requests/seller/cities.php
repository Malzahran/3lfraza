<?php
$cityObj = new \iCmsSeller\City();
$action = (isset($_POST['action'])) ? $escapeObj->stringEscape($_POST['action']) : '';
if (!empty($action)) {
    include('requests/seller/cities/' . $action . '.php');
}