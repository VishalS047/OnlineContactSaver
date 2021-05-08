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

const search = () => {
	// console.log("Yes chl rha h")

	let query = $("#search-input").val();

	if (query == '') {
		$(".search-result").hide();
	} else {
		console.log(query);

		let url = `http://localhost:9047/search/${query}`;

		fetch(url).then((response) => {
			return response.json();
		}).then((data) => {

			console.log(data)
			let text = `<div class='list-group'>`
			data.forEach((contact) => {
				text+= `<a href='/user/contact/${contact.contact_Id}' class='list-group-item item-group-item-action> ${contact.contact_Name} </a>`;
			});
			text+=`</div>`

			$(".search-result").html(text);

			$(".search-result").show();
		});

		
	}
}


function validate(evt) {
  var theEvent = evt || window.event;

  // Handle paste
  if (theEvent.type === 'paste') {
      key = event.clipboardData.getData('text/plain');
  } else {
  // Handle key press
      var key = theEvent.keyCode || theEvent.which;
      key = String.fromCharCode(key);
  }
  var regex = /[0-9]|\./;
  if( !regex.test(key) ) {
    theEvent.returnValue = false;
    if(theEvent.preventDefault) theEvent.preventDefault();
  }
}
