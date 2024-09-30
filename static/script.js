const Y_ALLOWED_CHARACTERS_REGEXP = /[0-9]+/;
const Y_FIELD = document.getElementById("Y");

const form = document.getElementById("dataform");
form.addEventListener('submit', submitForm);

const result_table = document.getElementById("result_table");

Y_FIELD.addEventListener("keypress", event => {
	if (!Y_ALLOWED_CHARACTERS_REGEXP.test(event.key)) {
		event.preventDefault();
	}
});

Y_FIELD.addEventListener("paste", event => {
if (!Y_ALLOWED_CHARACTERS_REGEXP.test(event.clipboardData.getData('text'))) {
	event.preventDefault();
	}
});

 async function submitForm(event) {

 	event.preventDefault();

	let x_result = document.getElementById("X").options[document.getElementById("X").selectedIndex].text;
	let y_result = document.getElementById("Y").value;
	let r_result = document.getElementById("R").options[document.getElementById("R").selectedIndex].text;

	if (x_result === "" || y_result === "" || r_result === "") {
		alert("Все поля должны быть заполнены! Пожалуйста, повторите ввод.");
	} else {
		let params = new URLSearchParams();
		params.append("X", x_result);
		params.append("Y", y_result);
		params.append("R", r_result);
		let response = await fetch(`/fcgi-bin/hello-world.jar?${params.toString()}`);
		let result_data = await response.json();
		let newRow = result_table.insertRow();
		let resultCell = newRow.insertCell();
		let c2 = newRow.insertCell();
		let c3 = newRow.insertCell();
		let c4 = newRow.insertCell();
		let c5 = newRow.insertCell();
		let c1text = document.createTextNode(result_data.result.text);
		let c2text = document.createTextNode(x_result);
		let c3text = document.createTextNode(y_result);
		let c4text = document.createTextNode(r_result);
		let c5text = document.createTextNode(result_data.time.text);
	}

}