<?php

$version = '1.0.0';
$themeData = array();

if (!defined('PHP_VERSION_ID')) {
    $phpversion = explode('.', PHP_VERSION);
    define('PHP_VERSION_ID', ($phpversion[0] * 10000 + $phpversion[1] * 100 + $phpversion[2]));
}

if (PHP_VERSION_ID < 50400)
    exit("iCms requires atleast PHP version 5.4.0 to work. Your PHP version is: " . PHP_VERSION . ". PHP 5.4.0 was released on March <strong>2012</strong>. It's about time you make the upgrade. Please upgrade your PHP version to 5.4+ or contact your hosting provider and ask them to upgrade it to 5.4+.");

if (!function_exists('mysqli_connect'))
    exit("iCms requires MySQLi Extension to work. MySQLi Extension is either not installed or not loaded in your server! Please install MySQLi extension in your server or contact your hosting provider and ask them to install MySQLi extension.");

if (!extension_loaded('gd') && !extension_loaded('gd2'))
    exit("iCms requires GD Library to process images. GD Library is either not installed or not loaded in your server! Please install GD Library in your server or contact your hosting provider and ask them to install GD Library.");

//error_reporting(0);
set_time_limit(0);
date_default_timezone_set('Africa/Cairo');
session_start();

// Includes
require('config.php');
require('tables.php');

// Connect to SQL Server
$conn = new mysqli($sql_host, $sql_user, $sql_pass, $sql_name);
$conn->set_charset("utf8");

// Check connection
if ($conn->connect_errno) exit($conn->connect_errno);

require_once('classes/autoload.php');
$escapeObj = new \iCms\Escape();
require_once('connect.php');

if (!isset($_SESSION[$config['spf'] . 'reset_time'])) $_SESSION[$config['spf'] . 'reset_time'] = time();
if ($config['reset_time'] > $_SESSION[$config['spf'] . 'reset_time']) {
    session_destroy();
    header("Location: " . $config['site_url']);
}

$utiObj = new \iCms\Utilities();
$L_CODE = $escapeObj->stringEscape($L_CODE);
$dbInit = new iCms\db();
$db = $dbInit->getConnection();
/* ----------------------------- */
/* Log In Check */
function isLogged()
{
    global $config;
    if (isset($_SESSION[$config['spf'] . 'user_id']) && isset($_SESSION[$config['spf'] . 'user_pass'])) {
        $userId = (int)$_SESSION[$config['spf'] . 'user_id'];
        $userPass = $_SESSION[$config['spf'] . 'user_pass'];
        if (isset($_SESSION[$config['spf'] . '_cache_']['user_data'][$userId])) {
            $cche_usrdata = $_SESSION[$config['spf'] . '_cache_']['user_data'][$userId];
            if (is_array($cche_usrdata))
                if ($cche_usrdata['password'] == $userPass) return true;
        }
        global $conn;
        $query = $conn->query("SELECT COUNT(id) AS count FROM " . DB_ACCOUNTS . " WHERE id=$userId AND password='$userPass' AND active=1");
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        return $fetch['count'];
    }
    return false;
}

/* Get Lang Text */
function Lang($keyword = '', $type = 1, $lang_code = '')
{
    global $conn, $L_CODE, $config;
    $type = (int)$type;
    $fetch = array();
    $escapeObj = new \iCms\Escape();
    $keyword = $escapeObj->stringEscape($keyword);
    if (!empty($lang_code)) $lang_code = $escapeObj->stringEscape($lang_code);
    else $lang_code = $escapeObj->stringEscape($L_CODE);

    if (isset($_SESSION[$config['spf'] . 'lang_data']) && $lang_code == $L_CODE && $type != 3) {
        global $lang;
        $fetch['text'] = isset($lang[$keyword]) ? $lang[$keyword] : '';
    } else {
        if ($type == 1) {
            $query = $conn->query("SELECT text FROM language_$lang_code WHERE keyword='$keyword' AND lang_code='$lang_code'");
            if ($query->num_rows == 1) $fetch = $query->fetch_array(MYSQLI_ASSOC);
        } else if ($type == 2) {
            $query = $conn->query("SELECT text FROM " . DB_LANGUAGE_DATA . " WHERE keyword='$keyword' AND lang_code='$lang_code' AND category IN ('category','city')");
            if ($query->num_rows == 1) $fetch = $query->fetch_array(MYSQLI_ASSOC);
        } else if ($type == 3) {
            $query = $conn->query("SELECT text FROM " . DB_LANGUAGE_DATA . " WHERE keyword='$keyword' AND lang_code='$lang_code' AND category='scategory'");
            if ($query->num_rows == 1) $fetch = $query->fetch_array(MYSQLI_ASSOC);
        }
    }
    if (!empty($fetch['text'])) return $fetch['text'];
    return false;
}

