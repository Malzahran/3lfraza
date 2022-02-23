<?php
if ($isLogged) include('api/dataop/menu/' . $user['type'] . '.php');
else $response["result"] = "logout";