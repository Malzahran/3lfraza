<?php
$catObj = new \iCmsSeller\Category();
$action = (isset($_POST['action'])) ? $escapeObj->stringEscape($_POST['action']) : '';
if (!empty($action)) {
    include('requests/seller/categories/' . $action . '.php');
}