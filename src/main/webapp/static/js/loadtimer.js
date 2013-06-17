function doOnLoad() {
    window.computed = 0;
    var host = ".."
    var LoadtimerApplication = function(data) {
       var self = this;
       self.searchProp = ko.observable("");
    }

    var application = new LoadtimerApplication({});
    // Activates knockout.js
    function update(){
        $.getJSON(host + "/environment", function (env) {
            application.update(env);
            window.latest = env;
        });
    }
    ko.applyBindings(application);

    function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }
}
