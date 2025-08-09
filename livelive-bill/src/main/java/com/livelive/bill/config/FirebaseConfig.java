package com.livelive.bill.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            // 환경변수에서 Firebase JSON 내용 읽기
            String firebaseJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
            if (firebaseJson == null || firebaseJson.isEmpty()) {
                throw new IllegalStateException("FIREBASE_SERVICE_ACCOUNT_JSON 환경변수가 설정되어 있지 않습니다.");
            }

            // 임시 파일에 JSON 내용 기록
            Path tempFile = Files.createTempFile("firebase-service-account", ".json");
            Files.writeString(tempFile, firebaseJson, StandardOpenOption.WRITE);

            // Firebase 초기화
            FileInputStream serviceAccount = new FileInputStream(tempFile.toFile());

//            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://livelive-bill-default-rtdb.firebaseio.com") // 꼭 수정!!
                    .build();

            FirebaseApp app = null;
            try {
                app = FirebaseApp.getInstance();
            } catch (IllegalStateException e) {
                // 이미 초기화 되어있으면 무시
            }
            if (app == null) {
                app = FirebaseApp.initializeApp(options);
            }

            // 임시 파일 삭제 (선택사항)
            tempFile.toFile().deleteOnExit();
        }

        return FirebaseDatabase.getInstance();
    }

    @Bean(name = "userRef")
    public DatabaseReference userReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference("users");
    }

    @Bean(name = "expenseRef")
    public DatabaseReference expenseReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference("expenses");
    }

//    @PostConstruct
//    public void init() throws Exception {
//        if (FirebaseApp.getApps().isEmpty()) {
//            try {
//                FileInputStream serviceAccount =
//                        new FileInputStream("src/main/resources/firebase-service-account.json");
////                    InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");
//
//                FirebaseOptions options = FirebaseOptions.builder()
//                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                        .setDatabaseUrl("https://livelive-bill-default-rtdb.firebaseio.com")
//                        .build();
//
//                FirebaseApp.initializeApp(options);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}