<?php

namespace iCmsAPI;

class UserControl
{

    public $data;
    private $id;
    private $conn;
    private $escapeObj;

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new \iCms\Escape();
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
        $query1 = $this->getConnection()->query("SELECT * FROM " . DB_ACCOUNTS . " WHERE id=" . $this->id . "");

        if ($query1->num_rows == 1) {
            $account_data = $query1->fetch_array(MYSQLI_ASSOC);

            if ($account_data['type'] == "user" || $account_data['type'] == "admin" || $account_data['type'] == "moderator") {
                $user_data = $this->getData();
            }

            $this->data = array_merge($account_data, $user_data);

            if (empty ($this->data['language'])) {
                $this->data['language'] = "english";
            }

            // Get first and last names
            $this->getNames();

            // Get avatar
            $this->getAvatar();

            // Get Account state
            $this->getActive();

            return $this->data;
        }
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    private function getData()
    {
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

            return $fetch;
        }
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
            $mediaObj = new \iCms\Media();
            $this->data['avatar'] = $mediaObj->getById($this->data['avatar_id']);
            $this->data['thumbnail_url'] = SITE_URL . '/' . $this->data['avatar']['each'][0]['url'] . '_thumb.' . $this->data['avatar']['each'][0]['extension'];
            $this->data['avatar_url'] = SITE_URL . '/' . $this->data['avatar']['each'][0]['url'] . '_thumb.' . $this->data['avatar']['each'][0]['extension'];
        }
    }

    private function getActive()
    {
        $this->data['active'] = ($this->data['active'] == 1) ? true : false;
    }

    public function isAdmin($adminId = 0)
    {
        if (!isLogged()) {
            return false;
        }

        $adminId = (int)$adminId;

        if ($this->data['type'] == "user" || $this->data['type'] == "admin" || $this->data['type'] == "moderator") {
            if ($adminId < 1) {
                global $user;
                $adminId = $user['id'];
            }

            if ($adminId == $this->id) {
                return true;
            }
        }
    }

}
