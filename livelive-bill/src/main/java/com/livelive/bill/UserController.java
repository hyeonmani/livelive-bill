package com.livelive.bill;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DatabaseReference userRef;

    public UserController(@Qualifier("userRef") DatabaseReference userRef) {
        this.userRef = userRef;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() throws Exception {
        DatabaseReference ref = userRef;

        final List<User> userList = new ArrayList<>();

        final CountDownLatch latch = new CountDownLatch(1);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    userList.add(user);
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        try {
            latch.await(); // Firebase는 비동기라 이걸로 동기 대기
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(userList);

    }

    @PostMapping
    public void addUser(@RequestBody Map<String, Object> userData) {

        try {
            String userId = userRef.push().getKey();
            userRef.child(userId).setValueAsync(userData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/{userId}")
    public void updateUser(@PathVariable String userId, @RequestBody Map<String, Object> userData) {
        userRef.child(userId).updateChildrenAsync(userData);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userRef.child(userId).removeValueAsync();
    }

//    @GetMapping
//    public List<User> getUsers() {
//        List<User> users = new ArrayList<>();
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    User user = child.getValue(User.class);
//                    if(user != null) users.add(user);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });
//             try {
//                 Thread.sleep(1000);
//             } catch (InterruptedException ignored) {
//
//             }
//
//        return users;
//    }
//
//    @PostMapping
//    public String addUser(@RequestBody User user) {
//        FirebaseToken token = FirebaseAuthFilter.currentUser.get();
//        if(token == null || !token.getUid().equals("oixA3QuL3SN0UoMH7Bo7NG5icTi1")) return "나디만 가능 하다내요.";
//
//        String id = userRef.push().getKey();
//        user.setId(id);
//        userRef.child(id).setValueAsync(user);
//        return "사용자 추가 완";
//    }
//
//    @DeleteMapping("/{id}")
//    public String deleteUser(@PathVariable String id) {
//        FirebaseToken token = FirebaseAuthFilter.currentUser.get();
//        if(token == null || !token.getUid().equals("oixA3QuL3SN0UoMH7Bo7NG5icTi1")) return "나디만 가능 하다내요.";
//
//        userRef.child(id).removeValueAsync();
//        return "삭제 완";
//    }

}
