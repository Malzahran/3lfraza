(function () {
    'use strict';
    var WindowFeatures = 'location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,resizable=yes,scrollbars=yes';

    function browseGallery(editor) {
        var url = editor.config['GalleryBrowseUrl'] + '?editor=' + editor.name;
        editor.popup(url, '80%', '70%', WindowFeatures);
    }

    CKEDITOR.plugins.add('iGallery', {
        icons: 'igallery',
        requires: 'popup',
        init: function (editor) {
            editor.addCommand('browseGallery', {
                exec: function (editor) {
                    browseGallery(editor);
                }
            });
            editor.ui.addButton('iGallery', {
                label: editor.lang.wsc.igallery,
                command: 'browseGallery',
                toolbar: 'insert'
            });
        }
    });
})();