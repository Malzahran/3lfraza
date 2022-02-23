<?php

$version = '1.0.0';
$themeData = array();

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
require_once('connect-api.php');

$escapeObj = new \iCms\Escape();
$utiObj = new \iCms\Utilities();
$L_CODE = $escapeObj->stringEscape($L_CODE);
$dbInit = new iCms\db();
$db = $dbInit->getConnection();


/* ----------------------------- */
/* Log In Check */
function isApiLogged($username = '', $token = '')
{
    global $conn;
    if (!empty($username) && !empty($token)) {
        $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE  logintoken='$token' AND username='$username' AND active=1");
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        return $fetch['id'];
    }
    return false;
}

/* Get Lang Text */
function Lang($keyword = '', $type = 1, $lang_code = '')
{
    global $conn;
    $type = (int)$type;
    $fetch = null;
    $escapeObj = new \iCms\Escape();
    $keyword = $escapeObj->stringEscape($keyword);
    if (!empty($lang_code)) $lang_code = $escapeObj->stringEscape($lang_code);
    else {
        global $L_CODE;
        $lang_code = $escapeObj->stringEscape($L_CODE);
    }
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

    if (!empty($fetch['text'])) return $fetch['text'];
    return false;
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
    if ($config['smooth_links'] == 1) $url = preg_replace($urls, $mods, $url);
    else $url = $config['site_url'] . '/' . $url;
    return $url;
}

/* Get user Id */
function getUserId(\mysqli $conn, $u = 0)
{
    if (is_numeric($u)) return (int)$u;
    $escapeObj = new \iCms\Escape();
    $u = $escapeObj->stringEscape($u);

    if (filter_var($u, FILTER_VALIDATE_EMAIL)) $query = $conn->query("SELECT id FROM accounts WHERE email='$u'");
    else $query = $conn->query("SELECT id FROM accounts WHERE username='$u'");
    if ($query->num_rows == 1) {
        $fetch = $query->fetch_array(MYSQLI_ASSOC);
        return $fetch['id'];
    }
    return false;
}

/* Create random key */
function generateKey($minlength = 5, $maxlength = 5, $uselower = true, $useupper = true, $usenumbers = true, $usespecial = false)
{
    $charset = '';

    if ($uselower) $charset .= "abcdefghijklmnopqrstuvwxyz";
    if ($useupper) $charset .= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    if ($usenumbers) $charset .= "123456789";
    if ($usespecial) $charset .= "~@#$%^*()_+-={}|][";
    if ($minlength > $maxlength) $length = mt_rand($maxlength, $minlength);
    else $length = mt_rand($minlength, $maxlength);
    $key = '';
    for ($i = 0; $i < $length; $i++) {
        $key .= $charset[(mt_rand(0, strlen($charset) - 1))];
    }
    return $key;
}

/* Validate username */
function validateUsername($u)
{
    if (strlen($u) > 3 && is_numeric($u) || preg_match('/^[A-Za-z0-9_]+$/', $u)) return true;
    return false;
}

/* Get username status */
function getUsernameStatus($userName = '')
{
    $escapeObj = new \iCms\Escape();
    if (empty($userName)) $userName = 'f' . generateKey(4, 5, false, false, true);
    $userName = $escapeObj->stringEscape($userName);
    if (!validateUsername($userName)) return 406;
    if (strlen($userName) < 4) return 410;
    global $conn;
    $query = $conn->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE username='$userName'");
    if ($query->num_rows == 0) return array('code' => 200, 'username' => $userName);
    else getUsernameStatus();
}

function getMessageRecipients($timelineId = 0, $limit = 10)
{
    global $isLogged;
    if (!$isLogged) return false;
    global $conn;
    $get = array();
    $excludes = array();
    $timelineId = (int)$timelineId;
    $limit = (int)$limit;
    if ($timelineId < 1) {
        global $user;
        $timelineId = $user['id'];
    }
    if ($limit < 1) $limit = 10;
    $queryText = "SELECT id FROM " . DB_ACCOUNTS . " WHERE id IN (SELECT user_id FROM " . DB_MESSAGES . " WHERE recipient_id=$timelineId AND active=1 AND seen=0 ORDER BY seen ASC, id DESC) AND active=1";
    $query = $conn->query($queryText);
    if ($query) {
        while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
            $timelineObj = new \iCms\User();
            $timelineObj->setId($fetch['id']);
            $get[] = $timelineObj->getRows();
        }
        return $get;
    }
    return array();
}

