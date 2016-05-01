angular.module('nyshexFilters', [])

.filter('percentageFormat', function() {
    return function(number) {
        return String(number * 100) + "%";
    }
})

.filter('utcTimestampFormat', function() {
    /**
    * @param utcTimestamp
    *        in ISO formatted UTC
    * @return dates
    **/
    return function(utcTimestamp) {
        return utcTimestamp ? new moment(utcTimestamp).tz("UTC").format("YYYY-MM-DD HH:mm:ss z") : "Unknown";
    }
})

