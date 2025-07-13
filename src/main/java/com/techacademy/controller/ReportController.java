package com.techacademy.controller;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;

import jakarta.servlet.http.HttpSession; // ← 追加
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;



import java.util.List;

@Controller
@RequestMapping("reports")
public class ReportController {

    private final ReportService reportService;
    private final HttpSession session; // ← セッション取得のため追加

    @Autowired
    public ReportController(ReportService reportService, HttpSession session) {
        this.reportService = reportService;
        this.session = session;
    }

    // 一覧表示
    @GetMapping
    public String list(Model model) {
        List<Report> reportList = reportService.findAll();
        model.addAttribute("reportList", reportList);
        return "reports/list";
    }

    // 登録画面の表示
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Report report = new Report();

        // セッションからログイン中の従業員を取得
        Employee loginEmployee = (Employee) session.getAttribute("loginEmployee");

        // nullチェック付きでセット
        if (loginEmployee != null) {
            report.setEmployee(loginEmployee);
        }

        model.addAttribute("report", report);
        return "reports/new";
    }

    @PostMapping("/new")
    public String create(@ModelAttribute Report report) {
        // セッションからログイン中の従業員を取得
        Employee loginEmployee = (Employee) session.getAttribute("loginEmployee");

        // ログイン従業員を紐付けて保存
        report.setEmployee(loginEmployee);
        reportService.save(report);

        return "redirect:/reports";
    }


}
