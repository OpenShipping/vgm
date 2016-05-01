angular.module('VGMCertificatesModule', [])

.config(function($routeProvider, $locationProvider) {
    $routeProvider
    .when('/vgm-certificates', {
        templateUrl : 'components/vgm-certificates/vgm-certificates.html',
        controller : 'CertificatesCtrl'
    })
    .when('/vgm-certificates/new', {
        templateUrl : 'components/vgm-certificates/vgm-form.html',
        controller : 'VGMFormCtrl'
    })
    ;

})

.controller('CertificatesCtrl', function($scope, $http) {


    $http({
        method : "GET",
        url : 'rest/private/vgm-certificate',
    }).then(function(response) {
        $scope.certificates = response.data.certificates;
    }, function() {

    });

})

.controller('CertificateCtrl', function($scope) {

    $scope.printCertificate = function(index) {

      var printContents = document.getElementById("certificate-" + index).innerHTML;
      var popupWin = window.open();
      popupWin.document.open();
      popupWin.document.write('<html><head><link rel="stylesheet" type="text/css" href="style.css"/>'+
        '<link href="bower_components/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">'+
        '<link href="bower_components/font-awesome/css/font-awesome.min.css" rel="stylesheet">'+
        '<link href="custom.less" rel="stylesheet/less" type="text/css"/>'+
        '<script src="bower_components/less/dist/less.min.js" type="text/javaScript"></script>'+
        '</head><body onload="window.print()">' + printContents + '</body></html>');
      popupWin.document.close();
    }
})


.controller('VGMFormCtrl', function($scope, $http, SecurityService) {

    $scope.lbToKg = function(lb) {
        return Math.round(lb / 0.45359237 * 1000) / 1000;
    }

    $scope.kgToLb = function(kg) {
        return Math.round(kg * 0.45359237 * 1000) / 1000;
    }
    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    // for e-signatures
    $scope.firstNameToMatch = SecurityService.getFirstName();
    $scope.lastNameToMatch = SecurityService.getLastName();

    function CargoItem() {
        this.description;
        this.weightKg;
        this.weightLb;
    }

    $scope.selectedContainer;
    $scope.selectContainer = function (index) {
        $scope.selectedContainer = $scope.containerSearchResults.splice(index, 1).pop();
        $scope.containerSearchResults = [$scope.selectedContainer];
        $scope.selectedContainer.cargoItems = [new CargoItem()];
        $scope.updateTotals();
    }

    $scope.updateTotals = function() {
        var totalWeightLb = $scope.selectedContainer.cargoItems.reduce(
            function(previousValue, currentValue) {
                currentValue = !isNumber(currentValue.weightLb) ? 0 : currentValue.weightLb;
                return previousValue + currentValue;
        }, 0);
        totalWeightLb += $scope.selectedContainer.tareWeightLb;
        $scope.totalWeightLb = Math.round(totalWeightLb * 1000) / 1000;

        var totalWeightKg = $scope.selectedContainer.cargoItems.reduce(
            function(previousValue, currentValue) {
                currentValue = !isNumber(currentValue.weightKg) ? 0 : currentValue.weightKg;
                return previousValue + currentValue;
        }, 0);
        totalWeightKg += $scope.selectedContainer.tareWeightKg;
        $scope.totalWeightKg = Math.round(totalWeightKg * 1000) / 1000;
    }
    $scope.addCargoItem = function() {
        $scope.selectedContainer.cargoItems.push(new CargoItem());
        $scope.updateTotals();
    };
    $scope.removeCargoItem = function() {
        if ($scope.selectedContainer.cargoItems.length > 1) { // Don't allow < 1 cargoItems
            $scope.selectedContainer.cargoItems.splice(-1, 1);
        };
        $scope.updateTotals();
    };
    $scope.decline = function() {
        $scope.containerSearch = "";
        $scope.containerSearchResults = [];
        $scope.selectedContainer = null;
    }

    $scope.searchForContainers = function () {
        if ($scope.containerSearch == '') {
            $scope.decline();
            return;
        }
        $http({
            method : "GET",
            url : 'rest/private/shipping-containers',
            params : { reference : $scope.containerSearch },
        }).then(function(response) {
            $scope.containerSearchResults = response.data.shippingContainers;
        }, function() {

        });
    }

    $scope.submitVgmForm = function () {
        console.log("reached submit vgm form");
        $http({
            method : "POST",
            url : 'rest/private/vgm-certificate',
            params : { certificate : $scope.selectedContainer,
                        vgmLb : $scope.totalWeightLb,
                        vgmKg : $scope.totalWeightKg
                     },
        }).then(function(response) {
            $scope.certificateSaved = response.data.certificateSaved;
        }, function() {

        });
    }


})
// end
;
