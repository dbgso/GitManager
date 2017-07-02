"use strict";
const repository_name = location.href.match(".*/(.*)")[1];
$.ajax({
    url : "/api/commits/" + repository_name + "/commits",
    method : "GET"
}).then(function(data1) {
    console.log(data1[1]);
    const container = document.querySelector('#gitGraph');
    // provide data in the DOT language
    const DOTstring = 'dinetwork {1 -> 1 -> 2; 2 -> 3; 2 -- 4; 2 -> 1 }';
    const parsedData = vis.network.convertDot(DOTstring);

    const nodes = [];
    const edges = [];
    $.each(data1, function(i, d){
        d.id = d.sha1;
        d.label = d.message;
        nodes.fixed = false;
        nodes.push(d);
        d.color = 'blue';
        if (d.parents.length > 1)
            d.color = 'green';

        $.each(d.parents, function(pi, pd){
            edges.push({
                arrows: "from",
                to: pd,
                from: d.sha1
            });
        });
    });

    const data = {
        nodes: nodes,
        edges: edges
    };

    const options = {
        layout: {
            hierarchical: true
        },
        configure: {
            enabled: true,
            showButton: true,
            container: document.querySelector('#configure')
        }
    };


    // you can extend the options like a normal JSON variable:
    // options.nodes = {
    //     color: 'red'
    // };

    // create a network
    const network = new vis.Network(container, data, options);
    console.log("finish");
}).fail(function(data) {
    alert(data);
});