function getMessages($data = array())
{
    global $isLogged;
    if (!$isLogged) return false;
    global $conn;
    $get = array();
    if (empty($data['recipient_id']) or !is_numeric($data['recipient_id'])) return false;
    $recipientId = (int)$data['recipient_id'];
    $recipientObj = new \iCms\User();
    $recipientObj->setId($recipientId);
    $recipient = $recipientObj->getRows();
    if (!isset($recipient['id'])) return false;
    $queryText = "SELECT id,active,media_id,recipient_id,seen,text,time,user_id FROM " . DB_MESSAGES . " WHERE active=1";
    if (!empty($data['message_id']) && is_numeric($data['message_id'])) {
        $messageId = (int)$data['message_id'];
        $queryText .= " AND id=$messageId";
    } else if (!empty($data['before_message_id']) && is_numeric($data['before_message_id'])) {
        $beforeMessageId = (int)$data['before_message_id'];
        $queryText .= " AND id < $beforeMessageId";
    } else if (!empty($data['after_message_id']) && is_numeric($data['after_message_id'])) {
        $afterMessageId = (int)$data['after_message_id'];
        $queryText .= " AND id > $afterMessageId";
    }
    if (empty($data['timeline_id']) or $data['timeline_id'] < 1) {
        global $user;
        $timelineId = $user['id'];
        $timeline = $user;
    } else {
        $timelineId = (int)$data['timeline_id'];
        $timelineObj = new \iCms\User();
        $timelineObj->setId($timelineId);
        $timeline = $timelineObj->getRows();
    }

    if (!isset($timeline['id'])) return false;
    if ($timelineId == $recipientId) return false;

    if (isset($data['new']) && $data['new'] == true) $queryText .= " AND seen=0 AND user_id=" . $recipientId . " AND recipient_id=" . $timelineId;
    else $queryText .= " AND ((user_id=" . $timelineId . " AND recipient_id=" . $recipientId . ") OR (user_id=" . $recipientId . " AND recipient_id=" . $timelineId . "))";
    $query = $conn->query($queryText);
    $queryLimitFrom = $query->num_rows - 10;
    if ($queryLimitFrom < 1) $queryLimitFrom = 0;
    $queryText .= " ORDER BY id ASC LIMIT $queryLimitFrom,10";
    $query2 = $conn->query($queryText);
    if ($query2->num_rows == 0) return false;
    $escapeObj = new \iCms\Escape();
    while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
        $timelineObj = new \iCms\User();
        $timelineObj->setId($fetch2['user_id']);
        $fetch2['account'] = $fetch2['timeline'] = $timelineObj->getRows();
        $fetch2['owner'] = false;
        $fetch2['media'] = array();
        if (!empty($fetch2['media_id'])) {
            $mediaObj = new \iCms\Media();
            $mediaObj->setId($fetch2['media_id']);
            $fetch2['media'] = $mediaObj->getRows();
        }

        if ($fetch2['recipient_id'] == $timelineId && $fetch2['seen'] == 0) $conn->query("UPDATE " . DB_MESSAGES . " SET seen=" . time() . " WHERE id=" . $fetch2['id']);
        $get[] = $fetch2;
    }
    return $get;
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

/* Register Media */
function registerMedia($upload, $album_id = 0, $uid = 0)
{
    if ($uid != 0) $uid = (int)$uid;
    global $conn;

    if (!file_exists('photos/' . date('Y'))) mkdir('photos/' . date('Y'), 0777, true);
    if (!file_exists('photos/' . date('Y') . '/' . date('m'))) mkdir('photos/' . date('Y') . '/' . date('m'), 0777, true);
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

                        if ($width > $height) {
                            $min_size = $height;
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
                            '100x75' => array(
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

                        processMedia('resize', $original_file, $original_file, $min_size, 0);

                        $conn->query("UPDATE " . DB_MEDIA . " SET user_id=$uid,album_id=$album_id,url='$original_file_name',temp=0,active=1 WHERE id=$sql_id");

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
    @$image = imagecreatefromjpeg($path);
    @$exif = exif_read_data($path);

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

    if (!is_numeric($quality) or $quality < 0 or $quality > 100) $quality = 100;
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