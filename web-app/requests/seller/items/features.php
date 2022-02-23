<?php
sellerOnly();
$method = (isset($_POST['method'])) ? $escapeObj->stringEscape($_POST['method']) : '';
if (!empty($method)) {
    $storeID = $user['store_id'];
    $featureCats = $utiObj->getStoreCats(0, $storeID, array(4));
    if ($method == 'add') {
        if (empty($_POST['title']['en'])) $_POST['title']['en'] = $_POST['title']['ar'];
        if ($register = $itemObj->registerFeature($_POST)) {
            $ftID = $register;
            $ftFetch = $utiObj->getItemFeatures(0, $ftID);
            if (isset($ftFetch['id'])) {
                $themeData['ft_select'] = '';
                $catInfo = $utiObj->getStoreCats($ftFetch['category']);
                if (isset($catInfo['category_id'])) {
                    $themeData['sel_id'] = $catInfo['category_id'];
                    $themeData['sel_name'] = Lang($catInfo['category_name'], 3);
                    $themeData['ft_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                }
                if (!empty($featureCats)) {
                    foreach ($featureCats as $k => $v) {
                        if ($v['category_id'] != $ftFetch['category']) {
                            $themeData['sel_id'] = $v['category_id'];
                            $themeData['sel_name'] = Lang($v['category_name'], 3);
                            $themeData['ft_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                        }
                    }
                }
                $ftInfoAR = $utiObj->getItemFeaturesInfo($ftID, 'ar');
                $ftInfoEN = $utiObj->getItemFeaturesInfo($ftID, 'en');
                $themeData['ft_id'] = $ftID;
                $themeData['ft_title_ar'] = $ftInfoAR['title'];
                $themeData['ft_title_en'] = $ftInfoEN['title'];
                $themeData['ft_price'] = $ftFetch['price'] != 0 ? round($ftFetch['price'], 2) : '';
                $themeData['ft_dsc_price'] = $ftFetch['dsc_price'] != 0 ? round($ftFetch['dsc_price'], 2) : '';
                $html = \iCms\UI::view('backend/seller/items/feature-item');
                $data = array(
                    'status' => 200,
                    'html' => $html
                );
            }
        }
    } else if ($method == 'get') {
        if (!empty($_POST['item_id'])) {
            $itemID = (int)$_POST['item_id'];
            $html = '';
            $getFT = $utiObj->getItemFeatures($itemID);
            if (!empty($getFT)) {
                foreach ($getFT as $k => $v) {
                    $themeData['ft_select'] = '';
                    $catInfo = $utiObj->getStoreCats($v['category']);
                    if (isset($catInfo['category_id'])) {
                        $themeData['sel_id'] = $catInfo['category_id'];
                        $themeData['sel_name'] = Lang($catInfo['category_name'], 3);
                        $themeData['ft_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                    }
                    if (!empty($featureCats)) {
                        foreach ($featureCats as $val) {
                            if ($val['category_id'] != $v['category']) {
                                $themeData['sel_id'] = $val['category_id'];
                                $themeData['sel_name'] = Lang($val['category_name'], 3);
                                $themeData['ft_select'] .= \iCms\UI::view('backend/global/select/selectopt');
                            }
                        }
                    }
                    $ftInfoAR = $utiObj->getItemFeaturesInfo($v['id'], 'ar');
                    $ftInfoEN = $utiObj->getItemFeaturesInfo($v['id'], 'en');
                    $themeData['ft_id'] = $v['id'];
                    $themeData['ft_title_ar'] = $ftInfoAR['title'];
                    $themeData['ft_title_en'] = $ftInfoEN['title'];
                    $themeData['ft_price'] = $v['price'] != 0 ? round($v['price'], 2) : '';
                    $themeData['ft_dsc_price'] = $v['dsc_price'] != 0 ? round($v['dsc_price'], 2) : '';
                    $html .= \iCms\UI::view('backend/seller/items/feature-item');
                }
            }
            $data = array(
                'status' => 200,
                'html' => $html
            );
        }
    } else if ($method == 'edit') {
        if (!empty($_POST['ft_id'])) {
            $itemObj->setId($_POST['ft_id']);
            if (empty($_POST['title']['en'])) {
                $_POST['title']['en'] = $_POST['title']['ar'];
            }
            $edit = $itemObj->editFeature($_POST);
            if ($edit) $data = array('status' => 200);
        }
    } else if ($method == 'delete') {
        if (!empty($_POST['ft_id'])) {
            $itemObj->setId($_POST['ft_id']);
            $del = $itemObj->deleteFeature();
            if ($del) $data = array('status' => 200);
        }
    }
}