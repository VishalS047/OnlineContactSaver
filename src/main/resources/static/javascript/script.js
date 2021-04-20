console.log("Just launced");
const toggleSideBar = () => {
	if ($('.sidebar').is(':visible')) {

		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
		/*console.log("Hello");*/
	} else {
		/*console.log("Hello");	*/
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

