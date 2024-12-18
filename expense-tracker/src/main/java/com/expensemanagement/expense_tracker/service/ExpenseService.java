package com.expensemanagement.expense_tracker.service;

import com.expensemanagement.expense_tracker.model.Expense;
import com.expensemanagement.expense_tracker.model.ExpenseStatus;
import com.expensemanagement.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Transactional
    public Expense createExpense(Expense expense) {
        validateExpense(expense);
        expense.setStatus(ExpenseStatus.PENDING);
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByDepartment(Long departmentId) {
        return expenseRepository.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public List<Expense> getExpensesByStatus(ExpenseStatus status) {
        return expenseRepository.findByStatus(status);
    }

    @Transactional
    public Expense updateExpenseStatus(Long id, ExpenseStatus status) {
        Expense expense = getExpenseById(id);
        expense.setStatus(status);
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAccountantStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPending", expenseRepository.countByStatus(ExpenseStatus.PENDING));

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now();

        stats.put("totalApproved", expenseRepository.countByStatusAndDateBetween(
                ExpenseStatus.APPROVED,
                startOfMonth,
                endOfMonth
        ));

        List<Expense> monthlyExpenses = expenseRepository.findByDateBetween(startOfMonth, endOfMonth);
        BigDecimal monthlyTotal = monthlyExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("monthlyTotal", monthlyTotal);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<Expense> getPendingExpensesByDepartment(Long departmentId) {
        return expenseRepository.findByDepartmentIdAndStatus(departmentId, ExpenseStatus.PENDING);
    }

    private void validateExpense(Expense expense) {
        if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
        if (expense.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (expense.getDate() == null) {
            expense.setDate(LocalDate.now().atStartOfDay());
        }
        if (expense.getDepartment() == null) {
            throw new IllegalArgumentException("Department is required");
        }
        if (expense.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }
    }

    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    @Transactional
    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense expense = getExpenseById(id);

        // Only update allowed fields
        if (expenseDetails.getDescription() != null) {
            expense.setDescription(expenseDetails.getDescription());
        }
        if (expenseDetails.getAmount() != null) {
            expense.setAmount(expenseDetails.getAmount());
        }
        if (expenseDetails.getCategory() != null) {
            expense.setCategory(expenseDetails.getCategory());
        }
        if (expenseDetails.getDate() != null) {
            expense.setDate(expenseDetails.getDate());
        }

        validateExpense(expense);
        return expenseRepository.save(expense);
    }
}