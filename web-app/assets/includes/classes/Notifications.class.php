<?php

namespace iCms;

class Notifications
{
    private $conn;
    private $escapeObj;
    private $utiObj;
    private $id;

    private $rid = 0;
    private $text = '';
    private $usid = 0;
    private $stid = 0;
    private $type = 0;
    private $state = 0;


    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        $this->utiObj = new Utilities();
        return $this;
    }

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    public function registerNotif($data = array())
    {
        if (isset($data['rid'])) {
            $this->rid = (int)$data['rid'];
        }
        if (isset($data['text'])) {
            $this->text = $this->escapeObj->postEscape($data['text']);
        }
        if (isset($data['usid'])) {
            $this->usid = (int)$data['usid'];
        }
        if (isset($data['stid'])) {
            $this->stid = (int)$data['stid'];
        }
        if (isset($data['type'])) {
            $this->type = (int)$data['type'];
        }
        if (isset($data['state'])) {
            $this->state = (int)$data['state'];
        }
        global $isLogged, $user;
        $query = $this->getConnection()->query("INSERT INTO " . DB_NOTIFICATIONS . " (active,notifier_id,related_id,seen,text,time,user_id,store_id,type,state) VALUES (1," . ($isLogged ? $user['id'] : 0) . "," . $this->rid . ",0,'" . $this->text . "'," . time() . "," . $this->usid . "," . $this->stid . "," . $this->type . "," . $this->state . ")");

        if ($query) {
            $notfId = $this->getConnection()->insert_id;
            return array(
                'id' => $notfId
            );


        }
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function getNotifications($dlimit = 0, $web = false, $unread = false, $mark = false, $api = false)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $get = array();
        $apidata = array();
        $limit = '';
        $tmpl = '';
        if ($dlimit != 0) {
            $limit = " LIMIT " . (int)$dlimit;
        }
        $where = '';

        global $user;
        if ($user['type'] == "seller") {
            $where .= " AND user_id = 0 AND store_id = " . $user['store_id'];
        } else {
            $where .= " AND user_id =  " . $user['id'] . "";
        }
        if ($unread == true) {
            $where .= " AND seen = 0";
        }


        $querytotal = $this->getConnection()->query("SELECT COUNT(id) AS count FROM " . DB_NOTIFICATIONS . " WHERE active = 1 $where ");
        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if ($web == true) {
            $order = "ORDER BY ";
            $order .= "id DESC";
            $queryText = "SELECT * FROM " . DB_NOTIFICATIONS . " WHERE active = 1 $where ";
            $queryText .= "$order $limit";

            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $tmpl .= $this->getNotificationTemp($fetch2);
                    $get[] = $fetch2;
                    if ($mark == true && $fetch2['store_id'] == 0) $this->getConnection()->query("UPDATE " . DB_NOTIFICATIONS . " SET seen = 1 WHERE id = " . $fetch2['id'] . "");
                }

            }
            $this->data = array('data' => $get, 'count' => $counttotal, 'tmpl' => $tmpl);
        } else if ($api == true) {
            $order = "ORDER BY ";
            $order .= "id DESC";
            $queryText = "SELECT * FROM " . DB_NOTIFICATIONS . " WHERE active = 1 $where ";
            $queryText .= "$order $limit";

            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $apidata[] = $this->getNotificationApiTemp($fetch2);
                    $get[] = $fetch2;
                    if ($mark == true) {
                        $queryupdate = $this->getConnection()->query("UPDATE " . DB_NOTIFICATIONS . " SET seen = 1 WHERE id = " . $fetch2['id'] . "");
                    }
                }

            }
            $this->data = array('data' => $get, 'count' => $counttotal, 'tmpl' => $apidata);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        } else {
            return false;
        }

    }

    public function getNotificationTemp($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        $ntitle = '';
        $nurl = '';
        global $user, $themeData;
        switch ($data['type']) {
            case 1:
                $userObj = new \iCms\User();
                $userInfo = $userObj->getById((int)$data['notifier_id']);
                $nurl = smoothLink('index.php?tab1=messages?recipient_id=' . $data['notifier_id']);
                $ntype = Lang('msg_label', 2);
                $ntitle = Lang('msg_notification', 2) . $userInfo['first_name'] . ' ' . $userInfo['last_name'];
                break;
            case 4:
                $name = '';
                $cid = 0;
                $type = ($data['store_id'] != 0) ? 2 : 1;
                if ($data['user_id'] != 0 && $data['store_id'] != 0) {
                    $storeINFO = $this->utiObj->getStore($data['store_id']);
                    if (isset($storeINFO['store']['store_id'])) {
                        $name = $storeINFO['dir']['title'];
                        $cid = $data['store_id'];
                    }
                } else {
                    $userObj = new \iCms\User();
                    $userInfo = $userObj->getById((int)$data['notifier_id']);
                    $name = $userInfo['name'];
                    $cid = $data['notifier_id'];
                }
                $ntitle = Lang('msg_notification') . $name;
                global $backend_url;
                $nurl = smoothLink('index.php?tab1=' . $backend_url . '&tab2=messages?recipient_id=' . $cid . '&type=' . $type);
                break;
            case 5:
                switch ($data['state']) {
                    case 1:
                        if ($user['type'] == "delivery") $ntitle = Lang('new_delviery_message');
                        else $ntitle = Lang('new_order_message');
                        break;
                    case 2:
                        $storeINFO = $this->utiObj->getStore($data['store_id']);
                        if (isset($storeINFO['store']['store_id'])) {
                            $name = $storeINFO['dir']['title'];
                        }
                        $ntitle = Lang('your_order_from_message') . $name . ' ' . Lang('order_prepration_message') . $data['text'] . ' ' . Lang('minute_label');
                        break;
                    case 4:
                        $ntitle = Lang('order_prepared_notification');
                        break;
                    case 5:
                        $ntitle = Lang('order_on_way_notification');
                        break;
                    case 6:
                        $ntitle = Lang('order_delivered_notification');
                        break;
                    case 7:
                        $storeINFO = $this->utiObj->getStore($data['store_id']);
                        if (isset($storeINFO['store']['store_id'])) $name = $storeINFO['dir']['title'];
                        $ntitle = Lang('order_refused_message') . ' ' . $name;
                        break;
                }
                global $sellerLogged;
                $nurl = ($sellerLogged) ? smoothLink('index.php?tab1=admin') : smoothLink('index.php?tab1=backend&tab2=orders');
                break;
            case 6:
                global $sellerLogged;
                $ntitle = Lang('new_worker_registration');
                $nurl = ($sellerLogged) ? smoothLink('index.php?tab1=admin&tab2=worker') : '';
                break;
            case 7:
                global $sellerLogged;
                $ntitle = Lang('new_delivery_registration');
                $nurl = ($sellerLogged) ? smoothLink('index.php?tab1=admin&tab2=delivery') : '';
                break;
        }
        $datetimeFormat = 'd-m-Y g:i A';
        $date = new \DateTime();
        $date->setTimestamp((int)$data['time']);
        $ntime = $date->format($datetimeFormat);

        $themeData['notif_url'] = $nurl;
        $themeData['notif_title'] = $ntitle;
        $themeData['notif_time'] = $ntime;
        $notification_temp = 'backend/header/notification-row';


        return \iCms\UI::view($notification_temp);

    }

    public function getNotificationApiTemp($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        $click = 6;
        $id = (int)$data['id'];
        $title2 = $icon = null;
        $intent = array('intent' => 0);
        global $user, $site_url;
        switch ($data['type']) {
            case 1:
                $userObj = new \iCms\User();
                $userInfo = $userObj->getById((int)$data['notifier_id']);
                $icon = $site_url . '/media/dir_icon.png';
                $id = $userInfo['id'];
                $title2 = $userInfo['name'];
                $ntype = Lang('msg_label', 2);
                $ntitle = Lang('msg_notification', 2) . $userInfo['first_name'] . ' ' . $userInfo['second_name'];
                break;
            case 4:
                $name = '';
                $cid = 0;
                $type = ($data['store_id'] != 0) ? 2 : 1;
                if ($data['user_id'] != 0 && $data['store_id'] != 0) {
                    $storeINFO = $this->utiObj->getStore($data['store_id']);
                    if (isset($storeINFO['store']['store_id'])) {
                        $name = $storeINFO['dir']['title'];
                        $cid = $data['store_id'];
                    }
                } else {
                    $userObj = new \iCms\User();
                    $userInfo = $userObj->getById((int)$data['notifier_id']);
                    $name = $userInfo['name'];
                    $cid = $data['notifier_id'];
                }
                $ntitle = Lang('msg_notification') . $name;
                $intent = array('intent' => 21, 'reqtype' => $name, 'reqid' => $cid, 'reqaid' => $type);
                $icon = $site_url . '/media/msg_icon.png';
                break;
            case 5:
                switch ($data['state']) {
                    case 1:
                        if ($user['type'] == "delivery") $ntitle = Lang('new_delviery_message');
                        else $ntitle = Lang('new_order_message');
                        break;
                    case 2:
                        $storeINFO = $this->utiObj->getStore($data['store_id']);
                        if (isset($storeINFO['store']['store_id'])) {
                            $name = $storeINFO['dir']['title'];
                        }
                        $ntitle = Lang('your_order_from_message') . $name . ' ' . Lang('order_prepration_message') . $data['text'] . ' ' . Lang('minute_label');
                        break;
                    case 4:
                        $ntitle = Lang('order_prepared_notification');
                        break;
                    case 5:
                        $ntitle = Lang('order_on_way_notification');
                        break;
                    case 6:
                        $ntitle = Lang('order_delivered_notification');
                        break;
                    case 7:
                        $storeINFO = $this->utiObj->getStore($data['store_id']);
                        if (isset($storeINFO['store']['store_id'])) $name = $storeINFO['dir']['title'];
                        $ntitle = Lang('order_refused_message') . ' ' . $name;
                        break;
                }
                $intent = array('intent' => 15, 'reqtype' => 'orders', 'reqstype' => 'getorders');
                $icon = $site_url . '/media/cart_icon.png';
                break;
            case 6:
                $ntitle = Lang('new_worker_registration');
                $intent = array('intent' => 1, 'reqtype' => 'seller_workers', 'reqstype' => 'get');
                $icon = $site_url . '/media/cart_icon.png';
                break;
            case 7:
                $ntitle = Lang('new_delivery_registration');
                $intent = array('intent' => 1, 'reqtype' => 'seller_delivery', 'reqstype' => 'get');
                $icon = $site_url . '/media/cart_icon.png';
                break;
        }
        $datetimeFormat = 'd-m-Y g:i A';
        $date = new \DateTime();
        $date->setTimestamp((int)$data['time']);
        $ntime = $date->format($datetimeFormat);
        $state = ($data['seen'] == 0) ? Lang('unread_label') : null;

        $notf = array('title' => $ntitle, 'title2' => $title2, 'desc' => $state, 'custom_icon' => $icon, 'click' => $click, 'intentData' => $intent, 'id' => $id, 'time' => $ntime);


        return $notf;

    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function setTtype($tp)
    {
        $this->type = (int)$tp;
    }

    public function setUser($usrid)
    {
        $this->userid = (int)$usrid;
    }

    public function setText($txt)
    {
        $this->text = $this->escapeObj->stringEscape($txt);
    }

    public function setState($stat)
    {
        $this->state = $this->escapeObj->stringEscape($stat);
    }

}