<?php
$itemObj = new \iCmsSeller\News();
$action = (isset($_POST['action'])) ? $escapeObj->stringEscape($_POST['action']) : '';
if (!empty($action)) {
    include('requests/seller/news/' . $action . '.php');
}