/* Set Session Language */
function LangSet($langname = '', $refresh = 0)
{
    global $conn, $config, $escapeObj;
    $lang = array();
    if (empty($langname)) $langname = $_SESSION[$config['spf'] . 'language'];
    $langname = $escapeObj->stringEscape($langname);
    $langExistQuery = $conn->query("SELECT * FROM " . DB_LANGUAGES . " WHERE langcode='$langname'");

    if ($langExistQuery->num_rows == 1) {
        unset($_SESSION[$config['spf'] . 'language']);
        unset($_SESSION[$config['spf'] . 'lang_data']);
        unset($_SESSION[$config['spf'] . 'lang_code']);
        unset($_SESSION[$config['spf'] . 'lang_dir']);
        unset($_SESSION[$config['spf'] . 'system_name']);
        unset($_SESSION[$config['spf'] . 'system_title']);
        unset($_SESSION[$config['spf'] . 'meta_desc']);
        unset($_SESSION[$config['spf'] . 'meta_keys']);
        $langparm = $langExistQuery->fetch_array(MYSQLI_ASSOC);
        $_SESSION[$config['spf'] . 'lang_code'] = $langparm['langcode'];
        $_SESSION[$config['spf'] . 'lang_dir'] = $langparm['langdir'];
        $_SESSION[$config['spf'] . 'system_name'] = $langparm['system_name'];
        $_SESSION[$config['spf'] . 'system_title'] = $langparm['system_title'];
        $_SESSION[$config['spf'] . 'meta_desc'] = $langparm['meta_desc'];
        $_SESSION[$config['spf'] . 'meta_keys'] = $langparm['meta_keys'];
        $config['language'] = $langparm['langcode'];
        $_SESSION[$config['spf'] . 'language'] = $langparm['langcode'];
        if (isset($refresh) && is_numeric($refresh) && $refresh == 1) {
            $langQuery = $conn->query("SELECT keyword,text FROM language_" . $_SESSION[$config['spf'] . 'lang_code']);
            $lang['system_name'] = $_SESSION[$config['spf'] . 'system_name'];
            $lang['system_title'] = $_SESSION[$config['spf'] . 'system_title'];
            $lang['lang_code'] = $_SESSION[$config['spf'] . 'lang_code'];
            $lang['lang_dir'] = $_SESSION[$config['spf'] . 'lang_dir'];
            $lang['meta_desc'] = $_SESSION[$config['spf'] . 'meta_desc'];
            $lang['meta_keys'] = $_SESSION[$config['spf'] . 'meta_keys'];
            while ($langFetch = $langQuery->fetch_array(MYSQLI_ASSOC)) {
                $lang[$langFetch['keyword']] = $langFetch['text'];
            }
            $langDataQuery = $conn->query("SELECT keyword,text FROM " . DB_LANGUAGE_DATA . " WHERE lang_code='" . $_SESSION[$config['spf'] . 'lang_code'] . "' AND category IN ('category','city')");
            if ($langDataQuery->num_rows >= 1) {
                while ($langDataFetch = $langDataQuery->fetch_array(MYSQLI_ASSOC)) {
                    $lang[$langDataFetch['keyword']] = $langDataFetch['text'];
                }
            }
            $_SESSION[$config['spf'] . 'lang_data'] = $lang;
        }
    }
    return $lang;
}

/* Get languages code */
function getLangcode($all = false)
{
    global $conn;
    $select = ($all) ? "*" : "name,langcode,langdir,required";
    $languages_code = array();
    $langQuery = $conn->query("SELECT $select FROM " . DB_LANGUAGES . "");
    if ($langQuery->num_rows >= 1) {
        while ($langFetch = $langQuery->fetch_array(MYSQLI_ASSOC)) {
            $languages_code[] = $langFetch;
        }
        return $languages_code;
    }
    return false;
}

// calculate geographical proximity
function mathGeoProximity( $latitude, $longitude, $radius, $miles = false )
{
    $radius = $miles ? $radius : ($radius * 0.621371192);

    $lng_min = $longitude - $radius / abs(cos(deg2rad($latitude)) * 69);
    $lng_max = $longitude + $radius / abs(cos(deg2rad($latitude)) * 69);
    $lat_min = $latitude - ($radius / 69);
    $lat_max = $latitude + ($radius / 69);

    return array(
        'latitudeMin'  => $lat_min,
        'latitudeMax'  => $lat_max,
        'longitudeMin' => $lng_min,
        'longitudeMax' => $lng_max
    );
}

