<%--
  Created by IntelliJ IDEA.
  User: lc
  Date: 16/6/25
  Time: 下午2:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="licheng">

    <title>YXGuard</title>

    <!--<link href="./css" rel="stylesheet">-->
    <link href="../css/toolkit-inverse.css" rel="stylesheet">
    <link href="../css/application.css" rel="stylesheet">
    <link href="../css/yxguard.css" rel="stylesheet">

    <style>
        /* note: this is a hack for ios iframe for bootstrap themes shopify page */
        /* this chunk of css is not part of the toolkit :) */
        body {
            width: 1px;
            min-width: 100%;
            *width: 100%;
        }
    </style>
</head>
<body>
<div class="bw">
    <div class="fu">

        <div class="ge aom">
            <nav class="aot">
                <div class="collapse and" id="nav-toggleable-sm">
                    <div class="aon">
                        <a class="aop cn" href="/">
                            <span class="bv aoq">YXGuard</span>
                        </a>
                    </div>
                    <hr class="aky">

                    <ul id="servicesid" class="nav of nav-stacked">

                    </ul>
                </div>
            </nav>
        </div>

        <div class="hc">

            <div class="apa">
                <div class="apb">
                    <h6 id="instancetitleid" class="apd">instances of service1</h6>
                </div>
            </div>

            <div class="fu db aln" id="instancesid">
            </div>

            <div class="anv alg ala">
                <h3 class="anw anx">Details</h3>
            </div>

            <div class="fu apt">
                <div class="gq gg ala">
                    <div class="apu ano">
                        <div class="alz">
                            <ul class="detail" id="detailid">
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>

<script src="../js/jquery.min.js"></script>
<script src="../js/yxguard.js"></script>
<script>
    // execute/clear BS laders for docs
    $(function(){while(window.BS&&window.BS.loader&&window.BS.loader.length){(window.BS.loader.pop())()}})
</script>
</body>
</html>
