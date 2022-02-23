<?php

namespace iCms;

class Messages
{
    private $conn;
    private $escapeObj;


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

    public function registerMessage($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        global $config;
        $post_ability = false;
        $other_media = false;

        if (empty($data['user_id']) or !is_numeric($data['user_id']) or $data['user_id'] < 1) {
            global $user, $userObj;
            $userId = $user['id'];
            $userFObj = $userObj;
            $userfetch = $user;
        } else {
            $userId = (int)$data['user_id'];
            $userFObj = new User();
            $userFObj->setId($userId);
            $userfetch = $userFObj->getRows();
        }

        if (!$userFObj->isAdmin()) {
            return false;
        }

        $text = '';
        $media_id = 0;
        $recipientId = 0;
        $store_id = 0;
        $type = (int)$data['type'];

        if (!empty($data['text'])) {
            $text = $data['text'];
            $text = $this->escapeObj->postEscape($text);
            $post_ability = true;
        }

        if (!empty($data['recipient_id']) && is_numeric($data['recipient_id']) && $data['recipient_id'] > 0) {
            $recipientId = (int)$data['recipient_id'];
        }

        if (!empty($data['store_id']) && is_numeric($data['store_id']) && $data['store_id'] > 0) {
            $store_id = (int)$data['store_id'];
        }
        $st = 1;
        if ($recipientId > 0) {
            $recipientObj = new User();
            $recipientObj->setId($recipientId);
            $recipient = $recipientObj->getRows();

            if (empty($recipient['id'])) {
                return false;
            }

            if ($userId == $recipientId) {
                return false;
            }
        }

        if ($store_id > 0) {
            $st = 2;
            $utiObj = new \iCms\Utilities();
            $storeINFO = $utiObj->getStore($store_id, true);
            if (!isset($storeINFO['store']['store_id'])) {
                return false;
            }
        }

        if ($post_ability) {
            $query = $this->getConnection()->query("INSERT INTO " . DB_MESSAGES . " (active,type,media_id,recipient_id,text,time,user_id,store_id) VALUES (1,$type,$media_id,$recipientId,'$text'," . time() . ",$userId,$store_id)");
            if ($query) {
                $msg_id = $this->getConnection()->insert_id;

                if ($data['disable_notf'] != true) {
                    $notarray = array('rid' => $msg_id, 'usid' => $recipientId, 'stid' => $store_id, 'type' => 4, 'state' => $st);
                    $notfication = new \iCms\Notifications();
                    $notfset = $notfication->registerNotif($notarray);
                }

                return $msg_id;
            }
        }
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function getMessages($data = array())
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $get = array();
        if (empty($data['recipient_id']) or !is_numeric($data['recipient_id'])) {
            return false;
        }

        $recipientId = (int)$data['recipient_id'];
        $recipientObj = new User();
        $recipientObj->setId($recipientId);
        $recipient = $recipientObj->getRows();

        if (!isset($recipient['id'])) {
            return false;
        }

        $queryText = "SELECT id,active,media_id,recipient_id,seen,text,time,user_id,store_id FROM " . DB_MESSAGES . " WHERE active=1";

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

        if (empty($data['user_id']) or $data['user_id'] < 1) {
            global $user, $userObj;
            $userId = $user['id'];
            $userFObj = $userObj;
            $userfetch = $user;
        } else {
            $userId = (int)$data['user_id'];
            $userFObj = new User();
            $userFObj->setId($userId);
            $userfetch = $userFObj->getRows();
        }

        if (!isset($userfetch['id'])) {
            return false;
        }

        if (!$userFObj->isAdmin()) {
            return false;
        }

        if ($userId == $recipientId) {
            return false;
        }

        if (isset($data['new']) && $data['new'] == true) {
            $queryText .= " AND seen=0 AND ((user_id=" . $recipientId . " AND recipient_id=" . $userId . " AND store_id = 0) OR (recipient_id=" . $userId . " AND store_id = " . $recipientId . "))";
        } else {
            $queryText .= " AND (((user_id=" . $userId . " AND recipient_id=" . $recipientId . ") OR (user_id=" . $recipientId . " AND recipient_id=" . $userId . ") AND store_id = 0) OR ((user_id=" . $userId . " AND store_id=" . $recipientId . ") OR (store_id=" . $recipientId . " AND recipient_id=" . $userId . ")))";
        }

        $query = $this->getConnection()->query($queryText);
        $queryLimitFrom = $query->num_rows - 10;

        if ($queryLimitFrom < 1) {
            $queryLimitFrom = 0;
        }

        $queryText .= " ORDER BY id ASC LIMIT $queryLimitFrom,10";
        $query2 = $this->getConnection()->query($queryText);

        if ($query2->num_rows == 0) {
            return false;
        }
        $utiObj = new \iCms\Utilities();
        $mediaObj = new \iCms\Media();
        global $site_url;
        $storeimg = $site_url . '/media/store_icon.png';

        while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
            if ($fetch2['store_id'] != 0 && $fetch2['recipient_id'] != 0) {
                $storeINFO = $utiObj->getStore($fetch2['store_id']);
                if (isset($storeINFO['store']['store_id'])) {
                    $simg = $storeimg;
                    $pInfo = $utiObj->getDirectory(0, 0, $storeINFO['dir']['parent_id']);
                    if ($pInfo['featured_image'] != 0) {
                        $fimage = $mediaObj->getById($pInfo['featured_image']);
                        $simg = $fimage['each'][0]['thumb_url'];
                    }
                    $fetch2['account'] = array('name' => $storeINFO['dir']['title'], 'thumbnail_url' => $simg);
                }
            } else {
                $userFObj = new User();
                $userFObj->setId($fetch2['user_id']);

                $fetch2['account'] = $userFObj->getRows();
                $fetch2['owner'] = false;

                if ($userFObj->isAdmin()) {
                    $fetch2['owner'] = true;
                }

                if ($fetch2['recipient_id'] == $userId && $fetch2['seen'] == 0) {
                    $this->getConnection()->query("UPDATE " . DB_MESSAGES . " SET seen=" . time() . " WHERE id=" . $fetch2['id']);
                }
            }

            $get[] = $fetch2;
        }

