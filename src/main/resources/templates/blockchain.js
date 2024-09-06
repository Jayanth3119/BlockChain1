document.addEventListener("DOMContentLoaded", function () {
    loadBlockchain();

    document.getElementById("mine-form").addEventListener("submit", function (event) {
        event.preventDefault();
        const transactions = document.getElementById("transactions").value;
        mineBlock(transactions);
    });

    document.getElementById("transaction-form").addEventListener("submit", function (event) {
        event.preventDefault();
        const hash = document.getElementById("hash").value;
        getTransactionStatus(hash);
    });
});

function loadBlockchain() {
    fetch("/api/blockchain")
        .then(response => response.json())
        .then(data => {
            const blockList = document.getElementById("block-list");
            blockList.innerHTML = "";
            data.forEach(block => {
                const listItem = document.createElement("li");
                listItem.textContent = `Block #${block.index} - Hash: ${block.hash}`;
                blockList.appendChild(listItem);
            });
        })
        .catch(error => console.error("Error loading blockchain:", error));
}

function mineBlock(transactions) {
    fetch("/api/blockchain/mine", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: transactions
    })
        .then(response => response.json())
        .then(data => {
            alert(`Block #${data.index} mined with hash: ${data.hash}`);
            loadBlockchain();
        })
        .catch(error => console.error("Error mining block:", error));
}

function getTransactionStatus(hash) {
    fetch(`/api/blockchain/transaction/${hash}`)
        .then(response => response.json())
        .then(data => {
            const statusMessage = document.getElementById("transaction-status-message");
            statusMessage.textContent = `Transaction found: ${data.sender} sent ${data.amount} to ${data.recipient}`;
        })
        .catch(error => {
            document.getElementById("transaction-status-message").textContent = "Transaction not found!";
            console.error("Error fetching transaction:", error);
        });
}
