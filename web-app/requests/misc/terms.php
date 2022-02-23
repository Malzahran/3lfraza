<?php
administrationOnly();
$data = array(
    'message' => Lang('error_somthing_went_wrong')
);
if (!empty($_POST['desc'])) {
    $keyword = 'terms';
    foreach ($_POST['desc'] as $v) {
        if (is_array($v)) {
            foreach ($v as $key => $value) {
                $dataSet = array();
                $dataSet['keyword'] = $keyword;
                $dataSet['title'] = '';
                $dataSet['content'] = $value;
                $dataSet['lang_code'] = $key;
                $checkExist = $utiObj->getMiscInfo($keyword, $key);
                if (!empty($checkExist)) {
                    $done = ($oprObj->editMisc($dataSet)) ? true : false;
                } else {
                    $done = ($oprObj->registerMisc($dataSet)) ? true : false;
                }
            }
        }
    }
    if ($done) {
        $data = array(
            'status' => 200,
            'message' => Lang('success_updated')
        );
    }
} else {
    $data = array(
        'message' => Lang('error_empty_data')
    );
}