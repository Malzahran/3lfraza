<?php
$itemObj = new \iCmsSeller\Promo();
$action = (isset($_POST['action'])) ? $escapeObj->stringEscape($_POST['action']) : '';
if (!empty($action)) {
    include('requests/seller/promo/' . $action . '.php');
}