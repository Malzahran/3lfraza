<?php
$itemObj = new \iCmsSeller\Item();
$action = (isset($_POST['action'])) ? $escapeObj->stringEscape($_POST['action']) : '';
if (!empty($action)) {
    include('requests/seller/items/' . $action . '.php');
}