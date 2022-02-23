<?php
sellerOnly();
if (!empty($_POST['title'])) {
    if ($cityObj->register($_POST)) {
        LangSet($L_CODE, 1);
        $data = array(
            'status' => 200
        );

    }
}