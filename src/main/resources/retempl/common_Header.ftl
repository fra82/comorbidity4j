<!DOCTYPE html>
<html>

<#setting number_format="computer">

<head>

  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  
  <title>Comorbidity analysis ${execID!'---'} (${execDate!'N/A'})</title>
  <!-- Jquery and JqueryUI -->
  <script src="${baseCSS_JSpath!''}js/jquery-3.2.1.min.js?v=${CSSJScount!'---'}"></script>
  <script src="${baseCSS_JSpath!''}js/jquery-ui.min.js?v=${CSSJScount!'---'}"></script>
  <link rel="stylesheet" href="${baseCSS_JSpath!''}css/jquery-ui.min.css?v=${CSSJScount!'---'}" type="text/css" />
  
  <!-- Slider -->
  <script src="${baseCSS_JSpath!''}js/jquery-ui-slider-pips.js?v=${CSSJScount!'---'}"></script>
  <link rel="stylesheet" href="${baseCSS_JSpath!''}css/jquery-ui-slider-pips.css?v=${CSSJScount!'---'}" type="text/css" />
  
  <!-- Plotly.js -->
  <script src="${baseCSS_JSpath!''}js/plotly-latest.min.js?v=${CSSJScount!'---'}"></script>
  
  <!-- Vis.js -->
  <script src="${baseCSS_JSpath!''}js/vis.min.js?v=${CSSJScount!'---'}"></script>
  <link rel="stylesheet" href="${baseCSS_JSpath!''}css/vis.min.css?v=${CSSJScount!'---'}" type="text/css" />
    
  <!-- Tabulator.js -->
  <link href="${baseCSS_JSpath!''}css/tabulator.min.css?v=${CSSJScount!'---'}" rel="stylesheet">
  <script type="text/javascript" src="${baseCSS_JSpath!''}js/tabulator.min.js?v=${CSSJScount!'---'}"></script>
  
  <!-- Navigation bar -->
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
  <link href='https://fonts.googleapis.com/css?family=Roboto:400,700' rel='stylesheet'>
  <link href="${baseCSS_JSpath!''}css/styleNav.css?v=${CSSJScount!'---'}" rel="stylesheet">
  <script type="text/javascript" src="${baseCSS_JSpath!''}js/indexNav.js?v=${CSSJScount!'---'}"></script>
  
  <!-- DIAGNOSIS GROUP SELECTION - START -->
  <!-- Selectize.js - https://github.com/selectize/selectize.js -->
  <link href="${baseCSS_JSpath!''}css/selectize.css?v=${CSSJScount!'---'}" rel="stylesheet">
  <script type="text/javascript" src="${baseCSS_JSpath!''}js/selectize.min.js?v=${CSSJScount!'---'}"></script>
  
  <!-- custom -->
  <link rel="stylesheet" href="${baseCSS_JSpath!''}css/main.css?v=${CSSJScount!'---'}" type="text/css" />
  
</head>


<body>
