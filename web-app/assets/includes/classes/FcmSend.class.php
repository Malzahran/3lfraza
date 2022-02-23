<?php

namespace iCms;

class FcmSend
{
    public $data;
    private $conn;
    private $escapeObj;
    private $tokens;
    private $ntitle;
    private $nmsg;
    private $type;
    private $ntype;
    private $naction;
    private $totalc = 0;
    private $successc = 0;
    private $failurec = 0;
    private $msgid;
    private $error;
    private $senderid;
    private $uid = 0;
    private $cid = 0;

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

    public function SendNotification()
    {
        if (!empty($this->tokens) && !empty($this->ntitle) && !empty($this->nmsg)) {
            $res = array();
            $res['data']['title'] = $this->ntitle;
            $res['data']['message'] = $this->nmsg;
            $res['data']['ntype'] = $this->ntype;
            $res['data']['nid'] = 1802;
            $res['data']['naction'] = $this->naction;
            $res['data']['image'] = '';

            $fields = array(
                'registration_ids' => $this->tokens,
                'data' => $res
            );

            //firebase server url to send the curl request
            $url = 'https://fcm.googleapis.com/fcm/send';

            //building headers for the request
            $headers = array(
                'Authorization: key=' . FIREBASE_API_KEY,
                'Content-Type: application/json'
            );

            //Initializing curl to open a connection
            $ch = curl_init();

            //Setting the curl url
            curl_setopt($ch, CURLOPT_URL, $url);

            //setting the method as post
            curl_setopt($ch, CURLOPT_POST, true);

            //adding headers
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

            //disabling ssl support
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);

            //adding the fields in json format
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));

            //finally executing the curl request
            $result = curl_exec($ch);

            if ($result === FALSE) {
                die('Curl failed: ' . curl_error($ch));
            }

            //Now close the connection
            curl_close($ch);

            //and save the result to DB
            $report = json_decode($result);
            $this->setSCount($report->success);
            $this->setFCount($report->failure);
            $this->setMsgid(isset($report->results[0]->message_id) ? $report->results[0]->message_id : '');
            $this->setError(isset($report->results[1]->error) ? $report->results[1]->error : '');
            $save = $this->saveReport();
            return $save;
        }
        return false;
    }

    private function setSCount($successc)
    {
        $this->successc = (int)$successc;
    }

    private function setFCount($failurec)
    {
        $this->failurec = (int)$failurec;
    }

    private function setMsgid($msgid)
    {
        $this->msgid = $this->escapeObj->stringEscape($msgid);
    }

    private function setError($error)
    {
        $this->error = $this->escapeObj->stringEscape($error);
    }

    private function saveReport()
    {
        if (!empty ($this->senderid) && !empty ($this->totalc) && !empty ($this->type) && !empty ($this->ntitle) && !empty ($this->nmsg)) {
            $query = $this->getConnection()->query("INSERT INTO " . DB_FCM_REP . " (notf_title,notf_msg,user_id,cityid,totalcount,success,failure,notf_type,action,sender_id,messageid,error,time) VALUES ('" . $this->ntitle . "','" . $this->nmsg . "'," . $this->uid . "," . $this->cid . "," . $this->totalc . "," . $this->successc . "," . $this->failurec . "," . $this->type . ",'" . $this->naction . "'," . $this->senderid . ",'" . $this->msgid . "','" . $this->error . "'," . time() . ")");
            if ($query) {
                $repid = $this->getConnection()->insert_id;
                return $repid;
            }
        }
        return false;
    }

    protected function getConnection()
    {
        return $this->conn;
    }

    public function CitiesFilter()
    {
        $get = array();

        $query = $this->getConnection()->query("SELECT DISTINCT user.current_city FROM " . DB_USERS . " AS user INNER JOIN " . DB_FCM . " AS fcm ON (user.id = fcm.user_id) ORDER BY user.current_city ASC");

        if ($query->num_rows >= 1) {
            while ($fetch = $query->fetch_array(MYSQLI_ASSOC)) {
                $get[] = $fetch['current_city'];
            }
        }
        $this->data = $get;

        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    public function getTokens($parm = array())
    {
        $get = array();
        $where = '';
        if (!empty ($parm['fgroup']) && $parm['fgroup'] != 'all') {
            $cfilter = (int)$parm['fgroup'];
            $where = "WHERE ";
            $where .= "user.current_city =" . $cfilter;
        }

        $querytotal = $this->getConnection()->query("SELECT COUNT(fcm.id) AS count FROM " . DB_FCM . " AS fcm INNER JOIN " . DB_USERS . " AS user ON (fcm.user_id = user.id) $where");

        if ($querytotal->num_rows >= 1) {
            $fetch = $querytotal->fetch_array(MYSQLI_ASSOC);
            $counttotal = $fetch['count'];
        } else {
            return false;
        }
        if (empty($parm['count'])) {

            $queryText = "SELECT fcm.token FROM " . DB_FCM . " AS fcm INNER JOIN " . DB_USERS . " AS user ON (fcm.user_id = user.id) $where";


            $query2 = $this->getConnection()->query($queryText);

            if ($query2->num_rows >= 1) {
                while ($fetch2 = $query2->fetch_array(MYSQLI_ASSOC)) {
                    $get[] = $fetch2['token'];
                }
            }
            $this->data = array('count' => $counttotal, 'data' => $get);
        } else {
            $this->data = $counttotal;
        }
        if (!empty($this->data)) {
            return $this->data;
        }
        return false;
    }

    public function setTokens($tokens)
    {
        if (!empty($tokens) && is_array($tokens)) {
            $this->tokens = $tokens;
        }
    }

    public function getSingleToken()
    {
        if (!empty($this->uid)) {
            $query = $this->getConnection()->query("SELECT token FROM " . DB_FCM . " WHERE user_id=" . $this->uid);

            if ($query->num_rows == 1) {
                $fetch = $query->fetch_array(MYSQLI_ASSOC);
                $this->data = $fetch['token'];
                return $this->data;
            }
        }
        return false;
    }

    public function setNTitle($tit)
    {
        $tit = strip_tags($tit);
        $this->ntitle = $this->escapeObj->stringEscape($tit);
    }

    public function setNMsg($msg)
    {
        $msg = strip_tags($msg);
        $this->nmsg = $this->escapeObj->stringEscape($msg);
    }

    public function setNType($typ)
    {
        $this->type = (int)$typ;
        switch ($this->type) {
            case 1:
                $nt = '';
                break;
            case 2:
                $nt = 'home';
                break;
            case 3:
                $nt = 'url';
                break;
            case 7:
                $nt = 'item';
                break;
        }
        $this->ntype = $nt;
    }

    public function setNAction($naction)
    {
        $this->naction = $this->escapeObj->stringEscape($naction);
    }

    public function setUserid($uid)
    {
        $this->uid = (int)$uid;
    }

    public function setSenderid($senderid)
    {
        $this->senderid = (int)$senderid;
    }

    public function setCityid($cid)
    {
        $this->cid = (int)$cid;
    }

    public function setTCount($totalc)
    {
        $this->totalc = (int)$totalc;
    }

}