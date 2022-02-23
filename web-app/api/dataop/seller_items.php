<?php
if ($sellerLogged) include('api/dataop/seller_items/' . $subtype . '.php');
else $response["result"] = "logout";