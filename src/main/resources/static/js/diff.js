"use strict";
const param = document.location.search;
const pathes = document.location.pathname.split('/');
const repository = pathes[2];
const sha1 = pathes[3];

const p = repository + "/" + sha1;
$.ajax({
    method:'get',
    url: '/api/' + p + param
}).then(function(data){
    $(document).ready(function () {
        $('#compare').mergely({
            cmsettings: {
                readOnly: true,
                lineNumbers: true,

            },
            lhs: function(setValue) {
                setValue(data[1]);
            },
            rhs: function(setValue) {
                setValue(data[0]);
            },
            editor_height: '500px',
            editor_width: '48%',
            ignorews: true


        });
    });
});