// calculate geographical distance between 2 points
function mathGeoDistance( $lat1, $lng1, $lat2, $lng2, $miles = false )
{
    $pi80 = M_PI / 180;
    $lat1 *= $pi80;
    $lng1 *= $pi80;
    $lat2 *= $pi80;
    $lng2 *= $pi80;

    $r = 6372.797; // mean radius of Earth in km
    $dlat = $lat2 - $lat1;
    $dlng = $lng2 - $lng1;
    $a = sin($dlat / 2) * sin($dlat / 2) + cos($lat1) * cos($lat2) * sin($dlng / 2) * sin($dlng / 2);
    $c = 2 * atan2(sqrt($a), sqrt(1 - $a));
    $km = $r * $c;
    return ($miles ? ($km * 0.621371192) : $km);
}

/**
 * find address using lat long
 */

function geolocationaddress($lat, $long)
{

    $ch = curl_init();
    $url = "https://api.tomtom.com/search/2/reverseGeocode/$lat,$long.json?key=n6vDMFoF1CAEDUmGteZtjWEaYAzmBffV";
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    $output = curl_exec($ch);
    $output = json_decode($output, true);
    curl_close($ch);
    return $output;
}

/* Send Mails */
function send_mail($to, $subject, $message)
{
    if (!filter_var($to, FILTER_VALIDATE_EMAIL)) return false;
    global $config;
    $headers = "From: " . $config['email'] . "\r\n";
    $headers .= "MIME-Version: 1.0\r\n";
    $headers .= "Content-Type: text/html; charset=UTF-8\r\n";
    if (mail($to, $subject, $message, $headers)) return true;
    return false;
}

/* Smooth Link */
function smoothLink($url = '')
{
    global $config, $L_CODE;
    $urls = array(
        '/^index\.php\?tab1=([^\/]+)&tab2=([^\/]+)&tab3=([^\/]+)$/i',
        '/^index\.php\?tab1=([^\/]+)&tab2=([^\/]+)$/i',
        '/^index\.php\?tab1=([^\/]+)$/i'
    );

    $mods = array(
        $config['site_url'] . '/' . $L_CODE . '/$1/$2/$3',
        $config['site_url'] . '/' . $L_CODE . '/$1/$2',
        $config['site_url'] . '/' . $L_CODE . '/$1'
    );

    if ($config['smooth_links'] == 1) {
        $url = preg_replace($urls, $mods, $url);
    } else {
        $url = $config['site_url'] . '/' . $url;
    }

    return $url;
}

/* Get months */
function getMonths()
{
    global $lang;

    $months[1] = array('january', $lang['january']);
    $months[2] = array('february', $lang['february']);
    $months[3] = array('march', $lang['march']);
    $months[4] = array('april', $lang['april']);
    $months[5] = array('may', $lang['may']);
    $months[6] = array('june', $lang['june']);
    $months[7] = array('july', $lang['july']);
    $months[8] = array('august', $lang['august']);
    $months[9] = array('september', $lang['september']);
    $months[10] = array('october', $lang['october']);
    $months[11] = array('november', $lang['november']);
    $months[12] = array('december', $lang['december']);

    return $months;
}

/* Create captcha */
function createCaptcha()
{
    global $config;
    $image = '';
    $image = @imagecreatetruecolor(80, 30);
    $background_color = @imagecolorallocate($image, 0, 100, 0);
    $text_color = @imagecolorallocate($image, 255, 255, 255);
    $pixel_color = @imagecolorallocate($image, 60, 75, 114);
    @imagefilledrectangle($image, 0, 0, 80, 30, $background_color);

    for ($i = 0; $i < 1000; $i++) {
        @imagesetpixel($image, rand() % 80, rand() % 30, $pixel_color);
    }

    $key = generateKey(6, 6, false, false, true);
    $_SESSION[$config['spf'] . 'captcha_key'] = $key;
    @imagestring($image, 7, 13, 8, $key, $text_color);
    $images = glob('photos/captcha_*.png');

    if (is_array($images)) {
        foreach ($images as $image_to_delete) {
            @unlink($image_to_delete);
        }
    }

    $image_url = 'photos/captcha_' . time() . '_' . mt_rand(1, 9999) . '.png';
    @imagepng($image, $image_url);

    $get = array(
        'image' => $image_url
    );
    return $get;
}

/* Create random key */
function generateKey($minlength = 5, $maxlength = 5, $uselower = true, $useupper = true, $usenumbers = true, $usespecial = false)
{
    $charset = '';

    if ($uselower) {
        $charset .= "abcdefghijklmnopqrstuvwxyz";
    }

    if ($useupper) {
        $charset .= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    }

    if ($usenumbers) {
        $charset .= "123456789";
    }

    if ($usespecial) {
        $charset .= "~@#$%^*()_+-={}|][";
    }

    if ($minlength > $maxlength) {
        $length = mt_rand($maxlength, $minlength);
    } else {
        $length = mt_rand($minlength, $maxlength);
    }

    $key = '';

    for ($i = 0; $i < $length; $i++) {
        $key .= $charset[(mt_rand(0, strlen($charset) - 1))];
    }

    return $key;
}

