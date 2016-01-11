if(window.obex === undefined)
    window.obex = {};
window.obex.opp = function(str, callback) {
    cordova.exec(
        callback,
        function(err) { callback("Error: " + err); },
        "OBEX",
        "opp",
        [str]);
};

