<?php

namespace iCms;


class Users
{
    private $conn;
    private $escapeObj;
    private $delObj;
    private $uid;
    private $perm;

    private $name;
    private $username;
    private $email;
    private $password;
    private $oldpassword;
    private $phone = '';
    private $gender = '';
    private $qualification = '';
    private $nid = '';
    private $dlc = '';
    private $vlc = '';
    private $active = 1;
    private $lon = '';
    private $lat = '';
    private $storeID = 0;
    private $delvType = 0;
    private $storeADMIN = 0;
    private $birthday = '';
    private $authmethod = 'web';
    private $city = 5000;
    private $address;
    private $street;
    private $building;
    private $floor = 0;
    private $apartment;
    private $additional;
    private $usertype = 'user';

    private $allowedGenders = array('male', 'female');

    function __construct()
    {
        global $conn;
        $this->conn = $conn;
        $this->escapeObj = new Escape();
        $this->delObj = new DeleteMedia();
        return $this;
    }

    public function register()
    {
        if (!empty ($this->name) && !empty ($this->username) && !empty ($this->email) && !empty ($this->password)) {
            $query = $this->getConnection()->query("INSERT INTO " . DB_ACCOUNTS . " (active,email,email_verification_key,name,password,time,type,authmethod,username) VALUES (" . $this->active . ",'" . $this->email . "','" . md5(generateKey()) . "','" . $this->name . "','" . $this->password . "'," . time() . ",'" . $this->usertype . "','" . $this->authmethod . "','" . $this->username . "')");

            if ($query) {
                $this->uid = $this->getConnection()->insert_id;
                $query2 = $this->getConnection()->query("INSERT INTO " . DB_USERS . " (id,birthday,phone,current_city,address,street,building,floor,apartment,additional,gender,store_id,store_admin,delv_type,qualification,nid,driving_license,vehicle_license,lat,lon) VALUES (" . $this->uid . ",'" . $this->birthday . "','" . $this->phone . "'," . $this->city . ",'" . $this->address . "','" . $this->street . "','" . $this->building . "'," . $this->floor . ",'" . $this->apartment . "','" . $this->additional . "','" . $this->gender . "'," . $this->storeID . "," . $this->storeADMIN . "," . $this->delvType . ",'" . $this->qualification . "','" . $this->nid . "','" . $this->dlc . "','" . $this->vlc . "','" . $this->lat . "','" . $this->lon . "')");

                if ($query2) return $this->uid;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function registerPerm()
    {
        if (!empty($this->perm) && !empty($this->uid)) {
            $permarray = $this->perm;
            $query = false;
            foreach ($permarray as $k => $v) {
                $count = count($permarray[$k]);
                $i = 0;
                $type = $this->escapeObj->stringEscape($k);
                $perm = array();
                $perm['view'] = $perm['add'] = $perm['del'] = $perm['edit'] = $perm['actv'] = $perm['feat'] = 0;
                foreach ($permarray[$k] as $ke => $va) {
                    $i += 1;
                    $permt = $this->escapeObj->stringEscape($ke);
                    $perm[$permt] = (int)$va;
                    if ($count == $i) {
                        $query = $this->getConnection()->query("INSERT INTO " . DB_SYS_ADMINS . " (active,admin_id,cview,cadd,cdel,cedit,cactv,cfeat,type) VALUES (1," . $this->uid . "," . $perm['view'] . "," . $perm['add'] . "," . $perm['del'] . "," . $perm['edit'] . "," . $perm['actv'] . "," . $perm['feat'] . ",'$type')");
                    }
                }
            }

            if ($query) {
                return true;
            }
        }
        return false;
    }

    public function edit($activate = 0)
    {
        if (!empty ($this->uid) && !empty ($this->name) && !empty ($this->username) && !empty ($this->email)) {
            $activate = (int)$activate;
            $update = "active = " . $activate;
            $update .= ",email = '" . $this->email . "'";
            $update .= ",name = '" . $this->name . "'";

            if (!empty($this->password)) {
                global $user, $admin, $sellerLogged, $seller;
                if ((($admin['system'] == true || $admin['users']['edit'] == true) || ($sellerLogged && (@$seller['system'] || @$seller['moderator']))) && $user['id'] != $this->uid) {
                    $update .= ",password = '" . $this->password . "'";
                } else if ($user['id'] == $this->uid && !empty($this->oldpassword) && !empty($this->password)) {
                    $checkpass = $this->CheckOldPassword();
                    if ($checkpass) {
                        $update .= ",password = '" . $this->password . "'";
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            $query = $this->getConnection()->query("UPDATE " . DB_ACCOUNTS . " SET $update WHERE id=" . $this->uid);
            if ($query) {
                $query2 = "UPDATE " . DB_USERS . " SET ";
                if (!empty($this->birthday)) {
                    $query2 .= "birthday='" . $this->birthday . "',";
                }

                if (!empty($this->phone)) {
                    $query2 .= "phone='" . $this->phone . "',";
                }

                if (!empty($this->city)) {
                    $query2 .= "current_city=" . $this->city . ",";
                }
                if (!empty($this->address)) {
                    $query2 .= "address='" . $this->address . "',";
                }
                if (!empty($this->street)) {
                    $query2 .= "street='" . $this->street . "',";
                }
                if (!empty($this->building)) {
                    $query2 .= "building='" . $this->building . "',";
                }
                if (!empty($this->floor)) {
                    $query2 .= "floor=" . $this->floor . ",";
                }
                if (!empty($this->apartment)) {
                    $query2 .= "apartment='" . $this->apartment . "',";
                }
                if (!empty($this->additional)) {
                    $query2 .= "additional='" . $this->additional . "',";
                }

                if (!empty($this->gender)) {
                    $query2 .= "gender='" . $this->gender . "',";
                }

                if (!empty($this->storeADMIN)) {
                    $query2 .= "store_admin=" . $this->storeADMIN . ",";
                }

                if (isset($this->qualification)) {
                    $query2 .= "qualification='" . $this->qualification . "',";
                }

                if (isset($this->nid)) {
                    $query2 .= "nid='" . $this->nid . "',";
                }

                if (isset($this->dlc)) {
                    $query2 .= "driving_license='" . $this->dlc . "',";
                }

                if (isset($this->vlc)) {
                    $query2 .= "vehicle_license='" . $this->vlc . "',";
                }

                $query2 .= "id=id WHERE id=" . $this->uid;

                $queryupdate = $this->getConnection()->query($query2);

                if ($queryupdate) {
                    return true;
                }
            }
        }
        return false;
    }

    private function CheckOldPassword()
    {
        if (!empty($this->uid) && !empty($this->oldpassword)) {
            $query1 = $this->getConnection()->query("SELECT id FROM " . DB_ACCOUNTS . " WHERE id = " . $this->uid . " AND active=1 AND password='" . $this->oldpassword . "'");
            if ($query1->num_rows == 1) {
                return true;
            }
        }
        return false;
    }

    public function updatePerm()
    {
        if (!empty($this->perm) && !empty($this->uid)) {
            $qdelperm = $this->getConnection()->query("DELETE FROM " . DB_SYS_ADMINS . " WHERE admin_id=" . $this->uid);
            if ($qdelperm) {
                $query = false;
                $permarray = $this->perm;
                foreach ($permarray as $k => $v) {
                    $count = count($permarray[$k]);
                    $i = 0;
                    $type = $this->escapeObj->stringEscape($k);
                    $perm = array();
                    $perm['view'] = $perm['add'] = $perm['del'] = $perm['edit'] = $perm['actv'] = $perm['feat'] = 0;
                    foreach ($permarray[$k] as $ke => $va) {
                        $i += 1;
                        $permt = $this->escapeObj->stringEscape($ke);
                        $perm[$permt] = (int)$va;
                        if ($count == $i) {
                            $query = $this->getConnection()->query("INSERT INTO " . DB_SYS_ADMINS . " (active,admin_id,cview,cadd,cdel,cedit,cactv,cfeat,type) VALUES (1," . $this->uid . "," . $perm['view'] . "," . $perm['add'] . "," . $perm['del'] . "," . $perm['edit'] . "," . $perm['actv'] . "," . $perm['feat'] . ",'$type')");
                        }
                    }
                }

                return $query ? true : false;
            }
        }
        return false;
    }

    public function activeUser()
    {
        $userfetch = null;
        if (!empty($this->uid)) {
            $uIObj = new User();
            $userfetch = $uIObj->getById($this->uid);
            if (!empty($userfetch['id'])) {
                if ($userfetch['active'] == 1) {
                    $active = 0;
                } else {
                    $active = 1;
                }
                $query = $this->getConnection()->query("UPDATE " . DB_ACCOUNTS . " SET active = $active WHERE id=" . $userfetch['id']);
                if ($query) {
                    return true;
                }
            }
        }
        return false;
    }

    public function deleteUser()
    {
        $continue = $done = false;
        if (!empty($this->uid)) {
            $uIObj = new User();
            $userfetch = $uIObj->getById($this->uid);
            $fetch_files = $uIObj->getFiles($this->uid);
            if (!empty($userfetch['id'])) {
                if ($userfetch['avatar_id'] != 0) {
                    $delimg = $this->delObj->deleteMedia($userfetch['avatar_id']);
                    if ($delimg) $continue = true;
                } else $continue = true;
                if ($continue == true) {
                    $querydel = $this->getConnection()->query("DELETE FROM " . DB_ACCOUNTS . " WHERE id=" . $this->uid);
                    if ($querydel) {
                        $qdelinfo = $this->getConnection()->query("DELETE FROM " . DB_USERS . " WHERE id=" . $this->uid);
                        if ($qdelinfo) {
                            if ($fetch_files) {
                                foreach ($fetch_files as $file) {
                                    $delFile = $this->delObj->deleteMedia($file['file_id']);
                                    if ($delFile) $done = true;
                                }
                            } else $done = true;
                            if ($userfetch['type'] == "moderator") {
                                $qdelperm = $this->getConnection()->query("DELETE FROM " . DB_SYS_ADMINS . " WHERE admin_id=" . $this->uid);
                                if ($qdelperm) $done = true;
                            } else $done = true;
                        }
                    }
                }
            }
            if ($done == true) {
                return true;
            }
        }
        return false;
    }

    public function setUserId($uid)
    {
        $this->uid = (int)$uid;
    }

    public function setPerm($perm)
    {
        $this->perm = $perm;
    }

    public function setName($n)
    {
        if (!empty($n)) {
            $this->name = $this->escapeObj->stringEscape($n);
        }
    }

    public function setPhone($ph)
    {
        if (!empty($ph)) {
            $this->phone = $this->escapeObj->stringEscape($ph);
        }
    }

    public function setEmail($e)
    {
        if (filter_var($e, FILTER_VALIDATE_EMAIL)) {
            $this->email = $this->escapeObj->stringEscape($e);
        }
    }

    public function setUsername($u)
    {
        if ($this->validateUsername($u)) {
            $this->username = $this->escapeObj->stringEscape($u);
        }
    }

    private function validateUsername($u)
    {
        if (strlen($u) > 3 && !is_numeric($u) && preg_match('/^[A-Za-z0-9_]+$/', $u)) {
            return true;
        }
        return false;
    }

    public function setPassword($p)
    {
        if (!empty($p)) {
            $this->password = sha1($p . md5($p));
        }
    }

    public function setoldpassword($op)
    {
        if (!empty($op)) {
            $this->oldpassword = sha1($op . md5($op));
        }
    }

    public function setGender($g)
    {
        if (in_array($g, $this->allowedGenders)) {
            $this->gender = $g;
        }
    }

    public function setBirthday($b)
    {
        if (is_array($b)) {
            $b = implode('-', $b);
            $regex = '/^([0-9]{1,2})\-([0-9]{1,2})\-([0-9]{4})$/';

            if (preg_match($regex, $b)) {
                $this->birthday = $b;
            }
        }
    }

    public function setCity($l)
    {
        $this->city = (int)$l;
    }

    public function setStore($sID)
    {
        $this->storeID = (int)$sID;
    }

    public function setStoreAdmin($sAD)
    {
        $this->storeADMIN = (int)$sAD;
    }

    public function setDeliveryType($DT)
    {
        $this->delvType = (int)$DT;
    }

    public function setActive($actv)
    {
        $this->active = (int)$actv;
    }

    public function setLatitude($latitude)
    {
        $this->lat = (double)$latitude;
    }

    public function setLongitude($longitude)
    {
        $this->lon = (double)$longitude;
    }

    public function setQualification($qualf)
    {
        $this->qualification = $this->escapeObj->stringEscape($qualf);
    }

    public function setNationalID($nd)
    {
        $this->nid = $this->escapeObj->stringEscape($nd);
    }

    public function setDrivingLicense($dlc)
    {
        $this->dlc = $this->escapeObj->stringEscape($dlc);
    }

    public function setVehicleLicense($vlc)
    {
        $this->vlc = $this->escapeObj->stringEscape($vlc);
    }

    public function setAddress($addr)
    {
        $this->address = $this->escapeObj->stringEscape($addr);
    }

    public function setStreet($street)
    {
        $this->street = $this->escapeObj->stringEscape($street);
    }

    public function setBuilding($building)
    {
        $this->building = $this->escapeObj->stringEscape($building);
    }

    public function setFloor($floor)
    {
        $this->floor = (int)$floor;
    }

    public function setApartment($apartment)
    {
        $this->apartment = $this->escapeObj->stringEscape($apartment);
    }

    public function setAdditional($additional)
    {
        $this->additional = $this->escapeObj->stringEscape($additional);
    }

    public function setAuth($auth)
    {
        $this->authmethod = $this->escapeObj->stringEscape($auth);
    }

    public function setUsertype($usertype)
    {
        $this->usertype = $this->escapeObj->stringEscape($usertype);
    }
}