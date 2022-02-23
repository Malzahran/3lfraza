<?php
sellerOnly();
if (!empty($_POST['promo']) && !empty($_POST['p_type']) && !empty($_POST['p_value'])) {
    if ($register = $itemObj->registerItem($_POST)) $data = array('status' => 200);
}