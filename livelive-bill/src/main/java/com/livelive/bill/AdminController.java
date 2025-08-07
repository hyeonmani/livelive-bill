package com.livelive.bill;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AdminController {

    private final String ADMIN_UID = "oixA3QuL3SN0UoMH7Bo7NG5icTi1";

    @PostMapping("/checkAdmin")
    public Map<String, Boolean> checkAdmin(@RequestHeader("Authorization") String authHeader) throws Exception {
        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        boolean isAdmin = ADMIN_UID.equals(uid);

        Map<String, Boolean> response = new HashMap<>();
        response.put("admin", isAdmin);
        return response;
    }
}
