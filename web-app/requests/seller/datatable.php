<?php
sellerOnly();
$dataObj = new \iCmsSeller\DataGet();
$type = (isset($_POST['t_type'])) ? $escapeObj->stringEscape($_POST['t_type']) : '';
if (!empty($type)) {
    include('requests/seller/datatable/' . $type . '.php');
}