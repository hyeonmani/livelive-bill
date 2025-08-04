const firebaseConfig = {
  apiKey: "AIzaSyBy4aBEuHQ0sudR-zfifWgNHk0ZMWD_T54",
  authDomain: "livelive-bill.firebaseapp.com",
  projectId: "livelive"
};
firebase.initializeApp(firebaseConfig);

let idToken = "";
let ADMIN_UID = "oixA3QuL3SN0UoMH7Bo7NG5icTi1"; // 서버에도 동일하게 맞춰야 함

async function signIn() {
  const provider = new firebase.auth.GoogleAuthProvider();
  const result = await firebase.auth().signInWithPopup(provider);
  idToken = await result.user.getIdToken();
  const uid = result.user.uid;
  alert("로그인 성공: " + uid);

  if (uid === ADMIN_UID) {
    document.getElementById("user-management").style.display = "block";
    loadUsers();
  }
  loadUserDropdown();
}

// 사용자 관리
async function addUser() {
  const name = document.getElementById("new-username").value;
  await fetch("https://your-backend-url.onrender.com/api/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + idToken
    },
    body: JSON.stringify({ name })
  });
  document.getElementById("new-username").value = "";
  loadUsers();
  loadUserDropdown();
}

async function loadUsers() {
  const res = await fetch("https://your-backend-url.onrender.com/api/users");
  const data = await res.json();
  const list = document.getElementById("user-list");
  list.innerHTML = "";
  data.forEach(user => {
    const li = document.createElement("li");
    li.textContent = user.name;
    const delBtn = document.createElement("button");
    delBtn.textContent = "삭제";
    delBtn.onclick = async () => {
      await fetch("https://your-backend-url.onrender.com/api/users/" + user.id, {
        method: "DELETE",
        headers: { "Authorization": "Bearer " + idToken }
      });
      loadUsers();
      loadUserDropdown();
    };
    li.appendChild(delBtn);
    list.appendChild(li);
  });
}

async function loadUserDropdown() {
  const res = await fetch("https://your-backend-url.onrender.com/api/users");
  const data = await res.json();
  const select = document.getElementById("user-select");
  select.innerHTML = "";
  data.forEach(user => {
    const option = document.createElement("option");
    option.value = user.name;
    option.textContent = user.name;
    select.appendChild(option);
  });
}

// 금액 저장
async function addExpense() {
  const user = document.getElementById("user-select").value;
  const date = document.getElementById("base-date").value;
  const amount = parseInt(document.getElementById("amount-input").value);
  await fetch("https://your-backend-url.onrender.com/api/expenses", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + idToken
    },
    body: JSON.stringify({ user, date, amount })
  });
  alert("저장 완료");
  document.getElementById("amount-input").value = "";
}

// 주간 조회
function getWeekRange(baseDateStr) {
  const baseDate = new Date(baseDateStr);
  const day = baseDate.getDay();
  const diffToMonday = (day === 0 ? -6 : 1 - day);
  const monday = new Date(baseDate);
  monday.setDate(baseDate.getDate() + diffToMonday);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  const format = (d) => d.toISOString().split('T')[0];
  return [format(monday), format(sunday)];
}

async function fetchExpensesByWeek() {
  const baseDate = document.getElementById('view-base-date').value;
  if (!baseDate) return alert("기준일을 선택하세요!");
  const [startDate, endDate] = getWeekRange(baseDate);
  const res = await fetch(`https://your-backend-url.onrender.com/api/expenses/range?startDate=${startDate}&endDate=${endDate}`);
  const data = await res.json();
  const tbody = document.querySelector('#expense-table tbody');
  tbody.innerHTML = '';
  data.forEach(e => {
    tbody.innerHTML += `<tr><td>${e.user}</td><td>${e.date}</td><td>${e.amount}</td></tr>`;
  });
}
