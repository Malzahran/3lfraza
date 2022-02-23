<?php
if ($sellerLogged) include('api/dataop/seller_cats/' . $subtype . '.php');
else $response["result"] = "logout";