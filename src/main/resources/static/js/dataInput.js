$(document).ready(function() {

	// Enable expandable div
	$(".headerExpander").click(function () {
	    $header = $(this);
	    $content = $header.next();
	    $content.slideToggle(500, function () {
	        $header.text(function () {
	            return $content.is(":visible") ? "Click to collapse info" : "Click for more info";
	        });
	    });
	});
	$(".headerExpander").click();
	

});

