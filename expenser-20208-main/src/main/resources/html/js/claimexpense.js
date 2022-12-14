function updatePage(id, email, date, amount) {
    let section = document.getElementById("claims_section");
    if (section.style.display === "none") {
        section.style.display = "block";
    }

    console.log(email);
    let name = email.charAt(0).toUpperCase() + email.split("@")[0].slice(1);
    name = name.substring(1);
    name = name.charAt(0).toUpperCase() + name.slice(1);

    let tableBody = document.getElementById("claims").getElementsByTagName("tbody")[0];
    let newRow = tableBody.insertRow();

    let fromCell = newRow.insertCell();
    fromCell.classList.add("text");
    fromCell.setAttribute("id", "claim_who_" + id)
    fromCell.appendChild(document.createTextNode(name));

    let dateCell = newRow.insertCell();
    dateCell.classList.add("date");
    dateCell.setAttribute("id", "claim_date_" + id)
    dateCell.appendChild(document.createTextNode(date));

    let settleCell = newRow.insertCell();
    settleCell.classList.add("text");
    settleCell.setAttribute("id", "claim_settled_" + id)
    settleCell.appendChild(document.createTextNode("No"));

    let amountCell = newRow.insertCell();
    amountCell.classList.add("money");
    amountCell.setAttribute("id", "claim_amount_" + id)
    amountCell.appendChild(document.createTextNode(amount.toFixed(2)));

    let totalClaimsElement = document.getElementById("total_claims");
    totalClaimsElement.innerText = (parseFloat(totalClaimsElement.innerText) + amount).toFixed(2);

    let unclaimedAmountElement = document.getElementById("unclaimed_amount");
    unclaimedAmountElement.innerText = (parseFloat(unclaimedAmountElement.innerText) - amount).toFixed(2);
}

claim_form.onsubmit = async (e) => {
  e.preventDefault();

  let claim = {};
  let fd = new FormData(claim_form);
  fd.forEach( (v,k) => {claim[k] = v;});
  console.log(JSON.stringify(claim));

  const options = {
    method: 'POST',
    body: JSON.stringify(claim),
    headers: { 'Content-Type': 'application/json' }
  }

   fetch('/api/claims', options)
     .then(res => res.json())
     .then(res => updatePage(res.id, res.claimFromWho, res.dueDate, res.claimAmount))
     .catch(err => console.error(err));
};
