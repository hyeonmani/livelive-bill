import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getStorage } from "firebase/storage";
import { getFirestore } from "firebase/firestore";

const firebaseConfig = {
  apiKey: "AIzaSyBy4aBEuHQ0sudR-zfifWgNHk0ZMWD_T54",
  authDomain: "livelive-bill.firebaseapp.com",
  databaseURL: "https://livelive-bill-default-rtdb.firebaseio.com",
  projectId: "livelive-bill",
  storageBucket: "livelive-bill.firebasestorage.app",
  messagingSenderId: "304274589421",
  appId: "1:304274589421:web:5a70d6d710ec2e1467eee4"
};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);       // for 인증
export const storage = getStorage(app); // for 스토리지
export const db = getFirestore(app);    // for 데이터베이스

