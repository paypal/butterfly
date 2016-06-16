<%@ page trimDirectiveWhitespaces="true"
  contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<!-- Don't cache the page to avoid login issues -->
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="-1">

<!-- <link rel="shortcut icon" href="favicon.ico" type="image/vnd.microsoft.icon" /> -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.0.3/css/bootstrap-theme.min.css">
<title>ostara: platform upgrades as a service</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="resources/lib/boostrap3/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css" href="resources/lib/boostrap3/css/bootstrap-theme.min.css"/>
<link rel="stylesheet" type="text/css" href="resources/css/font-awesome.min.css"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" type="text/css" href="resources/css/app.css"/>
<link rel="stylesheet" type="text/css" href="resources/css/main.css"/>

<style type="text/css">
  @font-face{font-family:'FontAwesome';src:url(resources/fonts/fontawesome-webfont.eot);src:url(resources/fonts/fontawesome-webfont.woff) format('embedded-opentype'),url(resources/fonts/fontawesome-webfont.woff) format('woff'),url(resources/fonts/fontawesome-webfont.ttf) format('truetype'),url(resources/fonts/fontawesome-webfont.svg) format('svg');font-weight:normal;font-style:normal}.fa{display:inline-block;font-family:FontAwesome;font-style:normal;font-weight:normal;line-height:1;-webkit-font-smoothing:antialiased;-moz-osx-font-smoothing:grayscale}.fa-lg{font-size:1.3333333333333333em;line-height:.75em;vertical-align:-15%}.fa-2x{font-size:2em}.fa-3x{font-size:3em}.fa-4x{font-size:4em}.fa-5x{font-size:5em}.fa-fw{width:1.2857142857142858em;text-align:center}.fa-ul{padding-left:0;margin-left:2.142857142857143em;list-style-type:none}.fa-ul>li{position:relative}.fa-li{position:absolute;left:-2.142857142857143em;width:2.142857142857143em;top:.14285714285714285em;text-align:center}.fa-li.fa-lg{left:-1.8571428571428572em}.fa-border{padding:.2em .25em .15em;border:solid .08em #eee;border-radius:.1em}.pull-right{float:right}.pull-left{float:left}.fa.pull-left{margin-right:.3em}.fa.pull-right{margin-left:.3em}.fa-spin{-webkit-animation:spin 2s infinite linear;-moz-animation:spin 2s infinite linear;-o-animation:spin 2s infinite linear;animation:spin 2s infinite linear}@-moz-keyframes spin{0%{-moz-transform:rotate(0deg)}100%{-moz-transform:rotate(359deg)}}@-webkit-keyframes spin{0%{-webkit-transform:rotate(0deg)}100%{-webkit-transform:rotate(359deg)}}@-o-keyframes spin{0%{-o-transform:rotate(0deg)}100%{-o-transform:rotate(359deg)}}@-ms-keyframes spin{0%{-ms-transform:rotate(0deg)}100%{-ms-transform:rotate(359deg)}}@keyframes spin{0%{transform:rotate(0deg)}100%{transform:rotate(359deg)}}.fa-rotate-90{filter:progid:DXImageTransform.Microsoft.BasicImage(rotation=1);-webkit-transform:rotate(90deg);-moz-transform:rotate(90deg);-ms-transform:rotate(90deg);-o-transform:rotate(90deg);transform:rotate(90deg)}.fa-rotate-180{filter:progid:DXImageTransform.Microsoft.BasicImage(rotation=2);-webkit-transform:rotate(180deg);-moz-transform:rotate(180deg);-ms-transform:rotate(180deg);-o-transform:rotate(180deg);transform:rotate(180deg)}.fa-rotate-270{filter:progid:DXImageTransform.Microsoft.BasicImage(rotation=3);-webkit-transform:rotate(270deg);-moz-transform:rotate(270deg);-ms-transform:rotate(270deg);-o-transform:rotate(270deg);transform:rotate(270deg)}.fa-flip-horizontal{filter:progid:DXImageTransform.Microsoft.BasicImage(rotation=0,mirror=1);-webkit-transform:scale(-1,1);-moz-transform:scale(-1,1);-ms-transform:scale(-1,1);-o-transform:scale(-1,1);transform:scale(-1,1)}.fa-flip-vertical{filter:progid:DXImageTransform.Microsoft.BasicImage(rotation=2,mirror=1);-webkit-transform:scale(1,-1);-moz-transform:scale(1,-1);-ms-transform:scale(1,-1);-o-transform:scale(1,-1);transform:scale(1,-1)}.fa-stack{position:relative;display:inline-block;width:2em;height:2em;line-height:2em;vertical-align:middle}.fa-stack-1x,.fa-stack-2x{position:absolute;left:0;width:100%;text-align:center}.fa-stack-1x{line-height:inherit}.fa-stack-2x{font-size:2em}.fa-inverse{color:#fff}.fa-glass:before{content:"\f000"}.fa-music:before{content:"\f001"}.fa-search:before{content:"\f002"}.fa-envelope-o:before{content:"\f003"}.fa-heart:before{content:"\f004"}.fa-star:before{content:"\f005"}.fa-star-o:before{content:"\f006"}.fa-user:before{content:"\f007"}.fa-film:before{content:"\f008"}.fa-th-large:before{content:"\f009"}.fa-th:before{content:"\f00a"}.fa-th-list:before{content:"\f00b"}.fa-check:before{content:"\f00c"}.fa-times:before{content:"\f00d"}.fa-search-plus:before{content:"\f00e"}.fa-search-minus:before{content:"\f010"}.fa-power-off:before{content:"\f011"}.fa-signal:before{content:"\f012"}.fa-gear:before,.fa-cog:before{content:"\f013"}.fa-trash-o:before{content:"\f014"}.fa-home:before{content:"\f015"}.fa-file-o:before{content:"\f016"}.fa-clock-o:before{content:"\f017"}.fa-road:before{content:"\f018"}.fa-download:before{content:"\f019"}.fa-arrow-circle-o-down:before{content:"\f01a"}.fa-arrow-circle-o-up:before{content:"\f01b"}.fa-inbox:before{content:"\f01c"}.fa-play-circle-o:before{content:"\f01d"}.fa-rotate-right:before,.fa-repeat:before{content:"\f01e"}.fa-refresh:before{content:"\f021"}.fa-list-alt:before{content:"\f022"}.fa-lock:before{content:"\f023"}.fa-flag:before{content:"\f024"}.fa-headphones:before{content:"\f025"}.fa-volume-off:before{content:"\f026"}.fa-volume-down:before{content:"\f027"}.fa-volume-up:before{content:"\f028"}.fa-qrcode:before{content:"\f029"}.fa-barcode:before{content:"\f02a"}.fa-tag:before{content:"\f02b"}.fa-tags:before{content:"\f02c"}.fa-book:before{content:"\f02d"}.fa-bookmark:before{content:"\f02e"}.fa-print:before{content:"\f02f"}.fa-camera:before{content:"\f030"}.fa-font:before{content:"\f031"}.fa-bold:before{content:"\f032"}.fa-italic:before{content:"\f033"}.fa-text-height:before{content:"\f034"}.fa-text-width:before{content:"\f035"}.fa-align-left:before{content:"\f036"}.fa-align-center:before{content:"\f037"}.fa-align-right:before{content:"\f038"}.fa-align-justify:before{content:"\f039"}.fa-list:before{content:"\f03a"}.fa-dedent:before,.fa-outdent:before{content:"\f03b"}.fa-indent:before{content:"\f03c"}.fa-video-camera:before{content:"\f03d"}.fa-picture-o:before{content:"\f03e"}.fa-pencil:before{content:"\f040"}.fa-map-marker:before{content:"\f041"}.fa-adjust:before{content:"\f042"}.fa-tint:before{content:"\f043"}.fa-edit:before,.fa-pencil-square-o:before{content:"\f044"}.fa-share-square-o:before{content:"\f045"}.fa-check-square-o:before{content:"\f046"}.fa-arrows:before{content:"\f047"}.fa-step-backward:before{content:"\f048"}.fa-fast-backward:before{content:"\f049"}.fa-backward:before{content:"\f04a"}.fa-play:before{content:"\f04b"}.fa-pause:before{content:"\f04c"}.fa-stop:before{content:"\f04d"}.fa-forward:before{content:"\f04e"}.fa-fast-forward:before{content:"\f050"}.fa-step-forward:before{content:"\f051"}.fa-eject:before{content:"\f052"}.fa-chevron-left:before{content:"\f053"}.fa-chevron-right:before{content:"\f054"}.fa-plus-circle:before{content:"\f055"}.fa-minus-circle:before{content:"\f056"}.fa-times-circle:before{content:"\f057"}.fa-check-circle:before{content:"\f058"}.fa-question-circle:before{content:"\f059"}.fa-info-circle:before{content:"\f05a"}.fa-crosshairs:before{content:"\f05b"}.fa-times-circle-o:before{content:"\f05c"}.fa-check-circle-o:before{content:"\f05d"}.fa-ban:before{content:"\f05e"}.fa-arrow-left:before{content:"\f060"}.fa-arrow-right:before{content:"\f061"}.fa-arrow-up:before{content:"\f062"}.fa-arrow-down:before{content:"\f063"}.fa-mail-forward:before,.fa-share:before{content:"\f064"}.fa-expand:before{content:"\f065"}.fa-compress:before{content:"\f066"}.fa-plus:before{content:"\f067"}.fa-minus:before{content:"\f068"}.fa-asterisk:before{content:"\f069"}.fa-exclamation-circle:before{content:"\f06a"}.fa-gift:before{content:"\f06b"}.fa-leaf:before{content:"\f06c"}.fa-fire:before{content:"\f06d"}.fa-eye:before{content:"\f06e"}.fa-eye-slash:before{content:"\f070"}.fa-warning:before,.fa-exclamation-triangle:before{content:"\f071"}.fa-plane:before{content:"\f072"}.fa-calendar:before{content:"\f073"}.fa-random:before{content:"\f074"}.fa-comment:before{content:"\f075"}.fa-magnet:before{content:"\f076"}.fa-chevron-up:before{content:"\f077"}.fa-chevron-down:before{content:"\f078"}.fa-retweet:before{content:"\f079"}.fa-shopping-cart:before{content:"\f07a"}.fa-folder:before{content:"\f07b"}.fa-folder-open:before{content:"\f07c"}.fa-arrows-v:before{content:"\f07d"}.fa-arrows-h:before{content:"\f07e"}.fa-bar-chart-o:before{content:"\f080"}.fa-twitter-square:before{content:"\f081"}.fa-facebook-square:before{content:"\f082"}.fa-camera-retro:before{content:"\f083"}.fa-key:before{content:"\f084"}.fa-gears:before,.fa-cogs:before{content:"\f085"}.fa-comments:before{content:"\f086"}.fa-thumbs-o-up:before{content:"\f087"}.fa-thumbs-o-down:before{content:"\f088"}.fa-star-half:before{content:"\f089"}.fa-heart-o:before{content:"\f08a"}.fa-sign-out:before{content:"\f08b"}.fa-linkedin-square:before{content:"\f08c"}.fa-thumb-tack:before{content:"\f08d"}.fa-external-link:before{content:"\f08e"}.fa-sign-in:before{content:"\f090"}.fa-trophy:before{content:"\f091"}.fa-github-square:before{content:"\f092"}.fa-upload:before{content:"\f093"}.fa-lemon-o:before{content:"\f094"}.fa-phone:before{content:"\f095"}.fa-square-o:before{content:"\f096"}.fa-bookmark-o:before{content:"\f097"}.fa-phone-square:before{content:"\f098"}.fa-twitter:before{content:"\f099"}.fa-facebook:before{content:"\f09a"}.fa-github:before{content:"\f09b"}.fa-unlock:before{content:"\f09c"}.fa-credit-card:before{content:"\f09d"}.fa-rss:before{content:"\f09e"}.fa-hdd-o:before{content:"\f0a0"}.fa-bullhorn:before{content:"\f0a1"}.fa-bell:before{content:"\f0f3"}.fa-certificate:before{content:"\f0a3"}.fa-hand-o-right:before{content:"\f0a4"}.fa-hand-o-left:before{content:"\f0a5"}.fa-hand-o-up:before{content:"\f0a6"}.fa-hand-o-down:before{content:"\f0a7"}.fa-arrow-circle-left:before{content:"\f0a8"}.fa-arrow-circle-right:before{content:"\f0a9"}.fa-arrow-circle-up:before{content:"\f0aa"}.fa-arrow-circle-down:before{content:"\f0ab"}.fa-globe:before{content:"\f0ac"}.fa-wrench:before{content:"\f0ad"}.fa-tasks:before{content:"\f0ae"}.fa-filter:before{content:"\f0b0"}.fa-briefcase:before{content:"\f0b1"}.fa-arrows-alt:before{content:"\f0b2"}.fa-group:before,.fa-users:before{content:"\f0c0"}.fa-chain:before,.fa-link:before{content:"\f0c1"}.fa-cloud:before{content:"\f0c2"}.fa-flask:before{content:"\f0c3"}.fa-cut:before,.fa-scissors:before{content:"\f0c4"}.fa-copy:before,.fa-files-o:before{content:"\f0c5"}.fa-paperclip:before{content:"\f0c6"}.fa-save:before,.fa-floppy-o:before{content:"\f0c7"}.fa-square:before{content:"\f0c8"}.fa-bars:before{content:"\f0c9"}.fa-list-ul:before{content:"\f0ca"}.fa-list-ol:before{content:"\f0cb"}.fa-strikethrough:before{content:"\f0cc"}.fa-underline:before{content:"\f0cd"}.fa-table:before{content:"\f0ce"}.fa-magic:before{content:"\f0d0"}.fa-truck:before{content:"\f0d1"}.fa-pinterest:before{content:"\f0d2"}.fa-pinterest-square:before{content:"\f0d3"}.fa-google-plus-square:before{content:"\f0d4"}.fa-google-plus:before{content:"\f0d5"}.fa-money:before{content:"\f0d6"}.fa-caret-down:before{content:"\f0d7"}.fa-caret-up:before{content:"\f0d8"}.fa-caret-left:before{content:"\f0d9"}.fa-caret-right:before{content:"\f0da"}.fa-columns:before{content:"\f0db"}.fa-unsorted:before,.fa-sort:before{content:"\f0dc"}.fa-sort-down:before,.fa-sort-asc:before{content:"\f0dd"}.fa-sort-up:before,.fa-sort-desc:before{content:"\f0de"}.fa-envelope:before{content:"\f0e0"}.fa-linkedin:before{content:"\f0e1"}.fa-rotate-left:before,.fa-undo:before{content:"\f0e2"}.fa-legal:before,.fa-gavel:before{content:"\f0e3"}.fa-dashboard:before,.fa-tachometer:before{content:"\f0e4"}.fa-comment-o:before{content:"\f0e5"}.fa-comments-o:before{content:"\f0e6"}.fa-flash:before,.fa-bolt:before{content:"\f0e7"}.fa-sitemap:before{content:"\f0e8"}.fa-umbrella:before{content:"\f0e9"}.fa-paste:before,.fa-clipboard:before{content:"\f0ea"}.fa-lightbulb-o:before{content:"\f0eb"}.fa-exchange:before{content:"\f0ec"}.fa-cloud-download:before{content:"\f0ed"}.fa-cloud-upload:before{content:"\f0ee"}.fa-user-md:before{content:"\f0f0"}.fa-stethoscope:before{content:"\f0f1"}.fa-suitcase:before{content:"\f0f2"}.fa-bell-o:before{content:"\f0a2"}.fa-coffee:before{content:"\f0f4"}.fa-cutlery:before{content:"\f0f5"}.fa-file-text-o:before{content:"\f0f6"}.fa-building-o:before{content:"\f0f7"}.fa-hospital-o:before{content:"\f0f8"}.fa-ambulance:before{content:"\f0f9"}.fa-medkit:before{content:"\f0fa"}.fa-fighter-jet:before{content:"\f0fb"}.fa-beer:before{content:"\f0fc"}.fa-h-square:before{content:"\f0fd"}.fa-plus-square:before{content:"\f0fe"}.fa-angle-double-left:before{content:"\f100"}.fa-angle-double-right:before{content:"\f101"}.fa-angle-double-up:before{content:"\f102"}.fa-angle-double-down:before{content:"\f103"}.fa-angle-left:before{content:"\f104"}.fa-angle-right:before{content:"\f105"}.fa-angle-up:before{content:"\f106"}.fa-angle-down:before{content:"\f107"}.fa-desktop:before{content:"\f108"}.fa-laptop:before{content:"\f109"}.fa-tablet:before{content:"\f10a"}.fa-mobile-phone:before,.fa-mobile:before{content:"\f10b"}.fa-circle-o:before{content:"\f10c"}.fa-quote-left:before{content:"\f10d"}.fa-quote-right:before{content:"\f10e"}.fa-spinner:before{content:"\f110"}.fa-circle:before{content:"\f111"}.fa-mail-reply:before,.fa-reply:before{content:"\f112"}.fa-github-alt:before{content:"\f113"}.fa-folder-o:before{content:"\f114"}.fa-folder-open-o:before{content:"\f115"}.fa-smile-o:before{content:"\f118"}.fa-frown-o:before{content:"\f119"}.fa-meh-o:before{content:"\f11a"}.fa-gamepad:before{content:"\f11b"}.fa-keyboard-o:before{content:"\f11c"}.fa-flag-o:before{content:"\f11d"}.fa-flag-checkered:before{content:"\f11e"}.fa-terminal:before{content:"\f120"}.fa-code:before{content:"\f121"}.fa-reply-all:before{content:"\f122"}.fa-mail-reply-all:before{content:"\f122"}.fa-star-half-empty:before,.fa-star-half-full:before,.fa-star-half-o:before{content:"\f123"}.fa-location-arrow:before{content:"\f124"}.fa-crop:before{content:"\f125"}.fa-code-fork:before{content:"\f126"}.fa-unlink:before,.fa-chain-broken:before{content:"\f127"}.fa-question:before{content:"\f128"}.fa-info:before{content:"\f129"}.fa-exclamation:before{content:"\f12a"}.fa-superscript:before{content:"\f12b"}.fa-subscript:before{content:"\f12c"}.fa-eraser:before{content:"\f12d"}.fa-puzzle-piece:before{content:"\f12e"}.fa-microphone:before{content:"\f130"}.fa-microphone-slash:before{content:"\f131"}.fa-shield:before{content:"\f132"}.fa-calendar-o:before{content:"\f133"}.fa-fire-extinguisher:before{content:"\f134"}.fa-rocket:before{content:"\f135"}.fa-maxcdn:before{content:"\f136"}.fa-chevron-circle-left:before{content:"\f137"}.fa-chevron-circle-right:before{content:"\f138"}.fa-chevron-circle-up:before{content:"\f139"}.fa-chevron-circle-down:before{content:"\f13a"}.fa-html5:before{content:"\f13b"}.fa-css3:before{content:"\f13c"}.fa-anchor:before{content:"\f13d"}.fa-unlock-alt:before{content:"\f13e"}.fa-bullseye:before{content:"\f140"}.fa-ellipsis-h:before{content:"\f141"}.fa-ellipsis-v:before{content:"\f142"}.fa-rss-square:before{content:"\f143"}.fa-play-circle:before{content:"\f144"}.fa-ticket:before{content:"\f145"}.fa-minus-square:before{content:"\f146"}.fa-minus-square-o:before{content:"\f147"}.fa-level-up:before{content:"\f148"}.fa-level-down:before{content:"\f149"}.fa-check-square:before{content:"\f14a"}.fa-pencil-square:before{content:"\f14b"}.fa-external-link-square:before{content:"\f14c"}.fa-share-square:before{content:"\f14d"}.fa-compass:before{content:"\f14e"}.fa-toggle-down:before,.fa-caret-square-o-down:before{content:"\f150"}.fa-toggle-up:before,.fa-caret-square-o-up:before{content:"\f151"}.fa-toggle-right:before,.fa-caret-square-o-right:before{content:"\f152"}.fa-euro:before,.fa-eur:before{content:"\f153"}.fa-gbp:before{content:"\f154"}.fa-dollar:before,.fa-usd:before{content:"\f155"}.fa-rupee:before,.fa-inr:before{content:"\f156"}.fa-cny:before,.fa-rmb:before,.fa-yen:before,.fa-jpy:before{content:"\f157"}.fa-ruble:before,.fa-rouble:before,.fa-rub:before{content:"\f158"}.fa-won:before,.fa-krw:before{content:"\f159"}.fa-bitcoin:before,.fa-btc:before{content:"\f15a"}.fa-file:before{content:"\f15b"}.fa-file-text:before{content:"\f15c"}.fa-sort-alpha-asc:before{content:"\f15d"}.fa-sort-alpha-desc:before{content:"\f15e"}.fa-sort-amount-asc:before{content:"\f160"}.fa-sort-amount-desc:before{content:"\f161"}.fa-sort-numeric-asc:before{content:"\f162"}.fa-sort-numeric-desc:before{content:"\f163"}.fa-thumbs-up:before{content:"\f164"}.fa-thumbs-down:before{content:"\f165"}.fa-youtube-square:before{content:"\f166"}.fa-youtube:before{content:"\f167"}.fa-xing:before{content:"\f168"}.fa-xing-square:before{content:"\f169"}.fa-youtube-play:before{content:"\f16a"}.fa-dropbox:before{content:"\f16b"}.fa-stack-overflow:before{content:"\f16c"}.fa-instagram:before{content:"\f16d"}.fa-flickr:before{content:"\f16e"}.fa-adn:before{content:"\f170"}.fa-bitbucket:before{content:"\f171"}.fa-bitbucket-square:before{content:"\f172"}.fa-tumblr:before{content:"\f173"}.fa-tumblr-square:before{content:"\f174"}.fa-long-arrow-down:before{content:"\f175"}.fa-long-arrow-up:before{content:"\f176"}.fa-long-arrow-left:before{content:"\f177"}.fa-long-arrow-right:before{content:"\f178"}.fa-apple:before{content:"\f179"}.fa-windows:before{content:"\f17a"}.fa-android:before{content:"\f17b"}.fa-linux:before{content:"\f17c"}.fa-dribbble:before{content:"\f17d"}.fa-skype:before{content:"\f17e"}.fa-foursquare:before{content:"\f180"}.fa-trello:before{content:"\f181"}.fa-female:before{content:"\f182"}.fa-male:before{content:"\f183"}.fa-gittip:before{content:"\f184"}.fa-sun-o:before{content:"\f185"}.fa-moon-o:before{content:"\f186"}.fa-archive:before{content:"\f187"}.fa-bug:before{content:"\f188"}.fa-vk:before{content:"\f189"}.fa-weibo:before{content:"\f18a"}.fa-renren:before{content:"\f18b"}.fa-pagelines:before{content:"\f18c"}.fa-stack-exchange:before{content:"\f18d"}.fa-arrow-circle-o-right:before{content:"\f18e"}.fa-arrow-circle-o-left:before{content:"\f190"}.fa-toggle-left:before,.fa-caret-square-o-left:before{content:"\f191"}.fa-dot-circle-o:before{content:"\f192"}.fa-wheelchair:before{content:"\f193"}.fa-vimeo-square:before{content:"\f194"}.fa-turkish-lira:before,.fa-try:before{content:"\f195"}.fa-plus-square-o:before{content:"\f196"} 
</style>
</head>

<body>

  <!-- HEADER -->
  <header id="header">
    <div id="logo-group">
      <span id="logo"><img src="resources/img/raptor2-launch1.png"
        alt="Platform Upgrades as a Service"></span> <span id="navtitle"
        class="navtitle">Platform Upgrades as a Service</span>
    </div>

    <!-- pulled right: nav area -->
    <div class="pull-right hdr-login">
      <!-- logout button -->
      <div id="logout" class="btn-header transparent pull-right loggedin">
        <img src="" class="gravtar" alt="gravtar"></img> <span><label
          class="value"></label></span> <span class="btn-icon"><a
          href="login" title="Sign Out"><i class="fa fa-sign-out"></i></a></span>
      </div>
      <!-- end logout button -->
    </div>
    <!-- end pulled right: nav area -->
  </header>

  <div id="headesr" style="display: none">
    <div class="navbar transparent sharedheader" style="">
      <div class="navbar-inner">
        <div>
          <a href="https://github.com/ebay/ostara" class="brand logo"> <img
            ></a>
        </div>
        <h1 class="app-title">
          upgrade as a service <sup>BETA</sup>
        </h1>
        <div class="float-right hdr-login">
          <div>
            <span class="loggedin"> <img src="" class="gravtar"
              alt="gravtar"></img> <label class="value"></label> <a
              href='login' class="logout">Logout</a>
          </div>
          <div class="anonymous">
            <label>Login: </label> <label class="value"></label>
          </div>
        </div>
      </div>
    </div>
  </div>



  <div class="containers">


    <section>

      <!-- MAIN PANEL -->
      <div id="main" role="main">
        <div class="themebox" style="background: url(resources/img/bg.jpg)"></div>
        <div id="content">
          <div class="content-box">
            <form role="form" id="form" action="process">
              <div class="form-elements">
                <div class="col-sm-12">
                  <h3 class="centered">Upgrade your app</h3>
                  
                  <div class="row">
                  	<div class="col-sm-3">
                      <div class="form-group">
                        <div class="input-group">
                        <span title="Target Platform version" class="input-group-addon"> <i class="fa fa-fighter-jet fa-lg fa-fw"></i></span> 
                          <select
                            name="platform-version" id="platform-version"
                            class="form-control input-lg">
                            	<option value="latest">Latest</option>
                            	<option value="micro">Latest micro</option>
                          </select>

                        </div>
                      </div>
                    </div>
                  </div>

					  </div>
                  </div>
                  <div class="row">
                    <div class="col-sm-8">
                      <div class="form-group">
                        <div class="input-group">
                          <span title="Git repository URL" class="input-group-addon"><i
                            class="fa fa-github-alt fa-lg fa-fw"></i></span> <input
                            class="form-control input-lg"
                            placeholder="https://github.com/{GitOrg}/{GitRepo}"
                            type="text" name="url" id="url" required />
                        </div>
                      </div>
                    </div>
                    <div class="col-sm-4">
                      <div class="form-group">
                        <div class="input-group">
                          <span title="Git branch" class="input-group-addon"><i
                            class="fa fa-code-fork fa-lg fa-fw"></i></span> <select
                            name="git-repo-branches" id="git-repo-branches"
                            class="form-control input-lg">
                          </select>

                        </div>
                      </div>
                    </div>
                    
                    <div class="col-sm-5">
                      <div class="form-group">
                        <div class="input-group">
                          <span class="input-group-addon"><i
                            class="fa fa-file-text fa-lg fa-fw"></i></span> <input
                            class="form-control input-lg"
                            placeholder="Path To Pom.Xml, pom.xml or folder/pom.xml"
                            type="text" name="pathtopom" id="pathtopom">
                        </div>
                      </div>
                    </div>
                    <div class="col-sm-3">
                      <div class="form-group">
                        <div class="input-group">
                          <span class="input-group-addon"><i
                            class="fa fa-cogs fa-lg fa-fw"></i></span> <select name="apptype"
                            id="apptype" class="form-control input-lg">
                            <option value="">Select App Type</option>
                            <option value="auto" selected>Autodetect</option>
                            <option value="web">Web</option>
                            <option value="ginger">REST/Ginger</option>
                            <option value="bes">BES</option>
                            <option value="batch">Batch</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="col-sm-4">
                      <div class="form-group">
                        <div class="input-group">
                          <span title="Notify users" class="input-group-addon"><i
                            class="fa fa-envelope fa-lg fa-fw"></i></span> <input
                            class="form-control input-lg"
                            placeholder="username@ebay.com" type="text" name="email"
                            id="email" required>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="row">
                    <div class="col-sm-8">
                      <div class="form-group">
                        <div class="input-group">
                          <span class="input-group-addon"><i
                            class="fa fa-question-circle fa-lg fa-fw"></i></span>
                          <label class="checkbox" >
                             <input type="checkbox" class="input-sm" id="upvtolatest" name="upvtolatest">Update missing version to latest
                          </label>
                          <p><span>For any artifact with no exact version match pick the latest from ebaycentral.</span>
                        </div>
                      </div>
                     </div>
                    </div>
                    
                    <div class="row">
                    <div class="col-sm-12">
                      <button type="submit" id="btn"
                        class=" btn btn-primary btn-lg col-sm-12">Submit</button>
                    </div>
                  </div>
                  </div>
                </div>
                </form>
              </div>
              <form role="form" id="form">
	              <div class="form-status alert alert-success">
	
	                <p style="font-weight: bold; font-size: 1.2em">Your platform
	                  upgrade request was submitted and is being processed.</p>
	                <p>
	                  You will receive an email with the next steps upon the completion of the upgrade. 
	                  <span style="padding-left: 1em"><button
	                      type="button"
	                      class="btn btn-default status-close btn-lg status-close"
	                      data-dismiss="alert" id-ref=".form-status" onClick="history.go(0)" VALUE="Refresh">Close</button></span>
	                </p>
	
	              </div>
              </form>
          </div>
        </div>
      </div>
    </section>


  </div>
  
  <div class="feedback">
  <%-- Building URL - on hold for now  
	  <a href="http://jirap.corp.ebay.com/secure/CreateIssueDetails!init.jspa?pid=13960&issuetype=3&summary=Feedback from UaaS portal&assignee=renyedi&customfield_10192=18089&customfield_10193=18085&components=22206&description=Please+provide+your+feedback+here" target="_blank">feedback</a>
	  --%>
	  
	  <a href="mailto:?subject=Feedback:Ostara%20[<%= System.currentTimeMillis() %>]&to=john@doe.com&body=Please%20provide%20your%20feedback%20below:">feedback</a>
	  
	</div>
  
  <footer>
  
  <link rel="stylesheet" type="text/css" href="resources/fonts/FontAwesome.otf"/> 
  <div
    style="position:fixed;width: 100%; height: 30px; padding: 5px; bottom: 0px;">
    <h5 style="margin: 0px">
      <i>Powered by <a href="https://github.com/ebay/Ostara">Ostara</a></i>
    </h5>
  </div>

  </footer>
  
  <script src="http://code.jquery.com/jquery-1.10.1.min.js"></script>
  <script src="http://fgnass.github.io/spin.js/spin.min.js"></script>

  <script>
    (function($win) {
      $win.ostara = {};
      $win.ostara.migration = {};
    }).call(this, window);

    ostara.migration.app = {
      isLoggedIn : function() {
        var user = ostara.storage.getFromSession("user");
        return user != null;
      },
      getUser : function() {
        return ostara.storage.getFromSession("user");
      },
      getFromStorage : function(key) {

      }

    };

    ostara.migration.app.router = {
      routes : {
        "home" : "index",
        "index" : "index",
        "login" : "login",
        "current" : location.href.split("/").pop(),
        "isLogin" : function(routeName) {
          return ostara.migration.app.router.routes
              .hasOwnProperty(routeName);
        }
      }
    };

    ostara.migration.app.start = function() {
      var app = ostara.migration.app;
      if (app.isLoggedIn()) {
        $('.hdr-login').attr("user", "123");
        if (app.getUser()["_fullName"] != null) {
          $('.loggedin .value').html(app.getUser()["_fullName"]);
        } else {
          $('.loggedin .value').html(app.getUser()["_userName"]);
        }

        $('.gravtar').attr("src", app.getUser()["_gravatar"]);

        var userEmail = app.getUser()["_email"];

        if (typeof userEmail == "undefined" || userEmail == null
            || userEmail == "") {
          userEmail = app.getUser()["_userName"] + "@ebay.com";
        }
        $('#email').val(userEmail);

      } else {
        history.pushState(null, null, "/rmigweb/login");
        var req = new XMLHttpRequest();
        req.open("GET", "/rmigweb/login", false);
        req.send(null);

        if (req.status == 200) {
          document.body.innerHTML = req.responseText;
        }
      }

    };
    ostara.storage = {
      getFromSession : function(key) {
        var sessionValue = window.sessionStorage.getItem(key);
        if (sessionValue != "undefined") {
          return JSON.parse(sessionValue);
        }
      },
      set : function(key, value, isSession) {
        if (isSession) {
          window.sessionStorage.setItem(key, JSON.stringify(value));
        }
      },
      session : function(key, value) {

      },
      clearSession : function(key, clearAll) {

      },
      clear : function(key, clearAll) {

      }
    };

    ostara.migration.app.start();

    /***
     * rmig is javascript singleton that take care of javascript maneuvers on the page
     * 
     */
    var rmig = {
      url : '',
      taskId : '',
      email : '',
      spinner : '',
      isValid : false,
      baseGitUrl : "https://github.corp.ebay.com",
      init : function() {
        $('.status-close').on("click", function() {
          var $target = $($(this).attr("id-ref"));
          $('.form-elements').slideDown('fast');
          if ($target) {
            $('.form-status').hide();
          }
          rmig.reset();
        });
        $('#form').on("submit", function(e) {
          rmig.handleSubmit(e);
          e.preventDefault();
          return false;

        });
        $('#btn').on('click', this.handleSubmit.bind(this));

        //$('#url').on('change', this.handleChange.bind(this));
        //$('#overrd').on('click', this.disableDerieved);
        $('#result').html('');
        $('.status').hide();
        $('#goback').on('click', this.goBack.bind(this));
        var gitUrlSchemes = [];
        var repositoryName = null;
        var repositoryOrg = null;

        document.getElementById("url").addEventListener(
            "input",
            function(e) {
              gitUrlSchemes = this.value.split("/");

              if (gitUrlSchemes.length == 5) {
                if (!rmig.isValidGitUrl(this.value)) {
                  rmig.setStatus(false);
                  return;
                }
                repositoryName = gitUrlSchemes.pop();
                repositoryOrg = gitUrlSchemes.pop();
                rmig.gitBranchesListOf(repositoryOrg,
                    repositoryName);
              } else if (gitUrlSchemes.length > 5) {
                rmig.setStatus(false);
                return;
              }
              
              rmig.checkValidPathToPom(repositoryName, repositoryOrg, $('#pathtopom').val());
            });
        document.getElementById("url").addEventListener("change",
            function(e) {
              rmig.checkValidGitUrl(this.value);
            });
        
        document.getElementById("pathtopom").addEventListener("input",
	        function(e) {
	        	rmig.checkValidPathToPom(repositoryName, repositoryOrg, this.value);
	        });
        
        document.getElementById("git-repo-branches").addEventListener("change",
         function(e) {
        	console.log('git repo change: ' + e);
        	rmig.checkValidPathToPom(repositoryName, repositoryOrg, $('#pathtopom').val());
         });
      },

      goBack : function(e) {
        e.preventDefault();
        //e.stopPropogation();
        $('#form').fadeIn(400);
        $('.status').fadeOut(400);
        $('#result').html('');
        $('input[type="text"]').val('');

      },
      isValidGitUrl : function(urlValue) {
        if (typeof urlValue === "undefined" || urlValue === null) {
          return false;
        } else {
          return urlValue.trim().indexOf(this.baseGitUrl) == 0;
        }
      },
      checkValidGitUrl : function(url) {
        if (!this.isValidGitUrl(url)) {
          this.setStatus(false);
          this.clearBranches();
        }
      },
      checkValidPathToPom: function(repositoryName, repositoryOrg, path) {
    	  console.log('pathtopom input' + repositoryName + ", " + repositoryOrg);
          
        var url = "<c:out value="${OSTARA_SERVICE_URL}"/>" + "git/" + repositoryOrg + "/" + repositoryName + "/contents?path=" + encodeURI(path) + "&branch=" + $('#git-repo-branches').val();
        $.get(url).done(function(cbResult) {
          console.log("result=" + cbResult);
          if (typeof cbResult === "undefined" || cbResult === null || cbResult === "") {
              rmig.setStatus(false);
          } else {
            rmig.setStatus(true);
          }
        });
      },
      
      // Collects all the branches across several github callbacks, displays the final list in the UI
      fetchBranches : function(branchLooper, gitOrg, gitRepository, branches) {
    	  if(typeof(branches)==='undefined') branches = [];
    	  
    	  var url = "<c:out value="${OSTARA_SERVICE_URL}"/>" + "git/" + gitOrg + "/" + gitRepository + "/branches?page=" + branchLooper + "&per_page=100";
	        
	      console.log("url for page " + branchLooper + "=" + url)
    	  
    	  $.get(url).done(function(branchList) {
	          if (branchList !== null && branchList != "") {
	        	if (typeof branchList === "undefined" || branchList === null || branchList === "") {
       	          return;
       	        }
       	        
       	        if (branchList === "") {
       	          return;
       	        }
       	        var newBranches = JSON.parse(branchList);
	            
	            if(newBranches.length > 0) {
	            	megaNewBranches = branches.concat(newBranches);
	            	newLooper = branchLooper + 1;
	            	rmig.fetchBranches(newLooper, gitOrg, gitRepository, megaNewBranches); // There could be one or more pages available
	            } else {
	            	// Display all branches
	            	rmig.populateBranches(branches);
		            rmig.checkValidPathToPom(gitRepository, gitOrg, $('#pathtopom').val());
	            }
	          } else {
	            rmig.setStatus(false);
	          }
	        });  
      },
      gitBranchesListOf : function(gitOrg, gitRepository) {
        "use strict";
        if (typeof gitOrg === "undefied"
            || typeof gitRepository === "undefined") {
          return;
        } else if (gitOrg === null || gitRepository === null) {
          return;
        }

	    this.fetchBranches(1, gitOrg, gitRepository);
      },
      clearBranches : function() {
        $('#git-repo-branches').html();
      },
      populateBranches : function(branches) {
        "use strict";
        var isSelected = "";
        var branchOptions = [];
        for (var i = 0; i < branches.length; i++) {
          if (branches[i].name === "master") {
            isSelected = "selected";
          }
          branchOptions
              .push("<option value='" + branches[i].name + "'" + isSelected + ">"
                  + branches[i].name + "</option>");
          isSelected = "";
        }
        $('#git-repo-branches').html(branchOptions.join(""));
        this.setStatus(true);
      },
      reset : function() {
        this.isValid = false;
        $('form').removeAttr("data-isvalid");
        document.getElementById("form").reset();
      },
      setStatus : function(isValid) {
        this.isValid = isValid;
        $('form').attr("data-isvalid", isValid);
      },

      handleSubmit : function(e) {
        console.log(this.isValid);
        if (!this.isValid) {
          return false;
        }
        var that = this;
        this.url = encodeURI($('#url').val());
        this.platformVersion = $('#platform-version').val();
        this.email = encodeURI($('#email').val());
        this.apptype = $('#apptype').find(":selected").val();
        this.updToLtst = $('#upvtolatest').is(':checked') ? 'checked': ''; 
        this.branch = $('#git-repo-branches').val();
        this.pathtopom = $('#pathtopom').val();
        this.branch = this.branch ? this.branch : 'master';
        that['pullrequest'] = null;
        $.get(
            'init?gitrepo=' + this.url + '&platformVersion=' + this.platformVersion + '&email=' + this.email
                + '&apptype=' + this.apptype + '&branch='
                + this.branch+ '&pathtopom=' + this.pathtopom
                + '&user='
                + ostara.migration.app.getUser()["_userName"] 
                + '&upvtolatest=' 
                + this.updToLtst,
            function(data) {
              $('#spinner').show();
              $('.form-elements').slideUp();
              $('.form-status').show();
              return false;
            });
        return false;

      },

      receiveUpdates : function(taskId, ip, pullRequestId) {
        //receiving the ip from the server, to resolve the log file being on a particular machine issue
        var that = this;
        $
            .ajax({
              url : "http://" + ip + ":8080/rmigweb/"
                  + "getTaskInfo?taskId=" + taskId,
              type : "GET",
              crossDomain : true,
              success : function(data) {
                if (data
                    .indexOf("You will receive an email with next steps") != -1) {
                  window.clearInterval(pullRequestId);
                  that.spinner.stop();
                  $('#spinner').hide();
                  //$('#spinner').spin(false); 
                }

                var processResult = data
                    .join("")
                    .replace("FAILED", "")
                    .replace(
                        "Output: https://github.com/ebay/Ostara",
                        "");
                ;
                $('#result').html(
                    '<p>' + processResult + '</p>');

              },
              error : function(data) {
                var processResult = data
                    .join("")
                    .replace("FAILED", "")
                    .replace(
                        "Output: https://github.com/ebay/Ostara",
                        "");
                ;
                $('#result').html(
                    '<p>' + processResult + '</p>');
                that.spinner.stop();
                $('#spinner').hide();
                //$('#spinner').spin(false);
              }

            });

        /* 	$.ajax({
                  url: "http://localhost:8079/students/add/",
                  type: "POST",
                  crossDomain: true,
                  data: JSON.stringify(somejson),
                  dataType: "json",
                  success: function (response) {
                      var resp = JSON.parse(response)
                      alert(resp.status);
                  },
                  error: function (xhr, status) {
                      alert("error");
                  }
              });
         */

      }

    };

    $(function() {
      rmig.init();
    });

    $('#login').on(
        "submit",
        function(e) {
          $.post("authenticate", {
            "username" : $('#username').val(),
            "password" : $('#password').val()
          }).done(
              function(cbResult) {
                if (cbResult.usertoken != "undefined"
                    && cbResult != null) {
                  if (cbResult.error != "undefined"
                      && cbResult.error != null) {
                    $('#login').attr("data-error",
                        "unauthorized");
                  } else {
                    ostara.storage.set("user", cbResult,
                        true);
                    $('.userName').html("testuser");
                    location.href = "index.jsp";
                  }

                }
              });

          e.preventDefault();
          return false;
        });
  </script>

</body>
</html>
