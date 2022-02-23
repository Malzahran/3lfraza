<?php

namespace iCms;

class User
{
    public $data;
    private $id;
    private $conn;
    private $escapeObj;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        return $this;
    }

    public function setConnection(\mysqli $conn)
    {
        $this->conn = $conn;
        return $this;
    }

    public function getById($id)
    {
        $this->setId($id);
        return $this->getRows();
    }

    public function setId($id)
    {
        $this->id = (int)$id;
    }

    public function getRows()
    {
        $query1 = $this->getConnection()->query("SELECT id,active,avatar_id,email,email_verification_key,email_verified,language,last_logout,last_logged,app_lastlogged,name,time,type,authmethod,username FROM " . DB_ACCOUNTS . " WHERE id=" . $this->id);

        if ($query1->num_rows == 1) {
            $account_data = $query1->fetch_array(MYSQLI_ASSOC);
            $user_data = array();
            global $userTypes;
            if (in_array($account_data['type'], $userTypes)) {
                $user_data = $this->getData();
            }

            $this->data = array_merge($account_data, $user_data);

            if (empty ($this->data['language'])) {
                $this->data['language'] = "ar";
            }

            if ($this->data['current_city'] != 0) {
                $utiObj = new Utilities();
                $cityinfo = $utiObj->getCity($this->data['current_city']);
                $this->data['city'] = $cityinfo['city_name'];
            } else {
                $this->data['city'] = 'no_main';
            }

            // Get first and last names
            $this->getNames();

            // Get avatar
            $this->getAvatar();

            if ($this->data['type'] == "seller") {
                $this->getStore();
            }

            // Get Account state
            $this->getActive();

            // Get Account last use
            $this->LastUse();

            // Get FCM token
            $this->haveToken();
        }
        return $this->data;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    private function getData()
    {
        $fetch = array();
        $query = $this->getConnection()->query("SELECT * FROM " . DB_USERS . " WHERE id=" . $this->id);

        if ($query->num_rows === 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);

            if (!empty($fetch['birthday'])) {
                $fetch['birth'] = explode('-', $fetch['birthday']);
                $fetch['birth'] = array(
                    'date' => $fetch['birth'][0],
                    'month' => $fetch['birth'][1],
                    'year' => $fetch['birth'][2]
                );
            }

            if ($this->data['avatar_id'] == 0) {
                $fetch['thumbnail_url'] = $fetch['avatar_url'] = THEME_URL . '/images/default-user.png';

                if (!empty($fetch['gender'])) {

                    if ($fetch['gender'] == "female") {
                        $fetch['thumbnail_url'] = $fetch['avatar_url'] = THEME_URL . '/images/default-user.png';
                    }
                }
            }
        }
        return $fetch;
    }

    private function getNames()
    {
        $nameBreak = explode(' ', $this->data['name']);
        $this->data['first_name'] = $nameBreak[0];
        $this->data['last_name'] = $nameBreak[count($nameBreak) - 1];
    }

    private function getAvatar()
    {
        if ($this->data['avatar_id'] > 0) {
            $mediaObj = new Media();
            $this->data['avatar'] = $mediaObj->getById($this->data['avatar_id']);
            $this->data['thumbnail_url'] = SITE_URL . '/' . $this->data['avatar']['each'][0]['url'] . '_thumb.' . $this->data['avatar']['each'][0]['extension'];
            $this->data['avatar_url'] = SITE_URL . '/' . $this->data['avatar']['each'][0]['url'] . '_squ.' . $this->data['avatar']['each'][0]['extension'];
        }
    }

    private function getStore()
    {
        $utiObj = new Utilities();
        $this->data['seller'] = $utiObj->getStore($this->data['store_id'], '', false);
        $isStoreAdmin = (int)$this->data['store_admin'];
        if ($isStoreAdmin != 0) {
            $this->data['seller']['logged'] = true;
            switch ($isStoreAdmin) {
                case 1:
                    $this->data['seller']['system'] = true;
                    break;
                case 2:
                    $this->data['seller']['moderator'] = true;
                    break;
                case 3:
                    $this->data['seller']['seller'] = true;
                    break;
            }
        }
    }
    
    public function getFiles($uid = 0)
    {
        $uid = (int)$uid;
        if ($uid != 0) {
            $files = array();
            $query = $this->getConnection()->query("SELECT * FROM " . DB_USER_FILES . " WHERE user_id=" . $uid);
            if ($query->num_rows > 0) {
                while($fetch = $query->fetch_array(MYSQLI_ASSOC)){
                    $files[] = $fetch;
                }
                return $files;
            }
        }
        return false;
    }

    private function getActive()
    {
        $this->data['active'] = ($this->data['active'] == 1) ? true : false;
    }

    private function LastUse()
    {
        $timestamp = $this->data['last_logout'];
        $datetimeFormat = 'Y-m-d h:i a';
        $date = new \DateTime();
        $date->setTimestamp($timestamp);
        $this->data['last_use'] = ($this->data['last_logout'] == null) ? '.............' : '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>';
    }

    private function haveToken()
    {
        $query = $this->getConnection()->query("SELECT token FROM " . DB_FCM . " WHERE user_id=" . $this->id);
        $fetch['token'] = null;
        if ($query->num_rows === 1) {
            $fetch = $query->fetch_array(MYSQLI_ASSOC);
            $this->data['token'] = $fetch['token'];
        }
        $this->data['havetoken'] = ($fetch['token'] == null) ? false : true;
    }

    public function isAdmin($adminId = 0)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }

        $adminId = (int)$adminId;
        global $userTypes;
        if (in_array($this->data['type'], $userTypes)) {
            if ($adminId < 1) {
                global $user;
                $adminId = $user['id'];
            }

            if ($adminId == $this->id) {
                return true;
            }
        }
        return false;
    }

    public function isSysAdmin($adminId = 0)
    {
        global $isLogged;
        if (!$isLogged) {
            return false;
        }
        $admin = array();
        $adminId = (int)$adminId;
        if ($this->data['type'] == "admin") {
            if ($adminId < 1) {
                global $user;
                $adminId = $user['id'];
            }

            if ($adminId == $this->id) {
                $admin['ability'] = true;
                $admin['system'] = true;

            }
            return $admin;
        } else if ($this->data['type'] == "moderator") {
            global $conn, $user;
            $admin['ability'] = false;
            $adminId = (int)$adminId;

            if ($adminId < 1) {
                $adminId = $user['id'];
            }
            $queryText = "SELECT * FROM " . DB_SYS_ADMINS . " WHERE admin_id=$adminId AND active=1";

            $query = $conn->query($queryText);

            if ($query->num_rows >= 1) {
                $admin['ability'] = true;
                while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                    $admin[$fetch['type']] = array();
                    $admin[$fetch['type']]['access'] = true;
                    if ($fetch['cview'] == 1) {
                        $admin[$fetch['type']]['view'] = true;
                    }
                    if ($fetch['cadd'] == 1) {
                        $admin[$fetch['type']]['add'] = true;
                    }
                    if ($fetch['cdel'] == 1) {
                        $admin[$fetch['type']]['del'] = true;
                    }
                    if ($fetch['cedit'] == 1) {
                        $admin[$fetch['type']]['edit'] = true;
                    }
                    if ($fetch['cactv'] == 1) {
                        $admin[$fetch['type']]['actv'] = true;
                    }

                    if ($fetch['cfeat'] == 1) {
                        $admin[$fetch['type']]['feat'] = true;
                    }
                }
            }
            return $admin;
        } else if ($this->data['type'] == "delivery") {
            $admin['ability'] = true;
            $admin['ord']['view'] = true;
            return $admin;
        }
        return false;
    }

}