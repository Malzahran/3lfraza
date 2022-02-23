<?php
sellerOnly();
$dataObj = new \iCmsSeller\DataGet();
if (isset($_POST['term']) && strlen($_POST['term']) > 0) {
    $limit = 10;
    $page = (int)$_POST['page'];
    $start = ($page - 1) * $limit;
    $filterDT['length'] = $limit;
    $filterDT['start'] = $start;
    $filterDT['store_id'] = $user['store_id'];
    $filterDT['search']['value'] = $_POST['term'];
    $fetch = $dataObj->getItemIds($filterDT);
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $itemInfo = $utiObj->getItem($v);
            $data['results'][] = array(
                'id' => $v,
                'text' => $itemInfo['item_code'] . ' - ' . $itemInfo['title']);
        }
        $endCount = $start + $limit;
        $morePages = $fetch['filter'] > $endCount;
        $data['pagination'] = array(
            "more" => $morePages
        );
    }
} else {
    $data['results'][] = array('id' => '', 'text' => Lang('search_input_ph'));
}