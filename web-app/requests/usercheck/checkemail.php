<?php
if ($status = getEmailStatus($_POST['query'])) {
    $data = array(
        'status' => $status
    );
}
if ($isLogged) {
    if ($status != 200 && $_POST['query'] == $user['email']) {
        $data = array(
            'status' => 200
        );
    }
}