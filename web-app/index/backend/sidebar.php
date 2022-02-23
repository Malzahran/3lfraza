<?php
if ($adminLogged || $sellerLogged) {
    $themeData['home_url'] = smoothLink('index.php?tab1=' . $backend_url . '&tab2=home');
    $themeData['sidebar'] = \iCms\UI::view('backend/global/sidebar/side-start');

    $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-main-start');

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['news_url'] = smoothLink('index.php?tab1=admin&tab2=news');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-news');
    }
    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['coverage_url'] = smoothLink('index.php?tab1=admin&tab2=coverage');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-coverage');

    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['location_alert_url'] = smoothLink('index.php?tab1=admin&tab2=location-alert');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-location-alert');
    }

    if ($aCATS && (@$seller['system'] || @$seller['moderator'])) {
        $themeData['seller_cats_url'] = smoothLink('index.php?tab1=admin&tab2=categories');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-seller-cats');
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['seller_items_url'] = smoothLink('index.php?tab1=admin&tab2=items');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-seller-items');
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['stock_alert_url'] = smoothLink('index.php?tab1=admin&tab2=stock-alert');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-stock-alert');
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['promo_url'] = smoothLink('index.php?tab1=admin&tab2=promo-codes');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-promo');
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['worker_url'] = smoothLink('index.php?tab1=admin&tab2=worker');
        $themeData['sidebar'] .= $sellerLogged ? \iCms\UI::view('backend/global/sidebar/side-worker') : '';
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['delivery_url'] = smoothLink('index.php?tab1=admin&tab2=delivery');
        $themeData['sidebar'] .= $sellerLogged ? \iCms\UI::view('backend/global/sidebar/side-delivery') : '';
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['delivery_tracker_url'] = smoothLink('index.php?tab1=admin&tab2=delivery-tracking');
        $themeData['sidebar'] .= $sellerLogged ? \iCms\UI::view('backend/global/sidebar/side-delivery-tracking') : '';
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['users_url'] = smoothLink('index.php?tab1=admin&tab2=users');
        $themeData['sidebar'] .= $sellerLogged ? \iCms\UI::view('backend/global/sidebar/side-users') : '';
    }

    if (@$seller['system'] || @$seller['moderator']) {
        $themeData['clients_url'] = smoothLink('index.php?tab1=admin&tab2=clients');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-clients');
    }

    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-reports-start');
    }
    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['report_orders_url'] = smoothLink('index.php?tab1=' . $backend_url . '&tab2=reports&tab3=orders');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-report-orders');
    }
    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-reports-end');
    }

    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['privacy_url'] = smoothLink('index.php?tab1=admin&tab2=privacy');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-prv');
    }
    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['terms_url'] = smoothLink('index.php?tab1=admin&tab2=terms');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-terms');
    }
    $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-main-end');
    if ((@$seller['system'] || @$seller['moderator'])) {
        $themeData['notf_url'] = smoothLink('index.php?tab1=admin&tab2=fcm');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-app-start');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-notf');
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-app-end');
    }
    if ($adminLogged) {
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-end');
    } else {
        $themeData['sidebar'] .= \iCms\UI::view('backend/global/sidebar/side-end-global');
    }
} else {
    $themeData['sidebar'] = '';
}