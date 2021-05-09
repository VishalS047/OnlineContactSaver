console.log("Just launced");
const toggleSideBar = () => {
  if ($(".sidebar").is(":visible")) {
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

  if (query == "") {
    $(".search-result").hide();
  } else {
    console.log(query);

    let url = `http://localhost:9047/search/${query}`;

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        let text = `<div class='list-group'>`;
        data.forEach((contact) => {
          text += `<a href='/user/contact/${contact.contact_Id}' class='list-group-item item-group-item-action> ${contact.contact_Name} </a>`;
        });
        text += `</div>`;

        $(".search-result").html(text);

        $(".search-result").show();
      });
  }
};

function validate(evt) {
  var theEvent = evt || window.event;

  // Handle paste
  if (theEvent.type === "paste") {
    key = event.clipboardData.getData("text/plain");
  } else {
    // Handle key press
    var key = theEvent.keyCode || theEvent.which;
    key = String.fromCharCode(key);
  }
  var regex = /[0-9]|\./;
  if (!regex.test(key)) {
    theEvent.returnValue = false;
    if (theEvent.preventDefault) theEvent.preventDefault();
  }
}

//first request to create order
const paymentStart = () => {
  let amount = $("#payment_field").val();
  if (amount == "" || amount == null) {
    /*alert("Enter some amount!!")*/
    swal("Failed!", "Amount is required!", "error");
    return;
  }

  // using ajax to send request to server to create order

  $.ajax({
    url: "/user/create_order",
    data: JSON.stringify({ amount: amount, info: "order-request" }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
    success: function (response) {
      // invoked when success
      console.log(response);

      if (response.status == "created") {
        /*open payment form*/
        var options = {
          key: "rzp_test_VxtfatH6wU2Kx4",
          amount: response.amount,
          currency: "INR",
          name: "ONLINE CONTACT BOOK",
          description: "Help us grow❤",
          image:
            "https://media-exp1.licdn.com/dms/image/C4E03AQGPVQSjiStV-w/profile-displayphoto-shrink_200_200/0/1614798457352?e=1626307200&v=beta&t=L0u8jz7HCrYZfzWz3cBwiP33yJzPKhoKS0CXt9hAgj0",
          order_id: response.id,
          handler: function (response) {
            console.log(response.razorpay_payment_id);
            console.log(response.razorpay_order_id);
            console.log(response.razorpay_signature);
            console.log("payment successful");
            /*alert("Congrats!! Payment is successfull!!")*/
            updatePaymentOnServer(
              response.razorpay_payment_id,
              response.razorpay_order_id,
              "paid"
            );

            swal("Good job!", "Congrats!! Payment is successfull!", "success");
          },
          prefill: {
            name: "",
            email: "",
            contact: "",
          },
          notes: {
            address: "Online Contact Manager",
          },
          theme: {
            color: "#3399cc",
          },
        };

        let razorPay = new Razorpay(options);

        razorPay.open();

        razorPay.on("payment.failed", function (response) {
          console.log(response.error.code);
          console.log(response.error.description);
          console.log(response.error.source);
          console.log(response.error.step);
          console.log(response.error.reason);
          console.log(response.error.metadata.order_id);
          console.log(response.error.metadata.payment_id);
          /*alert("payment failed");*/
          swal("Failed!", "Oops payment failed!", "error");
        });
      }
    },
    error: function (error) {
      console.log(error);
      alert("Something went wrong!!");
    },
    //invoked when error
  });
};

function updatePaymentOnServer(payment_Id, order_Id, status)
{
  $.ajax({
    url: "/user/update_order",
    data: JSON.stringify({
      payment_Id: payment_Id,
      order_id: order_Id,
      status: status,
    }),
    contentType: "application/json",
    type: "POST",
    dataType: "json",
	success: function(response) {
		swal("Good job!", "Congrats!! Payment is successfull!", "success");
	},
	error: function(error) {
		swal("Failed!", "Your payment is successful, but we couldn't capture it due to a glich we are working on it!!", "error");
	}
  });
}