/* Get languages */
function getLanguages()
{
    global $conn, $config;
    $escapeObj = new \iCms\Escape();
    $langsel = array();
    $language_i = 0;
    $languages_html = array();

    $langQuery = $conn->query("SELECT name,langcode,langdir FROM " . DB_LANGUAGES);

    while ($langFetch = $langQuery->fetch_array(MYSQLI_ASSOC)) {
        $language = $langFetch['langcode'];
        $language = str_replace('languages/', '', $language);
        $language = preg_replace('/([A-Za-z]+)\.php/i', '$1', $language);
        $language_i++;

        if ($config['smooth_links'] == 1) {
            $language_url = '?lang=' . $language;
        } else {
            $query_string = $_SERVER['QUERY_STRING'];
            $query_string = preg_replace('/(\&|)lang\=([A-Za-z0-9_]+)/i', '', $query_string);
            $language_url = 'index.php?' . $query_string . '&lang=' . $language;
            $language_url = $escapeObj->stringEscape(strip_tags($language_url));
        }

        $languages_html[] = '<a href="' . $language_url . '">' . ucwords(str_replace('_', ' ', $langFetch['name'])) . '</a>';
        $langsel[] = array('url' => $language_url, 'name' => $langFetch['name'], 'code' => $langFetch['langcode'], 'dir' => $langFetch['langdir']);
    }

    return array('langhtml' => $languages_html, 'langdata' => $langsel);
}

/* Get user Id */
function getUserId(\mysqli $conn, $u = 0)
{
    if (is_numeric($u)) {
        return (int)$u;
    }
    $escapeObj = new \iCms\Escape();
    $u = $escapeObj->stringEscape($u);

    if (filter_var($u, FILTER_VALIDATE_EMAIL)) {
        $query = $conn->query("SELECT id FROM accounts WHERE email='$u'");
    } else {
        $query = $conn->query("SELECT id FROM accounts WHERE username='$u'");
    }

    if ($query->num_rows == 1) {
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        return $fetch['id'];
    }

    return false;
}

/* Validate username */
function validateUsername($u)
{
    if (strlen($u) > 3 && is_numeric($u) || preg_match('/^[A-Za-z0-9_]+$/', $u)) {
        return true;
    }
}

/* Get username status */
function getUsernameStatus($userName = '')
{
    $escapeObj = new \iCms\Escape();
    $userName = $escapeObj->stringEscape($userName);

    if (!validateUsername($userName)) {
        return 406;
    }

    if (strlen($userName) < 4) {
        return 410;
    }

    global $conn;
    $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE username='$userName'");

    if ($query->num_rows == 0) {
        return 200;
    } else {
        return 410;
    }
}

/* Validate email */
function validateEmail($e)
{
    if (filter_var($e, FILTER_VALIDATE_EMAIL)) {
        return true;
    }
    return false;
}

/* Get Email status */
function getEmailStatus($query = '')
{
    $escapeObj = new \iCms\Escape();
    $query = $escapeObj->stringEscape($query);

    if (!validateEmail($query)) {
        return 406;
    }

    global $conn;
    $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE email='$query'");

    if ($query->num_rows == 0) {
        return 200;
    } else {
        return 410;
    }
}

/* Genrate username and Check State */
function genrateUsername()
{
    $escapeObj = new \iCms\Escape();
    $username = 'z' . generateKey(4, 5, false, false, true);
    $username = $escapeObj->stringEscape($username);

    if (!validateUsername($username)) {
        return 406;
    }

    if (strlen($username) < 4) {
        return 410;
    }

    global $conn;
    $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE username='$username'");

    if ($query->num_rows == 0) {
        return array('code' => 200, 'username' => $username);
    } else {
        genrateUsername();
    }
    return false;
}

