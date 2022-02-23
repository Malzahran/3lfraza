<?php
if ($sellerLogged) include('api/dataop/seller_delivery/' . $subtype . '.php');
else $response["result"] = "logout";