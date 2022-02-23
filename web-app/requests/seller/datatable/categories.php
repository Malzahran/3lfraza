<?php
$mediaObj = new \iCms\Media();
if (!empty($_POST)) {
    $fType = '';
    if (isset($_POST['parcat']) && $_POST['parcat'] == 1) {
        $fType = 1;
    } else if (isset($_POST['maincat']) && $_POST['maincat'] == 1) {
        $fType = 2;
    }
    $filterData = array();
    $type = $aFeatures ? array(1, 4) : array(1);
    $filterData['store_id'] = $user['store_id'];
    $filterData['type'] = $type;
    $filterData['ctype'] = $fType;
    $fetch = $dataObj->getCatIds(array_merge($_POST, $filterData));
    $viewData = array();
    $n = (int)$_POST['start'];
    if (!empty($fetch['data'])) {
        foreach ($fetch['data'] as $v) {
            $n++;
            $catInfo = $utiObj->getStoreCats($v);
            if ($catInfo['pid'] != 0) $pcat = $utiObj->getStoreCats($catInfo['pid']);
            else $pcat = array('category_name' => 'no_main');

            if ($catInfo['mid'] != 0) $scat = $utiObj->getStoreCats($catInfo['mid']);
            else if ($catInfo['mid'] == 0 && $catInfo['has_sub'] == 1) $scat = array('category_name' => 'no_main');
            else if ($catInfo['mid'] == 0 && $catInfo['has_sub'] == 0) $scat = array('category_name' => 'no_sub_cat');
            $timestamp = $catInfo['time'];
            $datetimeFormat = 'Y-m-d h:i a';
            $date = new \DateTime();
            $date->setTimestamp($timestamp);
            $type = Lang('seller_cat_type_' . $catInfo['type']);
            $themeData['item_id'] = $catInfo['category_id'];
            $themeData['item_name'] = Lang($catInfo['category_name'], 3);
            $catIMG = '';
            if ($catInfo['image_id'] != 0) {
                $imgFetch = $mediaObj->getById($catInfo['image_id']);
                $catIMG = '<img width="30px" src="' . $imgFetch['each'][0]['thumb_url'] . '" class="img-circle">';
            }
            $themeData['more_mange'] = '';
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/edit-btn');
            $themeData['more_mange'] .= \iCms\UI::view('backend/global/buttons/delete-btn');
            $moremange = \iCms\UI::view('backend/global/buttons/morebtn');


            $viewData[] = array(
                "DT_RowId" => 'tr_' . $catInfo['category_id'],
                'id' => $n,
                'title' => $themeData['item_name'] . ($catInfo['city_group'] != 0 ? '<br><b>' . ($catInfo['city_group'] == 3 ? Lang('all_label') : Lang('group_label') . ' ' . $catInfo['city_group']) . '</b>' : ''),
                'maincat' => (isset($pcat['type']) && $pcat['type'] == $catInfo['type']) ? Lang($pcat['category_name'], 3) : Lang('no_main'),
                'subcat' => (isset($scat['type']) && $scat['type'] == $catInfo['type']) ? Lang($scat['category_name'], 3) : Lang('no_main'),
                'catorder' => $catInfo['cat_order'] != 5000 ? $catInfo['cat_order'] : '',
                'photo' => $catIMG,
                'type' => $type,
                'date' => '<span dir="ltr">' . $date->format($datetimeFormat) . '</span>',
                'more' => $moremange);
        }
    }
    $data = array('draw' => $_POST['draw'], 'recordsTotal' => isset ($fetch['count']) ?
        (int)$fetch['count'] :
        0, 'recordsFiltered' => isset ($fetch['filter']) ?
        (int)$fetch['filter'] :
        0,
        'data' => $viewData);
}