<?php
if ($isLogged) {
    $cartObj = new \iCms\Cart();
    $cartObj->setUserId($user['id']);
    $cartObj->setStoreId($storeID);
    $cartCount = $cartObj->getCartByStoreId(true);
    $response["result"] = "success";
    $response["cart"]['count'] = $cartCount[0]['count'];
}