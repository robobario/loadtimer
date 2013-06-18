function doOnLoad() {
    function getHourlyAverages(key){
        $.getJSON(host + "/averages/hour/" + key, function (data) {
            application.showTopHourlyAverages(data);
        });
    }

    var HOURLY = "hourly"

    window.computed = 0;
    var host = "http://localhost:8080"
    var LoadtimerApplication = function() {
       var self = this;
       self.view = ko.observable(HOURLY)
       self.currentTimeBucket = ko.observable("")
       self.nextTimeBucket = ko.observable("")
       self.previousTimeBucket = ko.observable("")
       self.topAverages = ko.observable([]);
       self.averagesHeader = ko.computed(function(){
           switch(self.view()){
               case HOURLY:
                   return "Slowest average load times for hour beginning : " +self.currentTimeBucket()
           }});

        self.previousAveragesButtonLabel = ko.computed(function(){
            switch(self.view()){
                case HOURLY:
                    return "< previous hour : " +self.previousTimeBucket()
            }});

        self.nextAveragesButtonLabel = ko.computed(function(){
            switch(self.view()){
                case HOURLY:
                    return "next hour : " +self.nextTimeBucket() + " >"
            }});

        self.onPreviousClick = function(){
            getHourlyAverages(self.previousTimeBucket())
        }

        self.onNextClick = function(){
            getHourlyAverages(self.nextTimeBucket())
        }

       self.showTopHourlyAverages = function (data){
           self.view(HOURLY)
           self.topAverages(data.loadAverages)
           self.currentTimeBucket(data.bucketKey)
           self.previousTimeBucket(data.previousKey)
           self.nextTimeBucket(data.nextKey)
       }
    }

    var application = new LoadtimerApplication();
    // Activates knockout.js
    function update(){
        $.getJSON(host + "/slowest/last-hour", function (data) {
            application.showTopHourlyAverages(data);
        });
    }
    ko.applyBindings(application);
    update();

    function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }
}
