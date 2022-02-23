<?php
$itemObj = new \iCmsSeller\Item();
$type = $escapeObj->stringEscape($data->misc->type);
if (!empty($type)) include('api/dataop/seller_items/features/' . $type . '.php');