<?php
if ($user['type'] == "delivery") include('find_orders_delivery.php');
else if ($user['type'] == "worker") include('find_orders_worker.php');