/* Register Media */
function registerMedia($upload, $album_id = 0, $type = 0)
{
    global $isLogged;
    if (!$isLogged) {
        return false;
    }

    global $conn, $user;

    if (!file_exists('photos/' . date('Y'))) {
        mkdir('photos/' . date('Y'), 0777, true);
    }

    if (!file_exists('photos/' . date('Y') . '/' . date('m'))) {
        mkdir('photos/' . date('Y') . '/' . date('m'), 0777, true);
    }

    $photo_dir = 'photos/' . date('Y') . '/' . date('m');

    if (is_uploaded_file($upload['tmp_name'])) {
        $escapeObj = new \iCms\Escape();
        $upload['name'] = $escapeObj->stringEscape($upload['name']);
        $name = preg_replace('/([^A-Za-z0-9_\-\.]+)/i', '', $upload['name']);
        $ext = strtolower(substr($upload['name'], strrpos($upload['name'], '.') + 1, strlen($upload['name']) - strrpos($upload['name'], '.')));

        if ($upload['size'] > 1024) {
            if (preg_match('/(jpg|jpeg|png|gif)/', $ext)) {
                list($width, $height) = getimagesize($upload['tmp_name']);

                $query = $conn->query("INSERT INTO " . DB_MEDIA . " (extension,name,type) VALUES ('$ext','$name','photo')");

                if ($query) {
                    $sql_id = $conn->insert_id;
                    $original_file_name = $photo_dir . '/' . generateKey() . '_' . $sql_id . '_' . md5($sql_id);
                    $original_file = $original_file_name . '.' . $ext;

                    if (move_uploaded_file($upload['tmp_name'], $original_file)) {
                        fixOrientation($original_file);

                        $min_size = $width;
                        $m_size = $height;

                        if ($width > $height) {
                            $min_size = $height;
                            $m_size = $width;
                        }

                        $min_size = floor($min_size);

                        if ($min_size > 920) {
                            $min_size = 920;
                        }

                        $imageSizes = array(
                            'thumb' => array(
                                'type' => 'crop',
                                'width' => 200,
                                'height' => 200,
                                'name' => $original_file_name . '_thumb'
                            ),
                            '100x100' => array(
                                'type' => 'crop',
                                'width' => $min_size,
                                'height' => $min_size,
                                'name' => $original_file_name . '_squ'
                            ),
                            '100x60' => array(
                                'type' => 'crop',
                                'width' => $min_size,
                                'height' => floor($min_size * 0.60),
                                'name' => $original_file_name . '_rec'
                            )
                        );

                        foreach ($imageSizes as $ratio => $data) {
                            $save_file = $data['name'] . '.' . $ext;
                            processMedia($data['type'], $original_file, $save_file, $data['width'], $data['height']);
                        }
                        if ($type = 1) {
                            processMedia('resize', $original_file, $original_file, $m_size, $min_size);
                        } else {
                            processMedia('resize', $original_file, $original_file, $min_size, 0);
                        }

                        $conn->query("UPDATE " . DB_MEDIA . " SET user_id=" . $user['id'] . ",album_id=$album_id,url='$original_file_name',temp=0,active=1 WHERE id=$sql_id");

                        $get = array(
                            'id' => $sql_id,
                            'active' => 1,
                            'extension' => $ext,
                            'name' => $name,
                            'url' => $original_file_name
                        );

                        return $get;
                    }
                }
            }
        }
    }
}

/* Fix Orientation */
function fixOrientation($path)
{
    $image = @imagecreatefromjpeg($path);
    $exif = @exif_read_data($path);

    if (!empty($exif['Orientation'])) {
        switch ($exif['Orientation']) {
            case 3:
                $image = imagerotate($image, 180, 0);
                break;

            case 6:
                $image = imagerotate($image, -90, 0);
                break;

            case 8:
                $image = imagerotate($image, 90, 0);
                break;
        }
    }

    @imagejpeg($image, $path);
    return true;
}

