package com.livelive.bill;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final DatabaseReference expenseRef;

    public ExpenseController(@Qualifier("expenseRef") DatabaseReference expenseRef) {
        this.expenseRef = expenseRef;
    }

    @PostMapping
    public void addExpense(@RequestBody Map<String, Object> expenseData) {
        String expenseId = expenseRef.push().getKey();
        expenseRef.child(expenseId).setValueAsync(expenseData);
    }

    @GetMapping("/selectData")
    public List<Map<String, Object>> getExpensesByDate(@RequestParam("baseDate") String baseDateStr, @RequestParam("mode") String mode) throws Exception {
        LocalDate baseDate = LocalDate.parse(baseDateStr); // 예: 2025-08-05

        LocalDate start, prevStart;

        if ("month".equals(mode)) {
            start = baseDate.withDayOfMonth(1);
            prevStart = start.minusMonths(1);
        } else {
            start = baseDate.with(DayOfWeek.MONDAY);
            prevStart = start.minusWeeks(1);
        }

        LocalDate end = ("month".equals(mode)) ? start.plusMonths(1).minusDays(1) : start.plusDays(6);

        Map<String, Integer> current = new HashMap<>();
        Map<String, Integer> previous = new HashMap<>();

        CountDownLatch latch = new CountDownLatch(1);

        expenseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for (DataSnapshot ch : snap.getChildren()) {
                    Map<String, Object> e = (Map<String, Object>) ch.getValue();
                    String user = (String) e.get("user");
                    LocalDate d2 = LocalDate.parse((String) e.get("date"));
                    int amt = ((Number) e.get("amount")).intValue();

                    if (user != null) {
                        if (!user.isEmpty()) {
                            if (!d2.isBefore(start) && !d2.isAfter(end)) {
                                current.merge(user, amt, Integer::sum);
                            } else if (!d2.isBefore(prevStart) && d2.isBefore(start)) {
                                previous.merge(user, amt, Integer::sum);
                            }
                        }
                    }
                }
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                latch.countDown();
            }
        });

        latch.await();

        List<Map<String, Object>> result = new ArrayList<>();
        for (String u : current.keySet()) {
            int c = current.getOrDefault(u, 0);
            int p = previous.getOrDefault(u, 0);
            result.add(Map.of(
                    "user", u,
                    "current", c,
                    "previous", p,
                    "delta", c - p
            ));
        }

        result.sort((x, y) -> ((Integer)y.get("current")).compareTo((Integer)x.get("current")));

        return result;
    }

//    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("expenses");

//    @PostMapping
//    public ResponseEntity<String> saveExpense(@RequestBody Expense expense) {
//        FirebaseToken user = FirebaseAuthFilter.currentUser.get();
//        if (user == null || !user.getUid().equals("oixA3QuL3SN0UoMH7Bo7NG5icTi1")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("나디만 가능하다내요");
//        }
//
//        String id = dbRef.push().getKey();
//        dbRef.child(id).setValueAsync(expense);
//        return ResponseEntity.ok("저장 완");
//    }
//
//    @GetMapping("/range")
//    public void getExpensesInRange(@RequestParam String startDate, @RequestParam String endDate, HttpServletResponse response) throws IOException {
//
//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                List<Expense> result = new ArrayList<>();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    Expense e = child.getValue(Expense.class);
//                    if (e.getDate().compareTo(startDate) >= 0 && e.getDate().compareTo(endDate) <= 0) {
//                        result.add(e);
//                    }
//                }
//
//                try {
//                    response.setContentType("application/json");
//                    response.getWriter().write(new ObjectMapper().writeValueAsString(result));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                try {
//                    response.sendError(500, error.getMessage());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
}
