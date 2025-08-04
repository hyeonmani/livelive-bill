package com.livelive.bill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("expenses");

    @PostMapping
    public ResponseEntity<String> saveExpense(@RequestBody Expense expense) {
        FirebaseToken user = FirebaseAuthFilter.currentUser.get();
        if (user == null || !user.getUid().equals("oixA3QuL3SN0UoMH7Bo7NG5icTi1")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("나디만 가능하다내요");
        }

        String id = dbRef.push().getKey();
        dbRef.child(id).setValueAsync(expense);
        return ResponseEntity.ok("저장 완");
    }

    @GetMapping("/range")
    public void getExpensesInRange(@RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) throws IOException {

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Expense> result = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Expense e = child.getValue(Expense.class);
                    if (e.getDate().compareTo(startDate) >= 0 && e.getDate().compareTo(endDate) <= 0) {
                        result.add(e);
                    }
                }

                try {
                    response.setContentType("application/json");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                try {
                    response.sendError(500, error.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
