(function () {
    'use strict';

    function insertYoutube(editor) {
        editor.insertHtml('[youtube]' + editor.lang.wsc.iyoutube_desc + '[/youtube]');
    }

    CKEDITOR.plugins.add('iYoutube', {
        icons: 'iyoutube',
        init: function (editor) {
            editor.addCommand('insertYoutube', {
                exec: function (editor) {
                    insertYoutube(editor);
                }
            });
            editor.ui.addButton('iYoutube', {
                label: editor.lang.wsc.iyoutube,
                command: 'insertYoutube',
                toolbar: 'insert'
            });
        }
    });
})();