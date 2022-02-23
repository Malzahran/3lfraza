<?php
administrationOnly();
$reportObj = new \iCms\ReportsUtilities();
include('requests/report/' . $a . '.php');