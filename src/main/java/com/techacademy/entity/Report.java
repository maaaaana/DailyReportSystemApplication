package com.techacademy.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Report {

    public Report() {} // デフォルトコンストラクタ（必須）

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate reportDate;

    private String title;

    @ManyToOne
    @JoinColumn(name = "employee_code") // ← employeeの主キーが"code"ならOK
    private Employee employee;

    // --- Getter / Setter ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
}