/* Process Images */
function processMedia($run, $photo_src, $save_src, $width = 0, $height = 0, $quality = 80)
{

    if (!is_numeric($quality) or $quality < 0 or $quality > 100) {
        $quality = 80;
    }

    if (file_exists($photo_src)) {
        if (strrpos($photo_src, '.')) {
            $ext = substr($photo_src, strrpos($photo_src, '.') + 1, strlen($photo_src) - strrpos($photo_src, '.'));
            $fxt = (in_array($ext, array('jpeg', 'png', 'gif'))) ? $ext : "jpeg";
        } else {
            $ext = $fxt = 0;
        }

        if (preg_match('/(jpg|jpeg|png|gif)/', $ext)) {
            if ($fxt == "gif") {
                copy($photo_src, $save_src);
                return true;
            }

            list($photo_width, $photo_height) = getimagesize($photo_src);
            $create_from = "imagecreatefrom" . $fxt;
            $photo_source = $create_from($photo_src);

            if ($run == "crop") {
                if ($width > 0 && $height > 0) {
                    $crop_width = $photo_width;
                    $crop_height = $photo_height;
                    $k_w = 1;
                    $k_h = 1;
                    $dst_x = 0;
                    $dst_y = 0;
                    $src_x = 0;
                    $src_y = 0;

                    if ($width == 0 or $width > $photo_width) {
                        $width = $photo_width;
                    }

                    if ($height == 0 or $height > $photo_height) {
                        $height = $photo_height;
                    }

                    $crop_width = $width;
                    $crop_height = $height;

                    if ($crop_width > $photo_width) {
                        $dst_x = ($crop_width - $photo_width) / 2;
                    }

                    if ($crop_height > $photo_height) {
                        $dst_y = ($crop_height - $photo_height) / 2;
                    }

                    if ($crop_width < $photo_width || $crop_height < $photo_height) {
                        $k_w = $crop_width / $photo_width;
                        $k_h = $crop_height / $photo_height;

                        if ($crop_height > $photo_height) {
                            $src_x = ($photo_width - $crop_width) / 2;
                        } elseif ($crop_width > $photo_width) {
                            $src_y = ($photo_height - $crop_height) / 2;
                        } else {
                            if ($k_h > $k_w) {
                                $src_x = round(($photo_width - ($crop_width / $k_h)) / 2);
                            } else {
                                $src_y = round(($photo_height - ($crop_height / $k_w)) / 2);
                            }
                        }
                    }

                    $crop_image = @imagecreatetruecolor($crop_width, $crop_height);

                    if ($ext == "png") {
                        @imagesavealpha($crop_image, true);
                        @imagefill($crop_image, 0, 0, @imagecolorallocatealpha($crop_image, 0, 0, 0, 127));
                    }

                    @imagecopyresampled($crop_image, $photo_source, $dst_x, $dst_y, $src_x, $src_y, $crop_width - 2 * $dst_x, $crop_height - 2 * $dst_y, $photo_width - 2 * $src_x, $photo_height - 2 * $src_y);

                    @imageinterlace($crop_image, true);

                    if ($fxt == "jpeg") {
                        @imagejpeg($crop_image, $save_src, $quality);
                    } elseif ($fxt == "png") {
                        @imagepng($crop_image, $save_src);
                    } elseif ($fxt == "gif") {
                        @imagegif($crop_image, $save_src);
                    }

                    @imagedestroy($crop_image);
                }
            } elseif ($run == "resize") {
                if ($width == 0 && $height == 0) {
                    return false;
                }

                if ($width > 0 && $height == 0) {
                    $resize_width = $width;
                    $resize_ratio = $resize_width / $photo_width;
                    $resize_height = floor($photo_height * $resize_ratio);
                } elseif ($width == 0 && $height > 0) {
                    $resize_height = $height;
                    $resize_ratio = $resize_height / $photo_height;
                    $resize_width = floor($photo_width * $resize_ratio);
                } elseif ($width > 0 && $height > 0) {
                    $resize_width = $width;
                    $resize_height = $height;
                }

                if ($resize_width > 0 && $resize_height > 0) {
                    $resize_image = @imagecreatetruecolor($resize_width, $resize_height);

                    if ($ext == "png") {
                        @imagesavealpha($resize_image, true);
                        @imagefill($resize_image, 0, 0, @imagecolorallocatealpha($resize_image, 0, 0, 0, 127));
                    }

                    @imagecopyresampled($resize_image, $photo_source, 0, 0, 0, 0, $resize_width, $resize_height, $photo_width, $photo_height);
                    @imageinterlace($resize_image, true);

                    if ($fxt == "jpeg") {
                        @imagejpeg($resize_image, $save_src, $quality);
                    } elseif ($fxt == "png") {
                        @imagepng($resize_image, $save_src);
                    } elseif ($fxt == "gif") {
                        @imagegif($resize_image, $save_src);
                    }

                    @imagedestroy($resize_image);
                }
            } elseif ($run == "scale") {
                if ($width == 0) {
                    $width = 100;
                }

                if ($height == 0) {
                    $height = 100;
                }

                $scale_width = $photo_width * ($width / 100);
                $scale_height = $photo_height * ($height / 100);
                $scale_image = @imagecreatetruecolor($scale_width, $scale_height);

                if ($ext == "png") {
                    @imagesavealpha($scale_image, true);
                    @imagefill($scale_image, 0, 0, imagecolorallocatealpha($scale_image, 0, 0, 0, 127));
                }

                @imagecopyresampled($scale_image, $photo_source, 0, 0, 0, 0, $scale_width, $scale_height, $photo_width, $photo_height);
                @imageinterlace($scale_image, true);

                if ($fxt == "jpeg") {
                    @imagejpeg($scale_image, $save_src, $quality);
                } elseif ($fxt == "png") {
                    @imagepng($scale_image, $save_src);
                } elseif ($fxt == "gif") {
                    @imagegif($scale_image, $save_src);
                }

                @imagedestroy($scale_image);
            }
        }
    }
}

