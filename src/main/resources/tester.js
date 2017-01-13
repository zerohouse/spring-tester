var app = angular.module('app', ["ngPrettyJson", 'ngFileUpload']);
app.controller('apiCtrl', function ($scope, $http) {

    $scope.methods = ['GET', 'HEAD', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'TRACE'];
    $scope.headers = typeof headers === "undefined" ? {} : headers;
    $scope.tableHeaders = typeof tableHeaders === "undefined" ? {
            name: "Name",
            url: "URL",
            method: "Method"
        } : tableHeaders;
    $scope.apis = typeof apis === "undefined" ? [] : apis;
    $scope.title = typeof title === "undefined" ? "" : title;

    $scope.selectApi = function (api) {
        $scope.apis.forEach(function (api) {
            api.select = false;
        });
        api.select = true;
        if (!$scope.selectedApi)
            $scope.selectedApi = {};

        angular.copy(api, $scope.selectedApi);
        $scope.response = null;
        if ($scope.selectedApi.json) {
            $scope.type = 'json';
        }
        if (!$scope.selectedApi.method) {
            $scope.selectedApi.method = 'GET';
        }
    };
    $scope.newHeader = function () {
        $scope.headers[prompt("Key?")] = "";
    };
    $scope.deleteHeader = function (key) {
        delete $scope.headers[key];
    };

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
            options.parameter = api.parameter;
        } else if ($scope.type === "json")
            options.data = api.parameter;
        else if ($scope.type === "form") {
            var fd = new FormData();
            for (var key in api.parameter) {
                fd.append(key, api.parameter[key]);
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
            options.data = api.parameter;
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
});