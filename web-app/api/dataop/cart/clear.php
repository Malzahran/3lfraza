<?php
$cartObj->setUserId($user['id']);
$clearCart = $cartObj->ClearCart();
if ($clearCart) $response['result'] = 'success';