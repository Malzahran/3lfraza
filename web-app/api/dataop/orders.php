<?php
if ($isLogged) include('api/dataop/orders/' . $subtype . '.php');
else $response["result"] = "logout";