/* Timezones */
function getTimezones()
{
    $timezones = null;

    if ($timezones === null) {
        $timezones = array();
        $offsets = array();
        $now = new DateTime();

        foreach (DateTimeZone::listIdentifiers() as $timezone) {
            $now->setTimezone(new DateTimeZone($timezone));
            $offsets[] = $offset = $now->getOffset();
            $timezones[$timezone] = '(' . convertToGMT($offset) . ') ' . rearrangeTimezoneName($timezone);
        }

        array_multisort($offsets, $timezones);
    }

    unset($timezones['UTC']);
    return $timezones;
}

function convertToGMT($offset)
{
    $hours = intval($offset / 3600);
    $minutes = abs(intval($offset % 3600 / 60));
    return 'GMT' . ($offset ? sprintf('%+03d:%02d', $hours, $minutes) : '');
}

function rearrangeTimezoneName($name)
{
    $name = str_replace('/', ', ', $name);
    $name = str_replace('_', ' ', $name);
    $name = str_replace('St ', 'St. ', $name);
    return $name;
}

/* Is valid password reset token */
function isValidPasswordResetToken($string)
{
    $stringExp = explode('_', $string);
    $id = (int)$stringExp[0];
    $escapeObj = new \iCms\Escape();
    $password = $escapeObj->stringEscape($stringExp[1]);

    if ($id < 1) {
        return false;
    }

    global $conn;
    $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE id=$id AND password='$password' AND active=1");

    if ($query->num_rows == 1) {
        return array(
            'id' => $id,
            'password' => $password
        );
    } else {
        return false;
    }
}

function getTimeAgo($unix, $details = false)
{
    global $time;

    if ($details == true) {
        if (date('Y', $unix) == date('Y')) {
            if (date('dM', $unix) == date('dM')) {
                return date('h:i A', $unix);
            } else {
                return date('d M - h:i A', $unix);
            }
        } else {
            return date('d M Y - h:i A', $unix);
        }
    } else {
        $interval = 'Just now';

        if ($unix > $time) {
            $diff = $unix - $time;
            $prefix = 'after';
            $math = 'round';
        } else {
            $diff = $time - $unix;
            $prefix = 'before';
            $math = 'floor';
        }

        if ($diff >= 120) {
            $reminder = $math($diff / 60);
            $suffix = 'min';

            if ($diff >= (60 * 60)) {
                $reminder = $math($diff / (60 * 60));
                $suffix = 'hr';

                if ($diff >= (60 * 60 * 24)) {
                    $reminder = $math($diff / (60 * 60 * 24));
                    $suffix = 'day';

                    if ($diff >= (60 * 60 * 24 * 7)) {
                        $reminder = $math($diff / (60 * 60 * 24 * 7));
                        $suffix = 'week';

                        if ($diff > (60 * 60 * 24 * 31)) {
                            $reminder = $math($diff / (60 * 60 * 24 * 31));
                            $suffix = 'month';

                            if ($diff > (60 * 60 * 24 * 30 * 12)) {
                                $reminder = $math($diff / (60 * 60 * 24 * 30 * 12));
                                $suffix = 'yr';
                            }
                        }
                    }
                }
            }

            $interval = $reminder . ' ' . $suffix;

            if ($reminder != 1) {
                $interval .= 's';
            }

            if ($prefix == "after") {
                $interval = 'after ' . $interval;
            }
        }

        return $interval;
    }
}

