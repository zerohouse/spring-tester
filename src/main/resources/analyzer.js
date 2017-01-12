var app = angular.module('app', ["ngPrettyJson", 'ngFileUpload']);
app.controller('apiCtrl', function ($scope, $http) {
    $scope.selectApi = function (api) {
        $scope.apis.forEach(function (api) {
            api.select = false;
        });
        api.select = true;
        if (!$scope.selectedApi)
            $scope.selectedApi = {};

        angular.copy(api, $scope.selectedApi);
        $scope.response = null;
        if (api.json) {
            $scope.type = 'json';
        }
    };
    $scope.newHeader = function () {
        $scope.headers[prompt("헤더 키값")] = "";
    };
    $scope.deleteHeader = function (key) {
        delete $scope.headers[key];
    };
    $scope.headers = {};
    $scope.$watch('type', function (type) {
        switch (type) {
            case "json":
                $scope.headers['Content-Type'] = 'application/json';
                break;
            case "urlencoded":
                $scope.headers['Content-Type'] = 'application/x-www-form-urlencoded';
                break;
            default:
                delete $scope.headers['Content-Type'];
        }
    });
    $scope.$watch('selectedApi.method', function (method) {
        if (method === "GET" || method === "DELETE")
            $scope.type = 'urlencoded';
    });
    $scope.sendApi = function (api) {
        var method = api.method;
        var options = {
            method: method, url: api.url
        };
        options.headers = $scope.headers;
        if (method === "GET" || method === "DELETE") {
            options.url += "?dc=" + new Date().getTime().toString();
            options.params = api.params;
        } else if ($scope.type === "json")
            options.data = api.params;
        else if ($scope.type === "form") {
            var fd = new FormData();
            for (var key in api.params) {
                fd.append(key, api.params[key]);
            }
            options.data = fd;
            options.transformRequest = angular.identity;
        }
        else {
            options.transformRequest = function (obj) {
                var str = [];
                for (var p in obj) {
                    if (obj[p] === undefined || obj[p] === "" || typeof obj[p] === "function" || obj[p] === null || obj[p] === "null")
                        continue;
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
                return str.join("&");
            };
            options.data = api.params;
        }

        $http(options).then(function onSuccess(res) {
            $scope.response = res;
        }).catch(function onError(e) {
            $scope.response = e;
        });
    };
    $scope.isEmpty = function (map) {
        for (var key in map) {
            return !map.hasOwnProperty(key);
        }
        return true;
    };
    $scope.apis = apis;
});