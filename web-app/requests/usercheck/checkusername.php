<?php
if ($status = getUsernameStatus($_POST['query'])) {
    $data = array(
        'status' => $status
    );
}