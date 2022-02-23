<?php
if ($sellerLogged) include('api/dataop/seller_users/' . $subtype . '.php');
else $response["result"] = "logout";