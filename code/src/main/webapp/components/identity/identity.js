angular.module('IdentityModule', ['ngResource', 'ngRoute'])

.config(function($routeProvider, $locationProvider) {
    $routeProvider
    .when('/login', {
        templateUrl : 'components/identity/login.html',
        controller : 'LoginCtrl'
    })
    .when('/activate/:activationCode', {
        templateUrl : 'components/identity/login.html',
        controller : 'LoginCtrl'
    })
    .when('/register', {
        templateUrl : 'components/identity/registration-form.html',
        controller : 'RegistrationFormCtrl'
    })
    .when('/profile', {
        templateUrl : 'components/identity/profile.html',
        controller : 'ProfileCtrl'
    })

    ;

})

.controller('LoginCtrl', function($scope, LoginResource, ProfileResource, RegistrationResource,
        SecurityService, $location, $timeout, $routeParams) {

    $scope.newUser = {};
    $scope.activation = {};

    if ($routeParams.activationCode) {
        RegistrationResource.put(
            {
                activationCode : $routeParams.activationCode,
            },
            null, // No Post Data
            function(success) {
                $scope.activation.activated = success.activated;
                $scope.activation.previouslyActivated = success.previouslyActivated;
            },
            function(error) {
                $scope.unknownError = true;
            }

        );
    };

    $scope.login = function() {
        if ($scope.newUser.userId != undefined && $scope.newUser.password != undefined) {
            SecurityService.endSession(); // Remove old token before login
            LoginResource($scope.newUser).login($scope.newUser, function(success) {

                SecurityService.initSession(success);

                // Get profile info to know routing allowed
                ProfileResource.get(null, function(success) {
                    SecurityService.initProfile(success);
                    // without timeout location change can happen before backend is ready for the token
                    $timeout(function() { $location.path("/vgm-certificates"); }, 50);
                }, function(error) {
                    // May not access certain areas
                });
            }, function(error) {
                if (error.status === 403) {
                    $scope.invalidCredentials = true;
                }
                $scope.loginError = true;
                $scope.errorStatusText = error.statusText;
            });
        }
    };
})

.controller('NavigationCtrl', function($scope, LogoutService, SecurityService) {
    $scope.logout = LogoutService.logout;
    $scope.isNYSHEX = SecurityService.getBusinessType() == "NYSHEX" ? true : false;
})

.controller('RegistrationFormCtrl', function($scope, RegistrationResource) {

    $scope.businessTypes = [
        {
            type : "CARRIER",
            description : "CARRIER/VOCC"
        },
        {
            type : "SHIPPER",
            description : "SHIPPER/BCO"
        },
        {
            type : "CARRIER",
            description : "FORWARDER/NVOCC"
        },
    ];

    $scope.submitRegistration = function () {
        $scope.emailAlreadyExists = false;
        var failedEmailAddresses = [];
        $scope.$watch('email', function() {
            failedEmailAddresses.forEach(function(failedEmail){
                $scope.emailAlreadyExists =
                    $scope.email == failedEmail ? true : false;
            })
        });

        RegistrationResource.post(
            {
            registrationParams :
                {
                    businessName : $scope.businessName,
                    businessType : $scope.businessType.type,
                    businessAddress : $scope.businessAddress,
                    businessCity : $scope.businessCity,
                    businessPostalCode : $scope.businessPostalCode,
                    businessCountry : $scope.businessCountry,
                    firstName : $scope.firstName,
                    lastName : $scope.lastName,
                    email : $scope.email,
                    password : $scope.password
                }
            },
            null,
            function(success) {
                if (success.emailAlreadyExists) {
                    $scope.emailAlreadyExists = success.emailAlreadyExists;
                    failedEmailAddresses.push($scope.email);
                }
                $scope.registered = success.registered;
            },
            function(error) {
                $scope.unknownError = error.unknownError;
            }
        );
    };

})

.controller('ProfileCtrl', function($scope, ProfileResource) {

    $scope.$watchGroup(['password', 'confirmPassword'], function() {
        $scope.newPasswordsDoNotMatch =
            $scope.password != $scope.confirmPassword ? true : false;
    });


    $scope.profile = ProfileResource.get();

    $scope.updatePassword = function () {
        ProfileResource.put(
            {
                newPassword: $scope.password,
                oldPassword: $scope.oldPassword
            },
            null, // No Post Data
            function(success) {
                $scope.updatedPassword = success.updated;
                $scope.invalidOldPassword = success.invalidOldPassword;
            },
            function(error) {
                $scope.unknownError = true;
            }
        );
    };


})

.factory('ProfileResource', function($resource) {
    return $resource('rest/private/identity', null, {
        put : { method: 'PUT'}
    });
})

.factory('RegistrationResource', function($resource) {
    return $resource('/rest/registration', null, {
        post : { method: 'POST' },
        put : { method: 'PUT' }
    });
})

.factory('LoginResource', function($resource) {
    return function(newUser) {
        return $resource('rest/private/:dest', {}, {
            login : {
                method : 'POST',
                params : {
                    dest : "authc",
                },
                headers : {
                    "Authorization" : "Basic " + btoa(newUser.userId + ":" + newUser.password),
                    "X-Requested-With" : "XMLHttpRequest",
                },
            },
        });
    }
})

.factory('LogoutService', function($http, SecurityService, $location) {
    return {
        logout : function() {
            $http.post('rest/private/logout').finally(function() {
                SecurityService.endSession();
                $location.path("/login");
            });
        },
    };
})

.factory('SecurityService', function() {
    var SecurityService = function() {
        this.initSession = function(success) {
            // persist token, user id to the storage
            sessionStorage.setItem('token', success.authctoken);
        };

        this.initProfile = function(success) {
            // persist business type to indicate available paths
            sessionStorage.setItem('businessType', success.businessType);
            sessionStorage.setItem('firstName', success.firstName);
            sessionStorage.setItem('lastName', success.lastName);
        };

        this.endSession = function() {
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('businessType');
            sessionStorage.removeItem('firstName');
            sessionStorage.removeItem('lastName');
        };

        this.getToken = function() {
            return sessionStorage.getItem('token');
        };

        this.getBusinessType = function() {
            return sessionStorage.getItem('businessType');
        }
        this.getFirstName = function() {
            return sessionStorage.getItem('firstName');
        }
        this.getLastName = function() {
            return sessionStorage.getItem('lastName');
        }



        this.secureRequest = function(requestConfig) {
            var token = this.getToken();
            if (token != null && token != '' && token != 'undefined') {
                requestConfig.headers['Authorization'] = 'Token ' + token;
            }
        };
    };
    return new SecurityService();
})

.factory('AuthHttpResponseInterceptor', function($q, $location, SecurityService) {
    return {
        'request' : function(config) {
            SecurityService.secureRequest(config);
            return config || $q.when(config);
        },

        'response' : function(response) {
            return response || $q.when(response);
        },

        'responseError' : function(rejection) {
            if (rejection.status === 401) {
                SecurityService.endSession();
                $location.path('/login');
            return $q.reject(rejection);
        },
    };
})

.config(function($httpProvider) {
    // Http Interceptor to check auth failures for xhr requests
    $httpProvider.interceptors.push('AuthHttpResponseInterceptor');
})

// end
;