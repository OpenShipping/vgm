angular.module('ShippingContainersModule', ["ngFileUpload"])

.config(function($routeProvider, $locationProvider) {
    $routeProvider
    .when('/shipping-containers/new', {
        templateUrl : 'components/shipping-containers/shipping-containers.html',
        controller : 'ShippingContainersCtrl'
    })
    ;

})

.controller('ShippingContainersCtrl', function($scope, $http, Upload) {
    $scope.submitCSV = function () {
        var file = $scope.shippingContainerCSV;
        if (file && !file.$error) {
            Upload.upload({
                url: 'rest/private/shipping-containers/parse-csv',
                data: { file: file },
            }).then(function (resp) {
                $scope.submittedCSV = true;
                if (resp.data.Error) {
                    $scope.upload.status = resp.data.Error;
                } else if (resp.data.Success) {
                    $scope.upload.progress = 100;
                    $scope.upload.status = resp.data.Success;
                }
                $scope.duplicateCarrierCodesFound = resp.data.duplicateCarrierCodesFound;
                $scope.carrierNotFound = resp.data.carrierNotFound;
                $scope.shippingContainers = resp.data.shippingContainers;
            }, function (resp) {
                $scope.upload.status = 'An error occurred. The file could not be uploaded.';
            }, function (evt) {
            });
        } else {
            $scope.upload.status = 'Please select a valid file';
        }
    };

    $scope.submitData = function(contract) {
        $http({
            method : "POST",
            url : 'rest/private/shipping-containers',
            params : { shippingContainers : $scope.shippingContainers },
        }).then(function(response) {
            $scope.submittedData = response.data.persisted;
        }, function() {
        });
    }

})

// end
;
