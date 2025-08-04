package com.livelive.bill;

import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    @GetMapping
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if(user != null) users.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
             try {
                 Thread.sleep(1000);
             } catch (InterruptedException ignored) {

             }

        return users;
    }

    @PostMapping
    public String addUser(@RequestBody User user) {
        FirebaseToken token = FirebaseAuthFilter.currentUser.get();
        if(token == null || !token.getUid().equals("관리자UID")) return "나디만 가능 하다내요.";

        String id = userRef.push().getKey();
        user.setId(id);
        userRef.child(id).setValueAsync(user);
        return "사용자 추가 완";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable String id) {
        FirebaseToken token = FirebaseAuthFilter.currentUser.get();
        if(token == null || !token.getUid().equals("관리자UID")) return "나디만 가능 하다내요.";

        userRef.child(id).removeValueAsync();
        return "삭제 완";
    }

}
