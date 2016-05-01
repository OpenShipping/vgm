angular.module('nyshexApp', [
    'ngRoute',
    'ngAnimate',
    'ui.bootstrap',
    'infinite-scroll',
    'nyshexFilters',
    'VGMCertificatesModule',
    'ShippingContainersModule',
    'IdentityModule'
])

.config(function($routeProvider, $locationProvider) {
    $routeProvider
    .otherwise({
        redirectTo : '/login'
    })
    ;
    // $locationProvider.html5Mode(true);
})

// end
;
