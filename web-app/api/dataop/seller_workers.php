<?php
if ($sellerLogged) include('api/dataop/seller_workers/' . $subtype . '.php');
else $response["result"] = "logout";