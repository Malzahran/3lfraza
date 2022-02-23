<?
$fcmObj = new \iCmsAPI\FcmControl();
$fcmdata = $data->user;
$token = $fcmdata->fcmtoken;
$userid = (int)$fcmdata->uid;
if (!empty($token)) {
    if (!empty($userid)) {
        $fcmObj->setUserid($userid);
        $fcmObj->DeleteUserTokens();
    }
    $fcmObj->setFcmtoken($token);
    $chktok = $fcmObj->CheckToken();
    if ($chktok) {
        $fcmObj->setId($chktok);
        $fcmObj->DeleteToken();
        $fcmObj->InsertToken();
    } else $fcmObj->InsertToken();
}
$response["result"] = 'success';