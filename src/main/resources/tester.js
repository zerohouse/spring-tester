var app = angular.module('app', ["ngPrettyJson", 'ngFileUpload']);
app.filter('trust', [
    '$sce',
    function ($sce) {
        return function (value, type) {
            return $sce.trustAs(type || 'html', value);
        };
    }
]);
app.controller('apiCtrl', function ($scope, $http) {
    $scope.order = {};
    $scope.orderBy = function (order) {
        if ($scope.order.order === order) {
            $scope.order.desc = !$scope.order.desc;
            return;
        }
        $scope.order.order = order;
    };
    $scope.type = 'urlencoded';
    $scope.methods = ['GET', 'HEAD', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'TRACE'];
    $scope.headers = typeof headers === "undefined" ? {} : headers;
    $scope.tableHeaders = typeof tableHeaders === "undefined" ? {
            name: "Name",
            url: "URL",
            methodsString: "Method"
        } : tableHeaders;
    if ($scope.tableHeaders) {
        $scope.order.order = Object.keys($scope.tableHeaders)[0];
    }
    $scope.apis = typeof apis === "undefined" ? [] : apis;
    $scope.title = typeof title === "undefined" ? "" : title;
    $scope.apis.forEach(function (api) {
        api.methodsString = api.methods.join(", ");
        if (!api.parameter)
            api.parameter = {};
    });

    $scope.$watch('method', function (method) {
        if (method === "GET" || method === "DELETE") {
            $scope.type = 'urlencoded';
        }
    });

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
        if ($scope.selectedApi.methods.length == 0) {
            $scope.method = 'GET';
        } else
            $scope.method = $scope.selectedApi.methods[0];
    };
    $scope.newHeader = function () {
        $scope.headers[prompt("Key?")] = "";
    };
    $scope.newParameter = function () {
        $scope.selectedApi.parameter[prompt("Key?")] = "";
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
                $scope.headers['Content-Type'] = 'multipart/form-data';
        }
    });
    $scope.$watch('method', function (method) {
        if (method === "GET" || method === "DELETE")
            $scope.type = 'urlencoded';
    });
    $scope.sendApi = function (api) {
        var method = $scope.method;
        var options = {
            method: method, url: api.url
        };
        options.headers = $scope.headers;
        if (method === "GET" || method === "DELETE") {
            options.url += "?dc=" + new Date().getTime().toString();
            options.params = api.parameter;
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