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

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseDatabase firebaseDatabase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://livelive-bill-default-rtdb.firebaseio.com") // 꼭 수정!!
                    .build();

            FirebaseApp.initializeApp(options);
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