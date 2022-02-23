<?php
ini_set('display_errors', 'On');
error_reporting(E_ALL);
require_once('assets/includes/core.php');
function userOnly()
{
    global $isLogged;
    if (!$isLogged) {
        global $conn, $db;
        $conn->close();
        $db = null;
        exit("Please log in to continue!");
    }
}

function administrationOnly()
{
    global $adminLogged, $sellerLogged;
    $close = ($adminLogged || $sellerLogged) ? false : true;
    if ($close) {
        global $conn, $db;
        $conn->close();
        $db = null;
        exit("Please log in with admin account!");
    }
}

function adminOnly()
{
    global $adminLogged;
    if (!$adminLogged) {
        global $conn, $db;
        $conn->close();
        $db = null;
        exit("Please log in with admin account!");
    }
}

function sellerOnly()
{
    global $sellerLogged;
    if (!$sellerLogged) {
        global $conn, $db;
        $conn->close();
        $db = null;
        exit("Please log in with seller account!");
    }
}

$t = (!isset($_GET['t'])) ? "" : $escapeObj->stringEscape($_GET['t']);
$a = (!isset($_GET['a'])) ? "" : $escapeObj->stringEscape($_GET['a']);
$data = array();
if (empty($t)) exit('a');
include('requests/' . $t . '.php');
if (!empty($data)) {
    header("Content-type: application/json; charset=utf-8");
    echo json_encode($data);
}
$conn->close();
$db = null;
exit();