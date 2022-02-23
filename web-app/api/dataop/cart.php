<?php
if ($isLogged) {
    $cartObj = new \iCms\Cart();
    include('api/dataop/cart/' . $subtype . '.php');
}