<?php
$dataObj = new \iCms\DataGet();
if (isset($_POST['term']) && strlen($_POST['term']) > 0) {
    $limit = 10;
    $page = (int)$_POST['page'];
    $start = ($page - 1) * $limit;
    $filterDT['length'] = $limit;
    $filterDT['start'] = $start;
    $filterDT['search']['value'] = $_POST['term'];
    $fetch = $dataObj->getUserIds($filterDT);
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $fetchinfo = $userDObj->getById($v);
            $data['results'][] = array(
                'id' => $fetchinfo['id'],
                'text' => $fetchinfo['name'] . ' - ' . $fetchinfo['phone']);
        }
        $endCount = $start + $limit;
        $morePages = $fetch['filter'] > $endCount;
        $data['pagination'] = array(
            "more" => $morePages
        );
    } else {
        $data['results'][] = array(
            'id' => 'add',
            'text' => Lang('add_new_btn'));
    }
} else {
    $data['results'][] = array('id' => '', 'text' => Lang('search_input_ph'));
}