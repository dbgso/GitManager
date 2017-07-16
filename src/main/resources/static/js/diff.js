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
            width: 'auto',
            height: 'auto',
            ignorews: true
        });

        $(window).on("resize", function (){
            $("#compare").css("height", get_current_height() + "px");
        });

        bind_operations();

        $("#compare").css("height", get_current_height() + "px");
        $('#compare').mergely('resize');
    });
});


function bind_operations (){
    document.querySelector('#next-diff').onclick = function() {
        $('#compare').mergely('scrollToDiff', 'next');
    };
    document.querySelector('#prev-diff').onclick = function() {
        $('#compare').mergely('scrollToDiff', 'prev');
    };
    document.querySelector('#compare').onkeydown = function (e)　{
        const key = e.key;
        if (key === 'ArrowDown')
            $('#compare').mergely('scrollToDiff', 'next');
        else if (key === 'ArrowUp')
            $('#compare').mergely('scrollToDiff', 'prev');
    };
}


function get_current_height () {
    const hsize = $(window).height() * 0.9;
    return hsize;
}