        return $get;

    }

    public function getStoreMessages($data = array())
    {
        global $sellerLogged;
        if (!$sellerLogged) {
            return false;
        }

        $get = array();
        if (empty($data['recipient_id']) or !is_numeric($data['recipient_id'])) {
            return false;
        }

        $recipientId = (int)$data['recipient_id'];
        $recipientObj = new User();
        $recipientObj->setId($recipientId);
        $recipient = $recipientObj->getRows();

        if (!isset($recipient['id'])) {
            return false;
        }

        $queryText = "SELECT id,active,media_id,recipient_id,seen,text,time,user_id,store_id FROM " . DB_MESSAGES . " WHERE active=1";

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

        if (empty($data['user_id']) or $data['user_id'] < 1) {
            global $user;
            $userId = $user['store_id'];
        } else {
            $userId = (int)$data['user_id'];
        }

        if ($userId == $recipientId) {
            return false;
        }

        if (isset($data['new']) && $data['new'] == true) {
            $queryText .= " AND seen=0 AND user_id=" . $recipientId . " AND store_id=" . $userId;
        } else {
            $queryText .= " AND ((store_id=" . $userId . " AND recipient_id=" . $recipientId . ") OR (user_id=" . $recipientId . " AND store_id=" . $userId . "))";
        }

        $query = $this->getConnection()->query($queryText);
        $queryLimitFrom = $query->num_rows - 10;

        if ($queryLimitFrom < 1) {
            $queryLimitFrom = 0;
        }

        $queryText .= " ORDER BY id ASC LIMIT $queryLimitFrom,10";
        $query2 = $this->getConnection()->query($queryText);

        if ($query2->num_rows == 0) {
            return false;
        }
        $utiObj = new \iCms\Utilities();
        $mediaObj = new \iCms\Media();
        global $site_url;
        $storeimg = $site_url . '/media/store_icon.png';

        while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
            if ($fetch2['store_id'] != 0 && $fetch2['recipient_id'] != 0) {
                $storeINFO = $utiObj->getStore($fetch2['store_id']);
                if (isset($storeINFO['store']['store_id'])) {
                    $simg = $storeimg;
                    $pInfo = $utiObj->getDirectory(0, 0, $storeINFO['dir']['parent_id']);
                    if ($pInfo['featured_image'] != 0) {
                        $fimage = $mediaObj->getById($pInfo['featured_image']);
                        $simg = $fimage['each'][0]['thumb_url'];
                    }
                    if ($sellerLogged) {
                        $fetch2['owner'] = true;
                    }
                    $fetch2['account'] = array('name' => $storeINFO['dir']['title'], 'thumbnail_url' => $simg);
                }
            } else {
                $userFObj = new User();
                $userFObj->setId($fetch2['user_id']);

                $fetch2['account'] = $userFObj->getRows();
                $fetch2['owner'] = false;

                if ($userFObj->isAdmin()) {
                    $fetch2['owner'] = true;
                }

                if ($fetch2['recipient_id'] == $userId && $fetch2['seen'] == 0) {
                    $this->getConnection()->query("UPDATE " . DB_MESSAGES . " SET seen=" . time() . " WHERE id=" . $fetch2['id']);
                }
            }

            $get[] = $fetch2;
        }

        return $get;

    }

    public function getMessageRecipients($userId = 0, $new = false, $limit = 10)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $get = array();
        $excludes = array();
        $userId = (int)$userId;
        $limit = (int)$limit;

        if ($userId < 1) {
            global $user;
            $userId = $user['id'];
        }

        if ($limit < 1) {
            $limit = 10;
        }


        $excludes = array(0);

        $queryText = "SELECT id FROM " . DB_ACCOUNTS . " WHERE id IN (SELECT user_id FROM " . DB_MESSAGES . " WHERE recipient_id=$userId AND active=1 AND seen=0 AND type != 2 ORDER BY seen ASC, id DESC) AND active=1";
        $query = $this->getConnection()->query($queryText);

        while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
            $userFObj = new \iCms\User();
            $userFObj->setId($fetch['id']);
            $get[] = $userFObj->getRows();
            $excludes[] = $fetch['id'];
        }

        $excludeQuery = implode(',', $excludes);

        if (!$new) {
            $queryText = "SELECT id FROM " . DB_ACCOUNTS . " WHERE id NOT IN ($excludeQuery) AND (id IN (SELECT user_id FROM " . DB_MESSAGES . " WHERE recipient_id=$userId AND active=1 AND seen>0 AND type != 2 ORDER BY id DESC) OR id IN (SELECT recipient_id FROM " . DB_MESSAGES . " WHERE user_id=$userId AND active=1 AND type != 2 ORDER BY id DESC)) AND active=1 LIMIT $limit";
            $query = $this->getConnection()->query($queryText);

            while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                $userFObj = new \iCms\User();
                $userFObj->setId($fetch['id']);
                $get[] = $userFObj->getRows();
            }
        }
        $storeRecp = $this->getStoreRecipients($userId, $new, $limit);
        return array_merge($storeRecp, $get);
    }

    public function getStoreRecipients($userId = 0, $new = false, $limit = 10)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $get = array();
        $excludes = array();
        $userId = (int)$userId;
        $limit = (int)$limit;

        if ($userId < 1) {
            global $user;
            $userId = $user['id'];
        }

        if ($limit < 1) {
            $limit = 10;
        }


        $excludes = array(0);
        $utiObj = new \iCms\Utilities();
        $mediaObj = new \iCms\Media();
        global $site_url;
        $storeimg = $site_url . '/media/store_icon.png';
        $queryText = "SELECT store_id FROM " . DB_STORE . " WHERE store_id IN (SELECT store_id FROM " . DB_MESSAGES . " WHERE recipient_id=$userId AND active=1 AND seen=0 AND type = 2 ORDER BY seen ASC, id DESC) AND active=1";
        $query = $this->getConnection()->query($queryText);


        while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
            $storeINFO = $utiObj->getStore($fetch['store_id']);
            if (isset($storeINFO['store']['store_id'])) {
                $simg = $storeimg;
                $pInfo = $utiObj->getDirectory(0, 0, $storeINFO['dir']['parent_id']);
                if ($pInfo['featured_image'] != 0) {
                    $fimage = $mediaObj->getById($pInfo['featured_image']);
                    $simg = $fimage['each'][0]['thumb_url'];
                }
                $get[] = array('id' => $fetch['store_id'], 'name' => $storeINFO['dir']['title'], 'thumbnail_url' => $simg, 'ctype' => 2);
            }
            $excludes[] = $fetch['store_id'];
        }

        $excludeQuery = implode(',', $excludes);

        if (!$new) {
            $queryText = "SELECT store_id FROM " . DB_STORE . " WHERE store_id NOT IN ($excludeQuery) AND (store_id IN (SELECT store_id FROM " . DB_MESSAGES . " WHERE recipient_id=$userId AND active=1 AND seen>0 AND type = 2) OR store_id IN (SELECT store_id FROM " . DB_MESSAGES . " WHERE user_id=$userId AND active=1 AND type = 2)) AND active=1 ORDER BY store_id DESC LIMIT $limit";
            $query = $this->getConnection()->query($queryText);
            if ($query->num_rows > 0) {
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $storeINFO = $utiObj->getStore($fetch['store_id']);
                    if (isset($storeINFO['store']['store_id'])) {
                        $simg = $storeimg;
                        $pInfo = $utiObj->getDirectory(0, 0, $storeINFO['dir']['parent_id']);
                        if ($pInfo['featured_image'] != 0) {
                            $fimage = $mediaObj->getById($pInfo['featured_image']);
                            $simg = $fimage['each'][0]['thumb_url'];
                        }
                        $get[] = array('id' => $fetch['store_id'], 'name' => $storeINFO['dir']['title'], 'thumbnail_url' => $simg, 'ctype' => 2);
                    }
                }
            }
        }
        return $get;
    }

    public function getStoreMessageRecipient($userId = 0, $new = false, $limit = 10)
    {
        global $sellerLogged;
        if (!$sellerLogged) {
            return false;
        }

        $get = array();
        $excludes = array();
        $userId = (int)$userId;
        $limit = (int)$limit;

        if ($userId < 1) {
            global $user;
            $userId = $user['store_id'];
        }

        if ($limit < 1) {
            $limit = 10;
        }


        $excludes = array(0);

        $queryText = "SELECT id FROM " . DB_ACCOUNTS . " WHERE id IN (SELECT user_id FROM " . DB_MESSAGES . " WHERE store_id=$userId AND active=1 AND seen=0 AND recipient_id=0 ORDER BY seen ASC, id DESC) AND active=1";
        $query = $this->getConnection()->query($queryText);

        while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
            $userFObj = new \iCms\User();
            $userFObj->setId($fetch['id']);
            $get[] = array_merge($userFObj->getRows(), array('ctype' => 2));
            $excludes[] = $fetch['id'];
        }

        $excludeQuery = implode(',', $excludes);

        if (!$new) {
            $queryText = "SELECT id FROM " . DB_ACCOUNTS . " WHERE id NOT IN ($excludeQuery) AND (id IN (SELECT user_id FROM " . DB_MESSAGES . " WHERE store_id=$userId AND active=1 AND recipient_id=0 AND seen>0) OR id IN (SELECT recipient_id FROM " . DB_MESSAGES . " WHERE store_id=$userId AND active=1)) AND active=1 ORDER BY id DESC LIMIT $limit";
            $query = $this->getConnection()->query($queryText);

            while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                $userFObj = new \iCms\User();
                $userFObj->setId($fetch['id']);
                $get[] = $userFObj->getRows();
            }
        }
        return $get;
    }

}