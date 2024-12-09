package com.expensemanagement.expense_tracker.controller;

import com.expensemanagement.expense_tracker.model.Expense;
import com.expensemanagement.expense_tracker.model.ExpenseStatus;
import com.expensemanagement.expense_tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return expense != null ? ResponseEntity.ok(expense) : ResponseEntity.notFound().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getUserExpenses(@PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getExpensesByUserId(userId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Expense>> getDepartmentExpenses(@PathVariable Long departmentId) {
        return ResponseEntity.ok(expenseService.getExpensesByDepartment(departmentId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Expense> updateExpenseStatus(
            @PathVariable Long id,
            @RequestParam ExpenseStatus status) {
        Expense expense = expenseService.updateExpenseStatus(id, status);
        return expense != null ? ResponseEntity.ok(expense) : ResponseEntity.notFound().build();
    }
}