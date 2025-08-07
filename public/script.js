const firebaseConfig = {
  apiKey: "AIzaSyBy4aBEuHQ0sudR-zfifWgNHk0ZMWD_T54",
  authDomain: "livelive-bill.firebaseapp.com",
  projectId: "livelive",
};
firebase.initializeApp(firebaseConfig);

// let url = "https://your-backend-url.onrender.com"
let url = "http://localhost:8080";

// init Setting
const path = window.location.pathname;

if (path.endsWith("admin.html")) {
  document.getElementById("expenseDate").value = new Date()
    .toISOString()
    .substring(0, 10);
  loadUserDropdown();
} else {
  document.getElementById("baseDate").value = new Date()
    .toISOString()
    .substring(0, 10);

  selectData();
}

// init Setting

async function signIn() {
  const provider = new firebase.auth.GoogleAuthProvider();
  const result = await firebase.auth().signInWithPopup(provider);
  const idToken = await result.user.getIdToken();

  // 토큰을 백엔드로 전송해서 관리자 확인
  const response = await fetch("/api/checkAdmin", {
    method: "POST",
    headers: {
      Authorization: `Bearer ${idToken}`,
    },
  });

  const resultJson = await response.json();

  if (resultJson.admin) {
    window.location.href = "/admin.html";
  } else {
    alert("누구세요?");
    firebase.auth().signOut();
  }
}

async function selectData() {
  const baseDate = document.getElementById("baseDate").value;
  const mode = document.querySelector('input[name="mode"]:checked').value;
  if (!baseDate) {
    alert("기준일 선택");
    return;
  }
  const res = await fetch(
    `${url}/api/expenses/selectData?baseDate=${baseDate}&mode=${mode}`
  );
  const arr = await res.json();
  const tbody = document.getElementById("tbody");
  tbody.innerHTML = "";
  arr.forEach((e) => {
    const tr = document.createElement("tr");
    const delta = e.previous === 0 ? "-" : e.delta;
    const arrow = delta > 0 ? "↑" : delta < 0 ? "↓" : "";
    const color = delta > 0 ? "red" : delta < 0 ? "blue" : "black";
    tr.innerHTML = `<td>${e.user}</td>
      <td>${e.current}</td>
      <td style="color:${color}">${arrow} (${
      delta > 0 ? "+" : ""
    }${delta})</td>`;
    tbody.appendChild(tr);
  });
}

async function addUser() {
  const name = document.getElementById("userName").value.trim();
  if (!name) return alert("이름을 입력하세요.");

  await fetch(`${url}/api/users`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + idToken,
    },
    body: JSON.stringify({ name }),
  });

  document.getElementById("userName").value = "";
  loadUsers();
}

async function loadUsers() {
  const res = await fetch(`${url}`);
  const data = await res.json();
  const list = document.getElementById("userList");
  list.innerHTML = "";
  data.forEach((user) => {
    const li = document.createElement("li");
    li.textContent = user.name;
    const delBtn = document.createElement("button");
    delBtn.textContent = "삭제";
    delBtn.onclick = async () => {
      await fetch(`${url}` + user.id, {
        method: "DELETE",
        headers: { Authorization: "Bearer " + idToken },
      });
      loadUsers();
      loadUserDropdown();
    };
    li.appendChild(delBtn);
    list.appendChild(li);
  });
}

async function loadUserDropdown() {
  const res = await fetch(`${url}/api/users`);
  const data = await res.json();
  const select = document.getElementById("userSelect");
  select.innerHTML = "";
  data.forEach((user) => {
    const option = document.createElement("option");
    option.value = user.name;
    option.textContent = user.name;
    select.appendChild(option);
  });
}

async function addExpense() {
  const date = document.getElementById("expenseDate").value;
  const user = document.getElementById("userSelect").value;
  const amount = parseInt(document.getElementById("expenseAmount").value);
  if (!date || !user || isNaN(amount)) return alert("모두 입력해주세요.");

  await fetch(`${url}/api/expenses`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + idToken,
    },
    body: JSON.stringify({ user, date, amount }),
  });

  alert("등록되었습니다.");
  document.getElementById("expenseAmount").value = "";
}
