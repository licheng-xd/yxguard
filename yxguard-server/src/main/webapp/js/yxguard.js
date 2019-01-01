/**
 * Created by lc on 16/6/24.
 */

init();

var instanceidList = new Array();
var instList = {
    Set : function(key,value){this[key] = value},
    Get : function(key){return this[key]},
    Contains : function(key){return this.Get(key) == null?false:true},
    Remove : function(key){delete this[key]}
}

function init() {
    $.ajax({
        type: 'GET',
        url: "/yxguard/show/services",
        beforeSend: function(XMLHttpRequest){
        },
        success: function(data, textStatus){
            var resp = eval("(" + data + ")");
            console.log(resp);
            if (resp["code"] == 200) {
                var services = resp["obj"];
                if (services.length == 0) {
                    console.log("no valid service");
                }
                // 渲染服务列表
                var servicesid = document.getElementById("servicesid");
                servicesid.innerHTML = '<li id="servtitle" class="tq">Services</li>';
                for (var idx in services) {
                    var serviceName = services[idx];
                    servicesid.innerHTML += '<li id="li' + serviceName + '"><a onclick="getInstances(\'' + serviceName + '\')" class="ce apn ame">' + serviceName + '</a></li>';
                }
            } else {
                // 错误页面
                console.error(data);
            }
        },
        complete: function(XMLHttpRequest, textStatus){
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            console.error();
        }
    });
}



function getInstances(serviceName) {
    serviceName = serviceName.trim();
    makeServiceActive(serviceName);
    $.ajax({
        type: 'GET',
        url: "/yxguard/show/instances/" + serviceName,
        beforeSend: function(XMLHttpRequest){
        },
        success: function(data, textStatus){
            clearDetail();
            var resp = eval("(" + data + ")");
            console.log(resp);
            if (resp["code"] == 200) {
                var instances = resp["obj"];
                if (instances.length == 0) {
                    console.log("no valid instance");
                }
                // 渲染实例列表
                instanceidList = [];
                var instancetitleid = document.getElementById("instancetitleid");
                instancetitleid.innerHTML = "Instances of " + serviceName;
                var instancesid = document.getElementById("instancesid");
                instancesid.innerHTML = '';
                for (var idx in instances) {
                    //var inst = eval("(" + instances[idx] + ")");
                    var inst = instances[idx];
                    var instanceid = inst['id'];
                    instanceidList.push(instanceid);
                    instList.Set(instanceid, inst);
                    instancesid.innerHTML += getInstanceHtml(inst);
                }
            } else {
                // 错误页面
                console.error(data);
            }
        },
        complete: function(XMLHttpRequest, textStatus){
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            console.error();
        }
    });
}

function makeServiceActive(serviceName) {
    var servicesid = document.getElementById("servicesid");
    for (var idx in servicesid.children) {
        if (servicesid.children[idx].id == serviceName) {
            servicesid.children[idx].className = "active";
        } else if (servicesid.children[idx].id == 'servtitle') {
            servicesid.children[idx].className = "tq";
        } else {
            servicesid.children[idx].className = "";
        }
    }
    var liserviceid = document.getElementById("li" + serviceName);
    liserviceid.className = "active";
}

function getInstanceHtml(inst) {
    var instanceid = inst['id'];
    var ip = inst['address'];
    var port = inst['port'];
    var regtime = inst['regtime'];
    var hostname = inst['hostname'];
    var payload = inst['payload'];
    var on = true
    //noinspection JSValidateTypes
    //if (payload)
    //{
    //    var payloadJo = eval('(' + payload + ')');
    //    var onInPayload = payloadJo['on']
    //    if(typeof onInPayload != undefined){
    //        on = onInPayload;
    //    }
    //}

    return '<div class="gi ali"><div class="ako ale instance"><div class="ant" id="preloader5" '+ (on ? '' : 'style="animation: 0"')+'></div></div>'
            +'<div id="' + instanceid + '" class="deactiveinstance" onclick="getInstanceDetail(\''+ instanceid +'\')"><strong>' + hostname + '</strong><br><strong>' + ip + '</strong></div></div>';
}

function getInstanceDetail(instid) {
    var inst = instList.Get(instid);
    var instanceid = inst['id'];
    var serviceName = inst['name'];
    var ip = inst['address'];
    var port = inst['port'];
    var url = inst['url'];
    var regtime = inst['regtime'];
    var hostname = inst['hostname'];
    var payload = inst['payload'];
    var version = inst['version'];
    makeInstanceActive(instanceid);
    var detailid = document.getElementById('detailid');
    detailid.innerHTML = '<li>service: ' + serviceName + '</li>';
    detailid.innerHTML += '<li>id: ' + instanceid + '</li>';
    detailid.innerHTML += '<li>hostname: ' + hostname + '</li>';
    detailid.innerHTML += '<li>ip: ' + ip + '</li>';
    detailid.innerHTML += '<li>port: ' + port + '</li>';
    detailid.innerHTML += '<li>version: ' + version + '</li>';
    detailid.innerHTML += '<li>url: ' + url + '</li>';
    detailid.innerHTML += '<li>regtime: ' + new Date(regtime).toLocaleString() + '</li>';
    var payloadHtml
    if (payload)
    {
        payload = eval('(' + payload + ')');
        payloadHtml = '<li>payload:<br><pre>'+syntaxHighlight(payload)+'</pre></li>'
    }else{
        payloadHtml = '<li>payload:'+payload+'</li>'
    }
    detailid.innerHTML += payloadHtml;

    detailid.innerHTML += ('<div class="btndiv"><button onclick="unregister(\'' + serviceName + '\',\'' + instanceid + '\')" class="btn-default">注销</button></div>');
}

function syntaxHighlight(json) {
    if (typeof json != 'string') {
        json = JSON.stringify(json, undefined, 2);
    }
    json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

function makeInstanceActive(instanceid) {
    for (var idx in instanceidList) {
        if (instanceid == instanceidList[idx])
            document.getElementById(instanceidList[idx]).className = 'activeinstance';
        else
            document.getElementById(instanceidList[idx]).className = 'deactiveinstance';
    }
}

function unregister(serviceName, instanceid) {
    if (!confirm("确定删除实例: " + serviceName + "  " + instanceid)) {
        return;
    }

    $.ajax({
        type: 'GET',
        url: "/yxguard/manager/remove/" + serviceName + "/" + instanceid,
        beforeSend: function(XMLHttpRequest){
        },
        success: function(data, textStatus){
            var resp = eval("(" + data + ")");
            console.log(resp);
            if (resp["code"] == 200) {
                var instance = resp["obj"];
                // 删除页面上对应的实例
                removeInstance(serviceName, instanceid);
            } else {
                // 错误页面
                console.error(data);
            }
        },
        complete: function(XMLHttpRequest, textStatus){
        },
        error:function(XMLHttpRequest, textStatus, errorThrown){
            console.error();
        }
    });
}

function removeInstance(serviceName, instanceid) {
    clearDetail();
    getInstances(serviceName);
}

function clearDetail() {
    var detailid = document.getElementById('detailid');
    detailid.innerHTML = '';
}
