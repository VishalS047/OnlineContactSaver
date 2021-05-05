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