function pagination($total, $limit = 10, $page = 1, $url = '?')
{

    $adjacents = "2";
    $counter = 0;
    $page = ($page == 0 ? 1 : $page);

    $prev = $page - 1;
    $next = $page + 1;
    $lastpage = ceil($total / $limit);
    $lpm1 = $lastpage - 1;

    $pagination = "";
    if ($lastpage > 1) {
        $pagination .= "<ul class='pagination'>";
        if ($page > $counter + 1) {
            $pagination .= "<li class='previous'>
				<a href='{$url}page=$prev' aria-label='Previous'><i class='arrow_left'></i></a></li>";
        } else {
            $pagination .= "<li class='disabled previous'>
				<a href='#' aria-label='Previous'><i class='arrow_left'></i></a></li>";
        }
        if ($lastpage < 7 + ($adjacents * 2)) {
            for ($counter = 1; $counter <= $lastpage; $counter++) {
                if ($counter == $page)
                    $pagination .= "<li class='active'><a>$counter</a></li>";
                else
                    $pagination .= "<li><a href='{$url}page=$counter'>$counter</a></li>";
            }
        } elseif ($lastpage > 5 + ($adjacents * 2)) {
            if ($page < 1 + ($adjacents * 2)) {
                for ($counter = 1; $counter < 4 + ($adjacents * 2); $counter++) {
                    if ($counter == $page)
                        $pagination .= "<li class='page active'><a>$counter</a></li>";
                    else
                        $pagination .= "<li><a href='{$url}page=$counter'>$counter</a></li>";
                }
                $pagination .= "<li class='dot'>...</li>";
                $pagination .= "<li><a href='{$url}page=$lpm1'>$lpm1</a></li>";
                $pagination .= "<li><a href='{$url}page=$lastpage'>$lastpage</a></li>";
            } elseif ($lastpage - ($adjacents * 2) > $page && $page > ($adjacents * 2)) {
                $pagination .= "<li><a href='{$url}page=1'>1</a></li>";
                $pagination .= "<li><a href='{$url}page=2'>2</a></li>";
                $pagination .= "<li class='dot'>...</li>";
                for ($counter = $page - $adjacents; $counter <= $adjacents + $page; $counter++) {
                    if ($counter == $page)
                        $pagination .= "<li class='page active'><a>$counter</a></li>";
                    else
                        $pagination .= "<li><a href='{$url}page=$counter'>$counter</a></li>";
                }
                $pagination .= "<li class='dot'>..</li>";
                $pagination .= "<li><a href='{$url}page=$lpm1'>$lpm1</a></li>";
                $pagination .= "<li><a href='{$url}page=$lastpage'>$lastpage</a></li>";
            } else {
                $pagination .= "<li><a href='{$url}page=1'>1</a></li>";
                $pagination .= "<li><a href='{$url}page=2'>2</a></li>";
                $pagination .= "<li class='dot'>..</li>";
                for ($counter = $lastpage - (2 + ($adjacents * 2)); $counter <= $lastpage; $counter++) {
                    if ($counter == $page)
                        $pagination .= "<li class='page active'><a>$counter</a></li>";
                    else
                        $pagination .= "<li><a href='{$url}page=$counter'>$counter</a></li>";
                }
            }
        }

        if ($page < $counter - 1) {
            $pagination .= "<li class='next'><a href='{$url}page=$next' aria-label='Next'><i class='arrow_right'></i></a></li>";
        } else {
            $pagination .= "<li class='disabled next'><a href='#' aria-label='Next'><i class='arrow_right'></i></a></li>";
        }
        $pagination .= "</ul>";
    }
    return $pagination;
}

function layoutGenerator($arr = array(), $quen = 0)
{
    $numbers = $arr;
    shuffle($numbers);
    return array_slice($numbers, 0, $quen);
}

/* GET AND POST */
function __POST__($post, $return = '')
{
    $escapeObj = new \iCms\Escape();
    if (!isset($_POST[$post])) {
        return $return;
    } else if (is_numeric($_POST[$post])) {
        $return = (int)$_POST[$post];
    } else if (!empty($_POST[$post])) {
        if (strlen($_POST[$post]) <= 150) {
            $return = $escapeObj->stringEscape($_POST[$post]);
        } else {
            $return = $escapeObj->postEscape($_POST[$post]);
        }
    }
    return $return;
}

function __GET__($get, $return = '')
{
    $escapeObj = new \iCms\Escape();
    if (!isset($_GET[$get])) {
        return $return;
    } else if (is_numeric($_GET[$get])) {
        $return = (int)$_GET[$get];
    } else if (!empty($_GET[$get])) {
        if (strlen($_GET[$get]) >= 1 && strlen($_GET[$get]) <= 150) {
            $return = $escapeObj->stringEscape($_GET[$get]);
        } else {
            $return = $escapeObj->postEscape($_GET[$get]);
        }
    }

    return $return;
}

function grab_meta_tags($Url)
{
    $Urlname = preg_replace('/[^A-Za-z0-9_]/i', '', $Url);
    $get_meta = get_meta_tags($Url);
    $get_html = file_get_contents($Url);
    $title_preg_match = preg_match('/\<title\>(.*?)\<\/title\>/i', $get_html, $title_match);

    if (!empty($title_match[1])) {
        $get_meta['title'] = $title_match[1];
    }

    $img_preg_match = preg_match('/\<img(.*?)src\=\"(.*?)(\.jpg|\.png)\"(.*?)(|\/)\>/i', $get_html, $img_match);

    if (!empty($img_match[2])) {
        $get_meta['img_preview'] = $img_match[2] . $img_match[3];

        if (!preg_match('/http(|s)\:/i', $get_meta['img_preview'])) {
            $get_meta['img_preview'] = 'http:' . $get_meta['img_preview'];
        }
    }

    return $get_meta;
}

function file_ext($Url)
{
    $len = strlen($Url);
    $rpos = strrpos($Url, '.');
    $begin = $len - $rpos;
    $end = $rpos + 1;
    $sub = substr($Url, $end, $begin);
    $lwr = strtolower($sub);
    return $lwr;
}