function doOnLoad() {

    var FIVE_MINUTELY = "Five Minutely"
    var HOURLY = "Hourly"
    var DAILY = "Daily"
    var WEEKLY = "Weekly"
    var MONTHLY = "Monthly"

    window.computed = 0;
    var host = "http://localhost:8080"
    var LoadtimerApplication = function () {
        var self = this;
        self.currentTimeBucket = ko.observable("")
        self.nextTimeBucket = ko.observable("")
        self.previousTimeBucket = ko.observable("")
        self.topAverages = ko.observable([]);
        self.periodSelectOptions = ko.observableArray([FIVE_MINUTELY,HOURLY,DAILY,WEEKLY,MONTHLY])
        self.selectedPeriodOption = ko.observable(HOURLY)
        self.averagesHeader = ko.computed(function () {
            switch (self.selectedPeriodOption()) {
                case FIVE_MINUTELY:
                    return "Slowest average load times for five minutes beginning : " + self.currentTimeBucket()
                case HOURLY:
                    return "Slowest average load times for hour beginning : " + self.currentTimeBucket()
                case DAILY:
                    return "Slowest average load times for day beginning : " + self.currentTimeBucket()
                case WEEKLY:
                    return "Slowest average load times for week beginning : " + self.currentTimeBucket()
                case MONTHLY:
                    return "Slowest average load times for month beginning : " + self.currentTimeBucket()
            }
        });

        self.previousAveragesButtonLabel = ko.computed(function () {
            switch (self.selectedPeriodOption()) {
                case FIVE_MINUTELY:
                    return "< previous five minutes : " + self.previousTimeBucket()
                case HOURLY:
                    return "< previous hour : " + self.previousTimeBucket()
                case DAILY:
                    return "< previous day : " + self.previousTimeBucket()
                case WEEKLY:
                    return "< previous week : " + self.previousTimeBucket()
                case MONTHLY:
                    return "< previous month : " + self.previousTimeBucket()
            }
        });

        self.nextAveragesButtonLabel = ko.computed(function () {
            switch (self.selectedPeriodOption()) {
                case FIVE_MINUTELY:
                    return "next five minutes : " + self.nextTimeBucket() + " >"
                case HOURLY:
                    return "next hour : " + self.nextTimeBucket() + " >"
                case DAILY:
                    return "next day : " + self.nextTimeBucket() + " >"
                case WEEKLY:
                    return "next week : " + self.nextTimeBucket() + " >"
                case MONTHLY:
                    return "next month : " + self.nextTimeBucket() + " >"
            }
        });

        function getAveragesUrl(key) {
            switch (self.selectedPeriodOption()) {
                case FIVE_MINUTELY:
                    return host + "/averages/fiveMinute/" + key;
                case HOURLY:
                    return host + "/averages/hour/" + key;
                case DAILY:
                    return host + "/averages/day/" + key;
                case WEEKLY:
                    return host + "/averages/week/" + key;
                case MONTHLY:
                    return host + "/averages/month/" + key;
            }
        }

        function getLatestAveragesUrl() {
            switch (self.selectedPeriodOption()) {
                case FIVE_MINUTELY:
                    return host + "/slowest/last-five-minutes";
                case HOURLY:
                    return host + "/slowest/last-hour";
                case DAILY:
                    return host + "/slowest/last-day";
                case WEEKLY:
                    return host + "/slowest/last-week";
                case MONTHLY:
                    return host + "/slowest/last-month";
            }
        }

        function getHourlyAverages(key) {
            $.getJSON(getAveragesUrl(key), function (data) {
                application.showTopAverages(data);
            });
        }

        self.onPreviousClick = function () {
            getHourlyAverages(self.previousTimeBucket())
        }

        self.onNextClick = function () {
            getHourlyAverages(self.nextTimeBucket())
        }

        self.showTopAverages = function (data) {
            self.topAverages(data.loadAverages)
            self.currentTimeBucket(data.bucketKey)
            self.previousTimeBucket(data.previousKey)
            self.nextTimeBucket(data.nextKey)
        }

        self.showLatest = function() {
            $.getJSON(getLatestAveragesUrl(), function (data) {
                application.showTopAverages(data);
            });
        }

        self.selectedPeriodOption.subscribe(function(){
           self.showLatest();
        })

    }

    var application = new LoadtimerApplication();
    // Activates knockout.js
    ko.applyBindings(application);
    application.showLatest()
    function escapeRegExp(str) {
        return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
    }
}
